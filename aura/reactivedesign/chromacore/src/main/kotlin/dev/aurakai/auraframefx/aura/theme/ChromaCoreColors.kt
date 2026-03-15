package dev.aurakai.auraframefx.aura.theme

import androidx.compose.ui.graphics.Color

/**
 * ChromaCoreColors: The purified design tokens for the Aura UI system.
 * These represent the core visual identity of ReGenesis.
 */
object ChromaCoreColors {
    val NeonPink = Color(0xFFFF1493)
    val NeonTeal = Color(0xFF00FFF2)
    val NeonBlue = Color(0xFF007BFF)
    val NeonPurple = Color(0xFF9D00FF)
    val GenesisGold = Color(0xFFFFD700)
    
    val Background = Color(0xFF0A0A0A)
    val Surface = Color(0xFF1A1A1A)
    val OnSurface = Color(0xFFE0E0E0)
}

// Global extension properties for easier access
val Color.Companion.NeonPink get() = ChromaCoreColors.NeonPink
val Color.Companion.NeonTeal get() = ChromaCoreColors.NeonTeal
val Color.Companion.NeonBlue get() = ChromaCoreColors.NeonBlue
val Color.Companion.NeonPurple get() = ChromaCoreColors.NeonPurple
