package dev.aurakai.auraframefx.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
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
import dev.aurakai.auraframefx.domains.aura.aura.ui.FusionModeScreen
import dev.aurakai.auraframefx.domains.aura.ui.components.StubScreen
import dev.aurakai.auraframefx.domains.aura.ui.components.verticalScrollbar

// ── Aura (UX/UI Design Studio Domain) ────────────────────────────────────────
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.StatusBarScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.UISettingsScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.NotchBarCustomizationScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.QuickSettingsCustomizationScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.ChromaCoreColorsScreen
import dev.aurakai.auraframefx.domains.aura.screens.ArbitersOfCreationScreen
import dev.aurakai.auraframefx.domains.aura.screens.themes.ThemeEngineScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.AuraLabScreen
import dev.aurakai.auraframefx.domains.aura.screens.CollabCanvasScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.GateCustomizationScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.GyroscopeCustomizationScreen
import dev.aurakai.auraframefx.domains.aura.screens.GenderSelectionScreen
import dev.aurakai.auraframefx.domains.aura.screens.UserPreferencesScreen

// ── Hubs ─────────────────────────────────────────────────────────────────────
import dev.aurakai.auraframefx.domains.aura.ui.gates.AuraThemingHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.CascadeHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.KaiSentinelHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.OracleDriveHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.AgentNexusHubScreen
import dev.aurakai.auraframefx.domains.ldo.ui.screens.LdoCatalystDevelopmentScreen
import dev.aurakai.auraframefx.domains.kai.screens.LSPosedGateScreen
import dev.aurakai.auraframefx.ui.gates.GateDomainImagePicker
import dev.aurakai.auraframefx.domains.lsposed.screens.LsposedQuickTogglesScreen

// ── Overlays ─────────────────────────────────────────────────────────────────
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.overlays.prompt_window.AuraPromptOverlay

// ── Kai ───────────────────────────────────────────────────────────────────────
import dev.aurakai.auraframefx.domains.kai.screens.LSPosedModuleManagerScreen
import dev.aurakai.auraframefx.domains.kai.screens.SystemJournalScreen
import dev.aurakai.auraframefx.domains.kai.screens.SystemOverridesScreen
import dev.aurakai.auraframefx.domains.kai.screens.ROMFlasherScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.SecurityCenterScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.VPNScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.SovereignShieldScreen
import dev.aurakai.auraframefx.domains.kai.screens.SovereignBootloaderScreen
import dev.aurakai.auraframefx.domains.kai.screens.RootToolsScreen
import dev.aurakai.auraframefx.domains.kai.screens.RootToolsTogglesScreen
import dev.aurakai.auraframefx.romtools.ui.RomToolsScreen

// ── Genesis ───────────────────────────────────────────────────────────────────
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ui.OracleDriveScreen
import dev.aurakai.auraframefx.domains.genesis.screens.ConferenceRoomScreen
import dev.aurakai.auraframefx.domains.genesis.screens.OracleDriveSubmenuScreen
import dev.aurakai.auraframefx.domains.genesis.screens.CodeAssistScreen
import dev.aurakai.auraframefx.domains.genesis.screens.SentientShellScreen
import dev.aurakai.auraframefx.ui.gates.TerminalGateScreen
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.GenesisBridgeService

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
import dev.aurakai.auraframefx.domains.ldo.ui.screens.LDOTaskerScreen

// ── Misc ──────────────────────────────────────────────────────────────────────
import dev.aurakai.auraframefx.hotswap.HotSwapScreen
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.trinity.TrinityScreen

