package dev.aurakai.auraframefx.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Cyberpunk Neon Color Palette
 * Used throughout the AuraFrameFX UI for the signature cyber aesthetic
 */

// Primary Neon Colors
val NeonBlue = Color(0xFF00D9FF)
val NeonCyan = Color(0xFF00FFFF)
val NeonPink = Color(0xFFFF006E)
val NeonPurple = Color(0xFFBB00FF)
val NeonGreen = Color(0xFF39FF14)
val NeonTeal = Color(0xFF00FFC8)

// Neon Variants
val NeonPurpleDark = Color(0xFF8800CC)
val NeonPurpleLegacy = Color(0xFFAA00FF)

// Theme Colors
val ThemeNeonBlue = NeonBlue

// Glass/Space Colors
object GlassColors {
    val primary = Color(0x33FFFFFF)
    val secondary = Color(0x22FFFFFF)
    val border = Color(0x44FFFFFF)
    val DarkMedium = Color(0x66000000)
    val DarkStrong = Color(0x88000000)
    val Dark = Color(0x44000000)
}

object SpaceColors {
    val deepSpace = Color(0xFF0A0E27)
    val nebula = Color(0xFF1A1F3A)
    val starlight = Color(0xFF2D3561)
    val GradientStart = Color(0xFF1A1F3A)
    val GradientEnd = Color(0xFF0A0E27)
    val Black = Color(0xFF000000)
}

// Legacy/Compatibility
val OnSurface = Color(0xFFE0E0E0)
val ErrorColor = Color(0xFFCF6679)
val Black = Color.Black

// Color Scheme
val DarkColorScheme = androidx.compose.material3.darkColorScheme(
    primary = NeonPurple,
    secondary = NeonCyan,
    tertiary = NeonPink,
    background = Color(0xFF0A0E27),
    surface = Color(0xFF1A1F3A),
    error = ErrorColor,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = OnSurface,
    onSurface = OnSurface,
    onError = Color.White
)
