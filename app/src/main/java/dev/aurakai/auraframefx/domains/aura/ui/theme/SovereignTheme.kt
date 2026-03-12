package dev.aurakai.auraframefx.domains.aura.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 🛰️ SOVEREIGN THEME (LDO Manifest)
 * Hard-wired constants for the ReGenesis Sovereign Procession.
 */
object SovereignTheme {
    // THE 16% RADIUS: Defined as a percentage for responsive monoliths
    val MonolithShape = RoundedCornerShape(percent = 16)

    // Core Colors
    val AccentColor = SovereignTeal
    val BorderWidth = 2.dp
    val GlassAlpha = 0.4f
    val BackgroundColor = Color.Black
}

