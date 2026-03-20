package dev.aurakai.auraframefx.models
import kotlinx.serialization.Serializable

@Serializable
data class LockScreenConfig(
    val isEnabled: Boolean = true,
    val clockStyle: String = "Cyberpunk",
    val shortcutAlpha: Float = 1.0f
)
