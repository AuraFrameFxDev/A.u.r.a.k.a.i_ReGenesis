package dev.aurakai.auraframefx.domains.genesis.models

import kotlinx.serialization.Serializable

/**
 * Canonical AiRequestType enum — lives in :core-module because
 * AiRequest.kt (also in :core-module) depends on it.
 *
 * :app module must import from here, NOT define its own copy.
 * Any duplicate in app/AgentTypes.kt must be removed or aliased.
 */
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

    // Chat + UX/creative specializations used by :app
    CHAT,
    UI_GENERATION,
    THEME_CREATION,
    ANIMATION_DESIGN,
    CREATIVE_TEXT,
    VISUAL_CONCEPT,
    USER_EXPERIENCE
}
