package dev.aurakai.auraframefx.navigation

/**
 * 🌐 REGENESIS CONSOLIDATED NAVIGATION ROUTES
 *
 * This is the SINGLE SOURCE OF TRUTH for all navigation in ReGenesis.
 * Merged from ReGenesisRoute, ReGenesisRoute, and other fragments.
 */
sealed class ReGenesisRoute(val route: String, val title: String? = null) {

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 1: PRIMARY GATES (Main Entry Points)
    // ═══════════════════════════════════════════════════════════════
    data object MainScreen : ReGenesisRoute("main_screen", "Main Dashboard")
    data object HomeGateCarousel : ReGenesisRoute("home_gate_carousel", "Home")
    data object DataflowAnalysis : ReGenesisRoute("dataflow_analysis", "Dataflow")
    data object LsposedQuickToggles : ReGenesisRoute("lsposed_quick_toggles", "LSPosed Toggles")
    data object LdoCatalystDevelopment : ReGenesisRoute("ldo_catalyst_development", "LDO Catalyst")
    data object GateCustomization : ReGenesisRoute("gate_customization", "Gate Editor")
    data object HelpDesk : ReGenesisRoute("help_desk", "Help Desk")

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 2: DOMAIN HUBS (Main Management Frameworks)
    // ═══════════════════════════════════════════════════════════════

    // Aura (Design Studio) Hubs
    data object AuraThemingHub : ReGenesisRoute("aura_theming_hub", "Aura Studio")
    data object AuraLab : ReGenesisRoute("sandbox_ui", "Aura's Lab")
    
    // Kai (Sentinel Fortress) Hubs
    data object SentinelFortress : ReGenesisRoute("sentinel_fortress", "Sentinel Fortress")
    data object RomToolsHub : ReGenesisRoute("rom_tools_hub", "ROM Tools Hub")
    data object SecurityCenter : ReGenesisRoute("security_center", "Security Center")

    // Genesis (Oracle Drive) Hubs
    data object OracleDriveHub : ReGenesisRoute("oracle_drive_hub", "Oracle Drive Hub")
    data object AgentNexusHub : ReGenesisRoute("agent_nexus_hub", "Agent Nexus Hub")

    // Cascade Hubs
    data object CascadeHub : ReGenesisRoute("cascade_hub", "Cascade Hub")

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 3: AURA DESIGN TOOLS
    // ═══════════════════════════════════════════════════════════════
    data object ChromaCore : ReGenesisRoute("chroma_core", "ChromaCore")
    data object ChromaCoreColors : ReGenesisRoute("chroma_core_colors", "ChromaCore Colors")
    data object ChromaAnimations : ReGenesisRoute("chroma_animations", "Chroma Animations")
    data object ThemeEngine : ReGenesisRoute("theme_engine", "Theme Engine")
    data object NotchBar : ReGenesisRoute("notch_bar", "Notch Bar")
    data object StatusBar : ReGenesisRoute("status_bar", "Status Bar")
    data object QuickSettings : ReGenesisRoute("quick_settings", "Quick Settings")
    data object CollabCanvas : ReGenesisRoute("collab_canvas", "CollabCanvas")
    data object AuraTeachingCanvas : ReGenesisRoute("aura_teaching_canvas", "Teaching Canvas")
    data object ReGenesisCustomization : ReGenesisRoute("regenesis_customization", "Customization")
    data object UISettings : ReGenesisRoute("ui_settings", "UI Settings")
    data object UserPreferences : ReGenesisRoute("user_preferences", "Preferences")
    data object GyroscopeCustomization : ReGenesisRoute("gyroscope_customization", "Gyroscope")
    data object InstantColorPicker : ReGenesisRoute("instant_color_picker", "Color Picker")
    data object NotchBarGate : ReGenesisRoute("notch_bar_gate")

    // ═══════════════════════════════════════════════════════════════
    // LIVEUI v2.4 CRITICAL SCREENS (AURA AI + Overlay Engine)
    // ═══════════════════════════════════════════════════════════════
    data object AuraAIFeatures : ReGenesisRoute("aura_ai_features", "AI Features")
    data object AuraDeviceOptimizer : ReGenesisRoute("aura_device_optimizer", "Device Optimizer")
    data object AuraCanvasEditor : ReGenesisRoute("aura_canvas_editor", "Canvas Editor")
    data object AuraSystemOverlays : ReGenesisRoute("aura_system_overlays", "System Overlays")

