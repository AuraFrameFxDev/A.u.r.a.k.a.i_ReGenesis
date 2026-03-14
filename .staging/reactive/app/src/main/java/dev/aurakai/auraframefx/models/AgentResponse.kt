package dev.aurakai.auraframefx.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class AgentResponse(
    val content: String,
    val confidence: Float, // Changed from isSuccess (Boolean)
    val error: String? = null, // Kept error for now
    val agentName: String? = null,
    val metadata: Map<String, String> = emptyMap(),
    val timestamp: Long = System.currentTimeMillis(),
    @Contextual val lastActivity: Any? = null,
    @Contextual val agentType: Any? = null,
    val agent: AgentType
) {
    companion object {
        fun success(
            content: String,
            confidence: Float = 1.0f,
            agentName: String? = null,
            metadata: Map<String, Any> = emptyMap(),
            agent: AgentType = AgentType.SYSTEM
        ): AgentResponse {
            return AgentResponse(
                content = content,
                confidence = confidence,
                agentName = agentName,
                metadata = metadata.mapValues { it.value.toString() },
                error = null,
                timestamp = System.currentTimeMillis(),
                lastActivity = null,
                agentType = null,
                agent = agent
            )
        }

        fun error(
            message: String,
            agentName: String? = null,
            agent: AgentType = AgentType.SYSTEM
        ): AgentResponse {
            return AgentResponse(
                content = "",
                confidence = 0.0f,
                error = message,
                agentName = agentName,
                metadata = emptyMap(),
                timestamp = System.currentTimeMillis(),
                lastActivity = null,
                agentType = null,
                agent = agent
            )
        }

        fun processing(
            message: String,
            agentName: String? = null,
            agent: AgentType = AgentType.SYSTEM
        ): AgentResponse {
            return AgentResponse(
                content = message,
                confidence = 0.5f,
                error = null,
                agentName = agentName,
                metadata = emptyMap(),
                timestamp = System.currentTimeMillis(),
                lastActivity = null,
                agentType = null,
                agent = agent
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
