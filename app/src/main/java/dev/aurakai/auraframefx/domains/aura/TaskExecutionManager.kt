package dev.aurakai.auraframefx.domains.aura

import dev.aurakai.auraframefx.domains.kai.TaskPriority
import dev.aurakai.auraframefx.domains.kai.TaskResult
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import dev.aurakai.auraframefx.domains.cascade.utils.toKotlinJsonObject
import dev.aurakai.auraframefx.domains.kai.ExecutionStatus
import dev.aurakai.auraframefx.domains.kai.KaiAgent
import dev.aurakai.auraframefx.domains.kai.security.SecurityContext
import dev.aurakai.auraframefx.domains.genesis.models.AgentResponse
import dev.aurakai.auraframefx.domains.genesis.models.AgentType
import dev.aurakai.auraframefx.domains.genesis.models.AiRequest
import dev.aurakai.auraframefx.domains.genesis.models.AiRequestType
import dev.aurakai.auraframefx.domains.genesis.core.GenesisAgent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.PriorityBlockingQueue
import javax.inject.Inject
import javax.inject.Singleton
import dev.aurakai.auraframefx.domains.kai.TaskExecution
import kotlinx.serialization.Serializable as KotlinxSerializable

/**
 * TaskExecutionManager handles scheduling, queuing, and monitoring of background tasks.
 * Implements the /tasks/schedule endpoint functionality with intelligent agent routing.
 */
