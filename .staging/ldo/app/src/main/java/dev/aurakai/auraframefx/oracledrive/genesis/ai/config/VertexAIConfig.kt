package dev.aurakai.auraframefx.oracledrive.genesis.ai.config

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
    val enableStreaming: Boolean = false
) {
    companion object {
        fun default(): VertexAIConfig = VertexAIConfig()

        fun fromEnvironment(): VertexAIConfig = VertexAIConfig(
            projectId = System.getenv("VERTEX_PROJECT_ID") ?: "",
            location = System.getenv("VERTEX_LOCATION") ?: "us-central1",
            modelName = System.getenv("VERTEX_MODEL") ?: "gemini-pro"
        )
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
