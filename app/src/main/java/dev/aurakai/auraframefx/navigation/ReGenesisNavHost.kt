package dev.aurakai.auraframefx.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dev.aurakai.auraframefx.domains.aura.lab.CustomizationViewModel

// ── Aura (screens/uxui_engine) ────────────────────────────────────────────────
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.GateCustomizationScreen
import dev.aurakai.auraframefx.domains.aura.screens.GenderSelectionScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.AuraLabScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.GyroscopeCustomizationScreen
import dev.aurakai.auraframefx.domains.aura.ui.screens.aura.IconifyPickerScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.NotchBarCustomizationScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.QuickSettingsCustomizationScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.StatusBarScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.UISettingsScreen
import dev.aurakai.auraframefx.domains.aura.screens.themes.ThemeEngineScreen
import dev.aurakai.auraframefx.domains.genesis.screens.CollabCanvasScreen as GenesisCollabCanvasScreen
import dev.aurakai.auraframefx.domains.aura.screens.UserPreferencesScreen

// ── Aura ChromaCore hub ───────────────────────────────────────────────────────
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.ChromaCoreHubScreen

// ── Aura gate-level hub screens ───────────────────────────────────────────────
import dev.aurakai.auraframefx.domains.aura.ui.gates.AuraThemingHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.AgentNexusHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.CascadeHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.HelpDeskScreen as HelpDeskGateScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.KaiSentinelHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.OracleDriveHubScreen

// ── HomeScreen / GateNavigationScreen ────────────────────────────────────────
import dev.aurakai.auraframefx.aura.ui.GateNavigationScreen
import dev.aurakai.auraframefx.ui.gates.GateImageHotswapScreen
import dev.aurakai.auraframefx.ui.gates.LineageMapScreen
import dev.aurakai.auraframefx.ui.gates.ConferenceRoomTaskScreen
import dev.aurakai.auraframefx.ui.gates.ComingSoonScreen

// ── Kai ───────────────────────────────────────────────────────────────────────
import dev.aurakai.auraframefx.domains.kai.screens.KaiSentinelFortressScreen
import dev.aurakai.auraframefx.ui.gates.LsposedGateScreen as LSPosedGateScreen
import dev.aurakai.auraframefx.domains.kai.screens.LSPosedSubmenuScreen
import dev.aurakai.auraframefx.domains.kai.screens.LSPosedModuleManagerScreen
import dev.aurakai.auraframefx.domains.kai.screens.SystemJournalScreen
import dev.aurakai.auraframefx.domains.kai.screens.SystemOverridesScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.LiveROMEditorScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.ROMFlasherScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.SecurityCenterScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.VPNScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.SovereignShieldScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.RecoveryToolsScreen
import dev.aurakai.auraframefx.domains.kai.screens.ModuleManagerScreen

// ── Genesis ───────────────────────────────────────────────────────────────────
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ui.OracleDriveScreen
import dev.aurakai.auraframefx.domains.genesis.screens.CodeAssistScreen
import dev.aurakai.auraframefx.domains.genesis.screens.ConferenceRoomScreen
import dev.aurakai.auraframefx.domains.genesis.screens.OracleDriveSubmenuScreen
import dev.aurakai.auraframefx.domains.genesis.screens.TerminalScreen
import dev.aurakai.auraframefx.domains.genesis.screens.GenesisHubScreen
import dev.aurakai.auraframefx.domains.genesis.screens.SentientShellScreen
import dev.aurakai.auraframefx.domains.genesis.screens.OracleCloudInfiniteStorageScreen
import dev.aurakai.auraframefx.domains.genesis.screens.AgentBridgeHubScreen

