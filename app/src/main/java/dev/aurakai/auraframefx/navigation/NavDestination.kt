package dev.aurakai.auraframefx.navigation

/**
 * 🌐 REGENESIS NAVIGATION DESTINATIONS
 *
 * CLEAN HIERARCHY - ZERO DRIFT:
 *
 * LEVEL 1: ExodusHUD (Main Gates)
 * ├── UX/UI Design Studio → AuraThemingHub
 * ├── Sentinel Fortress → RomToolsHub
 * ├── Oracle Drive → OracleDriveHub
 * ├── Agent Nexus → AgentNexusHub
 * ├── LSPosed Quick Toggles → LsposedQuickToggles
 * ├── Help Services → HelpDesk
 * ├── Dataflow Analysis → DataflowAnalysis
 * └── LDO Catalyst Development → LdoCatalystDevelopment
 *
 * LEVEL 2: Domain Hubs (Main management screens)
 * └── Each hub contains tool screens
 *
 * LEVEL 3: Tool Screens (Individual features within each domain)
 */
sealed class NavDestination(val route: String) {

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 0: EXODUS HUD (Main Gate Carousel)
    // ═══════════════════════════════════════════════════════════════
    data object HomeGateCarousel : NavDestination("exodus_hud")

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 1: PRIMARY GATES (Main Entry Points)
    // ═══════════════════════════════════════════════════════════════
    data object DataflowAnalysis : NavDestination("dataflow_analysis")
    data object LsposedQuickToggles : NavDestination("lsposed_quick_toggles")
    data object LdoCatalystDevelopment : NavDestination("ldo_catalyst_development")

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 2: DOMAIN HUBS (Main Management Frameworks)
    // ═══════════════════════════════════════════════════════════════

    // Design & Creativity Hubs
    data object AuraThemingHub : NavDestination("aura_theming_hub")
    data object AuraLab : NavDestination("sandbox_ui")

    // Security & System Control Hubs
    data object RomToolsHub : NavDestination("rom_tools_hub")
    data object LSPosedHub : NavDestination("lsposed_hub")
    data object SystemToolsHub : NavDestination("system_tools_hub")

    // AI & Orchestration Hubs
    data object OracleDriveHub : NavDestination("oracle_drive_hub")
    data object OracleCloudStorage : NavDestination("oracle_cloud_storage")
    data object AgentBridgeHub : NavDestination("agent_bridge_hub")
    data object AgentNexusHub : NavDestination("agent_nexus_hub")

    // Progression & Monitoring Hubs
    data object MonitoringHub : NavDestination("monitoring_hub")
    data object MonitoringHUDs : NavDestination("monitoring_huds")

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 3: UX/UI DESIGN TOOLS (Complete Arsenal - 114+ Settings!)
    // Integrated from: Iconify (69), ColorBlendr (16), PixelLauncherEnhanced (29)
    // ═══════════════════════════════════════════════════════════════

    // --- ICONIFY INTEGRATION (69 Settings) ---
    data object IconifyPicker : NavDestination("aura/iconify")
    data object IconifyCategory : NavDestination("aura/iconify/{category}") {
        fun createRoute(category: String) = "aura/iconify/$category"
    }
    data object IconifyIconPacks : NavDestination("aura/iconify/icon_packs")
    data object IconifyBatteryStyles : NavDestination("aura/iconify/battery_styles")
    data object IconifyBrightnessBars : NavDestination("aura/iconify/brightness_bars")
    data object IconifyQSPanel : NavDestination("aura/iconify/qs_panel")
    data object IconifyNotifications : NavDestination("aura/iconify/notifications")
    data object IconifyVolumePanel : NavDestination("aura/iconify/volume_panel")
    data object IconifyNavigationBar : NavDestination("aura/iconify/navigation_bar")
    data object IconifyUIRoundness : NavDestination("aura/iconify/ui_roundness")
    data object IconifyIconShape : NavDestination("aura/iconify/icon_shape")
    data object IconifyStatusBar : NavDestination("aura/iconify/status_bar")
    data object IconifyXposedFeatures : NavDestination("aura/iconify/xposed_features")
    data object IconifyColorEngine : NavDestination("aura/iconify/color_engine")
    data object IconPicker : NavDestination("aura/iconify/icon_picker/{category}") {
        fun createRoute(category: String) = "aura/iconify/icon_picker/$category"
    }

