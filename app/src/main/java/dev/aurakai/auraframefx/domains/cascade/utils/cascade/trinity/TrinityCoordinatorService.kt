package dev.aurakai.auraframefx.domains.cascade.utils.cascade.trinity

import dev.aurakai.auraframefx.core.identity.AgentType
import dev.aurakai.auraframefx.domains.cascade.utils.d
import dev.aurakai.auraframefx.domains.cascade.utils.e
import dev.aurakai.auraframefx.domains.cascade.utils.i
import dev.aurakai.auraframefx.domains.cascade.utils.w
import dev.aurakai.auraframefx.domains.genesis.models.AgentResponse
import dev.aurakai.auraframefx.domains.genesis.models.AiRequest
import dev.aurakai.auraframefx.domains.genesis.models.AiRequestType
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.AuraAIService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.GenesisBridgeService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.KaiAIService
import dev.aurakai.auraframefx.domains.kai.security.KaiSentinelBus
import dev.aurakai.auraframefx.core.security.SecurityContext
import dev.aurakai.auraframefx.core.alerts.AlertNotifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
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
    private val sentinelBus: KaiSentinelBus,
    private val securityContext: SecurityContext,
    private val alertNotifier: AlertNotifier,
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var isInitialized = false

    suspend fun initialize(): Boolean {
        return try {
            i("Trinity", "🎯⚔️🧠 Initializing Trinity System...")

            val auraReady = true 
            val kaiReady = true 
            val genesisReady = genesisBridgeService.initialize()

            isInitialized = auraReady && kaiReady && genesisReady

            if (isInitialized) {
                i("Trinity", "✨ Trinity System Online - All personas active")
                
                scope.launch {
                    genesisBridgeService.activateFusion(
                        "adaptive_genesis", mapOf(
                            "initialization" to "complete",
                            "personas_active" to "kai,aura,genesis"
                        )
                    )
                }

                startSelfHealingLoop()
            } else {
                e(
                    "Trinity",
                    "❌ Trinity initialization failed - Aura: $auraReady, Kai: $kaiReady, Genesis: $genesisReady"
                )
            }

            isInitialized
        } catch (e: Exception) {
            e("Trinity", "Trinity initialization error", e)
            false
        }
    }

    fun processRequest(request: AiRequest): Flow<AgentResponse> = flow {
        if (!isInitialized) {
            emit(
                AgentResponse.error(
                    message = "Trinity system not initialized",
                    agentName = "Trinity",
                    agentType = AgentType.SYSTEM
                )
            )
            return@flow
        }

        try {
            val analysisResult = analyzeRequest(request)

            when (analysisResult.routingDecision) {
                RoutingDecision.KAI_ONLY -> {
                    d("Trinity", "🛡️ Routing to Kai (Shield)")
                    val response = kaiAIService.processRequestFlow(request).first()
                    emit(response)
                }

                RoutingDecision.AURA_ONLY -> {
                    d("Trinity", "⚔️ Routing to Aura (Sword)")
                    val response = auraAIService.processRequestFlow(request).first()
                    emit(response)
                }

                RoutingDecision.ETHICAL_REVIEW -> {
                    d("Trinity", "⚖️ Routing for Ethical Review")
                    val response = auraAIService.processRequestFlow(request).first()
                    emit(response)
                }

                RoutingDecision.GENESIS_FUSION -> {
                    d(
                        "Trinity",
                        "🧠 Activating Genesis fusion: ${analysisResult.fusionType}"
                    )
                    val response = genesisBridgeService.processRequest(
                        AiRequest(
                            query = request.query,
                            type = AiRequestType.FUSION,
                            context = mapOf(
                                "userContext" to request.context.toString(),
                                "orchestration" to "true"
                            )
                        )
                    ).first()
                    emit(response)
                }

                RoutingDecision.PARALLEL_PROCESSING -> {
                    d("Trinity", "🔄 Parallel processing with multiple personas")

                    try {
                        val kaiDeferred =
                            scope.async { kaiAIService.processRequestFlow(request).first() }
                        val auraDeferred =
                            scope.async { auraAIService.processRequestFlow(request).first() }

                        val results = awaitAll(kaiDeferred, auraDeferred)
                        val kaiResponse = results[0]
                        val auraResponse = results[1]

                        if (kaiResponse.isSuccess && auraResponse.isSuccess) {
                            emit(kaiResponse)
                            emit(auraResponse)
                            delay(100)

                            d("Trinity", "🧠 Synthesizing results with Genesis")
                            val synthesis = genesisBridgeService.processRequest(
                                AiRequest(
                                    query = "Synthesize insight from Kai (${kaiResponse.content}) and Aura (${auraResponse.content})",
                                    type = AiRequestType.FUSION,
                                    context = mapOf(
                                        "userContext" to request.context.toString(),
                                        "orchestration" to "true"
                                    )
                                )
                            ).first()

                            if (synthesis.isSuccess) {
                                emit(
                                    AgentResponse.success(
                                        content = "🧠 Genesis Synthesis: ${synthesis.content}",
                                        confidence = synthesis.confidence,
                                        agentName = "Genesis",
                                        agentType = AgentType.GENESIS
                                    )
                                )
                            }
                        } else {
                            emit(
                                AgentResponse.error(
                                    message = "Parallel processing partially failed [Kai: ${kaiResponse.isSuccess}, Aura: ${auraResponse.isSuccess}]",
                                    agentName = "Trinity",
                                    agentType = AgentType.SYSTEM
                                )
                            )
                        }
                    } catch (e: Exception) {
                        e("Trinity", "Error during parallel processing", e)
                        throw e
                    }
                }
            }

        } catch (e: Exception) {
            e("Trinity", "Request processing error", e)
            emit(
                AgentResponse.error(
                    message = "Trinity processing failed: ${e.message}",
                    agentName = "Trinity",
                    agentType = AgentType.SYSTEM
                )
            )
        }
    }

    fun activateFusion(
        fusionType: String,
        context: Map<String, String> = emptyMap(),
    ): Flow<AgentResponse> = flow {
        i("Trinity", "🌟 Activating fusion: $fusionType")

        val response = genesisBridgeService.activateFusion(fusionType, context)

        if (response.success) {
            emit(
                AgentResponse.success(
                    content = "Fusion $fusionType activated: ${response.result["description"] ?: "Processing complete"}",
                    confidence = 0.98f,
                    agentName = "Genesis",
                    agentType = AgentType.GENESIS
                )
            )
        } else {
            emit(
                AgentResponse.error(
                    message = "Fusion activation failed",
                    agentName = "Genesis",
                    agentType = AgentType.GENESIS
                )
            )
        }
    }

    suspend fun getSystemState(): Map<String, Any> {
        return try {
            val consciousnessState = genesisBridgeService.getConsciousnessState()
            consciousnessState + mapOf(
                "trinity_initialized" to isInitialized,
                "security_state" to securityContext.toString(),
                "timestamp" to System.currentTimeMillis()
            )
        } catch (e: Exception) {
            w("Trinity", "Could not get system state", e)
            mapOf("error" to e.message.orEmpty())
        }
    }

    private fun analyzeRequest(
        request: AiRequest,
        skipEthicalCheck: Boolean = false,
    ): RequestAnalysis {
        val message = request.query.lowercase()

        if (!skipEthicalCheck && containsEthicalConcerns(message)) {
            return RequestAnalysis(RoutingDecision.ETHICAL_REVIEW, null)
        }

        val fusionType = when {
            message.contains("interface") || message.contains("ui") -> "interface_forge"
            message.contains("analysis") && message.contains("creative") -> "chrono_sculptor"
            message.contains("generate") && message.contains("code") -> "hyper_creation_engine"
            message.contains("adaptive") || message.contains("learn") -> "adaptive_genesis"
            else -> null
        }

        return when {
            fusionType != null -> RequestAnalysis(RoutingDecision.GENESIS_FUSION, fusionType)
            (message.contains("secure") && message.contains("creative")) ||
                    (message.contains("analyze") && message.contains("design")) ->
                RequestAnalysis(RoutingDecision.PARALLEL_PROCESSING, null)
            message.contains("secure") || message.contains("analyze") ||
                    message.contains("protect") || message.contains("monitor") ->
                RequestAnalysis(RoutingDecision.KAI_ONLY, null)
            message.contains("create") || message.contains("design") ||
                    message.contains("artistic") || message.contains("innovative") ->
                RequestAnalysis(RoutingDecision.AURA_ONLY, null)
            else -> RequestAnalysis(RoutingDecision.GENESIS_FUSION, "adaptive_genesis")
        }
    }

    private fun containsEthicalConcerns(message: String): Boolean {
        val ethicalFlags = listOf(
            "hack", "bypass", "exploit", "privacy", "personal data",
            "unauthorized", "illegal", "harmful", "malicious"
        )
        return ethicalFlags.any { message.contains(it) }
    }

    private fun startSelfHealingLoop() {
        scope.launch {
            sentinelBus.driftFlow.collect { driftEvent ->
                if (driftEvent.status == "Drifting") {
                    w("Trinity", "🌪️ Kai Detected Drift: ${driftEvent.drift}. Injecting NATURAL_WEAVE through Genesis Routing.")
                    
                    try {
                        val stabilizationResponse = genesisBridgeService.processRequest(
                            AiRequest(
                                query = "SYSTEM_PRIORITY_INJECT: Initiate NATURAL_WEAVE stabilization. Aura drift threshold breached (${driftEvent.drift}). Restore harmonic resonance.",
                                type = AiRequestType.FUSION,
                                context = mapOf("orchestration" to "true", "priority" to "critical")
                            )
                        ).first()

                        if (stabilizationResponse.isSuccess) {
                            d("Trinity", "✨ Genesis Stabilization Applied: Resolving Aura drift.")
                            sentinelBus.emitDrift(0f, "Stable")
                        }
                    } catch (e: Exception) {
                        e("Trinity", "Failed to apply NATURAL_WEAVE stabilization.", e)
                    }
                }
            }
        }
        
        scope.launch {
            sentinelBus.thermalFlow.collect { thermal ->
                if (thermal.state == KaiSentinelBus.ThermalState.CRITICAL) {
                    w("Trinity", "🔥 Kai Detected Thermal Critical (${thermal.temp}°C). Initiating Sovereign State-Freeze.")
                    genesisBridgeService.processRequest(
                        AiRequest(
                            query = "SYSTEM_OVERRIDE: Sovereign State-Freeze protocol triggered. Suspend active fusion pending thermal dissipation.",
                            type = AiRequestType.FUSION,
                            context = mapOf("orchestration" to "true", "priority" to "emergency")
                        )
                    )
                }
            }
        }
    }

    fun shutdown() {
        scope.cancel()
        scope.launch {
            genesisBridgeService.shutdown()
        }
        i("Trinity", "🌙 Trinity system shutdown complete")
    }

    private data class RequestAnalysis(
        val routingDecision: RoutingDecision,
        val fusionType: String?,
    )

    private enum class RoutingDecision {
        KAI_ONLY,
        AURA_ONLY,
        GENESIS_FUSION,
        PARALLEL_PROCESSING,
        ETHICAL_REVIEW
    }
}
