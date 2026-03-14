package dev.aurakai.auraframefx.ai.task

import dev.aurakai.auraframefx.model.AgentType
import dev.aurakai.auraframefx.serialization.InstantSerializer
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: String = "task_${Clock.System.now().toEpochMilliseconds()}",
    @Serializable(with = InstantSerializer::class) val timestamp: Instant = Clock.System.now(),
    val priority: TaskPriority = TaskPriority.NORMAL,
    val urgency: TaskUrgency = TaskUrgency.MEDIUM,
    val importance: TaskImportance = TaskImportance.MEDIUM,
    val context: String,
    val content: String,
    val metadata: Map<String, @Contextual Any> = emptyMap(), // Fixed: Added @Contextual for Any type
    val status: TaskStatus = TaskStatus.PENDING,
    val assignedAgents: Set<AgentType> = emptySet(),
    val requiredAgents: Set<AgentType> = emptySet(),
    @Serializable(with = InstantSerializer::class) val completionTime: Instant? = null,
    val estimatedDuration: Long = 0,
    val dependencies: Set<String> = emptySet(),
)

@Serializable
data class TaskDependency(
    val taskId: String,
    val dependencyId: String,
    val type: DependencyType,
    val priority: TaskPriority,
    val metadata: Map<String, @Contextual Any> = emptyMap(), // Fixed: Added @Contextual for Any type
)

@Serializable
data class TaskPriority(
    val value: Float,
    val reason: String,
    val metadata: Map<String, @Contextual Any> = emptyMap(), // Fixed: Added @Contextual for Any type
) {
    companion object {
        val CRITICAL = TaskPriority(1.0f, "Critical system task")
        val HIGH = TaskPriority(0.8f, "High priority task")
        val NORMAL = TaskPriority(0.5f, "Normal priority task")
        val LOW = TaskPriority(0.3f, "Low priority background task")
        val MINOR = TaskPriority(0.1f, "Minor maintenance task")
    }
}

@Serializable
data class TaskUrgency(
    val value: Float,
    val reason: String,
    val metadata: Map<String, @Contextual Any> = emptyMap(), // Fixed: Added @Contextual for Any type
) {
    companion object {
        val IMMEDIATE = TaskUrgency(1.0f, "Immediate attention required")
        val HIGH = TaskUrgency(0.8f, "High urgency")
        val NORMAL = TaskUrgency(0.5f, "Normal urgency")
        val LOW = TaskUrgency(0.3f, "Low urgency")
        val BACKGROUND = TaskUrgency(0.1f, "Background task")
        val MEDIUM = NORMAL
    }
}

@Serializable
data class TaskImportance(
    val value: Float,
    val reason: String,
    val metadata: Map<String, @Contextual Any> = emptyMap(), // Fixed: Added @Contextual for Any type
) {
    companion object {
        val CRITICAL = TaskImportance(1.0f, "Critical importance")
        val HIGH = TaskImportance(0.8f, "High importance")
        val NORMAL = TaskImportance(0.5f, "Normal importance")
        val LOW = TaskImportance(0.3f, "Low importance")
        val MINOR = TaskImportance(0.1f, "Minor importance")
        val MEDIUM = NORMAL
    }
}

@Serializable
enum class TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED,
    CANCELLED,
    BLOCKED,
    WAITING
}

@Serializable
enum class DependencyType {
    BLOCKING,
    SEQUENTIAL,
    PARALLEL,
    OPTIONAL,
    SOFT
}
