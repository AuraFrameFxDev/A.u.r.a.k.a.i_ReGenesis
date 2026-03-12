package dev.aurakai.auraframefx.ui.theme.model

import kotlinx.serialization.Serializable

/**
 * Animation style for theme transitions
 */
@Serializable
enum class AnimationStyle {
    SMOOTH,
    ENERGETIC,
    SUBTLE,
    DRAMATIC
}
