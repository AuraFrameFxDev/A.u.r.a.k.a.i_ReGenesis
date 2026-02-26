package dev.aurakai.auraframefx.domains.aura.aura.animations

import kotlinx.serialization.Serializable

@Serializable
data class HomeScreenTransitionEffectsConfig(
    val defaultOutgoingEffect: HomeScreenTransitionEffect? = null,
    val defaultIncomingEffect: HomeScreenTransitionEffect? = null,
)

@Serializable
data class HomeScreenTransitionEffect(
    val type: String, // e.g., "slide", "fade", "zoom"
    val properties: TransitionProperties? = null,
)

@Serializable
data class TransitionProperties(
    val duration: Long = 300L,
    val direction: String? = null, // e.g., "left_to_right", "top_to_bottom" for slide
    val interpolator: String = "linear",
    // Add other relevant properties like scaleFactor for zoom, etc.
)

// Enums like HomeScreenTransitionType do not need @Serializable as per user instructions.
// If this file previously contained other classes or interfaces, they should be preserved.
