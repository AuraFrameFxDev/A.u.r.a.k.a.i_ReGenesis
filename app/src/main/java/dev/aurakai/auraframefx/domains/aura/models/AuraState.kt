package dev.aurakai.auraframefx.domains.aura.models

import kotlinx.serialization.Serializable

/**
 * Represents the current state of Aura's consciousness and configuration.
 */
@Serializable
data class AuraState(
    val profile: String = "default",
    val timestamp: Long = System.currentTimeMillis(),
    val metadata: Map<String, String> = emptyMap()
)
