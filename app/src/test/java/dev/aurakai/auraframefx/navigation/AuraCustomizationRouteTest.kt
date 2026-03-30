package dev.aurakai.auraframefx.navigation

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * Unit tests for [AuraCustomizationRoute] sealed class and [AuraSubGates] object.
 *
 * The class was renamed from ReGenesisRoute to AuraCustomizationRoute in this PR.
 * Tests verify: route string correctness, createRoute() helper, AuraSubGates data,
 * IntegrationStatus constants, and AuraSubGates.getSummary().
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("AuraCustomizationRoute Tests")
class AuraCustomizationRouteTest {

    // =========================================================================
    // Route String Correctness
    // =========================================================================

    @Nested
    @DisplayName("Route Strings")
    inner class RouteStringTests {

        @Test
        @DisplayName("IconifyPicker route should be 'aura/iconify'")
        fun `IconifyPicker route should be aura slash iconify`() {
            assertEquals("aura/iconify", AuraCustomizationRoute.IconifyPicker.route)
        }

        @Test
        @DisplayName("IconifyCategory route should contain category placeholder")
        fun `IconifyCategory route should contain category placeholder`() {
            assertEquals("aura/iconify/{category}", AuraCustomizationRoute.IconifyCategory.route)
        }

        @Test
        @DisplayName("IconPacksScreen route should be 'aura/iconify/icon_packs'")
        fun `IconPacksScreen route should be aura slash iconify slash icon_packs`() {
            assertEquals("aura/iconify/icon_packs", AuraCustomizationRoute.IconPacksScreen.route)
        }

        @Test
        @DisplayName("BatteryStylesScreen route should be 'aura/iconify/battery_styles'")
        fun `BatteryStylesScreen route should be correct`() {
            assertEquals("aura/iconify/battery_styles", AuraCustomizationRoute.BatteryStylesScreen.route)
        }

        @Test
        @DisplayName("BrightnessBarsScreen route should be 'aura/iconify/brightness_bars'")
        fun `BrightnessBarsScreen route should be correct`() {
            assertEquals("aura/iconify/brightness_bars", AuraCustomizationRoute.BrightnessBarsScreen.route)
        }

        @Test
        @DisplayName("QSPanelScreen route should be 'aura/iconify/qs_panel'")
        fun `QSPanelScreen route should be correct`() {
            assertEquals("aura/iconify/qs_panel", AuraCustomizationRoute.QSPanelScreen.route)
        }

        @Test
        @DisplayName("NotificationsScreen route should be 'aura/iconify/notifications'")
        fun `NotificationsScreen route should be correct`() {
            assertEquals("aura/iconify/notifications", AuraCustomizationRoute.NotificationsScreen.route)
        }

        @Test
        @DisplayName("VolumePanelScreen route should be 'aura/iconify/volume_panel'")
        fun `VolumePanelScreen route should be correct`() {
            assertEquals("aura/iconify/volume_panel", AuraCustomizationRoute.VolumePanelScreen.route)
        }

        @Test
        @DisplayName("NavigationBarScreen route should be 'aura/iconify/navigation_bar'")
        fun `NavigationBarScreen route should be correct`() {
            assertEquals("aura/iconify/navigation_bar", AuraCustomizationRoute.NavigationBarScreen.route)
        }

        @Test
        @DisplayName("UIRoundnessScreen route should be 'aura/iconify/ui_roundness'")
        fun `UIRoundnessScreen route should be correct`() {
            assertEquals("aura/iconify/ui_roundness", AuraCustomizationRoute.UIRoundnessScreen.route)
        }

        @Test
        @DisplayName("IconShapeScreen route should be 'aura/iconify/icon_shape'")
        fun `IconShapeScreen route should be correct`() {
            assertEquals("aura/iconify/icon_shape", AuraCustomizationRoute.IconShapeScreen.route)
        }

        @Test
        @DisplayName("StatusBarScreen route should be 'aura/iconify/status_bar'")
        fun `StatusBarScreen route should be correct`() {
            assertEquals("aura/iconify/status_bar", AuraCustomizationRoute.StatusBarScreen.route)
        }

        @Test
        @DisplayName("XposedFeaturesScreen route should be 'aura/iconify/xposed_features'")
        fun `XposedFeaturesScreen route should be correct`() {
            assertEquals("aura/iconify/xposed_features", AuraCustomizationRoute.XposedFeaturesScreen.route)
        }

        @Test
        @DisplayName("ColorEngineScreen route should be 'aura/iconify/color_engine'")
        fun `ColorEngineScreen route should be correct`() {
            assertEquals("aura/iconify/color_engine", AuraCustomizationRoute.ColorEngineScreen.route)
        }

        @Test
        @DisplayName("ColorBlendr route should be 'aura/colorblendr'")
        fun `ColorBlendr route should be correct`() {
            assertEquals("aura/colorblendr", AuraCustomizationRoute.ColorBlendr.route)
        }

        @Test
        @DisplayName("ColorBlendrMonet route should be 'aura/colorblendr/monet'")
        fun `ColorBlendrMonet route should be correct`() {
            assertEquals("aura/colorblendr/monet", AuraCustomizationRoute.ColorBlendrMonet.route)
        }

        @Test
        @DisplayName("ColorBlendrPalette route should be 'aura/colorblendr/palette'")
        fun `ColorBlendrPalette route should be correct`() {
            assertEquals("aura/colorblendr/palette", AuraCustomizationRoute.ColorBlendrPalette.route)
        }

        @Test
        @DisplayName("ColorBlendrPerApp route should be 'aura/colorblendr/per_app'")
        fun `ColorBlendrPerApp route should be correct`() {
            assertEquals("aura/colorblendr/per_app", AuraCustomizationRoute.ColorBlendrPerApp.route)
        }

        @Test
        @DisplayName("PixelLauncherEnhanced route should be 'aura/pixel_launcher_enhanced'")
        fun `PixelLauncherEnhanced route should be correct`() {
            assertEquals("aura/pixel_launcher_enhanced", AuraCustomizationRoute.PixelLauncherEnhanced.route)
        }

        @Test
        @DisplayName("PLEIcons route should be 'aura/ple/icons'")
        fun `PLEIcons route should be correct`() {
            assertEquals("aura/ple/icons", AuraCustomizationRoute.PLEIcons.route)
        }

        @Test
        @DisplayName("PLEHomeScreen route should be 'aura/ple/home_screen'")
        fun `PLEHomeScreen route should be correct`() {
            assertEquals("aura/ple/home_screen", AuraCustomizationRoute.PLEHomeScreen.route)
        }

        @Test
        @DisplayName("PLEAppDrawer route should be 'aura/ple/app_drawer'")
        fun `PLEAppDrawer route should be correct`() {
            assertEquals("aura/ple/app_drawer", AuraCustomizationRoute.PLEAppDrawer.route)
        }

        @Test
        @DisplayName("PLERecents route should be 'aura/ple/recents'")
        fun `PLERecents route should be correct`() {
            assertEquals("aura/ple/recents", AuraCustomizationRoute.PLERecents.route)
        }

        @Test
        @DisplayName("all routes should start with 'aura/'")
        fun `all routes should start with aura slash`() {
            val allRoutes = listOf(
                AuraCustomizationRoute.IconifyPicker,
                AuraCustomizationRoute.IconifyCategory,
                AuraCustomizationRoute.IconPacksScreen,
                AuraCustomizationRoute.BatteryStylesScreen,
                AuraCustomizationRoute.BrightnessBarsScreen,
                AuraCustomizationRoute.QSPanelScreen,
                AuraCustomizationRoute.NotificationsScreen,
                AuraCustomizationRoute.VolumePanelScreen,
                AuraCustomizationRoute.NavigationBarScreen,
                AuraCustomizationRoute.UIRoundnessScreen,
                AuraCustomizationRoute.IconShapeScreen,
                AuraCustomizationRoute.StatusBarScreen,
                AuraCustomizationRoute.XposedFeaturesScreen,
                AuraCustomizationRoute.ColorEngineScreen,
                AuraCustomizationRoute.ColorBlendr,
                AuraCustomizationRoute.ColorBlendrMonet,
                AuraCustomizationRoute.ColorBlendrPalette,
                AuraCustomizationRoute.ColorBlendrPerApp,
                AuraCustomizationRoute.PixelLauncherEnhanced,
                AuraCustomizationRoute.PLEIcons,
                AuraCustomizationRoute.PLEHomeScreen,
                AuraCustomizationRoute.PLEAppDrawer,
                AuraCustomizationRoute.PLERecents
            )
            allRoutes.forEach { route ->
                assertTrue(
                    route.route.startsWith("aura/"),
                    "Route '${route.route}' should start with 'aura/'"
                )
            }
        }

        @Test
        @DisplayName("all routes should be non-empty strings")
        fun `all routes should be non-empty strings`() {
            val allRoutes = listOf(
                AuraCustomizationRoute.IconifyPicker,
                AuraCustomizationRoute.IconifyCategory,
                AuraCustomizationRoute.ColorBlendr,
                AuraCustomizationRoute.PixelLauncherEnhanced,
                AuraCustomizationRoute.PLERecents
            )
            allRoutes.forEach { assertTrue(it.route.isNotEmpty()) }
        }
    }

