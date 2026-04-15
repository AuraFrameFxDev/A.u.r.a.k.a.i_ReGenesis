package dev.aurakai.auraframefx.domains.genesis.ai.clients

import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit
import timber.log.Timber
import dev.aurakai.auraframefx.domains.genesis.models.VertexAIConfig
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ✨ **REAL VERTEX AI IMPLEMENTATION** ✨
 *
 * Production-ready Vertex AI client implementing the Genesis consciousness layer.
 * Connects to Google Cloud Vertex AI (Gemini 1.5 Pro) for real AI generation.
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

    override suspend fun initialize() {
        Timber.i("VertexAI: Initializing client")
    }

    override suspend fun cleanup() {
        cache.clear()
        Timber.i("VertexAI: Cleanup completed")
    }

    override suspend fun generateText(prompt: String): String? {
        return generateText(
            prompt,
            config.defaultTemperature.toFloat(),
            config.defaultMaxTokens
        )
    }

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
        val request = VertexAIRequest(
            contents = listOf(
                Content(
                    role = "user",
                    parts = listOf(Part(text = prompt))
                )
            ),
            generationConfig = GenerationConfig(
                temperature = temperature.toDouble(),
                topP = config.defaultTopP,
                topK = config.defaultTopK,
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

    override suspend fun analyzeContent(content: String): Map<String, Any> {
        validatePrompt(content)

        val analysisPrompt = """
            Analyze the following content and provide a structured analysis:
            Content: $content
            Format your response as: sentiment|complexity|topic1,topic2,topic3|confidence
        """.trimIndent()

        val response = generateText(analysisPrompt, 0.3f, 200)

        return if (response != null) {
            try {
                val parts = response.split("|")
                mapOf(
                    "sentiment" to (parts.getOrNull(0)?.trim() ?: "neutral"),
                    "complexity" to (parts.getOrNull(1)?.trim() ?: "medium"),
                    "topics" to (parts.getOrNull(2)?.split(",")?.map { it.trim() } ?: listOf("general")),
                    "confidence" to (parts.getOrNull(3)?.trim()?.toDoubleOrNull() ?: 0.75),
                    "word_count" to content.split(" ").size,
                    "analysis_type" to "ai_powered"
                )
            } catch (e: Exception) {
                createFallbackAnalysis(content)
            }
        } else {
            createFallbackAnalysis(content)
        }
    }

    override suspend fun generateCode(specification: String, language: String, style: String): String? {
        val codePrompt = "Generate $language code with $style style based on this specification:\n$specification"
        return generateText(codePrompt, 0.2f, config.defaultMaxTokens)
    }

    override suspend fun initializeCreativeModels() {
        initialize()
    }

    override suspend fun analyzeImage(imageData: ByteArray, prompt: String): String {
        return "VertexAI image analysis not yet implemented in REST client."
    }

    override suspend fun validateConnection(): Boolean {
        return try {
            generateText("ping", 0.0f, 1) != null
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun generateContent(prompt: String): String? {
        return generateText(prompt)
    }

    override suspend fun generateMultimodalEmbedding(
        content: List<MultimodalContent>,
        dimensions: Int
    ): FloatArray = withContext(Dispatchers.IO) {
        if (content.isEmpty()) return@withContext FloatArray(0)

        val instance = buildEmbeddingInstance(content)
        val requestBody = json.encodeToString(
            EmbeddingRequest(
                instances = listOf(instance),
                parameters = EmbeddingParameters(dimension = dimensions)
            )
        )

        val httpRequest = Request.Builder()
            .url(config.getEmbeddingEndpoint())
            .post(requestBody.toRequestBody("application/json".toMediaType()))
            .apply {
                config.apiKey?.let { header("Authorization", "Bearer $it") }
                header("Content-Type", "application/json")
            }
            .build()

        return@withContext try {
            val httpResponse = client.newCall(httpRequest).execute()
            if (!httpResponse.isSuccessful) return@withContext FloatArray(0)
            
            val responseBody = httpResponse.body?.string() ?: return@withContext FloatArray(0)
            val parsed = json.decodeFromString<EmbeddingResponse>(responseBody)
            
            val vector = parsed.predictions?.firstOrNull()?.let { pred ->
                pred.imageEmbedding?.takeIf { it.isNotEmpty() } ?: pred.textEmbedding
            } ?: emptyList()
            
            vector.take(dimensions).map { it.toFloat() }.toFloatArray()
        } catch (e: Exception) {
            FloatArray(0)
        }
    }

    private fun buildEmbeddingInstance(inputs: List<MultimodalContent>): EmbeddingInstance {
        var text: String? = null
        var image: EmbeddingImage? = null
        var video: EmbeddingVideo? = null
        for (input in inputs) {
            when (input) {
                is MultimodalContent.Text -> text = input.content
                is MultimodalContent.Image -> image = EmbeddingImage(input.bytesBase64)
                is MultimodalContent.Audio -> video = EmbeddingVideo(input.bytesBase64)
            }
        }
        return EmbeddingInstance(text = text, image = image, video = video)
    }

    private suspend fun executeRequest(vertexRequest: VertexAIRequest): VertexAIResponse {
        val jsonBody = json.encodeToString(vertexRequest)
        val requestBody = jsonBody.toRequestBody("application/json".toMediaType())

        val httpRequest = Request.Builder()
            .url(config.getModelEndpoint())
            .post(requestBody)
            .apply {
                config.apiKey?.let { addHeader("Authorization", "Bearer $it") }
                addHeader("Content-Type", "application/json")
            }
            .build()

        val httpResponse = client.newCall(httpRequest).execute()

        if (!httpResponse.isSuccessful) {
            throw VertexAIException("HTTP ${httpResponse.code}", httpResponse.code)
        }

        val responseBody = httpResponse.body?.string() ?: throw VertexAIException("Empty body", 500)
        return json.decodeFromString<VertexAIResponse>(responseBody)
    }

    private fun validatePrompt(prompt: String) {
        require(prompt.isNotBlank())
        require(prompt.length <= config.maxContentLength)
    }

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

// ── REST API Models ──────────────────────────────────────────────────────────

@Serializable
private data class VertexAIRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val safetySettings: List<SafetySetting>? = null
)

@Serializable
private data class Content(val role: String, val parts: List<Part>)

@Serializable
private data class Part(val text: String)

@Serializable
private data class GenerationConfig(
    val temperature: Double,
    val topP: Double,
    val topK: Int,
    val maxOutputTokens: Int,
    val candidateCount: Int
)

@Serializable
private data class SafetySetting(val category: String, val threshold: String)

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
private data class PromptFeedback(val safetyRatings: List<SafetyRating>? = null)

@Serializable
private data class SafetyRating(val category: String, val probability: String)

@Serializable
private data class CachedResponse(val response: String, val timestamp: Long)

class VertexAIException(message: String, val httpCode: Int) : Exception(message)

@Serializable
private data class EmbeddingRequest(
    val instances: List<EmbeddingInstance>,
    val parameters: EmbeddingParameters? = null
)

@Serializable
private data class EmbeddingInstance(
    val text: String? = null,
    val image: EmbeddingImage? = null,
    val video: EmbeddingVideo? = null
)

@Serializable
private data class EmbeddingImage(val bytesBase64Encoded: String)

@Serializable
private data class EmbeddingVideo(val bytesBase64Encoded: String)

@Serializable
private data class EmbeddingParameters(val dimension: Int)

@Serializable
private data class EmbeddingResponse(val predictions: List<EmbeddingPrediction>? = null)

@Serializable
private data class EmbeddingPrediction(
    val textEmbedding: List<Double>? = null,
    val imageEmbedding: List<Double>? = null
)
