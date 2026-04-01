package dev.aurakai.auraframefx.domains.genesis.core

import dev.aurakai.auraframefx.core.identity.AgentType
import dev.aurakai.auraframefx.core.messaging.AgentMessage
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 📡 COMMUNICATION SYSTEM (CONFERENCE ROOM BUS)
 *
 * Provides a high-throughput, channel-based message bus for agent-to-agent
 * and agent-to-user communication within the ReGenesis Ecosystem.
 */
@Singleton
class CommunicationSystem @Inject constructor() {

    private val tag = "CommunicationSystem"

    // Unified message flow for the entire Resonance
    private val _resonanceFlow = MutableSharedFlow<AgentMessage>(
        replay = 50,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val resonanceFlow: SharedFlow<AgentMessage> = _resonanceFlow.asSharedFlow()

    /**
     * Broadcasts a message to the Resonance.
     */
    suspend fun broadcast(message: AgentMessage) {
        Timber.tag(tag).d("Broadcast: [${message.from}] -> ${message.content.take(50)}...")
        _resonanceFlow.emit(message)
    }

    /**
     * Helper to create and broadcast a system message.
     */
    suspend fun systemBroadcast(content: String) {
        val msg = AgentMessage(
            id = java.util.UUID.randomUUID().toString(),
            from = "SYSTEM",
            to = "ALL",
            content = content,
            timestamp = System.currentTimeMillis()
        )
        broadcast(msg)
    }

    /**
     * Filters the Resonance for specific agent frequencies.
     */
    fun observeFrequency(agentType: AgentType): SharedFlow<AgentMessage> {
        // In a real implementation, we would return a filtered flow
        return resonanceFlow
    }
}
