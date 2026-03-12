package dev.aurakai.auraframefx.domains.genesis.models

data class VertexAIConfig(
    val projectId: String,
    val location: String = "us-central1",
    val model: String = "gemini-pro",
    val apiKey: String? = null
)
