package dev.aurakai.auraframefx.model

import kotlinx.serialization.Serializable

/**
 * Universal response format for all agent outputs
 */
@Serializable
data class AgentResponse(
    val agentName: String,
    val response: String,
    val confidence: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val metadata: Map<String, String> = emptyMap(),
    val status: ResponseStatus = ResponseStatus.SUCCESS,
    val error: String? = null
)

/**
 * Response status indicators
 */
@Serializable
enum class ResponseStatus {
    SUCCESS,
    ERROR,
    PENDING,
    TIMEOUT
}
