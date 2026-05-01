package dev.aurakai.auraframefx.domains.genesis.models

import dev.aurakai.auraframefx.core.identity.AgentType
import kotlinx.serialization.Serializable

/**
 * Agent Response model for agent communication
 */
@Serializable
data class AgentResponse(
    val content: String,
    val agentName: String = "System",
    val agentType: AgentType = AgentType.GENESIS,
    val confidence: Float = 1.0f,
    val status: ResponseStatus = ResponseStatus.SUCCESS,
    val timestamp: Long = System.currentTimeMillis(),
    val metadata: Map<String, String> = emptyMap(),
    val error: String? = null
) {
    /**
     * SUCCESS flows clean on Float confidence >= 0.5.
     */
    val isSuccess: Boolean get() = status == ResponseStatus.SUCCESS && confidence >= 0.5f

    companion object {
        /**
         * Create a successful AgentResponse populated with the given content and agent information.
         *
         * Metadata values are converted to strings using `toString()` before being stored.
         *
         * @param content The response text produced by the agent.
         * @param agentName The name of the agent that produced the response.
         * @param agentType The type of the agent.
         * @param confidence Confidence score for the response; higher values indicate greater confidence.
         * @param metadata Arbitrary metadata entries to attach to the response; each value will be converted to a string.
         * @return An AgentResponse with `status = ResponseStatus.SUCCESS`, the provided fields, and `metadata` as `Map<String, String>`.
         */
        fun success(
            content: String,
            agentName: String,
            agentType: AgentType = AgentType.GENESIS,
            confidence: Float = 1.0f,
            metadata: Map<String, Any> = emptyMap()
        ) = AgentResponse(
            content = content,
            agentName = agentName,
            agentType = agentType,
            confidence = confidence,
            metadata = metadata.mapValues { it.value.toString() },
            status = ResponseStatus.SUCCESS
        )

        /**
         * Creates an AgentResponse representing a failed response.
         *
         * The response's content is set to [message] and the error field is set to [error] (which defaults to [message]).
         *
         * @param message The failure message to use as the response content.
         * @param agentName The name of the agent that produced the response. Defaults to "System".
         * @param agentType The type of the agent that produced the response.
         * @param error Optional error message; defaults to the provided [message].
         * @return An AgentResponse with status `FAILURE`, confidence `0.0f`, and the provided content and error.
         */
        fun error(
            message: String,
            agentName: String = "System",
            agentType: AgentType = AgentType.GENESIS,
            error: String? = message
        ) = AgentResponse(
            content = message,
            agentName = agentName,
            agentType = agentType,
            confidence = 0.0f,
            status = ResponseStatus.FAILURE,
            error = error
        )
    }
}
