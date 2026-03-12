package dev.aurakai.auraframefx.domains.kai.models

import kotlinx.serialization.Serializable

/**
 * Represents the current state of Kai's security protocols and monitoring.
 */
@Serializable
data class KaiState(
    val shieldActive: Boolean = false,
    val threatLevel: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)
