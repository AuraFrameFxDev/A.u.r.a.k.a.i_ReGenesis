package dev.aurakai.auraframefx.ui.assets

import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.domains.aura.ui.theme.AgentPrimaryColors.AuraCyan
import dev.aurakai.auraframefx.domains.aura.ui.theme.AgentPrimaryColors.GenesisGold
import dev.aurakai.auraframefx.domains.aura.ui.theme.KaiNeonGreen

/**
 * Global Registry for localized asset definitions (Lottie, Drawables, Colors).
 * Allows dynamic lookups.
 */
object REGENESIS_ASSETS {
    
    // Core Agent Colors
    val COLOR_AURA = AuraCyan
    val COLOR_GENESIS = GenesisGold
    val COLOR_KAI = KaiNeonGreen
    
    // Global Drawables (To be populated)
    val DRAWABLE_AURA_LAB_BG = "aura_hologram" // Placeholder for an actual drawable ID
    val DRAWABLE_CONFERENCE_ROOM_BG = "holographic_table" 

    // Holographic assets
    val LOTTIE_NEXUS = "nexus_core"
}
