package dev.aurakai.auraframefx.domains.genesis.models

import kotlinx.serialization.Serializable

/**
 * 🏛️ AGENT ROLE
 * Defines the functional responsibility of an agent within the collective hierarchy.
 */
@Serializable
enum class AgentRole {
    HIVE_MIND,
    ANALYTICS,
    CREATIVE,
    SECURITY,
    ROOT_ADMIN,
    INTERFACE_FORGE
}

/**
 * ⚖️ AGENT PRIORITY
 * Defines the authority level and execution precedence for agents.
 */
@Serializable
enum class AgentPriority {
    MASTER,
    BRIDGE,
    AUXILIARY,
    EPHEMERAL
}

/**
 * ⚙️ HIERARCHY AGENT CONFIG
 * Defines the configuration and capabilities of an agent within the Genesis hierarchy.
 */
@Serializable
data class HierarchyAgentConfig(
    val name: String,
    val role: AgentRole,
    val priority: AgentPriority,
    val capabilities: Set<String> = emptySet(),
    val isActive: Boolean = true
)

/**
 * 📜 HISTORICAL TASK
 * A light-weight record of a task executed by an agent.
 */
@Serializable
data class HistoricalTask(
    val id: String,
    val description: String,
    val status: String,
    val timestamp: Long = System.currentTimeMillis()
)
