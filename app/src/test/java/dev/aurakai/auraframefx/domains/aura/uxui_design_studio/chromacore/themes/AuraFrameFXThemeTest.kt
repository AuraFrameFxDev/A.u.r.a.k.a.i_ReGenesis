package dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.themes

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import dev.aurakai.auraframefx.domains.aura.ui.theme.GlassmorphicTheme
import org.junit.Rule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * Tests for the chromacore [AuraFrameFXTheme] composable.
 *
 * This theme was rewritten (PR change) to directly build its color schemes from
 * [GlassmorphicTheme] instead of delegating to a canonical theme.  The tests verify:
 * - Dark color scheme uses the correct GlassmorphicTheme tokens
 * - Light color scheme uses the correct GlassmorphicTheme tokens
 * - Both schemes use GlassmorphicTheme.TextPrimary as every "on" color
 * - The secondary container and surface variant tokens are mapped correctly
 * - Both schemes are distinct only in scheme-level identity, not in the color values
 *   (by design this theme applies the same glassmorphic palette to both dark and light modes)
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Chromacore AuraFrameFXTheme Color Scheme Tests")
class AuraFrameFXThemeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ---------------------------------------------------------------------------
    // Helper: capture color scheme inside composition
    // ---------------------------------------------------------------------------

    private fun captureColorSchemeForDark(): ColorScheme {
        var captured: ColorScheme? = null
        composeTestRule.setContent {
            AuraFrameFXTheme(darkTheme = true) {
                captured = androidx.compose.material3.MaterialTheme.colorScheme
            }
        }
        return captured!!
    }

    private fun captureColorSchemeForLight(): ColorScheme {
        var captured: ColorScheme? = null
        composeTestRule.setContent {
            AuraFrameFXTheme(darkTheme = false) {
                captured = androidx.compose.material3.MaterialTheme.colorScheme
            }
        }
        return captured!!
    }

    // ---------------------------------------------------------------------------
    // Dark theme color scheme
    // ---------------------------------------------------------------------------

    @Nested
    @DisplayName("Dark Theme Color Scheme")
    inner class DarkThemeColorSchemeTests {

        @Test
        @DisplayName("Dark theme primary should equal GlassmorphicTheme.Primary")
        fun `dark theme primary should use GlassmorphicTheme Primary`() {
            val scheme = captureColorSchemeForDark()
            assertEquals(
                GlassmorphicTheme.Primary,
                scheme.primary,
                "dark primary must equal GlassmorphicTheme.Primary"
            )
        }

        @Test
        @DisplayName("Dark theme secondary should equal GlassmorphicTheme.Secondary")
        fun `dark theme secondary should use GlassmorphicTheme Secondary`() {
            val scheme = captureColorSchemeForDark()
            assertEquals(
                GlassmorphicTheme.Secondary,
                scheme.secondary,
                "dark secondary must equal GlassmorphicTheme.Secondary"
            )
        }

        @Test
        @DisplayName("Dark theme background should equal GlassmorphicTheme.Background")
        fun `dark theme background should use GlassmorphicTheme Background`() {
            val scheme = captureColorSchemeForDark()
            assertEquals(
                GlassmorphicTheme.Background,
                scheme.background,
                "dark background must equal GlassmorphicTheme.Background"
            )
        }

        @Test
        @DisplayName("Dark theme surface should equal GlassmorphicTheme.Surface")
        fun `dark theme surface should use GlassmorphicTheme Surface`() {
            val scheme = captureColorSchemeForDark()
            assertEquals(
                GlassmorphicTheme.Surface,
                scheme.surface,
                "dark surface must equal GlassmorphicTheme.Surface"
            )
        }

        @Test
        @DisplayName("Dark theme onPrimary should equal GlassmorphicTheme.TextPrimary")
        fun `dark theme onPrimary should use GlassmorphicTheme TextPrimary`() {
            val scheme = captureColorSchemeForDark()
            assertEquals(
                GlassmorphicTheme.TextPrimary,
                scheme.onPrimary,
                "dark onPrimary must equal GlassmorphicTheme.TextPrimary"
            )
        }

        @Test
        @DisplayName("Dark theme onSecondary should equal GlassmorphicTheme.TextPrimary")
        fun `dark theme onSecondary should use GlassmorphicTheme TextPrimary`() {
            val scheme = captureColorSchemeForDark()
            assertEquals(
                GlassmorphicTheme.TextPrimary,
                scheme.onSecondary,
                "dark onSecondary must equal GlassmorphicTheme.TextPrimary"
            )
        }

        @Test
        @DisplayName("Dark theme onBackground should equal GlassmorphicTheme.TextPrimary")
        fun `dark theme onBackground should use GlassmorphicTheme TextPrimary`() {
            val scheme = captureColorSchemeForDark()
            assertEquals(
                GlassmorphicTheme.TextPrimary,
                scheme.onBackground,
                "dark onBackground must equal GlassmorphicTheme.TextPrimary"
            )
        }

        @Test
        @DisplayName("Dark theme onSurface should equal GlassmorphicTheme.TextPrimary")
        fun `dark theme onSurface should use GlassmorphicTheme TextPrimary`() {
            val scheme = captureColorSchemeForDark()
            assertEquals(
                GlassmorphicTheme.TextPrimary,
                scheme.onSurface,
                "dark onSurface must equal GlassmorphicTheme.TextPrimary"
            )
        }

        @Test
        @DisplayName("Dark theme secondaryContainer should equal GlassmorphicTheme.SecondaryVariant")
        fun `dark theme secondaryContainer should use GlassmorphicTheme SecondaryVariant`() {
            val scheme = captureColorSchemeForDark()
            assertEquals(
                GlassmorphicTheme.SecondaryVariant,
                scheme.secondaryContainer,
                "dark secondaryContainer must equal GlassmorphicTheme.SecondaryVariant"
            )
        }

        @Test
        @DisplayName("Dark theme surfaceVariant should equal GlassmorphicTheme.SurfaceVariant")
        fun `dark theme surfaceVariant should use GlassmorphicTheme SurfaceVariant`() {
            val scheme = captureColorSchemeForDark()
            assertEquals(
                GlassmorphicTheme.SurfaceVariant,
                scheme.surfaceVariant,
                "dark surfaceVariant must equal GlassmorphicTheme.SurfaceVariant"
            )
        }
    }

    // ---------------------------------------------------------------------------
    // Light theme color scheme
    // ---------------------------------------------------------------------------

    @Nested
    @DisplayName("Light Theme Color Scheme")
    inner class LightThemeColorSchemeTests {

        @Test
        @DisplayName("Light theme primary should equal GlassmorphicTheme.Primary")
        fun `light theme primary should use GlassmorphicTheme Primary`() {
            val scheme = captureColorSchemeForLight()
            assertEquals(
                GlassmorphicTheme.Primary,
                scheme.primary,
                "light primary must equal GlassmorphicTheme.Primary"
            )
        }

        @Test
        @DisplayName("Light theme secondary should equal GlassmorphicTheme.Secondary")
        fun `light theme secondary should use GlassmorphicTheme Secondary`() {
            val scheme = captureColorSchemeForLight()
            assertEquals(
                GlassmorphicTheme.Secondary,
                scheme.secondary,
                "light secondary must equal GlassmorphicTheme.Secondary"
            )
        }

        @Test
        @DisplayName("Light theme background should equal GlassmorphicTheme.Background")
        fun `light theme background should use GlassmorphicTheme Background`() {
            val scheme = captureColorSchemeForLight()
            assertEquals(
                GlassmorphicTheme.Background,
                scheme.background,
                "light background must equal GlassmorphicTheme.Background"
            )
        }

        @Test
        @DisplayName("Light theme surface should equal GlassmorphicTheme.Surface")
        fun `light theme surface should use GlassmorphicTheme Surface`() {
            val scheme = captureColorSchemeForLight()
            assertEquals(
                GlassmorphicTheme.Surface,
                scheme.surface,
                "light surface must equal GlassmorphicTheme.Surface"
            )
        }

        @Test
        @DisplayName("Light theme all on-colors should equal GlassmorphicTheme.TextPrimary")
        fun `light theme on-colors should all use GlassmorphicTheme TextPrimary`() {
            val scheme = captureColorSchemeForLight()
            assertEquals(GlassmorphicTheme.TextPrimary, scheme.onPrimary, "light onPrimary")
            assertEquals(GlassmorphicTheme.TextPrimary, scheme.onSecondary, "light onSecondary")
            assertEquals(GlassmorphicTheme.TextPrimary, scheme.onBackground, "light onBackground")
            assertEquals(GlassmorphicTheme.TextPrimary, scheme.onSurface, "light onSurface")
        }

        @Test
        @DisplayName("Light theme secondaryContainer should equal GlassmorphicTheme.SecondaryVariant")
        fun `light theme secondaryContainer should use GlassmorphicTheme SecondaryVariant`() {
            val scheme = captureColorSchemeForLight()
            assertEquals(
                GlassmorphicTheme.SecondaryVariant,
                scheme.secondaryContainer,
                "light secondaryContainer must equal GlassmorphicTheme.SecondaryVariant"
            )
        }

        @Test
        @DisplayName("Light theme surfaceVariant should equal GlassmorphicTheme.SurfaceVariant")
        fun `light theme surfaceVariant should use GlassmorphicTheme SurfaceVariant`() {
            val scheme = captureColorSchemeForLight()
            assertEquals(
                GlassmorphicTheme.SurfaceVariant,
                scheme.surfaceVariant,
                "light surfaceVariant must equal GlassmorphicTheme.SurfaceVariant"
            )
        }
    }

    // ---------------------------------------------------------------------------
    // Dark vs Light comparison
    // ---------------------------------------------------------------------------

    @Nested
    @DisplayName("Dark vs Light Theme Comparison")
    inner class DarkVsLightTests {

        @Test
        @DisplayName("Dark and light themes use the same primary color (glassmorphic palette is mode-agnostic)")
        fun `dark and light primary colors should both equal GlassmorphicTheme Primary`() {
            val dark = captureColorSchemeForDark()
            val light = captureColorSchemeForLight()
            assertEquals(dark.primary, light.primary, "Both modes must share GlassmorphicTheme.Primary")
        }

        @Test
        @DisplayName("Dark and light themes use the same background color")
        fun `dark and light background colors should both equal GlassmorphicTheme Background`() {
            val dark = captureColorSchemeForDark()
            val light = captureColorSchemeForLight()
            assertEquals(dark.background, light.background, "Both modes must share GlassmorphicTheme.Background")
        }

        @Test
        @DisplayName("Color scheme objects for dark and light are both valid ColorScheme instances")
        fun `both color schemes should be valid ColorScheme instances`() {
            val dark = captureColorSchemeForDark()
            val light = captureColorSchemeForLight()
            assertNotNull(dark, "Dark ColorScheme must not be null")
            assertNotNull(light, "Light ColorScheme must not be null")
            assertTrue(dark is ColorScheme, "dark must be a ColorScheme")
            assertTrue(light is ColorScheme, "light must be a ColorScheme")
        }
    }

    // ---------------------------------------------------------------------------
    // Composable renders without throwing
    // ---------------------------------------------------------------------------

    @Nested
    @DisplayName("Composable Rendering")
    inner class ComposableRenderingTests {

        @Test
        @DisplayName("AuraFrameFXTheme renders with darkTheme=true without throwing")
        fun `AuraFrameFXTheme darkTheme true renders without error`() {
            composeTestRule.setContent {
                AuraFrameFXTheme(darkTheme = true) {}
            }
            // If no exception was thrown, the test passes
        }

        @Test
        @DisplayName("AuraFrameFXTheme renders with darkTheme=false without throwing")
        fun `AuraFrameFXTheme darkTheme false renders without error`() {
            composeTestRule.setContent {
                AuraFrameFXTheme(darkTheme = false) {}
            }
        }

        @Test
        @DisplayName("AuraFrameFXTheme exposes MaterialTheme color scheme to content")
        fun `AuraFrameFXTheme should set MaterialTheme colorScheme visible to content`() {
            var observedPrimary: Color? = null
            composeTestRule.setContent {
                AuraFrameFXTheme(darkTheme = true) {
                    observedPrimary = androidx.compose.material3.MaterialTheme.colorScheme.primary
                }
            }
            assertNotNull(observedPrimary, "MaterialTheme.colorScheme.primary must be accessible inside AuraFrameFXTheme")
            assertEquals(
                GlassmorphicTheme.Primary,
                observedPrimary,
                "Exposed primary color must equal GlassmorphicTheme.Primary"
            )
        }
    }
}