package dev.aurakai.auraframefx.domains.cascade.utils.memory.models

import kotlinx.serialization.Serializable

/**
 * Shared InteractionEntry model for all memory managers
 * Prevents redeclaration errors across multiple files
 */
@Serializable
data class InteractionEntry(
    val prompt: String,
    val response: String,
    val timestamp: Long = System.currentTimeMillis(),
    val agentType: String? = null,
    val relevanceScore: Float = 0.0f  // Added for compatibility with search functions
)
