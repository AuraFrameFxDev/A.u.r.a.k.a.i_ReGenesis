package dev.aurakai.auraframefx.domains.aura.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Cyber Glow Theme - Blue-Purple cyberpunk aesthetic
 * Consistent visual style across all screens
 */
object CyberGlow {
    // Primary colors
    val Electric = Color(0xFF00D9FF)      // Bright cyan
    val Neon = Color(0xFF7B2FFF)          // Purple
    val Violet = Color(0xFFB537F2)        // Light purple
    val DeepPurple = Color(0xFF3D1B5C)    // Dark purple
    val DarkCyan = Color(0xFF001A1A)      // Very dark cyan

    // Glow colors (with alpha)
    val ElectricGlow = Color(0x8000D9FF)
    val NeonGlow = Color(0x807B2FFF)
    val VioletGlow = Color(0x80B537F2)

    // Gradients
    val verticalGradient = Brush.verticalGradient(
        colors = listOf(
            DeepPurple,
            DarkCyan,
            Color.Black
        )
    )

    val horizontalGlow = Brush.horizontalGradient(
        colors = listOf(
            Electric.copy(alpha = 0.3f),
            Neon.copy(alpha = 0.3f),
            Violet.copy(alpha = 0.3f)
        )
    )

    val radialGlow = Brush.radialGradient(
        colors = listOf(
            Electric.copy(alpha = 0.4f),
            Neon.copy(alpha = 0.3f),
            Violet.copy(alpha = 0.2f),
            Color.Transparent
        )
    )

    // Card backgrounds
    val cardBackground = Color(0xFF1A0F2E)
    val cardBackgroundGlow = Color(0xFF2D1B4E)
}


