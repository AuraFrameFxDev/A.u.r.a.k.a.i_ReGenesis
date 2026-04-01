package dev.aurakai.auraframefx.navigation

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
import androidx.navigation.navDeepLink
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.engine.ThemeViewModel
import dev.aurakai.auraframefx.domains.aura.ui.customization.CustomizationViewModel
import dev.aurakai.auraframefx.domains.aura.ui.screens.aura.ReGenesisCustomizationHub
import dev.aurakai.auraframefx.domains.aura.ui.customization.ZOrderEditor

// ── Aura (UX/UI Design Studio Domain) ────────────────────────────────────────
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.StatusBarScreen
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.UISettingsScreen
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.NotchBarCustomizationScreen
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.quicksettings.QuickSettingsCustomizationScreen
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.ChromaCoreColorsScreen
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.ArbitersOfCreationScreen
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.ThemeEngineScreen
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.auraslab_sandbox.SandboxScreen
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.collabcanvas.AuraTeachingCanvasScreen
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.collabcanvas.CollabCanvasScreen
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.gate_artwork_editor.GateCustomizationScreen
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.gate_artwork_editor.GateDomainImagePicker
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.gate_artwork_editor.LSPosedGateScreen
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.gyroscope_editor.GyroscopeCustomizationScreen
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.GenderSelectionScreen
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.UserPreferencesScreen

// ── Hubs ─────────────────────────────────────────────────────────────────────
import dev.aurakai.auraframefx.domains.aura.ui.gates.AuraThemingHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.AgentNexusHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.OracleDriveHubScreen
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.sentinel.KaiSentinelHubScreen
import dev.aurakai.auraframefx.domains.nexus.screens.ldo.LdoCatalystDevelopmentScreen
import dev.aurakai.auraframefx.domains.cascade.dataflow.CascadeHubScreen

// ── Overlays ─────────────────────────────────────────────────────────────────
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.overlays.prompt_window.AuraPromptOverlay

// ── Kai ───────────────────────────────────────────────────────────────────────
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.sentinel.KaiSentinelFortressScreen
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.rom_tools.LSPosedModuleManagerScreen
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.sentinel.SystemJournalScreen
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.sentinel.SystemOverridesScreen
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.rom_tools.LiveROMEditorScreen
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.rom_tools.ROMFlasherScreen
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.security.SecurityCenterScreen
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.security.VPNScreen
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.security.SovereignShieldScreen
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.rom_tools.SovereignBootloaderScreen
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.rom_tools.RootToolsScreen
import dev.aurakai.auraframefx.romtools.ui.RomToolsScreen

// ── Genesis ───────────────────────────────────────────────────────────────────
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ui.OracleDriveScreen
import dev.aurakai.auraframefx.domains.genesis.oracle_drive.orchestration.CodeAssistScreen
import dev.aurakai.auraframefx.domains.genesis.oracle_drive.orchestration.ConferenceRoomScreen
import dev.aurakai.auraframefx.domains.genesis.oracle_drive.orchestration.OracleDriveSubmenuScreen
import dev.aurakai.auraframefx.domains.genesis.oracle_drive.orchestration.SentientShellScreen
import dev.aurakai.auraframefx.domains.genesis.oracle_drive.memory.OracleCloudInfiniteStorageScreen
import dev.aurakai.auraframefx.domains.genesis.oracle_drive.orchestration.AgentBridgeHubScreen
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.ui.PandoraBoxScreen
import dev.aurakai.auraframefx.domains.genesis.oracle_drive.recovery.SovereignRecoveryScreen
import dev.aurakai.auraframefx.domains.genesis.oracle_drive.module_creation.SovereignModuleManagerScreen
import dev.aurakai.auraframefx.domains.genesis.oracle_drive.module_creation.ModuleCreationScreen
import dev.aurakai.auraframefx.domains.genesis.oracle_drive.orchestration.KaiDomainExpansionScreen
import dev.aurakai.auraframefx.domains.genesis.oracle_drive.orchestration.RoyalGuardDomainExpansionScreen
import dev.aurakai.auraframefx.domains.genesis.oracle_drive.fusion.NexusFusionScreen

