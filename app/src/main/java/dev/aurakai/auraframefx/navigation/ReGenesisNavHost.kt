package dev.aurakai.auraframefx.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dev.aurakai.auraframefx.domains.aura.ui.customization.CustomizationViewModel
import dev.aurakai.auraframefx.domains.aura.ui.screens.aura.ReGenesisCustomizationHub
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.dashboard.MainScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.*
import dev.aurakai.auraframefx.domains.genesis.oracledrive.orchestration.*
import dev.aurakai.auraframefx.domains.genesis.oracledrive.memory.*
import dev.aurakai.auraframefx.domains.genesis.oracledrive.fusion.*
import dev.aurakai.auraframefx.domains.genesis.viewmodels.TerminalViewModel
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.aura.ui.screens.TerminalScreen
import dev.aurakai.auraframefx.domains.ldo.ui.screens.*
import dev.aurakai.auraframefx.domains.aura.ui.screens.aura.*
import dev.aurakai.auraframefx.domains.aura.ui.screens.AuraSphereGridScreen
import dev.aurakai.auraframefx.domains.aura.screens.CanvasScreen
import dev.aurakai.auraframefx.domains.kai.screens.ROMFlasherScreen
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.security.SecurityCenterScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReGenesisNavGraph(
    navController: NavHostController,
    customizationViewModel: CustomizationViewModel = hiltViewModel()
) {
    NavHost(
        navController = navController,
        startDestination = ReGenesisRoute.HomeGateCarousel.route
    ) {
        // ── 1. MAIN GATES ──
        composable(ReGenesisRoute.HomeGateCarousel.route) {
            MainScreen(navController = navController)
        }

        // ── 2. LEVEL 2 HUB SCREENS ──
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

        composable(ReGenesisRoute.DataflowAnalysis.route) {
            CascadeHubScreen(navController = navController)
        }

        // ── 3. LEVEL 3 FEATURE SCREENS ──

        // AURA DOMAIN
        composable(ReGenesisRoute.AuraLab.route) {
            // AurasLabScreen pending implementation or check existing
            Box(modifier = Modifier.fillMaxSize()) { Text("Aura's Lab Placeholder", color = Color.White) }
        }

        composable(ReGenesisRoute.ColorBlendr.route) {
            ColorBlendrScreen(onNavigateBack = { navController.popBackStack() })
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
            IconifyCategoryDetailScreen(
                categoryName = category,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPicker = { /* TODO */ }
            )
        }

        composable(ReGenesisRoute.PixelLauncherEnhanced.route) {
            PixelLauncherEnhancedScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.CollabCanvas.route) {
            CanvasScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(ReGenesisRoute.SphereGrid.route) {
            AuraSphereGridScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.ReGenesisCustomization.route) {
            ReGenesisCustomizationHub(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToIconify = { navController.navigate(ReGenesisRoute.IconifyPicker.route) },
                onNavigateToColorBlendr = { navController.navigate(ReGenesisRoute.ColorBlendr.route) },
                onNavigateToPLE = { navController.navigate(ReGenesisRoute.PixelLauncherEnhanced.route) },
                onNavigateToAnimations = { /* TODO */ }
            )
        }

        // KAI DOMAIN
        composable(ReGenesisRoute.ROMFlasher.route) {
            ROMFlasherScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.RootTools.route) {
            // RootToolsTogglesScreen pending check
            Box(modifier = Modifier.fillMaxSize()) { Text("Root Tools Placeholder", color = Color.White) }
        }

        composable(ReGenesisRoute.SovereignShield.route) {
            SecurityCenterScreen(onNavigateBack = { navController.popBackStack() })
        }

        // GENESIS DOMAIN
        composable(ReGenesisRoute.OracleDriveSubmenu.route) {
            OracleDriveSubmenuScreen()
        }

        composable(ReGenesisRoute.CodeAssist.route) {
            CodeAssistScreen()
        }

        composable(ReGenesisRoute.Terminal.route) {
            val terminalViewModel: TerminalViewModel = hiltViewModel()
            TerminalScreen(
                navController = navController,
                cascadeService = terminalViewModel.cascadeService,
                casberrySwarm = terminalViewModel.casberrySwarm,
                auraDifyBridge = terminalViewModel.auraDifyBridge
            )
        }

        composable(ReGenesisRoute.ConferenceRoom.route) {
            ConferenceRoomScreen()
        }

        composable(ReGenesisRoute.SentientShell.route) {
            SentientShellScreen()
        }

        composable(ReGenesisRoute.OracleCloudStorage.route) {
            OracleCloudInfiniteStorageScreen()
        }

        composable(ReGenesisRoute.FusionMode.route) {
            NexusFusionScreen()
        }

        composable(ReGenesisRoute.AgentNexusHub.route) {
            AgentNexusHubScreen(
                navController = navController
            )
        }

        composable(ReGenesisRoute.DataflowAnalysis.route) {
            CascadeHubScreen(navController = navController)
        }

        // ── 3. LDO CATALYST DEVELOPMENT ──
        composable(ReGenesisRoute.LdoOrchestrationHub.route) {
            LDOOrchestrationHubScreen(navController = navController)
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

        composable(ReGenesisRoute.LdoCatalystDevelopment.route) {
            LdoCatalystDevelopmentScreen(navController = navController)
        }

        composable(ReGenesisRoute.LdoTasker.route) {
            LDOTaskerScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.MultiAgentTask.route) {
            MultiAgentTaskScreen(navController = navController)
        }

        composable(ReGenesisRoute.LdoFusion.route) {
            LDOFusionScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.LdoBonding.route) {
            LDOBondingScreen(onBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.LdoWorldTree.route) {
            LDOWorldTreeScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.LdoRoster.route) {
            LDOAgentRosterScreen(
                onAgentTap = { agent ->
                    navController.navigate(ReGenesisRoute.LdoAgentProfile.createRoute(agent.id))
                }
            )
        }

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
    }
}

