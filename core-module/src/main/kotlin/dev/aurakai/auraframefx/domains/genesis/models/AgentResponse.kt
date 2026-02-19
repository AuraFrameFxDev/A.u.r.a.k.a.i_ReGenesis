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
    val category: AgentCapabilityCategory = AgentCapabilityCategory.GENERIC,
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
            category: AgentCapabilityCategory = AgentCapabilityCategory.GENERIC,
            confidence: Float = 1.0f,
            metadata: Map<String, Any> = emptyMap()
        ) = AgentResponse(
            content = content,
            agentName = agentName,
            category = category,
            confidence = confidence,
            metadata = metadata.mapValues { it.value.toString() },
            status = Status.SUCCESS
        )

        fun error(
            message: String,
            agentName: String = "System",
            category: AgentCapabilityCategory = AgentCapabilityCategory.GENERIC,
            error: String? = message
        ) = AgentResponse(
            content = message,
            agentName = agentName,
            category = category,
            confidence = 0.0f,
            status = Status.ERROR,
            error = error
        )

        fun processing(
            message: String,
            agentName: String = "System",
            category: AgentCapabilityCategory = AgentCapabilityCategory.GENERIC
        ) = AgentResponse(
            content = message,
            agentName = agentName,
            category = category,
            confidence = 0.0f,
            status = Status.PROCESSING
        )
    }
}
