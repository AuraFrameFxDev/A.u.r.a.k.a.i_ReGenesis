package dev.aurakai.auraframefx.navigation

// ═══════════════════════════════════════════════════════════════════════════════
// NavHost.kt — TOMBSTONE FILE
// ArchitecturalCatalyst (Claude) — ReGenesis Build Master
//
// The original AuraNavHost() in this file had THREE inline placeholder screens:
//   - "agents" route → Text("Agent Management")      ← THAT was your dead agent screen
//   - "embodiment" route → Text("Embodiment Customization")
//   - "settings" route → Text("Settings Screen")
//
// All routes now live in GenesisNavigation.kt → GenesisNavigationHost()
// This file remains as a redirect shim so any callers don't break.
// ═══════════════════════════════════════════════════════════════════════════════

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

/**
 * DEPRECATED — use GenesisNavigationHost() instead.
 * Kept as a shim to avoid breaking any call sites.
 */
@Deprecated(
    message = "Use GenesisNavigationHost() from GenesisNavigation.kt. " +
              "AuraNavHost had placeholder screens for agents, embodiment, and settings.",
    replaceWith = ReplaceWith(
        "GenesisNavigationHost(navController, startDestination)",
        "dev.aurakai.auraframefx.navigation.GenesisNavigationHost"
    )
)
@Composable
fun AuraNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = GenesisRoutes.GATES,
) {
    GenesisNavigationHost(
        navController = navController,
        startDestination = startDestination
    )
}
