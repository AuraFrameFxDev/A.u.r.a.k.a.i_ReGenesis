package dev.aurakai.auraframefx.system.lockscreen.model

import dev.aurakai.auraframefx.ui.OverlayShape
import dev.aurakai.auraframefx.ui.ImageResource
import dev.aurakai.auraframefx.ui.HapticFeedbackConfig

/**
 * Configuration for lock screen customization
 */
data class LockScreenConfig(
    val showGenesisElements: Boolean = true,
    val clockConfig: ClockConfig = ClockConfig(),
    val hapticFeedback: HapticFeedbackConfig = HapticFeedbackConfig(),
    val animation: LockScreenAnimationConfig = LockScreenAnimationConfig(),
    val elements: List<LockScreenElement> = emptyList(),
    val background: BackgroundConfig? = null
)

/**
 * Clock configuration for lock screen
 */
data class ClockConfig(
    val position: String = "center",
    val format: String = "12h",
    val showSeconds: Boolean = false,
    val fontStyle: String = "default"
)



/**
 * Animation configuration for lock screen
 */
data class LockScreenAnimationConfig(
    val type: String = "fade",
    val duration: Long = 300L,
    val enabled: Boolean = true
)

/**
 * Background configuration for lock screen
 */
data class BackgroundConfig(
    val image: ImageResource? = null,
    val blur: Float = 0f,
    val dimming: Float = 0f
)

/**
 * Individual lock screen element
 */
data class LockScreenElement(
    val type: LockScreenElementType,
    val shape: OverlayShape = OverlayShape.ROUNDED_RECTANGLE,
    val animation: LockScreenAnimation = LockScreenAnimation.NONE,
    val position: Pair<Float, Float> = 0.5f to 0.5f,
    val properties: Map<String, Any> = emptyMap()
)

/**
 * Types of lock screen elements
 */
enum class LockScreenElementType {
    CLOCK,
    WEATHER,
    NOTIFICATION,
    SHORTCUT,
    CUSTOM
}

/**
 * Animation types for lock screen elements
 */
enum class LockScreenAnimation {
    NONE,
    FADE,
    SLIDE,
    SCALE,
    BOUNCE,
    PULSE
}
