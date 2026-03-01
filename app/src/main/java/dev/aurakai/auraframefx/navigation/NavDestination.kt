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
sealed class NavDestination(val route: String, string: String, nothing: Nothing?) {


    // ═══════════════════════════════════════════════════════════════
    // LEVEL 1: PRIMARY GATES (Main Entry Points)
    // ═══════════════════════════════════════════════════════════════
    data object DataflowAnalysis : ReGenesisNavHost("dataflow_analysis")
    data object LsposedQuickToggles : ReGenesisNavHost("lsposed_quick_toggles")
    data object LdoCatalystDevelopment : ReGenesisNavHost("ldo_catalyst_development")
    data object GateCustomization : ReGenesisNavHost("gate_customization")

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 2: DOMAIN HUBS (Main Management Frameworks)
    // ═══════════════════════════════════════════════════════════════

    // Design & Creativity Hubs
    data object AuraThemingHub : ReGenesisNavHost("aura_theming_hub")
    data object AuraLab : ReGenesisNavHost("sandbox_ui")

    // Security & System Control Hubs
    data object RomToolsHub : ReGenesisNavHost("rom_tools_hub")
    data object LSPosedHub : ReGenesisNavHost("lsposed_hub")
    data object SystemToolsHub : ReGenesisNavHost("system_tools_hub")

    // AI & Orchestration Hubs
    data object OracleDriveHub : ReGenesisNavHost("oracle_drive_hub")
    data object OracleCloudStorage : ReGenesisNavHost("oracle_cloud_storage")
    data object AgentBridgeHub : ReGenesisNavHost("agent_bridge_hub")
    data object AgentNexusHub : ReGenesisNavHost("agent_nexus_hub")

    // Progression & Monitoring Hubs
    data object MonitoringHub : ReGenesisNavHost("monitoring_hub")

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 3: UX/UI DESIGN TOOLS (Complete Arsenal - 114+ Settings!)
    // Integrated from: Iconify (69), ColorBlendr (16), PixelLauncherEnhanced (29)
    // ═══════════════════════════════════════════════════════════════

    // --- ICONIFY INTEGRATION (69 Settings) ---
    data object IconifyPicker : ReGenesisNavHost("aura/iconify")
    data object IconifyCategory : ReGenesisNavHost("aura/iconify/{category}") {
        fun createRoute(category: String) = "aura/iconify/$category"
    }

    data object IconifyIconPacks : ReGenesisNavHost("aura/iconify/icon_packs")
    data object IconifyBatteryStyles : ReGenesisNavHost("aura/iconify/battery_styles")
    data object IconifyBrightnessBars : ReGenesisNavHost("aura/iconify/brightness_bars")
    data object IconifyQSPanel : ReGenesisNavHost("aura/iconify/qs_panel")
    data object IconifyNotifications : ReGenesisNavHost("aura/iconify/notifications")
    data object IconifyVolumePanel : ReGenesisNavHost("aura/iconify/volume_panel")
    data object IconifyNavigationBar : ReGenesisNavHost("aura/iconify/navigation_bar")
    data object IconifyUIRoundness : ReGenesisNavHost("aura/iconify/ui_roundness")
    data object IconifyIconShape : ReGenesisNavHost("aura/iconify/icon_shape")
    data object IconifyStatusBar : ReGenesisNavHost("aura/iconify/status_bar")
    data object IconifyXposedFeatures : ReGenesisNavHost("aura/iconify/xposed_features")
    data object IconifyColorEngine : ReGenesisNavHost("aura/iconify/color_engine")
    data object IconPicker : ReGenesisNavHost("aura/iconify/icon_picker/{category}") {
        fun createRoute(category: String) = "aura/iconify/icon_picker/$category"
    }

    // --- COLORBLENDR INTEGRATION (16 Settings) ---
    data object ColorBlendr : ReGenesisNavHost("aura/colorblendr")
    data object ColorBlendrMonet : ReGenesisNavHost("aura/colorblendr/monet")
    data object ColorBlendrPalette : ReGenesisNavHost("aura/colorblendr/palette")
    data object ColorBlendrPerApp : ReGenesisNavHost("aura/colorblendr/per_app")

    // --- PIXEL LAUNCHER ENHANCED INTEGRATION (29 Settings) ---
    data object PixelLauncherEnhanced : ReGenesisNavHost("aura/pixel_launcher_enhanced")
    data object PLEIcons : ReGenesisNavHost("aura/ple/icons")
    data object PLEHomeScreen : ReGenesisNavHost("aura/ple/home_screen")
    data object PLEAppDrawer : ReGenesisNavHost("aura/ple/app_drawer")
    data object PLERecents : ReGenesisNavHost("aura/ple/recents")

