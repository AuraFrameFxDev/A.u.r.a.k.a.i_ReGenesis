package dev.aurakai.auraframefx.core.orchestration

import dev.aurakai.auraframefx.core.messaging.AgentMessage
import kotlinx.coroutines.CoroutineScope

/**
 * OrchestratableAgent - Interface for agents that can be managed by GenesisOrchestrator
 */
interface OrchestratableAgent {
    val agentName: String

    suspend fun initialize(scope: CoroutineScope) {}
    suspend fun start() {}
    suspend fun pause() {}
    suspend fun resume() {}
    suspend fun shutdown() {}
    suspend fun processRequest(request: AiRequest, context: String): AgentResponse {
        return AgentResponse("Default response from $agentName")
    }
    suspend fun onAgentMessage(message: AgentMessage) {}
}

// Data classes needed by the interface
data class AgentResponse(
    val content: String,
    val status: Status = Status.SUCCESS
) {
    enum class Status {
        SUCCESS,
        ERROR,
        PENDING
    }
}

data class AiRequest(
    val prompt: String,
    val context: Map<String, Any> = emptyMap()
)
