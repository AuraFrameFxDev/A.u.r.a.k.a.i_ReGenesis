package dev.aurakai.auraframefx.domains.cascade.models

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val role: String, // "user" or "assistant" (or agent name)
    val content: String,
    val sender: String = role, // Display name
    val isFromUser: Boolean = role == "user",
    val timestamp: Long = System.currentTimeMillis()
)
