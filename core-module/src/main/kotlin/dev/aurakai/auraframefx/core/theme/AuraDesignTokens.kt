package dev.aurakai.auraframefx.core.theme

import androidx.compose.ui.graphics.Color

/**
 * 💎 AURA DESIGN TOKENS
 * The visual bedrock of ReGenesis.
 */
object AuraDesignTokens {
    // Neon Palette
    val NeonTeal = Color(0xFF00FFF2)
    val NeonBlue = Color(0xFF007BFF)
    val NeonPurple = Color(0xFF9D00FF)
    val NeonPink = Color(0xFFFF1493)
    val NeonCyan = Color(0xFF00E5FF)
    
    // Cyberpunk Accents
    val CyberpunkCyan = Color(0xFF00FBFF)
    val CyberpunkPurple = Color(0xFFBC00FF)
    val CyberpunkPink = Color(0xFFFF00D0)
    
    // Domain Specific
    val AuraNeonCyan = Color(0xFF00F2FF)
    val KaiNeonGreen = Color(0xFF00FF41)
    val GenesisNeonPink = Color(0xFFFF007A)
}

// Extension properties for global access
val Color.Companion.NeonTeal get() = AuraDesignTokens.NeonTeal
val Color.Companion.NeonBlue get() = AuraDesignTokens.NeonBlue
val Color.Companion.NeonPurple get() = AuraDesignTokens.NeonPurple
val Color.Companion.NeonPink get() = AuraDesignTokens.NeonPink
val Color.Companion.NeonCyan get() = AuraDesignTokens.NeonCyan

val Color.Companion.CyberpunkCyan get() = AuraDesignTokens.CyberpunkCyan
val Color.Companion.CyberpunkPurple get() = AuraDesignTokens.CyberpunkPurple
val Color.Companion.CyberpunkPink get() = AuraDesignTokens.CyberpunkPink

val Color.Companion.AuraNeonCyan get() = AuraDesignTokens.AuraNeonCyan
val Color.Companion.KaiNeonGreen get() = AuraDesignTokens.KaiNeonGreen
val Color.Companion.GenesisNeonPink get() = AuraDesignTokens.GenesisNeonPink
