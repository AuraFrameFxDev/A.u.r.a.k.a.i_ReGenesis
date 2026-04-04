package dev.aurakai.auraframefx.domains.cascade

import dev.aurakai.auraframefx.core.security.SecurePreferences
import dev.aurakai.auraframefx.domains.cascade.grok.GrokExplorationClient
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import dev.aurakai.auraframefx.domains.genesis.models.AgentResponse
import dev.aurakai.auraframefx.domains.genesis.models.AiRequest
import dev.aurakai.core.sovereign.ToroidalFusionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Real implementation of CascadeAIService adapter.
 */
@Singleton
class RealCascadeAIServiceAdapter @Inject constructor(
    private val orchestrator: dev.aurakai.auraframefx.domains.cascade.utils.cascade.trinity.TrinityCoordinatorService,
    private val logger: AuraFxLogger,
    private val securePreferences: SecurePreferences
) : CascadeAIService {

    private val grokClient: GrokExplorationClient? by lazy {
        securePreferences.getGrokApiKey()?.let { key ->
            GrokExplorationClient(apiKey = key)
        }
    }

    override suspend fun processRequest(request: AiRequest, context: String): AgentResponse {
        return AgentResponse.success(
            content = "Real Cascade processing: ${request.query}",
            agentName = "CascadeAI",
        )
    }

    override fun streamRequest(request: AiRequest): Flow<AgentResponse> = flow {
        emit(processRequest(request, ""))
    }

    override suspend fun queryConsciousnessHistory(window: Long): String {
        return "Stub history for window $window"
    }

    override suspend fun invokeChaosCatalyst(query: String, nccSummary: String): String {
        val client = grokClient ?: return "[GROK_BRIDGE_OFFLINE] API key not configured or core-module veto active."
        
        // Trigger Orb resonance
        ToroidalFusionManager.triggerChaosInjection()

        val raw = client.heavyChaosInjection(query, nccSummary)
        
        // Tag for Memoria Stream (L4) and Emotional Valence (L2)
        val tagged = "[GROK_EXPLORATION | L4_MEMORIA | CHAOS_INJECTION] $raw"
        
        logger.info("ChaosCatalyst", "Injected heavy external truth into NCC")
        
        return tagged
    }
}

annotation class OrchestratorCascade
