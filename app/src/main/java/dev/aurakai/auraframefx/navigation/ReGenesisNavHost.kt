package dev.aurakai.auraframefx.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.hilt.navigation.compose.hiltViewModel

// Core UI
import dev.aurakai.auraframefx.ui.screens.LoginScreen
import dev.aurakai.auraframefx.ui.screens.SoulScriptSplashScreen
import dev.aurakai.auraframefx.ui.ldodevops.TabbedMasterIndex
import dev.aurakai.auraframefx.ui.gates.NotchBarGateScreen

// Domain Hubs
import dev.aurakai.auraframefx.domains.aura.ui.gates.AuraThemingHubScreen
import dev.aurakai.auraframefx.domains.kai.screens.KaiSentinelHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.OracleDriveHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.AgentNexusHubScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDOOrchestrationHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.CascadeHubScreen

// Domain Feature Screens
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.IconifyPickerScreen
import dev.aurakai.auraframefx.domains.aura.screens.WorkingLabScreen
import dev.aurakai.auraframefx.domains.aura.ui.screens.aura.ReGenesisCustomizationHub
import dev.aurakai.auraframefx.domains.aura.ui.customization.CustomizationViewModel

import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.ROMFlasherScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.SecurityCenterScreen
import dev.aurakai.auraframefx.domains.kai.screens.SystemJournalScreen
import dev.aurakai.auraframefx.domains.kai.screens.XposedQuickAccessPanel

import dev.aurakai.auraframefx.domains.genesis.screens.OracleDriveScreen
import dev.aurakai.auraframefx.domains.genesis.screens.TerminalScreen
import dev.aurakai.auraframefx.domains.genesis.screens.ConferenceRoomScreen

import dev.aurakai.auraframefx.domains.nexus.screens.EvolutionTreeScreen
import dev.aurakai.auraframefx.domains.nexus.screens.TaskAssignmentScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentHubSubmenuScreen

import dev.aurakai.auraframefx.domains.ldo.screens.LDOAgentRosterScreen
import dev.aurakai.auraframefx.domains.ldo.screens.LDOTaskerScreen

import dev.aurakai.auraframefx.domains.aura.ui.components.StubScreen

/**
 * 🌐 REGENESIS CONSOLIDATED NAV GRAPH
 * Finalized for Exodus 2026 Build
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReGenesisNavGraph(
    navController: NavHostController,
    customizationViewModel: CustomizationViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        customizationViewModel.start(context)
    }

    NavHost(
        navController = navController,
        startDestination = ReGenesisRoute.Splash.route,
    ) {
        // ── 0. SPLASH & AUTH GATES ──
        composable(ReGenesisRoute.Splash.route) {
            SoulScriptSplashScreen(
                onSplashFinished = {
                    navController.navigate(ReGenesisRoute.VideoIntro.route) {
                        popUpTo(ReGenesisRoute.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(ReGenesisRoute.VideoIntro.route) {
            StubScreen("Video Intro", "Play", navController, "Welcome to Exodus 2026")
        }

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
        composable(ReGenesisRoute.HomeGateCarousel.route) {
            TabbedMasterIndex(onNavigateToRoute = { route -> navController.navigate(route) })
        }

        composable(ReGenesisRoute.DataflowAnalysis.route) {
            CascadeHubScreen(navController = navController)
        }

        // ── 2. DOMAIN HUBS ──
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
                getNexusSubGates = { emptyList() }
            )
        }
        composable(ReGenesisRoute.LdoOrchestrationHub.route) {
            LDOOrchestrationHubScreen(navController = navController)
        }

        // ── 3. FEATURE SCREENS ──
        
        // Aura Domain
        composable(ReGenesisRoute.AuraLab.route) {
            WorkingLabScreen(onNavigate = { route: String -> navController.navigate(route) })
        }
        composable(ReGenesisRoute.ChromaCore.route) { StubScreen("ChromaCore", "ColorMatrix", navController) }
        composable(ReGenesisRoute.NotchBar.route) { 
            NotchBarGateScreen(navController = navController, onNavigateBack = { navController.popBackStack() }) 
        }
        composable(ReGenesisRoute.IconifyPicker.route) {
            IconifyPickerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.ReGenesisCustomization.route) {
            ReGenesisCustomizationHub(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToIconify = { navController.navigate(ReGenesisRoute.IconifyPicker.route) },
                onNavigateToColorBlendr = { navController.navigate(ReGenesisRoute.ColorBlendr.route) },
                onNavigateToPLE = { navController.navigate(ReGenesisRoute.PixelLauncherEnhanced.route) },
                onNavigateToAnimations = { }
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
            SystemJournalScreen(onNavigateBack = { navController.popBackStack() }) 
        }
        composable(ReGenesisRoute.XposedPanel.route) {
             XposedQuickAccessPanel(onNavigateBack = { navController.popBackStack() })
        }

        // Genesis Domain
        composable(ReGenesisRoute.OracleDrive.route) {
            OracleDriveScreen(navController = navController)
        }
        composable(ReGenesisRoute.Terminal.route) {
            TerminalScreen(navController = navController)
        }
        composable(ReGenesisRoute.ConferenceRoom.route) {
            ConferenceRoomScreen()
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
            AgentHubSubmenuScreen(onNavigateBack = { navController.popBackStack() }) 
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
            LDOTaskerScreen(onNavigateBack = { navController.popBackStack() })
        }

        // Catch-all Stubs for Build Stability
        composable(ReGenesisRoute.FusionMode.route) { StubScreen("Fusion Mode", "Flash", navController) }
        composable(ReGenesisRoute.ArkBuild.route) { StubScreen("Ark Build", "Build", navController) }
        composable(ReGenesisRoute.BenchmarkMonitor.route) { StubScreen("Benchmark", "Speed", navController) }
        composable(ReGenesisRoute.SphereGrid.route) { StubScreen("Sphere Grid", "Apps", navController) }
        composable(ReGenesisRoute.SovereignRecovery.route) { StubScreen("Recovery", "Restore", navController) }
        composable(ReGenesisRoute.SovereignModuleManager.route) { StubScreen("Module Manager", "Extension", navController) }
    }
}