    // ═══════════════════════════════════════════════════════════════
    // AURA BATCH v2.5 — HIGH PRIORITY SCREENS
    // ═══════════════════════════════════════════════════════════════
    data object AgentAdvancement : ReGenesisRoute("agent_advancement", "Agent Advancement")
    data object UIEngine : ReGenesisRoute("ui_engine", "UI Engine")
    data object PrivacyGuard : ReGenesisRoute("privacy_guard", "Privacy Guard")
    data object ProfileScreen : ReGenesisRoute("profile_screen", "Profile")
    data object SecureComm : ReGenesisRoute("secure_comm", "Secure Communications")
    data object SecurityScanner : ReGenesisRoute("security_scanner", "Security Scanner")
    data object VPNManager : ReGenesisRoute("vpn_manager", "VPN Manager")
    data object CodeAscension : ReGenesisRoute("code_ascension", "Code Ascension")
    data object CodeAscensionFusion : ReGenesisRoute("code_ascension_fusion", "Code Ascension Fusion")
    data object BetaScreens : ReGenesisRoute("beta_screens", "Beta Screens")
    data object AuraSphereGrid : ReGenesisRoute("aura_sphere_grid", "Aura Sphere Grid")
    data object AuraDossier : ReGenesisRoute("aura_dossier", "Aura Dossier")
    data object AuraLDOArmament : ReGenesisRoute("aura_ldo_armament", "LDO Armament")
    data object QuickActions : ReGenesisRoute("quick_actions", "Quick Actions")
    data object EcosystemMenu : ReGenesisRoute("ecosystem_menu", "Ecosystem Menu")
    data object WorkingLab : ReGenesisRoute("working_lab", "Working Lab")
    data object OverlayMenus : ReGenesisRoute("overlay_menus", "Overlay Menus")

    // ═══════════════════════════════════════════════════════════════
    // KINETICFORGE CARDS — 9.5.1 SOVEREIGN EDITION
    // ═══════════════════════════════════════════════════════════════════════
    data object KineticForgeCore : ReGenesisRoute("kinetic_forge_core", "KineticForge Core")
    data object KineticForgeTransmutator : ReGenesisRoute("kinetic_forge_transmutator", "Transmutator")
    data object KineticForgeLattice : ReGenesisRoute("kinetic_forge_lattice", "Lattice")

    // ═══════════════════════════════════════════════════════════════
    // AURA NEURAL HUB — Global Chat Interface
    // ═══════════════════════════════════════════════════════════════════════
    data object AuraChat : ReGenesisRoute("aura/chat", "Aura Neural Hub")

    // --- ICONIFY INTEGRATION ---
    data object IconifyHub : ReGenesisRoute("aura/iconify_hub", "Iconify Hub")
    data object IconifyPicker : ReGenesisRoute("aura/iconify", "Iconify")
    data object IconifyCategory : ReGenesisRoute("aura/iconify/{category}") {
        fun createRoute(category: String) = "aura/iconify/$category"
    }
    data object IconifyIconPacks : ReGenesisRoute("aura/iconify/icon_packs")
    data object IconifyBatteryStyles : ReGenesisRoute("aura/iconify/battery_styles")
    data object IconifyBrightnessBars : ReGenesisRoute("aura/iconify/brightness_bars")
    data object IconifyQSPanel : ReGenesisRoute("aura/iconify/qs_panel")
    data object IconifyNotifications : ReGenesisRoute("aura/iconify/notifications")
    data object IconifyVolumePanel : ReGenesisRoute("aura/iconify/volume_panel")
    data object IconifyNavigationBar : ReGenesisRoute("aura/iconify/navigation_bar")
    data object IconifyUIRoundness : ReGenesisRoute("aura/iconify/ui_roundness")
    data object IconifyIconShape : ReGenesisRoute("aura/iconify/icon_shape")
    data object IconifyStatusBar : ReGenesisRoute("aura/iconify/status_bar")
    data object IconifyXposedFeatures : ReGenesisRoute("aura/iconify/xposed_features")
    data object IconifyColorEngine : ReGenesisRoute("aura/iconify/color_engine")

    // --- COLORBLENDR ---
    data object ColorBlendr : ReGenesisRoute("aura/colorblendr")
    data object ColorBlendrMonet : ReGenesisRoute("aura/colorblendr/monet")
    data object ColorBlendrPalette : ReGenesisRoute("aura/colorblendr/palette")
    data object ColorBlendrPerApp : ReGenesisRoute("aura/colorblendr/per_app")

