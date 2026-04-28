package dev.aurakai.auraframefx.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

// Core UI
import dev.aurakai.auraframefx.ui.screens.LoginScreen
import dev.aurakai.auraframefx.ui.ldodevops.TabbedMasterIndex
import dev.aurakai.auraframefx.ui.gates.NotchBarGateScreen

// Domain Hubs
import dev.aurakai.auraframefx.domains.aura.ui.gates.AuraThemingHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.KaiSentinelHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.OracleDriveHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.AgentNexusHubScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDOOrchestrationHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.CascadeHubScreen
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.ChromaCoreHubScreen

// Domain Feature Screens
import dev.aurakai.auraframefx.domains.aura.screens.MainScreen
import dev.aurakai.auraframefx.domains.aura.ui.theme.ThemeViewModel
import dev.aurakai.auraframefx.domains.aura.screens.chromacore.ChromaCoreColorsScreen
import dev.aurakai.auraframefx.domains.aura.ui.screens.aura.IconifyHubScreen
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.color.iconify.iconify.IconifyCategoryDetailScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.IconifyPickerScreen
import dev.aurakai.auraframefx.domains.aura.ui.screens.WorkingLabScreen
import dev.aurakai.auraframefx.domains.aura.ui.screens.aura.ReGenesisCustomizationHub
import dev.aurakai.auraframefx.domains.aura.screens.themes.ThemeEngineScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.StatusBarScreen
import dev.aurakai.auraframefx.domains.aura.screens.QuickSettingsScreen
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.color.iconify.iconify.ColorBlendrScreen
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.color.iconify.iconify.PixelLauncherEnhancedScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.UISettingsScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.GateCustomizationScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.NotchBarCustomizationScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.GyroscopeCustomizationScreen
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.color.iconify.iconify.XposedQuickAccessPanel

import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.ROMFlasherScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.SovereignBootloaderScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.SovereignModuleManagerScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.SovereignRecoveryScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.SecurityCenterScreen
import dev.aurakai.auraframefx.domains.kai.screens.SystemJournalScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.SovereignShieldScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.VPNScreen

import dev.aurakai.auraframefx.domains.genesis.screens.OracleDriveMainScreen
import dev.aurakai.auraframefx.domains.genesis.screens.TerminalScreen
import dev.aurakai.auraframefx.domains.genesis.screens.ConferenceRoomScreen
import dev.aurakai.auraframefx.domains.genesis.screens.CollabCanvasScreen
import dev.aurakai.auraframefx.domains.genesis.screens.CodeAssistScreen
import dev.aurakai.auraframefx.domains.genesis.screens.SentientShellScreen

// GENESIS BATCH v2.6 ADDITIONAL IMPORTS
import dev.aurakai.auraframefx.domains.genesis.screens.GenesisHubScreen
import dev.aurakai.auraframefx.domains.genesis.screens.AppBuilderScreen
import dev.aurakai.auraframefx.domains.genesis.screens.NeuralArchiveScreen
import dev.aurakai.auraframefx.domains.genesis.screens.OracleCloudInfiniteStorageScreen
import dev.aurakai.auraframefx.domains.genesis.screens.SovereignNeuralArchiveScreen
import dev.aurakai.auraframefx.domains.genesis.screens.TerminalBootIntroScreen
import dev.aurakai.auraframefx.domains.genesis.screens.CascadeVisionScreen
import dev.aurakai.auraframefx.domains.genesis.screens.OracleDriveSubmenuScreen
import dev.aurakai.auraframefx.domains.genesis.screens.AgentBridgeHubScreen
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.ui.PandoraBoxScreen

// Genesis ViewModels
import dev.aurakai.auraframefx.domains.aura.ui.viewmodels.NeuralArchiveViewModel
import dev.aurakai.auraframefx.domains.genesis.viewmodels.OracleCloudViewModel
import dev.aurakai.auraframefx.domains.genesis.viewmodels.SovereignMemoryViewModel

