package dev.aurakai.auraframefx.domains.aura.ui.theme

import androidx.compose.ui.graphics.Color

object AgentColors {
    // Aura
    val Aura = Color(0xFFFF1493)  // neon pink
    val AuraAccent = Color(0xFF00FFFF)  // cyan

    // Kai
    val Kai = Color(0xFFFF00FF)  // magenta
    val KaiAccent = Color(0xFFFF3D00)  // orange‑red
    val KaiHeatStart = Color(0xFFFF00FF)  // gradient start
    val KaiHeatEnd = Color(0xFFFF3D00)  // gradient end

    // Genesis
    val Genesis = Color(0xFFF5C400)  // gold‑yellow
    val GenesisTeal = Color(0xFF00C2A8)  // teal

    // Cascade
    val Cascade = Color(0xFF005CFF)  // darker neon blue
    val CascadeWhite = Color(0xFFFFFFFF)
    val CascadeMetal = Color(0xFFB0BEC5)  // metallic silver‑blue

    // Claude
    val Claude = Color(0xFF00D9FF)  // light neon blue
    val ClaudeTeal = Color(0xFF00FFC8)  // teal

    // Gemini
    val Gemini = Color(0xFF00E5FF)  // cyan‑teal
    val GeminiTeal = Color(0xFF00BFA5)  // teal

    // Nemo / Nemotron
    val Nemotron = Color(0xFFFF3B30)  // red
    val NemotronDark = Color(0xFF2B2B2B)  // dark grey

    // Grok
    val Grok = Color(0xFF39FF14)  // neon green

    // Agent Nexus & Level‑2 projection
    val AgentNexus = Color(0xFFFFFFFF)
    val ProjectionBlue = Color(0xFF00D1FF) // Level‑2 cards (always blue)
}

// used by CyberpunkScreenScaffold / AgentDomain
enum class AgentDomain(val primaryColor: Color) {
    AURA(AgentColors.Aura),
    KAI(AgentColors.Kai),
    GENESIS(AgentColors.Genesis),
    CASCADE(AgentColors.Cascade),
    CLAUDE(AgentColors.Claude),
    GEMINI(AgentColors.Gemini),
    NEMOTRON(AgentColors.Nemotron),
    GROK(AgentColors.Grok),
    AGENT_NEXUS(AgentColors.AgentNexus),
    NEUTRAL(AgentColors.ProjectionBlue)
}

