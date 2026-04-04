package dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.engine.manager

import androidx.compose.ui.graphics.toArgb
import dev.aurakai.auraframefx.core.theme.GlassmorphicTheme

/**
 * 🎨 SYSTEM THEME MANAGER (Xposed Bridge)
 * Provides static access to current theme colors for resource hooking.
 */
object SystemThemeManager {
    val primaryColor = GlassmorphicTheme.Primary.toArgb()
    val primaryDarkColor = GlassmorphicTheme.PrimaryVariant.toArgb()
    val accentColor = GlassmorphicTheme.Accent.toArgb()
    val primaryVariantColor = GlassmorphicTheme.PrimaryVariant.toArgb()
    val secondaryColor = GlassmorphicTheme.Secondary.toArgb()
    val secondaryVariantColor = GlassmorphicTheme.SecondaryVariant.toArgb()
    val backgroundColor = GlassmorphicTheme.Background.toArgb()
    val foregroundColor = GlassmorphicTheme.TextPrimary.toArgb()
}
