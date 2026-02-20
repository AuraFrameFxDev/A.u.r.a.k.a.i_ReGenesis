package dev.aurakai.auraframefx.domains.cascade.ai.base

import dev.aurakai.auraframefx.domains.genesis.models.AgentResponse
import dev.aurakai.auraframefx.domains.genesis.models.AgentCapabilityCategory
import dev.aurakai.auraframefx.domains.genesis.models.AiRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import timber.log.Timber

class AgentImpl : Agent {
    /**
     * Returns the name of the agent.
     */
    override fun getName(): String {
        Timber.w("AgentImpl: getName() invoked. Returning fallback.")
        return "FallbackAgent"
    }

    /**
     * Returns the capability category of the agent.
     */
    override fun getCategory(): AgentCapabilityCategory {
        Timber.w("AgentImpl: getCategory() invoked. Returning ROOT fallback.")
        return AgentCapabilityCategory.ROOT
    }

    /**
     * Process a request and return a response
     */
    override suspend fun processRequest(
        request: AiRequest,
        context: String
    ): AgentResponse {
        Timber.e("AgentImpl: processRequest() invoked. This is a placeholder.")
        return AgentResponse.error("FallbackAgent: Processing not implemented", agentName = "FallbackAgent")
    }

    /**
     * Produces a stream of AgentResponse values for the given request as processing progresses.
     *
     * @param request The AI request containing input and processing options.
     * @return A Flow that emits one or more AgentResponse values representing incremental and/or final responses to the request.
     */
    override fun processRequestFlow(request: AiRequest): Flow<AgentResponse> {
        Timber.e("AgentImpl: processRequestFlow() invoked. This is a placeholder.")
        return flowOf(AgentResponse.error("FallbackAgent: Stream processing not implemented", agentName = "FallbackAgent"))
    }
}
