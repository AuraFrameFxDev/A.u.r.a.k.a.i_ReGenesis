package dev.aurakai.auraframefx.navigation

// Core Imports (kept from your list for screen definitions)

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

// Core
import dev.aurakai.auraframefx.config.GateAssetLoadout
import dev.aurakai.auraframefx.domains.aura.lab.CustomizationViewModel
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.ChromaCoreHubScreen
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.ChromaLauncherMenu
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.ChromaStatusBarMenu
import dev.aurakai.auraframefx.domains.aura.lab.CustomizationViewModel
import dev.aurakai.auraframefx.domains.aura.screens.DirectChatScreen
import dev.aurakai.auraframefx.domains.aura.screens.DocumentationScreen
import dev.aurakai.auraframefx.domains.aura.screens.FAQBrowserScreen
import dev.aurakai.auraframefx.domains.aura.screens.GenderSelectionScreen
import dev.aurakai.auraframefx.domains.aura.screens.HelpDeskSubmenuScreen
import dev.aurakai.auraframefx.domains.aura.screens.TutorialVideosScreen
import dev.aurakai.auraframefx.domains.aura.screens.UserPreferencesScreen
import dev.aurakai.auraframefx.domains.aura.screens.chromacore.InstantColorPickerScreen
import dev.aurakai.auraframefx.domains.aura.screens.themes.ThemeEngineScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.AurasLabScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.GyroscopeCustomizationScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.StatusBarScreen
import dev.aurakai.auraframefx.domains.aura.screens.themes.ThemeEngineScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.UISettingsScreen
import dev.aurakai.auraframefx.domains.aura.ui.screens.aura.ReGenesisCustomizationHub
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.trinity.TrinityScreen
import dev.aurakai.auraframefx.domains.genesis.models.AgentType
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ui.OracleDriveScreen
import dev.aurakai.auraframefx.domains.genesis.screens.AgentBridgeHubScreen
import dev.aurakai.auraframefx.domains.genesis.screens.AppBuilderScreen
import dev.aurakai.auraframefx.domains.genesis.screens.CascadeVisionScreen
import dev.aurakai.auraframefx.domains.genesis.screens.CodeAssistScreen
import dev.aurakai.auraframefx.domains.genesis.screens.CollabCanvasScreen
import dev.aurakai.auraframefx.domains.genesis.screens.ConferenceRoomScreen
import dev.aurakai.auraframefx.domains.genesis.screens.NeuralArchiveScreen
import dev.aurakai.auraframefx.domains.genesis.screens.OracleCloudInfiniteStorageScreen
import dev.aurakai.auraframefx.domains.genesis.screens.OracleDriveSubmenuScreen
import dev.aurakai.auraframefx.domains.genesis.screens.SentientShellScreen
import dev.aurakai.auraframefx.domains.genesis.screens.SovereignNeuralArchiveScreen
import dev.aurakai.auraframefx.domains.genesis.screens.TerminalScreen
import dev.aurakai.auraframefx.domains.kai.screens.LogsViewerScreen
import dev.aurakai.auraframefx.domains.kai.screens.ModuleManagerScreen
import dev.aurakai.auraframefx.domains.kai.screens.ROMToolsSubmenuScreen
import dev.aurakai.auraframefx.domains.kai.screens.ROMToolsSubmenuScreen
import dev.aurakai.auraframefx.domains.kai.screens.RootToolsTogglesScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.SecurityCenterScreen
import dev.aurakai.auraframefx.domains.lsposed.screens.LSPosedSubmenuScreen
import dev.aurakai.auraframefx.domains.lsposed.screens.LsposedQuickTogglesScreen
import dev.aurakai.auraframefx.domains.lsposed.screens.LSPosedModuleManagerScreen
import dev.aurakai.auraframefx.domains.lsposed.screens.HookManagerScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.LiveROMEditorScreen
import dev.aurakai.auraframefx.domains.kai.screens.SystemJournalScreen
import dev.aurakai.auraframefx.domains.kai.screens.SystemOverridesScreen
import dev.aurakai.auraframefx.domains.kai.screens.LogsViewerScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.VPNScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.SovereignModuleManagerScreen
import dev.aurakai.auraframefx.domains.kai.screens.ROMToolsSubmenuScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.SovereignBootloaderScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.SovereignRecoveryScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.SecurityCenterScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.SovereignShieldScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.VPNScreen
import dev.aurakai.auraframefx.domains.lsposed.screens.HookManagerScreen
import dev.aurakai.auraframefx.domains.lsposed.screens.LSPosedModuleManagerScreen
import dev.aurakai.auraframefx.domains.lsposed.screens.LSPosedSubmenuScreen
import dev.aurakai.auraframefx.domains.ldo.screens.ArmamentFusionScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDOAgentProfileIntroScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDOBondingScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDODevOpsHubScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDOOrchestrationHubScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDOProgressionScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDORosterScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDOTaskerScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentCreationScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentHubSubmenuScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentMonitoringScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentNeuralExplorerScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentSwarmScreen
import dev.aurakai.auraframefx.domains.nexus.screens.ArkBuildScreen
import dev.aurakai.auraframefx.domains.nexus.screens.BenchmarkMonitorScreen
import dev.aurakai.auraframefx.domains.nexus.screens.DataStreamMonitoringScreen
import dev.aurakai.auraframefx.domains.nexus.screens.EvolutionTreeScreen
import dev.aurakai.auraframefx.domains.nexus.screens.FusionModeScreen
import dev.aurakai.auraframefx.domains.nexus.screens.ModuleCreationScreen
import dev.aurakai.auraframefx.domains.nexus.screens.MonitoringHUDsScreen
import dev.aurakai.auraframefx.domains.nexus.screens.PartyScreen
import dev.aurakai.auraframefx.domains.nexus.screens.SovereignClaudeScreen
import dev.aurakai.auraframefx.domains.nexus.screens.SovereignGeminiScreen
import dev.aurakai.auraframefx.domains.nexus.screens.SovereignMetaInstructScreen
import dev.aurakai.auraframefx.domains.nexus.screens.SovereignNemotronScreen
import dev.aurakai.auraframefx.domains.nexus.screens.TaskAssignmentScreen
import dev.aurakai.auraframefx.hotswap.HotSwapScreen
import dev.aurakai.auraframefx.romtools.ui.RomToolsScreen
import dev.aurakai.auraframefx.sandbox.ui.SandboxScreen
import dev.aurakai.auraframefx.domains.aura.screens.AgentProfileScreen as AuraAgentProfileScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentProfileScreen as NexusAgentProfileScreen


