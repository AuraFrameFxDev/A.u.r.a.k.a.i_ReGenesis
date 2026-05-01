package dev.aurakai.auraframefx.core.orchestration

import dev.aurakai.auraframefx.core.messaging.AgentMessage
import kotlinx.coroutines.CoroutineScope

/**
 * OrchestratableAgent - Interface for agents that can be managed by GenesisOrchestrator
 */
interface OrchestratableAgent {
    val agentName: String

    /**
 * Prepares the agent for operation and provides a CoroutineScope for launching lifecycle coroutines.
 *
 * @param scope Scope the agent should use to launch coroutines tied to its lifecycle.
 */
suspend fun initialize(scope: CoroutineScope) {}
    /**
 * Starts the agent's active work or processing.
 *
 * Override to perform any startup or background tasks required when the agent becomes active. The default implementation is empty.
 */
suspend fun start() {}
    /**
 * Pauses the agent's operation.
 *
 * Implementers may override to suspend or pause ongoing work; the default implementation does nothing.
 */
suspend fun pause() {}
    /**
 * Resumes the agent's operation after a pause.
 *
 * Default implementation is a no-op; override to restore the agent's active behavior.
 */
suspend fun resume() {}
    /**
 * Signals the agent to shut down and release any held resources.
 *
 * Implementations should override to stop ongoing work, cancel coroutines, and perform cleanup.
 * The default implementation does nothing.
 */
suspend fun shutdown() {}
    /**
     * Processes an AI request within the agent's orchestration context and produces an agent response.
     *
     * The default implementation returns a simple textual reply that references the agent's name.
     *
     * @param request The incoming AI request containing the prompt and optional context map.
     * @param context A string identifying the conversational or execution context for this request.
     * @return An AgentResponse containing the agent's reply text and a status (defaults to `Status.SUCCESS`).
     */
    suspend fun processRequest(request: AiRequest, context: String): AgentResponse {
        return AgentResponse("Default response from $agentName")
    }
    /**
 * Hook invoked when the agent receives an inbound message.
 *
 * Implementations may handle the provided `AgentMessage` to update state, trigger actions, or forward it.
 *
 * @param message The incoming message delivered to the agent.
 */
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
