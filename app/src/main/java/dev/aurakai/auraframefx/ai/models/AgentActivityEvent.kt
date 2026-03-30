package dev.aurakai.auraframefx.ai.models

/**
 * Represents an activity event for an AI agent.
 */
data class AgentActivityEvent(
    val agentName: String,
    val rawPrompt: String,
    val response: String,
    val latencyMs: Long
)
