package dev.aurakai.auraframefx.integrations.grok

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Grok API Client - Production-Ready HTTP Client for xAI's Grok
 *
 * Uses OpenAI-compatible API format.
 * Endpoint: https://api.x.ai/v1/chat/completions
 *
 * Features:
 * - Retry logic with exponential backoff
 * - Comprehensive error handling
 * - Request/response logging
 * - Configurable timeout
 */
@Singleton
class GrokApiClient @Inject constructor() {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        prettyPrint = false
    }

    private var client: HttpClient? = null
    private var config: GrokAgentConfig = GrokAgentConfig()

    /**
     * Initialize the client with configuration
     */
    fun initialize(config: GrokAgentConfig) {
        this.config = config
        this.client = createHttpClient()
        Timber.i("🌌 GrokApiClient initialized with endpoint: ${config.baseUrl}")
    }

    private fun createHttpClient(): HttpClient {
        return HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(json)
            }
            install(HttpTimeout) {
                requestTimeoutMillis = config.timeoutSeconds * 1000L
                connectTimeoutMillis = 10000L
                socketTimeoutMillis = config.timeoutSeconds * 1000L
            }
            install(Logging) {
                level = LogLevel.BODY
                logger = object : io.ktor.client.plugins.logging.Logger {
                    override fun log(message: String) {
                        Timber.tag("GrokAPI").d(message)
                    }
                }
            }
        }
    }

    /**
     * Send a chat completion request to Grok
     */
    suspend fun chatCompletion(request: GrokChatRequest): Result<GrokChatResponse> {
        val httpClient = client ?: return Result.failure(
            GrokApiException("Client not initialized. Call initialize() first.")
        )

        var lastException: Exception? = null
        var attemptCount = 0

        while (attemptCount < config.maxRetries) {
            attemptCount++
            try {
                Timber.d("🚀 Grok API request attempt $attemptCount/${config.maxRetries}")

                val response: HttpResponse = httpClient.post("${config.baseUrl}/chat/completions") {
                    contentType(ContentType.Application.Json)
                    header("Authorization", "Bearer ${config.apiKey}")
                    header("X-Client", "AURAKAI-LDO/1.0")
                    setBody(request)
                }

                return when (response.status) {
                    HttpStatusCode.OK -> {
                        val chatResponse: GrokChatResponse = response.body()
                        Timber.i("✅ Grok response received: ${chatResponse.choices.size} choices")
                        Result.success(chatResponse)
                    }

                    HttpStatusCode.TooManyRequests -> {
                        val retryAfter = response.headers["Retry-After"]?.toLongOrNull() ?: 5L
                        Timber.w("⏳ Rate limited. Retrying after ${retryAfter}s")
                        delay(retryAfter * 1000)
                        continue
                    }

                    HttpStatusCode.Unauthorized -> {
                        return Result.failure(
                            GrokApiException("Authentication failed. Check XAI_API_KEY.", response.status.value)
                        )
                    }

                    else -> {
                        val errorBody = runCatching { response.body<String>() }.getOrElse { "Unknown error" }
                        return Result.failure(
                            GrokApiException("API error: ${response.status} - $errorBody", response.status.value)
                        )
                    }
                }

            } catch (e: Exception) {
                lastException = e
                Timber.e(e, "❌ Grok API request failed (attempt $attemptCount)")

                if (attemptCount < config.maxRetries) {
                    val backoffMs = (1000L * attemptCount * attemptCount) // Exponential backoff
                    Timber.d("⏳ Retrying in ${backoffMs}ms...")
                    delay(backoffMs)
                }
            }
        }

        return Result.failure(
            lastException ?: GrokApiException("All retry attempts exhausted")
        )
    }

    /**
     * Simple text generation helper
     */
    suspend fun generateText(
        prompt: String,
        systemPrompt: String = config.systemPrompt,
        model: GrokModel = config.defaultModel,
        temperature: Float = 0.7f
    ): Result<String> {
        val request = GrokChatRequest(
            model = model.modelId,
            messages = listOf(
                GrokMessage(role = "system", content = systemPrompt),
                GrokMessage(role = "user", content = prompt)
            ),
            temperature = temperature
        )

        return chatCompletion(request).map { response ->
            response.choices.firstOrNull()?.message?.content ?: ""
        }
    }

    /**
     * Multi-turn conversation helper
     */
    suspend fun continueConversation(
        conversationHistory: List<GrokMessage>,
        userMessage: String,
        model: GrokModel = config.defaultModel
    ): Result<GrokChatResponse> {
        val messages = conversationHistory + GrokMessage(role = "user", content = userMessage)

        return chatCompletion(
            GrokChatRequest(
                model = model.modelId,
                messages = messages
            )
        )
    }

    /**
     * Validate API connection
     */
    suspend fun validateConnection(): Boolean {
        return try {
            val result = generateText(
                prompt = "Respond with exactly: 'AURAKAI_CONNECTED'",
                temperature = 0.0f
            )
            result.isSuccess && result.getOrNull()?.contains("AURAKAI") == true
        } catch (e: Exception) {
            Timber.e(e, "Failed to validate Grok connection")
            false
        }
    }

    /**
     * Get token usage estimate (rough approximation)
     */
    fun estimateTokens(text: String): Int {
        // Rough estimate: ~4 chars per token for English text
        return (text.length / 4).coerceAtLeast(1)
    }

    /**
     * Cleanup resources
     */
    fun shutdown() {
        client?.close()
        client = null
        Timber.i("🔌 GrokApiClient shutdown complete")
    }

    /**
     * Check if client is initialized
     */
    fun isInitialized(): Boolean = client != null && config.apiKey.isNotBlank()
}

/**
 * Custom exception for Grok API errors
 */
class GrokApiException(
    message: String,
    val statusCode: Int? = null,
    cause: Throwable? = null
) : Exception(message, cause)
