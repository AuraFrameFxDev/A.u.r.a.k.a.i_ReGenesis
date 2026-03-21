package dev.aurakai.auraframefx.domains.aura.config

import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.domains.aura.ui.components.SubGateCard
import dev.aurakai.auraframefx.navigation.ReGenesisRoute

/**
 * 📦 GATE ASSET LOADOUT
 *
 * Central registry for all sub-gate assets (images, names, routes, colors).
 * Use this to quickly apply settings across the UI without searching multiple files.
 */
object GateAssetLoadout {

    /**
     * Get a specific gate card by ID
     */
    fun getGate(id: String): SubGateCard? = allGates[id]

    /**
     * AURA DOMAIN GATES (UX/UI & Design)
     */
    val auraGates = mapOf(
        "aura_lab" to SubGateCard(
            id = "aura_lab",
            title = "Aura's Lab",
            subtitle = "UI Sandbox & Prototyping",
            styleADrawable = GateAssetConfig.AuraSubGates.AURA_LAB.styleA,
            styleBDrawable = GateAssetConfig.AuraSubGates.AURA_LAB.styleB,
            fallbackDrawable = GateAssetConfig.AuraSubGates.AURA_LAB.fallback,
            route = ReGenesisRoute.AuraLab.route,
            accentColor = Color(0xFFBB86FC)
        ),
        "chromacore" to SubGateCard(
            id = "chromacore",
            title = "ChromaCore",
            subtitle = "Material You Color Engine",
            styleADrawable = GateAssetConfig.AuraSubGates.CHROMA_CORE.styleA,
            styleBDrawable = GateAssetConfig.AuraSubGates.CHROMA_CORE.styleB,
            fallbackDrawable = GateAssetConfig.AuraSubGates.CHROMA_CORE.fallback,
            route = ReGenesisRoute.ChromaCore.route,
            accentColor = Color(0xFF6200EE)
        ),
        "collab_canvas" to SubGateCard(
            id = "collab_canvas",
            title = "CollabCanvas",
            subtitle = "Collaborative Design",
            styleADrawable = GateAssetConfig.AuraSubGates.COLLAB_CANVAS.styleA,
            styleBDrawable = GateAssetConfig.AuraSubGates.COLLAB_CANVAS.styleB,
            fallbackDrawable = GateAssetConfig.AuraSubGates.COLLAB_CANVAS.fallback,
            route = ReGenesisRoute.CollabCanvas.route,
            accentColor = Color(0xFF00E5FF)
        ),
        "themes" to SubGateCard(
            id = "themes",
            title = "Themes",
            subtitle = "Theme Selection & Management",
            styleADrawable = GateAssetConfig.AuraSubGates.THEME_ENGINE.styleA,
            styleBDrawable = GateAssetConfig.AuraSubGates.THEME_ENGINE.styleB,
            fallbackDrawable = GateAssetConfig.AuraSubGates.THEME_ENGINE.fallback,
            route = ReGenesisRoute.ThemeEngine.route,
            accentColor = Color(0xFFFF6F00)
        ),
        "uxui_engine" to SubGateCard(
            id = "uxui_engine",
            title = "UXUI Engine",
            subtitle = "Iconify • ColorBlendr • PixelLauncher",
            styleADrawable = GateAssetConfig.AuraSubGates.THEME_ENGINE.styleA,
            styleBDrawable = GateAssetConfig.AuraSubGates.THEME_ENGINE.styleB,
            fallbackDrawable = GateAssetConfig.AuraSubGates.THEME_ENGINE.fallback,
            route = ReGenesisRoute.ReGenesisCustomization.route,
            accentColor = Color(0xFFFFD700)
        )
    )

