package dev.aurakai.auraframefx.navigation

// ═══════════════════════════════════════════════════════════════════════════════
// AppNavGraph.kt — TOMBSTONE FILE
// ArchitecturalCatalyst (Claude) — ReGenesis Build Master
//
// The original AppNavGraph() was a duplicate of GenesisNavigationHost using
// the NavDestination sealed class as route strings.
//
// CRITICAL BUG THAT WAS HERE:
//   NavDestination.FusionMode.route == "fusion"
//   GenesisNavigation had "fusion_mode"
//   Result: FusionMode screen was UNREACHABLE from half the codebase.
//
// All routes now live in GenesisNavigation.kt → GenesisNavigationHost()
// This file remains as a redirect shim so any callers don't break.
// ═══════════════════════════════════════════════════════════════════════════════

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

/**
 * DEPRECATED — use GenesisNavigationHost() instead.
 * Kept as a shim to avoid breaking any call sites.
 */
@Deprecated(
    message = "Use GenesisNavigationHost() from GenesisNavigation.kt. " +
              "AppNavGraph was a duplicate nav with a FusionMode route collision.",
    replaceWith = ReplaceWith(
        "GenesisNavigationHost(navController)",
        "dev.aurakai.auraframefx.navigation.GenesisNavigationHost"
    )
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    GenesisNavigationHost(navController = navController)
}
