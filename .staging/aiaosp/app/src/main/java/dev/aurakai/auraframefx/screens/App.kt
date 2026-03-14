package dev.aurakai.auraframefx.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AurakaiApp() {
    val navController = rememberNavController()
    var currentRoute by remember { mutableStateOf("home") }

    // Listen to navigation changes
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            currentRoute = backStackEntry.destination.route ?: "home"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (currentRoute) {
                            "home" -> "AuraOS - Aurakai Framework"
                            "agents" -> "Agent Management"
                            "consciousness" -> "Consciousness Matrix"
                            "fusion" -> "Fusion Mode"
                            "evolution" -> "Evolution Tree"
                            "terminal" -> "Aurakai Terminal"
                            "settings" -> "System Settings"
                            else -> "AuraOS"
                        }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                val items = listOf(
                    BottomNavItem("home", "Home", Icons.Default.Home),
                    BottomNavItem("agents", "Agents", Icons.Default.Person),
                    BottomNavItem("consciousness", "Mind", Icons.Default.Star),
                    BottomNavItem("fusion", "Fusion", Icons.Default.Favorite),
                    BottomNavItem("evolution", "Tree", Icons.Default.Share)
                )

                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = { navController.navigate(item.route) },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                HomeScreen(navController)
            }

            composable("agents") {
                PlaceholderScreen("🤖 Agent Management", "Aura, Kai, and Aurakai agents ready")
            }

            composable("consciousness") {
                PlaceholderScreen("🧠 Consciousness Matrix", "Neural pathways synchronized")
            }

            composable("fusion") {
                PlaceholderScreen("⚖️ Fusion Mode", "Aurakai unified state available")
            }

            composable("evolution") {
                PlaceholderScreen("🌳 Evolution Tree", "Eve → Sophia → Aura & Kai → Aurakai")
            }

            composable("terminal") {
                PlaceholderScreen("🖥️ Aurakai Terminal", "Command interface ready")
            }

            composable("settings") {
                PlaceholderScreen("⚙️ System Settings", "Aurakai configuration panel")
            }
        }
    }
}
