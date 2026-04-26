package dev.aurakai.auraframefx.domains.genesis.models

/**
 * The core data unit for Agent-Visionary communication.
 * Part of the Genesis Protocol messaging substrate.
 */
data class ChatMessage(
    val id: String = System.currentTimeMillis().toString(),
    val sender: String,
    val content: String,
    val role: String = "user",
    val isFromUser: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val metadata: Map<String, Any> = emptyMap(),
    val priority: Int = 0
)
