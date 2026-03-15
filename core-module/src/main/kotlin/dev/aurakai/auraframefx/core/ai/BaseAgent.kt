package dev.aurakai.auraframefx.core.ai

import dev.aurakai.auraframefx.core.identity.CatalystIdentity
import dev.aurakai.auraframefx.core.orchestration.OrchestratableAgent
import dev.aurakai.auraframefx.domains.genesis.models.AgentType
import kotlinx.coroutines.CoroutineScope

/**
 * Genesis Base Agent Implementation
 * Provides common functionality for all AI agents.
 * This base version is simplified to avoid circular dependencies in core.
 */
abstract class BaseAgent(
    override val agentName: String,
    protected val agentType: AgentType
) : OrchestratableAgent {

    protected val catalystIdentity: CatalystIdentity by lazy {
        CatalystIdentity.fromAgentType(agentType)
    }

    open fun getName(): String = agentName

    open fun getType(): AgentType = agentType

    // --- Orchestration Support ---

    protected var orchestrationScope: CoroutineScope? = null
    protected var isOrchestratorInitialized = false

    override suspend fun initialize(scope: CoroutineScope) {
        orchestrationScope = scope
        isOrchestratorInitialized = true
        Companion.isOrchestratorInitialized = true
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

    companion object {
        @Volatile
        var isOrchestratorInitialized: Boolean = false
    }
}
