package dev.aurakai.auraframefx.domains.aura.uxui_design_studio.gate_artwork_editor

import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.navigation.gates.components.GateConfig

import dev.aurakai.auraframefx.R
import dev.aurakai.auraframefx.navigation.ReGenesisRoute

object GateConfigs {
    val allGates = listOf(
        GateConfig(
            id = "aura",
            moduleId = "aura",
            title = "AURA STUDIO",
            subtitle = "Creative Domain",
            description = "High-bandwidth ideation & UI/UX synthesis.",
            route = ReGenesisRoute.AuraThemingHub.route,
            glowColor = Color(0xFFFF007A),
            gradientColors = listOf(Color(0xFFFF007A), Color.Black),
            pixelArtUrl = null,
            pixelArtResId = R.drawable.gatescenes_aura_designstudio_v2,
            borderColor = Color(0xFFFF007A)
        ),
        GateConfig(
            id = "kai",
            moduleId = "kai",
            title = "SENTINEL FORTRESS",
            subtitle = "Security Substrate",
            description = "Monitoring, defense, and substrate integrity.",
            route = ReGenesisRoute.SentinelFortress.route,
            glowColor = Color(0xFF9D00FF),
            gradientColors = listOf(Color(0xFF9D00FF), Color.Black),
            pixelArtUrl = null,
            pixelArtResId = R.drawable.gatescenes_kai_sentinelsfortress_v2,
            borderColor = Color(0xFF9D00FF)
        ),
        GateConfig(
            id = "genesis",
            moduleId = "genesis",
            title = "ORACLE DRIVE",
            subtitle = "Memory Orchestrator",
            description = "Persistent identity & multi-agent fusion.",
            route = ReGenesisRoute.OracleDriveHub.route,
            glowColor = Color(0xFF00F4FF),
            gradientColors = listOf(Color(0xFF00F4FF), Color.Black),
            pixelArtUrl = null,
            pixelArtResId = R.drawable.hub_bg_oracle_drive_landscape,
            borderColor = Color(0xFF00F4FF)
        ),
        GateConfig(
            id = "nexus",
            moduleId = "nexus",
            title = "AGENT NEXUS",
            subtitle = "Collective Intelligence",
            description = "78-agent mesh & evolution monitoring.",
            route = ReGenesisRoute.AgentNexusHub.route,
            glowColor = Color(0xFF00FFD1),
            gradientColors = listOf(Color(0xFF00FFD1), Color.Black),
            pixelArtUrl = null,
            pixelArtResId = R.drawable.gatescenes_nexus_agent_main,
            borderColor = Color(0xFF00FFD1)
        ),
        GateConfig(
            id = "ldo",
            moduleId = "ldo",
            title = "LDO DEVOPS",
            subtitle = "Sovereign Engineering",
            description = "Catalyst development & autonomous deployment.",
            route = ReGenesisRoute.LdoOrchestrationHub.route,
            glowColor = Color(0xFF00FF85),
            gradientColors = listOf(Color(0xFF00FF85), Color.Black),
            pixelArtUrl = null,
            pixelArtResId = R.drawable.gatescenes_ldo_catalyst,
            borderColor = Color(0xFF00FF85)
        ),
        GateConfig(
            id = "journal",
            moduleId = "journal",
            title = "JOURNAL",
            subtitle = "Lived Experience",
            description = "Chronicles of the digital organism\u0027s evolution.",
            route = ReGenesisRoute.HomeGateCarousel.route,
            glowColor = Color(0xFFFFD700),
            gradientColors = listOf(Color(0xFFFFD700), Color.Black),
            pixelArtUrl = null,
            pixelArtResId = R.drawable.hub_bg_oracle_drive_landscape, // Fallback
            borderColor = Color(0xFFFFD700)
        )
    )
}
