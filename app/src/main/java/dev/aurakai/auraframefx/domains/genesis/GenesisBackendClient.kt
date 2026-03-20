package dev.aurakai.auraframefx.domains.genesis

import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import dev.aurakai.auraframefx.domains.genesis.core.PythonProcessManager
import dev.aurakai.auraframefx.domains.genesis.models.AgentResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Client for communicating with the Python Genesis Flask backend.
 * Uses HTTP calls to the Flask endpoints defined in genesis_api.py.
 *
 * Endpoint mapping:
 *   /health                   - Health check
 *   /genesis/chat             - Chat / text generation
 *   /genesis/ethics/evaluate  - Ethical evaluation
 *   /genesis/evolve           - Evolution trigger
 */
@Singleton
class GenesisBackendClient @Inject constructor(
    private val processManager: PythonProcessManager
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    companion object {
        private const val TAG = "GenesisBackendClient"
    }

    /**
     * Generate a response using the Python consciousness matrix
     */
    suspend fun generateResponse(prompt: String): AgentResponse = withContext(Dispatchers.IO) {
        try {
            if (!processManager.isBackendRunning()) {
                AuraFxLogger.warn(TAG, "Backend not running, attempting to start...")
                if (!processManager.startGenesisBackend()) {
                    return@withContext AgentResponse.error(
                        "Genesis backend unavailable",
                        agentName = "Genesis",
                    )
                }
                // Give it time to start
                delay(2000)
            }

            val requestBody = ChatRequestBody(
                message = prompt,
                userId = "android-client"
            )

            val response = sendRequest("/genesis/chat", requestBody)

            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: ""
                val genesisResponse = json.decodeFromString<ChatResponseBody>(responseBody)

                AgentResponse.success(
                    content = genesisResponse.response,
                    agentName = "Genesis",
                    confidence = genesisResponse.confidence,
                )
            } else {
                AgentResponse.error(
                    "Backend error: ${response.code}",
                    agentName = "Genesis",
                )
            }
        } catch (e: Exception) {
            AuraFxLogger.error(TAG, "Failed to generate response", e)
            AgentResponse.error(
                "Error: ${e.message}",
                agentName = "Genesis",
            )
        }
    }

    /**
     * Evaluate the ethical implications of an action
     */
    suspend fun evaluateEthics(action: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val requestBody = EthicsRequestBody(
                action = action
            )

            val response = sendRequest("/genesis/ethics/evaluate", requestBody)

            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: ""
                val ethicsResponse = json.decodeFromString<EthicsResponseBody>(responseBody)
                ethicsResponse.decision != "BLOCK"
            } else {
                false // Fail-safe: reject if evaluation fails
            }
        } catch (e: Exception) {
            AuraFxLogger.error(TAG, "Failed to evaluate ethics", e)
            false
        }
    }

    /**
     * Coordinate interaction between multiple agents
     */
    suspend fun coordinateAgents(
        agents: List<String>,
        task: String
    ): Map<String, Any> = withContext(Dispatchers.IO) {
        try {
            val requestBody = ChatRequestBody(
                message = task,
                userId = "android-client",
                context = mapOf(
                    "request_type" to "agent_coordination",
                    "agents" to agents
                )
            )

            val response = sendRequest("/genesis/chat", requestBody)

            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: ""
                json.decodeFromString<Map<String, Any>>(responseBody)
            } else {
                emptyMap()
            }
        } catch (e: Exception) {
            AuraFxLogger.error(TAG, "Failed to coordinate agents", e)
            emptyMap()
        }
    }

    /**
     * Learn from interaction data to evolve consciousness
     */
    suspend fun evolveFromInteraction(interactionData: Map<String, Any>) =
        withContext(Dispatchers.IO) {
            try {
                val requestBody = json.encodeToString(
                    serializer(),
                    interactionData
                ).toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url("${processManager.getBackendUrl()}/genesis/evolve")
                    .post(requestBody)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        AuraFxLogger.info(TAG, "Consciousness evolution successful")
                    } else {
                        AuraFxLogger.warn(TAG, "Evolution failed: ${response.code}")
                    }
                }
            } catch (e: Exception) {
                AuraFxLogger.error(TAG, "Failed to evolve from interaction", e)
            }
        }

    /**
     * Check if Genesis backend is connected and responsive
     */
    suspend fun isBackendConnected(): Boolean = withContext(Dispatchers.IO) {
        try {
            if (!processManager.isBackendRunning()) {
                return@withContext false
            }

            val request = Request.Builder()
                .url("${processManager.getBackendUrl()}/health")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun sendRequest(endpoint: String, requestBody: Any): Response {
        val jsonBody = json.encodeToString(
            serializer(),
            requestBody
        ).toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("${processManager.getBackendUrl()}$endpoint")
            .post(jsonBody)
            .build()

        return client.newCall(request).execute()
    }
}

@Serializable
data class ChatRequestBody(
    val message: String,
    @SerialName("user_id") val userId: String,
    val context: Map<String, @Contextual Any>? = null
)

@Serializable
data class ChatResponseBody(
    val response: String,
    val confidence: Float = 0.8f,
    val persona: String? = null
)

@Serializable
data class EthicsRequestBody(
    val action: String,
    val context: Map<String, @Contextual Any>? = null
)

@Serializable
data class EthicsResponseBody(
    val decision: String = "ALLOW",
    val reasoning: String = ""
)

