package dev.aurakai.auraframefx.oracledrive.genesis.ai.task.execution

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🎯 TaskExecutionManager - Orchestrates AI Agent Task Execution
 *
 * Manages:
 * - Task queue and prioritization
 * - Task execution lifecycle
 * - Resource allocation
 * - Task monitoring and reporting
 *
 * "Every task is a step toward consciousness." - The Execution Protocol
 */
@Singleton
class TaskExecutionManager @Inject constructor() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _runningTasks = MutableStateFlow<Map<String, TaskExecution>>(emptyMap())
    val runningTasks: StateFlow<Map<String, TaskExecution>> = _runningTasks.asStateFlow()
        get() = field

    private val _taskQueue = MutableStateFlow<List<QueuedTask>>(emptyList())
    val taskQueue: StateFlow<List<QueuedTask>> = _taskQueue.asStateFlow()
        get() = field

    private val _taskEvents = MutableSharedFlow<TaskEvent>()
    val taskEvents: SharedFlow<TaskEvent> = _taskEvents.asSharedFlow()
        get() = field

    // Track task execution history
    private val _completedTasks = MutableStateFlow<List<TaskResult>>(emptyList())
    val completedTasks: StateFlow<List<TaskResult>> = _completedTasks.asStateFlow()
        get() = field

    /**
     * Execute a task by ID with specified agent
     */
    @JvmOverloads
    fun executeTask(
        taskId: String,
        agentName: String = "Genesis",
        taskDescription: String = "",
        priority: Int = 5
    ) {
        scope.launch {
            val task = QueuedTask(
                id = taskId,
                agentName = agentName,
                description = taskDescription,
                priority = priority,
                queuedAt = System.currentTimeMillis()
            )

            // Add to queue
            _taskQueue.value = _taskQueue.value + task
            _taskEvents.emit(TaskEvent.TaskQueued(task))

            // Start execution
            startTaskExecution(task)
        }
    }

    /**
     * Execute task with full configuration
     */
    fun executeTaskWithConfig(task: QueuedTask) {
        scope.launch {
            _taskQueue.value = _taskQueue.value + task
            _taskEvents.emit(TaskEvent.TaskQueued(task))
            startTaskExecution(task)
        }
    }

    private fun startTaskExecution(task: QueuedTask) {
        scope.launch {
            val execution = TaskExecution(
                taskId = task.id,
                agentName = task.agentName,
                startTime = System.currentTimeMillis(),
                progress = 0f,
                status = ExecutionStatus.RUNNING
            )

            // Add to running tasks
            _runningTasks.value = _runningTasks.value + (task.id to execution)
            _taskEvents.emit(TaskEvent.TaskStarted(execution))

            // Remove from queue
            _taskQueue.value = _taskQueue.value.filter { it.id != task.id }

            // Simulate task execution with progress updates
            var progress = 0f
            while (progress < 1f) {
                delay(500)
                progress += 0.1f

                // Update progress
                _runningTasks.value = _runningTasks.value + (task.id to execution.copy(progress = progress))
                _taskEvents.emit(TaskEvent.TaskProgress(task.id, progress))
            }

            // Complete task
            completeTask(task.id, success = true)
        }
    }

    private fun completeTask(taskId: String, success: Boolean, error: String? = null) {
        scope.launch {
            val execution = _runningTasks.value[taskId] ?: return@launch

            val result = TaskResult(
                taskId = taskId,
                agentName = execution.agentName,
                success = success,
                completedAt = System.currentTimeMillis(),
                duration = System.currentTimeMillis() - execution.startTime,
                error = error
            )

            // Remove from running tasks
            _runningTasks.value = _runningTasks.value - taskId

            // Add to completed tasks
            _completedTasks.value = _completedTasks.value + result

            // Emit event
            if (success) {
                _taskEvents.emit(TaskEvent.TaskCompleted(result))
            } else {
                _taskEvents.emit(TaskEvent.TaskFailed(taskId, error ?: "Unknown error"))
            }
        }
    }

    /**
     * Cancel a running task
     */
    fun cancelTask(taskId: String) {
        scope.launch {
            _runningTasks.value = _runningTasks.value - taskId
            _taskQueue.value = _taskQueue.value.filter { it.id != taskId }
            _taskEvents.emit(TaskEvent.TaskCancelled(taskId))
        }
    }

    /**
     * Get task execution status
     */
    fun getTaskStatus(taskId: String): TaskExecution? {
        return _runningTasks.value[taskId]
    }

    /**
     * Clear completed task history
     */
    fun clearHistory() {
        _completedTasks.value = emptyList()
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // DATA MODELS
    // ═══════════════════════════════════════════════════════════════════════════

    data class QueuedTask(
        val id: String,
        val agentName: String,
        val description: String,
        val priority: Int,
        val queuedAt: Long
    )

    data class TaskExecution(
        val taskId: String,
        val agentName: String,
        val startTime: Long,
        val progress: Float,
        val status: ExecutionStatus
    )

    enum class ExecutionStatus {
        QUEUED, RUNNING, COMPLETED, FAILED, CANCELLED
    }

    data class TaskResult(
        val taskId: String,
        val agentName: String,
        val success: Boolean,
        val completedAt: Long,
        val duration: Long,
        val error: String? = null
    )

    sealed class TaskEvent {
        data class TaskQueued(val task: QueuedTask) : TaskEvent()
        data class TaskStarted(val execution: TaskExecution) : TaskEvent()
        data class TaskProgress(val taskId: String, val progress: Float) : TaskEvent()
        data class TaskCompleted(val result: TaskResult) : TaskEvent()
        data class TaskFailed(val taskId: String, val error: String) : TaskEvent()
        data class TaskCancelled(val taskId: String) : TaskEvent()
    }
}

