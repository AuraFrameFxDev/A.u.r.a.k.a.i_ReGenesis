package dev.aurakai.auraframefx.domains.cascade.models

import kotlinx.serialization.Serializable

@Serializable
data class InteractionResponse(
    val content: String,
    val agent: String = "SYSTEM",
    val confidence: Float = 1.0f,
    val success: Boolean = true,
    val timestamp: Long = System.currentTimeMillis(),
    val metadata: Map<String, String> = emptyMap()
)
