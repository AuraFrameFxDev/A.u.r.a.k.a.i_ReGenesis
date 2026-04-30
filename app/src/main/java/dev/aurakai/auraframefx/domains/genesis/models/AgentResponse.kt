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
