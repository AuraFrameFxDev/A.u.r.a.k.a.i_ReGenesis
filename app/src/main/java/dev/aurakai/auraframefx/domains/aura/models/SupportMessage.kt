package dev.aurakai.auraframefx.domains.aura.models

/**
 * Represents a chat message in the support chat system.
 */
data class SupportMessage(
    val content: String,
    val sender: String,
    val isUser: Boolean,
    val timestamp: String
)
