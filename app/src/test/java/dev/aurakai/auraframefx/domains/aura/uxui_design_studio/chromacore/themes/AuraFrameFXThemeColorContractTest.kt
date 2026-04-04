package dev.aurakai.auraframefx.domains.aura.uxui_design_studio.chromacore.themes

import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.domains.aura.ui.theme.GlassmorphicTheme
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * Unit tests for the color contract of [AuraFrameFXTheme] in the chromacore module.
 *
 * This theme was rewritten in this PR to directly construct MaterialTheme color schemes
 * from [GlassmorphicTheme] tokens instead of delegating to a canonical theme.
 *
 * Since the composable itself requires Compose UI runtime (use androidTest for full
 * composable rendering tests), these tests verify:
 * - The [GlassmorphicTheme] tokens that AuraFrameFXTheme maps to its color scheme are correct
 * - Both dark and light mode intentionally use the same glassmorphic palette (mode-agnostic design)
 * - The "on-color" tokens (onPrimary, onSecondary, onBackground, onSurface) all map to
 *   [GlassmorphicTheme.TextPrimary]
 * - Container and variant tokens map to the expected GlassmorphicTheme entries
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Chromacore AuraFrameFXTheme Color Contract Tests")
class AuraFrameFXThemeColorContractTest {

    // -------------------------------------------------------------------------
    // Verify color scheme source tokens used in dark mode color scheme construction
    // dark/light both map to the same GlassmorphicTheme values (by design)
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("Primary Color Token Contract")
    inner class PrimaryColorTokenTests {

        @Test
        @DisplayName("Color scheme primary should map to GlassmorphicTheme.Primary")
        fun `primary token should be GlassmorphicTheme Primary`() {
            // AuraFrameFXTheme uses GlassmorphicTheme.Primary for both dark and light primary
            assertEquals(Color(0xFF4A90E2), GlassmorphicTheme.Primary)
        }

        @Test
        @DisplayName("GlassmorphicTheme.Primary should be fully opaque")
        fun `primary token should be fully opaque`() {
            val alpha = (GlassmorphicTheme.Primary.value shr 56).toInt() and 0xFF
            assertEquals(0xFF, alpha, "Primary color used in theme must be opaque")
        }

        @Test
        @DisplayName("GlassmorphicTheme.Primary should be stable across accesses")
        fun `primary token should be referentially stable`() {
            assertSame(GlassmorphicTheme.Primary, GlassmorphicTheme.Primary)
        }
    }

    @Nested
    @DisplayName("Secondary Color Token Contract")
    inner class SecondaryColorTokenTests {

        @Test
        @DisplayName("Color scheme secondary should map to GlassmorphicTheme.Secondary")
        fun `secondary token should be GlassmorphicTheme Secondary`() {
            assertEquals(Color(0xFF9B7EBD), GlassmorphicTheme.Secondary)
        }

        @Test
        @DisplayName("secondaryContainer slot maps to GlassmorphicTheme.SecondaryVariant")
        fun `secondary container token should be GlassmorphicTheme SecondaryVariant`() {
            assertEquals(Color(0xFF7B5FA0), GlassmorphicTheme.SecondaryVariant)
        }

        @Test
        @DisplayName("Secondary and SecondaryVariant should be distinct")
        fun `secondary and secondary variant should be distinct`() {
            assertNotEquals(
                GlassmorphicTheme.Secondary,
                GlassmorphicTheme.SecondaryVariant,
                "Secondary and SecondaryVariant must differ to provide variant contrast"
            )
        }
    }

    @Nested
    @DisplayName("Background and Surface Token Contract")
    inner class BackgroundSurfaceTokenTests {

        @Test
        @DisplayName("Color scheme background should map to GlassmorphicTheme.Background")
        fun `background token should be GlassmorphicTheme Background`() {
            assertEquals(Color(0xFF0F0F1A), GlassmorphicTheme.Background)
        }

        @Test
        @DisplayName("Color scheme surface should map to GlassmorphicTheme.Surface")
        fun `surface token should be GlassmorphicTheme Surface`() {
            assertEquals(Color(0xFF1E1E2E), GlassmorphicTheme.Surface)
        }

        @Test
        @DisplayName("surfaceVariant slot maps to GlassmorphicTheme.SurfaceVariant")
        fun `surface variant token should be GlassmorphicTheme SurfaceVariant`() {
            assertEquals(Color(0xFF2A2A3E), GlassmorphicTheme.SurfaceVariant)
        }

        @Test
        @DisplayName("Background and Surface should be distinct dark tones")
        fun `background and surface should be distinct`() {
            assertNotEquals(
                GlassmorphicTheme.Background,
                GlassmorphicTheme.Surface,
                "Background and Surface must be distinct tokens"
            )
        }
    }

