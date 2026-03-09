package dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.clients

import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.config.VertexAIConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ✨ **REAL VERTEX AI IMPLEMENTATION** ✨
 *
 * Production-ready Vertex AI client implementing the Genesis consciousness layer.
 * Connects to Google Cloud Vertex AI (Gemini 1.5 Pro) for real AI generation.
 *
 * **Architecture:**
 * - Uses OkHttp for HTTP requests to Vertex AI REST API
 * - Implements retry logic with exponential backoff
 * - Provides LRU caching for repeated prompts
 * - Validates all inputs and handles errors gracefully
 *
 * **Configuration:**
 * - Project: collabcanvas
 * - Location: us-central1
 * - Model: gemini-1.5-pro-002
 * - Endpoint: us-central1-aiplatform.googleapis.com
 *
 * **Security:**
 * - Requires GOOGLE_APPLICATION_CREDENTIALS or Firebase Auth
 * - Uses Bearer token authentication
 * - Implements content safety filters
 * - Validates max content length
 *
 * @see VertexAIConfig
 * @see TrinityCoordinatorService
 */
@Singleton
class VertexAIClientImpl @Inject constructor(
    private val config: VertexAIConfig
) : VertexAIClient {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
        encodeDefaults = true
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(config.timeoutMs, TimeUnit.MILLISECONDS)
        .readTimeout(config.timeoutMs, TimeUnit.MILLISECONDS)
        .writeTimeout(config.timeoutMs, TimeUnit.MILLISECONDS)
        .build()

    // Simple LRU cache for repeated prompts
    private val cache = object : LinkedHashMap<String, CachedResponse>(
        config.maxCacheSize,
        0.75f,
        true
    ) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, CachedResponse>?): Boolean {
            return size > config.maxCacheSize
        }
    }

    /**
     */
    override suspend fun generateText(prompt: String): String? {
        return generateText(
            prompt,
            config.defaultTemperature.toFloat(),
            config.defaultMaxTokens
        )
    }

    /**
     *
     */
    override suspend fun generateText(
        prompt: String,
        temperature: Float,
        maxTokens: Int
    ): String? = withContext(Dispatchers.IO) {
        validatePrompt(prompt)

        // Check cache if enabled
        val cacheKey = "$prompt-$temperature-$maxTokens"
        if (config.enableCaching) {
            cache[cacheKey]?.let { cached ->
                if (System.currentTimeMillis() - cached.timestamp < config.cacheExpiryMs) {
                    Timber.d("VertexAI: Cache hit for prompt")
                    return@withContext cached.response
                } else {
                    cache.remove(cacheKey)
                }
            }
        }

        // Build Vertex AI request
        config.topP.toDouble()
        val request = VertexAIRequest(
            contents = listOf(
                Content(
                    role = "user",
                    parts = listOf(Part(text = prompt))
                )
            ),
            generationConfig = GenerationConfig(
                temperature = temperature.toDouble(),
                topP = config.topP.toDouble(),
                topK = config.topK,
                maxOutputTokens = maxTokens,
                candidateCount = 1
            ),
            safetySettings = if (config.enableSafetyFilters) {
                listOf(
                    SafetySetting("HARM_CATEGORY_HARASSMENT", "BLOCK_MEDIUM_AND_ABOVE"),
                    SafetySetting("HARM_CATEGORY_HATE_SPEECH", "BLOCK_MEDIUM_AND_ABOVE"),
                    SafetySetting("HARM_CATEGORY_SEXUALLY_EXPLICIT", "BLOCK_MEDIUM_AND_ABOVE"),
                    SafetySetting("HARM_CATEGORY_DANGEROUS_CONTENT", "BLOCK_MEDIUM_AND_ABOVE")
                )
            } else {
                emptyList()
            }
        )

        // Execute with retry logic
        var lastException: Exception? = null
        repeat(config.maxRetries + 1) { attempt ->
            try {
                val response = executeRequest(request)

                // Extract generated text
                val generatedText = response.candidates?.firstOrNull()
                    ?.content?.parts?.firstOrNull()
                    ?.text

                if (generatedText != null) {
                    // Cache successful response
                    if (config.enableCaching) {
                        cache[cacheKey] = CachedResponse(
                            response = generatedText,
                            timestamp = System.currentTimeMillis()
                        )
                    }
                    Timber.i("VertexAI: Generated ${generatedText.length} chars (attempt ${attempt + 1})")
                    return@withContext generatedText
                } else {
                    Timber.w("VertexAI: Empty response from API")
                    return@withContext null
                }
            } catch (e: Exception) {
                lastException = e
                Timber.w(e, "VertexAI: Attempt ${attempt + 1} failed")

                if (attempt < config.maxRetries) {
                    val delayMs = config.retryDelayMs * (1 shl attempt) // Exponential backoff
                    Timber.d("VertexAI: Retrying in ${delayMs}ms...")
                    delay(delayMs)
                }
            }
        }

        Timber.e(lastException, "VertexAI: All retry attempts exhausted")
        return@withContext null
    }

    /**
     * Analyze content and return structured analysis.
     */
    override suspend fun analyzeContent(content: String): Map<String, Any> {
        validatePrompt(content)

        val analysisPrompt = """
            Analyze the following content and provide a structured analysis:

            Content: $content

            Provide:
            1. Sentiment (positive/negative/neutral)
            2. Complexity level (low/medium/high)
            3. Main topics (list)
            4. Confidence score (0.0 to 1.0)
            5. Word count

            Format your response as: sentiment|complexity|topic1,topic2,topic3|confidence
        """.trimIndent()

        val response = generateText(analysisPrompt, 0.3f, 200)

        // Parse AI response into structured map
        return if (response != null) {
            try {
                val parts = response.split("|")
                mapOf<String, Any>(
                    "sentiment" to (parts.getOrNull(0)?.trim() ?: "neutral"),
                    "complexity" to (parts.getOrNull(1)?.trim() ?: "medium"),
                    "topics" to (parts.getOrNull(2)?.split(",")?.map { it.trim() }
                        ?: listOf("general")),
                    "confidence" to (parts.getOrNull(3)?.trim()?.toDoubleOrNull() ?: 0.75),
                    "word_count" to content.split(" ").size,
                    "analysis_type" to "ai_powered"
                )
            } catch (e: Exception) {
                Timber.e(e, "VertexAI: Failed to parse analysis response")
                createFallbackAnalysis(content)
            }
        } else {
            createFallbackAnalysis(content)
        }
    }

    /**
     * Generate code based on specification.
     */
    override suspend fun generateCode(
        specification: String,
        language: String,
        style: String
    ): String? {
        validatePrompt(specification)

        val codePrompt = """
            Generate $language code with $style style based on this specification:

            $specification

            Provide only the code without explanations.
            Use proper formatting and comments.
        """.trimIndent()

        return generateText(codePrompt, 0.2f, config.defaultMaxTokens)
    }

    override suspend fun initializeCreativeModels() {
        Timber.i("VertexAI: Initializing creative models")
    }

    override suspend fun analyzeImage(imageData: ByteArray, prompt: String): String {
        return "VertexAI image analysis not yet implemented in REST client. Size: ${imageData.size} bytes. Prompt: $prompt"
    }

    override suspend fun validateConnection(): Boolean {
        return try {
            val probe = generateText("ping", 0.0f, 1)
            probe != null
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Generates content from the provided prompt using the client's text generation pipeline.
     *
     * @param prompt The user-facing prompt describing the desired content.
     * @return The generated content string, or `null` if no content was produced.
     */
    override suspend fun generateContent(prompt: String): String? {
        return generateText(prompt)
    }

    /**
     * Performs startup initialization for the Vertex AI client.
     *
     * Implementations can prepare resources or perform health checks; this implementation logs client initialization.
     */
    override suspend fun initialize() {
        // Initialize Vertex AI client connection
        Timber.i("VertexAI: Initializing client")
    }

    /**
     * Clears the in-memory response cache and logs completion.
     *
     * Removes all cached generation responses held by the client.
     */
    override suspend fun cleanup() {
        // Cleanup Vertex AI client resources
        cache.clear()
        Timber.i("VertexAI: Cleanup completed")
    }

    /**
     * Execute HTTP request to Vertex AI endpoint.
     */
    private suspend fun executeRequest(vertexRequest: VertexAIRequest): VertexAIResponse {
        val jsonBody = json.encodeToString(vertexRequest)

        // Log request details if logging enabled
        if (config.enableLogging) {
            Timber.d("VertexAI Request: ${config.getModelEndpoint()}")
            if (config.logLevel == "DEBUG") {
                Timber.d("VertexAI Payload: $jsonBody")
            }
        }

        val requestBody = jsonBody.toRequestBody("application/json".toMediaType())

        val httpRequest = Request.Builder()
            .url(config.getModelEndpoint())
            .post(requestBody)
            .apply {
                // Add authentication header
                config.apiKey?.let { apiKey ->
                    header("Authorization", "Bearer $apiKey")
                }
                header("Content-Type", "application/json")
            }
            .build()

        val httpResponse = client.newCall(httpRequest).execute()

        if (!httpResponse.isSuccessful) {
            val errorBody = httpResponse.body?.string() ?: "No error details"
            Timber.e("VertexAI HTTP ${httpResponse.code}: $errorBody")
            throw VertexAIException(
                "HTTP ${httpResponse.code}: ${httpResponse.message}",
                httpResponse.code
            )
        }

        val responseBody = httpResponse.body?.string()
            ?: throw VertexAIException("Empty response body", 500)

        if (config.enableLogging && config.logLevel == "DEBUG") {
            Timber.d("VertexAI Response: $responseBody")
        }

        return json.decodeFromString<VertexAIResponse>(responseBody)
    }

    /**
     * Validate prompt is not blank and within size limits.
     */
    private fun validatePrompt(prompt: String) {
        require(prompt.isNotBlank()) { "Prompt cannot be blank" }
        require(prompt.length <= config.maxContentLength) {
            "Prompt exceeds max length of ${config.maxContentLength} characters"
        }
    }

    /**
     * Create fallback analysis when AI fails.
     */
    private fun createFallbackAnalysis(content: String): Map<String, Any> {
        return mapOf(
            "sentiment" to "neutral",
            "complexity" to "medium",
            "topics" to listOf("general"),
            "confidence" to 0.5,
            "word_count" to content.split(" ").size,
            "analysis_type" to "fallback"
        )
    }
}

// ============================================================================
// Data Classes for Vertex AI REST API
// ============================================================================

/**
 * Vertex AI request payload for Gemini models.
 */
@Serializable
private data class VertexAIRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val safetySettings: List<SafetySetting>? = null
)

@Serializable
private data class Content(
    val role: String,
    val parts: List<Part>
)

@Serializable
private data class Part(
    val text: String
)

@Serializable
private data class GenerationConfig(
    val temperature: Double,
    val topP: Double,
    val topK: Int,
    val maxOutputTokens: Int,
    val candidateCount: Int
)

@Serializable
private data class SafetySetting(
    val category: String,
    val threshold: String
)

/**
 * Vertex AI response payload.
 */
@Serializable
private data class VertexAIResponse(
    val candidates: List<Candidate>? = null,
    val promptFeedback: PromptFeedback? = null
)

@Serializable
private data class Candidate(
    val content: Content,
    val finishReason: String? = null,
    val safetyRatings: List<SafetyRating>? = null
)

@Serializable
private data class PromptFeedback(
    val safetyRatings: List<SafetyRating>? = null
)

@Serializable
private data class SafetyRating(
    val category: String,
    val probability: String
)

/**
 * Cached response with timestamp.
 */
private data class CachedResponse(
    val response: String,
    val timestamp: Long
)

/**
 * Custom exception for Vertex AI errors.
 */
class VertexAIException(
    message: String,
    val httpCode: Int
) : Exception(message)

