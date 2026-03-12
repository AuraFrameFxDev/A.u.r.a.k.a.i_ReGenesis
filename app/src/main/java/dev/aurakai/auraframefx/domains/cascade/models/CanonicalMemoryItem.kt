package dev.aurakai.auraframefx.domains.cascade.models

import dev.aurakai.auraframefx.domains.genesis.models.AgentCapabilityCategory
import kotlinx.serialization.Serializable

/**
 * Canonical memory item for the memory chain system
 */
@Serializable
data class CanonicalMemoryItem(
    val id: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val importance: Float = 0.5f,
    val agentType: AgentCapabilityCategory? = null,
    val metadata: Map<String, String> = emptyMap()
)