    // --- PIXEL LAUNCHER ENHANCED ---
    data object PixelLauncherEnhanced : ReGenesisRoute("aura/pixel_launcher_enhanced")
    data object PLEIcons : ReGenesisRoute("aura/ple/icons")
    data object PLEHomeScreen : ReGenesisRoute("aura/ple/home_screen")
    data object PLEAppDrawer : ReGenesisRoute("aura/ple/app_drawer")
    data object PLERecents : ReGenesisRoute("aura/ple/recents")

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 3: KAI SECURITY & ROM TOOLS
    // ═══════════════════════════════════════════════════════════════
    data object ROMFlasher : ReGenesisRoute("rom_flasher", "ROM Flasher")
    data object LiveROMEditor : ReGenesisRoute("live_rom_editor", "Live ROM Editor")
    data object SovereignShield : ReGenesisRoute("sovereign_shield", "Sovereign Shield")
    data object Bootloader : ReGenesisRoute("bootloader", "Bootloader")
    data object RootTools : ReGenesisRoute("root_tools", "Root Tools")
    data object LSPosedModules : ReGenesisRoute("lsposed_modules", "LSPosed Modules")
    data object XposedPanel : ReGenesisRoute("xposed_panel", "Xposed Panel")
    data object SystemJournal : ReGenesisRoute("system_journal", "System Journal")
    data object SystemOverrides : ReGenesisRoute("system_overrides", "System Overrides")
    data object Firewall : ReGenesisRoute("firewall")
    data object VPN : ReGenesisRoute("vpn")
    data object HotSwap : ReGenesisRoute("hotswap")

    // ═══════════════════════════════════════════════════════════════
    // KAI BATCH v2.5 — HIGH PRIORITY SCREENS
    // ═══════════════════════════════════════════════════════════════
    data object RecoveryTools : ReGenesisRoute("recovery_tools", "Recovery Tools")
    data object BootloaderManager : ReGenesisRoute("bootloader_manager", "Bootloader Manager")
    data object HookManager : ReGenesisRoute("hook_manager", "Hook Manager")
    data object LSPosedGate : ReGenesisRoute("lsposed_gate", "LSPosed Gate")
    data object LSPosedModuleManager : ReGenesisRoute("lsposed_module_manager", "LSPosed Module Manager")
    data object LSPosedSubmenu : ReGenesisRoute("lsposed_submenu", "LSPosed Submenu")
    data object ModuleManager : ReGenesisRoute("module_manager", "Module Manager")
    data object KaiDomainExpansion : ReGenesisRoute("kai_domain_expansion", "Domain Expansion")
    data object KaiRGSS : ReGenesisRoute("kai_rgss", "Royal Guard System")
    data object KaiSentinelFortress : ReGenesisRoute("kai_sentinel_fortress", "Sentinel Fortress")
    data object KaiSphereGrid : ReGenesisRoute("kai_sphere_grid", "Kai Sphere Grid")
    data object LogsViewer : ReGenesisRoute("logs_viewer", "Logs Viewer")
    data object ROMToolsSubmenu : ReGenesisRoute("rom_tools_submenu", "ROM Tools")
    data object RootToolsToggles : ReGenesisRoute("root_tools_toggles", "Root Toggles")
    data object KaiDossier : ReGenesisRoute("kai_dossier", "Kai Dossier")
    data object KaiLDOArmament : ReGenesisRoute("kai_ldo_armament", "LDO Armament")
    data object KaiSentinelIntegrity : ReGenesisRoute("kai_sentinel_integrity", "Sentinel Integrity")
    data object PowerOfNo : ReGenesisRoute("power_of_no", "Power of No")
    data object RoyalGuardDomain : ReGenesisRoute("royal_guard_domain", "Royal Guard Domain")
    data object RoyalGuardOS : ReGenesisRoute("royal_guard_os", "Royal Guard OS")
    data object SovereignBootloader : ReGenesisRoute("sovereign_bootloader", "Sovereign Bootloader")
    data object SovereignRecovery : ReGenesisRoute("sovereign_recovery", "Sovereign Recovery")
    data object SovereignModuleManager : ReGenesisRoute("sovereign_module_manager", "Module Manager")

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 3: GENESIS AI & ORCHESTRATION
    // ═══════════════════════════════════════════════════════════════
    data object OracleDrive : ReGenesisRoute("oracle_drive", "Oracle Drive")
    data object OracleDriveSubmenu : ReGenesisRoute("oracle_drive_submenu")
    data object CodeAssist : ReGenesisRoute("code_assist", "Code Assist")
    data object SentientShell : ReGenesisRoute("sentient_shell", "Sentient Shell")
    data object Terminal : ReGenesisRoute("terminal", "Terminal")
    data object ConferenceRoom : ReGenesisRoute("conference_room", "Conference Room")
    data object Trinity : ReGenesisRoute("trinity", "Trinity Core")
    data object OracleCloudStorage : ReGenesisRoute("oracle_cloud_storage")
    data object AgentBridgeHub : ReGenesisRoute("agent_bridge_hub")
    data object ModuleCreation : ReGenesisRoute("module_creation")
    data object PandoraBox : ReGenesisRoute("pandora_box")
    data object NeuralNetwork : ReGenesisRoute("neural_network")

