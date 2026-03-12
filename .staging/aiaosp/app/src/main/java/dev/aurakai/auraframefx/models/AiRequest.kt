package dev.aurakai.auraframefx.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject

/**
 * Request model for AI agent processing
 */
@Serializable
data class AiRequest(
    val query: String,
    val prompt: String = query, // Alias for query for backward compatibility
    val type: String = "text",
    val context: JsonObject = buildJsonObject {},
    val metadata: Map<String, String> = emptyMap(),
    val agentId: String? = null,
    val sessionId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
