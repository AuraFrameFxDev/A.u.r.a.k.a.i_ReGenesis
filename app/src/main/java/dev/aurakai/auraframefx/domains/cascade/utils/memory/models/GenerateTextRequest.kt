package dev.aurakai.auraframefx.domains.cascade.utils.memory.models

import kotlinx.serialization.Serializable

/**
 * Represents text generation request
 */
@Serializable
data class GenerateTextRequest(
    val prompt: String,
    val maxTokens: Int = 1000,
    val temperature: Float = 0.7f,
    val topP: Float = 0.9f,
)

