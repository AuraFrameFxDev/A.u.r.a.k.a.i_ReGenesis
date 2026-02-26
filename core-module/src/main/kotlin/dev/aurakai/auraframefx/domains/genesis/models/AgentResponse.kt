package dev.aurakai.auraframefx.domains.genesis.models

import kotlinx.serialization.Serializable

/**
 * 📡 AGENT RESPONSE
 * The standardized response format from any agent in the ReGenesis collective.
 */
@Serializable
data class AgentResponse(
    val content: String,
    val agentName: String = "System",
    val agentType: AgentType = AgentType.GENESIS,
    val confidence: Float = 1.0f,
    val status: Status = Status.SUCCESS,
    val timestamp: Long = System.currentTimeMillis(),
    val metadata: Map<String, String> = emptyMap(),
    val error: String? = null
) {
    val isSuccess: Boolean get() = status == Status.SUCCESS

    enum class Status {
        SUCCESS, ERROR, PROCESSING, IDLE
    }

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
            status = Status.SUCCESS
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
            status = Status.ERROR,
            error = error
        )

        fun processing(
            message: String,
            agentName: String = "System",
            agentType: AgentType = AgentType.GENESIS
        ) = AgentResponse(
            content = message,
            agentName = agentName,
            agentType = agentType,
            confidence = 0.0f,
            status = Status.PROCESSING
        )
    }
}