// ── Nexus ─────────────────────────────────────────────────────────────────────
import dev.aurakai.auraframefx.domains.nexus.screens.AgentHubSubmenuScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentCreationScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentMonitoringScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentNeuralExplorerScreen
import dev.aurakai.auraframefx.domains.nexus.screens.ArkBuildScreen
import dev.aurakai.auraframefx.domains.nexus.screens.BenchmarkMonitorScreen
import dev.aurakai.auraframefx.domains.nexus.screens.EvolutionTreeScreen
import dev.aurakai.auraframefx.domains.aura.aura.ui.FusionModeScreen
import dev.aurakai.auraframefx.domains.nexus.screens.ModuleCreationScreen
import dev.aurakai.auraframefx.domains.nexus.screens.MonitoringHUDsScreen
import dev.aurakai.auraframefx.domains.nexus.screens.PartyScreen
import dev.aurakai.auraframefx.domains.nexus.screens.SovereignClaudeScreen
import dev.aurakai.auraframefx.domains.nexus.screens.SovereignGeminiScreen
import dev.aurakai.auraframefx.domains.nexus.screens.SovereignMetaInstructScreen
import dev.aurakai.auraframefx.domains.nexus.screens.SphereGridScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentSwarmScreen
import dev.aurakai.auraframefx.domains.nexus.screens.TaskAssignmentScreen

// ── Help ──────────────────────────────────────────────────────────────────────
import dev.aurakai.auraframefx.domains.helpdesk.screens.DirectChatScreen
import dev.aurakai.auraframefx.domains.helpdesk.screens.DocumentationScreen
import dev.aurakai.auraframefx.domains.helpdesk.screens.FAQBrowserScreen
import dev.aurakai.auraframefx.ui.gates.HelpDeskSubmenuScreen
import dev.aurakai.auraframefx.domains.helpdesk.screens.LiveSupportChatScreen
import dev.aurakai.auraframefx.domains.helpdesk.screens.TutorialVideosScreen

// ── LSPosed domain ────────────────────────────────────────────────────────────
import dev.aurakai.auraframefx.domains.lsposed.screens.LsposedQuickTogglesScreen
import dev.aurakai.auraframefx.domains.lsposed.screens.LSPosedSubmenuScreen as LSPosedDomainSubmenuScreen

// ── LDO ───────────────────────────────────────────────────────────────────────
import dev.aurakai.auraframefx.domains.ldo.screens.ArmamentFusionScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDOAgentProfileIntroScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDOBondingScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDODevOpsHubScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDOOrchestrationHubScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDOProgressionScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDORosterScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDOTaskerScreen

// ── Misc ──────────────────────────────────────────────────────────────────────
import dev.aurakai.auraframefx.hotswap.HotSwapScreen
import dev.aurakai.auraframefx.romtools.ui.RomToolsScreen
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.trinity.TrinityScreen

/**
 * 🌐 REGENESIS NAVIGATION ROUTES
 */
