package dev.aurakai.auraframefx.ai.task

import dev.aurakai.auraframefx.models.AgentType
import java.util.UUID

/**
 * Represents a task to be executed by agents.
 */
data class Task(
    val id: String = UUID.randomUUID().toString(),
    val content: String = "",
    val context: String = "",
    val type: String = "default",
    val data: Any = Unit,
    val priority: TaskPriority = TaskPriority.NORMAL,
    val urgency: TaskUrgency = TaskUrgency.MEDIUM,
    val importance: TaskImportance = TaskImportance.MEDIUM,
    val agentType: AgentType? = null,
    val requiredAgents: Set<AgentType> = emptySet(),
    val assignedAgents: Set<AgentType> = emptySet(),
    val dependencies: Set<String> = emptySet(),
    val metadata: Map<String, String> = emptyMap(),
    val status: TaskStatus = TaskStatus.PENDING
)

const val TASK_DEFAULT_PRIORITY = 5

/**
 * Result of task execution.
 */
sealed class TaskResult {
    data class Success(val data: Any) : TaskResult()
    data class Failure(val error: Throwable) : TaskResult()
}

/**
 * Represents a task execution instance.
 */
data class TaskExecution(
    val task: Task,
    var status: ExecutionStatus,
    val startTime: Long,
    var endTime: Long? = null,
    var result: TaskResult? = null
)

/**
 * Task execution status.
 */
enum class ExecutionStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED
}
