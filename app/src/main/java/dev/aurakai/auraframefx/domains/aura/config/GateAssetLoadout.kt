package dev.aurakai.auraframefx.domains.aura.config

import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.domains.aura.ui.components.SubGateCard
import dev.aurakai.auraframefx.navigation.ReGenesisNavHost

/**
 * 📦 GATE ASSET LOADOUT
 */
object GateAssetLoadout {

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
            route = ReGenesisNavHost.AuraLab.route,
            accentColor = Color(0xFFBB86FC)
        ),
        "chromacore" to SubGateCard(
            id = "chromacore",
            title = "ChromaCore",
            subtitle = "Material You Color Engine",
            styleADrawable = GateAssetConfig.AuraSubGates.CHROMA_CORE.styleA,
            styleBDrawable = GateAssetConfig.AuraSubGates.CHROMA_CORE.styleB,
            fallbackDrawable = GateAssetConfig.AuraSubGates.CHROMA_CORE.fallback,
            route = ReGenesisNavHost.ChromaCore.route,
            accentColor = Color(0xFF6200EE)
        ),
        "collab_canvas" to SubGateCard(
            id = "collab_canvas",
            title = "CollabCanvas",
            subtitle = "Collaborative Design",
            styleADrawable = GateAssetConfig.AuraSubGates.COLLAB_CANVAS.styleA,
            styleBDrawable = GateAssetConfig.AuraSubGates.COLLAB_CANVAS.styleB,
            fallbackDrawable = GateAssetConfig.AuraSubGates.COLLAB_CANVAS.fallback,
            route = ReGenesisNavHost.CollabCanvas.route,
            accentColor = Color(0xFF00FFFF)
        ),
        "themes" to SubGateCard(
            id = "themes",
            title = "Themes",
            subtitle = "Theme Selection & Management",
            styleADrawable = GateAssetConfig.AuraSubGates.THEME_ENGINE.styleA,
            styleBDrawable = GateAssetConfig.AuraSubGates.THEME_ENGINE.styleB,
            fallbackDrawable = GateAssetConfig.AuraSubGates.THEME_ENGINE.fallback,
            route = ReGenesisNavHost.ThemeEngine.route,
            accentColor = Color(0xFFFF6F00)
        ),
        "uxui_engine" to SubGateCard(
            id = "uxui_engine",
            title = "UXUI Engine",
            subtitle = "Iconify • ColorBlendr • PixelLauncher",
            styleADrawable = GateAssetConfig.AuraSubGates.THEME_ENGINE.styleA,
            styleBDrawable = GateAssetConfig.AuraSubGates.THEME_ENGINE.styleB,
            fallbackDrawable = GateAssetConfig.AuraSubGates.THEME_ENGINE.fallback,
            route = ReGenesisNavHost.ReGenesisCustomization.route,
            accentColor = Color(0xFFFFD700)
        ),
        "iconify" to SubGateCard(
            id = "iconify",
            title = "Iconify",
            subtitle = "Icon Packs & Styles",
            styleADrawable = GateAssetConfig.AuraSubGates.ICONIFY.styleA,
            styleBDrawable = GateAssetConfig.AuraSubGates.ICONIFY.styleB,
            fallbackDrawable = GateAssetConfig.AuraSubGates.ICONIFY.fallback,
            route = ReGenesisNavHost.IconifyPicker.route,
            accentColor = Color(0xFFFFCC00)
        ),
        "pixel_launcher" to SubGateCard(
            id = "pixel_launcher",
            title = "Pixel Launcher",
            subtitle = "Launcher Enhancements",
            styleADrawable = GateAssetConfig.AuraSubGates.PIXEL_LAUNCHER.styleA,
            styleBDrawable = GateAssetConfig.AuraSubGates.PIXEL_LAUNCHER.styleB,
            fallbackDrawable = GateAssetConfig.AuraSubGates.PIXEL_LAUNCHER.fallback,
            route = ReGenesisNavHost.PixelLauncherEnhanced.route,
            accentColor = Color(0xFF00FF85)
        ),
        "notch_bar" to SubGateCard(
            id = "notch_bar",
            title = "Notch Bar",
            subtitle = "Status & QS bars",
            styleADrawable = GateAssetConfig.AuraSubGates.NOTCH_BAR.styleA,
            styleBDrawable = GateAssetConfig.AuraSubGates.NOTCH_BAR.styleB,
            fallbackDrawable = GateAssetConfig.AuraSubGates.NOTCH_BAR.fallback,
            route = ReGenesisNavHost.NotchBar.route,
            accentColor = Color(0xFF40C4FF)
        ),
        "terminal" to SubGateCard(
            id = "terminal",
            title = "Terminal",
            subtitle = "UI Code Surface",
            styleADrawable = GateAssetConfig.AuraSubGates.TERMINAL.styleA,
            styleBDrawable = GateAssetConfig.AuraSubGates.TERMINAL.styleB,
            fallbackDrawable = GateAssetConfig.AuraSubGates.TERMINAL.fallback,
            route = ReGenesisNavHost.Terminal.route,
            accentColor = Color(0xFF90CAF9)
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
            route = ReGenesisNavHost.SecurityCenter.route,
            accentColor = Color(0xFF008B8B)
        ),
        "security_shield" to SubGateCard(
            id = "security_shield",
            title = "Security Shield",
            subtitle = "Encryption • VPN • Threat Monitor",
            styleADrawable = GateAssetConfig.KaiSubGates.SECURITY.styleA,
            styleBDrawable = GateAssetConfig.KaiSubGates.SECURITY.styleB,
            fallbackDrawable = GateAssetConfig.KaiSubGates.SECURITY.fallback,
            route = ReGenesisNavHost.SovereignShield.route,
            accentColor = Color(0xFF00E676)
        ),
        "bootloader" to SubGateCard(
            id = "bootloader",
            title = "Bootloader",
            subtitle = "System BIOS Control",
            styleADrawable = GateAssetConfig.KaiSubGates.BOOTLOADER.styleA,
            styleBDrawable = GateAssetConfig.KaiSubGates.BOOTLOADER.styleB,
            fallbackDrawable = GateAssetConfig.KaiSubGates.BOOTLOADER.fallback,
            route = ReGenesisNavHost.Bootloader.route,
            accentColor = Color(0xFF2979FF)
        ),
        "rom_tools" to SubGateCard(
            id = "rom_tools",
            title = "ROM Tools",
            subtitle = "Flasher • Editor • Recovery",
            styleADrawable = GateAssetConfig.KaiSubGates.ROM_FLASHER.styleA,
            styleBDrawable = GateAssetConfig.KaiSubGates.ROM_FLASHER.styleB,
            fallbackDrawable = GateAssetConfig.KaiSubGates.ROM_FLASHER.fallback,
            route = ReGenesisNavHost.ROMFlasher.route,
            accentColor = Color(0xFFFF3D00)
        ),
        "module_manager" to SubGateCard(
            id = "module_manager",
            title = "Module Manager",
            subtitle = "Magisk & LSPosed",
            styleADrawable = GateAssetConfig.KaiSubGates.MODULE_MANAGER.styleA,
            styleBDrawable = GateAssetConfig.KaiSubGates.MODULE_MANAGER.styleB,
            fallbackDrawable = GateAssetConfig.KaiSubGates.MODULE_MANAGER.fallback,
            route = ReGenesisNavHost.ModuleManager.route,
            accentColor = Color(0xFF00FF85)
        ),
        "root_tools" to SubGateCard(
            id = "root_tools",
            title = "Root Tools",
            subtitle = "System Modifications",
            styleADrawable = GateAssetConfig.KaiSubGates.ROOT_TOOLS.styleA,
            styleBDrawable = GateAssetConfig.KaiSubGates.ROOT_TOOLS.styleB,
            fallbackDrawable = GateAssetConfig.KaiSubGates.ROOT_TOOLS.fallback,
            route = ReGenesisNavHost.RootTools.route,
            accentColor = Color(0xFF00E5FF)
        ),
        "recovery" to SubGateCard(
            id = "recovery",
            title = "Recovery",
            subtitle = "TWRP & OrangeFox",
            styleADrawable = GateAssetConfig.KaiSubGates.RECOVERY.styleA,
            styleBDrawable = GateAssetConfig.KaiSubGates.RECOVERY.styleB,
            fallbackDrawable = GateAssetConfig.KaiSubGates.RECOVERY.fallback,
            route = ReGenesisNavHost.RecoveryTools.route,
            accentColor = Color(0xFFFFAB40)
        ),
        "vpn" to SubGateCard(
            id = "vpn",
            title = "VPN & Ad‑Block",
            subtitle = "Network Security",
            styleADrawable = GateAssetConfig.KaiSubGates.VPN.styleA,
            styleBDrawable = GateAssetConfig.KaiSubGates.VPN.styleB,
            fallbackDrawable = GateAssetConfig.KaiSubGates.VPN.fallback,
            route = ReGenesisNavHost.VPN.route,
            accentColor = Color(0xFF00BFA5)
        ),
        "lsposed_hub" to SubGateCard(
            id = "lsposed_hub",
            title = "LSPosed Hub",
            subtitle = "Xposed Hook Framework",
            styleADrawable = GateAssetConfig.KaiSubGates.LSPOSED.styleA,
            styleBDrawable = GateAssetConfig.KaiSubGates.LSPOSED.styleB,
            fallbackDrawable = GateAssetConfig.KaiSubGates.LSPOSED.fallback,
            route = ReGenesisNavHost.LSPosedHub.route,
            accentColor = Color(0xFFFFCC00)
        )
    )

    /**
     * GENESIS DOMAIN GATES
     */
    val genesisGates = mapOf(
        "oracle_drive" to SubGateCard(
            id = "oracle_drive",
            title = "Oracle Drive",
            subtitle = "Code Assist • Orchestrations • Creation",
            styleADrawable = GateAssetConfig.GenesisSubGates.NEURAL_ARCHIVE.styleA,
            styleBDrawable = GateAssetConfig.GenesisSubGates.NEURAL_ARCHIVE.styleB,
            fallbackDrawable = GateAssetConfig.GenesisSubGates.NEURAL_ARCHIVE.fallback,
            route = ReGenesisNavHost.OracleDrive.route,
            accentColor = Color(0xFF00FF85)
        )
    )

    /**
     * NEXUS DOMAIN GATES
     */
    val nexusGates = mapOf(
        "monitoring" to SubGateCard(
            id = "monitoring",
            title = "Monitoring",
            subtitle = "Agent & System Status",
            styleADrawable = GateAssetConfig.NexusSubGates.MONITORING.styleA,
            styleBDrawable = GateAssetConfig.NexusSubGates.MONITORING.styleB,
            fallbackDrawable = GateAssetConfig.NexusSubGates.MONITORING.fallback,
            route = ReGenesisNavHost.AgentMonitoring.route,
            accentColor = Color(0xFF800080)
        ),
        "sphere_grid" to SubGateCard(
            id = "sphere_grid",
            title = "Sphere Grid",
            subtitle = "DataVein Node Matrix",
            styleADrawable = GateAssetConfig.NexusSubGates.SPHERE_GRID.styleA,
            styleBDrawable = GateAssetConfig.NexusSubGates.SPHERE_GRID.styleB,
            fallbackDrawable = GateAssetConfig.NexusSubGates.SPHERE_GRID.fallback,
            route = ReGenesisNavHost.DataVeinSphere.route,
            accentColor = Color(0xFF00E5FF)
        ),
        "fusion_mode" to SubGateCard(
            id = "fusion_mode",
            title = "Fusion Mode",
            subtitle = "Agent Merging Protocols",
            styleADrawable = GateAssetConfig.NexusSubGates.FUSION_MODE.styleA,
            styleBDrawable = GateAssetConfig.NexusSubGates.FUSION_MODE.styleB,
            fallbackDrawable = GateAssetConfig.NexusSubGates.FUSION_MODE.fallback,
            route = ReGenesisNavHost.FusionMode.route,
            accentColor = Color(0xFFFF00DE)
        ),
        "task_assignment" to SubGateCard(
            id = "task_assignment",
            title = "Task Assignment",
            subtitle = "LDO Work Allocation",
            styleADrawable = GateAssetConfig.NexusSubGates.TASK_ASSIGNMENT.styleA,
            styleBDrawable = GateAssetConfig.NexusSubGates.TASK_ASSIGNMENT.styleB,
            fallbackDrawable = GateAssetConfig.NexusSubGates.TASK_ASSIGNMENT.fallback,
            route = ReGenesisNavHost.TaskAssignment.route,
            accentColor = Color(0xFF00FF88)
        ),
        "meta_instruct" to SubGateCard(
            id = "meta_instruct",
            title = "Meta Instruct",
            subtitle = "High-Level Directives",
            styleADrawable = GateAssetConfig.NexusSubGates.META_INSTRUCT.styleA,
            styleBDrawable = GateAssetConfig.NexusSubGates.META_INSTRUCT.styleB,
            fallbackDrawable = GateAssetConfig.NexusSubGates.META_INSTRUCT.fallback,
            route = ReGenesisNavHost.MetaInstruct.route,
            accentColor = Color(0xFFFFD700)
        )
    )

    val helpGates = mapOf(
        "help_center" to SubGateCard(
            id = "help_center",
            title = "Help Center",
            subtitle = "Ethereal Library • Documentation",
            styleADrawable = "help_style_a",
            styleBDrawable = "help_style_b",
            fallbackDrawable = null,
            route = "help_route",
            accentColor = Color(0xFFFFFFFF)
        )
    )

    val allGates = auraGates + kaiGates + genesisGates + nexusGates + helpGates

    fun getAuraLoadout() = auraGates.values.toList()
    fun getKaiLoadout() = kaiGates.values.toList()
    fun getGenesisLoadout() = genesisGates.values.toList()
    fun getNexusSubGates() = nexusGates.values.toList()
}
