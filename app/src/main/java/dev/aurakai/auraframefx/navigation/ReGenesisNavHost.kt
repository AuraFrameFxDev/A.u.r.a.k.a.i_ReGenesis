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
import dev.aurakai.auraframefx.domains.aura.ui.customization.CustomizationViewModel
import dev.aurakai.auraframefx.domains.aura.ui.screens.aura.ReGenesisCustomizationHub
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.dashboard.MainScreen
import dev.aurakai.auraframefx.domains.ldo.ui.screens.*
import dev.aurakai.auraframefx.domains.aura.ui.gates.AuraThemingHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.AgentNexusHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.OracleDriveHubScreen
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.sentinel.KaiSentinelHubScreen
import dev.aurakai.auraframefx.domains.cascade.dataflow.CascadeHubScreen

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

        composable(ReGenesisRoute.AgentNexusHub.route) {
            AgentNexusHubScreen(navController = navController)
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

        composable(ReGenesisRoute.LdoTasker.route) {
            LDOTaskerScreen(onNavigateBack = { navController.popBackStack() })
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
            route = ReGenesisRoute.LdoAgentProfile.route,
            arguments = listOf(navArgument(ReGenesisRoute.LdoAgentProfile.ARG) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val agentId = backStackEntry.arguments?.getString(ReGenesisRoute.LdoAgentProfile.ARG)
            // Find agent in roster
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