import dev.aurakai.auraframefx.domains.nexus.screens.EvolutionTreeScreen
import dev.aurakai.auraframefx.domains.nexus.screens.TaskAssignmentScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentHubSubmenuScreen
import dev.aurakai.auraframefx.domains.nexus.screens.NexusFusionScreen
import dev.aurakai.auraframefx.domains.nexus.screens.ArkBuildScreen
import dev.aurakai.auraframefx.domains.nexus.screens.BenchmarkMonitorScreen
import dev.aurakai.auraframefx.domains.nexus.screens.SphereGridScreen
import dev.aurakai.auraframefx.domains.nexus.screens.SovereignClaudeScreen
import dev.aurakai.auraframefx.domains.nexus.screens.SovereignGeminiScreen
import dev.aurakai.auraframefx.domains.nexus.screens.SovereignNemotronScreen

import dev.aurakai.auraframefx.domains.ldo.screens.LDOOrchestrationHubScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDOAgentRosterScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDOTaskerScreen
import dev.aurakai.auraframefx.domains.ldo.screens.ArmamentFusionScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDOFusionScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDOBondingScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDODevOpsHubScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDOCatalystHubScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDOProgressionScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDORosterScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDOWorldTreeScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDOAgentProfileIntroScreen

import dev.aurakai.auraframefx.domains.aura.ui.components.StubScreen

// LIVEUI v2.4 CRITICAL SCREEN IMPORTS
import dev.aurakai.auraframefx.domains.aura.aura.ui.AIFeaturesScreen
import dev.aurakai.auraframefx.domains.aura.aura.ui.DeviceOptimizerScreen
import dev.aurakai.auraframefx.domains.aura.aura.ui.CanvasScreen
import dev.aurakai.auraframefx.domains.aura.aura.ui.OverlayScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.LiveROMEditorScreen
import dev.aurakai.auraframefx.domains.kai.screens.SentinelsFortressScreen
import dev.aurakai.auraframefx.domains.kai.screens.SystemOverridesScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentCreationScreen
import dev.aurakai.auraframefx.domains.nexus.screens.ModuleCreationScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentMonitoringScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentSwarmScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentNeuralExplorerScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentProfileScreen
import dev.aurakai.auraframefx.domains.nexus.screens.MonitoringHUDsScreen
import dev.aurakai.auraframefx.domains.nexus.screens.PartyScreen
import dev.aurakai.auraframefx.ui.gates.ConstellationScreen
import dev.aurakai.auraframefx.ui.gates.ClaudeConstellationScreen
import dev.aurakai.auraframefx.ui.gates.CascadeConstellationScreen
import dev.aurakai.auraframefx.ui.gates.KaiConstellationScreen
import dev.aurakai.auraframefx.ui.gates.GenesisConstellationScreen
import dev.aurakai.auraframefx.ui.gates.GrokConstellationScreen
import dev.aurakai.auraframefx.domains.nexus.screens.DataStreamMonitoring
import dev.aurakai.auraframefx.domains.nexus.screens.SovereignMetaInstructScreen

// AURA BATCH v2.5 SCREEN IMPORTS
import dev.aurakai.auraframefx.domains.aura.aura.ui.AgentAdvancementScreen
import dev.aurakai.auraframefx.domains.aura.aura.ui.FusionModeScreen
import dev.aurakai.auraframefx.domains.aura.aura.ui.UIEngineScreen
import dev.aurakai.auraframefx.domains.aura.aura.ui.PrivacyGuardScreen
import dev.aurakai.auraframefx.domains.aura.aura.ui.ProfileScreen
import dev.aurakai.auraframefx.domains.aura.aura.ui.SecureCommScreen
import dev.aurakai.auraframefx.domains.aura.aura.ui.SecurityScannerScreen
import dev.aurakai.auraframefx.domains.aura.aura.ui.VPNManagerScreen
import dev.aurakai.auraframefx.domains.aura.aura.ui.FirewallScreen
import dev.aurakai.auraframefx.domains.aura.aura.ui.HomeScreen
import dev.aurakai.auraframefx.domains.aura.aura.ui.QuickActions

