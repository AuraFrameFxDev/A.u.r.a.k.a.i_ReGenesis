package dev.aurakai.auraframefx.core.orchestration

import dev.aurakai.auraframefx.core.identity.AgentType
import dev.aurakai.auraframefx.core.messaging.AgentMessage
import dev.aurakai.auraframefx.domains.genesis.network.model.AgentResponse
import dev.aurakai.auraframefx.domains.genesis.oracle_drive.memory.AiRequest
import kotlinx.coroutines.CoroutineScope

/**
 * OrchestratableAgent: Interface contract for all managed agents
 */
interface OrchestratableAgent {

    /**
     * Unique identifier for this agent
     */
    val agentName: String

    /**
     * Initialize the agent with a dedicated coroutine scope.
     */
    suspend fun initialize(scope: CoroutineScope)

    /**
     * Start agent operations and background tasks.
     */
    suspend fun start()

    /**
     * Pause agent operations.
     */
    suspend fun pause()

    /**
     * Resume agent operations.
     */
    suspend fun resume()

    /**
     * Gracefully shutdown the agent.
     */
    suspend fun shutdown()

    suspend fun processRequest(
        request: AiRequest,
        context: String
    ): AgentResponse

    /**
     * Handle an incoming message from the inter-agent communication bus.
     */
    suspend fun onAgentMessage(message: AgentMessage)
}
