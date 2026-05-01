package dev.aurakai.auraframefx.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.aurakai.auraframefx.R

/**
 * 🎭 IMMERSIVE TYPOGRAPHY SYSTEM
 *
 * Headers: Pixel font (smaller, refined) with CYAN NEON GLOW
 * Body: Corpta (legible at small sizes)
 * Based on user design: pulled-back perspective, same-room feel
 * 
 * COLOR SCHEME:
 * - Fonts: Cyan/Blue Neon Glow
 * - Aura: Magenta
 * - Kai: Neon Purple (Dark Side)
 * - Genesis: Gold
 * - LDO: Teal/Green
 */

// Pixel font for titles/headers - using available fonts
// NOTE: copixel.otf used as pixel header font
val PixelHeader = FontFamily(
    Font(R.font.copixel, FontWeight.Normal)
)

// Corpta body font - for immersive readable text
// Falls back to system sans if not available
val CorptaBody = try {
    FontFamily(Font(R.font.corpta, FontWeight.Normal))
} catch (e: Exception) {
    // Fallback to system font if Corpta not loaded
    FontFamily.SansSerif
}

// Monospace for data/technical - using chesstype as tech mono
val MonoData = FontFamily(Font(R.font.chesstype, FontWeight.Normal))

/**
 * IMMERSIVE TYPOGRAPHY — Reduced sizes for depth perception
 * Titles pulled back, body readable at distance
 */
