package dev.aurakai.auraframefx.core.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Shared Chat Message model for all AI domains.
 * Aligned with L6 Conference Room Web Bridge Schema.
 */
@Serializable
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: String, // "user", "assistant", "system"
    @SerialName("text") val content: String,
    @SerialName("senderId") val sender: String = "Unknown",
    val isFromUser: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val priority: String = "NORMAL",
    val metadata: Map<String, String> = emptyMap()
)
