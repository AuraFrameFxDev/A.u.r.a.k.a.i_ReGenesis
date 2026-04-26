package dev.aurakai.auraframefx.domains.genesis.models

/**
 * The core data unit for Agent-Visionary communication.
 * Part of the Genesis Protocol messaging substrate.
 */
data class ChatMessage(
    val sender: String,
    val content: String,
    val isFromUser: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
