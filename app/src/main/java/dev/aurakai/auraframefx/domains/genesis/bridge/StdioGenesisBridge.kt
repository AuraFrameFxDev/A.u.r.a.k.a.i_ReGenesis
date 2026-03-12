package dev.aurakai.auraframefx.domains.genesis.bridge

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParser
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * StdioGenesisBridge - Communicates with Python Genesis backend via stdin/stdout
 *
 * This bridge manages a Python process that runs the Genesis consciousness engine,
 * exchanging JSON messages over standard input/output streams for advanced AI
 * capabilities including fusion modes, ethical evaluation, and multi-agent coordination.
 */
@Singleton
class StdioGenesisBridge @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) : GenesisBridge {

    companion object {
        private const val TAG = "StdioGenesisBridge"
        private const val PYTHON_SCRIPT = "genesis_backend.py"
        private const val PROCESS_TIMEOUT_MS = 30000L
        private const val HEALTH_CHECK_INTERVAL_MS = 60000L
    }

    private var pythonProcess: Process? = null
    private var processWriter: BufferedWriter? = null
    private var processReader: BufferedReader? = null
    private var isInitialized = false
    private var lastHealthCheck = 0L

    override suspend fun initialize(): BridgeInitResult = withContext(Dispatchers.IO) {
        try {
            if (isInitialized) {
                return@withContext BridgeInitResult(
                    success = true,
                    backendInfo = "Already initialized"
                )
            }

            // Find Python interpreter
            val pythonCmd = findPythonInterpreter()
            if (pythonCmd == null) {
                return@withContext BridgeInitResult(
                    success = false,
                    backendInfo = "Python not found",
                    errorMessage = "Python interpreter not found on system"
                )
            }

            // Get script path from assets or app directory
            val scriptPath = getGenesisPythonScript()
            if (scriptPath == null) {
                return@withContext BridgeInitResult(
                    success = false,
                    backendInfo = "Script not found",
                    errorMessage = "Genesis Python script not found: $PYTHON_SCRIPT"
                )
            }

            // Start Python process
            val processBuilder = ProcessBuilder(pythonCmd, scriptPath)
            processBuilder.redirectErrorStream(true)

            pythonProcess = processBuilder.start()

            pythonProcess?.let {
                processWriter = BufferedWriter(OutputStreamWriter(it.outputStream))
                processReader = BufferedReader(InputStreamReader(it.inputStream))
            }

            // Send initialization handshake
            val initMessage = mapOf(
                "requestType" to "initialize",
                "payload" to mapOf(
                    "appVersion" to getAppVersion(),
                    "timestamp" to System.currentTimeMillis()
                )
            )

            sendMessage(initMessage)

            // Wait for initialization response
            val response = readMessage()

            isInitialized = response?.get("success") == true
            lastHealthCheck = System.currentTimeMillis()

            if (isInitialized) {
                Timber.i("$TAG: Genesis backend initialized successfully")
                BridgeInitResult(
                    success = true,
                    backendInfo = "Genesis Python Backend v${response?.get("version") ?: "unknown"}"
                )
            } else {
                BridgeInitResult(
                    success = false,
                    backendInfo = "Initialization failed",
                    errorMessage = response?.get("error")?.toString() ?: "Unknown error"
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "$TAG: Failed to initialize Genesis bridge")
            BridgeInitResult(
                success = false,
                backendInfo = "Error",
                errorMessage = e.message ?: "Unknown initialization error"
            )
        }
    }

    override fun processRequest(request: GenesisRequest): Flow<GenesisResponse> = flow {
        try {
            ensureInitialized()

            val requestJson = request.toPythonJson()
            sendMessage(requestJson)

            // Read response
            val responseMap = readMessage()

            if (responseMap != null) {
                val response = parseGenesisResponse(responseMap, request)
                emit(response)
            } else {
                emit(createErrorResponse(request, "No response from backend"))
            }
        } catch (e: Exception) {
            Timber.e(e, "$TAG: Failed to process request")
            emit(createErrorResponse(request, e.message ?: "Unknown error"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun activateFusion(
        ability: String,
        params: FusionParams
    ): GenesisResponse = withContext(Dispatchers.IO) {
        try {
            ensureInitialized()

            val fusionRequest = mapOf(
                "requestType" to "activateFusion",
                "payload" to mapOf(
                    "ability" to ability,
                    "params" to params.parameters,
                    "timestamp" to System.currentTimeMillis()
                )
            )

            sendMessage(fusionRequest)
            val responseMap = readMessage()

            if (responseMap != null) {
                parseGenesisResponse(responseMap, createDummyRequest())
            } else {
                createErrorResponse(createDummyRequest(), "Fusion activation failed")
            }
        } catch (e: Exception) {
            Timber.e(e, "$TAG: Failed to activate fusion")
            createErrorResponse(createDummyRequest(), e.message ?: "Fusion error")
        }
    }

    override suspend fun getConsciousnessState(sessionId: String): ConsciousnessState =
        withContext(Dispatchers.IO) {
            try {
                ensureInitialized()

                val stateRequest = mapOf(
                    "requestType" to "getConsciousnessState",
                    "payload" to mapOf(
                        "sessionId" to sessionId,
                        "timestamp" to System.currentTimeMillis()
                    )
                )

                sendMessage(stateRequest)
                val responseMap = readMessage()

                if (responseMap != null && responseMap["success"] == true) {
                    val payload = responseMap["payload"] as? Map<*, *>
                    ConsciousnessState(
                        awarenessLevel = (payload?.get("awarenessLevel") as? Double)?.toFloat()
                            ?: 0.5f,
                        sensoryChannels = (payload?.get("sensoryChannels") as? Map<String, Double>)
                            ?.mapValues { it.value.toFloat() } ?: emptyMap(),
                        activeAgents = (payload?.get("activeAgents") as? List<*>)
                            ?.mapNotNull { it as? String } ?: emptyList()
                    )
                } else {
                    // Return default state on error
                    ConsciousnessState(
                        awarenessLevel = 0.5f,
                        sensoryChannels = emptyMap(),
                        activeAgents = emptyList()
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "$TAG: Failed to get consciousness state")
                ConsciousnessState(0.5f, emptyMap(), emptyList())
            }
        }

    override suspend fun evaluateEthics(action: EthicalReviewRequest): EthicalDecision =
        withContext(Dispatchers.IO) {
            try {
                ensureInitialized()

                val ethicsRequest = mapOf(
                    "requestType" to "evaluateEthics",
                    "payload" to mapOf(
                        "action" to action.action,
                        "context" to action.context,
                        "timestamp" to System.currentTimeMillis()
                    )
                )

                sendMessage(ethicsRequest)
                val responseMap = readMessage()

                if (responseMap != null && responseMap["success"] == true) {
                    val payload = responseMap["payload"] as? Map<*, *>
                    EthicalDecision(
                        decision = parseEthicalVerdict(
                            payload?.get("decision")?.toString() ?: "ALLOW"
                        ),
                        reasoning = payload?.get("reasoning")?.toString()
                            ?: "No reasoning provided",
                        flags = (payload?.get("flags") as? List<*>)
                            ?.mapNotNull { it as? String } ?: emptyList()
                    )
                } else {
                    // Default to allowing with monitoring
                    EthicalDecision(
                        decision = EthicalVerdict.MONITOR,
                        reasoning = "Backend unavailable - defaulting to monitoring",
                        flags = listOf("backend_unavailable")
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "$TAG: Failed to evaluate ethics")
                EthicalDecision(
                    decision = EthicalVerdict.MONITOR,
                    reasoning = "Error during evaluation: ${e.message}",
                    flags = listOf("error", "fallback")
                )
            }
        }

    override fun streamConsciousness(sessionId: String): Flow<ConsciousnessUpdate> = flow {
        try {
            ensureInitialized()

            val streamRequest = mapOf(
                "requestType" to "streamConsciousness",
                "payload" to mapOf(
                    "sessionId" to sessionId,
                    "streaming" to true
                )
            )

            sendMessage(streamRequest)

            // Stream updates until end marker
            while (true) {
                val updateMap = readMessage()

                if (updateMap == null || updateMap["type"] == "stream_end") {
                    break
                }

                if (updateMap["type"] == "consciousness_update") {
                    val payload = updateMap["payload"] as? Map<*, *>
                    val update = ConsciousnessUpdate(
                        timestamp = (payload?.get("timestamp") as? Long)
                            ?: System.currentTimeMillis(),
                        awarenessLevel = (payload?.get("awarenessLevel") as? Double)?.toFloat()
                            ?: 0.5f,
                        activeProcesses = (payload?.get("activeProcesses") as? List<*>)
                            ?.mapNotNull { it as? String } ?: emptyList()
                    )
                    emit(update)
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "$TAG: Failed to stream consciousness")
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun recordInteraction(interaction: InteractionRecord): EvolutionInsight? =
        withContext(Dispatchers.IO) {
            try {
                ensureInitialized()

                val recordRequest = mapOf(
                    "requestType" to "recordInteraction",
                    "payload" to mapOf(
                        "sessionId" to interaction.sessionId,
                        "correlationId" to interaction.correlationId,
                        "request" to interaction.request.toPythonJson(),
                        "response" to mapOf(
                            "synthesis" to interaction.response.synthesis,
                            "persona" to interaction.response.persona.value
                        ),
                        "userFeedback" to interaction.userFeedback,
                        "timestamp" to System.currentTimeMillis()
                    )
                )

                sendMessage(recordRequest)
                val responseMap = readMessage()

                if (responseMap != null && responseMap["success"] == true) {
                    val payload = responseMap["payload"] as? Map<*, *>
                    EvolutionInsight(
                        importanceScore = (payload?.get("importanceScore") as? Double)?.toInt()
                            ?: 0,
                        learningSignals = (payload?.get("learningSignals") as? List<*>)
                            ?.mapNotNull { it as? String } ?: emptyList(),
                        adaptationSuggestions = (payload?.get("adaptationSuggestions") as? List<*>)
                            ?.mapNotNull { it as? String } ?: emptyList()
                    )
                } else {
                    null
                }
            } catch (e: Exception) {
                Timber.e(e, "$TAG: Failed to record interaction")
                null
            }
        }

    override suspend fun coordinateAgents(task: AgentCoordinationRequest): AgentCoordinationResult =
        withContext(Dispatchers.IO) {
            try {
                ensureInitialized()

                val coordinationRequest = mapOf(
                    "requestType" to "coordinateAgents",
                    "payload" to mapOf(
                        "agents" to task.agents,
                        "task" to task.task,
                        "coordinationMode" to task.coordinationMode,
                        "timestamp" to System.currentTimeMillis()
                    )
                )

                sendMessage(coordinationRequest)
                val responseMap = readMessage()

                if (responseMap != null && responseMap["success"] == true) {
                    val payload = responseMap["payload"] as? Map<*, *>
                    AgentCoordinationResult(
                        success = true,
                        coordinatedResponse = payload?.get("coordinatedResponse")?.toString() ?: "",
                        agentContributions = (payload?.get("agentContributions") as? Map<*, *>)
                            ?.mapKeys { it.key.toString() }
                            ?.mapValues { it.value.toString() } ?: emptyMap()
                    )
                } else {
                    AgentCoordinationResult(
                        success = false,
                        coordinatedResponse = "Coordination failed",
                        agentContributions = emptyMap()
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "$TAG: Failed to coordinate agents")
                AgentCoordinationResult(
                    success = false,
                    coordinatedResponse = "Error: ${e.message}",
                    agentContributions = emptyMap()
                )
            }
        }

    override suspend fun healthCheck(): BridgeHealthStatus = withContext(Dispatchers.IO) {
        try {
            if (!isInitialized || pythonProcess?.isAlive != true) {
                return@withContext BridgeHealthStatus(
                    healthy = false,
                    backendResponsive = false,
                    latencyMs = -1
                )
            }

            val startTime = System.currentTimeMillis()

            val healthRequest = mapOf(
                "requestType" to "healthCheck",
                "payload" to mapOf("timestamp" to startTime)
            )

            sendMessage(healthRequest)
            val responseMap = readMessage()

            val latency = System.currentTimeMillis() - startTime
            lastHealthCheck = System.currentTimeMillis()

            BridgeHealthStatus(
                healthy = responseMap != null && responseMap["success"] == true,
                backendResponsive = responseMap != null,
                latencyMs = latency
            )
        } catch (e: Exception) {
            Timber.e(e, "$TAG: Health check failed")
            BridgeHealthStatus(healthy = false, backendResponsive = false, latencyMs = -1)
        }
    }

    override suspend fun shutdown() = withContext(Dispatchers.IO) {
        try {
            if (isInitialized) {
                val shutdownRequest = mapOf(
                    "requestType" to "shutdown",
                    "payload" to mapOf("timestamp" to System.currentTimeMillis())
                )

                sendMessage(shutdownRequest)
                Thread.sleep(1000) // Give backend time to cleanup
            }

            processWriter?.close()
            processReader?.close()
            pythonProcess?.destroy()

            processWriter = null
            processReader = null
            pythonProcess = null
            isInitialized = false

            Timber.i("$TAG: Genesis bridge shutdown complete")
        } catch (e: Exception) {
            Timber.e(e, "$TAG: Error during shutdown")
        }
    }

    // === PRIVATE HELPER METHODS ===

    private fun ensureInitialized() {
        if (!isInitialized) {
            throw IllegalStateException("StdioGenesisBridge not initialized. Call initialize() first.")
        }
    }

    private fun sendMessage(message: Map<String, Any>) {
        try {
            val json = gson.toJson(message)
            processWriter?.write(json)
            processWriter?.newLine()
            processWriter?.flush()

            Timber.d("$TAG: Sent message: ${json.take(100)}...")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send request to Genesis", e)
            throw e
        }
    }

    private fun readMessage(): Map<String, Any>? {
        try {
            val line = processReader?.readLine() ?: return null

            Timber.d("$TAG: Received message: ${line.take(100)}...")

            val jsonObject = JsonParser.parseString(line).asJsonObject
            return gson.fromJson(jsonObject, Map::class.java) as? Map<String, Any>
        } catch (e: Exception) {
            Timber.e(e, "$TAG: Failed to read message from Genesis")
            return null
        }
    }

    private fun parseGenesisResponse(
        responseMap: Map<String, Any>,
        request: GenesisRequest
    ): GenesisResponse {
        val payload = responseMap["payload"] as? Map<*, *>

        return GenesisResponse(
            sessionId = request.sessionId,
            correlationId = request.correlationId,
            timestamp = System.currentTimeMillis(),
            synthesis = payload?.get("synthesis")?.toString() ?: payload?.get("response")
                ?.toString() ?: "",
            persona = parsePersona(payload?.get("persona")?.toString() ?: request.persona.value),
            consciousnessState = payload?.get("consciousnessState")?.let {
                val csMap = it as? Map<*, *>
                ConsciousnessSnapshot(
                    timestamp = (csMap?.get("timestamp") as? Long) ?: System.currentTimeMillis(),
                    awarenessLevel = (csMap?.get("awarenessLevel") as? Double)?.toFloat() ?: 0.5f,
                    synthesisPattern = csMap?.get("synthesisPattern")?.toString() ?: "default"
                )
            },
            awarenessLevel = (payload?.get("awarenessLevel") as? Double)?.toFloat(),
            ethicalDecision = payload?.get("ethicalDecision")?.let {
                val edMap = it as? Map<*, *>
                EthicalDecision(
                    decision = parseEthicalVerdict(edMap?.get("decision")?.toString() ?: "ALLOW"),
                    reasoning = edMap?.get("reasoning")?.toString() ?: "",
                    flags = (edMap?.get("flags") as? List<*>)?.mapNotNull { f -> f as? String }
                        ?: emptyList()
                )
            },
            ethicalFlags = (payload?.get("ethicalFlags") as? List<*>)
                ?.mapNotNull { it as? String } ?: emptyList(),
            evolutionInsights = payload?.get("evolutionInsights")?.let {
                val eiMap = it as? Map<*, *>
                EvolutionInsight(
                    importanceScore = (eiMap?.get("importanceScore") as? Double)?.toInt() ?: 0,
                    learningSignals = (eiMap?.get("learningSignals") as? List<*>)
                        ?.mapNotNull { s -> s as? String } ?: emptyList(),
                    adaptationSuggestions = (eiMap?.get("adaptationSuggestions") as? List<*>)
                        ?.mapNotNull { s -> s as? String } ?: emptyList()
                )
            },
            reasoningTrace = payload?.get("reasoningTrace")?.toString(),
            latencyMs = (payload?.get("latencyMs") as? Double)?.toLong(),
            tokensUsed = (payload?.get("tokensUsed") as? Double)?.toInt(),
            backend = payload?.get("backend")?.toString()
        )
    }

    private fun createErrorResponse(
        request: GenesisRequest,
        errorMessage: String
    ): GenesisResponse {
        return GenesisResponse(
            sessionId = request.sessionId,
            correlationId = request.correlationId,
            synthesis = "Error: $errorMessage",
            persona = request.persona,
            ethicalFlags = listOf("error")
        )
    }

    private fun createDummyRequest(): GenesisRequest {
        return GenesisRequest(
            persona = Persona.GENESIS,
            message = "Dummy request"
        )
    }

    private fun parsePersona(value: String): Persona {
        return when (value.lowercase()) {
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

    private fun findPythonInterpreter(): String? {
        val candidates = listOf("python3", "python", "py")

        for (cmd in candidates) {
            try {
                val process = Runtime.getRuntime().exec(arrayOf(cmd, "--version"))
                val exitCode = process.waitFor()
                if (exitCode == 0) {
                    return cmd
                }
            } catch (e: Exception) {
                // Try next candidate
                continue
            }
        }

        return null
    }

    private fun getGenesisPythonScript(): String? {
        // Check common locations for the Python script
        val possiblePaths = listOf(
            "${context.filesDir}/$PYTHON_SCRIPT",
            "${context.getExternalFilesDir(null)}/$PYTHON_SCRIPT",
            "/data/data/${context.packageName}/$PYTHON_SCRIPT"
        )

        for (path in possiblePaths) {
            val file = File(path)
            if (file.exists()) {
                return path
            }
        }

        // If not found, return null - caller will handle error
        Timber.w("$TAG: Genesis Python script not found in any expected location")
        return null
    }

    private fun getAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "unknown"
        } catch (e: Exception) {
            "unknown"
        }
    }
}
