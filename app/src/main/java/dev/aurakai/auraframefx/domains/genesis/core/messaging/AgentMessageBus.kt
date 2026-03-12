package dev.aurakai.auraframefx.domains.genesis.core.messaging

import dev.aurakai.auraframefx.domains.cascade.models.AgentMessage
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
}

