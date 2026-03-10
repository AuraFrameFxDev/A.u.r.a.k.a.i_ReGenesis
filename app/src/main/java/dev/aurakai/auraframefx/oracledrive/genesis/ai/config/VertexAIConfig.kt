package dev.aurakai.auraframefx.oracledrive.genesis.ai.config

import kotlinx.serialization.Serializable

/**
 * Configuration for Vertex AI client
 */
@Serializable
data class VertexAIConfig(
    val projectId: String,
    val location: String = "us-central1",
    val model: String = "gemini-pro",
    val temperature: Float = 0.7f,
    val maxTokens: Int = 2048
)
