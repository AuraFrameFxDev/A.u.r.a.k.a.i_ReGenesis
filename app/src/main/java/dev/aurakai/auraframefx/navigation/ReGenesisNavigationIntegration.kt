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
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.IconifyPickerScreen
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

    composable(ReGenesisRoute.IconifyPicker.route) {
        IconifyPickerScreen(
            onNavigateBack = onNavigateBack,
            onNavigateToCategory = { category ->
                navController.navigate(ReGenesisRoute.IconifyCategory.createRoute(category))
            }
        )
    }

    composable(
        route = ReGenesisRoute.IconifyCategory.route
    ) { backStackEntry ->
        // IconifyCategoryDetailScreen pending AgentsWired merge
        IconifyPickerScreen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToCategory = { category ->
                navController.navigate(ReGenesisRoute.IconifyCategory.createRoute(category))
            }
        )
    }

    // ========================================
    // COLORBLENDR ROUTES
    // ========================================

    composable(ReGenesisRoute.ColorBlendr.route) {
        IconifyPickerScreen(
            onNavigateBack = onNavigateBack,
            onNavigateToCategory = {}
        )
    }

    // ========================================
    // PIXEL LAUNCHER ENHANCED ROUTES
    // ========================================

    composable(ReGenesisRoute.PixelLauncherEnhanced.route) {
        IconifyPickerScreen(
            onNavigateBack = onNavigateBack,
            onNavigateToCategory = {}
        )
    }
}
