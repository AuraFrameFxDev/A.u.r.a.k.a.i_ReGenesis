package dev.aurakai.auraframefx.ai.task

import dev.aurakai.auraframefx.models.AgentType
import dev.aurakai.auraframefx.ui.viewmodels.AgentViewModel

const val TASK_DEFAULT_PRIORITY = 5

data class Task(
    val id: String,
    val type: String,
    val data: Any,
    val priority: AgentViewModel.TaskPriority = AgentViewModel.TaskPriority.NORMAL,
    val agentType: AgentType? = null,
)


sealed class TaskResult {
    data class Success(val data: Any) : TaskResult()
    data class Failure(val error: Throwable) : TaskResult()
}

data class TaskExecution(
    val task: Task,
    var status: ExecutionStatus,
    val startTime: Long,
    var endTime: Long? = null,
    var result: TaskResult? = null
)

enum class ExecutionStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED
}