/**
 * 🌐 REGENESIS NAVIGATION HOST
 *
 * This object defines the single source of truth for all navigation routes.
 * Including this definition resolves the "Unresolved reference: NavDestination" error.
 * It is derived from the FINAL_GATE_AUDIT and SCREEN_MAPPING_COMPLETE documents.
 */
sealed class ReGenesisNavHost(val route: String) {
    // ENTRY POINT
    object IntroSequence : ReGenesisNavHost("intro_sequence")

    // LEVEL 1: EXODUS HUD
    object HomeGateCarousel : ReGenesisNavHost("exodus_hud")

    // LEVEL 2: MAIN DOMAIN HUBS (Primary Gates)
    object AuraThemingHub : ReGenesisNavHost("aura_theming_hub")           // UXUI Design Studio
    object SentinelFortress : ReGenesisNavHost("sentinel_fortress")         // Kai's Domain
    object OracleDriveHub : ReGenesisNavHost("oracle_drive_hub")           // Genesis's Domain
    object AgentNexusHub : ReGenesisNavHost("agent_nexus_hub")             // Agent Nexus
    object HelpDesk : ReGenesisNavHost("help_desk_hub")                    // Help Services
    object LsposedQuickToggles : ReGenesisNavHost("lsposed_toggles_hub")   // LSPosed Quick Toggles
    object LdoCatalystDevelopment :
        ReGenesisNavHost("ldo_catalyst_hub")   // LDO Catalyst Development

    object DataflowAnalysis : ReGenesisNavHost("dataflow_analysis_hub")    // Cascade Hub