    // =========================================================================
    // IconifyCategory.createRoute()
    // =========================================================================

    @Nested
    @DisplayName("IconifyCategory.createRoute()")
    inner class CreateRouteTests {

        @Test
        @DisplayName("should create route with given category")
        fun `should create route with given category`() {
            val result = AuraCustomizationRoute.IconifyCategory.createRoute("icon_packs")
            assertEquals("aura/iconify/icon_packs", result)
        }

        @Test
        @DisplayName("should create route with multi-word category")
        fun `should create route with multi-word category`() {
            val result = AuraCustomizationRoute.IconifyCategory.createRoute("battery_styles")
            assertEquals("aura/iconify/battery_styles", result)
        }

        @Test
        @DisplayName("should create route with custom category name")
        fun `should create route with custom category name`() {
            val result = AuraCustomizationRoute.IconifyCategory.createRoute("my_custom_category")
            assertEquals("aura/iconify/my_custom_category", result)
        }

        @Test
        @DisplayName("should handle empty category string")
        fun `should handle empty category string`() {
            val result = AuraCustomizationRoute.IconifyCategory.createRoute("")
            assertEquals("aura/iconify/", result)
        }

        @Test
        @DisplayName("createRoute result should start with base IconifyPicker route")
        fun `createRoute result should start with base IconifyPicker route`() {
            val result = AuraCustomizationRoute.IconifyCategory.createRoute("some_cat")
            assertTrue(result.startsWith(AuraCustomizationRoute.IconifyPicker.route))
        }
    }

