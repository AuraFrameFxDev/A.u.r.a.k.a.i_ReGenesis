package dev.aurakai.auraframefx.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject

@Serializable
data class InteractionResponse(
    val content: String = "",
    val success: Boolean = true,
    val metadata: JsonObject = buildJsonObject { },
    val timestamp: Long = System.currentTimeMillis()
)
