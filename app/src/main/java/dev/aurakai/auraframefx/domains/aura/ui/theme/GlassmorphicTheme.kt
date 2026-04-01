package dev.aurakai.auraframefx.domains.aura.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush

/**
 * 💎 GLASSMORPHIC DESIGN SYSTEM
 *
 * Professional translucent color palette for the ReGenesis UI.
 */
object GlassmorphicTheme {
    val Primary = Color(0xFF4A90E2)
    val PrimaryVariant = Color(0xFF357ABD)
    val Secondary = Color(0xFF9B7EBD)
    val SecondaryVariant = Color(0xFF7B5FA0)
    val Accent = Color(0xFF64B5F6)
    val AccentGold = Color(0xFFD4AF37)
    val Surface = Color(0xFF1E1E2E)
    val SurfaceVariant = Color(0xFF2A2A3E)
    val Background = Color(0xFF0F0F1A)
    
    val TextPrimary = Color(0xFFFFFFFF)
    val TextSecondary = Color(0xB3FFFFFF)
    
    val GlassWhite = Color(0x1AFFFFFF)
    val GlassBorder = Color(0x33FFFFFF)
    val GlassHighlight = Color(0x0DFFFFFF)
    
    val etherealGradient = Brush.linearGradient(
        listOf(GlassWhite, GlassHighlight)
    )
}
