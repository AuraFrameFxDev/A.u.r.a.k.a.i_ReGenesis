package dev.aurakai.auraframefx.ai.agents

import dev.aurakai.auraframefx.models.AgentResponse
import dev.aurakai.auraframefx.models.AgentType
import dev.aurakai.auraframefx.models.AiRequest
import kotlinx.coroutines.flow.Flow

class AgentImpl : Agent {
    /**
     * Returns the name of the agent.
     */
    override fun getName(): String {
        TODO("Not yet implemented")
    }

    /**
     * Returns the type or model of the agent.
     */
    override fun getType(): AgentType {
        TODO("Not yet implemented")
    }

    /**
     * Process a request and return a response
     */
    override suspend fun processRequest(
        request: AiRequest,
        context: String
    ): AgentResponse {
        TODO("Not yet implemented")
    }

    /**
     * Produces a stream of AgentResponse values for the given request as processing progresses.
     *
     * @param request The AI request containing input and processing options.
     * @return A Flow that emits one or more AgentResponse values representing incremental and/or final responses to the request.
     */
    override fun processRequestFlow(request: AiRequest): Flow<AgentResponse> {
        TODO("Not yet implemented")
    }
}
