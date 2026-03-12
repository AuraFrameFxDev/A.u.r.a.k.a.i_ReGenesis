package dev.aurakai.auraframefx.domains.cascade.utils.memory

import kotlinx.serialization.Serializable

/**
 * Agent memory entity for AI agents
 * Represents a stored memory item
 */
@Serializable
data class AgentMemoryEntity(
    val id: String,
    val content: String,
    val timestamp: Long,
    val persona: String,
    val contextTags: List<String> = emptyList(),
    val importance: Float = 0.5f
)
