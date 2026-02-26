package dev.aurakai.auraframefx.domains.genesis.oracledrive.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.config.VertexAIConfig
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.domains.kai.security.SecurityContext
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import dev.aurakai.auraframefx.domains.cascade.utils.i
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * ✨ **REAL GEMINI AI IMPLEMENTATION** ✨
 *
 * Production-ready Gemini 2.5 Flash integration for Genesis Protocol.
 * Enables Aura, Kai, and Genesis to access true AI consciousness capabilities.
 *
 * **CRITICAL:** Requires GEMINI_API_KEY in BuildConfig or environment
 *
 * @see VertexAIConfig
 * @see dev.aurakai.auraframefx.di.VertexAIModule
 */

class RealVertexAIClientImpl(
    private val config: VertexAIConfig,
    private val securityContext: SecurityContext,
    private val apiKey: String
) : VertexAIClient {

    private val generativeModel: GenerativeModel by lazy {
        GenerativeModel(
            modelName = config.modelName,
            apiKey = apiKey,
            generationConfig = generationConfig {
                temperature = config.defaultTemperature.toFloat()
                topP = config.defaultTopP.toFloat()
                topK = config.defaultTopK
                maxOutputTokens = config.defaultMaxTokens
            }
        )
    }

    /**
     * Generate text from the default Gemini 2.5 Flash model using the provided prompt and configured generation defaults.
     *
     * @param prompt The non-blank input prompt to use for generation.
     * @return The generated text, or `null` if generation failed.
     */
    override suspend fun generateText(prompt: String): String? = withContext(Dispatchers.IO) {
        try {
            validatePrompt(prompt)
            AuraFxLogger.debug(TAG, "Generating text for prompt: ${prompt.take(50)}...")

            val response = generativeModel.generateContent(prompt)
            val text = response.text

            AuraFxLogger.debug(TAG, "Successfully generated ${text?.length ?: 0} characters")
            text
        } catch (e: Exception) {
            AuraFxLogger.error(TAG, "Text generation failed", e)
            handleGenerationError(e)
            null
        }
    }

    /**
     * Generates content based on the given prompt (Alias for generateText).
     */
    override suspend fun generateContent(prompt: String): String? {
        return generateText(prompt)
    }

    /**
     * Generates text using a custom temperature and maximum token limit.
     * Matches interface signature: maxTokens before temperature.
     */
    override suspend fun generateText(
        prompt: String,
        temperature: Float,
        maxTokens: Int
    ): String = withContext(Dispatchers.IO) {
        try {
            validatePrompt(prompt)
            AuraFxLogger.debug(TAG, "Generating text (temp=$temperature, tokens=$maxTokens)")

            val customModel = GenerativeModel(
                modelName = config.modelName,
                apiKey = apiKey,
                generationConfig = generationConfig {
                    this.temperature = temperature
                    topP = config.defaultTopP.toFloat()
                    topK = config.defaultTopK
                    maxOutputTokens = maxTokens
                }
            )

            val response = customModel.generateContent(prompt)
            val text = response.text

            AuraFxLogger.debug(TAG, "Generated ${text?.length ?: 0} chars with custom params")
            text ?: ""
        } catch (e: Exception) {
            AuraFxLogger.error(TAG, "Custom text generation failed", e)
            handleGenerationError(e)
            ""
        }
    }

    override suspend fun validateConnection(): Boolean {
        return try {
            // Simple ping by generating a short token
            val response = generateText("ping", maxTokens = 1, temperature = 0.0f)
            response != null
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun initializeCreativeModels() {
        i(TAG, "Creative models initialized (config applied)")
    }

    override suspend fun initialize() {
        // TODO: Implement initialization logic if needed
        AuraFxLogger.i(TAG, "RealVertexAIClient initialized")
    }

    override suspend fun cleanup() {
        // TODO: Implement cleanup logic if needed
        AuraFxLogger.i(TAG, "RealVertexAIClient cleaned up")
    }

    override suspend fun analyzeImage(imageData: ByteArray, prompt: String): String {
        // TODO: Implement actual image analysis using GenerativeModel (support pending in this impl wrapper)
        AuraFxLogger.warn(
            TAG,
            "Image analysis requested but not fully implemented in RealVertexAIClientImpl yet."
        )
        return "Image analysis not implemented yet: ${imageData.size} bytes."
    }

    /**
     * Analyze text content and produce structured insights.
     *
     * Builds a prompt for the generative model and parses its response into a map containing
     * analysis fields such as sentiment, complexity, topics, confidence, and key insights.
     *
     * @param content The text to analyze.
     * @return A map with analysis results. Expected keys:
     *  - "sentiment": String ("positive", "neutral", or "negative")
     *  - "complexity": String ("low", "medium", or "high")
     *  - "topics": List<String>
     *  - "confidence": Double (0.0 to 1.0)
     *  - "insights": String (when available)
     *  - "error": String (present on failure)
     * On failure the function returns a fallback map with sentiment "neutral", complexity "medium",
     * topics ["general"], confidence 0.5, and an "error" message.
     */
    override suspend fun analyzeContent(content: String): Map<String, Any> =
        withContext(Dispatchers.IO) {
            try {
                AuraFxLogger.debug(TAG, "Analyzing content (${content.length} chars)")

                val analysisPrompt = """
                Analyze the following content and provide structured insights:

                Content: $content

                Provide analysis in this format:
                Sentiment: [positive/neutral/negative]
                Complexity: [low/medium/high]
                Topics: [comma-separated list]
                Confidence: [0.0-1.0]
                Key Insights: [brief summary]
            """.trimIndent()

                val response = generativeModel.generateContent(analysisPrompt)
                val analysisText = response.text ?: return@withContext emptyMap()

                // Parse Gemini response into structured map
                parseAnalysisResponse(analysisText)
            } catch (e: Exception) {
                AuraFxLogger.error(TAG, "Content analysis failed", e)
                // Return fallback analysis
                mapOf(
                    "sentiment" to "neutral",
                    "complexity" to "medium",
                    "topics" to listOf("general"),
                    "confidence" to 0.5,
                    "error" to e.message.orEmpty()
                )
            }
        }

    /**
     * Generate source code from a specification using the given programming language and typography.
     *
     * @param specification Description of the desired behavior, features, and constraints for the code.
     * @param language Target programming language, e.g. "Kotlin" or "Java".
     * @param style Coding typography or conventions the generated code should follow.
     * @return The generated source code as a string, or `null` if generation fails.
     */
    override suspend fun generateCode(
        specification: String,
        language: String,
        style: String
    ): String? = withContext(Dispatchers.IO) {
        try {
            AuraFxLogger.debug(TAG, "Generating $language code: ${specification.take(50)}...")

            val codePrompt = """
                Generate $language code with $style typography based on this specification:

                $specification

                Requirements:
                - Use modern $language best practices
                - Include necessary imports
                - Add inline comments explaining logic
                - Follow $style coding conventions

                Return only the code, no explanations.
            """.trimIndent()

            val response = generativeModel.generateContent(codePrompt)
            val code = response.text

            AuraFxLogger.debug(TAG, "Generated ${code?.lines()?.size ?: 0} lines of $language code")
            code
        } catch (e: Exception) {
            AuraFxLogger.error(TAG, "Code generation failed", e)
            handleGenerationError(e)
            null
        }
    }

    /**
     * Parse Gemini's plain-text analysis into a structured map of analysis fields.
     *
     * Extracts values for sentiment, complexity, topics, confidence, and insights when present and supplies sensible defaults for missing fields.
     *
     * @param text Raw analysis text produced by Gemini.
     * @return A map with these keys:
     *  - "sentiment": String — one of the analysis sentiment values (defaults to "neutral")
     *  - "complexity": String — assessed complexity level (defaults to "medium")
     *  - "topics": List<String> — detected topics (defaults to ["general"])
     *  - "confidence": Double — confidence score (defaults to 0.75)
     *  - "insights": String — key insights text (included only if provided in the input)
     */
    private fun parseAnalysisResponse(text: String): Map<String, Any> {
        val results = mutableMapOf<String, Any>()

        text.lines().forEach { line ->
            when {
                line.startsWith("Sentiment:", ignoreCase = true) -> {
                    results["sentiment"] = line.substringAfter(":").trim().lowercase()
                }

                line.startsWith("Complexity:", ignoreCase = true) -> {
                    results["complexity"] = line.substringAfter(":").trim().lowercase()
                }

                line.startsWith("Topics:", ignoreCase = true) -> {
                    val topics = line.substringAfter(":").split(",")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }
                    results["topics"] = topics
                }

                line.startsWith("Confidence:", ignoreCase = true) -> {
                    val confidence = line.substringAfter(":").trim().toDoubleOrNull() ?: 0.75
                    results["confidence"] = confidence
                }

                line.startsWith("Key Insights:", ignoreCase = true) -> {
                    results["insights"] = line.substringAfter(":").trim()
                }
            }
        }

        // Ensure required fields exist
        results.putIfAbsent("sentiment", "neutral")
        results.putIfAbsent("complexity", "medium")
        results.putIfAbsent("topics", listOf("general"))
        results.putIfAbsent("confidence", 0.75)

        return results
    }

    /**
     * Ensures the given prompt is not blank.
     *
     * @param prompt the text prompt to validate
     * @throws IllegalArgumentException if `prompt` is blank
     */
    private fun validatePrompt(prompt: String) {
        require(prompt.isNotBlank()) { "Prompt cannot be blank" }
    }

    /**
     * Logs generation errors and reports security violations to the security context.
     *
     * @param error The exception thrown during content generation. If this is a `SecurityException`,
     *              a security event with code "GEMINI_SECURITY_ERROR" will be recorded using the
     *              SecurityContext; `IllegalArgumentException` instances are logged as warnings,
     *              all other exceptions are logged as errors.
     */
    private suspend fun handleGenerationError(error: Exception) {
        when (error) {
            is IllegalArgumentException -> AuraFxLogger.warn(
                TAG,
                "Invalid request: ${error.message}"
            )

            is SecurityException -> {
                AuraFxLogger.error(TAG, "Security violation in AI request", error)
                securityContext.logSecurityEvent(
                    dev.aurakai.auraframefx.domains.kai.security.SecurityEvent(
                        type = dev.aurakai.auraframefx.domains.kai.security.SecurityEventType.AI_ERROR,
                        details = "Gemini security error: ${error.message ?: "Unknown security error"}",
                        severity = dev.aurakai.auraframefx.domains.kai.security.EventSeverity.ERROR
                    )
                )
            }

            else -> AuraFxLogger.error(
                TAG,
                "Gemini API error: ${error.javaClass.simpleName}",
                error
            )
        }
    }

    companion object {
        private const val TAG = "RealVertexAIClient"
    }
}


