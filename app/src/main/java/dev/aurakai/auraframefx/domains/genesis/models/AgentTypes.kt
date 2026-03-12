package dev.aurakai.auraframefx.domains.genesis.models

/**
 * API agent type for network communication
 */
enum class ApiAgentType {
    AURA,
    CASCADE,
    KAI,
    GENESIS
}

// AgentCapabilityCategory is defined in core-module

/**
 * Types of AI requests for routing and processing
 */
enum class AiRequestType {
    TEXT,
    IMAGE,
    CREATIVE,
    SECURITY,
    COLLABORATIVE,
    SYSTEM,
    ANALYSIS,
    UI_GENERATION,
    THEME_CREATION,
    ANIMATION_DESIGN,
    CREATIVE_TEXT,
    VISUAL_CONCEPT,
    USER_EXPERIENCE,
    SECURITY_ANALYSIS,
    THREAT_ASSESSMENT,
    PERFORMANCE_ANALYSIS,
    CODE_REVIEW,
    SYSTEM_OPTIMIZATION,
    VULNERABILITY_SCAN,
    COMPLIANCE_CHECK,
    CHAT,
    NEURAL_WHISPER,
    SYNC_HEARTBEAT,
    AURA_SYNC,
    ARCHITECTURAL,
    REASONING,
    PATTERN,
    TECHNICAL,
    QUESTION,
    AUDIO,
    ACTION
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