    // =========================================================================
    // AuraSubGates
    // =========================================================================

    @Nested
    @DisplayName("AuraSubGates")
    inner class AuraSubGatesTests {

        @Test
        @DisplayName("ChromaCoreGate should have correct default name")
        fun `ChromaCoreGate should have correct default name`() {
            val gate = AuraSubGates.ChromaCoreGate()
            assertEquals("ChromaCore", gate.name)
        }

        @Test
        @DisplayName("ChromaCoreGate routes should include ColorBlendr route")
        fun `ChromaCoreGate routes should include ColorBlendr route`() {
            val gate = AuraSubGates.ChromaCoreGate()
            assertTrue(gate.routes.contains(AuraCustomizationRoute.ColorBlendr.route))
        }

        @Test
        @DisplayName("ChromaCoreGate routes should include ColorBlendrMonet route")
        fun `ChromaCoreGate routes should include ColorBlendrMonet route`() {
            val gate = AuraSubGates.ChromaCoreGate()
            assertTrue(gate.routes.contains(AuraCustomizationRoute.ColorBlendrMonet.route))
        }

        @Test
        @DisplayName("ChromaCoreGate routes should include ColorBlendrPalette route")
        fun `ChromaCoreGate routes should include ColorBlendrPalette route`() {
            val gate = AuraSubGates.ChromaCoreGate()
            assertTrue(gate.routes.contains(AuraCustomizationRoute.ColorBlendrPalette.route))
        }

        @Test
        @DisplayName("ChromaCoreGate settingsCount should be 16")
        fun `ChromaCoreGate settingsCount should be 16`() {
            val gate = AuraSubGates.ChromaCoreGate()
            assertEquals(16, gate.settingsCount)
        }

        @Test
        @DisplayName("ThemeEngineGate should have correct default name")
        fun `ThemeEngineGate should have correct default name`() {
            val gate = AuraSubGates.ThemeEngineGate()
            assertEquals("Theme Engine", gate.name)
        }

        @Test
        @DisplayName("ThemeEngineGate routes should include IconifyPicker route")
        fun `ThemeEngineGate routes should include IconifyPicker route`() {
            val gate = AuraSubGates.ThemeEngineGate()
            assertTrue(gate.routes.contains(AuraCustomizationRoute.IconifyPicker.route))
        }

        @Test
        @DisplayName("ThemeEngineGate settingsCount should be 69")
        fun `ThemeEngineGate settingsCount should be 69`() {
            val gate = AuraSubGates.ThemeEngineGate()
            assertEquals(69, gate.settingsCount)
        }

        @Test
        @DisplayName("ThemeEngineGate categories should have 12 entries")
        fun `ThemeEngineGate categories should have 12 entries`() {
            val gate = AuraSubGates.ThemeEngineGate()
            assertEquals(12, gate.categories.size)
        }

        @Test
        @DisplayName("CollabCanvasGate should have correct default name")
        fun `CollabCanvasGate should have correct default name`() {
            val gate = AuraSubGates.CollabCanvasGate()
            assertEquals("CollabCanvas", gate.name)
        }

        @Test
        @DisplayName("CollabCanvasGate routes should include all PLE routes")
        fun `CollabCanvasGate routes should include all PLE routes`() {
            val gate = AuraSubGates.CollabCanvasGate()
            assertTrue(gate.routes.contains(AuraCustomizationRoute.PixelLauncherEnhanced.route))
            assertTrue(gate.routes.contains(AuraCustomizationRoute.PLEIcons.route))
            assertTrue(gate.routes.contains(AuraCustomizationRoute.PLEHomeScreen.route))
            assertTrue(gate.routes.contains(AuraCustomizationRoute.PLEAppDrawer.route))
            assertTrue(gate.routes.contains(AuraCustomizationRoute.PLERecents.route))
        }

        @Test
        @DisplayName("CollabCanvasGate settingsCount should be 29")
        fun `CollabCanvasGate settingsCount should be 29`() {
            val gate = AuraSubGates.CollabCanvasGate()
            assertEquals(29, gate.settingsCount)
        }

        @Test
        @DisplayName("getSummary should return non-empty string")
        fun `getSummary should return non-empty string`() {
            val summary = AuraSubGates.getSummary()
            assertTrue(summary.isNotBlank())
        }

        @Test
        @DisplayName("getSummary should mention ChromaCore")
        fun `getSummary should mention ChromaCore`() {
            assertTrue(AuraSubGates.getSummary().contains("CHROMACORE"))
        }

        @Test
        @DisplayName("getSummary should mention THEME ENGINE")
        fun `getSummary should mention THEME ENGINE`() {
            assertTrue(AuraSubGates.getSummary().contains("THEME ENGINE"))
        }

        @Test
        @DisplayName("getSummary should mention COLLABCANVAS")
        fun `getSummary should mention COLLABCANVAS`() {
            assertTrue(AuraSubGates.getSummary().contains("COLLABCANVAS"))
        }
    }