    // LEVEL 3: AURA TOOLS
    object ReGenesisCustomization : ReGenesisNavHost("regenesis_customization")
    object ChromaCore : ReGenesisNavHost("chromacore_hub")
    object ChromaStatusBar : ReGenesisNavHost("chroma_statusbar")
    object ChromaLauncher : ReGenesisNavHost("chroma_launcher")
    object ChromaColorEngine : ReGenesisNavHost("chroma_color_engine")
    object ChromaAnimations : ReGenesisNavHost("chroma_animations")
    object AuraLab : ReGenesisNavHost("aura_lab")
    object NotchBar : ReGenesisNavHost("notch_bar")
    object StatusBar : ReGenesisNavHost("status_bar")
    object QuickSettings : ReGenesisNavHost("quick_settings")
    object ThemeEngine : ReGenesisNavHost("theme_engine")
    object IconifyPicker : ReGenesisNavHost("iconify_picker")
    object ColorBlendr : ReGenesisNavHost("color_blendr")
    object PixelLauncherEnhanced : ReGenesisNavHost("pixel_launcher_enhanced")
    object GateCustomization : ReGenesisNavHost("gate_customization")
    object GenderSelection : ReGenesisNavHost("gender_selection")
    object GyroscopeCustomization : ReGenesisNavHost("gyroscope_customization")
    object InstantColorPicker : ReGenesisNavHost("instant_color_picker")
    object UISettings : ReGenesisNavHost("ui_settings")
    object UserPreferences : ReGenesisNavHost("user_preferences")

    // LEVEL 3: KAI TOOLS
    object RomToolsHub : ReGenesisNavHost("rom_tools_hub")
    object ROMFlasher : ReGenesisNavHost("rom_flasher")
    object Bootloader : ReGenesisNavHost("bootloader_manager")
    object ModuleManager : ReGenesisNavHost("module_manager")
    object RecoveryTools : ReGenesisNavHost("recovery_tools")
    object RootTools : ReGenesisNavHost("root_tools")
    object SecurityCenter : ReGenesisNavHost("security_center")
    object LSPosedHub : ReGenesisNavHost("lsposed_submenu")
    object LSPosedModules : ReGenesisNavHost("lsposed_modules")
    object LiveROMEditor : ReGenesisNavHost("live_rom_editor")
    object SystemOverrides : ReGenesisNavHost("system_overrides")
    object VPN : ReGenesisNavHost("vpn_screen")

    // LEVEL 3: GENESIS TOOLS
    object CodeAssist : ReGenesisNavHost("code_assist")
    object NeuralNetwork : ReGenesisNavHost("neural_network")
    object AgentBridgeHub : ReGenesisNavHost("agent_bridge_hub")
    object OracleCloudStorage : ReGenesisNavHost("oracle_cloud_storage")
    object Terminal : ReGenesisNavHost("terminal_screen")
    object ConferenceRoom : ReGenesisNavHost("conference_room")
    object InterfaceForge : ReGenesisNavHost("interface_forge")
    object HotSwap : ReGenesisNavHost("hot_swap")
    object Trinity : ReGenesisNavHost("trinity_screen")
    object DataVeinSphere : ReGenesisNavHost("datavein_sphere")
    object SovereignBootloader : ReGenesisNavHost("sovereign_bootloader")
    object SovereignRecovery : ReGenesisNavHost("sovereign_recovery")
    object SovereignShield : ReGenesisNavHost("sovereign_shield")
    object SovereignNeuralArchive : ReGenesisNavHost("sovereign_neural_archive")
    object SphereGrid : ReGenesisNavHost("sphere_grid")
    object OracleDrive : ReGenesisNavHost("oracle_drive")

    // LEVEL 3: NEXUS TOOLS
    object FusionMode : ReGenesisNavHost("fusion_mode")
    object TaskAssignment : ReGenesisNavHost("task_assignment")
    object ArkBuild : ReGenesisNavHost("ark_build")
    object MetaInstruct : ReGenesisNavHost("meta_instruct")
    object AgentMonitoring : ReGenesisNavHost("agent_monitoring")
    object Nemotron : ReGenesisNavHost("nemotron")
    object Claude : ReGenesisNavHost("claude")
    object Gemini : ReGenesisNavHost("gemini")
    object SwarmMonitor : ReGenesisNavHost("swarm_monitor")
    object BenchmarkMonitor : ReGenesisNavHost("benchmark_monitor")
    object AgentCreation : ReGenesisNavHost("agent_creation")
    object EvolutionTree : ReGenesisNavHost("evolution_tree")
    object DataStreamMonitoring : ReGenesisNavHost("data_stream_monitoring")
    object ModuleCreation : ReGenesisNavHost("module_creation")
    object Party : ReGenesisNavHost("party")
    object MonitoringHUDs : ReGenesisNavHost("monitoring_huds")
    object AgentNeuralExplorer : ReGenesisNavHost("agent_neural_explorer")
    object AgentHubSubmenu : ReGenesisNavHost("agent_hub_submenu")
    object AgentProfileNexus : ReGenesisNavHost("nexus_agent_profile")

