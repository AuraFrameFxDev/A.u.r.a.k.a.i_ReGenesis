package dev.aurakai.auraframefx.domains.genesis.models

import kotlinx.serialization.Serializable

/**
 * 🛰️ TASK STATUS
 * Standardized execution states for any task in the Genesis Departure system.
 */
@Serializable
class TaskStatus {
    @Serializable
    enum class Status {
        PENDING,
        RUNNING,
        COMPLETED,
        FAILED,
        CANCELLED,
        BLOCKED,
        WAITING;

        val isFinished: Boolean get() = this == COMPLETED || this == FAILED || this == CANCELLED
    }
}
