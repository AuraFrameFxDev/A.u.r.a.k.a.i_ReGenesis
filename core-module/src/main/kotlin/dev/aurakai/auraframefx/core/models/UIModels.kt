package dev.aurakai.auraframefx.core.models

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

/**
 * 🎨 AURA THEME DATA
 * Core model for theme state across ReGenesis.
 */
@Serializable
data class AuraThemeData(
    val id: String = "default",
    val name: String = "CyberGlow",
    val accentColorHex: String = "#00FFF2",
    val animationStyle: AnimationStyle = AnimationStyle.FLOWING
) {
    val accentColor: Color get() = try { 
        Color(android.graphics.Color.parseColor(accentColorHex)) 
    } catch (e: Exception) { 
        Color(0xFF00FFF2) 
    }

    enum class AnimationStyle {
        ENERGETIC, CALMING, FLOWING, PULSING, SUBTLE
    }
}

/**
 * 📍 POSITION 3D
 * Standardized coordinate system for spatial UI elements.
 */
@Serializable
data class Position3D(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f,
    val rotationX: Float = 0f,
    val rotationY: Float = 0f,
    val rotationZ: Float = 0f
)

/**
 * 🎭 OVERLAY THEME
 */
@Serializable
data class OverlayTheme(
    val name: String,
    val primaryColorHex: String,
    val secondaryColorHex: String
)

/**
 * 🎞️ OVERLAY ANIMATION
 */
@Serializable
data class OverlayAnimation(
    val type: String,
    val durationMs: Int
)

/**
 * 🔀 OVERLAY TRANSITION
 */
@Serializable
data class OverlayTransition(
    val name: String,
    val type: String
)
