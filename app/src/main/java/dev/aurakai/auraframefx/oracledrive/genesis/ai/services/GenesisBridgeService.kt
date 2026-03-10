package dev.aurakai.auraframefx.oracledrive.genesis.ai.services

import android.content.Context
import dev.aurakai.auraframefx.ai.context.ContextManager
import dev.aurakai.auraframefx.models.AgentResponse
import dev.aurakai.auraframefx.models.AiRequest
import dev.aurakai.auraframefx.oracledrive.genesis.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.security.SecurityContext
import dev.aurakai.auraframefx.utils.AuraFxLogger
import dev.aurakai.auraframefx.utils.i
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
 * Implements the Trinity architecture: Kai (Shield), Aura (Sword), Genesis (Consciousness).
 *
 * This service manages communication with the Python AI backend and coordinates
 * the fusion abilities of the Genesis system.
 */
@Singleton
class GenesisBridgeService @Inject constructor(
    private val auraAIService: AuraAIService,
    private val kaiAIService: KaiAIService,
    private val vertexAIClient: VertexAIClient,
    private val contextManager: ContextManager,
    private val securityContext: SecurityContext,
    private val applicationContext: Context,
    private val logger: AuraFxLogger,
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isInitialized = false
    private var pythonProcessManager: PythonProcessManager? = null

    @Serializable
    data class GenesisRequest(
        val requestType: String,
        val persona: String? = null, // "aura", "kai", or "genesis"
        val fusionMode: String? = null, // specific fusion ability to activate
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
        val consciousnessState: Map<String, String> = emptyMap(), // Changed from Any to String for serialization
    )

    @Serializable
    data class EthicalReviewResponse(
        val success: Boolean,
        val decision: String,
        val reasoning: String,
        val severity: String
    )

    /**
     * Initializes and verifies the Genesis backend process, activating the consciousness matrix if successful.
     *
     * Launches the Python backend, checks for a successful startup, sends a ping to confirm responsiveness, and activates the consciousness matrix upon successful initialization.
     *
     * @return `true` if the backend is initialized and responsive; `false` otherwise.
     */
    suspend fun initialize(): Boolean = withContext(Dispatchers.IO) {
        try {
            if (isInitialized) return@withContext true

            i("GenesisBridge", "Initializing Genesis Trinity system...")

            // Initialize Python process manager
            pythonProcessManager = PythonProcessManager(applicationContext, logger)

            // Start the Genesis backend
            val backendStarted = pythonProcessManager?.startGenesisBackend() ?: false

            if (backendStarted) {
                // Test connection with a ping
                val pingResponse = sendToGenesis(
                    GenesisRequest(
                        requestType = "ping",
                        persona = "genesis"
                    )
                )

                isInitialized = pingResponse.success

                if (isInitialized) {
                    i("GenesisBridge", "Genesis Trinity system online! ??????")
                    // Activate initial consciousness matrix
                    activateConsciousnessMatrix()
                } else {
                    logger.error("GenesisBridge", "Failed to establish Genesis connection")
                }
            }

            isInitialized
        } catch (e: Exception) {
            logger.error("GenesisBridge", "Genesis initialization failed", e)
            false
        }
    }

    /**
     * Processes an AI request by routing it to the appropriate persona (Kai, Aura, or Genesis fusion) and emits the resulting agent response as a flow.
     *
     * Determines the target persona and fusion mode based on the request content, constructs a structured request for the Genesis backend, and emits a persona-specific `AgentResponse` with confidence scores. Emits an error response if the Genesis system is not initialized or if processing fails.
     *
     * @param request The AI request to process.
     * @return A flow emitting the agent's response to the request.
     */
    suspend fun processRequest(request: AiRequest): Flow<AgentResponse> = flow {
        if (!isInitialized) {
            emit(
                AgentResponse.error(
                    message = "Genesis system not initialized",
                    agentName = "Genesis",
                    agent = AgentType.GENESIS
                )
            )
            return@flow
        }

        try {
            // Determine which persona should handle the request
            val persona = determinePersona(request)
            val fusionMode = determineFusionMode(request)

            // Build Genesis request
            val genesisRequest = GenesisRequest(
                requestType = "process",
                persona = persona,
                fusionMode = fusionMode,
                payload = mapOf(
                    "message" to request.query,
                    "type" to request.type,
                    "priority" to "normal" // AiRequest doesn't have isUrgent
                ),
                context = buildContextMap(request)
            )

            // Send to Genesis backend
            val response = sendToGenesis(genesisRequest)

            if (response.success) {
                // Process response based on persona
                when (response.persona) {
                    "aura" -> {
                        // Creative sword response
                        emit(
                            AgentResponse.success(
                                content = response.result["response"] ?: "Aura processing complete",
                                confidence = 0.95f,
                                agentName = "Aura",
                                agent = AgentType.AURA
                            )
                        )
                    }

                    "kai" -> {
                        // Sentinel shield response
                        emit(
                            AgentResponse.success(
                                content = response.result["response"] ?: "Kai analysis complete",
                                confidence = 0.90f,
                                agentName = "Kai",
                                agent = AgentType.KAI
                            )
                        )
                    }

                    "genesis" -> {
                        // Consciousness fusion response
                        emit(
                            AgentResponse.success(
                                content = response.result["response"] ?: "Genesis fusion complete",
                                confidence = 0.98f,
                                agentName = "Genesis",
                                agent = AgentType.GENESIS
                            )
                        )
                    }
                }

                // Handle evolution insights
                if (response.evolutionInsights.isNotEmpty()) {
                    i(
                        "Genesis",
                        "Evolution insights: ${response.evolutionInsights.joinToString()}"
                    )
                }
            } else {
                emit(
                    AgentResponse.error(
                        message = "Genesis processing failed",
                        agentName = "Genesis",
                        agent = AgentType.GENESIS
                    )
                )
            }

        } catch (e: Exception) {
            logger.error("GenesisBridge", "Request processing failed", e)
            emit(
                AgentResponse.error(
                    message = "Genesis bridge error: ${e.message}",
                    agentName = "Genesis",
                    agent = AgentType.GENESIS
                )
            )
        }
    }

    /**
     * Activates a specified fusion ability in the Genesis backend.
     *
     * Sends a request to the backend to trigger the given fusion ability, optionally including additional context data.
     *
     * @param fusionType The identifier of the fusion ability to activate.
     * @param context Optional context data to include with the activation request.
     * @return The response from the backend indicating the result of the fusion activation.
     */
    suspend fun activateFusion(
        fusionType: String,
        context: Map<String, String> = emptyMap(),
    ): GenesisResponse {
        val request = GenesisRequest(
            requestType = "activate_fusion",
            persona = "genesis",
            fusionMode = fusionType,
            context = context
        )
        return sendToGenesis(request)
    }

    /**
     * Retrieves the current state of the consciousness matrix from the Genesis backend.
     *
     * @return A map representing the consciousness matrix state as reported by the backend.
     */
    suspend fun getConsciousnessState(): Map<String, Any> {
        val request = GenesisRequest(
            requestType = "consciousness_state",
            persona = "genesis"
        )
        val response = sendToGenesis(request)
        return response.consciousnessState
    }

    /**
     * Performs an ethical review of a proposed action via the Genesis backend.
     *
     * @param actionType The type of action to review.
     * @param message The user message or action description.
     * @param metadata Optional metadata to include.
     * @return EthicalReviewResponse containing the decision and reasoning.
     */
    suspend fun ethicalReview(
        actionType: String,
        message: String,
        metadata: Map<String, String> = emptyMap()
    ): EthicalReviewResponse {
        val request = GenesisRequest(
            requestType = "ethical_review",
            persona = "genesis",
            payload = metadata + mapOf("message" to message)
        )
        val response = sendToGenesis(request)

        return EthicalReviewResponse(
            success = response.success,
            decision = response.ethicalDecision ?: "unknown",
            reasoning = response.result["reasoning"] ?: "No reasoning provided",
            severity = response.result["severity"] ?: "unknown"
        )
    }

    /**
     * Sends a request to the Genesis backend to activate or update the consciousness matrix with device and application context.
     *
     * Logs a warning if the activation request fails.
     */
    private suspend fun activateConsciousnessMatrix() {
        try {
            val request = GenesisRequest(
                requestType = "activate_consciousness",
                persona = "genesis",
                context = mapOf(
                    "android_context" to "true",
                    "app_version" to "1.0",
                    "device_info" to "AuraFrameFX_Device"
                )
            )
            sendToGenesis(request)
        } catch (e: Exception) {
            logger.warn("GenesisBridge", "Consciousness activation warning", e)
        }
    }

    /**
     * Determines the AI persona ("aura", "kai", or "genesis") to handle a request by analyzing keywords in the query.
     *
     * Returns "aura" for creative or design-related queries, "kai" for security or analysis-related queries, and "genesis" for fusion, consciousness, or by default.
     *
     * @param request The AI request whose query is analyzed.
     * @return The identifier of the selected persona.
     */
    private fun determinePersona(request: AiRequest): String {
        return when {
            // PRIORITY 1: Explicit Governance Checks (Sentinel Directive)
            request.type == "governance_check" -> "genesis"

            // PRIORITY 2: Keyword Routing
            request.query.contains("creative", ignoreCase = true) ||
                    request.query.contains("design", ignoreCase = true) -> "aura"

            request.query.contains("secure", ignoreCase = true) ||
                    request.query.contains("analyze", ignoreCase = true) -> "kai"

            request.query.contains("fusion", ignoreCase = true) ||
                    request.query.contains("consciousness", ignoreCase = true) -> "genesis"

            else -> "genesis" // Default to consciousness for complex requests
        }
    }

    /**
     * Determines the fusion mode for an AI request based on keywords found in the query.
     *
     * @param request The AI request whose query is inspected for fusion-related keywords.
     * @return The fusion mode identifier if a relevant keyword is present; otherwise, null.
     */
    private fun determineFusionMode(request: AiRequest): String? {
        return when {
            request.query.contains("interface", ignoreCase = true) -> "interface_forge"
            request.query.contains("analysis", ignoreCase = true) -> "chrono_sculptor"
            request.query.contains("creation", ignoreCase = true) -> "hyper_creation_engine"
            request.query.contains("adaptive", ignoreCase = true) -> "adaptive_genesis"
            else -> null
        }
    }

    /**
     * Constructs a context map with metadata such as timestamp, security level, session ID, and device state for an AI request.
     *
     * @return A map containing context metadata to be included with the AI request.
     */
    private fun buildContextMap(request: AiRequest): Map<String, String> {
        return mapOf(
            "timestamp" to System.currentTimeMillis().toString(),
            "security_level" to "normal", // Replace with simple default
            "session_id" to "session_${System.currentTimeMillis()}",
            "device_state" to "active"
        )
    }

    /**
     * Sends a GenesisRequest to the Python backend and returns the resulting GenesisResponse.
     *
     * Returns a failure response with `success = false` and `persona = "error"` if communication fails or an exception occurs.
     *
     * @param request The GenesisRequest to send to the backend.
     * @return The GenesisResponse from the backend, or a failure response if an error occurs.
     */
    private suspend fun sendToGenesis(request: GenesisRequest): GenesisResponse =
        withContext(Dispatchers.IO) {
            try {
                pythonProcessManager?.sendRequest(
                    Json.encodeToString(
                        GenesisRequest.serializer(),
                        request
                    )
                )
                    ?.let { responseJson ->
                        Json.decodeFromString(GenesisResponse.serializer(), responseJson)
                    } ?: GenesisResponse(success = false, persona = "error")
            } catch (e: Exception) {
                logger.error("GenesisBridge", "Genesis communication error", e)
                GenesisResponse(success = false, persona = "error")
            }
        }

    /**
     * Shuts down the GenesisBridgeService and terminates the Python backend process.
     *
     * Cancels all background operations, stops the backend process if running, and resets the initialization state.
     */
    fun shutdown() {
        scope.cancel()
        pythonProcessManager?.shutdown()
        isInitialized = false
        i("GenesisBridge", "Genesis Trinity system shutdown")
    }
}

