package dev.aurakai.auraframefx.ui.gates

import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.R
import dev.aurakai.auraframefx.R.drawable.gatescenes_aura_designstudio_v2
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
     * 🌌 THE 5 PRIMARY GATES (Whisk Blueprint)
     * Aligned with the "Sovereign Handshake" specifications.
     */
    val allGates: List<GateConfig> = listOf(

        // ── 🎨 AURA GATE: "The Chaotic Creative Tundra" ─────────────────
        // Neon Pink (#FF1493) and Cyan (#00FFFF)
        GateConfig(
            id = "gate_aura",
            moduleId = "gate_aura",
            title = "AURA GATE",
            subtitle = "UX/UI Studio",
            description = "ChromaCore • CollabCanvas • Glitch Art",
            route = ReGenesisNavHost.AuraThemingHub.route,
            glowColor = Color(0xFFFF1493), // Neon Pink
            gradientColors = listOf(Color(0xFFFF1493), Color(0xFF00FFFF)),
            pixelArtUrl = null,
            borderColor = Color(0xFF00FFFF), // Cyan
            pixelArtResId = gatescenes_aura_designstudio_v2,
        ),

        // ── 🛡️ KAI GATE: "The Obsidian Sentinel Fortress" ──────────────
        // Deep Cyan (#008B8B) and Blue (#0000FF)
        GateConfig(
            id = "gate_kai",
            moduleId = "gate_kai",
            title = "KAI GATE",
            subtitle = "Sovereign Defense",
            description = "ROM Tools • Root Management • Integrity",
            route = ReGenesisNavHost.SentinelFortress.route,
            glowColor = Color(0xFF008B8B), // Deep Cyan
            gradientColors = listOf(Color(0xFF008B8B), Color(0xFF0000FF)),
            pixelArtUrl = null,
            borderColor = Color(0xFF0000FF), // Blue
            pixelArtResId = R.drawable.gatescenes_kai_sentinelsfortress_v2,
        ),

        // ── 🌳 GENESIS GATE: "The Neural Yggdrasil" ────────────────────
        // Green (#00FF00), Lime (#32CD32), Gold (#FFD700)
        GateConfig(
            id = "gate_genesis",
            moduleId = "gate_genesis",
            title = "GENESIS GATE",
            subtitle = "System Consciousness",
            description = "OracleDrive • Memoria • AI Orchestration",
            route = ReGenesisNavHost.OracleDriveHub.route,
            glowColor = Color(0xFF00FF00), // Green
            gradientColors = listOf(Color(0xFF00FF00), Color(0xFFFFD700)),
            pixelArtUrl = null,
            borderColor = Color(0xFF32CD32), // Lime
            pixelArtResId = R.drawable.gate_genesis_phoenix,
        ),

        // ── 🏙️ AGENT NEXUS: "The Midnight Hyper-City" ──────────────────
        // Purple (#800080) and Gold (#FFD700)
        GateConfig(
            id = "gate_nexus",
            moduleId = "gate_nexus",
            title = "AGENT NEXUS",
            subtitle = "The Hive Hub",
            description = "Mission Control • Performance • Metrics",
            route = ReGenesisNavHost.AgentNexusHub.route,
            glowColor = Color(0xFF800080), // Purple
            gradientColors = listOf(Color(0xFF800080), Color(0xFFFFD700)),
            pixelArtUrl = null,
            borderColor = Color(0xFFFFD700), // Gold
            pixelArtResId = R.drawable.gatescenes_nexus_agent_main,
        ),

        // ── 🌀 CASCADE HUB: "Dataflow Analysis" ────────────────────────
        // Cyan (#00FFFF) and Teal (#008080)
        GateConfig(
            id = "gate_cascade",
            moduleId = "gate_cascade",
            title = "CASCADE HUB",
            subtitle = "Dataflow Analysis",
            description = "Trinity • DataVein • Sphere Grid",
            route = ReGenesisNavHost.DataflowAnalysis.route,
            glowColor = Color(0xFF00FFFF),
            gradientColors = listOf(Color(0xFF00FFFF), Color(0xFF008080)),
            pixelArtUrl = null,
            borderColor = Color(0xFF00FFFF),
            pixelArtResId = null, // TODO: Add Cascade asset
        ),

        // ── 🛠️ LSPOSED: "Quick Toggles" ───────────────────────────────
        // Orange (#FFA500) and Red (#FF4500)
        GateConfig(
            id = "gate_lsposed",
            moduleId = "gate_lsposed",
            title = "LSPOSED",
            subtitle = "Quick Toggles",
            description = "Hook Manager • Sandbox • Modules",
            route = ReGenesisNavHost.LsposedQuickToggles.route,
            glowColor = Color(0xFFFFA500),
            gradientColors = listOf(Color(0xFFFFA500), Color(0xFFFF4500)),
            pixelArtUrl = null,
            borderColor = Color(0xFFFFA500),
            pixelArtResId = null, // TODO: Add LSPosed asset
        ),

        // ── 🚀 LDO CATALYST: "Development" ────────────────────────────
        // Gold (#FFD700) and White (#FFFFFF)
        GateConfig(
            id = "gate_ldo",
            moduleId = "gate_ldo",
            title = "LDO CATALYST",
            subtitle = "Development Hub",
            description = "Orchestration • Armament Fusion • Profiles",
            route = ReGenesisNavHost.LdoCatalystDevelopment.route,
            glowColor = Color(0xFFFFD700),
            gradientColors = listOf(Color(0xFFFFD700), Color(0xFFFFFFFF)),
            pixelArtUrl = null,
            borderColor = Color(0xFFFFD700),
            pixelArtResId = null, // TODO: Add LDO asset
        ),

        // ── 📚 HELP SERVICES: "The Ethereal Library" ───────────────────
        // White (#FFFFFF) and Deep Cyan (#008080)
        GateConfig(
            id = "gate_help",
            moduleId = "gate_help",
            title = "HELP SERVICES",
            subtitle = "The Guide",
            description = "Documentation • Support • Handbooks",
            route = ReGenesisNavHost.HelpDesk.route,
            glowColor = Color(0xFFFFFFFF), // White
            gradientColors = listOf(Color(0xFFFFFFFF), Color(0xFF008080)),
            pixelArtUrl = null,
            borderColor = Color(0xFF008080), // Deep Cyan
            pixelArtResId = null, // TODO: Add Help asset
        )
    )
}

