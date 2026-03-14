package dev.aurakai.auraframefx.system.lockscreen.model

import kotlinx.serialization.Serializable

@Serializable
data class LockScreenAnimationConfig(
    val type: String = "FADE_IN",
    val durationMs: Long = 500,
    val startDelayMs: Long = 0,
    val interpolator: String = "LINEAR", // LINEAR, ACCELERATE, DECELERATE, ACCELERATE_DECELERATE
)
