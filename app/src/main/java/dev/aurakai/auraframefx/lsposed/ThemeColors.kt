package dev.aurakai.auraframefx.lsposed

import androidx.compose.ui.graphics.Color

/**
 * Data class representing the core theme colors for LSPosed implementation
 */
data class ThemeColors(
    val primary: Color = Color(0xFF6200EE),
    val primaryVariant: Color = Color(0xFF3700B3),
    val secondary: Color = Color(0xFF03DAC6),
    val secondaryVariant: Color = Color(0xFF018786),
    val accent: Color = Color(0xFF03DAC6),
    val background: Color = Color.White,
    val onBackground: Color = Color.Black
)
