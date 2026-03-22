package dev.aurakai.auraframefx.domains.genesis.models

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Shared Chat Message model for all AI domains.
 */
@Serializable
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: String, // "user", "assistant", "system"
    val content: String,
    val sender: String = "Unknown",
    val isFromUser: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
