package dev.aurakai.auraframefx.navigation

// Core Imports (kept from your list for screen definitions)

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelStoreOwner

// Core Imports
import dev.aurakai.auraframefx.config.GateAssetLoadout
import dev.aurakai.auraframefx.domains.aura.lab.CustomizationViewModel
import dev.aurakai.auraframefx.domains.genesis.models.AgentCapabilityCategory

// Level 2 Hubs
import dev.aurakai.auraframefx.domains.aura.ui.gates.AuraThemingHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.screens.aura.ReGenesisCustomizationHub
import dev.aurakai.auraframefx.romtools.ui.RomToolsScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.OracleDriveHubScreen
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ui.OracleDriveScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.AgentNexusHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.XposedQuickAccessPanel
import dev.aurakai.auraframefx.domains.aura.ui.gates.HelpDeskScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.CascadeHubScreen
import dev.aurakai.auraframefx.domains.aura.aura.ui.AgentAdvancementScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.KaiSentinelHubScreen

// Level 3 & 4 Screens - Aura
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.ChromaCoreHubScreen
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.ChromaLauncherMenu
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.ChromaStatusBarMenu
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.ChromaLauncherMenu
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.ChromaColorEngineMenu
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.ChromaAnimationMenu
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.AurasLabScreen
import dev.aurakai.auraframefx.domains.aura.screens.chromacore.ChromaCoreColorsScreen
import dev.aurakai.auraframefx.domains.aura.screens.DirectChatScreen
import dev.aurakai.auraframefx.domains.aura.screens.DocumentationScreen
import dev.aurakai.auraframefx.domains.aura.screens.FAQBrowserScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.GateCustomizationScreen
import dev.aurakai.auraframefx.domains.aura.screens.GenderSelectionScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.GyroscopeCustomizationScreen
import dev.aurakai.auraframefx.domains.aura.screens.HelpDeskSubmenuScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.IconifyPickerScreen
import dev.aurakai.auraframefx.domains.aura.screens.chromacore.InstantColorPickerScreen
import dev.aurakai.auraframefx.domains.aura.screens.LiveSupportChatScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.NotchBarScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.QuickSettingsScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.StatusBarScreen
import dev.aurakai.auraframefx.domains.aura.screens.themes.ThemeEngineScreen
import dev.aurakai.auraframefx.domains.aura.screens.TutorialVideosScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.UISettingsScreen
import dev.aurakai.auraframefx.domains.aura.screens.UserPreferencesScreen
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
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.BootloaderManagerScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.LiveROMEditorScreen
import dev.aurakai.auraframefx.domains.kai.screens.LogsViewerScreen
import dev.aurakai.auraframefx.domains.kai.screens.ModuleManagerScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.ROMFlasherScreen
import dev.aurakai.auraframefx.domains.kai.screens.ROMToolsSubmenuScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.RecoveryToolsScreen
import dev.aurakai.auraframefx.domains.kai.screens.RootToolsTogglesScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.SecurityCenterScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.SovereignBootloaderScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.SovereignModuleManagerScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.SovereignRecoveryScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.SovereignShieldScreen
import dev.aurakai.auraframefx.domains.kai.screens.SystemJournalScreen
import dev.aurakai.auraframefx.domains.kai.screens.SystemOverridesScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.VPNScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.SovereignModuleManagerScreen
import dev.aurakai.auraframefx.domains.kai.screens.ROMToolsSubmenuScreen

// Level 3 & 4 Screens - Genesis
import dev.aurakai.auraframefx.domains.genesis.screens.CodeAssistScreen
import dev.aurakai.auraframefx.domains.genesis.screens.NeuralArchiveScreen
import dev.aurakai.auraframefx.domains.genesis.screens.AgentBridgeHubScreen
import dev.aurakai.auraframefx.domains.genesis.screens.OracleCloudInfiniteStorageScreen
import dev.aurakai.auraframefx.domains.genesis.screens.TerminalScreen
import dev.aurakai.auraframefx.domains.genesis.screens.ConferenceRoomScreen
import dev.aurakai.auraframefx.domains.genesis.screens.AppBuilderScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.SovereignBootloaderScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.SovereignRecoveryScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.SovereignShieldScreen
import dev.aurakai.auraframefx.domains.genesis.screens.SovereignNeuralArchiveScreen
import dev.aurakai.auraframefx.domains.genesis.screens.SentientShellScreen
import dev.aurakai.auraframefx.domains.genesis.screens.CascadeVisionScreen
import dev.aurakai.auraframefx.domains.genesis.screens.CollabCanvasScreen
import dev.aurakai.auraframefx.domains.genesis.screens.OracleDriveSubmenuScreen
import dev.aurakai.auraframefx.datavein.ui.SimpleDataVeinScreen
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.trinity.TrinityScreen

