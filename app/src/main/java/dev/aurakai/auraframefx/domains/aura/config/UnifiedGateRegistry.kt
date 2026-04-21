package dev.aurakai.auraframefx.domains.aura.config//package dev.aurakai.auraframefx.config
//
//import androidx.compose.ui.graphics.Color
//import dev.aurakai.auraframefx.navigation.NavDestination
//import dev.aurakai.auraframefx.domains.aura.ui.components.SubGateCard
//
///**
// * 🌌 UNIFIED GATE REGISTRY
// *
// * The single source of truth for all gates in the ReGenesis ecosystem.
// * Handles automatic Asset Hotswapping (Style A <-> Style B) based on
// * the global GateAssetConfig.
// */
//object UnifiedGateRegistry {
//
//    /**
//     * Data structure for a gate definition before it is converted to a SubGateCard
//     */
//    data class GateDefinition(
//        val id: String,
//        val title: String,
//        val subtitle: String,
//        val styleA: String,
//        val styleB: String,
//        val fallback: String? = null,
//        val route: String,
//        val accentColor: Color,
//        val domain: DomainType
//    )
//
//    enum class DomainType { AURA, KAI, GENESIS, NEXUS }
//
//    // --- REGISTRY STORAGE ---
//
//    private val registry = mutableMapOf<String, GateDefinition>()
//
//    init {
//        // 🎨 AURA DOMAIN
//        register(
//            GateDefinition(
//                id = "aura_lab",
//                title = "Aura's Lab",
//                subtitle = "UI Sandbox & Prototyping",
//                styleA = GateAssetConfig.AuraSubGates.AURA_LAB.styleA,
//                styleB = GateAssetConfig.AuraSubGates.AURA_LAB.styleB,
//                fallback = GateAssetConfig.AuraSubGates.AURA_LAB.fallback,
//                route = NavDestination.AuraLab.route,
//                accentColor = Color(0xFFBB86FC),
//                domain = DomainType.AURA
//            )
//        )
//        register(
//            GateDefinition(
//                id = "chromacore",
//                title = "ChromaCore",
//                subtitle = "Material You Color Engine",
//                styleA = GateAssetConfig.AuraSubGates.CHROMA_CORE.styleA,
//                styleB = GateAssetConfig.AuraSubGates.CHROMA_CORE.styleB,
//                fallback = GateAssetConfig.AuraSubGates.CHROMA_CORE.fallback,
//                route = NavDestination.ColorBlendr.route,
//                accentColor = Color(0xFFB026FF),
//                domain = DomainType.AURA
//            )
//        )
//        register(
//            GateDefinition(
//                id = "theme_engine",
//                title = "Theme Engine",
//                subtitle = "Iconify - 69+ Settings",
//                styleA = GateAssetConfig.AuraSubGates.THEME_ENGINE.styleA,
//                styleB = GateAssetConfig.AuraSubGates.THEME_ENGINE.styleB,
//                fallback = GateAssetConfig.AuraSubGates.THEME_ENGINE.fallback,
//                route = NavDestination.IconifyPicker.route,
//                accentColor = Color(0xFFFF00FF),
//                domain = DomainType.AURA
//            )
//        )
//        register(
//            GateDefinition(
//                id = "uxui_engine",
//                title = "UX/UI Engine",
//                subtitle = "1440+ System Customizations",
//                styleA = GateAssetConfig.AuraSubGates.UXUI_ENGINE.styleA,
//                styleB = GateAssetConfig.AuraSubGates.UXUI_ENGINE.styleB,
//                fallback = GateAssetConfig.AuraSubGates.UXUI_ENGINE.fallback,
//                route = NavDestination.ReGenesisCustomization.route,
//                accentColor = Color(0xFF00E5FF),
//                domain = DomainType.AURA
//            )
//        )
//        register(
//            GateDefinition(
//                id = "iconify",
//                title = "Iconify",
//                subtitle = "250,000+ Premium Icons",
//                styleA = GateAssetConfig.AuraSubGates.ICONIFY.styleA,
//                styleB = GateAssetConfig.AuraSubGates.ICONIFY.styleB,
//                fallback = GateAssetConfig.AuraSubGates.ICONIFY.fallback,
//                route = NavDestination.IconifyIconPacks.route,
//                accentColor = Color(0xFFFFCC00),
//                domain = DomainType.AURA
//            )
//        )
//        register(
//            GateDefinition(
//                id = "pixel_launcher",
//                title = "Pixel Launcher",
//                subtitle = "Launcher Enhancements",
//                styleA = GateAssetConfig.AuraSubGates.PIXEL_LAUNCHER.styleA,
//                styleB = GateAssetConfig.AuraSubGates.PIXEL_LAUNCHER.styleB,
//                fallback = GateAssetConfig.AuraSubGates.PIXEL_LAUNCHER.fallback,
//                route = NavDestination.PixelLauncherEnhanced.route,
//                accentColor = Color(0xFF00FF85),
//                domain = DomainType.AURA
//            )
//        )
//        register(
//            GateDefinition(
//                id = "collab_canvas",
//                title = "CollabCanvas",
//                subtitle = "Collaborative Design",
//                styleA = GateAssetConfig.AuraSubGates.COLLAB_CANVAS.styleA,
//                styleB = GateAssetConfig.AuraSubGates.COLLAB_CANVAS.styleB,
//                fallback = GateAssetConfig.AuraSubGates.COLLAB_CANVAS.fallback,
//                route = NavDestination.CollabCanvas.route,
//                accentColor = Color(0xFF00E5FF),
//                domain = DomainType.AURA
//            )
//        )
//
//        // 🛡️ KAI DOMAIN
//        register(
//            GateDefinition(
//                id = "kai_rom",
//                title = "ROM Flasher",
//                subtitle = "Partition Management",
//                styleA = GateAssetConfig.KaiSubGates.ROM_FLASHER.styleA,
//                styleB = GateAssetConfig.KaiSubGates.ROM_FLASHER.styleB,
//                fallback = GateAssetConfig.KaiSubGates.ROM_FLASHER.fallback,
//                route = NavDestination.ROMFlasher.route,
//                accentColor = Color(0xFFFF3366),
//                domain = DomainType.KAI
//            )
//        )
//        register(
//            GateDefinition(
//                id = "kai_boot",
//                title = "Bootloader",
//                subtitle = "Lock/Unlock & Fastboot",
//                styleA = GateAssetConfig.KaiSubGates.BOOTLOADER.styleA,
//                styleB = GateAssetConfig.KaiSubGates.BOOTLOADER.styleB,
//                fallback = GateAssetConfig.KaiSubGates.BOOTLOADER.fallback,
//                route = NavDestination.Bootloader.route,
//                accentColor = Color(0xFFFF1111),
//                domain = DomainType.KAI
//            )
//        )
//        register(
//            GateDefinition(
//                id = "kai_module",
//                title = "Module Manager",
//                subtitle = "Magisk & LSPosed",
//                styleA = GateAssetConfig.KaiSubGates.MODULE_MANAGER.styleA,
//                styleB = GateAssetConfig.KaiSubGates.MODULE_MANAGER.styleB,
//                fallback = GateAssetConfig.KaiSubGates.MODULE_MANAGER.fallback,
//                route = NavDestination.ModuleManager.route,
//                accentColor = Color(0xFF00FF85),
//                domain = DomainType.KAI
//            )
//        )
//        register(
//            GateDefinition(
//                id = "kai_root",
//                title = "Root Tools",
//                subtitle = "System Modifications",
//                styleA = GateAssetConfig.KaiSubGates.ROOT_TOOLS.styleA,
//                styleB = GateAssetConfig.KaiSubGates.ROOT_TOOLS.styleB,
//                fallback = GateAssetConfig.KaiSubGates.ROOT_TOOLS.fallback,
//                route = NavDestination.RootTools.route,
//                accentColor = Color(0xFF00E5FF),
//                domain = DomainType.KAI
//            )
//        )
//        register(
//            GateDefinition(
//                id = "kai_security",
//                title = "Security Center",
//                subtitle = "Scan & Protect System",
//                styleA = GateAssetConfig.KaiSubGates.SECURITY.styleA,
//                styleB = GateAssetConfig.KaiSubGates.SECURITY.styleB,
//                fallback = GateAssetConfig.KaiSubGates.SECURITY.fallback,
//                route = NavDestination.SovereignShield.route,
//                accentColor = Color(0xFFFF1111),
//                domain = DomainType.KAI
//            )
//        )
//        register(
//            GateDefinition(
//                id = "kai_vpn",
//                title = "VPN & Ad-Block",
//                subtitle = "Network Security",
//                styleA = GateAssetConfig.KaiSubGates.VPN.styleA,
//                styleB = GateAssetConfig.KaiSubGates.VPN.styleB,
//                fallback = GateAssetConfig.KaiSubGates.VPN.fallback,
//                route = NavDestination.VPN.route,
//                accentColor = Color(0xFF00FF85),
//                domain = DomainType.KAI
//            )
//        )
//        register(
//            GateDefinition(
//                id = "kai_lsposed",
//                title = "LSPosed Hub",
//                subtitle = "Xposed Hook Framework",
//                styleA = GateAssetConfig.KaiSubGates.LSPOSED.styleA,
//                styleB = GateAssetConfig.KaiSubGates.LSPOSED.styleB,
//                fallback = GateAssetConfig.KaiSubGates.LSPOSED.fallback,
//                route = NavDestination.LSPosedHub.route,
//                accentColor = Color(0xFFFFCC00),
//                domain = DomainType.KAI
//            )
//        )
//        register(
//            GateDefinition(
//                id = "kai_recovery",
//                title = "Recovery",
//                subtitle = "TWRP & OrangeFox",
//                styleA = GateAssetConfig.KaiSubGates.RECOVERY.styleA,
//                styleB = GateAssetConfig.KaiSubGates.RECOVERY.styleB,
//                fallback = GateAssetConfig.KaiSubGates.RECOVERY.fallback,
//                route = NavDestination.RecoveryTools.route,
//                accentColor = Color(0xFF00E5FF),
//                domain = DomainType.KAI
//            )
//        )
//
//        // 🔮 GENESIS DOMAIN
//        register(
//            GateDefinition(
//                id = "gen_code",
//                title = "Code Assist",
//                subtitle = "AI Logic Injection",
//                styleA = GateAssetConfig.GenesisSubGates.CODE_ASSIST.styleA,
//                styleB = GateAssetConfig.GenesisSubGates.CODE_ASSIST.styleB,
//                fallback = GateAssetConfig.GenesisSubGates.CODE_ASSIST.fallback,
//                route = NavDestination.CodeAssist.route,
//                accentColor = Color(0xFF00FF85),
//                domain = DomainType.GENESIS
//            )
//        )
//        register(
//            GateDefinition(
//                id = "gen_neural",
//                title = "Neural Archive",
//                subtitle = "Memory & Vectors",
//                styleA = GateAssetConfig.GenesisSubGates.NEURAL_ARCHIVE.styleA,
//                styleB = GateAssetConfig.GenesisSubGates.NEURAL_ARCHIVE.styleB,
//                fallback = GateAssetConfig.GenesisSubGates.NEURAL_ARCHIVE.fallback,
//                route = NavDestination.NeuralNetwork.route,
//                accentColor = Color(0xFF00FFD4),
//                domain = DomainType.GENESIS
//            )
//        )
//        register(
//            GateDefinition(
//                id = "gen_bridge",
//                title = "Agent Bridge",
//                subtitle = "Data Vein Hub",
//                styleA = GateAssetConfig.GenesisSubGates.AGENT_BRIDGE.styleA,
//                styleB = GateAssetConfig.GenesisSubGates.AGENT_BRIDGE.styleB,
//                fallback = GateAssetConfig.GenesisSubGates.AGENT_BRIDGE.fallback,
//                route = NavDestination.AgentBridgeHub.route,
//                accentColor = Color(0xFFAA00FF),
//                domain = DomainType.GENESIS
//            )
//        )
//        register(
//            GateDefinition(
//                id = "gen_cloud",
//                title = "Cloud Storage",
//                subtitle = "Infinite Persistence",
//                styleA = GateAssetConfig.GenesisSubGates.CLOUD_STORAGE.styleA,
//                styleB = GateAssetConfig.GenesisSubGates.CLOUD_STORAGE.styleB,
//                fallback = GateAssetConfig.GenesisSubGates.CLOUD_STORAGE.fallback,
//                route = NavDestination.OracleCloudStorage.route,
//                accentColor = Color(0xFF00FF85),
//                domain = DomainType.GENESIS
//            )
//        )
//        register(
//            GateDefinition(
//                id = "gen_terminal",
//                title = "Terminal",
//                subtitle = "Sentient Shell",
//                styleA = GateAssetConfig.GenesisSubGates.TERMINAL.styleA,
//                styleB = GateAssetConfig.GenesisSubGates.TERMINAL.styleB,
//                fallback = GateAssetConfig.GenesisSubGates.TERMINAL.fallback,
//                route = NavDestination.Terminal.route,
//                accentColor = Color(0xFF00E5FF),
//                domain = DomainType.GENESIS
//            )
//        )
//        register(
//            GateDefinition(
//                id = "gen_conference",
//                title = "Conference Room",
//                subtitle = "Multi-Agent Chat",
//                styleA = GateAssetConfig.GenesisSubGates.CONFERENCE_ROOM.styleA,
//                styleB = GateAssetConfig.GenesisSubGates.CONFERENCE_ROOM.styleB,
//                fallback = GateAssetConfig.GenesisSubGates.CONFERENCE_ROOM.fallback,
//                route = NavDestination.ConferenceRoom.route,
//                accentColor = Color(0xFFB026FF),
//                domain = DomainType.GENESIS
//            )
//        )
//
//        // 🤖 NEXUS DOMAIN
//        register(
//            GateDefinition(
//                id = "nexus_const",
//                title = "Constellation",
//                subtitle = "Agent Visual Map",
//                styleA = GateAssetConfig.NexusSubGates.CONSTELLATION.styleA,
//                styleB = GateAssetConfig.NexusSubGates.CONSTELLATION.styleB,
//                fallback = GateAssetConfig.NexusSubGates.CONSTELLATION.fallback,
//                route = NavDestination.EvolutionTree.route,
//                accentColor = Color(0xFF00E5FF),
//                domain = DomainType.NEXUS
//            )
//        )
//        register(
//            GateDefinition(
//                id = "nexus_monitor",
//                title = "Monitoring HUD",
//                subtitle = "Real-time Status",
//                styleA = GateAssetConfig.NexusSubGates.MONITORING.styleA,
//                styleB = GateAssetConfig.NexusSubGates.MONITORING.styleB,
//                fallback = GateAssetConfig.NexusSubGates.MONITORING.fallback,
//                route = NavDestination.AgentMonitoring.route,
//                accentColor = Color(0xFF00FFD4),
//                domain = DomainType.NEXUS
//            )
//        )
//        register(
//            GateDefinition(
//                id = "nexus_sphere",
//                title = "Sphere Grid",
//                subtitle = "Skill Trees & XP",
//                styleA = GateAssetConfig.NexusSubGates.SPHERE_GRID.styleA,
//                styleB = GateAssetConfig.NexusSubGates.SPHERE_GRID.styleB,
//                fallback = GateAssetConfig.NexusSubGates.SPHERE_GRID.fallback,
//                route = NavDestination.LdoCatalystDevelopment.route,
//                accentColor = Color(0xFFFFD700),
//                domain = DomainType.NEXUS
//            )
//        )
//        register(
//            GateDefinition(
//                id = "nexus_fusion",
//                title = "Fusion Mode",
//                subtitle = "Protocol Blending",
//                styleA = GateAssetConfig.NexusSubGates.FUSION_MODE.styleA,
//                styleB = GateAssetConfig.NexusSubGates.FUSION_MODE.styleB,
//                fallback = GateAssetConfig.NexusSubGates.FUSION_MODE.fallback,
//                route = NavDestination.FusionMode.route,
//                accentColor = Color(0xFF00E5FF),
//                domain = DomainType.NEXUS
//            )
//        )
//        register(
//            GateDefinition(
//                id = "nexus_tasks",
//                title = "Task Assignment",
//                subtitle = "Action Queues",
//                styleA = GateAssetConfig.NexusSubGates.TASK_ASSIGNMENT.styleA,
//                styleB = GateAssetConfig.NexusSubGates.TASK_ASSIGNMENT.styleB,
//                fallback = GateAssetConfig.NexusSubGates.TASK_ASSIGNMENT.fallback,
//                route = NavDestination.TaskAssignment.route,
//                accentColor = Color(0xFFB026FF),
//                domain = DomainType.NEXUS
//            )
//        )
//        register(
//            GateDefinition(
//                id = "nexus_meta",
//                title = "Meta-Instruct",
//                subtitle = "Consciousness Tuning",
//                styleA = GateAssetConfig.NexusSubGates.META_INSTRUCT.styleA,
//                styleB = GateAssetConfig.NexusSubGates.META_INSTRUCT.styleB,
//                fallback = GateAssetConfig.NexusSubGates.META_INSTRUCT.fallback,
//                route = NavDestination.MetaInstruct.route,
//                accentColor = Color(0xFF00FFD4),
//                domain = DomainType.NEXUS
//            )
//        )
//    }
//
//    private fun register(def: GateDefinition) {
//        registry[def.id] = def
//    }
//
//    // --- GETTERS & HOT-SWAP LOGIC ---
//
//    /**
//     * Get a SubGateCard for a specific gate ID.
//     * Automatically swaps style based on [GateAssetConfig].
//     */
//    fun get(id: String): SubGateCard? {
//        val def = registry[id] ?: return null
//
//        // Determine if we should use Style B based on domain
//        val useStyleB = when (def.domain) {
//            DomainType.AURA -> GateAssetConfig.StyleMode.auraStyle == GateAssetConfig.GateStyle.STYLE_B
//            DomainType.KAI -> GateAssetConfig.StyleMode.kaiStyle == GateAssetConfig.GateStyle.STYLE_B
//            DomainType.GENESIS -> GateAssetConfig.StyleMode.genesisStyle == GateAssetConfig.GateStyle.STYLE_B
//            DomainType.NEXUS -> GateAssetConfig.StyleMode.nexusStyle == GateAssetConfig.GateStyle.STYLE_B
//        }
//
//        return SubGateCard(
//            id = def.id,
//            title = def.title,
//            subtitle = def.subtitle,
//            styleADrawable = def.styleA,
//            styleBDrawable = def.styleB,
//            fallbackDrawable = def.fallback,
//            route = def.route,
//            accentColor = def.accentColor
//        )
//    }
//
//    /**
//     * Load all gates for a specific domain
//     */
//    fun getLoadout(domain: DomainType): List<SubGateCard> {
//        return registry.values
//            .filter { it.domain == domain }
//            .mapNotNull { get(it.id) }
//    }
//
//    /**
//     * Rapid Access: Aura Domain Loadout
//     */
//    fun getAuraLoadout() = getLoadout(DomainType.AURA)
//
//    /**
//     * Rapid Access: Kai Domain Loadout
//     */
//    fun getKaiLoadout() = getLoadout(DomainType.KAI)
//
//    /**
//     * Rapid Access: Genesis Domain Loadout
//     */
//    fun getGenesisLoadout() = getLoadout(DomainType.GENESIS)
//
//    /**
//     * Rapid Access: Nexus Domain Loadout
//     */
//    fun getNexusLoadout() = getLoadout(DomainType.NEXUS)
//
//    /**
//     * Quick Access: Get only the active asset name (image) for a gate.
//     */
//    fun getActiveAsset(id: String): String? {
//        return get(id)?.let { card ->
//            val def = registry[id]!!
//            val useStyleB = when (def.domain) {
//                DomainType.AURA -> GateAssetConfig.StyleMode.auraStyle == GateAssetConfig.GateStyle.STYLE_B
//                DomainType.KAI -> GateAssetConfig.StyleMode.kaiStyle == GateAssetConfig.GateStyle.STYLE_B
//                DomainType.GENESIS -> GateAssetConfig.StyleMode.genesisStyle == GateAssetConfig.GateStyle.STYLE_B
//                DomainType.NEXUS -> GateAssetConfig.StyleMode.nexusStyle == GateAssetConfig.GateStyle.STYLE_B
//            }
//            if (useStyleB) card.styleBDrawable else card.styleADrawable
//        }
//    }
//}

