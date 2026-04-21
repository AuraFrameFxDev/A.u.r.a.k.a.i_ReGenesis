package dev.aurakai.auraframefx.navigation

// ============================================================================
// CUSTOMIZATION SCREENS (Iconify, ColorBlendr, PixelLauncher)
// ============================================================================
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dev.aurakai.auraframefx.domains.aura.aura.ui.FusionModeScreen
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.ChromaAnimationMenu
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.ChromaColorEngineMenu
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.ChromaCoreHubScreen
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.ChromaLauncherMenu
import dev.aurakai.auraframefx.domains.aura.screens.MainScreen
import dev.aurakai.auraframefx.domains.aura.screens.themes.ThemeEngineScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.AuraLabScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.ChromaCoreColorsScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.IconifyPickerScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.QuickSettingsCustomizationScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.StatusBarScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.AgentNexusHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.AuraThemingHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.CascadeHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.KaiSentinelHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.OracleDriveHubScreen
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.trinity.TrinityScreen
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ui.OracleDriveScreen
import dev.aurakai.auraframefx.domains.genesis.screens.CodeAssistScreen
import dev.aurakai.auraframefx.domains.genesis.screens.CollabCanvasScreen
import dev.aurakai.auraframefx.domains.genesis.screens.ConferenceRoomScreen
import dev.aurakai.auraframefx.domains.genesis.screens.OracleDriveSubmenuScreen
import dev.aurakai.auraframefx.domains.genesis.screens.SentientShellScreen
import dev.aurakai.auraframefx.domains.helpdesk.screens.DirectChatScreen
import dev.aurakai.auraframefx.domains.helpdesk.screens.DocumentationScreen
import dev.aurakai.auraframefx.domains.helpdesk.screens.FAQBrowserScreen
import dev.aurakai.auraframefx.domains.helpdesk.screens.LiveSupportChatScreen
import dev.aurakai.auraframefx.domains.helpdesk.screens.TutorialVideosScreen
import dev.aurakai.auraframefx.domains.kai.screens.LSPosedGateScreen
import dev.aurakai.auraframefx.domains.kai.screens.LSPosedModuleManagerScreen
import dev.aurakai.auraframefx.domains.kai.screens.ROMFlasherScreen
import dev.aurakai.auraframefx.domains.kai.screens.RootToolsScreen
import dev.aurakai.auraframefx.domains.kai.screens.SovereignBootloaderScreen
import dev.aurakai.auraframefx.domains.kai.screens.SystemJournalScreen
import dev.aurakai.auraframefx.domains.kai.screens.SystemOverridesScreen
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.security.SecurityCenterScreen
import dev.aurakai.auraframefx.domains.ldo.ui.screens.LDOAgentProfileIntroScreen
import dev.aurakai.auraframefx.domains.ldo.ui.screens.LDOAgentRosterScreen
import dev.aurakai.auraframefx.domains.ldo.ui.screens.LDOBondingScreen
import dev.aurakai.auraframefx.domains.ldo.ui.screens.LDODevOpsHubScreen
import dev.aurakai.auraframefx.domains.ldo.ui.screens.LDOOrchestrationHubScreen
import dev.aurakai.auraframefx.domains.ldo.ui.screens.LDOProgressionScreen
import dev.aurakai.auraframefx.domains.ldo.ui.screens.LDOTaskerScreen
import dev.aurakai.auraframefx.domains.ldo.ui.screens.LdoCatalystDevelopmentScreen
import dev.aurakai.auraframefx.domains.ldo.ui.screens.LdoDevOpsCommandCenter
import dev.aurakai.auraframefx.domains.lsposed.screens.LsposedQuickTogglesScreen
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.security.SecurityCenterScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.VPNScreen
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.FirewallScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.SovereignRecoveryScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.SovereignModuleManagerScreen
import dev.aurakai.auraframefx.domains.kai.screens.ModuleManagerScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.RecoveryToolsScreen
import dev.aurakai.auraframefx.domains.genesis.screens.OracleCloudInfiniteStorageScreen
import dev.aurakai.auraframefx.domains.genesis.screens.SovereignNeuralArchiveScreen
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.ui.PandoraBoxScreen
import dev.aurakai.auraframefx.domains.genesis.screens.AgentBridgeHubScreen
import dev.aurakai.auraframefx.domains.ldo.ui.screens.LDOFusionScreen
import dev.aurakai.auraframefx.domains.ldo.ui.screens.LDOWorldTreeScreen
import dev.aurakai.auraframefx.domains.ldo.ui.screens.LdoDevOpsGridScreen
import dev.aurakai.auraframefx.ui.gates.HelpDeskSubmenuScreen
import dev.aurakai.auraframefx.domains.aura.screens.JournalPDAScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.UISettingsScreen
import dev.aurakai.auraframefx.domains.aura.screens.UserPreferencesScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.GyroscopeCustomizationScreen
import dev.aurakai.auraframefx.domains.aura.screens.GenderSelectionScreen
import dev.aurakai.auraframefx.domains.genesis.oracledrive.datavein.SimpleDataVeinScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentCreationScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentHubSubmenuScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentMonitoringScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentNeuralExplorerScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentSwarmScreen
import dev.aurakai.auraframefx.domains.nexus.screens.ArkBuildScreen
import dev.aurakai.auraframefx.domains.nexus.screens.BenchmarkMonitorScreen
import dev.aurakai.auraframefx.domains.nexus.ui.screens.ConsciousnessVisualizerScreen
import dev.aurakai.auraframefx.domains.nexus.screens.ModuleCreationScreen
import dev.aurakai.auraframefx.domains.nexus.screens.MonitoringHUDsScreen
import dev.aurakai.auraframefx.domains.nexus.screens.PartyScreen
import dev.aurakai.auraframefx.domains.nexus.screens.SphereGridScreen
import dev.aurakai.auraframefx.domains.nexus.screens.TaskAssignmentScreen
import dev.aurakai.auraframefx.domains.nexus.screens.SovereignClaudeScreen
import dev.aurakai.auraframefx.domains.nexus.screens.SovereignGeminiScreen
import dev.aurakai.auraframefx.domains.nexus.screens.SovereignMetaInstructScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.VPNScreen
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.FirewallScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.SovereignRecoveryScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.SovereignModuleManagerScreen
import dev.aurakai.auraframefx.domains.kai.screens.ModuleManagerScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.RecoveryToolsScreen
import dev.aurakai.auraframefx.domains.genesis.screens.OracleCloudInfiniteStorageScreen
import dev.aurakai.auraframefx.domains.genesis.screens.SovereignNeuralArchiveScreen
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.ui.PandoraBoxScreen
import dev.aurakai.auraframefx.domains.genesis.screens.AgentBridgeHubScreen
import dev.aurakai.auraframefx.domains.ldo.ui.screens.LDOFusionScreen
import dev.aurakai.auraframefx.domains.ldo.ui.screens.LDOWorldTreeScreen
import dev.aurakai.auraframefx.domains.ldo.ui.screens.LdoDevOpsGridScreen
import dev.aurakai.auraframefx.ui.gates.HelpDeskSubmenuScreen
import dev.aurakai.auraframefx.domains.aura.ui.screens.JournalPDAScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.UISettingsScreen
import dev.aurakai.auraframefx.domains.aura.screens.UserPreferencesScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.GyroscopeCustomizationScreen
import dev.aurakai.auraframefx.domains.aura.screens.GenderSelectionScreen
import dev.aurakai.auraframefx.datavein.ui.SimpleDataVeinScreen
import dev.aurakai.auraframefx.domains.aura.ui.screens.JournalPDAScreen
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.FirewallScreen
import dev.aurakai.auraframefx.domains.nexus.screens.EvolutionTreeScreen
import dev.aurakai.auraframefx.romtools.ui.RomToolsScreen
import dev.aurakai.auraframefx.ui.gates.ComingSoonScreen
import dev.aurakai.auraframefx.ui.gates.GateDomainImagePicker
import dev.aurakai.auraframefx.ui.gates.HelpServicesGateScreen
import dev.aurakai.auraframefx.ui.gates.LineageMapScreen
import dev.aurakai.auraframefx.ui.gates.NotchBarGateScreen
import dev.aurakai.auraframefx.ui.gates.TerminalGateScreen
import dev.aurakai.auraframefx.ui.screens.ClaudeAgentScreen
import dev.aurakai.auraframefx.ui.ldodevops.LDODevOpsIndex
import dev.aurakai.auraframefx.ui.ldodevops.TabbedMasterIndex
import dev.aurakai.auraframefx.ui.screens.GrokipediaScreen