    /**
     * KAI DOMAIN GATES (Ethical Governor + Security + Bootloader)
     */
    val kaiGates = mapOf(
        "ethical_governor" to SubGateCard(
            id = "ethical_governor",
            title = "Ethical Governor",
            subtitle = "9-Domain AI Oversight (Backend)",
            styleADrawable = GateAssetConfig.KaiSubGates.SECURITY.styleA,
            styleBDrawable = GateAssetConfig.KaiSubGates.SECURITY.styleB,
            fallbackDrawable = GateAssetConfig.KaiSubGates.SECURITY.fallback,
            route = ReGenesisRoute.SecurityCenter.route, // Monitors genesis_ethical_governor.py
            accentColor = Color(0xFFFFD700) // Gold for guardian
        ),
        "security_shield" to SubGateCard(
            id = "security_shield",
            title = "Security Shield",
            subtitle = "Encryption • VPN • Threat Monitor",
            styleADrawable = GateAssetConfig.KaiSubGates.SECURITY.styleA,
            styleBDrawable = GateAssetConfig.KaiSubGates.SECURITY.styleB,
            fallbackDrawable = GateAssetConfig.KaiSubGates.SECURITY.fallback,
            route = ReGenesisRoute.SovereignShield.route,
            accentColor = Color(0xFF00E676)
        ),
        "bootloader" to SubGateCard(
            id = "bootloader",
            title = "Bootloader",
            subtitle = "System BIOS Control",
            styleADrawable = GateAssetConfig.KaiSubGates.BOOTLOADER.styleA,
            styleBDrawable = GateAssetConfig.KaiSubGates.BOOTLOADER.styleB,
            fallbackDrawable = GateAssetConfig.KaiSubGates.BOOTLOADER.fallback,
            route = ReGenesisRoute.Bootloader.route,
            accentColor = Color(0xFF2979FF)
        ),
        "rom_tools" to SubGateCard(
            id = "rom_tools",
            title = "ROM Tools",
            subtitle = "Flasher • Editor • Recovery",
            styleADrawable = GateAssetConfig.KaiSubGates.ROM_FLASHER.styleA,
            styleBDrawable = GateAssetConfig.KaiSubGates.ROM_FLASHER.styleB,
            fallbackDrawable = GateAssetConfig.KaiSubGates.ROM_FLASHER.fallback,
            route = ReGenesisRoute.ROMFlasher.route, // Can be ROM hub later
            accentColor = Color(0xFFFF3D00)
        )
    )

    /**
     * GENESIS DOMAIN GATES (Oracle Drive = Level 1)
     * Code Assist, Orchestrations, Creation Tools are INSIDE Oracle Drive hub
     * Note: Agent Nexus is a SEPARATE domain (Agent HQ with monitoring)
     */
    val genesisGates = mapOf(
        "oracle_drive" to SubGateCard(
            id = "oracle_drive",
            title = "Oracle Drive",
            subtitle = "Neural Archive & AI Storage",
            styleADrawable = GateAssetConfig.GenesisSubGates.NEURAL_ARCHIVE.styleA,
            styleBDrawable = GateAssetConfig.GenesisSubGates.NEURAL_ARCHIVE.styleB,
            fallbackDrawable = GateAssetConfig.GenesisSubGates.NEURAL_ARCHIVE.fallback,
            route = ReGenesisRoute.OracleDrive.route,
            accentColor = Color(0xFF00B0FF) // Cyan - Genesis
        ),
        "code_assist" to SubGateCard(
            id = "code_assist",
            title = "Code Assist",
            subtitle = "Interactive AI Programming",
            styleADrawable = GateAssetConfig.GenesisSubGates.CODE_ASSIST.styleA,
            styleBDrawable = GateAssetConfig.GenesisSubGates.CODE_ASSIST.styleB,
            fallbackDrawable = GateAssetConfig.GenesisSubGates.CODE_ASSIST.fallback,
            route = ReGenesisRoute.CodeAssist.route,
            accentColor = Color(0xFF00E5FF)
        ),
        "agent_bridge" to SubGateCard(
            id = "agent_bridge",
            title = "Agent Bridge",
            subtitle = "Cosmic AI Connectivity",
            styleADrawable = GateAssetConfig.GenesisSubGates.AGENT_BRIDGE.styleA,
            styleBDrawable = GateAssetConfig.GenesisSubGates.AGENT_BRIDGE.styleB,
            fallbackDrawable = GateAssetConfig.GenesisSubGates.AGENT_BRIDGE.fallback,
            route = ReGenesisRoute.AgentBridgeHub.route,
            accentColor = Color(0xFFBB86FC)
        ),
        "cloud_storage" to SubGateCard(
            id = "cloud_storage",
            title = "Infinite Storage",
            subtitle = "Oracle Cloud Network",
            styleADrawable = GateAssetConfig.GenesisSubGates.CLOUD_STORAGE.styleA,
            styleBDrawable = GateAssetConfig.GenesisSubGates.CLOUD_STORAGE.styleB,
            fallbackDrawable = GateAssetConfig.GenesisSubGates.CLOUD_STORAGE.fallback,
            route = ReGenesisRoute.OracleCloudStorage.route,
            accentColor = Color(0xFF00B0FF)
        ),
        "sentient_shell" to SubGateCard(
            id = "sentient_shell",
            title = "Sentient Shell",
            subtitle = "Persona & Identity Matrix",
            styleADrawable = GateAssetConfig.GenesisSubGates.TERMINAL.styleA,
            styleBDrawable = GateAssetConfig.GenesisSubGates.TERMINAL.styleB,
            fallbackDrawable = GateAssetConfig.GenesisSubGates.TERMINAL.fallback,
            route = ReGenesisRoute.SentientShell.route,
            accentColor = Color(0xFF00E5FF)
        )
    )