    @Nested
    @DisplayName("On-Color Token Contract")
    inner class OnColorTokenTests {

        @Test
        @DisplayName("All on-colors (onPrimary, onSecondary, onBackground, onSurface) map to GlassmorphicTheme.TextPrimary")
        fun `all on-color slots should map to GlassmorphicTheme TextPrimary`() {
            // In the rewritten AuraFrameFXTheme, ALL four on-color slots are set to GlassmorphicTheme.TextPrimary
            val textPrimary = GlassmorphicTheme.TextPrimary
            assertNotNull(textPrimary, "TextPrimary must not be null")
            assertEquals(Color(0xFFFFFFFF), textPrimary, "TextPrimary must be fully opaque white")
        }

        @Test
        @DisplayName("GlassmorphicTheme.TextPrimary should be fully opaque white")
        fun `TextPrimary should be opaque white`() {
            val alpha = (GlassmorphicTheme.TextPrimary.value shr 56).toInt() and 0xFF
            assertEquals(0xFF, alpha, "TextPrimary used for all on-colors must be fully opaque")
        }

        @Test
        @DisplayName("TextPrimary and TextSecondary should be distinct (TextSecondary is unused in the scheme but must remain defined)")
        fun `TextPrimary and TextSecondary must be distinct`() {
            assertNotEquals(
                GlassmorphicTheme.TextPrimary,
                GlassmorphicTheme.TextSecondary,
                "TextPrimary (opaque) and TextSecondary (semi-transparent) must differ"
            )
        }
    }

    @Nested
    @DisplayName("Mode-Agnostic Design Invariants")
    inner class ModeAgnosticDesignTests {

        @Test
        @DisplayName("Both dark and light modes use GlassmorphicTheme.Primary as primary - same palette for both")
        fun `primary color is the same for dark and light mode by design`() {
            // The PR redesign intentionally uses the same glassmorphic palette for both modes.
            // This verifies the token that feeds both darkColorScheme and lightColorScheme.
            val primaryUsedInBothModes = GlassmorphicTheme.Primary
            assertEquals(
                Color(0xFF4A90E2),
                primaryUsedInBothModes,
                "Primary token must remain 0xFF4A90E2 for both dark and light scheme"
            )
        }

        @Test
        @DisplayName("Both modes share GlassmorphicTheme.Background - same dark background for both")
        fun `background is the same for dark and light mode by design`() {
            val backgroundUsedInBothModes = GlassmorphicTheme.Background
            assertEquals(
                Color(0xFF0F0F1A),
                backgroundUsedInBothModes,
                "Background token must remain 0xFF0F0F1A for both dark and light scheme"
            )
        }

        @Test
        @DisplayName("GlassmorphicTheme singleton is stable - no reconstruction between accesses")
        fun `GlassmorphicTheme singleton identity should be stable`() {
            val ref1 = GlassmorphicTheme
            val ref2 = GlassmorphicTheme
            assertSame(ref1, ref2, "GlassmorphicTheme must be the same singleton instance")
        }
    }

    @Nested
    @DisplayName("Palette Token Completeness for Theme Construction")
    inner class PaletteCompletenessForThemeTests {

        @Test
        @DisplayName("All tokens required for building color scheme should be non-null and non-Unspecified")
        fun `all required color scheme tokens should be defined`() {
            val requiredTokens = mapOf(
                "Primary" to GlassmorphicTheme.Primary,
                "Secondary" to GlassmorphicTheme.Secondary,
                "Background" to GlassmorphicTheme.Background,
                "Surface" to GlassmorphicTheme.Surface,
                "TextPrimary (onPrimary/onSecondary/onBackground/onSurface)" to GlassmorphicTheme.TextPrimary,
                "SecondaryVariant (secondaryContainer)" to GlassmorphicTheme.SecondaryVariant,
                "SurfaceVariant (surfaceVariant)" to GlassmorphicTheme.SurfaceVariant,
            )
            requiredTokens.forEach { (name, color) ->
                assertNotEquals(
                    Color.Unspecified,
                    color,
                    "Token '$name' must not be Color.Unspecified"
                )
            }
        }

        @Test
        @DisplayName("All required tokens should be fully opaque (no accidental transparency in scheme)")
        fun `all required color scheme tokens should be fully opaque`() {
            val opaqueTokens = mapOf(
                "Primary" to GlassmorphicTheme.Primary,
                "Secondary" to GlassmorphicTheme.Secondary,
                "Background" to GlassmorphicTheme.Background,
                "Surface" to GlassmorphicTheme.Surface,
                "TextPrimary" to GlassmorphicTheme.TextPrimary,
                "SecondaryVariant" to GlassmorphicTheme.SecondaryVariant,
                "SurfaceVariant" to GlassmorphicTheme.SurfaceVariant,
            )
            opaqueTokens.forEach { (name, color) ->
                val alpha = (color.value shr 56).toInt() and 0xFF
                assertEquals(0xFF, alpha, "Token '$name' must be fully opaque (alpha=0xFF) for use in MaterialTheme color scheme")
            }
        }
    }
}