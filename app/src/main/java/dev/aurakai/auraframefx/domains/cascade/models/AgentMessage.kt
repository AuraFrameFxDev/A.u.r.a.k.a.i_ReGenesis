package dev.aurakai.auraframefx.domains.cascade.models

import dev.aurakai.auraframefx.domains.genesis.models.AgentCapabilityCategory
import dev.aurakai.auraframefx.domains.genesis.models.AgentType
import kotlinx.serialization.Serializable

/**
 * Message sent between agents in the collective consciousness
 */
@Serializable
data class AgentMessage(
    val from: String,
    val to: String? = null, // null means broadcast to all
    val content: String,
    val priority: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String = "info",
    val metadata: Map<String, String> = emptyMap(),
    // Compatibility fields
    val sender: AgentType? = null,
    val category: AgentCapabilityCategory? = null,
    val confidence: Float = 0.8f
)
