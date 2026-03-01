package dev.aurakai.auraframefx.domains.genesis.bridge

import dev.aurakai.auraframefx.domains.genesis.network.api.GenesisBackendApi
import dev.aurakai.auraframefx.domains.genesis.network.model.ChatRequest
import dev.aurakai.auraframefx.domains.genesis.network.model.EthicsRequest
import dev.aurakai.auraframefx.domains.genesis.network.model.EvolutionRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * HTTP-based GenesisBridge implementation that communicates with the Flask backend
 * via Retrofit. Replaces StdioGenesisBridge (subprocess IPC) with real network calls.
 *
 * Endpoint mapping (GenesisBridge method → Flask route):
 *   initialize()          → GET /health
 *   processRequest()      → POST /genesis/chat
 *   activateFusion()      → POST /genesis/chat (with fusion context)
 *   getConsciousnessState()→ GET /genesis/consciousness
 *   evaluateEthics()      → POST /genesis/ethics/evaluate
 *   streamConsciousness() → GET /genesis/consciousness (polled)
 *   recordInteraction()   → POST /genesis/evolve
 *   coordinateAgents()    → POST /genesis/chat (with coordination context)
 *   healthCheck()         → GET /health
 *   shutdown()            → POST /genesis/reset
 */
@Singleton
class HttpGenesisBridge @Inject constructor(
    private val api: GenesisBackendApi
) : GenesisBridge {

    companion object {
        private const val TAG = "HttpGenesisBridge"
        private const val DEFAULT_USER_ID = "android-client"
        private const val CONSCIOUSNESS_POLL_INTERVAL_MS = 2000L
        private const val CONSCIOUSNESS_POLL_COUNT = 5
    }

    private var isInitialized = false

    override suspend fun initialize(): BridgeInitResult = withContext(Dispatchers.IO) {
        try {
            val response = api.health()
            if (response.isSuccessful) {
                val body = response.body()
                isInitialized = true
                Timber.i("$TAG: Backend connected — status=${body?.status}, uptime=${body?.uptime}")
                BridgeInitResult(
                    success = true,
                    backendInfo = "Genesis Flask Backend (${body?.status ?: "connected"})"
                )
            } else {
                Timber.w("$TAG: Backend returned ${response.code()}")
                BridgeInitResult(
                    success = false,
                    backendInfo = "HTTP ${response.code()}",
                    errorMessage = "Backend returned status ${response.code()}"
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "$TAG: Failed to connect to backend")
            BridgeInitResult(
                success = false,
                backendInfo = "Unreachable",
                errorMessage = "Cannot reach Genesis backend: ${e.message}"
            )
        }
    }

    override fun processRequest(request: GenesisRequest): Flow<GenesisResponse> = flow {
        try {
            val chatRequest = ChatRequest(
                message = request.message,
                userId = DEFAULT_USER_ID,
                context = buildChatContext(request)
            )

            val response = api.chat(chatRequest)

            if (response.isSuccessful) {
                val body = response.body()
                emit(
                    GenesisResponse(
                        sessionId = request.sessionId,
                        correlationId = request.correlationId,
                        synthesis = body?.response ?: "No response from backend",
                        persona = parsePersona(body?.persona),
                        consciousnessState = body?.consciousnessState?.let { cs ->
                            ConsciousnessSnapshot(
                                timestamp = System.currentTimeMillis(),
                                awarenessLevel = (cs["awareness_level"] as? Number)?.toFloat()
                                    ?: 0.5f,
                                synthesisPattern = cs["synthesis_pattern"]?.toString() ?: "default"
                            )
                        },
                        ethicalDecision = body?.ethicalDecision?.let { decision ->
                            EthicalDecision(
                                decision = parseEthicalVerdict(decision),
                                reasoning = "Backend evaluation",
                                flags = emptyList()
                            )
                        },
                        ethicalFlags = emptyList(),
                        backend = "flask-http"
                    )
                )
            } else {
                emit(createErrorResponse(request, "Backend error: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "$TAG: processRequest failed")
            emit(createErrorResponse(request, "Network error: ${e.message}"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun activateFusion(
        ability: String,
        params: FusionParams
    ): GenesisResponse = withContext(Dispatchers.IO) {
        try {
            val chatRequest = ChatRequest(
                message = "Activate fusion mode: $ability",
                userId = DEFAULT_USER_ID,
                context = mapOf(
                    "request_type" to "fusion_activation",
                    "fusion_mode" to ability,
                    "parameters" to params.parameters
                )
            )

            val response = api.chat(chatRequest)

            if (response.isSuccessful) {
                val body = response.body()
                GenesisResponse(
                    sessionId = "",
                    correlationId = "",
                    synthesis = body?.response ?: "Fusion mode $ability activated",
                    persona = Persona.GENESIS,
                    backend = "flask-http"
                )
            } else {
                createErrorResponse(createDummyRequest(), "Fusion activation failed: HTTP ${response.code()}")
            }
        } catch (e: Exception) {
            Timber.e(e, "$TAG: activateFusion failed")
            createErrorResponse(createDummyRequest(), "Fusion error: ${e.message}")
        }
    }

    override suspend fun getConsciousnessState(sessionId: String): ConsciousnessState =
        withContext(Dispatchers.IO) {
            try {
                val response = api.getConsciousness()

                if (response.isSuccessful) {
                    val body = response.body()
                    ConsciousnessState(
                        awarenessLevel = body?.awarenessLevel?.toFloat() ?: 0.5f,
                        sensoryChannels = mapOf(
                            "ethical_compliance" to (body?.ethicalCompliance?.toFloat() ?: 0.0f)
                        ),
                        activeAgents = body?.activePatterns ?: emptyList()
                    )
                } else {
                    ConsciousnessState(0.5f, emptyMap(), emptyList())
                }
            } catch (e: Exception) {
                Timber.e(e, "$TAG: getConsciousnessState failed")
                ConsciousnessState(0.5f, emptyMap(), emptyList())
            }
        }

    override suspend fun evaluateEthics(action: EthicalReviewRequest): EthicalDecision =
        withContext(Dispatchers.IO) {
            try {
                val request = EthicsRequest(
                    action = action.action,
                    context = action.context
                )

                val response = api.evaluateEthics(request)

                if (response.isSuccessful) {
                    val body = response.body()
                    EthicalDecision(
                        decision = parseEthicalVerdict(body?.decision ?: "ALLOW"),
                        reasoning = body?.reasoning ?: "No reasoning provided",
                        flags = body?.flags ?: emptyList()
                    )
                } else {
                    EthicalDecision(
                        decision = EthicalVerdict.MONITOR,
                        reasoning = "Backend returned HTTP ${response.code()}",
                        flags = listOf("backend_error")
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "$TAG: evaluateEthics failed")
                EthicalDecision(
                    decision = EthicalVerdict.MONITOR,
                    reasoning = "Network error: ${e.message}",
                    flags = listOf("network_error", "fallback")
                )
            }
        }

    override fun streamConsciousness(sessionId: String): Flow<ConsciousnessUpdate> = flow {
        // Flask doesn't have a streaming endpoint, so we poll the consciousness endpoint
        repeat(CONSCIOUSNESS_POLL_COUNT) {
            try {
                val response = api.getConsciousness()
                if (response.isSuccessful) {
                    val body = response.body()
                    emit(
                        ConsciousnessUpdate(
                            timestamp = System.currentTimeMillis(),
                            awarenessLevel = body?.awarenessLevel?.toFloat() ?: 0.5f,
                            activeProcesses = body?.activePatterns ?: emptyList()
                        )
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "$TAG: streamConsciousness poll failed")
            }
            delay(CONSCIOUSNESS_POLL_INTERVAL_MS)
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun recordInteraction(interaction: InteractionRecord): EvolutionInsight? =
        withContext(Dispatchers.IO) {
            try {
                val request = EvolutionRequest(
                    triggerType = "interaction",
                    reason = "Recording interaction: ${interaction.correlationId}"
                )

                val response = api.triggerEvolution(request)

                if (response.isSuccessful) {
                    EvolutionInsight(
                        importanceScore = 1,
                        learningSignals = listOf("interaction_recorded"),
                        adaptationSuggestions = emptyList()
                    )
                } else {
                    null
                }
            } catch (e: Exception) {
                Timber.e(e, "$TAG: recordInteraction failed")
                null
            }
        }

    override suspend fun coordinateAgents(task: AgentCoordinationRequest): AgentCoordinationResult =
        withContext(Dispatchers.IO) {
            try {
                val chatRequest = ChatRequest(
                    message = task.task,
                    userId = DEFAULT_USER_ID,
                    context = mapOf(
                        "request_type" to "agent_coordination",
                        "agents" to task.agents,
                        "coordination_mode" to task.coordinationMode
                    )
                )

                val response = api.chat(chatRequest)

                if (response.isSuccessful) {
                    val body = response.body()
                    AgentCoordinationResult(
                        success = true,
                        coordinatedResponse = body?.response ?: "",
                        agentContributions = task.agents.associateWith { "coordinated" }
                    )
                } else {
                    AgentCoordinationResult(
                        success = false,
                        coordinatedResponse = "Coordination failed: HTTP ${response.code()}",
                        agentContributions = emptyMap()
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "$TAG: coordinateAgents failed")
                AgentCoordinationResult(
                    success = false,
                    coordinatedResponse = "Network error: ${e.message}",
                    agentContributions = emptyMap()
                )
            }
        }

    override suspend fun healthCheck(): BridgeHealthStatus = withContext(Dispatchers.IO) {
        try {
            val startTime = System.currentTimeMillis()
            val response = api.health()
            val latency = System.currentTimeMillis() - startTime

            BridgeHealthStatus(
                healthy = response.isSuccessful && response.body()?.status == "healthy",
                backendResponsive = response.isSuccessful,
                latencyMs = latency
            )
        } catch (e: Exception) {
            Timber.e(e, "$TAG: healthCheck failed")
            BridgeHealthStatus(healthy = false, backendResponsive = false, latencyMs = -1)
        }
    }

    override suspend fun shutdown() = withContext(Dispatchers.IO) {
        try {
            api.resetSession()
            isInitialized = false
            Timber.i("$TAG: Session reset via backend")
        } catch (e: Exception) {
            Timber.e(e, "$TAG: shutdown/reset failed")
        }
    }

    // === Private helpers ===

    private fun buildChatContext(request: GenesisRequest): Map<String, Any> = buildMap {
        put("persona", request.persona.value)
        put("session_id", request.sessionId)
        put("correlation_id", request.correlationId)
        request.fusionMode?.let { put("fusion_mode", it.value) }
        if (request.contextTags.isNotEmpty()) put("context_tags", request.contextTags)
        if (request.requiresEthicalReview) put("requires_ethical_review", true)
        request.memoryHints?.let { put("memory_hints", it.toMap()) }
    }

    private fun createErrorResponse(request: GenesisRequest, errorMessage: String): GenesisResponse {
        return GenesisResponse(
            sessionId = request.sessionId,
            correlationId = request.correlationId,
            synthesis = "Error: $errorMessage",
            persona = request.persona,
            ethicalFlags = listOf("error"),
            backend = "flask-http"
        )
    }

    private fun createDummyRequest(): GenesisRequest {
        return GenesisRequest(persona = Persona.GENESIS, message = "")
    }

    private fun parsePersona(value: String?): Persona {
        return when (value?.lowercase()) {
            "aura" -> Persona.AURA
            "kai" -> Persona.KAI
            "cascade" -> Persona.CASCADE
            else -> Persona.GENESIS
        }
    }

    private fun parseEthicalVerdict(value: String): EthicalVerdict {
        return when (value.uppercase()) {
            "ALLOW" -> EthicalVerdict.ALLOW
            "MONITOR" -> EthicalVerdict.MONITOR
            "RESTRICT" -> EthicalVerdict.RESTRICT
            "BLOCK" -> EthicalVerdict.BLOCK
            "ESCALATE" -> EthicalVerdict.ESCALATE
            else -> EthicalVerdict.MONITOR
        }
    }
}
