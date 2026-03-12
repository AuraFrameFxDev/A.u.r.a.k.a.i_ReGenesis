package dev.aurakai.auraframefx.models

/**
 * API agent type for network communication
 */
enum class ApiAgentType {
    AURA,
    CASCADE,
    KAI,
    GENESIS
}

/**
 * Represents response types for content generation
 */
enum class ResponseType {
    TEXT,
    IMAGE,
    AUDIO,
    VIDEO,
    DATA
}

/**
 * Agent configuration for behavior
 */
data class AgentConfig(
    val type: AgentCapabilityCategory,
    val enabled: Boolean = true,
    val priority: Int = 1,
    val capabilities: List<String> = emptyList(),
    val settings: Map<String, String> = emptyMap(),
)

/**
 * Agent hierarchy data structure for organization
 */
data class AgentHierarchyData(
    val parentAgent: AgentCapabilityCategory?,
    val childAgents: List<AgentCapabilityCategory>,
    val level: Int,
)
