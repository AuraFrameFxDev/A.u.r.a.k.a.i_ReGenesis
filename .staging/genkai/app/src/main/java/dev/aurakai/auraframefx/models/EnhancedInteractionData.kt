package dev.aurakai.auraframefx.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject

@Serializable
data class EnhancedInteractionData(
    val query: String = "",
    val context: JsonObject = buildJsonObject { },
    val metadata: JsonObject = buildJsonObject { },
    val timestamp: Long = System.currentTimeMillis()
)