    // --- COLORBLENDR INTEGRATION (16 Settings) ---
    data object ColorBlendr : NavDestination("aura/colorblendr")
    data object ColorBlendrMonet : NavDestination("aura/colorblendr/monet")
    data object ColorBlendrPalette : NavDestination("aura/colorblendr/palette")
    data object ColorBlendrPerApp : NavDestination("aura/colorblendr/per_app")

    // --- PIXEL LAUNCHER ENHANCED INTEGRATION (29 Settings) ---
    data object PixelLauncherEnhanced : NavDestination("aura/pixel_launcher_enhanced")
    data object PLEIcons : NavDestination("aura/ple/icons")
    data object PLEHomeScreen : NavDestination("aura/ple/home_screen")
    data object PLEAppDrawer : NavDestination("aura/ple/app_drawer")
    data object PLERecents : NavDestination("aura/ple/recents")

    // --- LEGACY/OTHER AURA TOOLS ---
    data object CollabCanvas : NavDestination("collab_canvas")
    data object SandboxUi : NavDestination("sandbox_ui")
    data object ChromaCore : NavDestination("chroma_core")
    data object InstantColorPicker : NavDestination("instant_color_picker")
    data object GyroscopeCustomization : NavDestination("gyroscope_customization")
    data object ThemeManager : NavDestination("theme_manager")
    data object ReGenesisCustomization : NavDestination("regenesis_customization")
    data object UISettings : NavDestination("ui_settings")
    data object UserPreferences : NavDestination("user_preferences")
    data object GateCustomization : NavDestination("gate_customization")
    data object ThemeEngine : NavDestination("theme_engine")
    data object StatusBar : NavDestination("status_bar")
    data object ChromaCoreHub : NavDestination("aura/chroma_core/hub")
    data object ChromaStatusBar : NavDestination("aura/chroma_core/statusbar")
    data object ChromaLauncher : NavDestination("aura/chroma_core/launcher")
    data object ChromaColorEngine : NavDestination("aura/chroma_core/color_engine")
    data object ChromaAnimations : NavDestination("aura/chroma_core/animations")
    data object ModuleCreation : NavDestination("module_creation")
    data object AgentCreation : NavDestination("agent_creation")

