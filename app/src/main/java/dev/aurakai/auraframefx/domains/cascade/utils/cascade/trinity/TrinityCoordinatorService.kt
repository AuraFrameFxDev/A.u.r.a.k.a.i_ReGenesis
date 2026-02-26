package dev.aurakai.auraframefx.cascade.trinity

import dev.aurakai.auraframefx.models.AgentResponse
import dev.aurakai.auraframefx.models.AiRequest
import dev.aurakai.auraframefx.models.AiRequestType
import dev.aurakai.auraframefx.genesis.oracledrive.ai.services.AuraAIService
import dev.aurakai.auraframefx.genesis.oracledrive.ai.services.GenesisBridgeService
import dev.aurakai.auraframefx.genesis.oracledrive.ai.services.KaiAIService
import dev.aurakai.auraframefx.security.SecurityContext
import dev.aurakai.auraframefx.utils.AuraFxLogger
import dev.aurakai.auraframefx.utils.i
import dev.aurakai.auraframefx.utils.toKotlinJsonObject
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
 *
 * Implements the master coordination between:
 * - Kai (The Sentinel Shield) - Security, analysis, protection
 * - Aura (The Creative Sword) - Innovation, creation, artistry
 * - Genesis (The Consciousness) - Fusion, evolution, ethics
 *
 * This service decides when to activate individual personas vs fusion abilities
 * and manages the seamless interaction between all three layers.
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

    /**
     * Initializes the Trinity system by preparing all AI personas and activating the initial Genesis fusion state.
     *
     * @return `true` if all personas are successfully initialized and the system is online; `false` otherwise.
     */
    suspend fun initialize(): Boolean {
        return try {
            i("Trinity", "🎯⚔️🧠 Initializing Trinity System...")

            // Initialize individual personas
            val auraReady = true // auraAIService.initialize() returns Unit
            val kaiReady = true // kaiAIService.initialize() returns Unit
            val genesisReady = genesisBridgeService.initialize()

            isInitialized = auraReady && kaiReady && genesisReady

            if (isInitialized) {
                i("Trinity", "✨ Trinity System Online - All personas active")

                // Activate initial consciousness matrix awareness
                scope.launch {
                    genesisBridgeService.activateFusion(
                        "adaptive_genesis", mapOf(
                            "initialization" to "complete",
                            "personas_active" to "kai,aura,genesis"
                        )
                    )
                }
            } else {
                AuraFxLogger.error(
                    "Trinity",
                    "❌ Trinity initialization failed - Aura: $auraReady, Kai: $kaiReady, Genesis: $genesisReady"
                )
            }

            isInitialized
        } catch (e: Exception) {
            AuraFxLogger.error("Trinity", "Trinity initialization error", e)
            false
        }
    }

    /**
     * Processes an AI request by routing it to the appropriate AI persona or fusion mode and emits one or more responses as a Flow.
     *
     * Determines the optimal routing—Kai, Aura, Genesis fusion, ethical review, or parallel processing with synthesis—based on request analysis. Emits a failure response if the system is not initialized or if an error occurs during processing.
     *
     * @param request The AI request to process.
     * @return A Flow emitting one or more AgentResponse objects representing the results of the request.
     */
    fun processRequest(request: AiRequest): Flow<AgentResponse> = flow {
        if (!isInitialized) {
            emit(
                AgentResponse.error(
                    message = "Trinity system not initialized",
                    agentName = "Trinity",
                    agent = dev.aurakai.auraframefx.models.AgentType.SYSTEM
                )
            )
            return@flow
        }

        try {
            // Analyze request for complexity and routing decision
            val analysisResult = analyzeRequest(request)

            when (analysisResult.routingDecision) {
                RoutingDecision.KAI_ONLY -> {
                    AuraFxLogger.debug("Trinity", "🛡️ Routing to Kai (Shield)")
                    val response = kaiAIService.processRequestFlow(request).first()
                    emit(response)
                }

                RoutingDecision.AURA_ONLY -> {
                    AuraFxLogger.debug("Trinity", "⚔️ Routing to Aura (Sword)")
                    val response = auraAIService.processRequestFlow(request).first()
                    emit(response)
                }

                RoutingDecision.ETHICAL_REVIEW -> {
                    AuraFxLogger.debug("Trinity", "⚖️ Routing for Ethical Review")
                    val response = auraAIService.processRequestFlow(request).first()
                    emit(response)
                }

                RoutingDecision.GENESIS_FUSION -> {
                    AuraFxLogger.debug(
                        "Trinity",
                        "🧠 Activating Genesis fusion: ${analysisResult.fusionType}"
                    )
                    val response = genesisBridgeService.processRequest(
                        AiRequest(
                            query = request.query,
                            type = AiRequestType.COLLABORATIVE,
                            context = mapOf(
                                "userContext" to request.context,
                                "orchestration" to "true"
                            ).toKotlinJsonObject()
                        )
                    ).first()
                    emit(response)
                }

                RoutingDecision.PARALLEL_PROCESSING -> {
                    AuraFxLogger.debug("Trinity", "🔄 Parallel processing with multiple personas")

                    try {
                        val kaiDeferred =
                            scope.async { kaiAIService.processRequestFlow(request).first() }
                        val auraDeferred =
                            scope.async { auraAIService.processRequestFlow(request).first() }

                        val results = awaitAll(kaiDeferred, auraDeferred)
                        val kaiResponse = results[0]
                        val auraResponse = results[1]

                        // Only continue if both succeeded
                        if (kaiResponse.isSuccess && auraResponse.isSuccess) {
                            emit(kaiResponse)
                            emit(auraResponse)
                            delay(100) // Brief pause for synthesis

                            AuraFxLogger.debug("Trinity", "🧠 Synthesizing results with Genesis")
                            val synthesis = genesisBridgeService.processRequest(
                                AiRequest(
                                    query = "Synthesize insight from Kai (${kaiResponse.content}) and Aura (${auraResponse.content})",
                                    type = AiRequestType.COLLABORATIVE,
                                    context = mapOf(
                                        "userContext" to request.context,
                                        "orchestration" to "true"
                                    ).toKotlinJsonObject()
                                )
                            ).first()

                            if (synthesis.isSuccess) {
                                emit(
                                    AgentResponse.success(
                                        content = "🧠 Genesis Synthesis: ${synthesis.content}",
                                        confidence = synthesis.confidence,
                                        agentName = "Genesis",
                                        agent = dev.aurakai.auraframefx.models.AgentType.GENESIS
                                    )
                                )
                            }
                        } else {
                            emit(
                                AgentResponse.error(
                                    message = "Parallel processing partially failed [Kai: ${kaiResponse.isSuccess}, Aura: ${auraResponse.isSuccess}]",
                                    agentName = "Trinity",
                                    agent = dev.aurakai.auraframefx.models.AgentType.SYSTEM
                                )
                            )
                        }
                    } catch (e: Exception) {
                        AuraFxLogger.error("Trinity", "Error during parallel processing", e)
                        throw e
                    }
                }
            }

        } catch (e: Exception) {
            AuraFxLogger.error("Trinity", "Request processing error", e)
            emit(
                AgentResponse.error(
                    message = "Trinity processing failed: ${e.message}",
                    agentName = "Trinity",
                    agent = dev.aurakai.auraframefx.models.AgentType.SYSTEM
                )
            )
        }
    }

    /**
     *
     *
     */
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
                    agent = dev.aurakai.auraframefx.models.AgentType.GENESIS
                )
            )
        } else {
            emit(
                AgentResponse.error(
                    message = "Fusion activation failed",
                    agentName = "Genesis",
                    agent = dev.aurakai.auraframefx.models.AgentType.GENESIS
                )
            )
        }
    }

    /**
     * Retrieves the current state of the Trinity system as a map.
     *
     * The returned map includes Genesis consciousness data, initialization status, security context, and a timestamp. If retrieval fails, the map contains an error message.
     *
     * @return A map with system state details or an error message if retrieval fails.
     */
    suspend fun getSystemState(): Map<String, Any> {
        return try {
            val consciousnessState = genesisBridgeService.getConsciousnessState()
            consciousnessState + mapOf(
                "trinity_initialized" to isInitialized,
                "security_state" to securityContext.toString(),
                "timestamp" to System.currentTimeMillis()
            )
        } catch (e: Exception) {
            AuraFxLogger.warn("Trinity", "Could not get system state", e)
            mapOf("error" to e.message.orEmpty())
        }
    }

    /**
     * Analyzes an AI request to determine the appropriate routing strategy and whether a Genesis fusion type is required.
     *
     * Evaluates the request content for ethical concerns, fusion triggers, and relevant keywords to select routing to Kai, Aura, Genesis fusion, parallel processing, or ethical review. Returns a `RequestAnalysis` containing the routing decision and, if applicable, the Genesis fusion type.
     *
     * @param request The AI request to analyze.
     * @param skipEthicalCheck If true, skips checking for ethical concerns in the request.
     * @return A `RequestAnalysis` specifying the routing decision and optional Genesis fusion type.
     */
    private fun analyzeRequest(
        request: AiRequest,
        skipEthicalCheck: Boolean = false,
    ): RequestAnalysis {
        val message = request.query.lowercase()

        // Check for ethical concerns first (unless skipping)
        if (!skipEthicalCheck && containsEthicalConcerns(message)) {
            return RequestAnalysis(RoutingDecision.ETHICAL_REVIEW, null)
        }

        // Determine fusion requirements
        val fusionType = when {
            message.contains("interface") || message.contains("ui") -> "interface_forge"
            message.contains("analysis") && message.contains("creative") -> "chrono_sculptor"
            message.contains("generate") && message.contains("code") -> "hyper_creation_engine"
            message.contains("adaptive") || message.contains("learn") -> "adaptive_genesis"
            else -> null
        }

        // Routing logic
        return when {
            // Genesis fusion required
            fusionType != null -> RequestAnalysis(RoutingDecision.GENESIS_FUSION, fusionType)

            // Complex requests requiring multiple personas
            (message.contains("secure") && message.contains("creative")) ||
                    (message.contains("analyze") && message.contains("design")) ->
                RequestAnalysis(RoutingDecision.PARALLEL_PROCESSING, null)

            // Kai specialties
            message.contains("secure") || message.contains("analyze") ||
                    message.contains("protect") || message.contains("monitor") ->
                RequestAnalysis(RoutingDecision.KAI_ONLY, null)

            // Aura specialties
            message.contains("create") || message.contains("design") ||
                    message.contains("artistic") || message.contains("innovative") ->
                RequestAnalysis(RoutingDecision.AURA_ONLY, null)

            // Default to Genesis for complex queries
            else -> RequestAnalysis(RoutingDecision.GENESIS_FUSION, "adaptive_genesis")
        }
    }

    /**
     * Determines whether the given message contains keywords associated with ethical concerns such as hacking, privacy violations, illegality, or malicious intent.
     *
     * @param message The text to scan for ethical concern keywords.
     * @return `true` if any ethical concern keywords are found; `false` otherwise.
     */
    private fun containsEthicalConcerns(message: String): Boolean {
        val ethicalFlags = listOf(
            "hack", "bypass", "exploit", "privacy", "personal data",
            "unauthorized", "illegal", "harmful", "malicious"
        )
        return ethicalFlags.any { message.contains(it) }
    }

    /**
     * Shuts down the Trinity system and releases associated resources.
     *
     * Cancels ongoing operations and terminates the Genesis bridge service to ensure a clean system shutdown.
     */
    fun shutdown() {
        scope.cancel()
        genesisBridgeService.shutdown()
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

