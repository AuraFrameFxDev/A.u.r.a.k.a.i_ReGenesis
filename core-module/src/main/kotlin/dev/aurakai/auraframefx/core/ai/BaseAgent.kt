package dev.aurakai.auraframefx.core.ai

import dev.aurakai.auraframefx.core.identity.AgentType
import dev.aurakai.auraframefx.core.identity.CatalystIdentity
import dev.aurakai.auraframefx.core.messaging.AgentMessage
import dev.aurakai.auraframefx.core.orchestration.OrchestratableAgent
import dev.aurakai.auraframefx.domains.genesis.models.AgentResponse
import dev.aurakai.auraframefx.domains.genesis.models.AiRequest
import kotlinx.coroutines.CoroutineScope

/**
 * Genesis Base Agent Implementation
 * Provides common functionality for all AI agents.
 * This base version is simplified to avoid circular dependencies in core.
 */
abstract class BaseAgent(
    override val agentName: String,
    val identity: CatalystIdentity
) : OrchestratableAgent {

    @Deprecated("Use identity.agentType instead", ReplaceWith("identity.agentType"))
    protected val agentType: AgentType get() = identity.agentType

    open fun getName(): String = agentName

    open fun getType(): AgentType = identity.agentType

    // --- Orchestration Support ---

    protected var orchestrationScope: CoroutineScope? = null
    protected var isOrchestratorInitialized = false

    override suspend fun initialize(scope: CoroutineScope) {
        orchestrationScope = scope
        isOrchestratorInitialized = true
    }

    override suspend fun start() {
        // Default implementation - subclasses can override
    }

    override suspend fun pause() {
        // Default implementation - subclasses can override
    }

    override suspend fun resume() {
        // Default implementation - subclasses can override
    }

    override suspend fun shutdown() {
        orchestrationScope = null
        isOrchestratorInitialized = false
    }

    override suspend fun processRequest(
        request: AiRequest,
        context: String,
        agentType: AgentType
    ): AgentResponse {
        return AgentResponse.error("BaseAgent cannot process direct requests", agentName)
    }

    override suspend fun onAgentMessage(message: AgentMessage) {
        // Default no-op
    }

    companion object {
        @Volatile
        var isOrchestratorInitialized: Boolean = false
    }
}
