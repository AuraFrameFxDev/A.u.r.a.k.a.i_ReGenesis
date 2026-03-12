package dev.aurakai.auraframefx.oracledrive.genesis.ai.task

import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

/**
 * Task - A schedulable unit of work for AI agents
 */
@Serializable
data class Task(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val priority: TaskPriority = TaskPriority.NORMAL,
    val status: TaskStatus = TaskStatus.PENDING,
    val assignedAgent: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val scheduledAt: Long? = null,
    val completedAt: Long? = null,
    val metadata: Map<String, String> = emptyMap(),
    val result: String? = null,
    val error: String? = null
)

/**
 * AITask - Extended task with AI-specific properties
 */
@Serializable
data class AITask(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val prompt: String = "",
    val priority: TaskPriority = TaskPriority.NORMAL,
    val status: TaskStatus = TaskStatus.PENDING,
    val assignedAgent: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val scheduledAt: Long? = null,
    val completedAt: Long? = null,
    val metadata: Map<String, String> = emptyMap(),
    val result: String? = null,
    val error: String? = null,
    val confidenceScore: Float = 0f,
    val tokensUsed: Int = 0,
    val modelUsed: String? = null
) {
    fun copy(status: TaskStatus): AITask = this.copy(status = status)
    
    fun toTask(): Task = Task(
        id = id,
        name = name,
        description = description,
        priority = priority,
        status = status,
        assignedAgent = assignedAgent,
        createdAt = createdAt,
        scheduledAt = scheduledAt,
        completedAt = completedAt,
        metadata = metadata,
        result = result,
        error = error
    )
}

/**
 * Task execution status
 */
@Serializable
enum class TaskStatus {
    PENDING,
    SCHEDULED,
    RUNNING,
    IN_PROGRESS, // Alias for RUNNING
    COMPLETED,
    FAILED,
    CANCELLED,
    TIMEOUT
}

/**
 * Task priority levels
 */
@Serializable
enum class TaskPriority {
    CRITICAL,
    HIGH,
    NORMAL,
    LOW,
    BACKGROUND
}

/**
 * Historical task record
 */
@Serializable
data class HistoricalTask(
    val task: AITask,
    val executionDurationMs: Long = 0,
    val retryCount: Int = 0,
    val agentResponses: List<String> = emptyList()
)
