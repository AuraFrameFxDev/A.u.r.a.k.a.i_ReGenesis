package dev.aurakai.auraframefx.ai.agents

import dev.aurakai.auraframefx.models.AgentResponse
import dev.aurakai.auraframefx.models.AgentType
import dev.aurakai.auraframefx.models.AiRequest

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
