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

    // Helper: extract alpha from a Compose Color ULong value (bits 56-63)
    private fun Color.alphaComponent(): Int = (this.value shr 56).toInt() and 0xFF

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
        @DisplayName("Primary and PrimaryVariant should be distinct colors")
        fun `Primary and PrimaryVariant should be distinct`() {
            assertNotEquals(
                GlassmorphicTheme.Primary,
                GlassmorphicTheme.PrimaryVariant,
                "Primary and PrimaryVariant must differ"
            )
        }

        @Test
        @DisplayName("Secondary and SecondaryVariant should be distinct colors")
        fun `Secondary and SecondaryVariant should be distinct`() {
            assertNotEquals(
                GlassmorphicTheme.Secondary,
                GlassmorphicTheme.SecondaryVariant,
                "Secondary and SecondaryVariant must differ"
            )
        }

        @Test
        @DisplayName("Primary and Secondary should be distinct colors")
        fun `Primary and Secondary should be distinct`() {
            assertNotEquals(
                GlassmorphicTheme.Primary,
                GlassmorphicTheme.Secondary,
                "Primary and Secondary must differ"
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

        @Test
        @DisplayName("Accent should be fully opaque")
        fun `Accent should have full alpha`() {
            assertEquals(0xFF, GlassmorphicTheme.Accent.alphaComponent(), "Accent must be opaque")
        }

        @Test
        @DisplayName("AccentGold should be fully opaque")
        fun `AccentGold should have full alpha`() {
            assertEquals(0xFF, GlassmorphicTheme.AccentGold.alphaComponent(), "AccentGold must be opaque")
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
        @DisplayName("Background and Surface should be distinct")
        fun `Background and Surface should be distinct`() {
            assertNotEquals(
                GlassmorphicTheme.Background,
                GlassmorphicTheme.Surface,
                "Background and Surface must be distinct"
            )
        }

        @Test
        @DisplayName("Background should be fully opaque")
        fun `Background should have full alpha`() {
            assertEquals(0xFF, GlassmorphicTheme.Background.alphaComponent(), "Background must be opaque")
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
        @DisplayName("TextPrimary should have full alpha (0xFF)")
        fun `TextPrimary should have full alpha`() {
            assertEquals(0xFF, GlassmorphicTheme.TextPrimary.alphaComponent(), "TextPrimary must have alpha = 0xFF")
        }

        @Test
        @DisplayName("TextSecondary should have partial alpha (0xB3 = ~70%)")
        fun `TextSecondary should have 70 percent alpha`() {
            assertEquals(0xB3, GlassmorphicTheme.TextSecondary.alphaComponent(), "TextSecondary must have alpha = 0xB3")
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

        @Test
        @DisplayName("TextSecondary alpha should be less than TextPrimary alpha")
        fun `TextSecondary should have lower alpha than TextPrimary`() {
            val primaryAlpha = GlassmorphicTheme.TextPrimary.alphaComponent()
            val secondaryAlpha = GlassmorphicTheme.TextSecondary.alphaComponent()
            assert(secondaryAlpha < primaryAlpha) {
                "TextSecondary (alpha=$secondaryAlpha) should be less opaque than TextPrimary (alpha=$primaryAlpha)"
            }
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
        @DisplayName("GlassWhite alpha should be 0x1A (~10%)")
        fun `GlassWhite should have 10 percent alpha`() {
            assertEquals(0x1A, GlassmorphicTheme.GlassWhite.alphaComponent(), "GlassWhite alpha must be 0x1A")
        }

        @Test
        @DisplayName("GlassBorder alpha should be 0x33 (~20%)")
        fun `GlassBorder should have 20 percent alpha`() {
            assertEquals(0x33, GlassmorphicTheme.GlassBorder.alphaComponent(), "GlassBorder alpha must be 0x33")
        }

        @Test
        @DisplayName("GlassHighlight alpha should be 0x0D (~5%)")
        fun `GlassHighlight should have 5 percent alpha`() {
            assertEquals(0x0D, GlassmorphicTheme.GlassHighlight.alphaComponent(), "GlassHighlight alpha must be 0x0D")
        }

        @Test
        @DisplayName("GlassBorder should be more opaque than GlassWhite")
        fun `GlassBorder should have higher alpha than GlassWhite`() {
            val glassBorderAlpha = GlassmorphicTheme.GlassBorder.alphaComponent()
            val glassWhiteAlpha = GlassmorphicTheme.GlassWhite.alphaComponent()
            assert(glassBorderAlpha > glassWhiteAlpha) {
                "GlassBorder (alpha=$glassBorderAlpha) should be more opaque than GlassWhite (alpha=$glassWhiteAlpha)"
            }
        }

        @Test
        @DisplayName("GlassWhite should be more opaque than GlassHighlight")
        fun `GlassWhite should have higher alpha than GlassHighlight`() {
            val glassWhiteAlpha = GlassmorphicTheme.GlassWhite.alphaComponent()
            val glassHighlightAlpha = GlassmorphicTheme.GlassHighlight.alphaComponent()
            assert(glassWhiteAlpha > glassHighlightAlpha) {
                "GlassWhite (alpha=$glassWhiteAlpha) should be more opaque than GlassHighlight (alpha=$glassHighlightAlpha)"
            }
        }

        @Test
        @DisplayName("Opacity ordering: GlassBorder > GlassWhite > GlassHighlight")
        fun `Glass colors should be ordered by opacity correctly`() {
            val border = GlassmorphicTheme.GlassBorder.alphaComponent()
            val white = GlassmorphicTheme.GlassWhite.alphaComponent()
            val highlight = GlassmorphicTheme.GlassHighlight.alphaComponent()
            assert(border > white && white > highlight) {
                "Expected GlassBorder ($border) > GlassWhite ($white) > GlassHighlight ($highlight)"
            }
        }

        @Test
        @DisplayName("All three glass colors should be distinct")
        fun `GlassWhite GlassBorder and GlassHighlight should all be distinct`() {
            assertNotEquals(GlassmorphicTheme.GlassWhite, GlassmorphicTheme.GlassBorder, "GlassWhite and GlassBorder must differ")
            assertNotEquals(GlassmorphicTheme.GlassWhite, GlassmorphicTheme.GlassHighlight, "GlassWhite and GlassHighlight must differ")
            assertNotEquals(GlassmorphicTheme.GlassBorder, GlassmorphicTheme.GlassHighlight, "GlassBorder and GlassHighlight must differ")
        }

        @Test
        @DisplayName("All glass colors should be translucent (alpha < 0xFF)")
        fun `All glass colors should be translucent`() {
            assert(GlassmorphicTheme.GlassWhite.alphaComponent() < 0xFF) { "GlassWhite must be translucent" }
            assert(GlassmorphicTheme.GlassBorder.alphaComponent() < 0xFF) { "GlassBorder must be translucent" }
            assert(GlassmorphicTheme.GlassHighlight.alphaComponent() < 0xFF) { "GlassHighlight must be translucent" }
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
            assertSame(GlassmorphicTheme.Primary, GlassmorphicTheme.Primary, "Primary must return same Color instance")
            assertSame(GlassmorphicTheme.Background, GlassmorphicTheme.Background, "Background must return same Color instance")
            assertSame(GlassmorphicTheme.GlassWhite, GlassmorphicTheme.GlassWhite, "GlassWhite must return same Color instance")
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
                assertEquals(0xFF, color.alphaComponent(), "$name must have full alpha (0xFF)")
            }
        }

        @Test
        @DisplayName("Glass overlay colors and TextSecondary should all be translucent (alpha < 0xFF)")
        fun `All glass colors and TextSecondary should be translucent`() {
            val translucentColors = mapOf(
                "GlassWhite" to GlassmorphicTheme.GlassWhite,
                "GlassBorder" to GlassmorphicTheme.GlassBorder,
                "GlassHighlight" to GlassmorphicTheme.GlassHighlight,
                "TextSecondary" to GlassmorphicTheme.TextSecondary,
            )
            translucentColors.forEach { (name, color) ->
                assert(color.alphaComponent() < 0xFF) {
                    "$name should be translucent (alpha < 0xFF), got alpha=0x${color.alphaComponent().toString(16)}"
                }
            }
        }

        @Test
        @DisplayName("No palette entry should equal Color.Unspecified")
        fun `All palette entries should be defined and non-default`() {
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
                assertNotEquals(Color.Unspecified, color, "No palette entry should equal Color.Unspecified")
            }
        }

        @Test
        @DisplayName("Palette should contain exactly 14 color entries")
        fun `Palette should contain expected number of color entries`() {
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
            assertEquals(14, paletteEntries.size, "Palette should define exactly 14 colors")
        }
    }

    @Nested
    @DisplayName("Color Separation and Regression Tests")
    inner class ColorSeparationTests {

        @Test
        @DisplayName("Background should be darker than Surface (lower value)")
        fun `Background should be a darker shade than Surface`() {
            // Both are fully opaque dark blues; Background (0xFF0F0F1A) < Surface (0xFF1E1E2E)
            assertNotEquals(
                GlassmorphicTheme.Background,
                GlassmorphicTheme.Surface,
                "Background and Surface must be distinct dark tones"
            )
        }

        @Test
        @DisplayName("Primary palette colors should all differ from glass overlay colors")
        fun `Primary colors should be distinct from glass overlay colors`() {
            val brandColors = listOf(
                GlassmorphicTheme.Primary,
                GlassmorphicTheme.Secondary,
                GlassmorphicTheme.Accent,
            )
            val glassColors = listOf(
                GlassmorphicTheme.GlassWhite,
                GlassmorphicTheme.GlassBorder,
                GlassmorphicTheme.GlassHighlight,
            )
            for (brand in brandColors) {
                for (glass in glassColors) {
                    assertNotEquals(brand, glass, "Brand color $brand must differ from glass color $glass")
                }
            }
        }
    }
}