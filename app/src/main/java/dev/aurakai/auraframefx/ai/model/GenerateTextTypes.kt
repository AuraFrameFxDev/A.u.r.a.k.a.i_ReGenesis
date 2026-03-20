package dev.aurakai.auraframefx.ai.model

import kotlinx.serialization.Serializable

@Serializable
data class GenerateTextRequest(
    val prompt: String,
    val agentId: String? = null,
    val temperature: Float = 0.7f,
    val maxTokens: Int = 1000
)

@Serializable
data class GenerateTextResponse(
    val text: String,
    val model: String? = null,
    val usage: Map<String, Int> = emptyMap()
)