    /**
     * NEXUS DOMAIN GATES (Agent Coordination & Monitoring)
     */
    val nexusGates = mapOf(
        "monitoring" to SubGateCard(
            id = "monitoring",
            title = "Monitoring",
            subtitle = "Agent & System Status",
            styleADrawable = GateAssetConfig.NexusSubGates.MONITORING.styleA,
            styleBDrawable = GateAssetConfig.NexusSubGates.MONITORING.styleB,
            fallbackDrawable = GateAssetConfig.NexusSubGates.MONITORING.fallback,
            route = ReGenesisRoute.AgentMonitoring.route,
            accentColor = Color(0xFF7B2FFF)
        ),
        "sphere_grid" to SubGateCard(
            id = "sphere_grid",
            title = "Sphere Grid",
            subtitle = "DataVein Node Matrix",
            styleADrawable = GateAssetConfig.NexusSubGates.SPHERE_GRID.styleA,
            styleBDrawable = GateAssetConfig.NexusSubGates.SPHERE_GRID.styleB,
            fallbackDrawable = GateAssetConfig.NexusSubGates.SPHERE_GRID.fallback,
            route = ReGenesisRoute.DataVeinSphere.route,
            accentColor = Color(0xFF00E5FF)
        ),
        "constellation" to SubGateCard(
            id = "constellation",
            title = "Lineage Tree",
            subtitle = "Family Lineage Connection",
            styleADrawable = GateAssetConfig.NexusSubGates.CONSTELLATION.styleA,
            styleBDrawable = GateAssetConfig.NexusSubGates.CONSTELLATION.styleB,
            fallbackDrawable = GateAssetConfig.NexusSubGates.CONSTELLATION.fallback,
            route = ReGenesisRoute.AgentNeuralExplorer.route, // Map to explorer
            accentColor = Color(0xFFBB86FC)
        ),
        "fusion_mode" to SubGateCard(
            id = "fusion_mode",
            title = "Fusion Mode",
            subtitle = "Agent Merging Protocols",
            styleADrawable = GateAssetConfig.NexusSubGates.FUSION_MODE.styleA,
            styleBDrawable = GateAssetConfig.NexusSubGates.FUSION_MODE.styleB,
            fallbackDrawable = GateAssetConfig.NexusSubGates.FUSION_MODE.fallback,
            route = ReGenesisRoute.FusionMode.route,
            accentColor = Color(0xFFFF00DE)
        ),
        "task_assignment" to SubGateCard(
            id = "task_assignment",
            title = "Task Assignment",
            subtitle = "LDO Work Allocation",
            styleADrawable = GateAssetConfig.NexusSubGates.TASK_ASSIGNMENT.styleA,
            styleBDrawable = GateAssetConfig.NexusSubGates.TASK_ASSIGNMENT.styleB,
            fallbackDrawable = GateAssetConfig.NexusSubGates.TASK_ASSIGNMENT.fallback,
            route = ReGenesisRoute.TaskAssignment.route,
            accentColor = Color(0xFF00FF88)
        ),
        "meta_instruct" to SubGateCard(
            id = "meta_instruct",
            title = "Meta Instruct",
            subtitle = "High-Level Directives",
            styleADrawable = GateAssetConfig.NexusSubGates.META_INSTRUCT.styleA,
            styleBDrawable = GateAssetConfig.NexusSubGates.META_INSTRUCT.styleB,
            fallbackDrawable = GateAssetConfig.NexusSubGates.META_INSTRUCT.fallback,
            route = ReGenesisRoute.MetaInstruct.route,
            accentColor = Color(0xFFFFD700)
        )
    )

