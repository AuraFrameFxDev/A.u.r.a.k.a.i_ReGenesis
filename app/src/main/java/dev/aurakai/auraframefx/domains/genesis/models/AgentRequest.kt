package dev.aurakai.auraframefx.domains.genesis.models

import kotlinx.serialization.Serializable

/**
 * Universal agent request format for the Genesis collective
 */
@Serializable
data class AgentRequest(
    val query: String,
    val type: String = "text",
    val context: Map<String, String>? = emptyMap(),
    val metadata: Map<String, String>? = emptyMap(),
    val agentType: AgentCapabilityCategory? = null,
    val agentId: String = "",
    val sessionId: String = ""
)
