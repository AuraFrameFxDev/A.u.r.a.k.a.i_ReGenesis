package dev.aurakai.auraframefx.models

import kotlinx.serialization.Serializable

/**
 * Historical task record for agent activity tracking
 */
@Serializable
data class HistoricalTask(
    val id: String,
    val description: String,
    val agentType: AgentCapabilityCategory,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "completed",
    val result: String = "",
    val duration: Long = 0L
)