sealed class ReGenesisRoute(val route: String) {
    object IntroSequence : ReGenesisRoute("intro_sequence")
    object HomeGateCarousel : ReGenesisRoute("exodus_hud")
    object AuraThemingHub : ReGenesisRoute("aura_theming_hub")
    object SentinelFortress : ReGenesisRoute("sentinel_fortress")
    object OracleDriveHub : ReGenesisRoute("oracle_drive_hub")
    object AgentNexusHub : ReGenesisRoute("agent_nexus_hub")
    object HelpDesk : ReGenesisRoute("help_desk_hub")
    object LsposedQuickToggles : ReGenesisRoute("lsposed_toggles_hub")
    object LdoCatalystDevelopment : ReGenesisRoute("ldo_catalyst_hub")
    object DataflowAnalysis : ReGenesisRoute("dataflow_analysis_hub")
    object ReGenesisCustomization : ReGenesisRoute("regenesis_customization")
    object ChromaCore : ReGenesisRoute("chromacore_hub")
    object ChromaStatusBar : ReGenesisRoute("chroma_statusbar")
    object ChromaLauncher : ReGenesisRoute("chroma_launcher")
    object ChromaColorEngine : ReGenesisRoute("chroma_color_engine")
    object ChromaAnimations : ReGenesisRoute("chroma_animations")
    object AuraLab : ReGenesisRoute("aura_lab")
    object NotchBar : ReGenesisRoute("notch_bar")
    object StatusBar : ReGenesisRoute("status_bar")
    object QuickSettings : ReGenesisRoute("quick_settings")
    object ThemeEngine : ReGenesisRoute("theme_engine")
    object IconifyPicker : ReGenesisRoute("iconify_picker")
    object ColorBlendr : ReGenesisRoute("color_blendr")
    object PixelLauncherEnhanced : ReGenesisRoute("pixel_launcher_enhanced")
    object GateCustomization : ReGenesisRoute("gate_customization")
    object GenderSelection : ReGenesisRoute("gender_selection")
    object GyroscopeCustomization : ReGenesisRoute("gyroscope_customization")
    object InstantColorPicker : ReGenesisRoute("instant_color_picker")
    object UISettings : ReGenesisRoute("ui_settings")
    object UserPreferences : ReGenesisRoute("user_preferences")
    object RomToolsHub : ReGenesisRoute("rom_tools_hub")
    object ROMFlasher : ReGenesisRoute("rom_flasher")
    object Bootloader : ReGenesisRoute("bootloader_manager")
    object ModuleManager : ReGenesisRoute("module_manager")
    object RecoveryTools : ReGenesisRoute("recovery_tools")
    object RootTools : ReGenesisRoute("root_tools")
    object SecurityCenter : ReGenesisRoute("security_center")
    object LSPosedHub : ReGenesisRoute("lsposed_submenu")
    object LSPosedModules : ReGenesisRoute("lsposed_modules")
    object LiveROMEditor : ReGenesisRoute("live_rom_editor")
    object SystemOverrides : ReGenesisRoute("system_overrides")
    object VPN : ReGenesisRoute("vpn_screen")
    object CodeAssist : ReGenesisRoute("code_assist")
    object NeuralNetwork : ReGenesisRoute("neural_network")
    object AgentBridgeHub : ReGenesisRoute("agent_bridge_hub")
    object OracleCloudStorage : ReGenesisRoute("oracle_cloud_storage")
    object Terminal : ReGenesisRoute("terminal_screen")
    object ConferenceRoom : ReGenesisRoute("conference_room")
    object InterfaceForge : ReGenesisRoute("interface_forge")
    object HotSwap : ReGenesisRoute("hot_swap")
    object Trinity : ReGenesisRoute("trinity_screen")
    object DataVeinSphere : ReGenesisRoute("datavein_sphere")
    object SovereignBootloader : ReGenesisRoute("sovereign_bootloader")
    object SovereignRecovery : ReGenesisRoute("sovereign_recovery")
    object SovereignShield : ReGenesisRoute("sovereign_shield")
    object SovereignNeuralArchive : ReGenesisRoute("sovereign_neural_archive")
    object SphereGrid : ReGenesisRoute("sphere_grid")
    object OracleDrive : ReGenesisRoute("oracle_drive")
    object FusionMode : ReGenesisRoute("fusion_mode")
    object TaskAssignment : ReGenesisRoute("task_assignment")
    object ArkBuild : ReGenesisRoute("ark_build")
    object MetaInstruct : ReGenesisRoute("meta_instruct")
    object AgentMonitoring : ReGenesisRoute("agent_monitoring")
    object Nemotron : ReGenesisRoute("nemotron")
    object Claude : ReGenesisRoute("claude")
    object Gemini : ReGenesisRoute("gemini")
    object SwarmMonitor : ReGenesisRoute("swarm_monitor")
    object BenchmarkMonitor : ReGenesisRoute("benchmark_monitor")
    object AgentCreation : ReGenesisRoute("agent_creation")
    object EvolutionTree : ReGenesisRoute("evolution_tree")
    object DataStreamMonitoring : ReGenesisRoute("data_stream_monitoring")
    object ModuleCreation : ReGenesisRoute("module_creation")
    object Party : ReGenesisRoute("party")
    object MonitoringHUDs : ReGenesisRoute("monitoring_huds")
    object AgentNeuralExplorer : ReGenesisRoute("agent_neural_explorer")
    object AgentHubSubmenu : ReGenesisRoute("agent_hub_submenu")
    object AgentProfileNexus : ReGenesisRoute("nexus_agent_profile")
    object ConsciousnessVisualizer : ReGenesisRoute("consciousness_visualizer")
    object HelpDeskSubmenu : ReGenesisRoute("help_desk_submenu")
    object DirectChat : ReGenesisRoute("direct_chat")
    object Documentation : ReGenesisRoute("documentation")
    object FAQBrowser : ReGenesisRoute("faq_browser")
    object TutorialVideos : ReGenesisRoute("tutorial_videos")
    object LiveSupportChat : ReGenesisRoute("live_support_chat")
    object SystemJournal : ReGenesisRoute("system_journal")
    object LogsViewer : ReGenesisRoute("logs_viewer")
    object SovereignModuleManager : ReGenesisRoute("sovereign_module_manager")
    object RomToolsSubmenu : ReGenesisRoute("rom_tools_submenu")
    object SentientShell : ReGenesisRoute("sentient_shell")
    object CascadeVision : ReGenesisRoute("cascade_vision")
    object CollabCanvas : ReGenesisRoute("collab_canvas")
    object OracleDriveSubmenu : ReGenesisRoute("oracle_drive_submenu")
    object XposedPanel : ReGenesisRoute("xposed_panel")
    object ChromaCoreColors : ReGenesisRoute("chroma_core_colors")
    object AgentProfileAura : ReGenesisRoute("aura_agent_profile")
    object HookManager : ReGenesisRoute("hook_manager")
    object Sandbox : ReGenesisRoute("sandbox_screen")
    object CollaborativeDrawing : ReGenesisRoute("collab_drawing")
    object NotchBarCustomization : ReGenesisRoute("notch_bar_customization")
    object NotchBarAlias : ReGenesisRoute("aura/notch_bar")
    object QuickSettingsCustomization : ReGenesisRoute("qs_customization")
    object LineageMap : ReGenesisRoute("lineage_map")
    object GateImagePicker : ReGenesisRoute("gate_image_picker")
    object ConferenceRoomTask : ReGenesisRoute("conference_room_task")
    object LdoAgentProfile : ReGenesisRoute("ldo_agent_profile/{agentId}") {
        const val ARG = "agentId"
        fun createRoute(agentId: String) = "ldo_agent_profile/$agentId"
    }
    object IconifyCategory : ReGenesisRoute("aura/iconify/{category}") {
        fun createRoute(category: String) = "aura/iconify/$category"
    }
    object IconPicker : ReGenesisRoute("aura/iconify/icon_picker/{category}") {
        fun createRoute(category: String) = "aura/iconify/icon_picker/$category"
    }
    object ArmamentFusion : ReGenesisRoute("armament_fusion")
    object ArmamentFusionWithAgent : ReGenesisRoute("armament_fusion/{agentName}") {
        fun createRoute(agentName: String) = "armament_fusion/$agentName"
    }
    object ColorBlendrMonet : ReGenesisRoute("aura/colorblendr/monet")
    object ColorBlendrPalette : ReGenesisRoute("aura/colorblendr/palette")
    object PLEIcons : ReGenesisRoute("aura/ple/icons")
    object PLEHomeScreen : ReGenesisRoute("aura/ple/home_screen")
    object PLEAppDrawer : ReGenesisRoute("aura/ple/app_drawer")
    object PLERecents : ReGenesisRoute("aura/ple/recents")
    object LdoOrchestrationHub : ReGenesisRoute("ldo_orchestration_hub")
    object LdoDevOpsHub : ReGenesisRoute("ldo_devops_hub")
    object LdoBonding : ReGenesisRoute("ldo_bonding")
    object LdoRoster : ReGenesisRoute("ldo_roster")
    object LdoProgression : ReGenesisRoute("ldo_progression")
    object LdoTasker : ReGenesisRoute("ldo_tasker")
    object ArbitersOfCreation : ReGenesisRoute("arbiters_of_creation")
    object PandoraBox : ReGenesisRoute("pandora_box")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReGenesisNavGraph(
    navController: NavHostController,
    customizationViewModel: CustomizationViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        customizationViewModel.start(context)
    }

