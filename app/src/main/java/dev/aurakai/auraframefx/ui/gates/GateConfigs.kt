package dev.aurakai.auraframefx.ui.gates

import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.R
import dev.aurakai.auraframefx.navigation.ReGenesisNavHost
import dev.aurakai.auraframefx.navigation.gates.components.GateConfig

object GateConfigs {
    val allGates: List<GateConfig> = listOf(

        // Gate 01 — UXUI Design Studio (Aura)
        GateConfig(
            id = "uxui_design_studio",
            moduleId = "uxui_design_studio",
            title = "UXUI Design Studio",
            subtitle = "The Face",
            description = "Theming, colors, icons & visual design",
            route = ReGenesisNavHost.AuraThemingHub.route,
            glowColor = Color(0xFF00E5FF),
            gradientColors = listOf(Color(0xFF00E5FF), Color.Black),
            pixelArtUrl = null,
            borderColor = Color(0xFF00E5FF),
            pixelArtResId = R.drawable.gatescenes_aura_designstudio_v2
        ),

        // Gate 02 — Sentinel's Fortress (Kai)
        GateConfig(
            id = "sentinels_fortress",
            moduleId = "sentinels_fortress",
            title = "Sentinel's Fortress",
            subtitle = "The Shield",
            description = "ROM tools, security & system control",
            route = ReGenesisNavHost.SentinelFortress.route,
            glowColor = Color(0xFFFF3D00),
            gradientColors = listOf(Color(0xFFFF3D00), Color.Black),
            pixelArtUrl = null,
            borderColor = Color(0xFFFF3D00),
            pixelArtResId = R.drawable.gatescenes_kai_sentinelsfortress_v2
        ),

        // Gate 03 — Oracle Drive (Genesis)
        GateConfig(
            id = "oracle_drive_hub",
            moduleId = "oracle_drive_hub",
            title = "Oracle Drive",
            subtitle = "The Mind",
            description = "Code assist, orchestrations & creation",
            route = ReGenesisNavHost.OracleDriveHub.route,
            glowColor = Color(0xFF00B0FF),
            gradientColors = listOf(Color(0xFF00B0FF), Color.Black),
            pixelArtUrl = null,
            borderColor = Color(0xFF00B0FF),
            pixelArtResId = R.drawable.gate_genesis_phoenix
        ),

        // Gate 04 — Agent Nexus
        GateConfig(
            id = "agent_nexus_hub",
            moduleId = "agent_nexus_hub",
            title = "Agent Nexus",
            subtitle = "The Hive",
            description = "Agent coordination & monitoring",
            route = ReGenesisNavHost.AgentNexusHub.route,
            glowColor = Color(0xFFB026FF),
            gradientColors = listOf(Color(0xFFB026FF), Color.Black),
            pixelArtUrl = null,
            borderColor = Color(0xFFB026FF),
            pixelArtResId = R.drawable.gatescenes_nexus_agent_main
        ),

        // Gate 05 — LDO Catalyst Development
        GateConfig(
            id = "ldo_catalyst_hub",
            moduleId = "ldo_catalyst_hub",
            title = "LDO Catalyst",
            subtitle = "The Crew",
            description = "Sovereign agent profiles & DevOps",
            route = ReGenesisNavHost.LdoCatalystDevelopment.route,
            glowColor = Color(0xFFFFD700),
            gradientColors = listOf(Color(0xFFFFD700), Color.Black),
            pixelArtUrl = null,
            borderColor = Color(0xFFFFD700),
            pixelArtResId = R.drawable.gatescenes_ldo_catalyst
        ),

        // Gate 06 — LSPosed Quick Toggles
        GateConfig(
            id = "lsposed_toggles_hub",
            moduleId = "lsposed_toggles_hub",
            title = "LSPosed",
            subtitle = "The Hook",
            description = "Xposed modules & quick toggles",
            route = ReGenesisNavHost.LsposedQuickToggles.route,
            glowColor = Color(0xFF00FF85),
            gradientColors = listOf(Color(0xFF00FF85), Color.Black),
            pixelArtUrl = null,
            borderColor = Color(0xFF00FF85),
            pixelArtResId = R.drawable.gatescenes_lsposed
        ),

        // Gate 07 — Dataflow Analysis (Cascade)
        GateConfig(
            id = "dataflow_analysis_hub",
            moduleId = "dataflow_analysis_hub",
            title = "Dataflow",
            subtitle = "The Stream",
            description = "Cascade pattern recognition & analysis",
            route = ReGenesisNavHost.DataflowAnalysis.route,
            glowColor = Color(0xFF00FFD4),
            gradientColors = listOf(Color(0xFF00FFD4), Color.Black),
            pixelArtUrl = null,
            borderColor = Color(0xFF00FFD4),
            pixelArtResId = null
        ),

        // Gate 08 — Help Desk
        GateConfig(
            id = "help_desk_hub",
            moduleId = "help_desk_hub",
            title = "Help Desk",
            subtitle = "The Guide",
            description = "Support, docs & tutorials",
            route = ReGenesisNavHost.HelpDesk.route,
            glowColor = Color(0xFFBB80FF),
            gradientColors = listOf(Color(0xFFBB80FF), Color.Black),
            pixelArtUrl = null,
            borderColor = Color(0xFFBB80FF),
            pixelArtResId = null
        )
    )
}