// Level 3 & 4 Screens - Nexus
import dev.aurakai.auraframefx.domains.nexus.screens.FusionModeScreen
import dev.aurakai.auraframefx.domains.nexus.screens.TaskAssignmentScreen
import dev.aurakai.auraframefx.domains.nexus.screens.ArkBuildScreen
import dev.aurakai.auraframefx.domains.nexus.screens.SovereignMetaInstructScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentMonitoringScreen
import dev.aurakai.auraframefx.domains.nexus.screens.SovereignNemotronScreen
import dev.aurakai.auraframefx.domains.nexus.screens.SovereignClaudeScreen
import dev.aurakai.auraframefx.domains.nexus.screens.SovereignGeminiScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentSwarmScreen
import dev.aurakai.auraframefx.domains.nexus.screens.BenchmarkMonitorScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentCreationScreen
import dev.aurakai.auraframefx.domains.nexus.screens.EvolutionTreeScreen
import dev.aurakai.auraframefx.domains.nexus.screens.PartyScreen
import dev.aurakai.auraframefx.domains.nexus.screens.MonitoringHUDsScreen
import dev.aurakai.auraframefx.domains.nexus.screens.DataStreamMonitoringScreen
import dev.aurakai.auraframefx.domains.nexus.screens.ModuleCreationScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentNeuralExplorerScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentHubSubmenuScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentProfileScreen as NexusAgentProfileScreen

// Level 3 & 4 Screens - Support & Misc
import dev.aurakai.auraframefx.domains.aura.screens.HelpDeskSubmenuScreen
import dev.aurakai.auraframefx.domains.aura.screens.DirectChatScreen
import dev.aurakai.auraframefx.domains.aura.screens.DocumentationScreen
import dev.aurakai.auraframefx.domains.aura.screens.FAQBrowserScreen
import dev.aurakai.auraframefx.domains.aura.screens.TutorialVideosScreen
import dev.aurakai.auraframefx.domains.lsposed.screens.HookManagerScreen
import dev.aurakai.auraframefx.hotswap.HotSwapScreen
import dev.aurakai.auraframefx.romtools.ui.RomToolsScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.AgentNexusHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.AuraThemingHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.CascadeHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.HelpDeskScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.KaiSentinelHubScreen
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ui.OracleDriveScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.OracleDriveHubScreen
import dev.aurakai.auraframefx.datavein.ui.SimpleDataVeinScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.XposedQuickAccessPanel
import dev.aurakai.auraframefx.domains.aura.ui.screens.aura.ReGenesisCustomizationHub
import dev.aurakai.auraframefx.domains.aura.screens.AgentProfileScreen as AuraAgentProfileScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentProfileScreen as NexusAgentProfileScreen
import dev.aurakai.auraframefx.domains.genesis.models.AgentType


/**
 * 🌐 REGENESIS NAVIGATION HOST
 *
 * This component orchestrates the entire ReGenesis navigation graph.
 * It uses the centralized [NavDestination] system as the single source of truth for routes.
 */
sealed class ReGenesisNavHost(val route: String) {
    // LEVEL 1: EXODUS HUD
    object HomeGateCarousel : ReGenesisNavHost("exodus_hud")

