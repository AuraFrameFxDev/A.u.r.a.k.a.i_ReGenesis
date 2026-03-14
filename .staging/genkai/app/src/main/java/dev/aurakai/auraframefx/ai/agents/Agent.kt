package dev.aurakai.auraframefx.ai.agents

import dev.aurakai.auraframefx.models.AgentResponse
import dev.aurakai.auraframefx.models.AgentType
import dev.aurakai.auraframefx.models.AiRequest
import dev.aurakai.auraframefx.models.InteractionResponse
import kotlinx.coroutines.flow.Flow

/**
 * Interface representing an AI agent.
 */
internal interface Agent {
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
     * Produces a stream of AgentResponse values for the given request as processing progresses.
     *
     * @param request The AI request containing input and processing options.
     * @return A Flow that emits one or more AgentResponse values representing incremental and/or final responses to the request.
     */
    fun processRequestFlow(request: AiRequest): Flow<AgentResponse>

    /**
     * Constructs an InteractionResponse from the provided fields.
     *
     * @param content The response content.
     * @param success True if the interaction succeeded, false otherwise.
     * @param timestamp Epoch milliseconds for when the interaction occurred.
     * @param metadata Additional key-value data associated with the interaction.
     * @return An InteractionResponse populated with the given content, success flag, timestamp, and metadata.
     */
    fun InteractionResponse(
        content: String,
        success: Boolean,
        timestamp: Long,
        metadata: Map<String, Any>
    ): InteractionResponse
}
