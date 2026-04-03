package dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.themes

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import dev.aurakai.auraframefx.domains.aura.ui.theme.GlassmorphicTheme

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
