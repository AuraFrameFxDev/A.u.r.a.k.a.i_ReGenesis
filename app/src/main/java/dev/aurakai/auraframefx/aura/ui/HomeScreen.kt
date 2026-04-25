package dev.aurakai.auraframefx.aura.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import dev.aurakai.auraframefx.ui.ldodevops.TabbedMasterIndex

/**
 * 🎨 MAIN SCREEN (AURA DASHBOARD)
 * ReGenesis Version: Tabbed Master Index Wrapper
 */
@Composable
fun MainScreen(
    navController: NavController,
) {
    // We now use the TabbedMasterIndex as the primary navigation substrate.
    TabbedMasterIndex(onNavigateToRoute = { route -> navController.navigate(route) })
}
