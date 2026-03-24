package dev.aurakai.auraframefx.navigation

// Configuration

// Aura Domain

// Kai Domain

// Genesis Domain

// LSPosed Domain

// LDO Domain

// Nexus Domain

// Cascade Domain

// Misc
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dev.aurakai.auraframefx.domains.aura.lab.CustomizationViewModel
import dev.aurakai.auraframefx.aura.ui.HomeScreen
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ui.OracleDriveScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.ROMFlasherScreen
import dev.aurakai.auraframefx.domains.ldo.screens.ArmamentFusionScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDOAgentProfileIntroScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDOBondingScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDODevOpsHubScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDOOrchestrationHubScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDOProgressionScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDORosterScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDOTaskerScreen
import dev.aurakai.auraframefx.romtools.ui.RomToolsScreen

/**
 * 🌐 REGENESIS NAVIGATION ROUTES
 *
 * This object defines the single source of truth for all navigation routes.
 */
sealed class ReGenesisRoute(val route: String) {
    // ENTRY POINT
    object IntroSequence : ReGenesisRoute("intro_sequence")

    // LEVEL 1: EXODUS HUD
    object HomeGateCarousel : ReGenesisRoute("exodus_hud")

    // LEVEL 2: MAIN DOMAIN HUBS (Primary Gates)
    object AuraThemingHub : ReGenesisRoute("aura_theming_hub")           // UXUI Design Studio
    object SentinelFortress : ReGenesisRoute("sentinel_fortress")         // Kai's Domain
    object OracleDriveHub : ReGenesisRoute("oracle_drive_hub")           // Genesis's Domain
    object AgentNexusHub : ReGenesisRoute("agent_nexus_hub")             // Agent Nexus
    object HelpDesk : ReGenesisRoute("help_desk_hub")                    // Help Services
    object LsposedQuickToggles : ReGenesisRoute("lsposed_toggles_hub")   // LSPosed Quick Toggles
    object LdoCatalystDevelopment :
        ReGenesisRoute("ldo_catalyst_hub")   // LDO Catalyst Development

    object DataflowAnalysis : ReGenesisRoute("dataflow_analysis_hub")    // Cascade Hub

    // LEVEL 3: AURA TOOLS
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

    // LEVEL 3: KAI TOOLS
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

    // LEVEL 3: GENESIS TOOLS
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

    // LEVEL 3: NEXUS TOOLS
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

    // LEVEL 3: HELP & SUPPORT
    object HelpDeskSubmenu : ReGenesisRoute("help_desk_submenu")
    object DirectChat : ReGenesisRoute("direct_chat")
    object Documentation : ReGenesisRoute("documentation")
    object FAQBrowser : ReGenesisRoute("faq_browser")
    object TutorialVideos : ReGenesisRoute("tutorial_videos")
    object LiveSupportChat : ReGenesisRoute("live_support_chat")

    // LEVEL 3: KAI SPECIALIZED
    object SystemJournal : ReGenesisRoute("system_journal")
    object LogsViewer : ReGenesisRoute("logs_viewer")
    object SovereignModuleManager : ReGenesisRoute("sovereign_module_manager")
    object RomToolsSubmenu : ReGenesisRoute("rom_tools_submenu")

    // LEVEL 3: GENESIS SPECIALIZED
    object SentientShell : ReGenesisRoute("sentient_shell")
    object CascadeVision : ReGenesisRoute("cascade_vision")
    object CollabCanvas : ReGenesisRoute("collab_canvas")
    object OracleDriveSubmenu : ReGenesisRoute("oracle_drive_submenu")

    // Additional Specialized Routes
    object ChromaCoreColors : ReGenesisRoute("chroma_core_colors")
    object AgentProfileAura : ReGenesisRoute("aura_agent_profile")
    object HookManager : ReGenesisRoute("hook_manager")
    object Sandbox : ReGenesisRoute("sandbox_screen")
    object CollaborativeDrawing : ReGenesisRoute("collab_drawing")
    object NotchBarCustomization : ReGenesisRoute("notch_bar_customization")
    object NotchBarAlias : ReGenesisRoute("aura/notch_bar") // canonical deep-link alias
    object QuickSettingsCustomization : ReGenesisRoute("qs_customization")

    // Parameterized Routes
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

    // Armament Fusion Matrix
    object ArmamentFusion : ReGenesisRoute("armament_fusion")
    object ArmamentFusionWithAgent : ReGenesisRoute("armament_fusion/{agentName}") {
        fun createRoute(agentName: String) = "armament_fusion/$agentName"
    }

    // ColorBlendr Sub-routes
    object ColorBlendrMonet : ReGenesisRoute("aura/colorblendr/monet")
    object ColorBlendrPalette : ReGenesisRoute("aura/colorblendr/palette")

    // PLE Sub-routes
    object PLEIcons : ReGenesisRoute("aura/ple/icons")
    object PLEHomeScreen : ReGenesisRoute("aura/ple/home_screen")
    object PLEAppDrawer : ReGenesisRoute("aura/ple/app_drawer")
    object PLERecents : ReGenesisRoute("aura/ple/recents")

    // LDO Orchestration Hub
    object LdoOrchestrationHub : ReGenesisRoute("ldo_orchestration_hub")
    object LdoDevOpsHub : ReGenesisRoute("ldo_devops_hub")
    object LdoBonding : ReGenesisRoute("ldo_bonding")
    object LdoRoster : ReGenesisRoute("ldo_roster")
    object LdoProgression : ReGenesisRoute("ldo_progression")
    object LdoTasker : ReGenesisRoute("ldo_tasker")
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
        composable(ReGenesisRoute.HomeGateCarousel.route) {
            HomeScreen(
                onNavigateToModule = { moduleId ->
                    // Standard gate-to-route mapper
                    val route = when (moduleId) {
                        "aura_theming" -> ReGenesisRoute.AuraThemingHub.route
                        "sentinel_fortress" -> ReGenesisRoute.SentinelFortress.route
                        "oracle_drive" -> ReGenesisRoute.OracleDrive.route
                        "nexus" -> ReGenesisRoute.AgentNexusHub.route
                        "dataflow" -> ReGenesisRoute.DataflowAnalysis.route
                        "ldo" -> ReGenesisRoute.LdoOrchestrationHub.route
                        else -> ReGenesisRoute.HomeGateCarousel.route
                    }
                    if (route != ReGenesisRoute.HomeGateCarousel.route) {
                        navController.navigate(route)
                    }
                }
            )
        }

        composable(ReGenesisRoute.AuraThemingHub.route) {
            // Placeholder for AuraThemingHubScreen
        }

        composable(ReGenesisRoute.RomToolsHub.route) {
            RomToolsScreen()
        }

        composable(ReGenesisRoute.ROMFlasher.route) {
            ROMFlasherScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.OracleDrive.route) {
            OracleDriveScreen(navController = navController)
        }

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
            val agentName = backStackEntry.arguments?.getString("agentName")
            ArmamentFusionScreen(navController = navController, preloadAgentName = agentName)
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
            val agentId = backStackEntry.arguments?.getString(ReGenesisRoute.LdoAgentProfile.ARG)
            LDOAgentProfileIntroScreen(
                agentId = agentId,
                onBack = { navController.popBackStack() }
            )
        }

        // Add more composables here as needed...
    }
}
