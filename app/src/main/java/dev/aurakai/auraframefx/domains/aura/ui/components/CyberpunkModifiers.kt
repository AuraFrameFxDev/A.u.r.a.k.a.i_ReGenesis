package dev.aurakai.auraframefx.domains.aura.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.core.theme.*

/**
 * Cyberpunk-themed modifier extensions for creating digital effects
 */

fun Modifier.cyberEdgeGlow(
    primaryColor: Color = Color.NeonBlue,
    secondaryColor: Color = Color.NeonBlue
) = this
    .shadow(
        elevation = 8.dp,
        shape = RoundedCornerShape(4.dp),
        ambientColor = primaryColor,
        spotColor = secondaryColor
    )
    .border(
        width = 1.dp,
        color = primaryColor.copy(alpha = 0.6f),
        shape = RoundedCornerShape(4.dp)
    )

fun Modifier.digitalGlitchEffect() = this
    .shadow(
        elevation = 4.dp,
        shape = RoundedCornerShape(2.dp),
        ambientColor = Color.NeonPurple,
        spotColor = Color.NeonPurple
    )
    .border(
        width = 2.dp,
        color = Color.NeonPurple.copy(alpha = 0.8f),
        shape = RoundedCornerShape(2.dp)
    )

fun Modifier.digitalPixelEffect() = this
    .shadow(
        elevation = 6.dp,
        shape = RoundedCornerShape(1.dp),
        ambientColor = Color.NeonTeal,
        spotColor = Color.NeonTeal
    )
    .border(
        width = 1.dp,
        color = Color.NeonTeal.copy(alpha = 0.7f),
        shape = RoundedCornerShape(1.dp)
    )

fun Modifier.digitalPixelEffect(visible: Boolean) = if (visible) {
    this.digitalPixelEffect()
} else {
    this
}

enum class CornerStyle {
    ROUNDED,
    SHARP,
    HEXAGON,
    ANGLED
}

enum class BackgroundStyle {
    SOLID,
    GRADIENT,
    GLITCH,
    MATRIX,
    HEX_GRID,
    TRANSPARENT
}
