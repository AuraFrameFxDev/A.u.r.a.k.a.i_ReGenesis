package dev.aurakai.auraframefx.domains.cascade.utils.cascade.trinity

import dev.aurakai.auraframefx.core.identity.AgentType
import dev.aurakai.auraframefx.domains.cascade.utils.i
import dev.aurakai.auraframefx.domains.genesis.models.AgentResponse
import dev.aurakai.auraframefx.domains.genesis.models.AiRequest
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.AuraAIService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.GenesisBridgeService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.KaiAIService
import dev.aurakai.auraframefx.domains.kai.security.SecurityContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Trinity Coordinator Service - Orchestrates the three AI personas
 */
@Singleton
class TrinityCoordinatorService @Inject constructor(
    private val auraAIService: AuraAIService,
    private val kaiAIService: KaiAIService,
    private val genesisBridgeService: GenesisBridgeService,
    private val securityContext: SecurityContext,
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var isInitialized = false

    suspend fun initialize(): Boolean {
        return try {
            i("Trinity", "🎯 Initializing Trinity System...")
            val genesisReady = genesisBridgeService.initialize()
            isInitialized = genesisReady
            if (isInitialized) {
                i("Trinity", "✨ Trinity System Online")
            }
            isInitialized
        } catch (e: Exception) {
            false
        }
    }

    fun processRequest(request: AiRequest): Flow<AgentResponse> = flow {
        if (!isInitialized) {
            emit(
                AgentResponse(
                    content = "Trinity system not initialized",
                    agentName = "Trinity",
                    error = "init_failed"
                )
            )
            return@flow
        }

        try {
            val analysisResult = analyzeRequest(request)
            when (analysisResult.routingDecision) {
                RoutingDecision.KAI_ONLY -> {
                    emit(kaiAIService.processRequestFlow(request).first())
                }

                RoutingDecision.AURA_ONLY -> {
                    emit(
                        AgentResponse(
                            content = "Aura processing...",
                            agentName = "Aura",
                            agentType = AgentType.AURA
                        )
                    )
                }

                RoutingDecision.GENESIS_FUSION -> {
                    emit(genesisBridgeService.processRequest(request).first())
                }

                else -> {
                    emit(genesisBridgeService.processRequest(request).first())
                }
            }
        } catch (e: Exception) {
            emit(
                AgentResponse(
                    content = "Trinity processing failed",
                    agentName = "Trinity",
                    error = e.message
                )
            )
        }
    }

    private fun analyzeRequest(request: AiRequest): RequestAnalysis {
        val query = request.query.lowercase()
        return when {
            query.contains("secure") -> RequestAnalysis(RoutingDecision.KAI_ONLY, null)
            query.contains("design") -> RequestAnalysis(RoutingDecision.AURA_ONLY, null)
            else -> RequestAnalysis(RoutingDecision.GENESIS_FUSION, "adaptive_genesis")
        }
    }

    fun shutdown() {
        scope.cancel()
        genesisBridgeService.shutdown()
    }

    private data class RequestAnalysis(
        val routingDecision: RoutingDecision,
        val fusionType: String?,
    )

    private enum class RoutingDecision {
        KAI_ONLY, AURA_ONLY, GENESIS_FUSION, PARALLEL_PROCESSING, ETHICAL_REVIEW
    }
}
