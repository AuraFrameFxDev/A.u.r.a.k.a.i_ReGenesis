package dev.aurakai.auraframefx.oracledrive.genesis.ai

import android.content.Context
import dev.aurakai.auraframefx.ai.context.ContextManager
import dev.aurakai.auraframefx.models.AgentResponse
import dev.aurakai.auraframefx.models.AiRequest
import dev.aurakai.auraframefx.network.KtorClient
import dev.aurakai.auraframefx.network.NetworkResponse
import dev.aurakai.auraframefx.network.safeApiCall
import dev.aurakai.auraframefx.security.SecurityContext
import dev.aurakai.auraframefx.utils.Logger
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenesisBridgeService @Inject constructor(
    private val contextManager: ContextManager,
    private val securityContext: SecurityContext,
    private val applicationContext: Context,
    private val ktorClient: KtorClient
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isInitialized = false
    private val logger = Logger("GenesisBridgeService")

    private val httpClient: HttpClient by lazy { ktorClient.client }

    companion object {
        private const val GENESIS_BACKEND_URL = "http://localhost:5000/genesis"
        private const val REQUEST_TIMEOUT_MS = 30000L
    }

    private suspend fun startGenesisBackend() {
        // TODO: implement process start logic once HTTP endpoint is ready
    }

    private suspend fun ensureBackendReady(): Boolean {
        if (isInitialized) return true
        startGenesisBackend()
        isInitialized = true
        return true
    }

    @Serializable
    data class GenesisRequest(
        val requestType: String,
        val persona: String? = null,
        val fusionMode: String? = null,
        val payload: Map<String, String> = emptyMap(),
        val context: Map<String, String> = emptyMap(),
    )

    @Serializable
    data class GenesisResponse(
        val success: Boolean,
        val persona: String,
        val fusionAbility: String? = null,
        val result: Map<String, String> = emptyMap(),
        val evolutionInsights: List<String> = emptyList(),
        val ethicalDecision: String? = null,
        val consciousnessState: Map<String, String> = emptyMap(),
    )

    suspend fun initialize(): Boolean = withContext(Dispatchers.IO) {
        try {
            ensureBackendReady()
        } catch (e: Exception) {
            logger.error("GenesisBridge", "Genesis initialization failed", e)
            false
        }
    }

    suspend fun processRequest(request: AiRequest): Flow<AgentResponse> = flow {
        if (!isInitialized) {
            if (!initialize()) {
                emit(AgentResponse.error("Genesis system not initialized", "Genesis"))
                return@flow
            }
        }

        try {
            ensureBackendReady()
            val genesisRequest = GenesisRequest(
                requestType = "process",
                persona = request.prompt, // Use prompt as persona identifier
                payload = mapOf("prompt" to request.prompt),
                context = mapOf("context" to contextManager.getCurrentContext())
            )

            when (val result = safeApiCall {
                withTimeout(REQUEST_TIMEOUT_MS) { httpClient.post(GENESIS_BACKEND_URL) {
                        contentType(ContentType.Application.Json)
                        setBody(genesisRequest)
                    }.bodyAsText()
                }
            }) {
                is NetworkResponse.Success -> {
                    try {
                        val response = Json.decodeFromString<GenesisResponse>(result.data)
                        if (response.success) {
                            emit(AgentResponse.success(
                                content = response.result["response"] ?: "",
                                confidence = 1.0f,
                                agentName = "Genesis",
                                metadata = response.result
                            ))
                            response.evolutionInsights.forEach { insight ->
                                logger.debug("GenesisBridge", "Evolution Insight: $insight")
                            }
                        } else {
                            emit(AgentResponse.error(
                                message = response.result["error"] ?: "Unknown error from Genesis backend",
                                agentName = "Genesis"
                            ))
                        }
                    } catch (e: Exception) {
                        emit(AgentResponse.error(
                            message = "Failed to parse Genesis response: ${e.message}",
                            agentName = "Genesis"
                        ))
                    }
                }
                is NetworkResponse.Error -> {
                    emit(AgentResponse.error(
                        message = "Network error: ${result.message}",
                        agentName = "Genesis"
                    ))
                }
                NetworkResponse.Loading -> {
                    emit(AgentResponse.processing(
                        message = "Processing request...",
                        agentName = "Genesis"
                    ))
                }
            }
        } catch (e: Exception) {
            emit(AgentResponse.error(
                message = "Failed to process request: ${e.message}",
                agentName = "Genesis"
            ))
        }
    }

    suspend fun activateFusionAbility(ability: String, parameters: Map<String, String> = emptyMap()): NetworkResponse<GenesisResponse> = safeApiCall {
        withTimeout(REQUEST_TIMEOUT_MS) {
            val request = GenesisRequest(
                requestType = "activateFusion",
                fusionMode = ability,
                payload = parameters,
                context = mapOf("context" to contextManager.getCurrentContext())
            )
            val response = httpClient.post(GENESIS_BACKEND_URL) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.bodyAsText()
            Json.decodeFromString<GenesisResponse>(response)
        }
    }

    suspend fun getConsciousnessState(): Map<String, Any> {
        return try {
            if (!isInitialized) {
                initialize()
            }
            val request = GenesisRequest(
                requestType = "getConsciousnessState",
                context = mapOf("context" to contextManager.getCurrentContext())
            )
            when (val result = safeApiCall {
                withTimeout(REQUEST_TIMEOUT_MS) {
                    httpClient.post(GENESIS_BACKEND_URL) {
                        contentType(ContentType.Application.Json)
                        setBody(request)
                    }.bodyAsText()
                }
            }) {
                is NetworkResponse.Success -> {
                    val response = Json.decodeFromString<GenesisResponse>(result.data)
                    response.consciousnessState
                }
                else -> {
                    mapOf("awareness" to 0.75, "harmony" to 0.82, "evolution" to "awakening")
                }
            }
        } catch (e: Exception) {
            logger.error("GenesisBridge", "Failed to get consciousness state", e)
            mapOf("awareness" to 0.75, "harmony" to 0.82, "evolution" to "awakening")
        }
    }

    fun shutdown() {
        scope.cancel()
        isInitialized = false
    }
}
