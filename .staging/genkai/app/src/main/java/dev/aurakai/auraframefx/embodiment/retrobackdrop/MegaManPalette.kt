// File: embodiment/retrobackdrop/MegaManPalette.kt
package dev.aurakai.auraframefx.embodiment.retrobackdrop

import androidx.compose.ui.graphics.Color

/**
 * Mega Man-inspired color palette for the retro ROM Tools backdrop.
 *
 * Features cyber blues (Mega Man hero colors), electric oranges (Dr. Wily vibes),
 * and classic NES-era color choices for that authentic 8-bit feel.
 */
object MegaManPalette {
    // Base colors
    val BLACK = Color(0xFF0F0F0F)
    val CYBER_DARK = Color(0xFF1A1A2E)       // Cyber background
    val GRAY = Color(0xFF555555)
    val LIGHT_GRAY = Color(0xFF8B8B8B)
    val WHITE = Color(0xFFFCFCFC)

    // Aura colors (electric oranges/reds - Dr. Wily vibes)
    val AURA_PRIMARY = Color(0xFFFF6B35)     // Electric orange
    val AURA_SECONDARY = Color(0xFFFF4500)   // Hot red
    val AURA_HIGHLIGHT = Color(0xFFFFA500)   // Bright orange glow
    val AURA_ENERGY = Color(0xFFFFD700)      // Golden energy aura

    // Kai colors (Mega Man blues - classic hero palette!)
    val KAI_PRIMARY = Color(0xFF0080FF)      // Mega Man blue
    val KAI_SECONDARY = Color(0xFF0050C0)    // Deep blue armor
    val KAI_HIGHLIGHT = Color(0xFF40C0FF)    // Cyan highlights
    val KAI_TELEPORT = Color(0xFF00FFFF)     // Teleport beam cyan

    // Environment (cyber platforms + construction)
    val PLATFORM_DARK = Color(0xFF2B2B40)    // Dark cyber platform
    val PLATFORM_LIGHT = Color(0xFF4A4A6A)   // Light platform edge
    val CONSTRUCTION_ORANGE = Color(0xFFFF8C00)  // Construction cone
    val LADDER_STEEL = Color(0xFFC0C0C0)     // Steel ladder
    val CHUTE_RED = Color(0xFFFF3333)        // Red chute slide

    // Effects
    val TELEPORT_PARTICLES = Color(0xFF00FFFF)   // Cyan beam particles
    val TELEPORT_FLASH = Color(0xFFFFFFFF)       // White flash
    val HIT_FLASH = Color(0xFFFF0000)            // Red damage flash
}