    // LEVEL 2: MAIN DOMAIN HUBS (Primary Gates)
    object AuraThemingHub : ReGenesisNavHost("aura_theming_hub")           // UXUI Design Studio
    object SentinelFortress : ReGenesisNavHost("sentinel_fortress")         // Kai's Domain
    object OracleDriveHub : ReGenesisNavHost("oracle_drive_hub")           // Genesis's Domain
    object AgentNexusHub : ReGenesisNavHost("agent_nexus_hub")             // Agent Nexus
    object HelpDesk : ReGenesisNavHost("help_desk_hub")                    // Help Services
    object LsposedQuickToggles : ReGenesisNavHost("lsposed_toggles_hub")   // LSPosed Quick Toggles
    object LdoCatalystDevelopment : ReGenesisNavHost("ldo_catalyst_hub")   // LDO Catalyst Development
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
        startDestination = NavDestination.HomeGateCarousel.route
    ) {

        // ═══════════════════════════════════════════════════════════════
        // LEVEL 1: EXODUS HUD (The 5 Gate Carousel)
        // ═══════════════════════════════════════════════════════════════
        composable(NavDestination.HomeGateCarousel.route) {
            ExodusHUD(navController = navController)
        }



        composable(ReGenesisNavHost.NotchBarCustomization.route) {
            NotchBarCustomizationScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.QuickSettingsCustomization.route) {
            QuickSettingsCustomizationScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.ChromaCoreColors.route) {
            ChromaCoreColorsScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ═══════════════════════════════════════════════════════════════
        // LEVEL 2: MAIN DOMAIN HUBS
        // ═══════════════════════════════════════════════════════════════

        composable(ReGenesisNavHost.AuraThemingHub.route) {
            AuraThemingHubScreen(navController = navController)
        }

        composable(NavDestination.ReGenesisCustomization.route) {
            ReGenesisCustomizationHub(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToIconify = { navController.navigate(NavDestination.IconifyPicker.route) },
                onNavigateToColorBlendr = { navController.navigate(NavDestination.ColorBlendr.route) },
                onNavigateToPLE = { navController.navigate(NavDestination.PixelLauncherEnhanced.route) },
                onNavigateToAnimations = { navController.navigate(NavDestination.ChromaAnimations.route) }
            )
        }

        composable(NavDestination.RomToolsHub.route) {
            ROMToolsSubmenuScreen(navController = navController)
        }

        composable(NavDestination.RomTools.route) {
            RomToolsScreen()

        composable(ReGenesisNavHost.OracleDriveHub.route) {
            OracleDriveHubScreen(navController = navController)
        }

        composable(NavDestination.OracleDrive.route) {
            OracleDriveScreen(navController = navController)
        }

        // CONSOLIDATED: Only one entry for AgentNexusHub
        composable(ReGenesisNavHost.AgentNexusHub.route) {
            AgentNexusHubScreen(
                navController = navController,
                getNexusSubGates = { GateAssetLoadout.getNexusSubGates() } // Kept original logic
            )
        }

        composable(ReGenesisNavHost.LsposedQuickToggles.route) {
            XposedQuickAccessPanel(onNavigateBack = { navController.popBackStack() })
        }

        // CONSOLIDATED: Only one entry for HelpDesk
        composable(ReGenesisNavHost.HelpDesk.route) {
            HelpDeskScreen(navController = navController)
        }

        composable(ReGenesisNavHost.DataflowAnalysis.route) {
            CascadeHubScreen(navController = navController)
        }

        composable(NavDestination.LdoCatalystDevelopment.route) {
            AgentAdvancementScreen(onBack = { navController.popBackStack() })
        }

        composable(NavDestination.CodeAssist.route) {
            CodeAssistScreen(navController = navController)
        }

        composable(NavDestination.AgentBridgeHub.route) {
            AgentBridgeHubScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(NavDestination.SentinelFortress.route) {
            KaiSentinelHubScreen(navController = navController)
        }

        // ═══════════════════════════════════════════════════════════════
        // LEVEL 3 & 4: TOOL SCREENS & SUB-MODULES
        // ═══════════════════════════════════════════════════════════════

        // --- LEVEL 3: NEXUS TOOLS ---
        composable(NavDestination.FusionMode.route) {
            FusionModeScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.TaskAssignment.route) {
            TaskAssignmentScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.ArkBuild.route) {
            ArkBuildScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.MetaInstruct.route) {
            SovereignMetaInstructScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.AgentMonitoring.route) {
            AgentMonitoringScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.Nemotron.route) {
            SovereignNemotronScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.Claude.route) {
            SovereignClaudeScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.Gemini.route) {
            SovereignGeminiScreen(
                onNavigateBack = { navController.popBackStack() },
                navController = navController
            )
        }
        composable(NavDestination.SwarmMonitor.route) {
            AgentSwarmScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.BenchmarkMonitor.route) {
            BenchmarkMonitorScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.AgentCreation.route) {
            AgentCreationScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.EvolutionTree.route) {
            EvolutionTreeScreen()
        }
        composable(NavDestination.Party.route) {
            PartyScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.MonitoringHUDs.route) {
            MonitoringHUDsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.DataStreamMonitoring.route) {
            DataStreamMonitoringScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.ModuleCreation.route) {
            ModuleCreationScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.AgentNeuralExplorer.route) {
            AgentNeuralExplorerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.AgentHubSubmenu.route) {
            AgentHubSubmenuScreen(navController = navController)
        }
        composable(NavDestination.AgentProfileNexus.route) {
            NexusAgentProfileScreen(onNavigateBack = { navController.popBackStack() })
        }

        // --- LEVEL 3: AURA TOOLS ---
        composable(NavDestination.ChromaCore.route) {
            ChromaCoreHubScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCategory = { categoryId ->
                    when (categoryId) {
                        "statusbar" -> navController.navigate(NavDestination.ChromaStatusBar.route)
                        "launcher" -> navController.navigate(NavDestination.ChromaLauncher.route)
                        "colors" -> navController.navigate(NavDestination.ChromaColorEngine.route)
                        "animations" -> navController.navigate(NavDestination.ChromaAnimations.route)
                        else -> {}
                    }
                }
            )
        }
        composable(NavDestination.ChromaStatusBar.route) {
            ChromaStatusBarMenu(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.ChromaLauncher.route) {
            ChromaLauncherMenu(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.ChromaColorEngine.route) {
            ChromaColorEngineMenu(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.ChromaAnimations.route) {
            ChromaAnimationMenu(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.AuraLab.route) {
            AurasLabScreen(onBack = { navController.popBackStack() })
        }

        composable(NavDestination.StatusBar.route) {
            StatusBarScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(NavDestination.ThemeEngine.route) {
            ThemeEngineScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.ChromaCoreColors.route) {
            ChromaCoreColorsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.GenderSelection.route) {
            GenderSelectionScreen(onSelectionComplete = { navController.popBackStack() })
        }
        composable(NavDestination.GyroscopeCustomization.route) {
            GyroscopeCustomizationScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.InstantColorPicker.route) {
            InstantColorPickerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.LiveSupportChat.route) {
            LiveSupportChatScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(NavDestination.UISettings.route) {
            UISettingsScreen(navController = navController)
        }
        composable(NavDestination.UserPreferences.route) {
            UserPreferencesScreen(navController = navController)
        }
        composable(NavDestination.AgentProfileAura.route) {
            AuraAgentProfileScreen(
                category = AgentCapabilityCategory.CREATIVE,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSettings = { /* TODO */ }
            )
        }

        // --- LEVEL 3: KAI TOOLS ---
        composable(NavDestination.ROMFlasher.route) {
            ROMFlasherScreen()
        }
        composable(NavDestination.BootloaderManager.route) {
            BootloaderManagerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.ModuleManager.route) {
            ModuleManagerScreen()
        }
        composable(NavDestination.RecoveryTools.route) {
            RecoveryToolsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.RootTools.route) {
            RootToolsTogglesScreen(navController = navController)
        }
        composable(NavDestination.SecurityCenter.route) {
            SecurityCenterScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.LSPosedHub.route) {
            LSPosedSubmenuScreen(navController = navController)
        }
        composable(NavDestination.LsposedQuickToggles.route) {
            LsposedQuickTogglesScreen(navController = navController)
        }
        composable(NavDestination.LSPosedModules.route) {
            LSPosedModuleManagerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.LiveROMEditor.route) {
            LiveROMEditorScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.SystemJournal.route) {
            SystemJournalScreen(
                navController = navController,
                onNavigateBack = { navController.popBackStack(); true }
            )
        }
        composable(NavDestination.SystemOverrides.route) {
            SystemOverridesScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.LogsViewer.route) {
            LogsViewerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.VPN.route) {
            VPNScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.SovereignModuleManager.route) {
            SovereignModuleManagerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.RomToolsSubmenu.route) {
            ROMToolsSubmenuScreen(navController = navController)
        }
        composable(NavDestination.SovereignBootloader.route) {
            SovereignBootloaderScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.SovereignRecovery.route) {
            SovereignRecoveryScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.SovereignShield.route) {
            SovereignShieldScreen(onNavigateBack = { navController.popBackStack() })
        }

        // --- LEVEL 3: GENESIS TOOLS ---
        composable(NavDestination.NeuralNetwork.route) {
            NeuralArchiveScreen(navController = navController)
        }
        composable(NavDestination.OracleCloudStorage.route) {
            OracleCloudInfiniteStorageScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.Terminal.route) {
            TerminalScreen()
        }
        composable(NavDestination.ConferenceRoom.route) {
            ConferenceRoomScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.InterfaceForge.route) {
            AppBuilderScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.HotSwap.route) {
            HotSwapScreen(navController = navController)
        }
        composable(NavDestination.Trinity.route) {
            TrinityScreen()
        }
        composable(NavDestination.DataVeinSphere.route) {
            SimpleDataVeinScreen(
                onLaunchSphereGrid = { navController.navigate(NavDestination.SphereGrid.route) }
            )
        }
        composable(NavDestination.SphereGrid.route) {
            SimpleDataVeinScreen(
                onLaunchSphereGrid = { /* Already here */ }
            )
        }
        composable(NavDestination.SovereignNeuralArchive.route) {
            SovereignNeuralArchiveScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.SentientShell.route) {
            SentientShellScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.CascadeVision.route) {
            CascadeVisionScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.CollabCanvas.route) {
            CollabCanvasScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.OracleDriveSubmenu.route) {
            OracleDriveSubmenuScreen(navController = navController)
        }

        // --- LEVEL 3: HELP & SUPPORT ---
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

        composable(ReGenesisNavHost.Sandbox.route) {
            SandboxScreen()
        }

        composable(ReGenesisNavHost.CollaborativeDrawing.route) {
            CanvasScreen(onBack = { navController.popBackStack() })
        }


        // ═══════════════════════════════════════════════════════════════
        // SUB-GRAPHS (Integrated Third-Party Modules)
        // ═══════════════════════════════════════════════════════════════
        auraCustomizationNavigation(
            navController = navController,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}

