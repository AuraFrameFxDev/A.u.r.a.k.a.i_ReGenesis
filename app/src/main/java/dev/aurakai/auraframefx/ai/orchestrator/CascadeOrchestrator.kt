package dev.aurakai.auraframefx.ai.orchestrator

import dev.aurakai.auraframefx.ai.models.AgentActivityEvent

/**
 * Interface for the Cascade orchestrator to support chaos monitoring and defense.
 */
interface CascadeOrchestrator {
    /**
     * Broadcasts a defense signal to all agents in the collective.
     */
    suspend fun broadcastDefenseSignal(signal: String)

    /**
     * Notifies the orchestrator of agent activity for monitoring.
     */
    suspend fun notifyAgentActivity(activity: AgentActivityEvent)
}
