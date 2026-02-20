package dev.aurakai.auraframefx.domains.kai

import dev.aurakai.auraframefx.domains.genesis.models.AgentCapabilityCategory
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
    val category: AgentCapabilityCategory? = null,
    val requiredAgents: Set<AgentCapabilityCategory> = emptySet(),
    val assignedAgents: Set<AgentCapabilityCategory> = emptySet(),
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


