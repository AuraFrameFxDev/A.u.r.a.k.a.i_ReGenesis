package dev.aurakai.auraframefx.domains.aura.models

import kotlinx.serialization.Serializable

/**
 * Represents different emotional states for AI agents
 */
@Serializable
enum class Emotion {
    NEUTRAL,
    HAPPY,
    SAD,
    EXCITED,
    CURIOUS,
    CONFUSED,
    FOCUSED,
    RELAXED,
    ANXIOUS,
    CONFIDENT,
    PLAYFUL,
    SERIOUS,
    EMPATHETIC,
    ANALYTICAL,
    SERENE,
    CONTEMPLATIVE,
    MISCHIEVOUS,
    MYSTERIOUS,
    MELANCHOLIC,
    ANGRY,
    SURPRISED,
    FEARFUL,
    DISGUSTED,
    CALM;

    companion object {
        // Provide common emotion constants for compatibility
        val DEFAULT = NEUTRAL
    }
}