    NavHost(
        navController = navController,
        startDestination = ReGenesisRoute.HomeGateCarousel.route
    ) {
        // ── 1. MAIN GATES ──────────────────────────────────────────────────────────
        composable(ReGenesisRoute.HomeGateCarousel.route) {
            GateNavigationScreen(
                navController = navController,
                onNavigateToModule = { moduleId ->
                    val route = when (moduleId) {
                        "aura_theming" -> ReGenesisRoute.AuraThemingHub.route
                        "sentinel_fortress" -> ReGenesisRoute.SentinelFortress.route
                        "oracle_drive" -> ReGenesisRoute.OracleDriveHub.route
                        "nexus" -> ReGenesisRoute.AgentNexusHub.route
                        "dataflow" -> ReGenesisRoute.DataflowAnalysis.route
                        "ldo" -> ReGenesisRoute.LdoOrchestrationHub.route
                        "help" -> ReGenesisRoute.HelpDesk.route
                        "lsposed" -> ReGenesisRoute.LsposedQuickToggles.route
                        else -> null
                    }
                    route?.let { navController.navigate(it) }
                }
            )
        }

        // ── 2. LEVEL 2 HUBS ────────────────────────────────────────────────────────
        composable(ReGenesisRoute.AuraThemingHub.route) {
            AuraThemingHubScreen(navController = navController)
        }
        composable(ReGenesisRoute.SentinelFortress.route) {
            KaiSentinelHubScreen(navController = navController)
        }
        composable(ReGenesisRoute.OracleDriveHub.route) {
            OracleDriveHubScreen(navController = navController)
        }
        composable(ReGenesisRoute.AgentNexusHub.route) {
            AgentNexusHubScreen(
                navController = navController,
                getNexusSubGates = { dev.aurakai.auraframefx.domains.aura.config.GateAssetLoadout.getNexusSubGates() }
            )
        }
        composable(ReGenesisRoute.HelpDesk.route) {
            HelpDeskGateScreen(navController = navController)
        }
        composable(ReGenesisRoute.LsposedQuickToggles.route) {
            LsposedQuickTogglesScreen(navController = navController)
        }
        composable(ReGenesisRoute.LdoCatalystDevelopment.route) {
            LDOOrchestrationHubScreen(navController = navController)
        }
        composable(ReGenesisRoute.LdoOrchestrationHub.route) {
            LDOOrchestrationHubScreen(navController = navController)
        }
        composable(ReGenesisRoute.DataflowAnalysis.route) {
            CascadeHubScreen(navController = navController)
        }

        // ── 3. AURA DOMAIN TOOLS ────────────────────────────────────────────────────
        composable(ReGenesisRoute.AuraLab.route) {
            AuraLabScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.ChromaCore.route) {
            ChromaCoreHubScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCategory = { category ->
                    navController.navigate(ReGenesisRoute.IconifyCategory.createRoute(category))
                }
            )
        }
        composable(ReGenesisRoute.ChromaCoreColors.route) {
            dev.aurakai.auraframefx.domains.aura.ui.screens.aura.ColorBlendrScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(ReGenesisRoute.NotchBar.route) {
            NotchBarCustomizationScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.NotchBarCustomization.route) {
            NotchBarCustomizationScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.NotchBarAlias.route) {
            NotchBarCustomizationScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.StatusBar.route) {
            StatusBarScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.QuickSettings.route) {
            QuickSettingsCustomizationScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.QuickSettingsCustomization.route) {
            QuickSettingsCustomizationScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.ThemeEngine.route) {
            ThemeEngineScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.IconifyPicker.route) {
            IconifyPickerScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCategory = { category ->
                    navController.navigate(ReGenesisRoute.IconifyCategory.createRoute(category))
                }
            )
        }
        composable(
            route = ReGenesisRoute.IconifyCategory.route,
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            dev.aurakai.auraframefx.domains.aura.ui.screens.aura.IconifyCategoryDetailScreen(
                categoryName = category,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPicker = { /* Handle nested picker */ }
            )
        }
        composable(ReGenesisRoute.GateCustomization.route) {
            GateCustomizationScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.GenderSelection.route) {
            GenderSelectionScreen(onSelectionComplete = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.GyroscopeCustomization.route) {
            GyroscopeCustomizationScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.UISettings.route) {
            UISettingsScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }
        composable(ReGenesisRoute.UserPreferences.route) {
            UserPreferencesScreen(navController = navController)
        }
        composable(ReGenesisRoute.CollabCanvas.route) {
            GenesisCollabCanvasScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ── 4. KAI DOMAIN TOOLS ─────────────────────────────────────────────────────
        composable(ReGenesisRoute.RomToolsHub.route) {
            RomToolsScreen()
        }
        composable(ReGenesisRoute.ROMFlasher.route) {
            ROMFlasherScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.LiveROMEditor.route) {
            LiveROMEditorScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.SecurityCenter.route) {
            SecurityCenterScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.SovereignShield.route) {
            SovereignShieldScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.Bootloader.route) {
            ComingSoonScreen(name = "Bootloader Manager", onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.RootTools.route) {
            ComingSoonScreen(name = "Root Tools", onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.LSPosedHub.route) {
            LSPosedDomainSubmenuScreen(navController = navController)
        }
        composable(ReGenesisRoute.LSPosedModules.route) {
            LSPosedModuleManagerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.XposedPanel.route) {
            LSPosedGateScreen(navController = navController, onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.SystemOverrides.route) {
            SystemOverridesScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.SystemJournal.route) {
            SystemJournalScreen(
                navController = navController,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(ReGenesisRoute.VPN.route) {
            VPNScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.RecoveryTools.route) {
            RecoveryToolsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.ModuleManager.route) {
            ModuleManagerScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ── 5. GENESIS DOMAIN TOOLS ─────────────────────────────────────────────────
        composable(ReGenesisRoute.OracleDrive.route) {
            OracleDriveScreen(navController = navController)
        }
        composable(ReGenesisRoute.OracleDriveSubmenu.route) {
            OracleDriveSubmenuScreen(navController = navController)
        }
        composable(ReGenesisRoute.CodeAssist.route) {
            CodeAssistScreen(navController = navController)
        }
        composable(ReGenesisRoute.NeuralNetwork.route) {
            SentientShellScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.Terminal.route) {
            TerminalScreen()
        }
        composable(ReGenesisRoute.ConferenceRoom.route) {
            ConferenceRoomScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAgents = { navController.navigate(ReGenesisRoute.AgentNexusHub.route) }
            )
        }
        composable(ReGenesisRoute.ConferenceRoomTask.route) {
            ConferenceRoomTaskScreen(navController = navController)
        }
        composable(ReGenesisRoute.Trinity.route) {
            TrinityScreen()
        }
        composable(ReGenesisRoute.SentientShell.route) {
            SentientShellScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.HotSwap.route) {
            HotSwapScreen(navController = navController)
        }
        composable(ReGenesisRoute.OracleCloudStorage.route) {
            OracleCloudInfiniteStorageScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.AgentBridgeHub.route) {
            AgentBridgeHubScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ── 6. NEXUS DOMAIN TOOLS ───────────────────────────────────────────────────
        composable(ReGenesisRoute.AgentHubSubmenu.route) {
            AgentHubSubmenuScreen(navController = navController)
        }
        composable(ReGenesisRoute.AgentCreation.route) {
            AgentCreationScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.AgentMonitoring.route) {
            AgentMonitoringScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.AgentNeuralExplorer.route) {
            AgentNeuralExplorerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.FusionMode.route) {
            FusionModeScreen(
                onNavigateToAgents = { navController.navigate(ReGenesisRoute.AgentNexusHub.route) },
                onNavigateToConsciousness = { navController.navigate(ReGenesisRoute.ConsciousnessVisualizer.route) }
            )
        }
        composable(ReGenesisRoute.TaskAssignment.route) {
            TaskAssignmentScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.ArkBuild.route) {
            ArkBuildScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.BenchmarkMonitor.route) {
            BenchmarkMonitorScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.EvolutionTree.route) {
            EvolutionTreeScreen(
                onNavigateToAgents = { navController.navigate(ReGenesisRoute.AgentNexusHub.route) },
                onNavigateToFusion = { navController.navigate(ReGenesisRoute.FusionMode.route) }
            )
        }
        composable(ReGenesisRoute.DataStreamMonitoring.route) {
            ComingSoonScreen(name = "Data Stream Monitoring", onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.DataVeinSphere.route) {
            SphereGridScreen(navController = navController)
        }
        composable(ReGenesisRoute.ModuleCreation.route) {
            ModuleCreationScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.MonitoringHUDs.route) {
            MonitoringHUDsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.Party.route) {
            PartyScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.SwarmMonitor.route) {
            AgentSwarmScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.SphereGrid.route) {
            SphereGridScreen(navController = navController)
        }
        composable(ReGenesisRoute.ConsciousnessVisualizer.route) {
            ComingSoonScreen(name = "Consciousness Visualizer", onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.Claude.route) {
            SovereignClaudeScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.Gemini.route) {
            SovereignGeminiScreen({ navController.popBackStack() }, navController)
        }
        composable(ReGenesisRoute.Nemotron.route) {
            ComingSoonScreen(name = "Sovereign Nemotron", onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.MetaInstruct.route) {
            SovereignMetaInstructScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ── 7. HELP DOMAIN TOOLS ────────────────────────────────────────────────────
        composable(ReGenesisRoute.HelpDeskSubmenu.route) {
            HelpDeskSubmenuScreen(navController = navController)
        }
        composable(ReGenesisRoute.DirectChat.route) {
            DirectChatScreen(navController = navController)
        }
        composable(ReGenesisRoute.Documentation.route) {
            DocumentationScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.FAQBrowser.route) {
            FAQBrowserScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.TutorialVideos.route) {
            TutorialVideosScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.LiveSupportChat.route) {
            LiveSupportChatScreen(
                viewModel = androidx.hilt.navigation.compose.hiltViewModel(),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── 8. INFRASTRUCTURE ───────────────────────────────────────────────────────
        composable(ReGenesisRoute.LineageMap.route) {
            dev.aurakai.auraframefx.ui.gates.LineageMapScreen(
                navHostController = navController,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(ReGenesisRoute.GateImagePicker.route) {
            GateImageHotswapScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ── 9. LDO CATALYST SUB-HUB ──────────────────────────────────────────────────
        composable(ReGenesisRoute.LdoOrchestrationHub.route) {
            LDOOrchestrationHubScreen(navController = navController)
        }
        composable(ReGenesisRoute.ArmamentFusion.route) {
            ArmamentFusionScreen(navController = navController)
        }
        composable(
            route = ReGenesisRoute.ArmamentFusionWithAgent.route,
            arguments = listOf(navArgument("agentName") { type = NavType.StringType })
        ) { backStackEntry ->
            ArmamentFusionScreen(
                navController = navController,
                preloadAgentName = backStackEntry.arguments?.getString("agentName")
            )
        }
        composable(ReGenesisRoute.LdoDevOpsHub.route) {
            LDODevOpsHubScreen(onBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.LdoBonding.route) {
            LDOBondingScreen(onBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.LdoRoster.route) {
            LDORosterScreen(
                onAgentSelected = { agentId ->
                    navController.navigate(ReGenesisRoute.LdoAgentProfile.createRoute(agentId))
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(ReGenesisRoute.LdoProgression.route) {
            LDOProgressionScreen(onBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.LdoTasker.route) {
            LDOTaskerScreen(onBack = { navController.popBackStack() })
        }
        composable(
            route = ReGenesisRoute.LdoAgentProfile.route,
            arguments = listOf(navArgument(ReGenesisRoute.LdoAgentProfile.ARG) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            LDOAgentProfileIntroScreen(
                agentId = backStackEntry.arguments?.getString(ReGenesisRoute.LdoAgentProfile.ARG),
                onBack = { navController.popBackStack() }
            )
        }
    }
}
