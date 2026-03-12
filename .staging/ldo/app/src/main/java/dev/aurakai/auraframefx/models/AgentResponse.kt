package dev.aurakai.auraframefx.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * Response from an AI agent
 */
@Serializable
data class AgentResponse(
    val agentName: String = "",
    val response: String = "",
    val content: String = response, // Alias for response
    val confidence: Float = 0f,
    val timestamp: Long = System.currentTimeMillis(),
    @Contextual
    val metadata: Map<String, @Contextual Any> = emptyMap(),
    val error: String? = null,
    val agent: AgentType? = null,
    val isSuccess: Boolean = error == null,
    val isError: Boolean = error != null,
    val status: String = if (error == null) "success" else "error"
) {
    companion object {
        /**
         * Create a success response
         */
        fun success(
            content: String,
            agentName: String = "",
            confidence: Float = 1.0f,
            metadata: Map<String, Any> = emptyMap(),
            agent: AgentType? = null
        ): AgentResponse = AgentResponse(
            agentName = agentName,
            response = content,
            content = content,
            confidence = confidence,
            metadata = metadata,
            agent = agent,
            isSuccess = true,
            status = "success"
        )

        /**
         * Create an error response
         */
        fun error(
            message: String,
            agentName: String = "",
            agent: AgentType? = null
        ): AgentResponse = AgentResponse(
            agentName = agentName,
            response = "",
            content = "",
            confidence = 0f,
            error = message,
            agent = agent,
            isSuccess = false,
            isError = true,
            status = "error"
        )

        /**
         * Create a processing/in-progress response
         */
        fun processing(
            message: String = "Processing...",
            agentName: String = ""
        ): AgentResponse = AgentResponse(
            agentName = agentName,
            response = message,
            content = message,
            confidence = 0f,
            status = "processing"
        )

        /**
         * Create an empty/default response
         */
        fun empty(): AgentResponse = AgentResponse()
    }
}
