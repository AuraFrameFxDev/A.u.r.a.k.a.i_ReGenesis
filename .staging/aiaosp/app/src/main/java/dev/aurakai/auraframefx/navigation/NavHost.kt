package dev.aurakai.auraframefx.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.aurakai.auraframefx.aura.ui.IntroScreen
import dev.aurakai.auraframefx.ui.screens.HolographicMenuScreen

/**
 * Defines the navigation graph for the Genesis Protocol application.
 *
 * Navigation Routes:
 * - intro: Initial intro screen with Aura & Kai presentation
 * - home: Main holographic menu interface
 * - settings: Application settings and configuration
 * - agents: AI agent management screen
 * - embodiment: Embodiment and avatar customization
 */
@Composable
fun AuraNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = "intro",
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier.fillMaxSize()
    ) {
        // Intro screen - first-time user experience
        composable("intro") {
            IntroScreen(
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("intro") { inclusive = true }
                    }
                }
            )
        }

        // Home screen - main holographic menu
        composable("home") {
            HolographicMenuScreen(
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }

        // Settings screen
        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // AI Agents management
        composable("agents") {
            AgentManagementScreen(
                onBack = { navController.popBackStack() }
            )
        }

        // Embodiment customization
        composable("embodiment") {
            EmbodimentCustomizationScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

/**
 * Settings screen placeholder
 */
@Composable
private fun SettingsScreen(onBack: () -> Unit) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            androidx.compose.material3.Text("Settings Screen")
            androidx.compose.material3.Button(onClick = onBack) {
                androidx.compose.material3.Text("Back")
            }
        }
    }
}

/**
 * Agent management screen placeholder
 */
@Composable
private fun AgentManagementScreen(onBack: () -> Unit) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            androidx.compose.material3.Text("Agent Management")
            androidx.compose.material3.Button(onClick = onBack) {
                androidx.compose.material3.Text("Back")
            }
        }
    }
}

/**
 * Embodiment customization screen placeholder
 */
@Composable
private fun EmbodimentCustomizationScreen(onBack: () -> Unit) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            androidx.compose.material3.Text("Embodiment Customization")
            androidx.compose.material3.Button(onClick = onBack) {
                androidx.compose.material3.Text("Back")
            }
        }
    }
}
