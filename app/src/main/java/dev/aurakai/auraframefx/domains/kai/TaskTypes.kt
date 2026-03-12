package dev.aurakai.auraframefx.domains.kai

import kotlinx.serialization.Serializable

/**
 * Task priority levels for scheduling.
 */
@Serializable
enum class TaskPriority(val value: Float) {
    LOW(0.25f),
    NORMAL(0.5f),
    HIGH(0.75f),
    CRITICAL(1.0f)
}

/**
 * Task urgency levels for scheduling.
 */
@Serializable
enum class TaskUrgency(val value: Float) {
    LOW(0.25f),
    MEDIUM(0.5f),
    HIGH(0.75f),
    URGENT(1.0f)
}

/**
 * Task importance levels for scheduling.
 */
@Serializable
enum class TaskImportance(val value: Float) {
    LOW(0.25f),
    MEDIUM(0.5f),
    HIGH(0.75f),
    CRITICAL(1.0f)
}

/**
 * Task execution status.
 */
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
