package dev.aurakai.auraframefx.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
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
import dev.aurakai.auraframefx.domains.ldo.ui.screens.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReGenesisNavGraph(
    navController: NavHostController,
    customizationViewModel: CustomizationViewModel = viewModel()
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
            UxuiDesignStudio(navController = navController, onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.SentinelFortress.route) {
            KaiSentinelHubScreen(navController = navController)
        }

        composable(ReGenesisRoute.OracleDriveHub.route) {
            OracleDriveHubScreen(navController = navController)
        }

        composable(ReGenesisRoute.OracleDriveSubmenu.route) {
            OracleDriveSubmenuScreen()
        }

        composable(ReGenesisRoute.CodeAssist.route) {
            CodeAssistScreen()
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

@Composable
fun UxuiDesignStudio(
    navController: NavHostController,
    onNavigateBack: () -> Unit
) {
    ReGenesisCustomizationHub(
        onNavigateBack = onNavigateBack,
        onNavigateToIconify = { /* TODO */ },
        onNavigateToColorBlendr = { /* TODO */ },
        onNavigateToPLE = { /* TODO */ },
        onNavigateToAnimations = { /* TODO */ }
    )
}