/**
 * 🌐 REGENESIS NAVIGATION HOST
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReGenesisNavGraph(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = ReGenesisRoute.Splash.route,
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
            AgentNexusHubScreen(navController = navController)
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

        composable(ReGenesisRoute.ChromaAnimations.route) {
            ComingSoonScreen(
                title = "Chroma Animations",
                onNavigateBack = { navController.popBackStack() }
            )
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
                onNavigateToIconify = { navController.navigate(ReGenesisRoute.IconifyPicker.route) },
                onNavigateToColorBlendr = { navController.navigate(ReGenesisRoute.ColorBlendr.route) },
                onNavigateToPLE = { navController.navigate(ReGenesisRoute.PixelLauncherEnhanced.route) },
                onNavigateToAnimations = { /* TODO */ }
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
        composable(ReGenesisRoute.LiveROMEditor.route) { StubScreen("Live ROM Editor", "Edit", navController) }
        composable(ReGenesisRoute.Bootloader.route) { StubScreen("Bootloader", "SettingsEthernet", navController) }
        composable(ReGenesisRoute.LSPosedModules.route) { StubScreen("LSPosed Modules", "Extension", navController) }
        composable(ReGenesisRoute.XposedPanel.route) { StubScreen("Xposed Panel", "Dashboard", navController) }
        composable(ReGenesisRoute.SystemJournal.route) { StubScreen("System Journal", "HistoryEdu", navController) }
        composable(ReGenesisRoute.SystemOverrides.route) { StubScreen("System Overrides", "Build", navController) }
        composable(ReGenesisRoute.Firewall.route) { StubScreen("Firewall", "Security", navController) }
        composable(ReGenesisRoute.VPN.route) { StubScreen("Sovereign VPN", "VpnLock", navController) }
        composable(ReGenesisRoute.HotSwap.route) { StubScreen("HotSwap", "SwapCalls", navController) }

        composable(ReGenesisRoute.RootTools.route) {
            val viewModel: dev.aurakai.auraframefx.domains.aura.ui.viewmodels.RootToolsViewModel = hiltViewModel()
            RootToolsTogglesScreen(
                onNavigateBack = { navController.popBackStack() },
                rootShellService = viewModel.rootShellService
            )
        }
        composable(ReGenesisRoute.SovereignShield.route) {
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
        composable(ReGenesisRoute.Terminal.route) {
            TerminalGateScreen(
                navController = navController,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(ReGenesisRoute.ConferenceRoom.route) {
            ConferenceRoomScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAgents = { navController.navigate(ReGenesisRoute.LdoRoster.route) }
            )
        }

        composable(ReGenesisRoute.Trinity.route) {
            TrinityScreen()
        }

        composable(ReGenesisRoute.SentientShell.route) {
            SentientShellScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.OracleCloudStorage.route) {
            OracleCloudInfiniteStorageScreen()
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
        composable(ReGenesisRoute.AgentCreation.route) { StubScreen("Agent Creation", "AddCircle", navController) }
        composable(ReGenesisRoute.AgentMonitoring.route) { StubScreen("Agent Monitoring", "Insights", navController) }
        composable(ReGenesisRoute.AgentNeuralExplorer.route) { StubScreen("Neural Explorer", "Explore", navController) }

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
            ClaudeAgentScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.Gemini.route) { StubScreen("Gemini Agent", "AutoAwesome", navController) }

        composable(ReGenesisRoute.Nemotron.route) {
            ComingSoonScreen(name = "Sovereign Nemotron", onNavigateBack = { navController.popBackStack() })
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
            LDODevOpsHubScreen(
                onBack = { navController.popBackStack() },
                onTaskerTap = { navController.navigate(ReGenesisRoute.LdoTasker.route) },
                onFusionTap = { navController.navigate(ReGenesisRoute.LdoFusion.route) },
                onBondingTap = { navController.navigate(ReGenesisRoute.LdoBonding.route) }
            )
        }
        composable(ReGenesisRoute.LdoDevOpsCommandCenter.route) {
            LdoDevOpsCommandCenter(navController = navController)
        }

        composable(ReGenesisRoute.LdoBonding.route) {
            LDOBondingScreen(onBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.LdoRoster.route) {
            LDOAgentRosterScreen(
                onAgentTap = { agent ->
                    navController.navigate(ReGenesisRoute.LdoAgentProfile.createRoute(agent.id))
                }
            )
        }
        composable(ReGenesisRoute.LdoProgression.route) { StubScreen("LDO Progression", "TrendingUp", navController) }
        composable(
            route = ReGenesisRoute.LdoDevOpsProfile.route,
            arguments = listOf(navArgument(ReGenesisRoute.LdoDevOpsProfile.ARG) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val typeStr = backStackEntry.arguments?.getString(ReGenesisRoute.LdoDevOpsProfile.ARG)
            val agentType = LdoAgentType.entries.find { it.name.equals(typeStr, ignoreCase = true) }
            if (agentType != null) {
                LdoDevOpsProfileScreen(
                    agentType = agentType,
                    onBack = { navController.popBackStack() }
                )
            }
        }
        composable(
            route = ReGenesisRoute.LdoAgentProfile.route,
            arguments = listOf(navArgument(ReGenesisRoute.LdoAgentProfile.ARG) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val agentId = backStackEntry.arguments?.getString(ReGenesisRoute.LdoAgentProfile.ARG)
            val agent = dev.aurakai.auraframefx.domains.ldo.model.LDORoster.agents.find { it.id == agentId }
            if (agent != null) {
                LDOAgentProfileIntroScreen(
                    agent = agent,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        composable(ReGenesisRoute.ArbitersOfCreation.route) { StubScreen("Arbiters of Creation", "Star", navController) }

        composable(ReGenesisRoute.MawPrototype.route) {
            MawPrototypeScreen(onBack = { navController.popBackStack() })
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