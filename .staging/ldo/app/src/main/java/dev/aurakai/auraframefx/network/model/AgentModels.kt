@file:JvmName("NetworkAgentModels")
package dev.aurakai.auraframefx.network.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * Network-specific agent request model
 */
@Serializable
data class AgentRequest(
    val query: String,
    val type: String,
    val context: Map<String, String> = emptyMap(),
    val metadata: Map<String, String> = emptyMap(),
    val agentId: String = "",
    val sessionId: String = ""
)

/**
 * Typealias to unify AgentResponse across the codebase
 */
typealias AgentResponse = dev.aurakai.auraframefx.models.AgentResponse

/**
 * Network-specific status response
 */
@Serializable
data class AgentStatusResponse(
    val agentName: String,
    val status: String,
    val confidence: Double,
    val timestamp: Long,
    val error: String? = null,
    @Contextual
    val metadata: Map<String, Any> = emptyMap()
)