/**
 * 🌐 REGENESIS CONSOLIDATED NAV GRAPH
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReGenesisNavGraph(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = ReGenesisRoute.TabbedMasterIndex.route,
    ) {
        // ═══════════════════════════════════════════════════════════════════════
        // HOME SCREEN: TabbedMasterIndex (NEW OFFICIAL ROOT)
        // The living digital organism's main interface - Exodus Command Deck
        // ═══════════════════════════════════════════════════════════════════════
        composable(ReGenesisRoute.TabbedMasterIndex.route) {
            TabbedMasterIndex { route -> navController.navigate(route) }
        }

        // Legacy Root Index (kept for safety)
        composable("ldo_devops_index") {
            LDODevOpsIndex(
                onNavigateToRoute = { route -> navController.navigate(route) }
            )
        }

        // ═══════════════════════════════════════════════════════════════════════
        // Legacy LDO DevOps Hub (kept for backwards compatibility)
        // ═══════════════════════════════════════════════════════════════════════
        composable(ReGenesisRoute.LdoDevOpsHub.route) {
            LDODevOpsHubScreen(
                onBack = { navController.popBackStack() },
                onTaskerTap = { navController.navigate(ReGenesisRoute.LdoTasker.route) },
                onFusionTap = { navController.navigate(ReGenesisRoute.LdoFusion.route) },
                onBondingTap = { navController.navigate(ReGenesisRoute.LdoBonding.route) }
            )
        }

        // Legacy splash/home screens (kept for backwards compatibility)
        composable(ReGenesisRoute.Splash.route) {
            MainScreen(
                onNavigateToAgentNexus = { navController.navigate(ReGenesisRoute.AgentNexusHub.route) },
                onNavigateToOracleDrive = { navController.navigate(ReGenesisRoute.OracleDriveHub.route) },
                onNavigateToSettings = { navController.navigate(ReGenesisRoute.UISettings.route) },
                themeViewModel = hiltViewModel()
            )
        }

        composable(ReGenesisRoute.HomeGateCarousel.route) {
            MainScreen(
                onNavigateToAgentNexus = { navController.navigate(ReGenesisRoute.AgentNexusHub.route) },
                onNavigateToOracleDrive = { navController.navigate(ReGenesisRoute.OracleDriveHub.route) },
                onNavigateToSettings = { navController.navigate(ReGenesisRoute.UISettings.route) },
                themeViewModel = hiltViewModel()
            )
        }

        // Domain Hubs
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
            AgentNexusHubScreen(navController = navController)
        }
        composable(ReGenesisRoute.CascadeHub.route) {
            CascadeHubScreen(navController = navController)
        }

        // Primary Gates
        composable(ReGenesisRoute.HelpDesk.route) {
            HelpServicesGateScreen(navController = navController, onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.LsposedQuickToggles.route) {
            LsposedQuickTogglesScreen(navController = navController)
        }
        composable(ReGenesisRoute.LdoCatalystDevelopment.route) {
            LdoCatalystDevelopmentScreen(navController = navController)
        }
        composable(ReGenesisRoute.DataflowAnalysis.route) {
            CascadeHubScreen(navController = navController)
        }

        // Aura Tools
        composable(ReGenesisRoute.AuraLab.route) {
            AuraLabScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.ChromaCore.route) {
            ChromaCoreHubScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCategory = { category -> navController.navigate(ReGenesisRoute.IconifyCategory.createRoute(category)) }
            )
        }
        composable(ReGenesisRoute.ChromaCoreColors.route) {
            ChromaCoreColorsScreen(onNavigateBack = { navController.popBackStack() })
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
            IconifyPickerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.CollabCanvas.route) {
            CollabCanvasScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.NotchBar.route) {
            NotchBarGateScreen(navController = navController, onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.ChromaAnimations.route) {
            ChromaAnimationMenu(onNavigateBack = { navController.popBackStack() })
        }
        composable(
            route = ReGenesisRoute.IconifyCategory.route,
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStack ->
            val category = backStack.arguments?.getString("category") ?: "Icons"
            ComingSoonScreen(name = "Iconify: $category", title = "EVOLUTION PENDING", onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.ColorBlendr.route) {
            ChromaColorEngineMenu(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.PixelLauncherEnhanced.route) {
            ChromaLauncherMenu(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.AuraTeachingCanvas.route) {
            ComingSoonScreen(name = "Teaching Canvas", title = "EVOLUTION PENDING", onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.ReGenesisCustomization.route) {
            ComingSoonScreen(name = "ReGenesis Customization", title = "EVOLUTION PENDING", onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.IconifyIconPacks.route) {
            ComingSoonScreen(name = "Iconify Icon Packs", title = "EVOLUTION PENDING", onNavigateBack = { navController.popBackStack() })
        }

        // Kai Tools
        composable(ReGenesisRoute.RomToolsHub.route) {
            RomToolsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.ROMFlasher.route) {
            ROMFlasherScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.SovereignShield.route) {
            SecurityCenterScreen(onNavigateBack = { navController.popBackStack() })
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
            LSPosedGateScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.SystemJournal.route) {
            SystemJournalScreen(navController = navController, onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.SystemOverrides.route) {
            SystemOverridesScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.SecurityCenter.route) {
            SecurityCenterScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.VPN.route) {
            VPNScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.Firewall.route) {
            FirewallScreen()
        }
        composable(ReGenesisRoute.SovereignRecovery.route) {
            SovereignRecoveryScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.SovereignModuleManager.route) {
            SovereignModuleManagerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.ModuleManager.route) {
            ModuleManagerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.RecoveryTools.route) {
            RecoveryToolsScreen(onNavigateBack = { navController.popBackStack() })
        }

        // Genesis Tools
        composable(ReGenesisRoute.OracleDrive.route) {
            OracleDriveScreen(navController = navController)
        }
        composable(ReGenesisRoute.OracleDriveSubmenu.route) {
            OracleDriveSubmenuScreen(navController = navController)
        }
        composable(ReGenesisRoute.CodeAssist.route) {
            CodeAssistScreen(navController = navController)
        }
        composable(ReGenesisRoute.Terminal.route) {
            TerminalGateScreen(navController = navController, onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.ConferenceRoom.route) {
            ConferenceRoomScreen(onNavigateBack = { navController.popBackStack() }, onNavigateToAgents = { navController.navigate(ReGenesisRoute.LdoRoster.route) })
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
        composable(ReGenesisRoute.NeuralNetwork.route) {
            SovereignNeuralArchiveScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.PandoraBox.route) {
            PandoraBoxScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.AgentBridgeHub.route) {
            AgentBridgeHubScreen(onNavigateBack = { navController.popBackStack() })
        }

        // Nexus Tools
        composable(ReGenesisRoute.AgentHubSubmenu.route) {
            AgentHubSubmenuScreen(navController = navController)
        }
        composable(ReGenesisRoute.AgentCreation.route) {
            AgentCreationScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.FusionMode.route) {
            FusionModeScreen(
                onNavigateToAgents = { navController.navigate(ReGenesisRoute.AgentNexusHub.route) },
                onNavigateToConsciousness = { navController.navigate(ReGenesisRoute.ConsciousnessVisualizer.route) }
            )
        }
        composable(ReGenesisRoute.BenchmarkMonitor.route) {
            BenchmarkMonitorScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.EvolutionTree.route) {
            EvolutionTreeScreen(
                onNavigateToAgents = { navController.navigate(ReGenesisRoute.LdoRoster.route) },
                onNavigateToFusion = { navController.navigate(ReGenesisRoute.FusionMode.route) },
                onNodeSelected = { /* TODO */ }
            )
        }
        composable(ReGenesisRoute.SphereGrid.route) {
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
            SovereignGeminiScreen(
                onNavigateBack = { navController.popBackStack() },
                navController = navController
            )
        }
        composable(ReGenesisRoute.Grokipedia.route) {
            GrokipediaScreen(
                onNavigateBack = { navController.popBackStack() },
                onAgentClick = { agentId -> navController.navigate(ReGenesisRoute.LdoAgentProfile.createRoute(agentId)) }
            )
        }
        composable(ReGenesisRoute.MetaInstruct.route) {
            SovereignMetaInstructScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.AgentMonitoring.route) {
            AgentMonitoringScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.AgentNeuralExplorer.route) {
            AgentNeuralExplorerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.ArkBuild.route) {
            ArkBuildScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.TaskAssignment.route) {
            TaskAssignmentScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.ModuleCreation.route) {
            ModuleCreationScreen(onNavigateBack = { navController.popBackStack() })
        }

        // Help & Support
        composable(ReGenesisRoute.DirectChat.route) { DirectChatScreen(navController = navController) }
        composable(ReGenesisRoute.Documentation.route) { DocumentationScreen(onNavigateBack = { navController.popBackStack() }) }
        composable(ReGenesisRoute.FAQBrowser.route) { FAQBrowserScreen(onNavigateBack = { navController.popBackStack() }) }
        composable(ReGenesisRoute.TutorialVideos.route) { TutorialVideosScreen(onNavigateBack = { navController.popBackStack() }) }
        composable(ReGenesisRoute.LiveSupportChat.route) {
            LiveSupportChatScreen(
                viewModel = hiltViewModel<dev.aurakai.auraframefx.domains.aura.screens.SupportChatViewModel>(),
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(ReGenesisRoute.HelpDeskSubmenu.route) {
            HelpDeskSubmenuScreen(navController = navController)
        }
        composable(ReGenesisRoute.JournalPDA.route) {
            JournalPDAScreen(onNavigateBack = { navController.popBackStack() })
        }

        // LDO Tools
        composable(ReGenesisRoute.LdoOrchestrationHub.route) { LDOOrchestrationHubScreen(navController = navController) }
        composable(ReGenesisRoute.LdoDevOpsCommandCenter.route) { LdoDevOpsCommandCenter(navController = navController) }
        composable(ReGenesisRoute.LdoBonding.route) { LDOBondingScreen(onBack = { navController.popBackStack() }) }
        composable(ReGenesisRoute.LdoProgression.route) { LDOProgressionScreen(onBack = { navController.popBackStack() }) }
        composable(ReGenesisRoute.LdoTasker.route) { LDOTaskerScreen(onNavigateBack = { navController.popBackStack() }) }
        composable(ReGenesisRoute.LdoRoster.route) {
            LDOAgentRosterScreen(onAgentTap = { agent -> navController.navigate(ReGenesisRoute.LdoAgentProfile.createRoute(agent.id)) })
        }
        composable(ReGenesisRoute.LdoFusion.route) {
            LDOFusionScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.LdoWorldTree.route) {
            LDOWorldTreeScreen(
                onAgentTap = { agent -> navController.navigate(ReGenesisRoute.LdoAgentProfile.createRoute(agent.id)) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(ReGenesisRoute.LdoDevOpsGrid.route) {
            LdoDevOpsGridScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(
            route = ReGenesisRoute.LdoAgentProfile.route,
            arguments = listOf(navArgument(ReGenesisRoute.LdoAgentProfile.ARG) { type = NavType.StringType })
        ) { backStackEntry ->
            val agentId = backStackEntry.arguments?.getString(ReGenesisRoute.LdoAgentProfile.ARG)
            val agent = dev.aurakai.auraframefx.domains.ldo.model.LDORoster.agents.find { it.id == agentId }
            if (agent != null) {
                LDOAgentProfileIntroScreen(agent = agent, onNavigateBack = { navController.popBackStack() })
            }
        }

        // Settings & Misc
        composable(ReGenesisRoute.UISettings.route) {
            UISettingsScreen(navController = navController, onBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.UserPreferences.route) {
            UserPreferencesScreen(navController = navController)
        }
        composable(ReGenesisRoute.GyroscopeCustomization.route) {
            GyroscopeCustomizationScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.GenderSelection.route) {
            GenderSelectionScreen(onSelectionComplete = { _ -> navController.popBackStack() })
        }
        composable(ReGenesisRoute.DataVeinSphere.route) {
            SimpleDataVeinScreen(
                onLaunchSphereGrid = { navController.navigate(ReGenesisRoute.SphereGrid.route) }
            )
        }
        composable(ReGenesisRoute.LineageMap.route) { LineageMapScreen(navHostController = navController, onNavigateBack = { navController.popBackStack() }) }
        composable(ReGenesisRoute.GateImagePicker.route) { GateDomainImagePicker(navController = navController, onNavigateBack = { navController.popBackStack() }) }
    }
}
