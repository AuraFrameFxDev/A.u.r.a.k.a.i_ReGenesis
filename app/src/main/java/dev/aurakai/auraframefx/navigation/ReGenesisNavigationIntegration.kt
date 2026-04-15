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
import dev.aurakai.auraframefx.domains.aura.ui.screens.aura.*

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
        val category = backStackEntry.arguments?.getString("category") ?: "Icon Packs"
        IconifyCategoryDetailScreen(
            categoryName = category,
            onNavigateBack = { navController.popBackStack() },
            onNavigateToPicker = { styleCategory ->
                // Nested style picker if needed
            }
        )
    }

    // ========================================
    // COLORBLENDR ROUTES
    // ========================================

    composable(ReGenesisRoute.ColorBlendr.route) {
        ColorBlendrScreen(
            onNavigateBack = onNavigateBack
        )
    }

    // ========================================
    // PIXEL LAUNCHER ENHANCED ROUTES
    // ========================================

    composable(ReGenesisRoute.PixelLauncherEnhanced.route) {
        PixelLauncherEnhancedScreen(
            onNavigateBack = onNavigateBack
        )
    }
}
