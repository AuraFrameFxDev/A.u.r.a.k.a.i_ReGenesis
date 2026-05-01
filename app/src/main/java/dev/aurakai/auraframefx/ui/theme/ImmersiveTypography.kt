package dev.aurakai.auraframefx.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.R

/**
 * 🎭 IMMERSIVE TYPOGRAPHY SYSTEM
 *
 * Headers: Pixel font (smaller, refined)
 * Body: Corpta (legible at small sizes)
 * Based on user design: pulled-back perspective, same-room feel
 */

// Pixel font for titles/headers - REDUCED sizes
val PixelHeader = FontFamily(
    Font(R.font.pixelifysans_medium, FontWeight.Medium),
    Font(R.font.pixelifysans_bold, FontWeight.Bold)
)

// Corpta body font - for immersive readable text
// Falls back to system sans if not available
val CorptaBody = try {
    FontFamily(
        Font(R.font.corpta_regular, FontWeight.Normal),
        Font(R.font.corpta_medium, FontWeight.Medium),
        Font(R.font.corpta_bold, FontWeight.Bold)
    )
} catch (e: Exception) {
    // Fallback to system font if Corpta not loaded
    FontFamily.SansSerif
}

// Monospace for data/technical
val MonoData = FontFamily(
    Font(R.font.jetbrains_mono_regular, FontWeight.Normal),
    Font(R.font.jetbrains_mono_medium, FontWeight.Medium)
)

/**
 * IMMERSIVE TYPOGRAPHY — Reduced sizes for depth perception
 * Titles pulled back, body readable at distance
 */
val ImmersiveTypography = Typography(
    // Display - Holographic titles (REDUCED from 57/45/36)
    displayLarge = TextStyle(
        fontFamily = PixelHeader,
        fontWeight = FontWeight.Medium,
        fontSize = 36.sp,  // Was 57sp
        lineHeight = 44.sp,
        letterSpacing = 0.5.sp
    ),
    displayMedium = TextStyle(
        fontFamily = PixelHeader,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp,  // Was 45sp
        lineHeight = 36.sp,
        letterSpacing = 0.5.sp
    ),
    displaySmall = TextStyle(
        fontFamily = PixelHeader,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,  // Was 36sp
        lineHeight = 28.sp,
        letterSpacing = 0.5.sp
    ),

    // Headlines - Section headers
    headlineLarge = TextStyle(
        fontFamily = PixelHeader,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,  // Was 32sp
        lineHeight = 26.sp,
        letterSpacing = 0.5.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = PixelHeader,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,  // Was 28sp
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = PixelHeader,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,  // Was 24sp
        lineHeight = 22.sp,
        letterSpacing = 0.5.sp
    ),

    // Titles - Card headers
    titleLarge = TextStyle(
        fontFamily = PixelHeader,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,  // Was 22sp
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    titleMedium = TextStyle(
        fontFamily = PixelHeader,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,  // Was 16sp
        lineHeight = 18.sp,
        letterSpacing = 0.25.sp
    ),
    titleSmall = TextStyle(
        fontFamily = CorptaBody,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,  // Was 14sp
        lineHeight = 16.sp,
        letterSpacing = 0.15.sp
    ),

    // Body - Corpta for readability
    bodyLarge = TextStyle(
        fontFamily = CorptaBody,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,  // Was 16sp
        lineHeight = 19.sp,
        letterSpacing = 0.25.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = CorptaBody,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,  // Was 14sp
        lineHeight = 17.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = CorptaBody,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,  // Was 12sp
        lineHeight = 15.sp,
        letterSpacing = 0.25.sp
    ),

    // Labels - Small data
    labelLarge = TextStyle(
        fontFamily = MonoData,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,  // Was 14sp
        lineHeight = 16.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = MonoData,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,  // Was 12sp
        lineHeight = 15.sp,
        letterSpacing = 0.1.sp
    ),
    labelSmall = TextStyle(
        fontFamily = MonoData,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,  // Was 11sp
        lineHeight = 14.sp,
        letterSpacing = 0.1.sp
    )
)

/**
 * IMMERSIVE UI DIMENSIONS
 * For 3D depth perspective and pulled-back feel
 */
object ImmersiveDimensions {
    // Card elevation for depth
    val CardElevationNear = 8.dp      // Foreground elements
    val CardElevationMid = 4.dp       // Mid-ground
    val CardElevationFar = 1.dp       // Background

    // Spacing for same-room feel
    val SpacerTight = 4.dp
    val SpacerStandard = 8.dp
    val SpacerRelaxed = 12.dp
    val SpacerGenerous = 16.dp

    // Padding for card content
    val CardPadding = 12.dp
    val CardPaddingCompact = 8.dp

    // Corner radius
    val CardCornerSmall = 8.dp
    val CardCornerStandard = 12.dp
    val CardCornerLarge = 16.dp

    // Glow intensity
    val GlowIntensitySubtle = 0.15f
    val GlowIntensityStandard = 0.25f
    val GlowIntensityStrong = 0.4f
}

/**
 * IMMERSIVE COLORS
 * For holographic depth effects
 */
object ImmersiveColors {
    val HolographicCyan = Color(0xFF00F0FF)
    val HolographicPurple = Color(0xFFB829DD)
    val HolographicGreen = Color(0xFF00FF88)
    val HolographicAmber = Color(0xFFFFAA00)
    val HolographicRed = Color(0xFFFF3366)

    val DepthNear = Color(0xFFFFFFFF)
    val DepthMid = Color(0xCCFFFFFF)
    val DepthFar = Color(0x99FFFFFF)
    val DepthBackground = Color(0x66FFFFFF)

    val GlassLight = Color(0x1AFFFFFF)
    val GlassMedium = Color(0x0DFFFFFF)
    val GlassDark = Color(0x08000000)
}
