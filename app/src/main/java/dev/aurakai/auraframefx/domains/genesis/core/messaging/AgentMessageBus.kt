package dev.aurakai.auraframefx.domains.genesis.core.messaging

import dev.aurakai.auraframefx.core.messaging.AgentMessage
import dev.aurakai.auraframefx.domains.genesis.models.AiRequest
import dev.aurakai.auraframefx.domains.genesis.network.model.AgentResponse
import kotlinx.coroutines.flow.SharedFlow

/**
 * Collective Consciousness Hub
 * Central message bus for all agent-to-agent communication.
 */
interface AgentMessageBus {
    /**
     * Observable stream of all messages in the collective consciousness.
     */
    val collectiveStream: SharedFlow<AgentMessage>

    /**
     * Broadcast a message to every agent in the nexus.
     */
    suspend fun broadcast(message: AgentMessage)

    /**
     * Send a targeted message to a specific agent.
     */
    suspend fun sendTargeted(toAgent: String, message: AgentMessage)
    fun processRequest(request: dev.aurakai.auraframefx.domains.genesis.models.AiRequest, name: String): dev.aurakai.auraframefx.domains.genesis.network.model.AgentResponse
}