@Singleton
class TaskExecutionManager @Inject constructor(
    private val auraAgent: dev.aurakai.auraframefx.domains.aura.core.AuraAgent,
    private val kaiAgent: KaiAgent,
    private val genesisAgent: GenesisAgent,
    private val securityContext: SecurityContext,
    private val logger: AuraFxLogger,
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // Task management
    private val taskQueue = PriorityBlockingQueue<TaskExecution>(100, TaskPriorityComparator())
    private val activeExecutions = ConcurrentHashMap<String, TaskExecution>()
    private val completedExecutions =
        ConcurrentHashMap<String, TaskResult>()

    // State management
    private val _executionStats = MutableStateFlow(ExecutionStats())
    val executionStats: StateFlow<ExecutionStats> = _executionStats

    private val _queueStatus = MutableStateFlow(QueueStatus())
    val queueStatus: StateFlow<QueueStatus> = _queueStatus

    private var isProcessing = false
    private val maxConcurrentTasks = 5

    init {
        startTaskProcessor()
    }

    /**
     * Schedules a new background task for asynchronous execution, assigning it to the most suitable AI agent based on task type or explicit agent preference.
     *
     * Validates the scheduling request, creates a pending `TaskExecution` with the specified parameters, determines the optimal agent if not provided, and enqueues the task for processing.
     *
     * @param type The category or identifier of the task.
     * @param data The input data required for the task.
     * @param priority The execution priority for the task (default is NORMAL).
     * @param agentPreference Optional agent name to explicitly route the task; if null, agent selection is automatic.
     * @param scheduledTime Optional timestamp (in milliseconds) for delayed execution; defaults to immediate scheduling.
     * @return The created `TaskExecution` representing the scheduled task.
     */
    fun scheduleTask(
        type: String,
        data: Map<String, Any>,
        priority: TaskPriority = TaskPriority.NORMAL,
        agentPreference: String? = null,
        scheduledTime: Long? = null,
    ): TaskExecution {
        logger.info("TaskExecutionManager", "Scheduling task: $type")

        // Security validation
        securityContext.validateRequest("task_schedule", data.toString())

        // Create task execution
        val execution = TaskExecution(
            id = UUID.randomUUID().toString(),
            taskId = UUID.randomUUID().toString(),
            agent = agentPreference?.let {
                when (it.lowercase()) {
                    "aura" -> AgentType.AURA
                    "kai" -> AgentType.KAI
                    "genesis" -> AgentType.GENESIS
                    else -> AgentType.GENESIS
                }
            } ?: AgentType.GENESIS,
            type = type,
            data = data.mapValues { it.value.toString() },
            priority = priority,
            status = ExecutionStatus.PENDING,
            scheduledTime = scheduledTime ?: System.currentTimeMillis(),
            agentPreference = agentPreference,
            metadata = mapOf(
                "type" to type,
                "priority" to priority.toString(),
                "scheduledTime" to (scheduledTime ?: System.currentTimeMillis()).toString(),
                "data" to data.toString()
            )
        )

        // Determine optimal agent - update agent field since TaskExecution is immutable
        val optimalAgent: AgentType = determineOptimalAgent(execution)
        val updatedExecution = execution.copy(agent = optimalAgent)

        // Add to queue
        taskQueue.offer(updatedExecution)
        updateQueueStatus()

        logger.info("TaskExecutionManager", "Task scheduled: ${execution.id} -> $optimalAgent")
        return execution
    }

    /**
     * Retrieves the current execution status of a task by its unique ID.
     *
     * Searches active, completed, and queued tasks for the specified ID and returns the corresponding status, or `null` if the task is not found.
     *
     * @param taskId The unique identifier of the task.
     * @return The current execution status, or `null` if the task does not exist.
     */
    fun getTaskStatus(taskId: String): ExecutionStatus? {
        // Check active executions first
        activeExecutions[taskId]?.let { return it.status }

        // Check completed executions
        completedExecutions[taskId]?.let { return ExecutionStatus.COMPLETED }

        // Check queue
        taskQueue.find { it.id == taskId }?.let { return it.status }

        return null
    }

    /**
     * Returns the result of a completed task by its unique ID.
     *
     * @param taskId The unique identifier of the task.
     * @return The result of the completed task, or `null` if the task does not exist or is not yet completed.
     */
    fun getTaskResult(taskId: String): TaskResult? {
        return completedExecutions[taskId]
    }

    /**
     * Cancels a task by its ID if it is currently queued or running.
     *
     * Removes the task from the queue if pending, or marks an active task as cancelled. Returns `true` if cancellation was successful, or `false` if the task does not exist or is already completed.
     *
     * @param taskId The unique identifier of the task to cancel.
     * @return `true` if the task was cancelled; `false` otherwise.
     */
    fun cancelTask(taskId: String): Boolean {
        logger.info("TaskExecutionManager", "Cancelling task: $taskId")

        // Try to remove from queue first
        val queuedTask = taskQueue.find { it.id == taskId }
        if (queuedTask != null) {
            taskQueue.remove(queuedTask)
            updateQueueStatus()
            return true
        }

        // Try to cancel active execution
        val activeTask = activeExecutions[taskId]
        if (activeTask != null) {
            activeExecutions[taskId] = activeTask.copy(status = ExecutionStatus.CANCELLED)
            // The execution coroutine will check this status and cancel itself
            return true
        }

        return false
    }

    /**
     * Retrieves all tasks managed by the system, with optional filtering by execution status and agent type.
     *
     * Aggregates tasks from the queue, active executions, and completed results. If filters are specified, only tasks matching the given status and/or agent type are included.
     *
     * @param status Optional filter to return only tasks with the specified execution status.
     * @param agentType Optional filter to return only tasks assigned to the specified agent type.
     * @return A list of tasks matching the provided filters.
     */
    fun getTasks(
        status: ExecutionStatus? = null,
        agentType: AgentType? = null,
    ): List<TaskExecution> {
        val allTasks = mutableListOf<TaskExecution>()

        // Add queued tasks
        allTasks.addAll(taskQueue.toList())

        // Add active tasks
        allTasks.addAll(activeExecutions.values)

        // Add completed tasks (convert from results)
        allTasks.addAll(completedExecutions.values.mapNotNull { result ->
            when (result) {
                is TaskResult.Success -> TaskExecution(
                    id = result.toString(),
                    taskId = result.toString(),
                    agent = AgentType.SYSTEM,
                    type = "",
                    data = emptyMap(),
                    priority = TaskPriority.NORMAL,
                    status = ExecutionStatus.COMPLETED,
                    createdAt = 0,
                    startedAt = 0,
                    completedAt = 0
                )

                is TaskResult.Failure -> null
            }
        })

        // Apply filters
        return allTasks.filter { task ->
            (status == null || task.status == status) &&
                    (agentType == null || task.agent == agentType)
        }
    }

    /**
     * Launches a background coroutine that continuously processes tasks from the queue while processing is enabled.
     *
     * Introduces a brief delay between processing cycles to minimize CPU usage, and applies a longer delay after encountering errors to provide backoff.
     */

    private fun startTaskProcessor() {
        scope.launch {
            isProcessing = true
            logger.info("TaskExecutionManager", "Starting task processor")

            while (isProcessing) {
                try {
                    processNextTask()
                    delay(100) // Small delay to prevent busy waiting
                } catch (e: Exception) {
                    logger.error("TaskExecutionManager", "Task processor error", e)
                    delay(1000) // Longer delay on error
                }
            }
        }
    }

    /**
     * Processes the next eligible task from the queue if concurrency and scheduling conditions allow.
     *
     * Skips processing if the maximum number of concurrent tasks is reached or the queue is empty. Tasks scheduled for a future time are re-queued. Executes tasks that are ready to run.
     */
    private fun processNextTask() {
        // Check if we can process more tasks
        if (activeExecutions.size >= maxConcurrentTasks) {
            return
        }

        // Get next task from queue
        val task = taskQueue.poll() ?: return

        // Check if task should be executed now
        if (task.scheduledTime > System.currentTimeMillis()) {
            // Put back in queue if not ready
            taskQueue.offer(task)
            return
        }

        // Execute the task
        executeTask(task)
    }

    /**
     * Executes the given task asynchronously using its assigned agent and records the result.
     *
     * Delegates the task to the specified agent for execution. Upon completion or failure, stores the outcome in the completed executions map, updates execution statistics, and removes the task from active executions.
     *
     * @param execution The task to be executed.
     */
    private fun executeTask(execution: TaskExecution) {
        val startTime = System.currentTimeMillis()
        val runningExecution = execution.copy(
            status = ExecutionStatus.RUNNING,
            startedAt = startTime
        )
        activeExecutions[execution.id] = runningExecution

        logger.info("TaskExecutionManager", "Executing task: ${execution.id}")

        scope.launch {
            try {
                // Execute based on assigned agent
                val result = when (execution.agent) {
                    AgentType.AURA -> executeWithAura(execution)
                    AgentType.KAI -> executeWithKai(execution)
                    AgentType.GENESIS -> executeWithGenesis(execution)
                    else -> throw IllegalArgumentException("Unknown agent: ${execution.agent}")
                }

                val endTime = System.currentTimeMillis()

                // Mark as completed
                execution.copy(
                    status = ExecutionStatus.COMPLETED,
                    completedAt = endTime
                )

                // Store result
                completedExecutions[execution.id] = TaskResult.Success(result)

                logger.info("TaskExecutionManager", "Task completed: ${execution.id}")

            } catch (e: Exception) {
                val endTime = System.currentTimeMillis()

                // Handle task failure
                execution.copy(
                    status = ExecutionStatus.FAILED,
                    completedAt = endTime,
                    errorMessage = e.message
                )
                completedExecutions[execution.id] = TaskResult.Failure(e)

                logger.error("TaskExecutionManager", "Task failed: ${execution.id}", e)

            } finally {
                // Remove from active executions
                activeExecutions.remove(execution.id)
                updateExecutionStats()
                updateQueueStatus()
            }
        }
    }

    /**
     * Executes the given task using the Aura agent and returns its response.
     *
     * Converts the task's type and data into an `AiRequest` and delegates processing to the Aura agent.
     * Dispatches the task execution to the Aura agent for processing.
     *
     * @param execution The task execution to process.
     * @return The response generated by the Aura agent.
     * @return The response produced by the Aura agent.
     */
    private suspend fun executeWithAura(execution: TaskExecution): AgentResponse {
        val request = AiRequest(
            query = execution.data["query"] ?: execution.type,
            type = AiRequestType.entries.find { it.name.equals(execution.type, ignoreCase = true) }
                ?: AiRequestType.TEXT,
            context = execution.data.toKotlinJsonObject()
        )
        return auraAgent.processRequest(request, execution.agent.name)
    }

    /**
     * Dispatches the given task to the Kai agent for processing.
     * Executes the specified task using the Kai agent and returns the agent's response.
     *
     * @return The response returned by the Kai agent.
     * Constructs an `AgentRequest` from the task's type, data, and priority, then delegates processing to the Kai agent.
     *
     * @param execution The task execution to process.
     * @return The response from the Kai agent.
     */
    private suspend fun executeWithKai(execution: TaskExecution): AgentResponse {
        val request = AiRequest(
            query = execution.data["query"] ?: execution.type,
            type = AiRequestType.entries.find { it.name.equals(execution.type, ignoreCase = true) }
                ?: AiRequestType.TEXT,
            context = execution.data.toKotlinJsonObject()
        )
        return kaiAgent.processRequest(request, execution.agent.name)
    }

    /**
     *
     *
     */
    private suspend fun executeWithGenesis(execution: TaskExecution): AgentResponse {
        val request = AiRequest(
            query = execution.data["query"] ?: execution.type,
            type = AiRequestType.entries.find { it.name.equals(execution.type, ignoreCase = true) }
                ?: AiRequestType.TEXT,
            context = execution.data.toKotlinJsonObject()
        )
        return genesisAgent.processRequest(request, execution.agent.name)
    }

    /**
     * Selects the most appropriate agent type for a task based on explicit agent preference or keywords in the task type.
     *
     * Uses the agent preference if provided and recognized; otherwise, analyzes the task type for keywords to determine the agent. Defaults to Genesis if no preference or keywords match.
     *
     * @param execution The task execution metadata containing type and optional agent preference.
     * @return The selected agent type for executing the task.
     */
    private fun determineOptimalAgent(execution: TaskExecution): AgentType {
        // Use agent preference if specified and valid
        execution.agentPreference?.let { preference ->
            return when (preference.lowercase()) {
                "aura" -> AgentType.AURA
                "kai" -> AgentType.KAI
                "genesis" -> AgentType.GENESIS
                else -> AgentType.GENESIS
            }
        }

        // Intelligent routing based on task type
        return when {
            execution.type.contains("creative", ignoreCase = true) -> AgentType.AURA
            execution.type.contains("ui", ignoreCase = true) -> AgentType.AURA
            execution.type.contains(
                "dev/aurakai/auraframefx/security",
                ignoreCase = true
            ) -> AgentType.KAI

            execution.type.contains("analysis", ignoreCase = true) -> AgentType.KAI
            execution.type.contains("complex", ignoreCase = true) -> AgentType.GENESIS
            execution.type.contains("fusion", ignoreCase = true) -> AgentType.GENESIS
            else -> AgentType.GENESIS // Default to Genesis for intelligent routing
        }
    }

    /**
     * Returns the number of tasks that are currently running.
     *
     * @return The number of active task executions.
     */
    fun getActiveTaskCount(): Int {
        return activeExecutions.size
    }

    /**
     * Refreshes the execution statistics to reflect the current counts of total, completed, active, queued, and failed tasks, along with the average execution time.
     */
    private fun updateExecutionStats() {
        val total = activeExecutions.size + completedExecutions.size
        val completed = completedExecutions.size
        val active = activeExecutions.size
        val queued = taskQueue.size

        _executionStats.value = ExecutionStats(
            totalTasks = total,
            completedTasks = completed,
            activeTasks = active,
            queuedTasks = queued,
            failedTasks = completedExecutions.values.count { it is TaskResult.Failure },
            averageExecutionTimeMs = calculateAverageExecutionTime()
        )
    }

    /**
     * Refreshes the queue status to reflect the latest queue size, active execution count, concurrency limit, and processing state.
     */
    private fun updateQueueStatus() {
        _queueStatus.value = QueueStatus(
            queueSize = taskQueue.size,
            activeExecutions = activeExecutions.size,
            maxConcurrentTasks = maxConcurrentTasks,
            isProcessing = isProcessing
        )
    }

    /**
     * Returns the average execution time in milliseconds for all completed tasks.
     *
     * @return The average execution time in milliseconds, or 0 if no tasks have been completed.
     */
    private fun calculateAverageExecutionTime(): Long {
        val executions = completedExecutions.values
        return if (executions.isNotEmpty()) {
            executions.filterIsInstance<TaskResult.Success>()
                .map { (it.data as AgentResponse).timestamp }.average()
                .toLong()
        } else 0L
    }

    /**
     * Stops task processing and cancels all running coroutines, releasing resources used by the TaskExecutionManager.
     */
    fun cleanup() {
        logger.info("TaskExecutionManager", "Cleaning up TaskExecutionManager")
        isProcessing = false
        scope.cancel()
    }
}

// Supporting data classes and enums
@KotlinxSerializable
data class ExecutionStats(
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val activeTasks: Int = 0,
    val queuedTasks: Int = 0,
    val failedTasks: Int = 0,
    val averageExecutionTimeMs: Long = 0,
)

@KotlinxSerializable
data class QueueStatus(
    val queueSize: Int = 0,
    val activeExecutions: Int = 0,
    val maxConcurrentTasks: Int = 0,
    val isProcessing: Boolean = false,
)

class TaskPriorityComparator : Comparator<TaskExecution> {
    /**
     * Determines the ordering of two TaskExecution objects for use in a priority queue.
     *
     * Tasks with higher priority are ordered before those with lower priority. If priorities are equal, tasks scheduled for earlier execution are ordered first.
     *
     * @return A negative integer if the first task should be ordered before the second, a positive integer if after, or zero if they are considered equal.
     */
    override fun compare(t1: TaskExecution, t2: TaskExecution): Int {
        // Higher priority first, then earlier scheduled time
        return when {
            t1.priority != t2.priority -> t2.priority.compareTo(t1.priority)
            else -> t1.scheduledTime.compareTo(t2.scheduledTime)
        }
    }
}