    // --- Aliases for User Plan parity ---
    data object AuraCollabCanvas : NavDestination("aura_collab_canvas")
    data object AuraSandboxUi : NavDestination("aura_sandbox_ui")
    data object AuraChromaCore : NavDestination("aura_chroma_core")
    data object AuraInstantColorPicker : NavDestination("aura_instant_color_picker")
    data object AuraGyroscopeCustomization : NavDestination("aura_gyroscope_customization")
    data object AuraThemeManager : NavDestination("aura_theme_manager")
    data object AuraIconifyPicker : NavDestination("aura_iconify_picker")
    data object AuraReGenesisCustomization : NavDestination("aura_regenesis_customization")
    data object AuraUISettings : NavDestination("aura_ui_settings")
    data object AuraUserPreferences : NavDestination("aura_user_preferences")

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 3: SECURITY & ROM TOOLS (10+ Features!)
    // ═══════════════════════════════════════════════════════════════
    data object Firewall : NavDestination("firewall")
    data object VpnManager : NavDestination("vpn_manager")
    data object SecurityScanner : NavDestination("security_scanner")
    data object DeviceOptimizer : NavDestination("device_optimizer")
    data object PrivacyGuard : NavDestination("privacy_guard")
    data object Bootloader : NavDestination("bootloader")
    data object BootloaderManager : NavDestination("bootloader_manager")
    data object RootTools : NavDestination("root_tools")
    data object ROMFlasher : NavDestination("rom_flasher")
    data object LiveROMEditor : NavDestination("live_rom_editor")
    data object RecoveryTools : NavDestination("recovery_tools")
    data object LSPosedModules : NavDestination("lsposed_modules")
    data object HookManager : NavDestination("hook_manager")
    data object SystemOverrides : NavDestination("system_overrides")
    data object ModuleManager : NavDestination("module_manager")
    data object SecurityCenter : NavDestination("security_center")
    data object VPN : NavDestination("vpn")
    data object SentinelFortress : NavDestination("sentinel_fortress")
    data object HotSwap : NavDestination("hotswap")
    data object Trinity : NavDestination("trinity")
    data object SovereignBootloader : NavDestination("sovereign_bootloader")
    data object SovereignRecovery : NavDestination("sovereign_recovery")
    data object SovereignShield : NavDestination("sovereign_shield")
    data object SystemJournal : NavDestination("system_journal")
    data object LogsViewer : NavDestination("logs_viewer")
    data object SovereignModuleManager : NavDestination("sovereign_module_manager")
    data object RomToolsSubmenu : NavDestination("rom_tools_submenu")

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 3: AI & ORCHESTRATION TOOLS
    // ═══════════════════════════════════════════════════════════════
    data object NeuralArchive : NavDestination("neural_archive")
    data object SovereignNeuralArchive : NavDestination("sovereign_neural_archive")
    data object CodeAssist : NavDestination("code_assist")
    data object NeuralNetwork : NavDestination("neural_network")
    data object Terminal : NavDestination("terminal")
    data object ConferenceRoom : NavDestination("conference_room")
    data object SentientShell : NavDestination("sentient_shell")
    data object CascadeVision : NavDestination("cascade_vision")
    data object OracleDriveSubmenu : NavDestination("oracle_drive_submenu")
    data object OracleDrive : NavDestination("oracle_drive")

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 3: AGENT NEXUS TOOLS (Multi-Agent Coordination)
    // ═══════════════════════════════════════════════════════════════
    data object AgentHub : NavDestination("agent_hub")
    data object DirectChat : NavDestination("direct_chat")
    data object TaskAssignment : NavDestination("task_assignment")
    data object AgentMonitoring : NavDestination("agent_monitoring")
    data object FusionMode : NavDestination("fusion_mode")
    data object EvolutionTree : NavDestination("evolution_tree")
    data object ArkBuild : NavDestination("ark_build")
    data object MetaInstruct : NavDestination("meta_instruct")
    data object Nemotron : NavDestination("nemotron")
    data object DataStreamMonitoring : NavDestination("data_monitor")
    data object Claude : NavDestination("claude")
    data object Gemini : NavDestination("gemini")
    data object SwarmMonitor : NavDestination("swarm_monitor")
    data object BenchmarkMonitor : NavDestination("benchmark_monitor")
    data object InterfaceForge : NavDestination("interface_forge")
    data object SphereGrid : NavDestination("sphere_grid")
    data object DataVeinSphere : NavDestination("datavein_sphere")
    data object AgentHubSubmenu : NavDestination("agent_hub_submenu")
    data object AgentNeuralExplorer : NavDestination("agent_neural_explorer")
    data object AgentProfileNexus : NavDestination("nexus_agent_profile")
    data object AgentProfileAura : NavDestination("aura_agent_profile")
    data object Party : NavDestination("party")

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 3: SPECIALIZED SCREENS
    // ═══════════════════════════════════════════════════════════════
    data object RomTools : NavDestination("rom_tools")
    data object LiveSupportChat : NavDestination("live_support_chat")

    // ═══════════════════════════════════════════════════════════════
    // MISSING ROUTES (From string navigate() calls)
    // ═══════════════════════════════════════════════════════════════
    data object GenderSelection : NavDestination("gender_selection")
    data object Consciousness : NavDestination("consciousness")
    data object Evolution : NavDestination("evolution")
    data object Login : NavDestination("login")
    data object AiChatBeta : NavDestination("ai_chat")
    data object SettingsBeta : NavDestination("settings_beta")

    // ═══════════════════════════════════════════════════════════════
    // LDO CATALYST DEVELOPMENT (9 Agent Profile Gates)
    // ═══════════════════════════════════════════════════════════════
    data object LdoAuraProfile : NavDestination("ldo_aura_profile")
    data object LdoKaiProfile : NavDestination("ldo_kai_profile")
    data object LdoGenesisProfile : NavDestination("ldo_genesis_profile")
    data object LdoClaudeProfile : NavDestination("ldo_claude_profile")
    data object LdoCascadeProfile : NavDestination("ldo_cascade_profile")
    data object LdoGrokProfile : NavDestination("ldo_grok_profile")
    data object LdoGeminiProfile : NavDestination("ldo_gemini_profile")
    data object LdoNematronProfile : NavDestination("ldo_nematron_profile")
    data object LdoPerplexityProfile : NavDestination("ldo_perplexity_profile")

    // ═══════════════════════════════════════════════════════════════
    // UTILITY & INFRASTRUCTURE
    // ═══════════════════════════════════════════════════════════════
    data object HelpDesk : NavDestination("help_desk")
    data object HelpDeskSubmenu : NavDestination("help_desk_submenu")
    data object Documentation : NavDestination("documentation")
    data object FAQBrowser : NavDestination("faq_browser")
    data object TutorialVideos : NavDestination("tutorial_videos")
}
