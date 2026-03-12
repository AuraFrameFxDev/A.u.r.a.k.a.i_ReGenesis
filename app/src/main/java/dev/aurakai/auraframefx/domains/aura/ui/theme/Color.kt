package dev.aurakai.auraframefx.domains.aura.ui.theme

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
val NeonRed = Color(0xFFFF003C)
val NeonYellow = Color(0xFFEAFF00)
val SovereignTeal = Color(0xFF00FFFF)

// Specific Branding Colors
val GenesisNeonPink = Color(0xFFFF00DE)
val AuraNeonCyan = Color(0xFF00FFFB)
// KaiNeonGreen is defined in KaiColor.kt
// NeonTeal is defined in primary neon colors above

// Neon Variants
val NeonPurpleDark = Color(0xFF8800CC)
val NeonPurpleLegacy = Color(0xFFAA00FF)

// Theme Colors
val ThemeNeonBlue = NeonBlue

// Dark Theme Colors
val DarkBackground = Color(0xFF0D0D15)
val OnPrimary = Color.White
val OnSecondary = Color.White
val OnTertiary = Color.White
val OnSurface = Color(0xFFE0E0E0)
val OnSurfaceVariant = Color(0xFFB0B0C0)
val Surface = Color(0xFF121220)
val SurfaceVariant = Color(0xFF1E1E30)
val ErrorColor = Color(0xFFCF6679)

// Light Theme Colors
val LightPrimary = Color(0xFF007AFF)
val LightOnPrimary = Color.White
val LightSecondary = Color(0xFF5856D6)
val LightOnSecondary = Color.White
val LightTertiary = Color(0xFFFF2D55)
val LightOnTertiary = Color.White
val LightBackground = Color(0xFFF5F5F7)
val LightOnBackground = Color(0xFF1C1C1E)
val LightSurface = Color.White
val LightOnSurface = Color(0xFF1C1C1E)
val LightSurfaceVariant = Color(0xFFE5E5EA)
val LightOnSurfaceVariant = Color(0xFF3A3A3C)
val LightOnError = Color.White

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

