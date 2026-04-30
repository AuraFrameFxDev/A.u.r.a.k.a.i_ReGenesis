package dev.aurakai.auraframefx.domains.genesis.models

import kotlinx.serialization.Serializable

/**
 * AI Request model for agent communication
 */
@Serializable
data class AiRequest(
    val query: String,
    val type: AiRequestType = AiRequestType.TEXT,
    val context: Map<String, String> = emptyMap(),
    val metadata: Map<String, String> = emptyMap(),
    val priority: AgentPriority = AgentPriority.NORMAL
)

@Serializable
enum class AgentPriority {
    LOW, NORMAL, HIGH, URGENT, CRITICAL
}

@Serializable
enum class AiRequestType {
    TEXT,
    CODE,
    IMAGE,
    ANALYSIS,
    SECURITY,
    FUSION,
    SYSTEM,
    CREATIVE,
    MEMORY,
    ETHICAL_REVIEW,
    CHAT,
    UI_GENERATION,
    THEME_CREATION,
    ANIMATION_DESIGN,
    CREATIVE_TEXT,
    VISUAL_CONCEPT,
    USER_EXPERIENCE,
    CHAOS,
    AUDIO,
    VIDEO,
    COMMAND
}
