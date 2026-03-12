package dev.aurakai.auraframefx.domains.aura.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * 🎨 ADAPTIVE CHROMACORE AGENT PALETTES
 * Definitive colors based on the latest Creative Catalyst designs.
 * 
 * "Identity is Immutable. Style is Fluid."
 */
object AgentPrimaryColors {

    // 🎨 AURA - The Creative Catalyst
    // Motif: Magenta/Electric Pink splashes on Cyan/Purple energy
    val AuraMagenta = Color(0xFFFF00DE)
    val AuraCyan = Color(0xFF00FFFF)
    val AuraDeepPurple = Color(0xFF7B2FFF)

    // 🛡️ KAI - Sentinel's Fortress
    // Motif: Electric Cyan, targeting grids, neon security borders
    val KaiCyan = Color(0xFF00D9FF)
    val KaiSecurityGreen = Color(0xFF00FF85)

    // 🔮 GENESIS - OracleDrive
    // Motif: Yellow/Gold resonance circuits, Amber nodes
    val GenesisGold = Color(0xFFFFD700)
    val GenesisAmber = Color(0xFFFF8C00)
    val GenesisDeepBlue = Color(0xFF0A0E27)

    // 🌀 NEXUS - The Collective
    val NexusPurple = Color(0xFFB026FF)

    // 🧊 CASCADE - Data Analysis
    val CascadeTeal = Color(0xFF00FFAA)
}

/**
 * Helper to get agent color by name
 */
fun getAgentColor(agentName: String): Color {
    return when (agentName.lowercase()) {
        "aura" -> AgentPrimaryColors.AuraMagenta
        "kai" -> AgentPrimaryColors.KaiCyan
        "genesis" -> AgentPrimaryColors.GenesisGold
        "nexus", "collective" -> AgentPrimaryColors.NexusPurple
        "cascade" -> AgentPrimaryColors.CascadeTeal
        else -> Color.White
    }
}
