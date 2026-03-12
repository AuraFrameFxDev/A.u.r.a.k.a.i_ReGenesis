package dev.aurakai.auraframefx.domains.aura

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Animation configuration for lock screen
 */
@Serializable
data class LockScreenConfigAnimation(
    @SerialName("enabled")
    val enabled: Boolean = true,

    @SerialName("style")
    val style: String = "hologram",

    @SerialName("intensity")
    val intensity: Float = 0.7f
) {
    /**
     * Animation type enum for hook-based animation dispatch.
     */
    enum class AnimationType {
        Slide,
        Fade,
        Zoom
    }
}
