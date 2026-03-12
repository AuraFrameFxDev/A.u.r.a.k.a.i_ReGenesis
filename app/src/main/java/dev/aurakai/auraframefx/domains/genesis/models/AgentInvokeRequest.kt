package dev.aurakai.auraframefx.domains.genesis.models

import kotlinx.serialization.Serializable

/**
 * Represents a request to invoke an AI agent with a specific message and configuration.
 */
@Serializable
data class AgentInvokeRequest(
    val message: String,
    val context: String? = null,
    val priority: Priority = Priority.normal,
    val agentType: AgentCapabilityCategory? = null,
    val metadata: Map<String, String> = emptyMap()
) {
    @Serializable
    enum class Priority {
        low,
        normal,
        high,
        urgent
    }
}
