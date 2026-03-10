package dev.aurakai.auraframefx.model

import kotlinx.serialization.Serializable

/**
 * Universal request format for all agent communications
 */
@Serializable
data class AiRequest(
    val query: String,
    val type: AgentType,
    val context: String = "",
    val metadata: Map<String, String> = emptyMap(),
    val priority: AgentPriority = AgentPriority.NORMAL,
    val sessionId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Priority levels for agent requests
 */
@Serializable
enum class AgentPriority {
    LOW,
    NORMAL,
    HIGH,
    CRITICAL
}