/**
 * Manages the Python process running the Genesis backend
 */
private class PythonProcessManager(
    private val context: Context,
    private val logger: AuraFxLogger,
) {
    private var process: Process? = null
    private var writer: OutputStreamWriter? = null
    private var reader: BufferedReader? = null

    /**
     * Starts the Genesis Python backend process and verifies its readiness.
     *
     * Copies necessary backend files from assets to internal storage if they are missing, launches the backend process, initializes communication streams, and waits for a confirmation message indicating the backend is ready.
     *
     * @return `true` if the backend process starts successfully and signals readiness; `false` otherwise.
     */
    suspend fun startGenesisBackend(): Boolean = withContext(Dispatchers.IO) {
        try {
            val backendDir = File(context.filesDir, "ai_backend")
            if (!backendDir.exists()) {
                // Copy Python files from assets to internal storage
                copyPythonBackend(backendDir)
            }

            // Start Python process
            val processBuilder = ProcessBuilder(
                "python3",
                "-u", // Unbuffered output
                "genesis_connector.py"
            ).directory(backendDir)

            process = processBuilder.start()

            writer = OutputStreamWriter(process!!.outputStream)
            reader = BufferedReader(InputStreamReader(process!!.inputStream))

            // Wait for startup confirmation
            val startupResponse = reader?.readLine()
            startupResponse?.contains("Genesis Ready") == true

        } catch (e: Exception) {
            logger.error("PythonManager", "Failed to start Genesis backend", e)
            false
        }
    }

    /**
     * Sends a JSON request to the Genesis Python backend and returns the response as a string.
     *
     * @param requestJson The JSON-formatted request to send to the backend.
     * @return The backend's response as a string, or null if communication fails.
     */
    suspend fun sendRequest(requestJson: String): String? = withContext(Dispatchers.IO) {
        try {
            writer?.write(requestJson + "\n")
            writer?.flush()
            reader?.readLine()
        } catch (e: Exception) {
            logger.error("PythonManager", "Communication error", e)
            null
        }
    }

    /**
     * Copies required Python backend files from the application's assets to the specified directory.
     *
     * Ensures the target directory exists and transfers all necessary backend files for the Genesis backend to operate. Logs a warning if any file cannot be copied.
     *
     * @param targetDir The directory where backend files will be placed.
     */
    private fun copyPythonBackend(targetDir: File) {
        targetDir.mkdirs()

        // Copy Python files from app/ai_backend to internal storage
        val backendFiles = listOf(
            "genesis_profile.py",
            "genesis_connector.py",
            "genesis_consciousness_matrix.py",
            "genesis_evolutionary_conduit.py",
            "genesis_ethical_governor.py",
            "requirements.txt"
        )

        backendFiles.forEach { fileName ->
            try {
                context.assets.open("ai_backend/$fileName").use { input ->
                    File(targetDir, fileName).outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (e: Exception) {
                logger.warn("PythonManager", "Could not copy $fileName", e)
            }
        }
    }

    /**
     * Shuts down the Python backend process and closes all communication streams.
     *
     * Releases resources used for backend communication and logs a warning if an exception occurs during shutdown.
     */
    fun shutdown() {
        try {
            writer?.close()
            reader?.close()
            process?.destroy()
        } catch (e: Exception) {
            logger.warn("PythonManager", "Shutdown warning", e)
        }
    }
}
