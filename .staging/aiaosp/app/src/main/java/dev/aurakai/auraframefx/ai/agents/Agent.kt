package dev.aurakai.auraframefx.ai.agents

import dev.aurakai.auraframefx.model.AgentResponse
import dev.aurakai.auraframefx.model.AgentType
import dev.aurakai.auraframefx.model.AiRequest
import kotlinx.coroutines.flow.Flow

/**
 * Top-level value declaration for versioning or identification.
 */
const val TOPL_VL: String = "1.0.0"

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
