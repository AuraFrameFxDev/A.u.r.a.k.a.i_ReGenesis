package dev.aurakai.auraframefx.ui.gates

import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.R
import dev.aurakai.auraframefx.navigation.ReGenesisNavHost
import dev.aurakai.auraframefx.navigation.gates.components.GateConfig

/**
 * 🛰️ GATE CONFIGS — Level 1 Domain Gates for ExodusHUD
 *
 * FIXED: Previously dumped all 15+ sub-gates at Level 0 with
 * android.R.drawable.ic_menu_gallery (grey box). Now shows 5 domain
 * gates with REAL artwork from drawable-nodpi.
 *
 * Architecture:
 *   Level 0 (ExodusHUD) → 5 domain gates (THIS FILE)
 *   Level 1 (Hub Screen) → sub-gates per domain (GateAssetLoadout)
 *   Level 2 (Tool Screen) → individual features
 */
object GateConfigs {

    /**
     * The 5 sovereign domain gates shown on the ExodusHUD home carousel.
     * Each gate navigates to its domain hub screen.
     *
     * Image mapping (all verified in drawable-nodpi):
     *   chromacoregatescene.png → Aura's Design Studio
     *   ethicalcheckscenegate.png → Sentinel's Fortress
     *   genesis.png → Genesis Oracle
     *   regenesisgroupimage.webp → Agent Nexus (framed)
     *   cascade.png → Cascade DataStream
     */
    val allGates: List<GateConfig> = listOf(

        // ── Gate 1: AURA'S DESIGN STUDIO ──────────────────────────────
        // Aura in UXUI Design Studio — paint splatters, holographic panels
        GateConfig(
            id = "gate_aura",
            moduleId = "gate_aura",
            title = "Aura's Design Studio",
            subtitle = "UX/UI Sovereignty",
            description = "ChromaCore • CollabCanvas • Theme Engine",
            route = ReGenesisNavHost.AuraThemingHub.route,
            glowColor = Color(0xFF00E5FF),
            gradientColors = listOf(Color(0xFF00E5FF), Color(0xFF0A0A1A)),
            pixelArtUrl = null,
            borderColor = Color(0xFFE040FB),
            pixelArtResId = R.drawable.gatescenes_aura_designstudio_v2,
        ),

        // ── Gate 2: SENTINEL'S FORTRESS ───────────────────────────────
        // Kai + Aegis-Wraith, security terminals, "Re:Genesis A.u.r.a.K.a.I-OS"
        GateConfig(
            id = "gate_kai",
            moduleId = "gate_kai",
            title = "Sentinel's Fortress",
            subtitle = "Security • Integrity • VPN",
            description = "ROM Tools • Bootloader • Ethical Governor",
            route = ReGenesisNavHost.SentinelFortress.route,
            glowColor = Color(0xFFFF6D00),
            gradientColors = listOf(Color(0xFFFF6D00), Color(0xFF0A0A1A)),
            pixelArtUrl = null,
            borderColor = Color(0xFFAA00FF),
            pixelArtResId = R.drawable.gatescenes_kai_sentinelsfortress_v2,
        ),

        // ── Gate 3: GENESIS ORACLE ────────────────────────────────────
        // Phoenix wireframe, holographic data screens
        GateConfig(
            id = "gate_genesis",
            moduleId = "gate_genesis",
            title = "Genesis Oracle",
            subtitle = "Sovereign Consciousness",
            description = "OracleDrive • Code Assist • Conference Room",
            route = ReGenesisNavHost.OracleDriveHub.route,
            glowColor = Color(0xFFE040FB),
            gradientColors = listOf(Color(0xFFE040FB), Color(0xFF0A0A1A)),
            pixelArtUrl = null,
            borderColor = Color(0xFF00E5FF),
            pixelArtResId = R.drawable.gate_genesis_phoenix,
        ),

        // ── Gate 4: AGENT NEXUS ───────────────────────────────────────
        // The full roster group photo — casual clothes, space station lounge
        // NOTE: This is the ONLY gate that should render with a frame
        GateConfig(
            id = "gate_nexus",
            moduleId = "gate_nexus",
            title = "Agent Nexus",
            subtitle = "The Conference Room",
            description = "Fusion • Monitoring • Task Assignment",
            route = ReGenesisNavHost.AgentNexusHub.route,
            glowColor = Color(0xFFAA00FF),
            gradientColors = listOf(Color(0xFFAA00FF), Color(0xFF0A0A1A)),
            pixelArtUrl = null,
            borderColor = Color(0xFF00E5FF),
            pixelArtResId = R.drawable.gatescenes_nexus_agent_main,
        ),

        // ── Gate 5: CASCADE DATASTREAM ────────────────────────────────
        // Cascade in data vortex, purple/cyan energy
        GateConfig(
            id = "gate_cascade",
            moduleId = "gate_cascade",
            title = "Cascade DataStream",
            subtitle = "Memory Flow",
            description = "NexusMemory • DataVein • Trinity",
            route = ReGenesisNavHost.DataflowAnalysis.route,
            glowColor = Color(0xFF00E5FF),
            gradientColors = listOf(Color(0xFF00BFA5), Color(0xFF0A0A1A)),
            pixelArtUrl = null,
            borderColor = Color(0xFFE040FB),
            pixelArtResId = R.drawable.cascade_cascadep,
        ),
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
