package dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.config

import kotlinx.serialization.Serializable

/**
 * Configuration for Vertex AI client
 */
@Serializable
data class VertexAIConfig(
    val projectId: String = "",
    val location: String = "us-central1",
    val endpoint: String = "us-central1-aiplatform.googleapis.com",
    val modelName: String = "gemini-pro",
    val model: String = "gemini-pro", // Alias for modelName
    val apiKey: String? = null,
    val temperature: Float = 0.7f,
    val maxTokens: Int = 2048,
    val maxOutputTokens: Int = 8192,
    val topP: Float = 0.95f,
    val topK: Int = 40,
    val candidateCount: Int = 1,
    val stopSequences: List<String> = emptyList(),
    val safetySettings: Map<String, String> = emptyMap(),
    val timeout: Long = 30000,
    val retryCount: Int = 3,
    val enableStreaming: Boolean = false,
    // Additional properties for VertexAIClientImpl
    val timeoutMs: Long = 30000,
    val maxCacheSize: Int = 100,
    val defaultTemperature: Double = 0.7,
    val defaultMaxTokens: Int = 2048,
    val defaultTopP: Float = 0.95f,
    val defaultTopK: Int = 40,
    val enableCaching: Boolean = true,
    val cacheExpiryMs: Long = 3600000, // 1 hour
    val enableSafetyFilters: Boolean = true,
    val maxRetries: Int = 3,
    val retryDelayMs: Long = 1000,
    val enableLogging: Boolean = true,
    val logLevel: String = "INFO",
    val maxContentLength: Int = 32000
) {
    companion object {
        /**
         * Create a VertexAIConfig populated with the library's standard default values.
         *
         * @return A VertexAIConfig instance populated with the standard default values for all configuration fields.
         */
        fun default(): VertexAIConfig = VertexAIConfig()

        /**
         * Builds a VertexAIConfig by reading configuration from environment variables.
         *
         * Reads the following environment variables and falls back to the shown defaults when absent:
         * - VERTEX_PROJECT_ID -> projectId (default: "")
         * - VERTEX_LOCATION -> location (default: "us-central1")
         * - VERTEX_MODEL -> modelName (default: "gemini-pro")
         *
         * @return A VertexAIConfig populated from the environment variables (using defaults for any unset values).
         */
        fun fromEnvironment(): VertexAIConfig = VertexAIConfig(
            projectId = System.getenv("VERTEX_PROJECT_ID") ?: "",
            location = System.getenv("VERTEX_LOCATION") ?: "us-central1",
            modelName = System.getenv("VERTEX_MODEL") ?: "gemini-pro"
        )
    }

    /**
     * Get the full model endpoint URL
     */
    fun getModelEndpoint(): String {
        return "https://$endpoint/v1/projects/$projectId/locations/$location/publishers/google/models/$modelName:generateContent"
    }
}

/**
 * Safety category thresholds
 */
@Serializable
data class SafetyThreshold(
    val category: String,
    val threshold: SafetyLevel = SafetyLevel.BLOCK_MEDIUM_AND_ABOVE
)

@Serializable
enum class SafetyLevel {
    BLOCK_NONE,
    BLOCK_LOW_AND_ABOVE,
    BLOCK_MEDIUM_AND_ABOVE,
    BLOCK_ONLY_HIGH
}
