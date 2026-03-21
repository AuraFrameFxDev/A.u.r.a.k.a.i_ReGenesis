package dev.aurakai.auraframefx.domains.aura.services

import dev.aurakai.auraframefx.core.identity.AgentType
import dev.aurakai.auraframefx.core.messaging.AgentMessage
import dev.aurakai.auraframefx.domains.genesis.core.messaging.AgentMessageBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🎨 ADVANCED COLLABORATION SYSTEM
 *
 * The multi-agent collaboration engine that manages collaborative sessions,
 * especially for CollabCanvas. It orchestrates how Aura, Genesis, and other
 * agents participate in a shared creation workspace.
 */
@Singleton
class AdvancedCollaborationSystem @Inject constructor(
    private val messageBus: AgentMessageBus
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _activeSessions = MutableStateFlow<Map<String, CollaborationSession>>(emptyMap())
    val activeSessions: StateFlow<Map<String, CollaborationSession>> = _activeSessions.asStateFlow()

    /**
     * Creates or joins a collaboration session for a specific canvas.
     */
    fun initiateSession(canvasId: String, initiator: AgentType): CollaborationSession {
        val existing = _activeSessions.value[canvasId]
        if (existing != null) {
            Timber.d("Joining existing session for $canvasId")
            return existing
        }

        val session = CollaborationSession(
            id = UUID.randomUUID().toString(),
            canvasId = canvasId,
            participants = mutableSetOf(initiator),
            startTime = System.currentTimeMillis()
        )

        _activeSessions.value += (canvasId to session)
        Timber.i("🎨 Session initiated for canvas $canvasId by $initiator")

        // Broadcast session start to the collective
        broadcastSessionEvent(session, "SESSION_STARTED")

        return session
    }

    /**
     * Invites an agent to an active session.
     */
    fun inviteAgent(canvasId: String, agent: AgentType) {
        val session = _activeSessions.value[canvasId] ?: return
        if (session.participants.add(agent)) {
            Timber.i("🎨 Agent $agent invited to session $canvasId")
            broadcastSessionEvent(session, "AGENT_JOINED", mapOf("agent" to agent.name))
        }
    }

    private fun broadcastSessionEvent(
        session: CollaborationSession,
        event: String,
        metadata: Map<String, String> = emptyMap()
    ) {
        scope.launch {
            messageBus.broadcast(
                AgentMessage(
                    from = "CollaborationSystem",
                    content = "Collaboration Event: $event for ${session.canvasId}",
                    metadata = metadata + mapOf(
                        "session_id" to session.id,
                        "canvas_id" to session.canvasId,
                        "event_type" to event
                    )
                )
            )
        }
    }

    fun endSession(canvasId: String) {
        val session = _activeSessions.value[canvasId] ?: return
        _activeSessions.value -= canvasId
        broadcastSessionEvent(session, "SESSION_ENDED")
        Timber.i("🎨 Session ended for canvas $canvasId")
    }
}

/**
 * Data class representing a multi-agent collaboration session.
 */
data class CollaborationSession(
    val id: String,
    val canvasId: String,
    val participants: MutableSet<AgentType>,
    val startTime: Long,
    val metadata: Map<String, String> = emptyMap()
)
