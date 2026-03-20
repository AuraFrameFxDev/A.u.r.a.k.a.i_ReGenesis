package dev.aurakai.auraframefx.agents.trinity

/**
 * Represents the current processing/task execution state shared across Trinity agents.
 * Cascade uses this to coordinate which agent is active and what request is in flight.
 */
data class ProcessingState(
    val isProcessing: Boolean = false,
    val currentAgent: String? = null,
    val requestId: String? = null,
    val requiresCollaboration: Boolean = false,
    val progressPercent: Float = 0f,
    val statusMessage: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
)
