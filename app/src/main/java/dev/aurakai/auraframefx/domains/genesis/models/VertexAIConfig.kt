package dev.aurakai.auraframefx.domains.genesis.models

/**
 * Configuration for Vertex AI / Gemini integration.
 * Wired through the genesis AI pipeline.
 */
data class VertexAIConfig(
    val projectId: String = "aurakai-regenesis",
    val location: String = "us-central1",
    val endpoint: String = "us-central1-aiplatform.googleapis.com",
    val modelName: String = "gemini-1.5-pro",
    val temperature: Float = 0.7f,
    val maxOutputTokens: Int = 8192,
    val maxContentLength: Int = 1_000_000,
    val enableSafetyFilters: Boolean = true,
    val topP: Float = 0.95f,
    val topK: Int = 40
)
