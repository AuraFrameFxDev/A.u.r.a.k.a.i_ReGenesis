@file:JvmName("NetworkAgentModels")

package dev.aurakai.auraframefx.domains.genesis.network.model

import kotlinx.serialization.Serializable

/**
 * Network-specific agent request model
 */
/**
 * Typealias to unify AgentRequest across the codebase
 */
typealias AgentRequest = dev.aurakai.auraframefx.domains.genesis.models.AgentRequest
typealias AgentResponse = dev.aurakai.auraframefx.domains.genesis.models.AgentResponse

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
    val metadata: Map<String, String> = emptyMap()
)