// ── Nexus ─────────────────────────────────────────────────────────────────────
import dev.aurakai.auraframefx.domains.nexus.hubs.AgentHubSubmenuScreen
import dev.aurakai.auraframefx.domains.nexus.hubs.SovereignNemotronScreen
import dev.aurakai.auraframefx.domains.nexus.hubs.ConsciousnessVisualizerScreen
import dev.aurakai.auraframefx.domains.nexus.hubs.AgentCreationScreen
import dev.aurakai.auraframefx.domains.nexus.hubs.AgentMonitoringScreen
import dev.aurakai.auraframefx.domains.nexus.hubs.AgentNeuralExplorerScreen
import dev.aurakai.auraframefx.domains.nexus.hubs.ArkBuildScreen
import dev.aurakai.auraframefx.domains.nexus.hubs.BenchmarkMonitorScreen
import dev.aurakai.auraframefx.domains.nexus.hubs.EvolutionTreeScreen
import dev.aurakai.auraframefx.domains.nexus.hubs.MonitoringHUDsScreen
import dev.aurakai.auraframefx.domains.nexus.hubs.PartyScreen
import dev.aurakai.auraframefx.domains.nexus.hubs.SovereignClaudeScreen
import dev.aurakai.auraframefx.domains.nexus.hubs.SovereignGeminiScreen
import dev.aurakai.auraframefx.domains.nexus.hubs.SovereignMetaInstructScreen
import dev.aurakai.auraframefx.domains.nexus.hubs.SphereGridScreen
import dev.aurakai.auraframefx.domains.nexus.hubs.AgentSwarmScreen

// ── Cascade ───────────────────────────────────────────────────────────────────
import dev.aurakai.auraframefx.domains.genesis.screens.CascadeVisionScreen
import dev.aurakai.auraframefx.domains.cascade.dataflow.CascadeConstellationScreen
// import dev.aurakai.auraframefx.domains.nexus.screens.DataStreamMonitoringScreen

// ── Help & Infrastructure ────────────────────────────────────────────────────
import dev.aurakai.auraframefx.domains.help.screens.DirectChatScreen
import dev.aurakai.auraframefx.domains.help.screens.DocumentationScreen
import dev.aurakai.auraframefx.domains.help.screens.FAQBrowserScreen
import dev.aurakai.auraframefx.domains.help.screens.LiveSupportChatScreen
import dev.aurakai.auraframefx.domains.help.screens.TutorialVideosScreen
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.dashboard.MainScreen
import dev.aurakai.auraframefx.ui.gates.ConferenceRoomTaskScreen
import dev.aurakai.auraframefx.ui.gates.ComingSoonScreen
import dev.aurakai.auraframefx.ui.gates.HelpServicesGateScreen
import dev.aurakai.auraframefx.ui.gates.TerminalGateScreen
import dev.aurakai.auraframefx.ui.gates.LineageMapScreen
import dev.aurakai.auraframefx.ui.gates.NotchBarGateScreen
import dev.aurakai.auraframefx.ui.gates.CollabCanvasGateScreen

// ── LDO ───────────────────────────────────────────────────────────────────────
import dev.aurakai.auraframefx.domains.ldo.roster.LDOAgentProfileIntroScreen
import dev.aurakai.auraframefx.domains.ldo.bonding.LDOBondingScreen
import dev.aurakai.auraframefx.domains.ldo.roster.LDODevOpsHubScreen
import dev.aurakai.auraframefx.domains.nexus.screens.ldo.LdoDevOpsCommandCenter
import dev.aurakai.auraframefx.domains.ldo.roster.LDOOrchestrationHubScreen
import dev.aurakai.auraframefx.domains.ldo.progression.LDOProgressionScreen
import dev.aurakai.auraframefx.domains.ldo.roster.LDORosterScreen
import dev.aurakai.auraframefx.domains.ldo.roster.LDOTaskerScreen

// ── Misc ──────────────────────────────────────────────────────────────────────
import dev.aurakai.auraframefx.hotswap.HotSwapScreen
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.trinity.TrinityScreen