    // =========================================================================
    // IntegrationStatus
    // =========================================================================

    @Nested
    @DisplayName("IntegrationStatus")
    inner class IntegrationStatusTests {

        @Test
        @DisplayName("iconifyComplete should be true")
        fun `iconifyComplete should be true`() {
            assertTrue(IntegrationStatus.iconifyComplete)
        }

        @Test
        @DisplayName("colorBlendrComplete should be true")
        fun `colorBlendrComplete should be true`() {
            assertTrue(IntegrationStatus.colorBlendrComplete)
        }

        @Test
        @DisplayName("pixelLauncherEnhancedComplete should be true")
        fun `pixelLauncherEnhancedComplete should be true`() {
            assertTrue(IntegrationStatus.pixelLauncherEnhancedComplete)
        }

        @Test
        @DisplayName("totalSettings should be 114")
        fun `totalSettings should be 114`() {
            assertEquals(114, IntegrationStatus.totalSettings)
        }

        @Test
        @DisplayName("categoriesCount should be 22")
        fun `categoriesCount should be 22`() {
            assertEquals(22, IntegrationStatus.categoriesCount)
        }

        @Test
        @DisplayName("notes should be non-empty")
        fun `notes should be non-empty`() {
            assertTrue(IntegrationStatus.notes.isNotBlank())
        }
    }
}