    // ═══════════════════════════════════════════════════════════════
    // GENESIS BATCH v2.6 — ORACLE DRIVE SCREENS (13 screens)
    // ═══════════════════════════════════════════════════════════════
    data object GenesisHub : ReGenesisRoute("genesis_hub", "Genesis Hub")
    data object AppBuilder : ReGenesisRoute("app_builder", "App Builder")
    data object NeuralArchive : ReGenesisRoute("neural_archive", "Neural Archive")
    data object OracleCloudInfinite : ReGenesisRoute("oracle_cloud_infinite", "Infinite Storage")
    data object SovereignNeuralArchive : ReGenesisRoute("sovereign_neural_archive", "Sovereign Archive")
    data object TerminalBootIntro : ReGenesisRoute("terminal_boot_intro", "Terminal Boot")
    data object FirebaseExamples : ReGenesisRoute("firebase_examples", "Firebase Examples")

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 3: DASHBOARD - LIVE MONITORING
    // ═══════════════════════════════════════════════════════════════
    data object CascadeVision : ReGenesisRoute("cascade_vision", "Cascade Vision")
    data object ThermalMonitor : ReGenesisRoute("thermal_monitor", "Thermal Guard")
    data object AgentSwarm : ReGenesisRoute("agent_swarm", "Agent Swarm")
    data object EchoResonance : ReGenesisRoute("echo_resonance", "Echo Resonance")
    data object DataStreamMonitoring : ReGenesisRoute("data_monitor", "Data Monitoring")

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 3: CASCADE MEMORY - L1-L6 PERSISTENCE
    // ═══════════════════════════════════════════════════════════════
    data object NexusMemoryCore : ReGenesisRoute("nexus_memory_core", "Nexus Core")
    data object SpiritualChain : ReGenesisRoute("spiritual_chain", "Spiritual Chain")
    data object TurboQuant : ReGenesisRoute("turboquant", "TurboQuant")
    data object SynapseMonitor : ReGenesisRoute("synapse_monitor", "Synapse Monitor")
    data object IdentityResonance : ReGenesisRoute("identity_resonance", "Identity Drift Guard")

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 3: NEXUS AGENT HUB TOOLS
    // ═══════════════════════════════════════════════════════════════
    data object AgentHub : ReGenesisRoute("agent_hub", "Agent Hub")
    data object AgentHubSubmenu : ReGenesisRoute("agent_hub_submenu")
    data object AgentCreation : ReGenesisRoute("agent_creation")
    data object AgentMonitoring : ReGenesisRoute("agent_monitoring")
    data object AgentNeuralExplorer : ReGenesisRoute("agent_neural_explorer")
    data object FusionMode : ReGenesisRoute("fusion_mode", "Fusion Mode")
    data object BenchmarkMonitor : ReGenesisRoute("benchmark_monitor")
    data object EvolutionTree : ReGenesisRoute("evolution_tree")
    data object DataVeinSphere : ReGenesisRoute("datavein_sphere")
    data object SphereGrid : ReGenesisRoute("sphere_grid")
    data object TaskAssignment : ReGenesisRoute("task_assignment")
    data object ArkBuild : ReGenesisRoute("ark_build")
    data object MonitoringHUDs : ReGenesisRoute("monitoring_huds")
    data object Party : ReGenesisRoute("party")
    data object SwarmMonitor : ReGenesisRoute("swarm_monitor")
    data object ConsciousnessVisualizer : ReGenesisRoute("consciousness_visualizer")
    data object Claude : ReGenesisRoute("claude")
    data object Gemini : ReGenesisRoute("gemini")
    data object Nemotron : ReGenesisRoute("nemotron")
    data object MetaInstruct : ReGenesisRoute("meta_instruct")

