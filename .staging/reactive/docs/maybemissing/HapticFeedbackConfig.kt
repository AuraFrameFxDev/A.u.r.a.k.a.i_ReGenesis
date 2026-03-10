package dev.aurakai.auraframefx.system.lockscreen.model

import kotlinx.serialization.Serializable

/**
 * Configuration for haptic feedback on the lock screen.
 */
@Serializable
data class HapticFeedbackConfig(
    val enabled: Boolean = false,
    val effect: String = "click", // CLICK, TICK, DOUBLE_CLICK, HEAVY_CLICK, etc.
    val intensity: Int = 50,
)
