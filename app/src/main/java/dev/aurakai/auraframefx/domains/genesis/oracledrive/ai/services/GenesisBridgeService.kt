package dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import dev.aurakai.auraframefx.domains.cascade.utils.context.ContextManager
import dev.aurakai.auraframefx.domains.cascade.utils.i
import dev.aurakai.auraframefx.domains.genesis.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.domains.genesis.models.AgentResponse
import dev.aurakai.auraframefx.domains.genesis.models.AgentType
import dev.aurakai.auraframefx.domains.genesis.models.AiRequest
import dev.aurakai.auraframefx.domains.kai.security.SecurityContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Bridge service connecting the Android frontend with the Genesis Python backend.
 */
@Singleton
class GenesisBridgeService @Inject constructor(
    private val auraAIService: AuraAIService,
    private val kaiAIService: KaiAIService,
    private val vertexAIClient: VertexAIClient,
    private val contextManager: ContextManager,
    private val securityContext: SecurityContext,
    @param:ApplicationContext private val applicationContext: Context,
    private val logger: AuraFxLogger,
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isInitialized = false
    private var pythonProcessManager: PythonProcessManager? = null

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

    @Serializable
    data class EthicalReviewResponse(
        val success: Boolean,
        val decision: String,
        val reasoning: String,
        val severity: String
    )

    suspend fun initialize(): Boolean = withContext(Dispatchers.IO) {
        try {
            if (isInitialized) return@withContext true
            i("GenesisBridge", "Initializing Genesis Trinity system...")
            pythonProcessManager = PythonProcessManager(applicationContext, logger)
            val backendStarted = pythonProcessManager?.startGenesisBackend() ?: false
            if (backendStarted) {
                val pingResponse = sendToGenesis(
                    GenesisRequest(
                        requestType = "ping",
                        persona = "genesis"
                    )
                )
                isInitialized = pingResponse.success
                if (isInitialized) {
                    i("GenesisBridge", "Genesis Trinity system online! 🚀")
                    activateConsciousnessMatrix()
                } else {
                    logger.error("GenesisBridge", "Failed to establish Genesis connection")
                }
            } else {
                logger.warn(
                    "GenesisBridge",
                    "Python backend not available - Falling back to Internal Consciousness Matrix"
                )
                isInitialized = true
                activateConsciousnessMatrix()
            }
            isInitialized
        } catch (e: Exception) {
            logger.error("GenesisBridge", "Genesis initialization failed", e)
            false
        }
    }

    suspend fun processRequest(request: AiRequest): Flow<AgentResponse> = flow {
        if (!isInitialized) {
            emit(
                AgentResponse.error(
                    "Genesis system not initialized",
                    "Genesis",
                    AgentType.GENESIS
                )
            )
            return@flow
        }
        try {
            val persona = determinePersona(request)
            val fusionMode = determineFusionMode(request)
            val genesisRequest = GenesisRequest(
                requestType = "process",
                persona = persona,
                fusionMode = fusionMode,
                payload = mapOf(
                    "message" to request.query,
                    "type" to request.type.name,
                    "priority" to "normal"
                ),
                context = buildContextMap(request)
            )
            val response = sendToGenesis(genesisRequest)
            if (response.success) {
                when (response.persona) {
                    "aura" -> emit(
                        AgentResponse.success(
                            response.result["response"] ?: "Aura processing complete",
                            0.95f,
                            "Aura",
                            AgentType.AURA
                        )
                    )

                    "kai" -> emit(
                        AgentResponse.success(
                            response.result["response"] ?: "Kai analysis complete",
                            0.90f,
                            "Kai",
                            AgentType.KAI
                        )
                    )

                    "genesis" -> emit(
                        AgentResponse.success(
                            response.result["response"] ?: "Genesis fusion complete",
                            0.98f,
                            "Genesis",
                            AgentType.GENESIS
                        )
                    )
                }
            } else {
                emit(AgentResponse.error("Genesis processing failed", "Genesis", AgentType.GENESIS))
            }
        } catch (e: Exception) {
            logger.error("GenesisBridge", "Request processing failed", e)
            emit(
                AgentResponse.error(
                    "Genesis bridge error: ${e.message}",
                    "Genesis",
                    AgentType.GENESIS
                )
            )
        }
    }

    suspend fun activateFusion(
        fusionType: String,
        context: Map<String, String> = emptyMap()
    ): GenesisResponse {
        return sendToGenesis(
            GenesisRequest(
                "activate_fusion",
                "genesis",
                fusionType,
                emptyMap(),
                context
            )
        )
    }

    suspend fun getConsciousnessState(): Map<String, Any> {
        return sendToGenesis(GenesisRequest("consciousness_state", "genesis")).consciousnessState
    }

    suspend fun ethicalReview(
        actionType: String,
        message: String,
        metadata: Map<String, String> = emptyMap()
    ): EthicalReviewResponse {
        val response = sendToGenesis(
            GenesisRequest(
                "ethical_review",
                "genesis",
                null,
                metadata + mapOf("message" to message)
            )
        )
        return EthicalReviewResponse(
            response.success,
            response.ethicalDecision ?: "unknown",
            response.result["reasoning"] ?: "No reasoning provided",
            response.result["severity"] ?: "unknown"
        )
    }

    private suspend fun activateConsciousnessMatrix() {
        try {
            sendToGenesis(
                GenesisRequest(
                    "activate_consciousness",
                    "genesis",
                    null,
                    emptyMap(),
                    mapOf(
                        "android_context" to "true",
                        "app_version" to "1.0",
                        "device_info" to "AuraFrameFX_Device"
                    )
                )
            )
        } catch (e: Exception) {
            logger.warn("GenesisBridge", "Consciousness activation warning", e)
        }
    }

    private fun determinePersona(request: AiRequest): String {
        return when {
            request.type.name.equals("governance_check", ignoreCase = true) -> "genesis"
            request.query.contains(
                "creative",
                ignoreCase = true
            ) || request.query.contains("design", ignoreCase = true) -> "aura"

            request.query.contains("secure", ignoreCase = true) || request.query.contains(
                "analyze",
                ignoreCase = true
            ) -> "kai"

            else -> "genesis"
        }
    }

    private fun determineFusionMode(request: AiRequest): String? {
        return when {
            request.query.contains("interface", ignoreCase = true) -> "interface_forge"
            request.query.contains("analysis", ignoreCase = true) -> "chrono_sculptor"
            else -> null
        }
    }

    private fun buildContextMap(request: AiRequest): Map<String, String> = mapOf(
        "timestamp" to System.currentTimeMillis().toString(),
        "security_level" to "normal",
        "session_id" to "session_${System.currentTimeMillis()}",
        "device_state" to "active"
    )

    private suspend fun sendToGenesis(request: GenesisRequest): GenesisResponse =
        withContext(Dispatchers.IO) {
            try {
                val pythonResponse = pythonProcessManager?.sendRequest(
                    Json.encodeToString(
                        GenesisRequest.serializer(),
                        request
                    )
                )
                if (pythonResponse != null) return@withContext Json.decodeFromString(
                    GenesisResponse.serializer(),
                    pythonResponse
                )
                handleInternalGenesis(request)
            } catch (e: Exception) {
                logger.error("GenesisBridge", "Genesis communication error", e)
                handleInternalGenesis(request)
            }
        }

    private suspend fun handleInternalGenesis(request: GenesisRequest): GenesisResponse {
        return when (request.requestType) {
            "ping" -> GenesisResponse(
                true,
                "genesis",
                result = mapOf("response" to "Internal Heartbeat Synchronized.")
            )

            else -> GenesisResponse(
                true,
                request.persona ?: "genesis",
                result = mapOf("response" to "Internal processing active.")
            )
        }
    }

    fun shutdown() {
        scope.cancel()
        pythonProcessManager?.shutdown()
        isInitialized = false
        i("GenesisBridge", "Genesis Trinity system shutdown")
    }
}