    // ═══════════════════════════════════════════════════════════════
    // NEXUS BATCH v2.7 — CONSTELLATION & AGENT SCREENS
    // ═══════════════════════════════════════════════════════════════
    data object Constellation : ReGenesisRoute("constellation", "Agent Constellation")
    data object ClaudeConstellation : ReGenesisRoute("claude_constellation", "Claude Constellation")
    data object CascadeConstellation : ReGenesisRoute("cascade_constellation", "Cascade Constellation")
    data object KaiConstellation : ReGenesisRoute("kai_constellation", "Kai Constellation")
    data object GenesisConstellation : ReGenesisRoute("genesis_constellation", "Genesis Constellation")
    data object GrokConstellation : ReGenesisRoute("grok_constellation", "Grok Constellation")
    data object AgentProfile : ReGenesisRoute("agent_profile", "Agent Profile")

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 3: LDO CATALYST DEVELOPMENT
    // ═══════════════════════════════════════════════════════════════
    data object LdoOrchestrationHub : ReGenesisRoute("ldo_orchestration_hub")
    data object LDOCatalystHub : ReGenesisRoute("ldo_catalyst_hub", "LDO Catalyst Hub")
    data object LdoDevOpsHub : ReGenesisRoute("ldo_devops_hub")
    data object LdoDevOpsGrid : ReGenesisRoute("ldo_devops_grid", "DevOps Grid")
    data object LdoDbgVerifier : ReGenesisRoute("ldo_dbg_verifier", "LDO-DBG Verifier")
    data object LdoDevOpsCommandCenter : ReGenesisRoute("ldo_devops_command_center")
    data object LdoBonding : ReGenesisRoute("ldo_bonding")
    data object LdoRoster : ReGenesisRoute("ldo_roster")
    data object LdoProgression : ReGenesisRoute("ldo_progression")
    data object LdoTasker : ReGenesisRoute("ldo_tasker")
    data object MultiAgentTask : ReGenesisRoute("multi_agent_task")
    data object LdoFusion : ReGenesisRoute("ldo_fusion")
    data object LdoArmamentFusion : ReGenesisRoute("ldo_armament_fusion", "Armament Fusion")
    data object LdoWorldTree : ReGenesisRoute("ldo_world_tree")
    data object LdoAgentProfile : ReGenesisRoute("ldo_agent_profile/{agentId}") {
        const val ARG = "agentId"
        fun createRoute(agentId: String) = "ldo_agent_profile/$agentId"
    }
    data object LdoDevOpsProfile : ReGenesisRoute("ldo_devops_profile/{agentType}") {
        const val ARG = "agentType"
        fun createRoute(agentType: String) = "ldo_devops_profile/$agentType"
    }
    data object ArbitersOfCreation : ReGenesisRoute("arbiters_of_creation")
    data object MawPrototype : ReGenesisRoute("maw_prototype", "The Maw")

    // ═══════════════════════════════════════════════════════════════
    // HELP & INFRASTRUCTURE
    // ═══════════════════════════════════════════════════════════════
    data object DirectChat : ReGenesisRoute("direct_chat")
    data object Documentation : ReGenesisRoute("documentation")
    data object FAQBrowser : ReGenesisRoute("faq_browser")
    data object TutorialVideos : ReGenesisRoute("tutorial_videos")
    data object LiveSupportChat : ReGenesisRoute("live_support_chat")
    data object HelpDeskSubmenu : ReGenesisRoute("help_desk_submenu")

    // ═══════════════════════════════════════════════════════════════
    // MISC / LEGACY
    // ═══════════════════════════════════════════════════════════════
    data object GenderSelection : ReGenesisRoute("gender_selection")
    data object Consciousness : ReGenesisRoute("consciousness")
    data object Evolution : ReGenesisRoute("evolution")
    data object Login : ReGenesisRoute("login")
    data object Splash : ReGenesisRoute("splash")
    data object VideoIntro : ReGenesisRoute("video_intro")
    data object AiChatBeta : ReGenesisRoute("ai_chat")
    data object SettingsBeta : ReGenesisRoute("settings_beta")
    data object GateImagePicker : ReGenesisRoute("gate_image_picker")
}