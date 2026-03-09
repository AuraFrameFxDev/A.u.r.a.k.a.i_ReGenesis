package dev.aurakai.auraframefx.agent

import java.util.UUID

/**
 * Message type for inter-agent orchestration communication.
 */
data class OrchestratableMessage(
    val id: String = UUID.randomUUID().toString(),
    val senderId: String,
    val type: MessageType = MessageType.STANDARD,
    val content: String = "",
    val metadata: Map<String, String> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis()
)

enum class MessageType {
    STANDARD,
    COMMAND,
    RESPONSE,
    BROADCAST,
    SYSTEM
}

/**
 * Basic message wrapper for agent communication.
 */
data class Message(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val type: MessageType = MessageType.STANDARD,
    val timestamp: Long = System.currentTimeMillis()
)