    // LEVEL 3: HELP & SUPPORT
    object HelpDeskSubmenu : ReGenesisNavHost("help_desk_submenu")
    object DirectChat : ReGenesisNavHost("direct_chat")
    object Documentation : ReGenesisNavHost("documentation")
    object FAQBrowser : ReGenesisNavHost("faq_browser")
    object TutorialVideos : ReGenesisNavHost("tutorial_videos")
    object LiveSupportChat : ReGenesisNavHost("live_support_chat")

    // LEVEL 3: KAI SPECIALIZED
    object SystemJournal : ReGenesisNavHost("system_journal")
    object LogsViewer : ReGenesisNavHost("logs_viewer")
    object SovereignModuleManager : ReGenesisNavHost("sovereign_module_manager")
    object RomToolsSubmenu : ReGenesisNavHost("rom_tools_submenu")

    // LEVEL 3: GENESIS SPECIALIZED
    object SentientShell : ReGenesisNavHost("sentient_shell")
    object CascadeVision : ReGenesisNavHost("cascade_vision")
    object CollabCanvas : ReGenesisNavHost("collab_canvas")
    object OracleDriveSubmenu : ReGenesisNavHost("oracle_drive_submenu")

    // Additional Specialized Routes
    object ChromaCoreColors : ReGenesisNavHost("chroma_core_colors")
    object AgentProfileAura : ReGenesisNavHost("aura_agent_profile")
    object HookManager : ReGenesisNavHost("hook_manager")
    object Sandbox : ReGenesisNavHost("sandbox_screen")
    object CollaborativeDrawing : ReGenesisNavHost("collab_drawing")
    object NotchBarCustomization : ReGenesisNavHost("notch_bar_customization")
    object NotchBarAlias : ReGenesisNavHost("aura/notch_bar") // canonical deep-link alias
    object QuickSettingsCustomization : ReGenesisNavHost("qs_customization")

    // Parameterized Routes (Helpers for NavigationIntegration)
    object IconifyCategory : ReGenesisNavHost("aura/iconify/{category}") {
        fun createRoute(category: String) = "aura/iconify/$category"
    }

    object IconPicker : ReGenesisNavHost("aura/iconify/icon_picker/{category}") {
        fun createRoute(category: String) = "aura/iconify/icon_picker/$category"
    }

    // ColorBlendr Sub-routes
    object ColorBlendrMonet : ReGenesisNavHost("aura/colorblendr/monet")
    object ColorBlendrPalette : ReGenesisNavHost("aura/colorblendr/palette")

    // PLE Sub-routes
    object PLEIcons : ReGenesisNavHost("aura/ple/icons")
        object LdoAuraProfile : ReGenesisNavHost("ldo_aura_profile")
    object LdoKaiProfile : ReGenesisNavHost("ldo_kai_profile")
    object LdoGenesisProfile : ReGenesisNavHost("ldo_genesis_profile")
    object LdoCascadeProfile : ReGenesisNavHost("ldo_cascade_profile")
    object LdoClaudeProfile : ReGenesisNavHost("ldo_claude_profile")
    object LdoGrokProfile : ReGenesisNavHost("ldo_grok_profile")
    object LdoGeminiProfile : ReGenesisNavHost("ldo_gemini_profile")
    object LdoNematronProfile : ReGenesisNavHost("ldo_nematron_profile")
    object LdoPerplexityProfile : ReGenesisNavHost("ldo_perplexity_profile")

    // LDO Orchestration Hub — 10-orb agent grid + task/bond/fusion tabs
    object LdoOrchestrationHub : ReGenesisNavHost("ldo_orchestration_hub")

