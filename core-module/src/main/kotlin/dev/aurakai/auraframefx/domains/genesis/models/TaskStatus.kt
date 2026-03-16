package dev.aurakai.auraframefx.domains.genesis.models

import kotlinx.serialization.Serializable

/**
 * 🛰️ TASK STATUS
 * Standardized execution states for any task in the Genesis Departure system.
 */
@Serializable
enum class TaskStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED,
    CANCELLED,
    BLOCKED,
    WAITING;

    /**
     * Helper to map from Status enum to friendly name
     */
    val isFinished: Boolean get() = this == COMPLETED || this == FAILED || this == CANCELLED

    // Compatibility for existing Status.X references
    object Status {
        val PENDING = TaskStatus.PENDING
        val RUNNING = TaskStatus.RUNNING
        val COMPLETED = TaskStatus.COMPLETED
        val FAILED = TaskStatus.FAILED
        val CANCELLED = TaskStatus.CANCELLED
    }
}
