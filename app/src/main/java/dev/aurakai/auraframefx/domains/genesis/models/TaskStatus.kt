package dev.aurakai.auraframefx.domains.genesis.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 📊 TASK STATUS
 * Standardized status tracking for all asynchronous AI tasks.
 */
@Serializable
data class TaskStatus(
    @SerialName("task_id")
    val taskId: String,
    @SerialName("status")
    val status: Status,
    @SerialName("progress")
    val progress: Float? = null,
    @SerialName("message")
    val message: String? = null
) {
    /**
     * The specific execution state.
     */
    @Serializable
    enum class Status(val value: String) {
        @SerialName("pending")
        PENDING("pending"),

        @SerialName("running")
        RUNNING("running"),

        @SerialName("completed")
        COMPLETED("completed"),

        @SerialName("failed")
        FAILED("failed"),

        @SerialName("cancelled")
        CANCELLED("cancelled");
    }
}
