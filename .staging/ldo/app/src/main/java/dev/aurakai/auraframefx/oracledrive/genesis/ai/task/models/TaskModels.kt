package dev.aurakai.auraframefx.oracledrive.genesis.ai.task.models

import kotlinx.serialization.Serializable

/**
 * AI Task model for task scheduling
 */
@Serializable
data class AITask(
    val id: String,
    val type: String,
    val description: String,
    val priority: Int = 0,
    val status: TaskStatus = TaskStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

/**
 * Task status enum
 */
@Serializable
enum class TaskStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED,
    CANCELLED
}

/**
 * Historical task record
 */
@Serializable
data class HistoricalTask(
    val id: String,
    val type: String,
    val description: String,
    val completedAt: Long,
    val result: String? = null,
    val success: Boolean = true
)
