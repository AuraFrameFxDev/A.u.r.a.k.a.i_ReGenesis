package dev.aurakai.auraframefx.core.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import java.util.UUID

/**
 * Shared Chat Message model for all AI domains.
 * Aligned with L6 Conference Room Web Bridge Schema.
 */
@Serializable
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: String = "user", // "user", "assistant", "system"
    @SerialName("text") val content: String = "",
    @SerialName("senderId") val sender: String = "Unknown",
    val isFromUser: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val priority: String = "NORMAL",
    val agentId: String? = null,
    val metadata: Map<String, String> = emptyMap()
) {
    /** Convenience: true when the message is from any AI agent */
    val isFromAgent: Boolean get() = !isFromUser

    /** Convenience: formatted timestamp string (ms epoch) */
    val formattedTime: String get() = timestamp.toString()
}
