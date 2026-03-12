package dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.task.models

import kotlinx.serialization.Serializable
import java.util.UUID
import dev.aurakai.auraframefx.domains.genesis.models.TaskStatus

/**
 * AI Task model for task scheduling and execution
 */
@Serializable
data class AITask(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val prompt: String = "",
    val priority: String = "normal",
    val status: TaskStatus.Status = TaskStatus.Status.PENDING,
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
)

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