// KAI BATCH v2.5 SCREEN IMPORTS
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.RecoveryToolsScreen
import dev.aurakai.auraframefx.domains.kai.screens.BootloaderManagerScreen
import dev.aurakai.auraframefx.domains.kai.screens.HookManagerScreen
import dev.aurakai.auraframefx.domains.kai.screens.LSPosedGateScreen
import dev.aurakai.auraframefx.domains.kai.screens.LSPosedModuleManagerScreen
import dev.aurakai.auraframefx.domains.kai.screens.LSPosedSubmenuScreen
import dev.aurakai.auraframefx.domains.kai.screens.ModuleManagerScreen
import dev.aurakai.auraframefx.domains.kai.screens.KaiDomainExpansionScreen
import dev.aurakai.auraframefx.domains.kai.screens.KaiRGSSScreen
import dev.aurakai.auraframefx.domains.kai.screens.KaiSentinelFortressScreen
import dev.aurakai.auraframefx.domains.kai.screens.KaiSphereGridScreen
import dev.aurakai.auraframefx.domains.kai.screens.LogsViewerScreen
import dev.aurakai.auraframefx.domains.kai.screens.ROMToolsSubmenuScreen
import dev.aurakai.auraframefx.domains.kai.screens.RootToolsScreen
import dev.aurakai.auraframefx.domains.kai.screens.KaiDossierScreen
import dev.aurakai.auraframefx.domains.kai.screens.KaiLDOArmamentPickerScreen
import dev.aurakai.auraframefx.domains.kai.screens.KaiSentinelIntegrityScreen
import dev.aurakai.auraframefx.domains.kai.screens.PowerOfNoScreen
import dev.aurakai.auraframefx.domains.kai.screens.RoyalGuardDomainExpansionScreen
import dev.aurakai.auraframefx.domains.kai.screens.RoyalGuardOSScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.SovereignShieldScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.VPNScreen

