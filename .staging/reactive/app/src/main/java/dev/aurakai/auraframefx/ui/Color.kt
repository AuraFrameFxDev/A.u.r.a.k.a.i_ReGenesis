package dev.aurakai.auraframefx.ui

import androidx.compose.ui.graphics.Color

// Primary Colors - Enhanced Neon Palette
// NOTE: NeonTeal, NeonPurple, NeonPink, NeonCyan moved to Theme.kt for consistency
// Provide deprecated legacy aliases that reference the theme values to avoid
// duplicate simple-name imports and keep backward compatibility.

import dev.aurakai.auraframefx.ui.theme.NeonBlue as ThemeNeonBlue
import dev.aurakai.auraframefx.ui.theme.NeonPurple as ThemeNeonPurple
import dev.aurakai.auraframefx.ui.theme.NeonPink as ThemeNeonPink
import dev.aurakai.auraframefx.ui.theme.NeonCyan as ThemeNeonCyan

@Deprecated("Use dev.aurakai.auraframefx.ui.theme.NeonPurple instead", level = DeprecationLevel.WARNING)
val NeonPurpleLegacy: Color = ThemeNeonPurple

// Legacy NeonBlue in root `ui` package. Prefer `dev.aurakai.auraframefx.ui.theme.NeonBlue`.
@Deprecated("Use dev.aurakai.auraframefx.ui.theme.NeonBlue instead", level = DeprecationLevel.WARNING)
val NeonBlueLegacy: Color = ThemeNeonBlue // Bright cyan for highlights

// Keep a small set of package-local legacy names (distinct) to avoid duplicate/simple-name collisions
@Deprecated("Use dev.aurakai.auraframefx.ui.theme.NeonPink instead", level = DeprecationLevel.WARNING)
val NeonPinkLegacy: Color = ThemeNeonPink

@Deprecated("Use dev.aurakai.auraframefx.ui.theme.NeonCyan instead", level = DeprecationLevel.WARNING)
val NeonCyanLegacy: Color = ThemeNeonCyan

// Other local colors remain defined here (these are not moved to theme)
val NeonGreen = Color(0xFF00FF00) // Bright neon green
val NeonRed = Color(0xFFFF0000)   // Bright neon red
val NeonYellow = Color(0xFFFFFF00) // Bright yellow

// Dark Theme Colors
val DarkBackground = Color(0xFF000000) // Pure black for depth
val Surface = Color(0xFF1A1A1A) // Very dark grey for surfaces
val SurfaceVariant = Color(0xFF2D2D2D) // Slightly lighter for variants
val OnSurface = Color(0xFF00FFCC) // Neon teal for main text
val OnSurfaceVariant = Color(0xFF00FFFF) // Neon cyan for variant text
val OnPrimary = Color(0xFF000000) // Black for text on neon backgrounds
val OnSecondary = Color(0xFF000000) // Black for secondary text
val OnTertiary = Color(0xFF000000) // Black for tertiary text

// Light Theme Colors
val LightPrimary = Color(0xFF00FFCC) // Neon teal
val LightOnPrimary = Color(0xFF000000) // Black for text
val LightSecondary = Color(0xFFE000FF) // Neon purple
val LightOnSecondary = Color(0xFF000000) // Black for text
val LightTertiary = Color(0xFF00FFFF) // Neon cyan
val LightOnTertiary = Color(0xFF000000) // Black for text
val LightBackground = Color(0xFF1A1A1A) // Dark background
val LightOnBackground = Color(0xFF00FFCC) // Neon teal for text
val LightSurface = Color(0xFF2D2D2D) // Surface color
val LightOnSurface = Color(0xFF00FFCC) // Text on surface
val LightSurfaceVariant = Color(0xFF3D3D3D) // Surface variant
val LightOnSurfaceVariant = Color(0xFF00FFFF) // Text on surface variant
val LightOnError = Color(0xFF000000) // Black for error text
val LightError = Color(0xFFFF0000) // Red for errors

// Common Colors
val CardBackground = Color(0xFF333333) // Card background color

// System Colors
val ErrorColor = Color(0xFFFF0000) // Bright red for errors
val WarningColor = Color(0xFFFFA500) // Orange for warnings
val SuccessColor = Color(0xFF00FF00) // Bright green for success

// Accent Colors - Glowing Effects
val Accent1 = Color(0xFF00FFCC) // Primary teal glow
val Accent2 = Color(0xFFE000FF) // Secondary purple glow
val Accent3 = Color(0xFF00FFFF) // Tertiary cyan glow
val Accent4 = Color(0xFFFF00FF) // Pink glow for special elements

// Special Effects Colors
val GlowOverlay = Color(0x1A00FFCC) // Semi-transparent teal glow
val PulseOverlay = Color(0x1AE000FF) // Semi-transparent purple pulse
val HoverOverlay = Color(0x1A00FFFF) // Semi-transparent cyan hover
val PressOverlay = Color(0x1AFF00FF) // Semi-transparent pink press
