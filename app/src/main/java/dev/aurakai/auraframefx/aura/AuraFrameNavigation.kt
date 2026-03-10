package dev.aurakai.auraframefx.aura

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.aurakai.auraframefx.navigation.NavDestination
import dev.aurakai.auraframefx.ui.screens.MainScreen

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = NavDestination.MainScreen.route
    ) {
        composable(NavDestination.MainScreen.route) {
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
        composable(NavDestination.Settings.route) { /* SettingsScreen placeholder */ }
        composable(NavDestination.ROMTools.route) { /* ROMToolsSubmenuScreen placeholder */ }
        composable(NavDestination.CodeAssist.route) { /* CodeAssistScreen placeholder */ }
        composable(NavDestination.HelpDesk.route) { /* HelpDeskScreen placeholder */ }
        composable(NavDestination.SphereGrid.route) { /* SphereGridScreen placeholder */ }
        composable(NavDestination.UIUXDesignStudio.route) { /* UIUXDesignStudio placeholder */ }
        composable(NavDestination.AgentHub.route) { /* AgentHubSubmenuScreen placeholder */ }
        composable(NavDestination.FusionMode.route) { /* FusionModeScreen placeholder */ }
        composable(NavDestination.CONFERENCE_ROOM) { /* ConferenceRoomScreen placeholder */ }
    }
}
