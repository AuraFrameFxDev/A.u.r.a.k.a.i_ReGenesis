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
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.ColorBlendrScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.IconifyCategoryDetailScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.IconifyPickerScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.PixelLauncherEnhancedScreen

// ============================================================================
// NAVIGATION DESTINATIONS FOR CUSTOMIZATION
// ============================================================================

/**
 * AURA Domain - Customization Navigation Routes
 */
sealed class AuraCustomizationRoute(val route: String) {
    // Main Iconify
    object IconifyPicker : AuraCustomizationRoute("aura/iconify")
    object IconifyCategory : AuraCustomizationRoute("aura/iconify/{category}") {
        fun createRoute(category: String) = "aura/iconify/$category"
    }

    // Iconify Sub-categories
    object IconPacksScreen : AuraCustomizationRoute("aura/iconify/icon_packs")
    object BatteryStylesScreen : AuraCustomizationRoute("aura/iconify/battery_styles")
    object BrightnessBarsScreen : AuraCustomizationRoute("aura/iconify/brightness_bars")
    object QSPanelScreen : AuraCustomizationRoute("aura/iconify/qs_panel")
    object NotificationsScreen : AuraCustomizationRoute("aura/iconify/notifications")
    object VolumePanelScreen : AuraCustomizationRoute("aura/iconify/volume_panel")
    object NavigationBarScreen : AuraCustomizationRoute("aura/iconify/navigation_bar")
    object UIRoundnessScreen : AuraCustomizationRoute("aura/iconify/ui_roundness")
    object IconShapeScreen : AuraCustomizationRoute("aura/iconify/icon_shape")
    object StatusBarScreen : AuraCustomizationRoute("aura/iconify/status_bar")
    object XposedFeaturesScreen : AuraCustomizationRoute("aura/iconify/xposed_features")
    object ColorEngineScreen : AuraCustomizationRoute("aura/iconify/color_engine")

    // ColorBlendr
    object ColorBlendr : AuraCustomizationRoute("aura/colorblendr")
    object ColorBlendrMonet : AuraCustomizationRoute("aura/colorblendr/monet")
    object ColorBlendrPalette : AuraCustomizationRoute("aura/colorblendr/palette")
    object ColorBlendrPerApp : AuraCustomizationRoute("aura/colorblendr/per_app")

    // PixelLauncherEnhanced
    object PixelLauncherEnhanced : AuraCustomizationRoute("aura/pixel_launcher_enhanced")
    object PLEIcons : AuraCustomizationRoute("aura/ple/icons")
    object PLEHomeScreen : AuraCustomizationRoute("aura/ple/home_screen")
    object PLEAppDrawer : AuraCustomizationRoute("aura/ple/app_drawer")
    object PLERecents : AuraCustomizationRoute("aura/ple/recents")
}

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

    composable(AuraCustomizationRoute.IconifyPicker.route) {
        IconifyPickerScreen(
            onNavigateBack = onNavigateBack,
            onNavigateToCategory = { category ->
                navController.navigate(AuraCustomizationRoute.IconifyCategory.createRoute(category))
            }
        )
    }

    composable(
        route = AuraCustomizationRoute.IconifyCategory.route
    ) { backStackEntry ->
        val category = backStackEntry.arguments?.getString("category") ?: "Icon Packs"
        IconifyCategoryDetailScreen(
            categoryName = category,
            onNavigateBack = { navController.popBackStack() }
        )
    }

    // ========================================
    // COLORBLENDR ROUTES
    // ========================================

    composable(AuraCustomizationRoute.ColorBlendr.route) {
        ColorBlendrScreen(
            onNavigateBack = onNavigateBack
        )
    }

    // ========================================
    // PIXEL LAUNCHER ENHANCED ROUTES
    // ========================================

    composable(AuraCustomizationRoute.PixelLauncherEnhanced.route) {
        PixelLauncherEnhancedScreen(
            onNavigateBack = onNavigateBack
        )
    }
}
