package dev.aurakai.auraframefx.ui

import androidx.compose.ui.graphics.Color

// === GENESIS NEON PALETTE ===
// Required for AuraFrameFX UI Components
val NeonCyan = Color(0xFF00F0FF)
val NeonBlue = Color(0xFF0077FF)
val NeonPurple = Color(0xFFBC13FE)
val NeonPink = Color(0xFFFF0099)
val NeonTeal = Color(0xFF00FF99)
val NeonPurpleDark = Color(0xFF4A0072)
val NeonGreen = Color(0xFF00FF00)
val NeonRed = Color(0xFFFF0000)
val NeonYellow = Color(0xFFFFFF00)

// Color Lists for UI Components
val SpaceColors = listOf(
    NeonCyan,
    NeonBlue,
    NeonPurple,
    NeonPink,
    NeonTeal
)

val GlassColors = listOf(
    Color(0x22FFFFFF),
    Color(0x11FFFFFF)
)

// Dark Theme Colors
val DarkBackground = Color(0xFF000000) // Pure black for depth
val Surface = Color(0xFF1A1A1A)        // Very dark grey for surfaces
val SurfaceVariant = Color(0xFF2D2D2D) // Slightly lighter for variants
val OnSurface = Color(0xFF00FFCC)      // Neon teal for main text
val OnSurfaceVariant = Color(0xFF00FFFF) // Neon cyan for variant text
val OnPrimary = Color(0xFF000000)      // Black for text on neon backgrounds
val OnSecondary = Color(0xFF000000)    // Black for secondary text
val OnTertiary = Color(0xFF000000)     // Black for tertiary text

// Light Theme Colors
val LightPrimary = Color(0xFF00FFCC)        // Neon teal
val LightOnPrimary = Color(0xFF000000)      // Black for text
val LightSecondary = Color(0xFFE000FF)      // Neon purple
val LightOnSecondary = Color(0xFF000000)    // Black for text
val LightTertiary = Color(0xFF00FFFF)       // Neon cyan
val LightOnTertiary = Color(0xFF000000)     // Black for text
val LightBackground = Color(0xFF1A1A1A)     // Dark background
val LightOnBackground = Color(0xFF00FFCC)   // Neon teal for text
val LightSurface = Color(0xFF2D2D2D)        // Surface color
val LightOnSurface = Color(0xFF00FFCC)      // Text on surface
val LightSurfaceVariant = Color(0xFF3D3D3D) // Surface variant
val LightOnSurfaceVariant = Color(0xFF00FFFF) // Text on surface variant
val LightOnError = Color(0xFF000000)        // Black for error text
val LightError = Color(0xFFFF0000)          // Red for errors

// Common Colors
val CardBackground = Color(0xFF333333)  // Card background color

// System Colors
val ErrorColor = Color(0xFFFF0000)     // Bright red for errors
val WarningColor = Color(0xFFFFA500)   // Orange for warnings
val SuccessColor = Color(0xFF00FF00)   // Bright green for success

// Accent Colors - Glowing Effects
val Accent1 = Color(0xFF00FFCC)        // Primary teal glow
val Accent2 = Color(0xFFE000FF)        // Secondary purple glow
val Accent3 = Color(0xFF00FFFF)        // Tertiary cyan glow
val Accent4 = Color(0xFFFF00FF)        // Pink glow for special elements

// Special Effects Colors
val GlowOverlay = Color(0x1A00FFCC)    // Semi-transparent teal glow
val PulseOverlay = Color(0x1AE000FF)   // Semi-transparent purple pulse
val HoverOverlay = Color(0x1A00FFFF)   // Semi-transparent cyan hover
val PressOverlay = Color(0x1AFF00FF)   // Semi-transparent pink press

// Transparency constant
val TRANSPARENT = Color(0x00000000)