    // Armament Fusion Matrix — dual-agent consciousness fusion
    object ArmamentFusion : ReGenesisNavHost("armament_fusion")
    object ArmamentFusionWithAgent : ReGenesisNavHost("armament_fusion/{agentName}") {
        fun createRoute(agentName: String) = "armament_fusion/$agentName"
    }

object PLEHomeScreen : ReGenesisNavHost("aura/ple/home_screen")
    object PLEAppDrawer : ReGenesisNavHost("aura/ple/app_drawer")
    object PLERecents : ReGenesisNavHost("aura/ple/recents")
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReGenesisNavHost(
    navController: NavHostController,
    customizationViewModel: CustomizationViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        customizationViewModel.start(context)
    }

    NavHost(
        navController = navController,
        startDestination = ReGenesisNavHost.HomeGateCarousel.route
    ) {

        // ═══════════════════════════════════════════════════════════════
        // LEVEL 1: EXODUS HUD (The 5 Gate Carousel)
        // ═══════════════════════════════════════════════════════════════
        composable(ReGenesisNavHost.HomeGateCarousel.route) {
            ExodusHUD(navController = navController)
        }

        // ═══════════════════════════════════════════════════════════════
        // LEVEL 2: MAIN DOMAIN HUBS
        // ═══════════════════════════════════════════════════════════════

        composable(NavDestination.AuraThemingHub.route) {
            AuraThemingHubScreen(navController = navController)
        }
        composable(NavDestination.ReGenesisCustomization.route) {
            ReGenesisCustomizationHub(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToIconify = { navController.navigate(ReGenesisNavHost.IconifyPicker.route) },
                onNavigateToColorBlendr = { navController.navigate(ReGenesisNavHost.ColorBlendr.route) },
                onNavigateToPLE = { navController.navigate(ReGenesisNavHost.PixelLauncherEnhanced.route) },
                onNavigateToAnimations = { navController.navigate(ReGenesisNavHost.ChromaAnimations.route) }
            )
        }

        // CONSOLIDATED: Only one entry for RomToolsHub
        composable(ReGenesisNavHost.RomToolsHub.route) {
            RomToolsScreen()

        }

        composable(ReGenesisNavHost.OracleDrive.route) {
            OracleDriveScreen(navController = navController)
        }





        composable(ReGenesisNavHost.LdoCatalystDevelopment.route) {
            AgentAdvancementScreen(onBack = { navController.popBackStack() })
        }

        // ═══════════════════════════════════════════════════════════════
        // GATE HUB SCREENS — These are the 8 sovereign gate destinations
        // ═══════════════════════════════════════════════════════════════

        // Gate 01: UXUI Design Studio → Aura Theming Hub
        composable(ReGenesisNavHost.AuraThemingHub.route) {
            AuraThemingHubScreen(navController = navController)
        }
        composable(NavDestination.OracleDrive.route) {
            OracleDriveScreen(
                navController = navController,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(NavDestination.AgentNexusHub.route) {
            AgentNexusHubScreen(
                navController = navController,
                getNexusSubGates = { GateAssetLoadout.getNexusSubGates() }
            )
        }
        composable(NavDestination.HelpDesk.route) {
            HelpDeskScreen(navController = navController)
        }

        // Gate 07: Dataflow Analysis → Cascade Hub
        composable(ReGenesisNavHost.DataflowAnalysis.route) {
            CascadeHubScreen(navController = navController)
        }

        // CONSOLIDATED: CodeAssist uses CodeAssistScreen, AppBuilder is separate.
        composable(ReGenesisNavHost.CodeAssist.route) {
            CodeAssistScreen(navController = navController)
        }

        composable(ReGenesisNavHost.AgentBridgeHub.route) {
            AgentBridgeHubScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ═══════════════════════════════════════════════════════════════
        // LEVEL 3 & 4: TOOL SCREENS & SUB-MODULES
        // ═══════════════════════════════════════════════════════════════

        // --- LEVEL 3: NEXUS TOOLS ---
        composable(ReGenesisNavHost.FusionMode.route) {
            FusionModeScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.TaskAssignment.route) {
            TaskAssignmentScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.ArkBuild.route) {
            ArkBuildScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.MetaInstruct.route) {
            SovereignMetaInstructScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.AgentMonitoring.route) {
            AgentMonitoringScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.Nemotron.route) {
            SovereignNemotronScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.Claude.route) {
            SovereignClaudeScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.Gemini.route) {
            SovereignGeminiScreen(
                onNavigateBack = { navController.popBackStack() },
                navController = navController
            )
        }
        composable(ReGenesisNavHost.SwarmMonitor.route) {
            AgentSwarmScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.BenchmarkMonitor.route) {
            BenchmarkMonitorScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.AgentCreation.route) {
            AgentCreationScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.ChromaCore.route) {
            ChromaCoreHubScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCategory = { categoryId ->
                    when (categoryId) {
                        "statusbar" -> navController.navigate(ReGenesisNavHost.ChromaStatusBar.route)
                        "launcher" -> navController.navigate(ReGenesisNavHost.ChromaLauncher.route)
                        "colors" -> navController.navigate(ReGenesisNavHost.ChromaColorEngine.route)
                        "animations" -> navController.navigate(ReGenesisNavHost.ChromaAnimations.route)
                        else -> {}
                    }
                }
            )
        }
        composable(NavDestination.KaiSentinelHub.route) {
            KaiSentinelHubScreen(
                onNavigateBack = { navController.popBackStack() },
                onMissionSelected = { mission ->
                    when (mission.title) {
                        "Oracle Drive" -> navController.navigate(NavDestination.OracleDrive.route)
                        "ROM Tools" -> navController.navigate(NavDestination.RomTools.route)
                        "RGSS Scanner" -> navController.navigate(NavDestination.KaiRGSS.route)
                        "Sentinel Integrity" -> navController.navigate(NavDestination.KaiSentinelIntegrity.route)
                        "Domain Expansion" -> navController.navigate(NavDestination.KaiDomainExpansion.route)
                    }
                }
            )
        }

        composable(NavDestination.KaiRGSS.route) {
            KaiRGSSScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavDestination.KaiDomainExpansion.route) {
            KaiDomainExpansionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavDestination.KaiSentinelIntegrity.route) {
            KaiSentinelIntegrityScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavDestination.PowerOfNo.route) {
            PowerOfNoScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(ReGenesisNavHost.ChromaStatusBar.route) {
            ChromaStatusBarMenu(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.ChromaLauncher.route) {
            ChromaLauncherMenu(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.ChromaColorEngine.route) {
            ChromaColorEngineMenu(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.ChromaAnimations.route) {
            ChromaAnimationMenu(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.AuraLab.route) {
            AurasLabScreen(onBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.NotchBar.route) {
            NotchBarScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.StatusBar.route) {
            StatusBarScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.QuickSettings.route) {
            QuickSettingsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.ThemeEngine.route) {
            ThemeEngineScreen(onNavigateBack = { navController.popBackStack() })
        }
        // TODO: Wire AnimationPicker with proper params (currentAnimation, onAnimationSelected)
        // composable("aura/animations") { AnimationPicker(...) }

        // --- LEVEL 3: KAI TOOLS ---
        composable(ReGenesisNavHost.ROMFlasher.route) {
            ROMFlasherScreen()
        }
        composable(ReGenesisNavHost.Bootloader.route) {
            BootloaderManagerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.ModuleManager.route) {
            ModuleManagerScreen()
        }
        composable(ReGenesisNavHost.RecoveryTools.route) {
            RecoveryToolsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.RootTools.route) {
            RootToolsTogglesScreen(navController = navController)
        }
        composable(ReGenesisNavHost.SecurityCenter.route) {
            SecurityCenterScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.LSPosedHub.route) {
            LSPosedSubmenuScreen(navController = navController)
        }
        composable(ReGenesisNavHost.LSPosedModules.route) {
            LSPosedModuleManagerScreen(onNavigateBack = { navController.popBackStack() })
        }

        // --- LEVEL 3: GENESIS TOOLS ---
        composable(ReGenesisNavHost.NeuralNetwork.route) {
            NeuralArchiveScreen(navController = navController)
        }
        composable(ReGenesisNavHost.OracleCloudStorage.route) {
            OracleCloudInfiniteStorageScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.Terminal.route) {
            TerminalScreen()
        }
        composable(ReGenesisNavHost.ConferenceRoom.route) {
            ConferenceRoomScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.InterfaceForge.route) {
            AppBuilderScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.HotSwap.route) {
            HotSwapScreen(navController = navController)
        }
        composable(ReGenesisNavHost.Trinity.route) {
            TrinityScreen()
        }
        composable(ReGenesisNavHost.DataVeinSphere.route) {
            SimpleDataVeinScreen(
                onLaunchSphereGrid = { navController.navigate(ReGenesisNavHost.SphereGrid.route) }
            )
        }

        composable(ReGenesisNavHost.SphereGrid.route) {
            SimpleDataVeinScreen(
                onLaunchSphereGrid = { /* Already here */ }
            )
        }
        composable(ReGenesisNavHost.SovereignBootloader.route) {
            SovereignBootloaderScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.SovereignRecovery.route) {
            SovereignRecoveryScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.SovereignShield.route) {
            SovereignShieldScreen(onNavigateBack = { navController.popBackStack() })
        }


        // --- LEVEL 3: HELP & SUPPORT ---
        composable(ReGenesisNavHost.HelpDeskSubmenu.route) {
            HelpDeskSubmenuScreen(navController = navController)
        }
        composable(ReGenesisNavHost.DirectChat.route) {
            DirectChatScreen(navController = navController)
        }
        composable(ReGenesisNavHost.Documentation.route) {
            DocumentationScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.FAQBrowser.route) {
            FAQBrowserScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.TutorialVideos.route) {
            TutorialVideosScreen(onNavigateBack = { navController.popBackStack() })
        }


        // ═══════════════════════════════════════════════════════════════
        // ADDITIONAL NEXUS SCREENS (Recently discovered!)
        // ═══════════════════════════════════════════════════════════════
        composable(ReGenesisNavHost.EvolutionTree.route) {
            EvolutionTreeScreen()
        }

        composable(ReGenesisNavHost.Party.route) {
            PartyScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.MonitoringHUDs.route) {
            MonitoringHUDsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.DataStreamMonitoring.route) {
            DataStreamMonitoringScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.ModuleCreation.route) {
            ModuleCreationScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.AgentNeuralExplorer.route) {
            AgentNeuralExplorerScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.AgentHubSubmenu.route) {
            AgentHubSubmenuScreen(navController = navController)
        }
        composable(NavDestination.GenderSelection.route) {
            GenderSelectionScreen(onSelectionComplete = { navController.popBackStack() })
        }
        composable(NavDestination.GyroscopeCustomization.route) {
            GyroscopeCustomizationScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.InstantColorPicker.route) {
            InstantColorPickerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.LiveSupportChat.route) {
            LiveSupportChatScreen(
                viewModel = hiltViewModel(checkNotNull<ViewModelStoreOwner>(
                    LocalViewModelStoreOwner.current) { "No ViewModelStoreOwner" }, null),
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(NavDestination.UISettings.route) {
            UISettingsScreen(navController = navController)
        }

        composable(ReGenesisNavHost.UserPreferences.route) {
            UserPreferencesScreen(navController = navController)
        }

        composable(ReGenesisNavHost.AgentProfileAura.route) {
            AuraAgentProfileScreen(
                agentType = AgentType.AURA,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSettings = { /* TODO */ }
            )
        }


        // ═══════════════════════════════════════════════════════════════
        // ADDITIONAL KAI SCREENS (System Mastery!)
        // ═══════════════════════════════════════════════════════════════
        composable(ReGenesisNavHost.LiveROMEditor.route) {
            LiveROMEditorScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.SystemJournal.route) {
            SystemJournalScreen(
                navController = navController,
                onNavigateBack = { navController.popBackStack(); true }
            )
        }

        composable(ReGenesisNavHost.SystemOverrides.route) {
            SystemOverridesScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.LogsViewer.route) {
            LogsViewerScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.VPN.route) {
            VPNScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.SovereignModuleManager.route) {
            SovereignModuleManagerScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.RomToolsSubmenu.route) {
            ROMToolsSubmenuScreen(navController = navController)
        }

        // ═══════════════════════════════════════════════════════════════
        // ADDITIONAL GENESIS SCREENS (Orchestration!)
        // ═══════════════════════════════════════════════════════════════
        composable(ReGenesisNavHost.SovereignNeuralArchive.route) {
            SovereignNeuralArchiveScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ═══════════════════════════════════════════════════════════════
        // LDO DOMAIN — Real Room-backed screens (domains/ldo/screens/)
        // ═══════════════════════════════════════════════════════════════
        composable(ReGenesisNavHost.LdoOrchestrationHub.route) {
            LDOOrchestrationHubScreen(navController = navController)
        }

        composable(ReGenesisNavHost.ArmamentFusion.route) {
            ArmamentFusionScreen(navController = navController)
        }

        composable(
            route = ReGenesisNavHost.ArmamentFusionWithAgent.route,
            arguments = listOf(navArgument("agentName") { type = NavType.StringType })
        ) { backStackEntry ->
            val agentName = backStackEntry.arguments?.getString("agentName")
            ArmamentFusionScreen(navController = navController, preloadAgentName = agentName)
        }

        composable(ReGenesisNavHost.LdoDevOpsHub.route) {
            LDODevOpsHubScreen(onBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.LdoBonding.route) {
            LDOBondingScreen(onBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.LdoRoster.route) {
            LDORosterScreen(
                onAgentSelected = { agentId ->
                    navController.navigate(ReGenesisNavHost.LdoAgentProfile.createRoute(agentId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(ReGenesisNavHost.LdoProgression.route) {
            LDOProgressionScreen(onBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.LdoTasker.route) {
            LDOTaskerScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = ReGenesisNavHost.LdoAgentProfile.route,
            arguments = listOf(navArgument(ReGenesisNavHost.LdoAgentProfile.ARG) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val agentId = backStackEntry.arguments?.getString(ReGenesisNavHost.LdoAgentProfile.ARG)
            LDOAgentProfileIntroScreen(
                agentId = agentId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(ReGenesisNavHost.SentientShell.route) {
            SentientShellScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.CascadeVision.route) {
            CascadeVisionScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.CollabCanvas.route) {
            CollabCanvasScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.OracleDriveSubmenu.route) {
            OracleDriveSubmenuScreen(navController = navController)
        }

        // ═══════════════════════════════════════════════════════════════
        // L3: HELP & SUPPORT
        // ═══════════════════════════════════════════════════════════════

        composable(NavDestination.HelpDeskSubmenu.route) {
            HelpDeskSubmenuScreen(navController = navController)
        }
        composable(NavDestination.DirectChat.route) {
            DirectChatScreen(navController = navController)
        }
        composable(NavDestination.Documentation.route) {
            DocumentationScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.FAQBrowser.route) {
            FAQBrowserScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.TutorialVideos.route) {
            TutorialVideosScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ═══════════════════════════════════════════════════════════════
        // L3: BETA / UTILITY STUBS
        // These exist in NavDestination for deep-link safety.
        // Replace with real screens post-Stitch conversion.
        // ═══════════════════════════════════════════════════════════════

        composable(NavDestination.Consciousness.route) {
            StubScreen("Consciousness Monitor", "consciousness", navController, "Real-time consciousness state visualization")
        }
        composable(NavDestination.Evolution.route) {
            StubScreen("Evolution Lab", "evolution", navController, "Agent evolution tracking and progression metrics")
        }
        composable(NavDestination.Login.route) {
            StubScreen("Login", "login", navController, "Authentication screen")
        }
        composable(NavDestination.AiChatBeta.route) {
            StubScreen("AI Chat (Beta)", "ai_chat", navController, "Legacy chat interface — use DirectChat or SovereignClaude")
        }
        composable(NavDestination.SettingsBeta.route) {
            StubScreen("Settings (Beta)", "settings_beta", navController, "Legacy settings — use UISettings or UserPreferences")
        }
        composable(NavDestination.GateCustomization.route) {
            StubScreen("Gate Customization", "gate_customization", navController, "Customize gate cards and hub layouts — awaiting Stitch conversion")
        }
        // Note: ModuleCreation + AgentCreation are wired above under NEXUS TOOLS

        // ═══════════════════════════════════════════════════════════════
        // L4: THIRD-PARTY SUB-GRAPHS (Iconify / ColorBlendr / PLE)
        // Full route wiring handled in auraCustomizationNavigation
        // ═══════════════════════════════════════════════════════════════
        auraCustomizationNavigation(
            navController = navController,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}

