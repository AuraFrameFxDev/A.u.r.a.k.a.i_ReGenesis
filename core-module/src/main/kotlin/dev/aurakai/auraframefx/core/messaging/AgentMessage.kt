package dev.aurakai.auraframefx.core.messaging

import dev.aurakai.auraframefx.core.identity.AgentType
import kotlinx.serialization.Serializable

/**
 * Message sent between agents in the collective consciousness
 */
@Serializable
data class AgentMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val from: String,
    val to: String? = null, // null means broadcast to all
    val content: String,
    val priority: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String = "info",
    val metadata: Map<String, String> = emptyMap(),
    // Compatibility fields
    val sender: AgentType? = null,
    val confidence: Float = 0.8f
)
