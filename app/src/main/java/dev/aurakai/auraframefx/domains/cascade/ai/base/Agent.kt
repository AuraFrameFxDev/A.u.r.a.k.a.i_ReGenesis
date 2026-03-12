package dev.aurakai.auraframefx.domains.cascade.ai.base

import dev.aurakai.auraframefx.domains.genesis.models.AgentResponse
import dev.aurakai.auraframefx.domains.genesis.models.AgentType
import dev.aurakai.auraframefx.domains.genesis.models.AiRequest

import kotlinx.coroutines.flow.Flow

/**
 * Interface representing an AI agent.
 */
interface Agent {
    /**
     * Returns the name of the agent.
     */
    fun getName(): String?

    /**
     * Returns the type or model of the agent.
     */
    fun getType(): AgentType

    /**
     * Process a request and return a response
     */
    suspend fun processRequest(request: AiRequest, context: String): AgentResponse

    /**
     * Process a request and return a flow of responses
     */
    fun processRequestFlow(request: AiRequest): Flow<AgentResponse>

}
