package dev.aurakai.auraframefx.domains.aura.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 💎 Professional Glassmorphism Utilities
 *
 * Reusable modifiers for creating beautiful glass effects with neon accents
 */

/**
 * Helper to create color with alpha (since Color.copy doesn't exist in all Compose versions)
 */
private fun Color.withAlpha(alpha: Float): Color = Color(
    red = this.red,
    green = this.green,
    blue = this.blue,
    alpha = alpha
)

/**
 * Creates a glassmorphic surface with blur and transparency
 *
 * @param glassColor Background glass color with transparency
 * @param borderColor Optional border color for neon outline
 * @param borderWidth Border width (default 1.dp)
 * @param cornerRadius Corner radius (default 16.dp)
 * @param blurRadius Blur intensity (default 8.dp)
 */
fun Modifier.glassmorphic(
    glassColor: Color = GlassColors.DarkMedium,
    borderColor: Color? = NeonPurple.withAlpha(0.4f),
    borderWidth: Dp = 1.dp,
    cornerRadius: Dp = 16.dp,
    blurRadius: Dp = 8.dp
) = this.composed {
    val shape = RoundedCornerShape(cornerRadius)

    this
        .clip(shape)
        .background(glassColor)
        .then(
            if (borderColor != null) {
                Modifier.border(borderWidth, borderColor, shape)
            } else {
                Modifier
            }
        )
        .blur(blurRadius)
}

/**
 * Creates a glassmorphic card with neon glow border
 *
 * @param glowColor Neon glow color (default: purple)
 * @param cornerRadius Corner radius (default 16.dp)
 */
fun Modifier.glassCard(
    glowColor: Color = NeonPurple,
    cornerRadius: Dp = 16.dp
) = this.glassmorphic(
    glassColor = GlassColors.DarkMedium,
    borderColor = glowColor.withAlpha(0.5f),
    borderWidth = 2.dp,
    cornerRadius = cornerRadius,
    blurRadius = 12.dp
)

/**
 * Creates a strong glassmorphic background
 *
 * @param tint Optional color tint for the glass
 * @param cornerRadius Corner radius (default 20.dp)
 */
fun Modifier.glassBackground(
    tint: Color? = null,
    cornerRadius: Dp = 20.dp
) = this.glassmorphic(
    glassColor = tint?.withAlpha(0.3f) ?: GlassColors.DarkStrong,
    borderColor = tint?.withAlpha(0.2f),
    borderWidth = 1.dp,
    cornerRadius = cornerRadius,
    blurRadius = 16.dp
)

/**
 * Creates a subtle glass overlay for layering
 */
fun Modifier.glassOverlay(
    opacity: Float = 0.1f
) = this.composed {
    background(Color.White.withAlpha(opacity))
}

/**
 * Creates a neon gradient background
 *
 * @param startColor Gradient start color
 * @param endColor Gradient end color
 * @param angle Gradient angle (default: 45 degrees)
 */
fun Modifier.neonGradient(
    startColor: Color = NeonPurple,
    endColor: Color = NeonTeal,
    angle: Float = 45f
) = this.composed {
    background(
        brush = Brush.linearGradient(
            colors = listOf(startColor, endColor),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
        )
    )
}

/**
 * Creates a space-themed background with gradient
 */
fun Modifier.spaceBackground() = this.composed {
    background(
        brush = Brush.verticalGradient(
            colors = listOf(
                SpaceColors.GradientStart,
                SpaceColors.GradientEnd,
                SpaceColors.Black
            )
        )
    )
}

/**
 * Creates a neon glow border effect
 *
 * @param glowColor Neon color for the glow
 * @param glowWidth Glow border width (default 2.dp)
 * @param cornerRadius Corner radius (default 12.dp)
 */
fun Modifier.neonGlow(
    glowColor: Color = NeonPurple,
    glowWidth: Dp = 2.dp,
    cornerRadius: Dp = 12.dp
) = this.composed {
    val shape = RoundedCornerShape(cornerRadius)
    this
        .clip(shape)
        .border(glowWidth, glowColor, shape)
}

/**
 * Creates a pulsating neon glow effect (for attention-grabbing elements)
 *
 * @param glowColor Neon color for the glow
 * @param cornerRadius Corner radius (default 12.dp)
 */
fun Modifier.neonPulse(
    glowColor: Color = NeonPink,
    cornerRadius: Dp = 12.dp
) = this.composed {
    // TODO: Add animated pulsating glow effect
    // For now, static glow
    neonGlow(glowColor, 3.dp, cornerRadius)
}

/**
 * 🎨 Preset glassmorphic styles
 */
object GlassPresets {
    /**
     * Agent card style - glassmorphic with agent-specific glow
     */
    fun Modifier.agentCard(agentColor: Color = NeonPurple) = this.glassmorphic(
        glassColor = GlassColors.DarkMedium,
        borderColor = agentColor.withAlpha(0.6f),
        borderWidth = 2.dp,
        cornerRadius = 20.dp,
        blurRadius = 10.dp
    )

    /**
     * Modal/Dialog style - strong glass with neon outline
     */
    fun Modifier.modal() = this.glassmorphic(
        glassColor = GlassColors.DarkStrong,
        borderColor = NeonPurple.withAlpha(0.8f),
        borderWidth = 3.dp,
        cornerRadius = 24.dp,
        blurRadius = 20.dp
    )

    /**
     * Button style - subtle glass with glow
     */
    fun Modifier.button(glowColor: Color = NeonTeal) = this.glassmorphic(
        glassColor = glowColor.withAlpha(0.2f),
        borderColor = glowColor.withAlpha(0.7f),
        borderWidth = 2.dp,
        cornerRadius = 12.dp,
        blurRadius = 6.dp
    )

    /**
     * Navigation bar style - horizontal glass bar
     */
    fun Modifier.navBar() = this.glassmorphic(
        glassColor = GlassColors.DarkStrong,
        borderColor = NeonPurple.withAlpha(0.3f),
        borderWidth = 1.dp,
        cornerRadius = 0.dp,
        blurRadius = 16.dp
    )

    /**
     * Input field style - subtle glass with teal accent
     */
    fun Modifier.inputField() = this.glassmorphic(
        glassColor = GlassColors.Dark,
        borderColor = NeonTeal.withAlpha(0.5f),
        borderWidth = 1.dp,
        cornerRadius = 8.dp,
        blurRadius = 4.dp
    )
}