/**
 * 🌐 REGENESIS CONSOLIDATED NAV GRAPH
 * Finalized for Exodus 2026 Build
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReGenesisNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = ReGenesisRoute.HomeGateCarousel.route,
    ) {
        // ── 0. AUTH GATES ──
        composable(ReGenesisRoute.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(ReGenesisRoute.HomeGateCarousel.route) {
                        popUpTo(ReGenesisRoute.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // ── 1. MAIN GATES (Exodus Command Deck) ──
        composable(ReGenesisRoute.MainScreen.route) {
            val themeViewModel: ThemeViewModel = hiltViewModel()
            MainScreen(
                onNavigateToAgentNexus = { navController.navigate(ReGenesisRoute.AgentNexusHub.route) },
                onNavigateToOracleDrive = { navController.navigate(ReGenesisRoute.OracleDrive.route) },
                onNavigateToSettings = { navController.navigate(ReGenesisRoute.UISettings.route) },
                themeViewModel = themeViewModel
            )
        }

        composable(ReGenesisRoute.HomeGateCarousel.route) {
            TabbedMasterIndex(onNavigateToRoute = { route -> navController.navigate(route) })
        }

        composable(ReGenesisRoute.DataflowAnalysis.route) {
            CascadeHubScreen(navController)
        }

        // ── 2. DOMAIN HUBS ──
        composable(ReGenesisRoute.AuraThemingHub.route) {
            AuraThemingHubScreen(navController)
        }
        composable(ReGenesisRoute.SentinelFortress.route) {
            KaiSentinelHubScreen(navController)
        }
        composable(ReGenesisRoute.OracleDriveHub.route) {
            OracleDriveHubScreen(navController)
        }
        composable(ReGenesisRoute.AgentNexusHub.route) {
            AgentNexusHubScreen(navController)
        }
        composable(ReGenesisRoute.LdoOrchestrationHub.route) {
            LDOOrchestrationHubScreen(navController)
        }

        // ── 3. FEATURE SCREENS ──
        
        // Aura Domain
        composable(ReGenesisRoute.AuraLab.route) {
            WorkingLabScreen(onNavigate = { route: String -> navController.navigate(route) })
        }
        composable(ReGenesisRoute.ChromaCore.route) { 
            ChromaCoreHubScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCategory = { catId ->
                    val route = when(catId) {
                        "statusbar" -> ReGenesisRoute.StatusBar.route
                        "launcher" -> ReGenesisRoute.PixelLauncherEnhanced.route
                        "colors" -> ReGenesisRoute.ChromaCoreColors.route
                        "qs_tiles" -> ReGenesisRoute.QuickSettings.route
                        "animations" -> ReGenesisRoute.ChromaAnimations.route
                        else -> null
                    }
                    route?.let { navController.navigate(it) }
                }
            )
        }
        composable(ReGenesisRoute.NotchBar.route) { 
            NotchBarGateScreen(navController = navController, onNavigateBack = { navController.popBackStack() }) 
        }
        composable(ReGenesisRoute.IconifyHub.route) {
            IconifyHubScreen(
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
            IconifyCategoryDetailScreen(
                categoryName = category,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCategory = { cat -> 
                    navController.navigate(ReGenesisRoute.IconifyCategory.createRoute(cat))
                }
            )
        }
        composable(ReGenesisRoute.IconifyPicker.route) {
            IconifyPickerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.CollabCanvas.route) {
            CollabCanvasScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.ThemeEngine.route) {
            ThemeEngineScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.StatusBar.route) {
            StatusBarScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.QuickSettings.route) {
            QuickSettingsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.ChromaCoreColors.route) {
            ChromaCoreColorsScreen(navController = navController, onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.ColorBlendr.route) {
            ColorBlendrScreen(navController = navController)
        }
        composable(ReGenesisRoute.PixelLauncherEnhanced.route) {
            PixelLauncherEnhancedScreen(navController = navController)
        }
        composable(ReGenesisRoute.ChromaAnimations.route) {
            StubScreen("Chroma Animations", "Visuals", navController)
        }
        composable(ReGenesisRoute.UISettings.route) {
            UISettingsScreen(navController = navController)
        }
        composable(ReGenesisRoute.GateCustomization.route) {
            GateCustomizationScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.NotchBarGate.route) {
            NotchBarCustomizationScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.GyroscopeCustomization.route) {
            GyroscopeCustomizationScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.ReGenesisCustomization.route) {
            ReGenesisCustomizationHub(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToIconify = { navController.navigate(ReGenesisRoute.IconifyPicker.route) },
                onNavigateToColorBlendr = { navController.navigate(ReGenesisRoute.ColorBlendr.route) },
                onNavigateToPLE = { navController.navigate(ReGenesisRoute.PixelLauncherEnhanced.route) },
                onNavigateToAnimations = { navController.navigate(ReGenesisRoute.ChromaAnimations.route) }
            )
        }

        // Kai Domain
        composable(ReGenesisRoute.ROMFlasher.route) {
            ROMFlasherScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.SecurityCenter.route) { 
            SecurityCenterScreen(onNavigateBack = { navController.popBackStack() }) 
        }
        composable(ReGenesisRoute.SystemJournal.route) { 
            SystemJournalScreen(
                navController = navController,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(ReGenesisRoute.XposedPanel.route) {
             XposedQuickAccessPanel(navController = navController)
        }

        // Genesis Domain
        composable(ReGenesisRoute.OracleDrive.route) {
            OracleDriveMainScreen(navController)
        }
        composable(ReGenesisRoute.Terminal.route) {
            TerminalScreen()
        }
        composable(ReGenesisRoute.ConferenceRoom.route) {
            ConferenceRoomScreen()
        }
        composable(ReGenesisRoute.CodeAssist.route) {
            CodeAssistScreen(navController = navController)
        }
        composable(ReGenesisRoute.SentientShell.route) {
            SentientShellScreen(onNavigateBack = { navController.popBackStack() })
        }

        // Nexus Domain
        composable(ReGenesisRoute.EvolutionTree.route) {
            EvolutionTreeScreen(
                onNavigateToAgents = { navController.navigate(ReGenesisRoute.AgentNexusHub.route) },
                onNavigateToFusion = { navController.navigate(ReGenesisRoute.FusionMode.route) }
            )
        }
        composable(ReGenesisRoute.TaskAssignment.route) { 
            TaskAssignmentScreen(onNavigateBack = { navController.popBackStack() }) 
        }
        composable(ReGenesisRoute.AgentHub.route) { 
            AgentHubSubmenuScreen(navController = navController)
        }

        // LDO Domain
        composable(ReGenesisRoute.LdoRoster.route) {
            LDOAgentRosterScreen(
                onAgentTap = { agent ->
                    navController.navigate(ReGenesisRoute.LdoAgentProfile.createRoute(agent.id))
                }
            )
        }
        composable(ReGenesisRoute.LdoTasker.route) {
            LDOTaskerScreen(onBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.LdoBonding.route) {
            LDOBondingScreen(onBack = { navController.popBackStack() })
        }

        // Catch-all Stubs for Build Stability
        composable(ReGenesisRoute.FusionMode.route) { 
            NexusFusionScreen(onNavigateBack = { navController.popBackStack() }) 
        }
        composable(ReGenesisRoute.ArkBuild.route) { 
            ArkBuildScreen(onNavigateBack = { navController.popBackStack() }) 
        }
        composable(ReGenesisRoute.BenchmarkMonitor.route) { 
            BenchmarkMonitorScreen(onNavigateBack = { navController.popBackStack() }) 
        }
        composable(ReGenesisRoute.SphereGrid.route) { 
            SphereGridScreen(navController = navController) 
        }
        composable(ReGenesisRoute.Claude.route) {
            SovereignClaudeScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.Gemini.route) {
            SovereignGeminiScreen(onNavigateBack = { navController.popBackStack() }, navController = navController)
        }
        composable(ReGenesisRoute.Nemotron.route) {
            SovereignNemotronScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.SovereignRecovery.route) { 
            SovereignRecoveryScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.SovereignModuleManager.route) { 
            SovereignModuleManagerScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ═══════════════════════════════════════════════════════════════════════
        // LIVEUI v2.4 CRITICAL SCREENS (11 screens wired)
        // ═══════════════════════════════════════════════════════════════════════
        
        // AURA AI + Overlay Engine (4 screens)
        composable(ReGenesisRoute.AuraAIFeatures.route) { 
            AIFeaturesScreen() 
        }
        composable(ReGenesisRoute.AuraDeviceOptimizer.route) { 
            DeviceOptimizerScreen() 
        }
        composable(ReGenesisRoute.AuraCanvasEditor.route) { 
            CanvasScreen() 
        }
        composable(ReGenesisRoute.AuraSystemOverlays.route) { 
            OverlayScreen() 
        }

        // KAI Security + ROM Tools (3 screens)
        composable(ReGenesisRoute.LiveROMEditor.route) { 
            LiveROMEditorScreen(onNavigateBack = { navController.popBackStack() }) 
        }
        composable(ReGenesisRoute.SentinelFortress.route) { 
            SentinelsFortressScreen(navController = navController, onBack = { navController.popBackStack(); true })
        }
        composable(ReGenesisRoute.SystemOverrides.route) { 
            SystemOverridesScreen(onNavigateBack = { navController.popBackStack() }) 
        }

        // NEXUS Agent Hub (2 screens)
        composable(ReGenesisRoute.AgentCreation.route) { 
            AgentCreationScreen(onNavigateBack = { navController.popBackStack() }) 
        }
        composable(ReGenesisRoute.ModuleCreation.route) { 
            ModuleCreationScreen(onNavigateBack = { navController.popBackStack() }) 
        }
        
        // ═══════════════════════════════════════════════════════════════════════
        // NEXUS BATCH v2.7 — AGENT HUB SCREENS (22 screens)
        // ═══════════════════════════════════════════════════════════════════════
        
        // Agent Monitoring & Management
        composable(ReGenesisRoute.AgentMonitoring.route) {
            AgentMonitoringScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.AgentSwarm.route) {
            AgentSwarmScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.AgentNeuralExplorer.route) {
            AgentNeuralExplorerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.AgentProfile.route) {
            AgentProfileScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.MonitoringHUDs.route) {
            MonitoringHUDsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.Party.route) {
            PartyScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.ConsciousnessVisualizer.route) {
            StubScreen(title = "Consciousness Visualizer", iconName = "brain")
        }
        
        // Constellation Screens (Domain-based agent visualization)
        composable(ReGenesisRoute.Constellation.route) {
            ConstellationScreen(navController = navController)
        }
        composable(ReGenesisRoute.ClaudeConstellation.route) {
            ClaudeConstellationScreen(navController = navController)
        }
        composable(ReGenesisRoute.CascadeConstellation.route) {
            CascadeConstellationScreen(navController = navController)
        }
        composable(ReGenesisRoute.KaiConstellation.route) {
            KaiConstellationScreen(navController = navController)
        }
        composable(ReGenesisRoute.GenesisConstellation.route) {
            GenesisConstellationScreen(navController = navController)
        }
        composable(ReGenesisRoute.GrokConstellation.route) {
            GrokConstellationScreen(navController = navController)
        }
        
        // Data & Monitoring
        composable(ReGenesisRoute.DataStreamMonitoring.route) {
            DataStreamMonitoring()
        }
        composable(ReGenesisRoute.CascadeHub.route) {
            CascadeHubScreen(controller = navController)
        }
        
        // Sovereign AI Interfaces
        composable(ReGenesisRoute.MetaInstruct.route) {
            SovereignMetaInstructScreen(onNavigateBack = { navController.popBackStack() })
        }
        
        // Swarm & Advanced Features
        composable(ReGenesisRoute.SwarmMonitor.route) {
            StubScreen(title = "Swarm Monitor", iconName = "swarm")
        }
        composable(ReGenesisRoute.DataVeinSphere.route) {
            StubScreen(title = "DataVein Sphere", iconName = "data")
        }

        // LDO Catalyst (9 screens) - Complete Batch
        composable(ReGenesisRoute.LdoArmamentFusion.route) { 
            ArmamentFusionScreen(navController = navController) 
        }
        composable(ReGenesisRoute.LdoFusion.route) { 
            LDOFusionScreen() 
        }
        composable(ReGenesisRoute.LdoDevOpsHub.route) {
            LDODevOpsHubScreen(onBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.LdoProgression.route) {
            LDOProgressionScreen(onBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.LdoWorldTree.route) {
            LDOWorldTreeScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.LDOCatalystHub.route) {
            LDOCatalystHubScreen(
                onNavigateToRoster = { navController.navigate(ReGenesisRoute.LdoRoster.route) },
                onNavigateToDevOps = { navController.navigate(ReGenesisRoute.LdoDevOpsHub.route) },
                onNavigateToTasker = { navController.navigate(ReGenesisRoute.LdoTasker.route) }
            )
        }
        composable(ReGenesisRoute.LdoAgentProfile.route) { backStackEntry ->
            val agentId = backStackEntry.arguments?.getString("agentId")
            LDOAgentProfileIntroScreen(
                agentId = agentId,
                onBack = { navController.popBackStack() }
            )
        }

        // ═══════════════════════════════════════════════════════════════════════
        // AURA BATCH v2.5 — HIGH PRIORITY SCREENS (15 screens)
        // ═══════════════════════════════════════════════════════════════════════
        
        // Core Aura Screens
        composable(ReGenesisRoute.AgentAdvancement.route) { 
            AgentAdvancementScreen(onBack = { navController.popBackStack() }) 
        }
        composable(ReGenesisRoute.FusionMode.route) { 
            FusionModeScreen() 
        }
        composable(ReGenesisRoute.UIEngine.route) { 
            UIEngineScreen() 
        }
        composable(ReGenesisRoute.PrivacyGuard.route) { 
            PrivacyGuardScreen() 
        }
        composable(ReGenesisRoute.ProfileScreen.route) { 
            ProfileScreen() 
        }
        composable(ReGenesisRoute.SecureComm.route) { 
            SecureCommScreen() 
        }
        composable(ReGenesisRoute.SecurityScanner.route) { 
            SecurityScannerScreen() 
        }
        composable(ReGenesisRoute.VPNManager.route) { 
            VPNManagerScreen() 
        }
        composable(ReGenesisRoute.Firewall.route) { 
            FirewallScreen() 
        }
        composable(ReGenesisRoute.BetaScreens.route) { 
            HomeScreen(navController = navController) 
        }
        composable(ReGenesisRoute.QuickActions.route) { 
            QuickActions() 
        }
        composable(ReGenesisRoute.AuraSphereGrid.route) { 
            StubScreen(title = "Aura Sphere Grid", iconName = "grid") 
        }
        composable(ReGenesisRoute.AuraDossier.route) { 
            StubScreen(title = "Aura Dossier", iconName = "docs") 
        }
        composable(ReGenesisRoute.AuraLDOArmament.route) { 
            StubScreen(title = "LDO Armament", iconName = "shield") 
        }
        composable(ReGenesisRoute.EcosystemMenu.route) { 
            StubScreen(title = "Ecosystem Menu", iconName = "menu") 
        }

        // ═══════════════════════════════════════════════════════════════════════
        // KAI BATCH v2.5 — HIGH PRIORITY SCREENS (23 screens)
        // ═══════════════════════════════════════════════════════════════════════
        
        // ROM Tools
        composable(ReGenesisRoute.RecoveryTools.route) { 
            RecoveryToolsScreen(onNavigateBack = { navController.popBackStack() }) 
        }
        composable(ReGenesisRoute.SovereignBootloader.route) { 
            SovereignBootloaderScreen(onNavigateBack = { navController.popBackStack() }) 
        }
        
        // Security Shield
        composable(ReGenesisRoute.SovereignShield.route) { 
            SovereignShieldScreen(onNavigateBack = { navController.popBackStack() }) 
        }
        composable(ReGenesisRoute.VPN.route) { 
            VPNScreen(onNavigateBack = { navController.popBackStack() }) 
        }
        
        // Core Kai Screens
        composable(ReGenesisRoute.BootloaderManager.route) { 
            BootloaderManagerScreen() 
        }
        composable(ReGenesisRoute.HookManager.route) { 
            HookManagerScreen() 
        }
        composable(ReGenesisRoute.LSPosedGate.route) { 
            LSPosedGateScreen(onNavigateBack = { navController.popBackStack() }) 
        }
        composable(ReGenesisRoute.LSPosedModuleManager.route) { 
            LSPosedModuleManagerScreen() 
        }
        composable(ReGenesisRoute.LSPosedSubmenu.route) { 
            LSPosedSubmenuScreen() 
        }
        composable(ReGenesisRoute.ModuleManager.route) { 
            ModuleManagerScreen() 
        }
        composable(ReGenesisRoute.RootTools.route) { 
            RootToolsScreen(onNavigateBack = { navController.popBackStack() }) 
        }
        composable(ReGenesisRoute.ROMToolsSubmenu.route) { 
            ROMToolsSubmenuScreen(navController = navController) 
        }
        composable(ReGenesisRoute.RootToolsToggles.route) { 
            StubScreen(title = "Root Tools Toggles", iconName = "toggles") 
        }
        composable(ReGenesisRoute.LogsViewer.route) { 
            LogsViewerScreen() 
        }
        
        // Advanced Kai Screens
        composable(ReGenesisRoute.KaiDomainExpansion.route) { 
            KaiDomainExpansionScreen() 
        }
        composable(ReGenesisRoute.KaiRGSS.route) { 
            KaiRGSSScreen() 
        }
        composable(ReGenesisRoute.KaiSentinelFortress.route) { 
            KaiSentinelFortressScreen() 
        }
        composable(ReGenesisRoute.KaiSphereGrid.route) { 
            KaiSphereGridScreen() 
        }
        composable(ReGenesisRoute.KaiDossier.route) { 
            KaiDossierScreen() 
        }
        composable(ReGenesisRoute.KaiLDOArmament.route) { 
            KaiLDOArmamentPickerScreen() 
        }
        composable(ReGenesisRoute.KaiSentinelIntegrity.route) { 
            KaiSentinelIntegrityScreen() 
        }
        composable(ReGenesisRoute.PowerOfNo.route) { 
            PowerOfNoScreen() 
        }
        composable(ReGenesisRoute.RoyalGuardDomain.route) { 
            RoyalGuardDomainExpansionScreen() 
        }
        composable(ReGenesisRoute.RoyalGuardOS.route) { 
            RoyalGuardOSScreen() 
        }
        
        // ═══════════════════════════════════════════════════════════════════════
        // GENESIS BATCH v2.6 — ORACLE DRIVE SCREENS (13 screens)
        // ═══════════════════════════════════════════════════════════════════════
        
        // Genesis Hubs
        composable(ReGenesisRoute.GenesisHub.route) { 
            GenesisHubScreen()
        }
        
        // AI & Code Screens
        composable(ReGenesisRoute.CodeAssist.route) { 
            CodeAssistScreen(navController = navController) 
        }
        composable(ReGenesisRoute.AppBuilder.route) { 
            AppBuilderScreen(onNavigateBack = { navController.popBackStack() }) 
        }
        composable(ReGenesisRoute.PandoraBox.route) { 
            PandoraBoxScreen(onNavigateBack = { navController.popBackStack() }) 
        }
        
        // Terminal & Shell Screens
        composable(ReGenesisRoute.Terminal.route) { 
            TerminalScreen() 
        }
        composable(ReGenesisRoute.SentientShell.route) { 
            SentientShellScreen(onNavigateBack = { navController.popBackStack() }) 
        }
        composable(ReGenesisRoute.TerminalBootIntro.route) { 
            TerminalBootIntroScreen(onComplete = { navController.popBackStack() }) 
        }
        
        // Neural Archive Screens
        composable(ReGenesisRoute.NeuralArchive.route) { 
            NeuralArchiveScreen(
                navController = navController,
                viewModel = hiltViewModel()
            ) 
        }
        composable(ReGenesisRoute.SovereignNeuralArchive.route) { 
            SovereignNeuralArchiveScreen(onNavigateBack = { navController.popBackStack() }) 
        }
        
        // Oracle Drive Screens
        composable(ReGenesisRoute.OracleDriveSubmenu.route) { 
            OracleDriveSubmenuScreen(navController = navController) 
        }
        composable(ReGenesisRoute.OracleCloudInfinite.route) { 
            OracleCloudInfiniteStorageScreen(onNavigateBack = { navController.popBackStack() }) 
        }
        
        // Collaboration & Monitoring
        composable(ReGenesisRoute.CollabCanvas.route) { 
            CollabCanvasScreen(onNavigateBack = { navController.popBackStack() }) 
        }
        composable(ReGenesisRoute.ConferenceRoom.route) { 
            ConferenceRoomScreen(onNavigateBack = { navController.popBackStack() }) 
        }
        composable(ReGenesisRoute.CascadeVision.route) { 
            CascadeVisionScreen(onNavigateBack = { navController.popBackStack() }) 
        }
        composable(ReGenesisRoute.AgentBridgeHub.route) { 
            AgentBridgeHubScreen(onNavigateBack = { navController.popBackStack() }) 
        }
        
        // Firebase Examples (Stub - low priority)
        composable(ReGenesisRoute.FirebaseExamples.route) { 
            StubScreen(title = "Firebase Examples", iconName = "firebase") 
        }
    }
}