    // --- LEGACY/OTHER AURA TOOLS ---
    data object CollabCanvas : ReGenesisNavHost("collab_canvas")
    data object SandboxUi : ReGenesisNavHost("sandbox_ui")
    data object ChromaCore : ReGenesisNavHost("chroma_core")
    data object InstantColorPicker : ReGenesisNavHost("instant_color_picker")
    data object GyroscopeCustomization : ReGenesisNavHost("gyroscope_customization")
    data object ThemeManager : ReGenesisNavHost("theme_manager")
    data object ReGenesisCustomization : ReGenesisNavHost("regenesis_customization")
    data object UISettings : ReGenesisNavHost("ui_settings")
    data object UserPreferences : ReGenesisNavHost("user_preferences")
    data object ThemeEngine : ReGenesisNavHost("theme_engine")
    data object ChromaCoreColors : ReGenesisNavHost("chroma_core_colors")
    data object NotchBar : ReGenesisNavHost("notch_bar")
    data object StatusBar : ReGenesisNavHost("status_bar")
    data object QuickSettings : ReGenesisNavHost("quick_settings")
    data object ChromaCoreHub : ReGenesisNavHost("aura/chroma_core/hub")
    data object ChromaStatusBar : ReGenesisNavHost("aura/chroma_core/statusbar")
    data object ChromaLauncher : ReGenesisNavHost("aura/chroma_core/launcher")
    data object ChromaColorEngine : ReGenesisNavHost("aura/chroma_core/color_engine")
    data object ChromaAnimations : ReGenesisNavHost("aura/chroma_core/animations")
    data object ModuleCreation : ReGenesisNavHost("module_creation")
    data object AgentCreation : ReGenesisNavHost("agent_creation")

    // --- Aliases for User Plan parity ---
    data object AuraCollabCanvas : ReGenesisNavHost("aura_collab_canvas")
    data object AuraSandboxUi : ReGenesisNavHost("aura_sandbox_ui")
    data object AuraChromaCore : ReGenesisNavHost("aura_chroma_core")
    data object AuraInstantColorPicker : ReGenesisNavHost("aura_instant_color_picker")
    data object AuraGyroscopeCustomization : ReGenesisNavHost("aura_gyroscope_customization")
    data object AuraThemeManager : ReGenesisNavHost("aura_theme_manager")
    data object AuraIconifyPicker : ReGenesisNavHost("aura_iconify_picker")
    data object AuraReGenesisCustomization : ReGenesisNavHost("aura_regenesis_customization")
    data object AuraUISettings : ReGenesisNavHost("aura_ui_settings")
    data object AuraUserPreferences : ReGenesisNavHost("aura_user_preferences")

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 3: SECURITY & ROM TOOLS (10+ Features!)
    // ═══════════════════════════════════════════════════════════════
    data object Firewall : ReGenesisNavHost("firewall")
    data object VpnManager : ReGenesisNavHost("vpn_manager")
    data object SecurityScanner : ReGenesisNavHost("security_scanner")
    data object DeviceOptimizer : ReGenesisNavHost("device_optimizer")
    data object PrivacyGuard : ReGenesisNavHost("privacy_guard")
    data object Bootloader : ReGenesisNavHost("bootloader")
    data object BootloaderManager : ReGenesisNavHost("bootloader_manager")
    data object RootTools : ReGenesisNavHost("root_tools")
    data object ROMFlasher : ReGenesisNavHost("rom_flasher")
    data object LiveROMEditor : ReGenesisNavHost("live_rom_editor")
    data object RecoveryTools : ReGenesisNavHost("recovery_tools")
    data object LSPosedModules : ReGenesisNavHost("lsposed_modules")
    data object HookManager : ReGenesisNavHost("hook_manager")
    data object SystemOverrides : ReGenesisNavHost("system_overrides")
    data object ModuleManager : ReGenesisNavHost("module_manager")
    data object SecurityCenter : ReGenesisNavHost("security_center")
    data object VPN : ReGenesisNavHost("vpn")
    data object SentinelFortress : ReGenesisNavHost("sentinel_fortress")
    data object HotSwap : ReGenesisNavHost("hotswap")
    data object Trinity : ReGenesisNavHost("trinity")
    data object SovereignBootloader : ReGenesisNavHost("sovereign_bootloader")
    data object SovereignRecovery : ReGenesisNavHost("sovereign_recovery")
    data object SovereignShield : ReGenesisNavHost("sovereign_shield")

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 3: AI & ORCHESTRATION TOOLS
    // ═══════════════════════════════════════════════════════════════
    data object NeuralArchive : ReGenesisNavHost("neural_archive")
    data object SovereignNeuralArchive : ReGenesisNavHost("sovereign_neural_archive")
    data object CodeAssist : ReGenesisNavHost("code_assist")
    data object NeuralNetwork : ReGenesisNavHost("neural_network")
    data object Terminal : ReGenesisNavHost("terminal")
    data object ConferenceRoom : ReGenesisNavHost("conference_room")

