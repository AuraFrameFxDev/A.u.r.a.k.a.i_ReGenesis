package dev.aurakai.auraframefx.core.models

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

/**
 * 🎨 AURA THEME DATA
 * Core model for theme state across ReGenesis.
 */
@Serializable
data class AuraThemeData(
    val id: String,
    val name: String,
    val accentColorHex: String,
    val animationStyle: AnimationStyle = AnimationStyle.FLOWING
) {
    val accentColor: Color get() = Color(android.graphics.Color.parseColor(accentColorHex))

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
