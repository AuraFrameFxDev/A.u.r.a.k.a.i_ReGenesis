package dev.aurakai.auraframefx.domains.aura.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import dev.aurakai.auraframefx.domains.aura.ui.theme.NeonBlue
import dev.aurakai.auraframefx.domains.aura.ui.theme.NeonPurple
import dev.aurakai.auraframefx.domains.aura.ui.theme.NeonTeal

/**
 * Cyberpunk-themed modifier extensions for creating digital effects
 */

/**
 * Applies a cyberpunk-themed neon blue edge glow effect to the modifier.
 *
 * Adds a shadow with 8.dp elevation and a rounded 4.dp corner, along with a semi-transparent neon blue border.
 * Use to give UI elements a distinctive cyber edge highlight.
 */
/**
 * Applies a neon blue edge glow effect to the component.
 *
 * Adds a shadow and a semi-transparent neon blue border with rounded corners for a cyberpunk-inspired appearance.
 */
/**
 * Applies a neon blue edge glow effect with a soft shadow and semi-transparent border to the component.
 *
 * Creates a cyberpunk-inspired appearance using a rounded shadow and border in neon blue.
 */
fun Modifier.cyberEdgeGlow(
    primaryColor: androidx.compose.ui.graphics.Color = NeonBlue,
    secondaryColor: androidx.compose.ui.graphics.Color = NeonBlue
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

/**
 * Applies a digital glitch effect with neon purple shadow and border to the modifier.
 *
 * Creates a cyberpunk-inspired appearance using a 4.dp elevation shadow and a 2.dp border with rounded corners in neon purple.
 */
fun Modifier.digitalGlitchEffect() = this
    .shadow(
        elevation = 4.dp,
        shape = RoundedCornerShape(2.dp),
        ambientColor = NeonPurple,
        spotColor = NeonPurple
    )
    .border(
        width = 2.dp,
        color = NeonPurple.copy(alpha = 0.8f),
        shape = RoundedCornerShape(2.dp)
    )

/**
 * Applies a pixelated cyberpunk effect with a neon teal shadow and border.
 *
 * Creates a digital pixel visual style by adding a 6.dp shadow and a 1.dp border with slightly rounded corners, using NeonTeal color at 70% opacity.
 */
fun Modifier.digitalPixelEffect() = this
    .shadow(
        elevation = 6.dp,
        shape = RoundedCornerShape(1.dp),
        ambientColor = NeonTeal,
        spotColor = NeonTeal
    )
    .border(
        width = 1.dp,
        color = NeonTeal.copy(alpha = 0.7f),
        shape = RoundedCornerShape(1.dp)
    )

/**
 * Conditionally applies a pixelated cyberpunk effect based on the visible parameter.
 *
 * @param visible If true, applies the digital pixel effect; otherwise returns the modifier unchanged.
 */
fun Modifier.digitalPixelEffect(visible: Boolean) = if (visible) {
    this.digitalPixelEffect()
} else {
    this
}

enum class CornerStyle {
    ROUNDED,
    SHARP,
    HEXAGON,
    ANGLED  // Added for HomeScreen compatibility
}

enum class BackgroundStyle {
    SOLID,
    GRADIENT,
    GLITCH,
    MATRIX,
    HEX_GRID,      // Added for HomeScreen compatibility
    TRANSPARENT    // Added for HomeScreen compatibility
}