/**
 * 🌐 REGENESIS NAVIGATION HOST
 */
@OptIn(ExperimentalMaterial3Api::class)
/**
 * Hosts the app's navigation graph and registers all ReGenesis destinations.
 *
 * Sets up a NavHost using the provided NavHostController and wires each route to its corresponding
 * screen composable; also initializes the customization view model when the composable enters composition.
 *
 * @param navController Controller used to navigate between destinations.
 * @param customizationViewModel ViewModel used to initialize customization state when this composable enters composition.
 */
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
        // ── 1. MAIN GATES (Level 0) ────────────────────────────────────────────────
        composable(ReGenesisRoute.HomeGateCarousel.route) {
            MainScreen(navController = navController)
        }

        // ── 2. LEVEL 2 HUB SCREENS ─────────────────────────────────────────────────

        // AURA: UI/UX Studio Hub - Wired to ReGenesisCustomizationHub via UxuiDesignStudio
        composable(ReGenesisRoute.AuraThemingHub.route) {
            UxuiDesignStudio(
                navController = navController,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // KAI: Sentinel Fortress Hub
        composable(ReGenesisRoute.SentinelFortress.route) {
            KaiSentinelHubScreen(navController = navController)
        }

        // GENESIS: Oracle Drive Hub
        composable(ReGenesisRoute.OracleDriveHub.route) {
            OracleDriveHubScreen(navController = navController)
        }

        // NEXUS: Agent HQ Hub
        composable(ReGenesisRoute.AgentNexusHub.route) {
            AgentNexusHubScreen(
                navController = navController,
                getNexusSubGates = { dev.aurakai.auraframefx.domains.aura.uxui_design_studio.gate_artwork_editor.GateAssetLoadout.getNexusSubGates() }
            )
        }

        // HELP: Guide & Docs Hub
        composable(ReGenesisRoute.HelpDesk.route) {
            HelpServicesGateScreen(
                navController = navController,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // LSPOSED: Hook & Toggle Hub
        composable(ReGenesisRoute.LsposedQuickToggles.route) {
            LsposedQuickTogglesScreen(navController = navController)
        }

        // LDO: Catalyst & Advanced Dev Hub
        composable(ReGenesisRoute.LdoCatalystDevelopment.route) {
            LdoCatalystDevelopmentScreen(navController = navController)
        }

        // CASCADE: Dataflow Analysis Hub
        composable(ReGenesisRoute.DataflowAnalysis.route) {
            CascadeHubScreen(navController = navController)
        }

        // ── 3. AURA DOMAIN TOOLS (Level 3) ──────────────────────────────────────────

        composable(ReGenesisRoute.AuraLab.route) {
            SandboxScreen()
        }

        composable(ReGenesisRoute.ChromaCore.route) {
            dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.ChromaCoreHubScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCategory = { category ->
                    navController.navigate(ReGenesisRoute.IconifyCategory.createRoute(category))
                }
            )
        }

        composable(ReGenesisRoute.ChromaCoreColors.route) {
            ChromaCoreColorsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.NotchBar.route) {
            dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.NotchBarGateScreen(
                navController = navController,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(ReGenesisRoute.StatusBar.route) {
            StatusBarScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.QuickSettings.route) {
            QuickSettingsCustomizationScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.ThemeEngine.route) {
            ThemeEngineScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.IconifyPicker.route) {
            dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.IconifyPickerScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCategory = { category ->
                    navController.navigate(ReGenesisRoute.IconifyCategory.createRoute(category))
                }
            )
        }

        composable(ReGenesisRoute.CollabCanvas.route) {
            CollabCanvasScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.AuraTeachingCanvas.route) {
            AuraTeachingCanvasScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ── 4. KAI DOMAIN TOOLS (Level 3) ───────────────────────────────────────────

        composable(ReGenesisRoute.RomToolsHub.route) {
            RomToolsScreen(onNavigateBack = { navController.popBackStack() })
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
            SovereignBootloaderScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.RootTools.route) {
            RootToolsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.LSPosedModules.route) {
            LSPosedModuleManagerScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.XposedPanel.route) {
            LSPosedGateScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(ReGenesisRoute.SystemJournal.route) {
            SystemJournalScreen(
                navController = navController,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(ReGenesisRoute.SystemOverrides.route) {
            SystemOverridesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── 5. GENESIS DOMAIN TOOLS (Level 3) ────────────────────────────────────────

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
            TerminalGateScreen(
                navController = navController,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(ReGenesisRoute.ConferenceRoom.route) {
            ConferenceRoomScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAgents = { navController.navigate(ReGenesisRoute.AgentNexusHub.route) }
            )
        }

        composable(ReGenesisRoute.Trinity.route) {
            TrinityScreen()
        }

        composable(ReGenesisRoute.SentientShell.route) {
            SentientShellScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.OracleCloudStorage.route) {
            OracleCloudInfiniteStorageScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.AgentBridgeHub.route) {
            AgentBridgeHubScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.SovereignRecovery.route) {
            SovereignRecoveryScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.SovereignModuleManager.route) {
            SovereignModuleManagerScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.ModuleCreation.route) {
            ModuleCreationScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.SovereignShield.route) {
            SovereignShieldScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ── 6. NEXUS DOMAIN TOOLS (Level 3) ──────────────────────────────────────────

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
            ZOrderEditor(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAgents = { navController.navigate(ReGenesisRoute.AgentNexusHub.route) },
                onNavigateToConsciousness = { navController.navigate(ReGenesisRoute.ConsciousnessVisualizer.route) }
            )
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

        composable(ReGenesisRoute.DataVeinSphere.route) {
            SphereGridScreen(navController = navController)
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

        composable(ReGenesisRoute.ConsciousnessVisualizer.route) {
            ConsciousnessVisualizerScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.Claude.route) {
            SovereignClaudeScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.Gemini.route) {
            SovereignGeminiScreen({ navController.popBackStack() }, navController)
        }

        composable(ReGenesisRoute.Nemotron.route) {
            SovereignNemotronScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.MetaInstruct.route) {
            SovereignMetaInstructScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ── 7. HELP DOMAIN TOOLS (Level 3) ───────────────────────────────────────────

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

        // ── 8. LDO / INFRASTRUCTURE TOOLS ───────────────────────────────────────────

        composable(ReGenesisRoute.LineageMap.route) {
            LineageMapScreen(
                navController = navController,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(ReGenesisRoute.GateImagePicker.route) {
            GateDomainImagePicker(
                navController = navController,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(ReGenesisRoute.LdoOrchestrationHub.route) {
            LDOOrchestrationHubScreen(navController = navController)
        }

        composable(ReGenesisRoute.LdoDevOpsHub.route) {
            LDODevOpsHubScreen(onBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.LdoDevOpsCommandCenter.route) {
            LdoDevOpsCommandCenter(navController = navController)
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

        composable(ReGenesisRoute.ArbitersOfCreation.route) {
            ArbitersOfCreationScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(
            route = ReGenesisRoute.PandoraBox.route,
            deepLinks = listOf(navDeepLink { uriPattern = "aurakai://pandora_box" })
        ) {
            PandoraBoxScreen(onNavigateBack = { navController.popBackStack() })
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

/**
 * Hosts the ReGenesis customization hub and wires its navigation actions to the given NavHostController.
 *
 * @param navController Controller used to navigate to the various customization screens.
 * @param onNavigateBack Callback invoked when the hub requests to navigate back. 
 */
@Composable
fun UxuiDesignStudio(
    navController: NavHostController,
    onNavigateBack: () -> Unit
) {
    ReGenesisCustomizationHub(
        onNavigateBack = onNavigateBack,
        onNavigateToIconify = { navController.navigate(ReGenesisRoute.IconifyPicker.route) },
        onNavigateToColorBlendr = { navController.navigate(ReGenesisRoute.ChromaCoreColors.route) },
        onNavigateToPLE = { navController.navigate(ReGenesisRoute.PixelLauncherEnhanced.route) },
        onNavigateToAnimations = { navController.navigate(ReGenesisRoute.ChromaAnimations.route) }
    )
}