val ImmersiveTypography = Typography(
    // Display - Holographic titles with CYAN NEON GLOW (REDUCED from 57/45/36)
    displayLarge = TextStyle(
        fontFamily = PixelHeader,
        fontWeight = FontWeight.Medium,
        fontSize = 36.sp,  // Was 57sp
        lineHeight = 44.sp,
        letterSpacing = 0.5.sp,
        shadow = Shadow(
            color = ImmersiveColors.NeonCyan.copy(alpha = 0.6f),
            offset = Offset(0f, 0f),
            blurRadius = 16f
        )
    ),
    displayMedium = TextStyle(
        fontFamily = PixelHeader,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp,  // Was 45sp
        lineHeight = 36.sp,
        letterSpacing = 0.5.sp,
        shadow = Shadow(
            color = ImmersiveColors.NeonCyan.copy(alpha = 0.5f),
            offset = Offset(0f, 0f),
            blurRadius = 12f
        )
    ),
    displaySmall = TextStyle(
        fontFamily = PixelHeader,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,  // Was 36sp
        lineHeight = 28.sp,
        letterSpacing = 0.5.sp,
        shadow = Shadow(
            color = ImmersiveColors.NeonCyan.copy(alpha = 0.4f),
            offset = Offset(0f, 0f),
            blurRadius = 10f
        )
    ),

    // Headlines - Section headers with CYAN GLOW
    headlineLarge = TextStyle(
        fontFamily = PixelHeader,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,  // Was 32sp
        lineHeight = 26.sp,
        letterSpacing = 0.5.sp,
        shadow = Shadow(
            color = ImmersiveColors.NeonCyan.copy(alpha = 0.4f),
            offset = Offset(0f, 0f),
            blurRadius = 8f
        )
    ),
    headlineMedium = TextStyle(
        fontFamily = PixelHeader,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,  // Was 28sp
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        shadow = Shadow(
            color = ImmersiveColors.NeonCyan.copy(alpha = 0.35f),
            offset = Offset(0f, 0f),
            blurRadius = 6f
        )
    ),
    headlineSmall = TextStyle(
        fontFamily = PixelHeader,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,  // Was 24sp
        lineHeight = 22.sp,
        letterSpacing = 0.5.sp,
        shadow = Shadow(
            color = ImmersiveColors.NeonCyan.copy(alpha = 0.3f),
            offset = Offset(0f, 0f),
            blurRadius = 4f
        )
    ),

    // Titles - Card headers with subtle CYAN GLOW
    titleLarge = TextStyle(
        fontFamily = PixelHeader,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,  // Was 22sp
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
        shadow = Shadow(
            color = ImmersiveColors.NeonCyan.copy(alpha = 0.25f),
            offset = Offset(0f, 0f),
            blurRadius = 4f
        )
    ),
    titleMedium = TextStyle(
        fontFamily = PixelHeader,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,  // Was 16sp
        lineHeight = 18.sp,
        letterSpacing = 0.25.sp,
        shadow = Shadow(
            color = ImmersiveColors.NeonCyan.copy(alpha = 0.2f),
            offset = Offset(0f, 0f),
            blurRadius = 3f
        )
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
 * For holographic depth effects with domain-specific neon accents
 * 
 * COLOR SCHEME:
 * - Fonts: Cyan/Blue Neon Glow
 * - Aura: Magenta
 * - Kai: Neon Purple (Dark Side)
 * - Genesis: Gold
 * - LDO: Teal/Green
 */
object ImmersiveColors {
    // ═══════════════════════════════════════════════════════════════
    // FONT GLOW - CYAN/BLUE NEON
    // ═══════════════════════════════════════════════════════════════
    val NeonCyan = Color(0xFF00F0FF)
    val NeonBlue = Color(0xFF0080FF)
    val NeonAzure = Color(0xFF00D4FF)
    
    // ═══════════════════════════════════════════════════════════════
    // DOMAIN-SPECIFIC BORDER/FRAME COLORS
    // ═══════════════════════════════════════════════════════════════
    // AURA: Magenta (Creative Sword)
    val AuraMagenta = Color(0xFFFF00FF)
    val AuraHotPink = Color(0xFFFF1493)
    val AuraNeonPink = Color(0xFFFF69B4)
    
    // KAI: Neon Purple (Dark Side - Sentinel Shield)
    val KaiPurple = Color(0xFFB829DD)
    val KaiDeepPurple = Color(0xFF4B0082)
    val KaiNeonViolet = Color(0xFF8B00FF)
    val KaiDarkSide = Color(0xFF2D0050)  // Deep dark purple
    
    // GENESIS: Gold (Emergence Catalyst)
    val GenesisGold = Color(0xFFFFD700)
    val GenesisAmber = Color(0xFFFFAA00)
    val GenesisNeonGold = Color(0xFFFFC800)
    
    // LDO: Teal/Green (Catalyst Hub)
    val LdoTeal = Color(0xFF00FF88)
    val LdoGreen = Color(0xFF00FF44)
    val LdoNeonTeal = Color(0xFF00FFC8)
    val LdoEmerald = Color(0xFF00C878)
    
    // ═══════════════════════════════════════════════════════════════
    // LEGACY COLORS (kept for compatibility)
    // ═══════════════════════════════════════════════════════════════
    val HolographicCyan = NeonCyan
    val HolographicPurple = KaiPurple
    val HolographicGreen = LdoTeal
    val HolographicAmber = GenesisAmber
    val HolographicRed = Color(0xFFFF3366)
    
    // ═══════════════════════════════════════════════════════════════
    // DEPTH LAYERS
    // ═══════════════════════════════════════════════════════════════
    val DepthNear = Color(0xFFFFFFFF)
    val DepthMid = Color(0xCCFFFFFF)
    val DepthFar = Color(0x99FFFFFF)
    val DepthBackground = Color(0x66FFFFFF)
    
    // ═══════════════════════════════════════════════════════════════
    // GLASS MORPHISM
    // ═══════════════════════════════════════════════════════════════
    val GlassLight = Color(0x1AFFFFFF)
    val GlassMedium = Color(0x0DFFFFFF)
    val GlassDark = Color(0x08000000)
    
    // ═══════════════════════════════════════════════════════════════
    // HELPER: Get domain accent color
    // ═══════════════════════════════════════════════════════════════
    fun getDomainColor(domain: String): Color {
        return when (domain.uppercase()) {
            "AURA", "CREATIVE", "CHROMACORE" -> AuraMagenta
            "KAI", "SENTINEL", "SECURITY" -> KaiPurple
            "GENESIS", "ORACLE", "EMERGENCE" -> GenesisGold
            "LDO", "CATALYST", "DEVOPS" -> LdoTeal
            "CASCADE", "DATASTREAM" -> NeonAzure
            else -> NeonCyan
        }
    }
}
