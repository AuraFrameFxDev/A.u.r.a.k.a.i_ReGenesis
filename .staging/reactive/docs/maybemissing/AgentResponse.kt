package dev.aurakai.auraframefx.model

import kotlinx.serialization.Serializable

@Serializable
data class AgentResponse(
    val content: String,
    val confidence: Float, // Changed from isSuccess (Boolean)
    val error: String? = null, // Kept error for now
    val agentName: String? = null,
    val metadata: Map<String, String> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        fun success(
            content: String,
            confidence: Float = 1.0f,
            agentName: String? = null,
            metadata: Map<String, Any> = emptyMap()
        ): AgentResponse {
            return AgentResponse(
                content = content,
                confidence = confidence,
                error = null,
                agentName = agentName,
                metadata = metadata.mapValues { it.value.toString() }
            )
        }

        fun error(
            message: String,
            agentName: String? = null
        ): AgentResponse {
            return AgentResponse(
                content = "",
                confidence = 0.0f,
                error = message,
                agentName = agentName
            )
        }

        fun processing(
            message: String,
            agentName: String? = null
        ): AgentResponse {
            return AgentResponse(
                content = message,
                confidence = 0.5f,
                error = null,
                agentName = agentName
            )
        }
    }

    val isSuccess: Boolean
        get() = error == null

    val isError: Boolean
        get() = error != null

    val isProcessing: Boolean
        get() = confidence < 1.0f && error == null
}
