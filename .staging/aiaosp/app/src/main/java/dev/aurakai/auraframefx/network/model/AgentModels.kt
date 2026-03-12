package dev.aurakai.auraframefx.network.model

import kotlinx.serialization.Serializable

@Serializable
data class AgentRequest(
    val query: String,
    val type: String,
    val context: Map<String, String> = emptyMap(),
    val metadata: Map<String, String> = emptyMap(),
    val agentId: String,
    val sessionId: String
)

@Serializable
data class AgentResponse(
    val agentName: String,
    val response: String,
    val confidence: Double,
    val timestamp: Long,
    val metadata: Map<String, Any> = emptyMap(),
    val error: String? = null
)

@Serializable
data class AgentStatusResponse(
    val agentName: String,
    val status: String,
    val confidence: Double,
    val timestamp: Long,
    val error: String? = null,
    val metadata: Map<String, Any> = emptyMap()
)
