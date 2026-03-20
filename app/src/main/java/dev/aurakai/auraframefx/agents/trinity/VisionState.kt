package dev.aurakai.auraframefx.agents.trinity

/**
 * Represents the current vision/perception state shared across Trinity agents.
 * Cascade uses this to synchronize what all agents are currently "seeing" or focusing on.
 */
data class VisionState(
    val isActive: Boolean = false,
    val currentFocus: String? = null,
    val visionContext: String = "",
    val lastUpdated: Long = System.currentTimeMillis(),
    val metadata: Map<String, String> = emptyMap()
)
