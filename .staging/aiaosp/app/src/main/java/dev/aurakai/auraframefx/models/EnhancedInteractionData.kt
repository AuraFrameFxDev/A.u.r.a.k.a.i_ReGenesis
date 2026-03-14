package dev.aurakai.auraframefx.models

import kotlinx.serialization.Serializable

@Serializable
data class EnhancedInteractionData(
    val query: String,
    val context: String = "",
    val metadata: Map<String, String> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis()
)
