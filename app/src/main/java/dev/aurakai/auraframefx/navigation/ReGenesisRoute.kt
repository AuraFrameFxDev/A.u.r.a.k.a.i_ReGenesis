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

    // --- ICONIFY INTEGRATION ---
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
    data object SovereignRecovery : ReGenesisRoute("sovereign_recovery")
    data object SovereignModuleManager : ReGenesisRoute("sovereign_module_manager")
    data object ModuleCreation : ReGenesisRoute("module_creation")
    data object PandoraBox : ReGenesisRoute("pandora_box")
    data object NeuralNetwork : ReGenesisRoute("neural_network")

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 3: NEXUS AGENT HUB TOOLS
    // ═══════════════════════════════════════════════════════════════
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
    // LEVEL 3: LDO CATALYST DEVELOPMENT
    // ═══════════════════════════════════════════════════════════════
    data object LdoOrchestrationHub : ReGenesisRoute("ldo_orchestration_hub")
    data object LdoDevOpsHub : ReGenesisRoute("ldo_devops_hub")
    data object LdoDevOpsCommandCenter : ReGenesisRoute("ldo_devops_command_center")
    data object LdoBonding : ReGenesisRoute("ldo_bonding")
    data object LdoRoster : ReGenesisRoute("ldo_roster")
    data object LdoProgression : ReGenesisRoute("ldo_progression")
    data object LdoTasker : ReGenesisRoute("ldo_tasker")
    data object LdoAgentProfile : ReGenesisRoute("ldo_agent_profile/{agentId}") {
        const val ARG = "agentId"
        fun createRoute(agentId: String) = "ldo_agent_profile/$agentId"
    }
    data object ArbitersOfCreation : ReGenesisRoute("arbiters_of_creation")

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
    data object AiChatBeta : ReGenesisRoute("ai_chat")
    data object SettingsBeta : ReGenesisRoute("settings_beta")
    data object GateImagePicker : ReGenesisRoute("gate_image_picker")
}