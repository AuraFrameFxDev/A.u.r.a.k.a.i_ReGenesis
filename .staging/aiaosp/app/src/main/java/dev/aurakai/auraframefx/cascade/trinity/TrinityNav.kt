package dev.aurakai.auraframefx.cascade.trinity

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

/**
 * Navigation graph for the Trinity system.
 */
@Composable
fun TrinityNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = TrinityScreens.TrinityHome.route
    ) {
        // Main Trinity Screen
        composable(route = TrinityScreens.TrinityHome.route) {
            TrinityScreen()
        }

        // Add more screens for the Trinity system as needed
    }
}

/**
 * Sealed class representing all screens in the Trinity navigation graph.
 */
sealed class TrinityScreens(val route: String) {
    object TrinityHome : TrinityScreens("trinity_home")

    // Add more screens as needed
    // Example:
    // object AgentDetail : TrinityScreens("agent_detail/{agentId}")
}