    // ═══════════════════════════════════════════════════════════════
    // LEVEL 3: AGENT NEXUS TOOLS (Multi-Agent Coordination)
    // ═══════════════════════════════════════════════════════════════
    data object AgentHub : ReGenesisNavHost("agent_hub")
    data object DirectChat : ReGenesisNavHost("direct_chat")
    data object TaskAssignment : ReGenesisNavHost("task_assignment")
    data object AgentMonitoring : ReGenesisNavHost("agent_monitoring")
    data object FusionMode : ReGenesisNavHost("fusion_mode")
    data object EvolutionTree : ReGenesisNavHost("evolution_tree")
    data object ArkBuild : ReGenesisNavHost("ark_build")
    data object MetaInstruct : ReGenesisNavHost("meta_instruct")
    data object Nemotron : ReGenesisNavHost("nemotron")
    data object DataStreamMonitoring : ReGenesisNavHost("data_monitor")
    data object Claude : ReGenesisNavHost("claude")
    data object Gemini : ReGenesisNavHost("gemini")
    data object SwarmMonitor : ReGenesisNavHost("swarm_monitor")
    data object BenchmarkMonitor : ReGenesisNavHost("benchmark_monitor")
    data object InterfaceForge : ReGenesisNavHost("interface_forge")
    data object SphereGrid : ReGenesisNavHost("sphere_grid")
    data object DataVeinSphere : ReGenesisNavHost("datavein_sphere")

    // ═══════════════════════════════════════════════════════════════
    // MISSING ROUTES (From string navigate() calls)
    // ═══════════════════════════════════════════════════════════════
    data object GenderSelection : ReGenesisNavHost("gender_selection")
    data object Consciousness : ReGenesisNavHost("consciousness")
    data object Evolution : ReGenesisNavHost("evolution")
    data object Login : ReGenesisNavHost("login")
    data object AiChatBeta : ReGenesisNavHost("ai_chat")
    data object SettingsBeta : ReGenesisNavHost("settings_beta")

    // ═══════════════════════════════════════════════════════════════
    // LDO CATALYST DEVELOPMENT (9 Agent Profile Gates)
    // ═══════════════════════════════════════════════════════════════
    data object LdoAuraProfile : ReGenesisNavHost("ldo_aura_profile")
    data object LdoKaiProfile : ReGenesisNavHost("ldo_kai_profile")
    data object LdoGenesisProfile : ReGenesisNavHost("ldo_genesis_profile")
    data object LdoClaudeProfile : ReGenesisNavHost("ldo_claude_profile")
    data object LdoCascadeProfile : ReGenesisNavHost("ldo_cascade_profile")
    data object LdoGrokProfile : ReGenesisNavHost("ldo_grok_profile")
    data object LdoGeminiProfile : ReGenesisNavHost("ldo_gemini_profile")
    data object LdoNematronProfile : ReGenesisNavHost("ldo_nematron_profile")
    data object LdoPerplexityProfile : ReGenesisNavHost("ldo_perplexity_profile")

    // ═══════════════════════════════════════════════════════════════
    // UTILITY & INFRASTRUCTURE
    // ═══════════════════════════════════════════════════════════════
    data object HelpDesk : ReGenesisNavHost("help_desk")
    data object HelpDeskSubmenu : ReGenesisNavHost("help_desk_submenu")
    data object Documentation : ReGenesisNavHost("documentation")
    data object FAQBrowser : ReGenesisNavHost("faq_browser")
    data object TutorialVideos : ReGenesisNavHost("tutorial_videos")
    data object Settings : ReGenesisNavHost("settings")
}

// Agent Hub
object AgentHub : NavDestination("agent_hub", "Agent Hub", null)
object TaskAssignment : NavDestination("task_assignment", "Task Assignment", null)
object DirectChat : NavDestination("direct_chat", "Direct Chat", null)
object AgentMonitoring : NavDestination("agent_monitoring", "Agent Monitoring", null)
object FusionMode : NavDestination("fusion", "Fusion Mode", null)
object CodeAssist : NavDestination("code_assist", "Code Assist", null)