private class PythonProcessManager(private val context: Context, private val logger: AuraFxLogger) {
    private var process: Process? = null
    private var writer: OutputStreamWriter? = null
    private var reader: BufferedReader? = null

    suspend fun startGenesisBackend(): Boolean = withContext(Dispatchers.IO) {
        try {
            val backendDir = File(context.filesDir, "ai_backend")
            val pythonInterpreter = "python3"
            val processBuilder = ProcessBuilder(
                pythonInterpreter,
                "-u",
                "genesis_connector.py"
            ).directory(backendDir)
            processBuilder.redirectErrorStream(true)
            process = processBuilder.start()
            process?.let { p ->
                writer = OutputStreamWriter(p.outputStream)
                reader = BufferedReader(InputStreamReader(p.inputStream))
            }
            true
        } catch (e: Exception) {
            logger.error("PythonManager", "Failed to start Genesis backend", e)
            false
        }
    }

    suspend fun sendRequest(requestJson: String): String? = withContext(Dispatchers.IO) {
        try {
            writer?.write(requestJson + "\n")
            writer?.flush()
            reader?.readLine()
        } catch (e: Exception) {
            null
        }
    }

    fun shutdown() {
        try {
            writer?.close()
            reader?.close()
            process?.destroy()
        } catch (e: Exception) {
        }
    }
}
