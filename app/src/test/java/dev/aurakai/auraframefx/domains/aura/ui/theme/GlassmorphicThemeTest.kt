package dev.aurakai.auraframefx.domains.aura.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

/**
 * Comprehensive unit tests for [GlassmorphicTheme].
 *
 * Tests cover:
 * - Exact ARGB color values for all palette entries
 * - Alpha channel correctness for translucent glass colors
 * - Gradient brush existence and composition
 * - Singleton identity (object reference stability)
 * - Distinctness between logically separate colors
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("GlassmorphicTheme Test Suite")
class GlassmorphicThemeTest {

    @Nested
    @DisplayName("Primary and Secondary Color Values")
    inner class PrimarySecondaryColorTests {

        @Test
        @DisplayName("Primary color should be 0xFF4A90E2")
        fun `Primary should have correct ARGB value`() {
            assertEquals(Color(0xFF4A90E2), GlassmorphicTheme.Primary)
        }

        @Test
        @DisplayName("PrimaryVariant color should be 0xFF357ABD")
        fun `PrimaryVariant should have correct ARGB value`() {
            assertEquals(Color(0xFF357ABD), GlassmorphicTheme.PrimaryVariant)
        }

        @Test
        @DisplayName("Secondary color should be 0xFF9B7EBD")
        fun `Secondary should have correct ARGB value`() {
            assertEquals(Color(0xFF9B7EBD), GlassmorphicTheme.Secondary)
        }

        @Test
        @DisplayName("SecondaryVariant color should be 0xFF7B5FA0")
        fun `SecondaryVariant should have correct ARGB value`() {
            assertEquals(Color(0xFF7B5FA0), GlassmorphicTheme.SecondaryVariant)
        }

        @Test
        @DisplayName("Primary and PrimaryVariant should be distinct")
        fun `Primary and PrimaryVariant should be distinct colors`() {
            assertNotEquals(
                GlassmorphicTheme.Primary,
                GlassmorphicTheme.PrimaryVariant,
                "Primary and PrimaryVariant must differ"
            )
        }

        @Test
        @DisplayName("Secondary and SecondaryVariant should be distinct")
        fun `Secondary and SecondaryVariant should be distinct colors`() {
            assertNotEquals(
                GlassmorphicTheme.Secondary,
                GlassmorphicTheme.SecondaryVariant,
                "Secondary and SecondaryVariant must differ"
            )
        }
    }

    @Nested
    @DisplayName("Accent Color Values")
    inner class AccentColorTests {

        @Test
        @DisplayName("Accent color should be 0xFF64B5F6")
        fun `Accent should have correct ARGB value`() {
            assertEquals(Color(0xFF64B5F6), GlassmorphicTheme.Accent)
        }

        @Test
        @DisplayName("AccentGold color should be 0xFFD4AF37")
        fun `AccentGold should have correct ARGB value`() {
            assertEquals(Color(0xFFD4AF37), GlassmorphicTheme.AccentGold)
        }

        @Test
        @DisplayName("Accent and AccentGold should be distinct colors")
        fun `Accent and AccentGold should be distinct`() {
            assertNotEquals(
                GlassmorphicTheme.Accent,
                GlassmorphicTheme.AccentGold,
                "Accent and AccentGold must differ"
            )
        }
    }

    @Nested
    @DisplayName("Surface and Background Color Values")
    inner class SurfaceBackgroundColorTests {

        @Test
        @DisplayName("Surface color should be 0xFF1E1E2E")
        fun `Surface should have correct ARGB value`() {
            assertEquals(Color(0xFF1E1E2E), GlassmorphicTheme.Surface)
        }

        @Test
        @DisplayName("SurfaceVariant color should be 0xFF2A2A3E")
        fun `SurfaceVariant should have correct ARGB value`() {
            assertEquals(Color(0xFF2A2A3E), GlassmorphicTheme.SurfaceVariant)
        }

        @Test
        @DisplayName("Background color should be 0xFF0F0F1A")
        fun `Background should have correct ARGB value`() {
            assertEquals(Color(0xFF0F0F1A), GlassmorphicTheme.Background)
        }

        @Test
        @DisplayName("Surface and SurfaceVariant should be distinct")
        fun `Surface and SurfaceVariant should be distinct`() {
            assertNotEquals(
                GlassmorphicTheme.Surface,
                GlassmorphicTheme.SurfaceVariant,
                "Surface and SurfaceVariant must differ"
            )
        }

        @Test
        @DisplayName("Background should be darker than Surface")
        fun `Background should differ from Surface`() {
            assertNotEquals(
                GlassmorphicTheme.Background,
                GlassmorphicTheme.Surface,
                "Background and Surface must be distinct"
            )
        }
    }

    @Nested
    @DisplayName("Text Color Values")
    inner class TextColorTests {

        @Test
        @DisplayName("TextPrimary should be fully opaque white 0xFFFFFFFF")
        fun `TextPrimary should be opaque white`() {
            assertEquals(Color(0xFFFFFFFF), GlassmorphicTheme.TextPrimary)
        }

        @Test
        @DisplayName("TextSecondary should be semi-transparent white 0xB3FFFFFF")
        fun `TextSecondary should be semi-transparent white`() {
            assertEquals(Color(0xB3FFFFFF), GlassmorphicTheme.TextSecondary)
        }

        @Test
        @DisplayName("TextPrimary should be fully opaque (alpha 0xFF)")
        fun `TextPrimary should have full alpha`() {
            val alpha = (GlassmorphicTheme.TextPrimary.value shr 56).toInt() and 0xFF
            assertEquals(0xFF, alpha, "TextPrimary must have alpha = 0xFF")
        }

        @Test
        @DisplayName("TextSecondary should have partial alpha (0xB3 = 70%)")
        fun `TextSecondary should have 70 percent alpha`() {
            // Color(0xB3FFFFFF): alpha component is 0xB3 = 179 out of 255
            val alpha = (GlassmorphicTheme.TextSecondary.value shr 56).toInt() and 0xFF
            assertEquals(0xB3, alpha, "TextSecondary must have alpha = 0xB3")
        }

        @Test
        @DisplayName("TextPrimary and TextSecondary should be distinct")
        fun `TextPrimary and TextSecondary should be distinct`() {
            assertNotEquals(
                GlassmorphicTheme.TextPrimary,
                GlassmorphicTheme.TextSecondary,
                "TextPrimary and TextSecondary must differ in alpha"
            )
        }
    }

    @Nested
    @DisplayName("Glass Translucent Color Values")
    inner class GlassColorTests {

        @Test
        @DisplayName("GlassWhite should be 0x1AFFFFFF")
        fun `GlassWhite should have correct value`() {
            assertEquals(Color(0x1AFFFFFF), GlassmorphicTheme.GlassWhite)
        }

        @Test
        @DisplayName("GlassBorder should be 0x33FFFFFF")
        fun `GlassBorder should have correct value`() {
            assertEquals(Color(0x33FFFFFF), GlassmorphicTheme.GlassBorder)
        }

        @Test
        @DisplayName("GlassHighlight should be 0x0DFFFFFF")
        fun `GlassHighlight should have correct value`() {
            assertEquals(Color(0x0DFFFFFF), GlassmorphicTheme.GlassHighlight)
        }

        @Test
        @DisplayName("GlassWhite alpha should be 0x1A (10%)")
        fun `GlassWhite should have 10 percent alpha`() {
            val alpha = (GlassmorphicTheme.GlassWhite.value shr 56).toInt() and 0xFF
            assertEquals(0x1A, alpha, "GlassWhite alpha must be 0x1A")
        }

        @Test
        @DisplayName("GlassBorder alpha should be 0x33 (20%)")
        fun `GlassBorder should have 20 percent alpha`() {
            val alpha = (GlassmorphicTheme.GlassBorder.value shr 56).toInt() and 0xFF
            assertEquals(0x33, alpha, "GlassBorder alpha must be 0x33")
        }

        @Test
        @DisplayName("GlassHighlight alpha should be 0x0D (5%)")
        fun `GlassHighlight should have 5 percent alpha`() {
            val alpha = (GlassmorphicTheme.GlassHighlight.value shr 56).toInt() and 0xFF
            assertEquals(0x0D, alpha, "GlassHighlight alpha must be 0x0D")
        }

        @Test
        @DisplayName("GlassBorder should be more opaque than GlassWhite")
        fun `GlassBorder should have higher alpha than GlassWhite`() {
            val glassBorderAlpha = (GlassmorphicTheme.GlassBorder.value shr 56).toInt() and 0xFF
            val glassWhiteAlpha = (GlassmorphicTheme.GlassWhite.value shr 56).toInt() and 0xFF
            assert(glassBorderAlpha > glassWhiteAlpha) {
                "GlassBorder (alpha=$glassBorderAlpha) should be more opaque than GlassWhite (alpha=$glassWhiteAlpha)"
            }
        }

        @Test
        @DisplayName("GlassWhite should be more opaque than GlassHighlight")
        fun `GlassWhite should have higher alpha than GlassHighlight`() {
            val glassWhiteAlpha = (GlassmorphicTheme.GlassWhite.value shr 56).toInt() and 0xFF
            val glassHighlightAlpha = (GlassmorphicTheme.GlassHighlight.value shr 56).toInt() and 0xFF
            assert(glassWhiteAlpha > glassHighlightAlpha) {
                "GlassWhite (alpha=$glassWhiteAlpha) should be more opaque than GlassHighlight (alpha=$glassHighlightAlpha)"
            }
        }

        @Test
        @DisplayName("All three glass colors should be distinct")
        fun `GlassWhite, GlassBorder, and GlassHighlight should all be distinct`() {
            assertNotEquals(
                GlassmorphicTheme.GlassWhite,
                GlassmorphicTheme.GlassBorder,
                "GlassWhite and GlassBorder must differ"
            )
            assertNotEquals(
                GlassmorphicTheme.GlassWhite,
                GlassmorphicTheme.GlassHighlight,
                "GlassWhite and GlassHighlight must differ"
            )
            assertNotEquals(
                GlassmorphicTheme.GlassBorder,
                GlassmorphicTheme.GlassHighlight,
                "GlassBorder and GlassHighlight must differ"
            )
        }
    }

    @Nested
    @DisplayName("etherealGradient Brush")
    inner class EtherealGradientTests {

        @Test
        @DisplayName("etherealGradient should not be null")
        fun `etherealGradient should not be null`() {
            assertNotNull(GlassmorphicTheme.etherealGradient, "etherealGradient must not be null")
        }

        @Test
        @DisplayName("etherealGradient should be a Brush instance")
        fun `etherealGradient should be a Brush`() {
            assert(GlassmorphicTheme.etherealGradient is Brush) {
                "etherealGradient must be a Brush"
            }
        }

        @Test
        @DisplayName("etherealGradient reference should be stable (singleton field)")
        fun `etherealGradient reference should be the same object each access`() {
            val first = GlassmorphicTheme.etherealGradient
            val second = GlassmorphicTheme.etherealGradient
            assertSame(first, second, "etherealGradient must return the same Brush instance")
        }
    }

    @Nested
    @DisplayName("Singleton Identity")
    inner class SingletonTests {

        @Test
        @DisplayName("GlassmorphicTheme should behave as a singleton object")
        fun `GlassmorphicTheme should be the same reference on each access`() {
            val ref1 = GlassmorphicTheme
            val ref2 = GlassmorphicTheme
            assertSame(ref1, ref2, "GlassmorphicTheme must be a singleton")
        }

        @Test
        @DisplayName("Color properties should return identical values on repeated access")
        fun `Color properties should be referentially stable`() {
            assertSame(
                GlassmorphicTheme.Primary,
                GlassmorphicTheme.Primary,
                "Primary must return same Color instance"
            )
            assertSame(
                GlassmorphicTheme.Background,
                GlassmorphicTheme.Background,
                "Background must return same Color instance"
            )
        }
    }

    @Nested
    @DisplayName("Color Palette Completeness")
    inner class PaletteCompletenessTests {

        @Test
        @DisplayName("All opaque brand colors should have full alpha (0xFF)")
        fun `All opaque brand colors should have full alpha channel`() {
            val opaqueColors = mapOf(
                "Primary" to GlassmorphicTheme.Primary,
                "PrimaryVariant" to GlassmorphicTheme.PrimaryVariant,
                "Secondary" to GlassmorphicTheme.Secondary,
                "SecondaryVariant" to GlassmorphicTheme.SecondaryVariant,
                "Accent" to GlassmorphicTheme.Accent,
                "AccentGold" to GlassmorphicTheme.AccentGold,
                "Surface" to GlassmorphicTheme.Surface,
                "SurfaceVariant" to GlassmorphicTheme.SurfaceVariant,
                "Background" to GlassmorphicTheme.Background,
                "TextPrimary" to GlassmorphicTheme.TextPrimary,
            )

            opaqueColors.forEach { (name, color) ->
                val alpha = (color.value shr 56).toInt() and 0xFF
                assertEquals(0xFF, alpha, "$name must have full alpha (0xFF), got 0x${alpha.toString(16)}")
            }
        }

        @Test
        @DisplayName("Glass overlay colors should all be translucent (alpha < 0xFF)")
        fun `All glass colors should be translucent`() {
            val glassColors = mapOf(
                "GlassWhite" to GlassmorphicTheme.GlassWhite,
                "GlassBorder" to GlassmorphicTheme.GlassBorder,
                "GlassHighlight" to GlassmorphicTheme.GlassHighlight,
                "TextSecondary" to GlassmorphicTheme.TextSecondary,
            )

            glassColors.forEach { (name, color) ->
                val alpha = (color.value shr 56).toInt() and 0xFF
                assert(alpha < 0xFF) {
                    "$name should be translucent (alpha < 0xFF), got alpha=0x${alpha.toString(16)}"
                }
            }
        }

        @Test
        @DisplayName("All 13 palette entries should be defined and non-default")
        fun `All palette entries should be defined`() {
            // Verify none of the palette colors equal Color.Unspecified (the zero value)
            val paletteEntries = listOf(
                GlassmorphicTheme.Primary,
                GlassmorphicTheme.PrimaryVariant,
                GlassmorphicTheme.Secondary,
                GlassmorphicTheme.SecondaryVariant,
                GlassmorphicTheme.Accent,
                GlassmorphicTheme.AccentGold,
                GlassmorphicTheme.Surface,
                GlassmorphicTheme.SurfaceVariant,
                GlassmorphicTheme.Background,
                GlassmorphicTheme.TextPrimary,
                GlassmorphicTheme.TextSecondary,
                GlassmorphicTheme.GlassWhite,
                GlassmorphicTheme.GlassBorder,
                GlassmorphicTheme.GlassHighlight,
            )

            paletteEntries.forEach { color ->
                assertNotEquals(
                    Color.Unspecified,
                    color,
                    "No palette entry should equal Color.Unspecified"
                )
            }
        }
    }
}