    /**
     * 🌐 PRIMARY DOMAIN GATES (The 8 Level 2 Gates)
     */
    val mainGates = mapOf(
        "aura" to SubGateCard(
            id = "aura",
            title = "Aura Studio",
            subtitle = "UX/UI Design & Theming",
            styleADrawable = GateAssetConfig.MainGates.UXUI_DESIGN_STUDIO.STYLE_A,
            styleBDrawable = GateAssetConfig.MainGates.UXUI_DESIGN_STUDIO.STYLE_B,
            fallbackDrawable = "gate_aura_final",
            route = ReGenesisRoute.AuraThemingHub.route,
            accentColor = Color(0xFFBB86FC)
        ),
        "kai" to SubGateCard(
            id = "kai",
            title = "Sentinel Fortress",
            subtitle = "Security & System Control",
            styleADrawable = GateAssetConfig.MainGates.SENTINELS_FORTRESS.STYLE_A,
            styleBDrawable = GateAssetConfig.MainGates.SENTINELS_FORTRESS.STYLE_B,
            fallbackDrawable = "gate_kai_final",
            route = ReGenesisRoute.SentinelFortress.route,
            accentColor = Color(0xFF00E676)
        ),
        "genesis" to SubGateCard(
            id = "genesis",
            title = "Oracle Drive",
            subtitle = "AI Brain & Orchestration",
            styleADrawable = GateAssetConfig.MainGates.ORACLE_DRIVE.STYLE_A,
            styleBDrawable = GateAssetConfig.MainGates.ORACLE_DRIVE.STYLE_B,
            fallbackDrawable = "gate_genesis_final",
            route = ReGenesisRoute.OracleDriveHub.route,
            accentColor = Color(0xFF00B0FF)
        ),
        "nexus" to SubGateCard(
            id = "nexus",
            title = "Agent Nexus",
            subtitle = "Agent HQ & Monitoring",
            styleADrawable = GateAssetConfig.MainGates.AGENT_NEXUS.STYLE_A,
            styleBDrawable = GateAssetConfig.MainGates.AGENT_NEXUS.STYLE_B,
            fallbackDrawable = "gate_nexus_final",
            route = ReGenesisRoute.AgentNexusHub.route,
            accentColor = Color(0xFF7B2FFF)
        ),
        "help" to SubGateCard(
            id = "help",
            title = "Help Services",
            subtitle = "Support & Docs",
            styleADrawable = GateAssetConfig.MainGates.HELP_SERVICES.STYLE_A,
            styleBDrawable = GateAssetConfig.MainGates.HELP_SERVICES.STYLE_B,
            fallbackDrawable = "gate_help_final",
            route = ReGenesisRoute.HelpDesk.route,
            accentColor = Color(0xFF00FF88)
        ),
        "lsposed" to SubGateCard(
            id = "lsposed",
            title = "LSPosed",
            subtitle = "Quick Toggles & Hooks",
            styleADrawable = GateAssetConfig.MainGates.LSPOSED_QUICK_TOGGLES.STYLE_A,
            styleBDrawable = GateAssetConfig.MainGates.LSPOSED_QUICK_TOGGLES.STYLE_B,
            fallbackDrawable = "gate_lsposed_final",
            route = ReGenesisRoute.LsposedQuickToggles.route,
            accentColor = Color(0xFFFFD700)
        ),
        "dataflow" to SubGateCard(
            id = "dataflow",
            title = "Dataflow Analysis",
            subtitle = "Cascade & Logic Visualization",
            styleADrawable = GateAssetConfig.MainGates.DATAFLOW_ANALYSIS.STYLE_A,
            styleBDrawable = GateAssetConfig.MainGates.DATAFLOW_ANALYSIS.STYLE_B,
            fallbackDrawable = "gate_cascade_final",
            route = ReGenesisRoute.DataflowAnalysis.route,
            accentColor = Color(0xFF00E5FF)
        ),
        "ldo" to SubGateCard(
            id = "ldo",
            title = "LDO Catalyst",
            subtitle = "Agent Advancement & Dev",
            styleADrawable = GateAssetConfig.MainGates.LDO_CATALYST_DEVELOPMENT.STYLE_A,
            styleBDrawable = GateAssetConfig.MainGates.LDO_CATALYST_DEVELOPMENT.STYLE_B,
            fallbackDrawable = "gate_ldo_final",
            route = ReGenesisRoute.LdoCatalystDevelopment.route,
            accentColor = Color(0xFFFF6F00)
        )
    )

    /**
     * Combined map of all gates for quick lookup
     */
    val allGates = auraGates + kaiGates + genesisGates + nexusGates + mainGates

    /**
     * Quick Load lists for Hubs
     */
    fun getAuraLoadout() = auraGates.values.toList()
    fun getKaiLoadout() = kaiGates.values.toList()
    fun getGenesisLoadout() = genesisGates.values.toList()
    fun getNexusSubGates() = nexusGates.values.toList()
}

