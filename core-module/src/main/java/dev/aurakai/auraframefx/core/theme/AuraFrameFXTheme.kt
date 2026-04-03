package dev.aurakai.auraframefx.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush

/**
 * 💎 GLASSMORPHIC DESIGN SYSTEM
 *
 * Professional translucent color palette for the ReGenesis UI.
 */
object GlassmorphicTheme {
    val Primary = Color(0xFF4A90E2)
    val PrimaryVariant = Color(0xFF357ABD)
    val Secondary = Color(0xFF9B7EBD)
    val SecondaryVariant = Color(0xFF7B5FA0)
    val Accent = Color(0xFF64B5F6)
    val AccentGold = Color(0xFFD4AF37)
    val Surface = Color(0xFF1E1E2E)
    val SurfaceVariant = Color(0xFF2A2A3E)
    val Background = Color(0xFF0F0F1A)

    val TextPrimary = Color(0xFFFFFFFF)
    val TextSecondary = Color(0xB3FFFFFF)

    val GlassWhite = Color(0x1AFFFFFF)
    val GlassBorder = Color(0x33FFFFFF)
    val GlassHighlight = Color(0x0DFFFFFF)

    val etherealGradient = Brush.linearGradient(
        listOf(GlassWhite, GlassHighlight)
    )
}

/**
 * Applies the AuraFrameFX color scheme to its content, selecting a dark or light palette according to `darkTheme`.
 *
 * @param darkTheme If `true`, the dark color scheme is applied; otherwise the light color scheme is applied.
 * @param content Composable UI content to be rendered within the themed Material surface.
 */
@Composable
fun AuraFrameFXTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = GlassmorphicTheme.Primary,
            secondary = GlassmorphicTheme.Secondary,
            background = GlassmorphicTheme.Background,
            surface = GlassmorphicTheme.Surface,
            onPrimary = GlassmorphicTheme.TextPrimary,
            onSecondary = GlassmorphicTheme.TextPrimary,
            onBackground = GlassmorphicTheme.TextPrimary,
            onSurface = GlassmorphicTheme.TextPrimary,
            secondaryContainer = GlassmorphicTheme.SecondaryVariant,
            surfaceVariant = GlassmorphicTheme.SurfaceVariant
        )
    } else {
        lightColorScheme(
            primary = GlassmorphicTheme.Primary,
            secondary = GlassmorphicTheme.Secondary,
            background = GlassmorphicTheme.Background,
            surface = GlassmorphicTheme.Surface,
            onPrimary = GlassmorphicTheme.TextPrimary,
            onSecondary = GlassmorphicTheme.TextPrimary,
            onBackground = GlassmorphicTheme.TextPrimary,
            onSurface = GlassmorphicTheme.TextPrimary,
            secondaryContainer = GlassmorphicTheme.SecondaryVariant,
            surfaceVariant = GlassmorphicTheme.SurfaceVariant
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
