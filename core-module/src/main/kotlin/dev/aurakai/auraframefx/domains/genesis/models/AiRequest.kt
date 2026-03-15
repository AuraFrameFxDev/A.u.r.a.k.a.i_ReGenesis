package dev.aurakai.auraframefx.domains.genesis.models

import kotlinx.serialization.Serializable

/**
 * 📡 AI REQUEST
 * Universal request format for any agent in the ReGenesis collective.
 */
@Serializable
data class AiRequest(
    val query: String,
    val type: AiRequestType = AiRequestType.TEXT,
    val context: String = "",
    val metadata: Map<String, String> = emptyMap(),
    val priority: AgentPriority = AgentPriority.NORMAL
)

enum class AgentPriority {
    LOW, NORMAL, HIGH, URGENT, CRITICAL
}

