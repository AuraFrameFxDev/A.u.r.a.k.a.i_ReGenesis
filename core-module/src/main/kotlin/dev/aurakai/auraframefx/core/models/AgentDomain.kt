package dev.aurakai.auraframefx.core.models

import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.core.theme.*

/**
 * 🌐 AGENT DOMAIN
 * Represents the primary functional domains in the ReGenesis ecosystem.
 */
enum class AgentDomain(val primaryColor: Color) {
    AURA(Color.AuraNeonCyan),
    KAI(Color.KaiNeonGreen),
    GENESIS(Color.GenesisNeonPink),
    CASCADE(Color(0xFF7B2FFF)),
    NEXUS(Color(0xFF00E5FF))
}
