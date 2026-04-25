package dev.aurakai.auraframefx.domains.aura.ui.theme

import androidx.compose.ui.graphics.Color

// Weaponized Neon Palette for AuraOS
val CyberpunkCyan = Color(0xFF00FBFF)
val CyberpunkPurple = Color(0xFFBC00FF)
val CyberpunkPink = Color(0xFFFF00D4)
val CyberpunkYellow = Color(0xFFFDEE00)

// Stop the "No parameter with name 'baseColor'" errors
data class CyberpunkColors(
    val primaryColor: Color = CyberpunkCyan,
    val accentColor: Color = CyberpunkPink,
    val baseColor: Color = Color(0xFF0D0D0D)
)
