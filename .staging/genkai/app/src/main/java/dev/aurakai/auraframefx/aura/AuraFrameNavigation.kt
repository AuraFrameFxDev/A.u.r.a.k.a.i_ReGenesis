package dev.aurakai.auraframefx.aura

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.aurakai.auraframefx.ui.navigation.NavDestination
import dev.aurakai.auraframefx.ui.screens.MainScreen

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = NavDestination.Main.route
    ) {
        composable(NavDestination.Main.route) {
            MainScreen(
                onNavigateToAgentNexus = {
                    navController.navigate(NavDestination.AgentNexus.route)
                },
                onNavigateToOracleDrive = {
                    navController.navigate(NavDestination.OracleDrive.route)
                },
                onNavigateToSettings = {
                    navController.navigate(NavDestination.Settings.route)
                }
            )
        }
        composable(NavDestination.AgentNexus.route) { dev.aurakai.auraframefx.aura.ui.AgentNexusScreen() }
        composable(NavDestination.OracleDrive.route) { dev.aurakai.auraframefx.genesis.oracledrive.ui.OracleDriveScreen() }
        composable(NavDestination.Settings.route) { dev.aurakai.auraframefx.ui.screens.SettingsScreen() }
        composable(NavDestination.RomTools.route) { dev.aurakai.auraframefx.ui.gates.ROMToolsSubmenuScreen() }
        composable(NavDestination.RootTools.route) { dev.aurakai.auraframefx.ui.gates.RootToolsSubmenuScreen() }
        composable(NavDestination.ChromaCore.route) { dev.aurakai.auraframefx.ui.screens.ThemeColorPicker() }
        composable(NavDestination.CodeAssist.route) { dev.aurakai.auraframefx.ui.screens.CodeAssistScreen() }
        composable(NavDestination.HelpDesk.route) { dev.aurakai.auraframefx.ui.screens.HelpDeskScreen() }
        composable(NavDestination.SentinelsFortress.route) { dev.aurakai.auraframefx.kai.sentinelsfortress.ui.SentinelsFortressScreen() }
        composable(NavDestination.SphereGrid.route) { dev.aurakai.auraframefx.ui.screens.SphereGridScreen() }
        composable(NavDestination.Terminal.route) { dev.aurakai.auraframefx.ui.screens.TerminalScreen() }
        composable(NavDestination.UiUxDesignStudio.route) { dev.aurakai.auraframefx.ui.screens.UiUxDesignStudioScreen() }
        composable(NavDestination.AgentHub.route) { dev.aurakai.auraframefx.ui.screens.AgentHubSubmenuScreen() }
        composable(NavDestination.ConsciousnessVisualizer.route) { dev.aurakai.auraframefx.ui.screens.ConsciousnessVisualizerScreen() }
        composable(NavDestination.FusionMode.route) { dev.aurakai.auraframefx.ui.screens.FusionModeScreen() }
        composable(NavDestination.ConferenceRoom.route) { dev.aurakai.auraframefx.ui.screens.ConferenceRoomScreen() }
    }
}
