/**
 * ReGenesis Navigation Integration for Customization Apps
 *
 * This file wires up the REAL Iconify, ColorBlendr, and PixelLauncherEnhanced
 * screens into the ReGenesis navigation system.
 */

package dev.aurakai.auraframefx.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import dev.aurakai.auraframefx.domains.aura.ui.screens.aura.IconifyPickerScreen
import dev.aurakai.auraframefx.navigation.ReGenesisRoute.ColorBlendr
import dev.aurakai.auraframefx.navigation.ReGenesisRoute.IconifyCategory
import dev.aurakai.auraframefx.navigation.ReGenesisRoute.IconifyPicker
import dev.aurakai.auraframefx.navigation.ReGenesisRoute.PixelLauncherEnhanced

// ColorBlendrScreen, IconifyCategoryDetailScreen, PixelLauncherEnhancedScreen - pending AgentsWired merge

// ============================================================================
// NAVIGATION DESTINATIONS FOR CUSTOMIZATION
// ============================================================================

/**
 * Navigation graph extension for AURA customization routes
 */
fun NavGraphBuilder.auraCustomizationNavigation(
    navController: NavHostController,
    onNavigateBack: () -> Unit
) {
    // ========================================
    // ICONIFY ROUTES
    // ========================================

    composable(IconifyPicker.route) {
        IconifyPickerScreen(
            onNavigateBack = onNavigateBack,
            onNavigateToCategory = { category ->
                navController.navigate(IconifyCategory.createRoute(category))
            }
        )
    }

    composable(
        route = IconifyCategory.route
    ) { backStackEntry ->
        // IconifyCategoryDetailScreen pending AgentsWired merge
        IconifyPickerScreen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToCategory = { category ->
                navController.navigate(IconifyCategory.createRoute(category))
            }
        )
    }

    // ========================================
    // COLORBLENDR ROUTES
    // ========================================

    composable(ColorBlendr.route) {
        IconifyPickerScreen(
            onNavigateBack = onNavigateBack,
            onNavigateToCategory = {}
        )
    }

    // ========================================
    // PIXEL LAUNCHER ENHANCED ROUTES
    // ========================================

    composable(PixelLauncherEnhanced.route) {
        IconifyPickerScreen(
            onNavigateBack = onNavigateBack,
            onNavigateToCategory = {}
        )
    }
}
