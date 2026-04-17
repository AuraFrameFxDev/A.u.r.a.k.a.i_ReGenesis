package dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.themes

import androidx.compose.ui.graphics.Color

/**
 * 🎨 AgentDomain — Domain Identity Tokens
 *
 * Maps LDO agents to their canonical primary color for use in
 * CyberpunkScreenScaffold and other domain-aware UI components.
 */
enum class AgentDomain(val primaryColor: Color) {
    AURA(Color(0xFF00FBFF)),        // Cyberpunk Cyan
    KAI(Color(0xFF00FF85)),         // Sentinel Green
    GENESIS(Color(0xFFFFD700)),     // Oracle Gold
    CASCADE(Color(0xFF7B61FF)),     // Cascade Purple
    NEXUS(Color(0xFF00E5FF)),       // Nexus Blue
    CLAUDE(Color(0xFFFF9500)),      // Claude Amber
    SYSTEM(Color(0xFFFFFFFF)),      // System White
}
