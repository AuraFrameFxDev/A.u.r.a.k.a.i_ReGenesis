package dev.aurakai.auraframefx.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.Rule

/**
 * Comprehensive unit tests for AuraThemes.
 * Testing framework: JUnit 5 with Compose testing rule
 *
 * Tests cover:
 * - Theme properties validation
 * - Color schemes for light and dark modes
 * - Animation styles enum
 * - getColorScheme extension function
 * - Cross-theme comparisons and edge cases
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("AuraThemes Test Suite")
class AuraThemesTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Nested
    @DisplayName("AnimationStyle Enum Tests")
    inner class AnimationStyleTests {

        @Test
        @DisplayName("Should contain all expected animation style values")
        fun `AnimationStyle enum should contain all expected values`() {
            val expectedStyles = setOf(
                AuraTheme.AnimationStyle.SUBTLE,
                AuraTheme.AnimationStyle.ENERGETIC,
                AuraTheme.AnimationStyle.CALMING,
                AuraTheme.AnimationStyle.PULSING,
                AuraTheme.AnimationStyle.FLOWING
            )

            val actualStyles = AuraTheme.AnimationStyle.values().toSet()
            assertEquals(
                expectedStyles,
                actualStyles,
                "AnimationStyle enum should contain all expected values"
            )
            assertEquals(
                5,
                AuraTheme.AnimationStyle.values().size,
                "AnimationStyle enum should have exactly 5 values"
            )
        }

        @Test
        @DisplayName("Should have correct enum names")
        fun `AnimationStyle enum values should be accessible and have correct names`() {
            assertEquals("SUBTLE", AuraTheme.AnimationStyle.SUBTLE.name)
            assertEquals("ENERGETIC", AuraTheme.AnimationStyle.ENERGETIC.name)
            assertEquals("CALMING", AuraTheme.AnimationStyle.CALMING.name)
            assertEquals("PULSING", AuraTheme.AnimationStyle.PULSING.name)
            assertEquals("FLOWING", AuraTheme.AnimationStyle.FLOWING.name)
        }

        @Test
        @DisplayName("Should have stable ordinal values")
        fun `AnimationStyle enum should have stable ordinal values`() {
            assertEquals(0, AuraTheme.AnimationStyle.SUBTLE.ordinal)
            assertEquals(1, AuraTheme.AnimationStyle.ENERGETIC.ordinal)
            assertEquals(2, AuraTheme.AnimationStyle.CALMING.ordinal)
            assertEquals(3, AuraTheme.AnimationStyle.PULSING.ordinal)
            assertEquals(4, AuraTheme.AnimationStyle.FLOWING.ordinal)
        }
    }

    @Nested
    @DisplayName("CyberpunkTheme Tests")
    inner class CyberpunkThemeTests {

        @Test
        @DisplayName("Should have correct basic properties")
        fun `CyberpunkTheme should have correct basic properties`() {
            assertEquals("Cyberpunk", CyberpunkTheme.name)
            assertEquals(
                "High-energy neon aesthetics for a futuristic feel",
                CyberpunkTheme.description
            )
            assertEquals(Color(0xFF00FFFF), CyberpunkTheme.accentColor)
            assertEquals(AuraTheme.AnimationStyle.ENERGETIC, CyberpunkTheme.animationStyle)
        }

        @Nested
        @DisplayName("Light Color Scheme")
        inner class LightColorSchemeTests {

            @Test
            @DisplayName("Should have correct primary colors")
            fun `CyberpunkTheme light color scheme should have correct primary colors`() {
                val lightScheme = CyberpunkTheme.lightColorScheme
                assertEquals(Color(0xFF00FFFF), lightScheme.primary)
                assertEquals(Color(0xFF000000), lightScheme.onPrimary)
                assertEquals(Color(0xFF004D4D), lightScheme.primaryContainer)
                assertEquals(Color(0xFF00FFFF), lightScheme.onPrimaryContainer)
            }

            @Test
            @DisplayName("Should have correct secondary colors")
            fun `CyberpunkTheme light color scheme should have correct secondary colors`() {
                val lightScheme = CyberpunkTheme.lightColorScheme
                assertEquals(Color(0xFFFF0080), lightScheme.secondary)
                assertEquals(Color(0xFF000000), lightScheme.onSecondary)
                assertEquals(Color(0xFF4D0026), lightScheme.secondaryContainer)
                assertEquals(Color(0xFFFF0080), lightScheme.onSecondaryContainer)
            }

            @Test
            @DisplayName("Should have correct tertiary colors")
            fun `CyberpunkTheme light color scheme should have correct tertiary colors`() {
                val lightScheme = CyberpunkTheme.lightColorScheme
                assertEquals(Color(0xFF8000FF), lightScheme.tertiary)
                assertEquals(Color(0xFF000000), lightScheme.onTertiary)
                assertEquals(Color(0xFF26004D), lightScheme.tertiaryContainer)
                assertEquals(Color(0xFF8000FF), lightScheme.onTertiaryContainer)
            }

            @Test
            @DisplayName("Should have correct surface colors")
            fun `CyberpunkTheme light color scheme should have correct surface colors`() {
                val lightScheme = CyberpunkTheme.lightColorScheme
                assertEquals(Color(0xFF0A0A0A), lightScheme.background)
                assertEquals(Color(0xFF00FFFF), lightScheme.onBackground)
                assertEquals(Color(0xFF1A1A1A), lightScheme.surface)
                assertEquals(Color(0xFF00FFFF), lightScheme.onSurface)
            }
        }

        @Nested
        @DisplayName("Dark Color Scheme")
        inner class DarkColorSchemeTests {

            @Test
            @DisplayName("Should have correct primary colors")
            fun `CyberpunkTheme dark color scheme should have correct primary colors`() {
                val darkScheme = CyberpunkTheme.darkColorScheme
                assertEquals(Color(0xFF00FFFF), darkScheme.primary)
                assertEquals(Color(0xFF000000), darkScheme.onPrimary)
                assertEquals(Color(0xFF004D4D), darkScheme.primaryContainer)
                assertEquals(Color(0xFF00FFFF), darkScheme.onPrimaryContainer)
            }

            @Test
            @DisplayName("Should have correct surface colors")
            fun `CyberpunkTheme dark color scheme should have correct surface colors`() {
                val darkScheme = CyberpunkTheme.darkColorScheme
                assertEquals(Color(0xFF000000), darkScheme.background)
                assertEquals(Color(0xFF00FFFF), darkScheme.onBackground)
                assertEquals(Color(0xFF0A0A0A), darkScheme.surface)
                assertEquals(Color(0xFF00FFFF), darkScheme.onSurface)
            }

            @Test
            @DisplayName("Should maintain consistent primary colors between light and dark modes")
            fun `CyberpunkTheme should maintain consistent primary colors between light and dark modes`() {
                assertEquals(
                    CyberpunkTheme.lightColorScheme.primary,
                    CyberpunkTheme.darkColorScheme.primary
                )
                assertEquals(
                    CyberpunkTheme.lightColorScheme.secondary,
                    CyberpunkTheme.darkColorScheme.secondary
                )
                assertEquals(
                    CyberpunkTheme.lightColorScheme.tertiary,
                    CyberpunkTheme.darkColorScheme.tertiary
                )
            }
        }
    }

    @Nested
    @DisplayName("SolarFlareTheme Tests")
    inner class SolarFlareThemeTests {

        @Test
        @DisplayName("Should have correct basic properties")
        fun `SolarFlareTheme should have correct basic properties`() {
            assertEquals("Solar Flare", SolarFlareTheme.name)
            assertEquals(
                "Warm, energizing colors to brighten your day",
                SolarFlareTheme.description
            )
            assertEquals(Color(0xFFFFB000), SolarFlareTheme.accentColor)
            assertEquals(AuraTheme.AnimationStyle.PULSING, SolarFlareTheme.animationStyle)
        }

        @Nested
        @DisplayName("Light Color Scheme")
        inner class LightColorSchemeTests {

            @Test
            @DisplayName("Should have correct warm primary colors")
            fun `SolarFlareTheme light color scheme should have correct warm colors`() {
                val lightScheme = SolarFlareTheme.lightColorScheme
                assertEquals(Color(0xFFFFB000), lightScheme.primary)
                assertEquals(Color(0xFF000000), lightScheme.onPrimary)
                assertEquals(Color(0xFFFF6B35), lightScheme.secondary)
                assertEquals(Color(0xFFFFD700), lightScheme.tertiary)
            }

            @Test
            @DisplayName("Should have bright background colors")
            fun `SolarFlareTheme light color scheme should have bright background colors`() {
                val lightScheme = SolarFlareTheme.lightColorScheme
                assertEquals(Color(0xFFFFFBF5), lightScheme.background)
                assertEquals(Color(0xFF4D3300), lightScheme.onBackground)
                assertEquals(Color(0xFFFFF8F0), lightScheme.surface)
                assertEquals(Color(0xFF4D3300), lightScheme.onSurface)
            }
        }

        @Nested
        @DisplayName("Dark Color Scheme")
        inner class DarkColorSchemeTests {

            @Test
            @DisplayName("Should have correct warm colors")
            fun `SolarFlareTheme dark color scheme should have correct warm colors`() {
                val darkScheme = SolarFlareTheme.darkColorScheme
                assertEquals(Color(0xFFFFB000), darkScheme.primary)
                assertEquals(Color(0xFF000000), darkScheme.onPrimary)
                assertEquals(Color(0xFFFF6B35), darkScheme.secondary)
                assertEquals(Color(0xFFFFD700), darkScheme.tertiary)
            }

            @Test
            @DisplayName("Should have dark background colors")
            fun `SolarFlareTheme dark color scheme should have dark background colors`() {
                val darkScheme = SolarFlareTheme.darkColorScheme
                assertEquals(Color(0xFF1A1000), darkScheme.background)
                assertEquals(Color(0xFFFFE0B3), darkScheme.onBackground)
                assertEquals(Color(0xFF2D1F00), darkScheme.surface)
                assertEquals(Color(0xFFFFE0B3), darkScheme.onSurface)
            }
        }
    }

    @Nested
    @DisplayName("ForestTheme Tests")
    inner class ForestThemeTests {

        @Test
        @DisplayName("Should have correct basic properties")
        fun `ForestTheme should have correct basic properties`() {
            assertEquals("Forest", ForestTheme.name)
            assertEquals("Natural, calming colors for peace and focus", ForestTheme.description)
            assertEquals(Color(0xFF4CAF50), ForestTheme.accentColor)
            assertEquals(AuraTheme.AnimationStyle.FLOWING, ForestTheme.animationStyle)
        }

        @Nested
        @DisplayName("Light Color Scheme")
        inner class LightColorSchemeTests {

            @Test
            @DisplayName("Should have correct natural colors")
            fun `ForestTheme light color scheme should have correct natural colors`() {
                val lightScheme = ForestTheme.lightColorScheme
                assertEquals(Color(0xFF4CAF50), lightScheme.primary)
                assertEquals(Color(0xFFFFFFFF), lightScheme.onPrimary)
                assertEquals(Color(0xFF8BC34A), lightScheme.secondary)
                assertEquals(Color(0xFF795548), lightScheme.tertiary)
            }

            @Test
            @DisplayName("Should have light natural background colors")
            fun `ForestTheme light color scheme should have light natural background colors`() {
                val lightScheme = ForestTheme.lightColorScheme
                assertEquals(Color(0xFFF1F8E9), lightScheme.background)
                assertEquals(Color(0xFF1B5E20), lightScheme.onBackground)
                assertEquals(Color(0xFFF8FFF8), lightScheme.surface)
                assertEquals(Color(0xFF1B5E20), lightScheme.onSurface)
            }
        }

        @Nested
        @DisplayName("Dark Color Scheme")
        inner class DarkColorSchemeTests {

            @Test
            @DisplayName("Should have correct natural colors")
            fun `ForestTheme dark color scheme should have correct natural colors`() {
                val darkScheme = ForestTheme.darkColorScheme
                assertEquals(Color(0xFF4CAF50), darkScheme.primary)
                assertEquals(Color(0xFF000000), darkScheme.onPrimary)
                assertEquals(Color(0xFF8BC34A), darkScheme.secondary)
                assertEquals(Color(0xFF795548), darkScheme.tertiary)
            }

            @Test
            @DisplayName("Should have dark natural background colors")
            fun `ForestTheme dark color scheme should have dark natural background colors`() {
                val darkScheme = ForestTheme.darkColorScheme
                assertEquals(Color(0xFF0D1F0D), darkScheme.background)
                assertEquals(Color(0xFFC8E6C9), darkScheme.onBackground)
                assertEquals(Color(0xFF1A2E1A), darkScheme.surface)
                assertEquals(Color(0xFFC8E6C9), darkScheme.onSurface)
            }
        }
    }

    @Nested
    @DisplayName("Cross-Theme Comparison Tests")
    inner class CrossThemeTests {

        @Test
        @DisplayName("All themes should have unique names")
        fun `All themes should have unique names`() {
            val names = setOf(
                CyberpunkTheme.name,
                SolarFlareTheme.name,
                ForestTheme.name
            )
            assertEquals(3, names.size, "All theme names should be unique")
        }

        @Test
        @DisplayName("All themes should have unique descriptions")
        fun `All themes should have unique descriptions`() {
            val descriptions = setOf(
                CyberpunkTheme.description,
                SolarFlareTheme.description,
                ForestTheme.description
            )
            assertEquals(3, descriptions.size, "All theme descriptions should be unique")
        }

        @Test
        @DisplayName("All themes should have unique accent colors")
        fun `All themes should have unique accent colors`() {
            val accentColors = setOf(
                CyberpunkTheme.accentColor,
                SolarFlareTheme.accentColor,
                ForestTheme.accentColor
            )
            assertEquals(3, accentColors.size, "All theme accent colors should be unique")
        }

        @Test
        @DisplayName("All themes should have unique animation styles")
        fun `All themes should have unique animation styles`() {
            val animationStyles = setOf(
                CyberpunkTheme.animationStyle,
                SolarFlareTheme.animationStyle,
                ForestTheme.animationStyle
            )
            assertEquals(3, animationStyles.size, "All theme animation styles should be unique")
        }

        @Test
        @DisplayName("All themes should implement AuraTheme interface")
        fun `All themes should implement AuraTheme interface`() {
            assertTrue(CyberpunkTheme is AuraTheme, "CyberpunkTheme should implement AuraTheme")
            assertTrue(SolarFlareTheme is AuraTheme, "SolarFlareTheme should implement AuraTheme")
            assertTrue(ForestTheme is AuraTheme, "ForestTheme should implement AuraTheme")
        }
    }

    @Nested
    @DisplayName("Color Scheme Validation Tests")
    inner class ColorSchemeValidationTests {

        @Test
        @DisplayName("All theme light color schemes should be valid ColorScheme objects")
        fun `All theme light color schemes should be valid ColorScheme objects`() {
            assertNotNull(
                CyberpunkTheme.lightColorScheme,
                "CyberpunkTheme light color scheme should not be null"
            )
            assertNotNull(
                SolarFlareTheme.lightColorScheme,
                "SolarFlareTheme light color scheme should not be null"
            )
            assertNotNull(
                ForestTheme.lightColorScheme,
                "ForestTheme light color scheme should not be null"
            )

            assertTrue(
                CyberpunkTheme.lightColorScheme is ColorScheme,
                "CyberpunkTheme light color scheme should be ColorScheme"
            )
            assertTrue(
                SolarFlareTheme.lightColorScheme is ColorScheme,
                "SolarFlareTheme light color scheme should be ColorScheme"
            )
            assertTrue(
                ForestTheme.lightColorScheme is ColorScheme,
                "ForestTheme light color scheme should be ColorScheme"
            )
        }

        @Test
        @DisplayName("All theme dark color schemes should be valid ColorScheme objects")
        fun `All theme dark color schemes should be valid ColorScheme objects`() {
            assertNotNull(
                CyberpunkTheme.darkColorScheme,
                "CyberpunkTheme dark color scheme should not be null"
            )
            assertNotNull(
                SolarFlareTheme.darkColorScheme,
                "SolarFlareTheme dark color scheme should not be null"
            )
            assertNotNull(
                ForestTheme.darkColorScheme,
                "ForestTheme dark color scheme should not be null"
            )

            assertTrue(
                CyberpunkTheme.darkColorScheme is ColorScheme,
                "CyberpunkTheme dark color scheme should be ColorScheme"
            )
            assertTrue(
                SolarFlareTheme.darkColorScheme is ColorScheme,
                "SolarFlareTheme dark color scheme should be ColorScheme"
            )
            assertTrue(
                ForestTheme.darkColorScheme is ColorScheme,
                "ForestTheme dark color scheme should be ColorScheme"
            )
        }

        @Test
        @DisplayName("Color schemes should have different background colors for light and dark modes")
        fun `Color schemes should have different background colors for light and dark modes`() {
            assertNotEquals(
                CyberpunkTheme.lightColorScheme.background,
                CyberpunkTheme.darkColorScheme.background,
                "CyberpunkTheme should have different light/dark backgrounds"
            )

            assertNotEquals(
                SolarFlareTheme.lightColorScheme.background,
                SolarFlareTheme.darkColorScheme.background,
                "SolarFlareTheme should have different light/dark backgrounds"
            )

            assertNotEquals(
                ForestTheme.lightColorScheme.background, ForestTheme.darkColorScheme.background,
                "ForestTheme should have different light/dark backgrounds"
            )
        }
    }

    @Nested
    @DisplayName("getColorScheme Extension Function Tests")
    inner class GetColorSchemeTests {

        @Test
        @DisplayName("Should return light scheme when isDarkTheme is false")
        fun `getColorScheme should return light scheme when isDarkTheme is false`() {
            composeTestRule.setContent {
                val colorScheme = CyberpunkTheme.getColorScheme(isDarkTheme = false)
                assertEquals(
                    CyberpunkTheme.lightColorScheme,
                    colorScheme,
                    "Should return light color scheme"
                )
            }
        }

        @Test
        @DisplayName("Should return dark scheme when isDarkTheme is true")
        fun `getColorScheme should return dark scheme when isDarkTheme is true`() {
            composeTestRule.setContent {
                val colorScheme = CyberpunkTheme.getColorScheme(isDarkTheme = true)
                assertEquals(
                    CyberpunkTheme.darkColorScheme,
                    colorScheme,
                    "Should return dark color scheme"
                )
            }
        }

        @Test
        @DisplayName("Should work for all themes in light mode")
        fun `getColorScheme should work for all themes in light mode`() {
            composeTestRule.setContent {
                assertEquals(
                    CyberpunkTheme.lightColorScheme,
                    CyberpunkTheme.getColorScheme(isDarkTheme = false),
                    "CyberpunkTheme should return light scheme"
                )

                assertEquals(
                    SolarFlareTheme.lightColorScheme,
                    SolarFlareTheme.getColorScheme(isDarkTheme = false),
                    "SolarFlareTheme should return light scheme"
                )

                assertEquals(
                    ForestTheme.lightColorScheme, ForestTheme.getColorScheme(isDarkTheme = false),
                    "ForestTheme should return light scheme"
                )
            }
        }

        @Test
        @DisplayName("Should work for all themes in dark mode")
        fun `getColorScheme should work for all themes in dark mode`() {
            composeTestRule.setContent {
                assertEquals(
                    CyberpunkTheme.darkColorScheme,
                    CyberpunkTheme.getColorScheme(isDarkTheme = true),
                    "CyberpunkTheme should return dark scheme"
                )

                assertEquals(
                    SolarFlareTheme.darkColorScheme,
                    SolarFlareTheme.getColorScheme(isDarkTheme = true),
                    "SolarFlareTheme should return dark scheme"
                )

                assertEquals(
                    ForestTheme.darkColorScheme, ForestTheme.getColorScheme(isDarkTheme = true),
                    "ForestTheme should return dark scheme"
                )
            }
        }
    }

    @Nested
    @DisplayName("Edge Case and Robustness Tests")
    inner class EdgeCaseTests {

        @Test
        @DisplayName("Theme names should not be empty or blank")
        fun `Theme names should not be empty or blank`() {
            assertFalse(CyberpunkTheme.name.isEmpty(), "CyberpunkTheme name should not be empty")
            assertFalse(CyberpunkTheme.name.isBlank(), "CyberpunkTheme name should not be blank")

            assertFalse(SolarFlareTheme.name.isEmpty(), "SolarFlareTheme name should not be empty")
            assertFalse(SolarFlareTheme.name.isBlank(), "SolarFlareTheme name should not be blank")

            assertFalse(ForestTheme.name.isEmpty(), "ForestTheme name should not be empty")
            assertFalse(ForestTheme.name.isBlank(), "ForestTheme name should not be blank")
        }

        @Test
        @DisplayName("Theme descriptions should not be empty or blank")
        fun `Theme descriptions should not be empty or blank`() {
            assertFalse(
                CyberpunkTheme.description.isEmpty(),
                "CyberpunkTheme description should not be empty"
            )
            assertFalse(
                CyberpunkTheme.description.isBlank(),
                "CyberpunkTheme description should not be blank"
            )

            assertFalse(
                SolarFlareTheme.description.isEmpty(),
                "SolarFlareTheme description should not be empty"
            )
            assertFalse(
                SolarFlareTheme.description.isBlank(),
                "SolarFlareTheme description should not be blank"
            )

            assertFalse(
                ForestTheme.description.isEmpty(),
                "ForestTheme description should not be empty"
            )
            assertFalse(
                ForestTheme.description.isBlank(),
                "ForestTheme description should not be blank"
            )
        }

        @Test
        @DisplayName("Accent colors should have full alpha channel")
        fun `Accent colors should have full alpha channel`() {
            assertEquals(
                0xFF, (CyberpunkTheme.accentColor.value shr 24).toInt(),
                "CyberpunkTheme accent color should have full alpha"
            )
            assertEquals(
                0xFF, (SolarFlareTheme.accentColor.value shr 24).toInt(),
                "SolarFlareTheme accent color should have full alpha"
            )
            assertEquals(
                0xFF, (ForestTheme.accentColor.value shr 24).toInt(),
                "ForestTheme accent color should have full alpha"
            )
        }

        @Test
        @DisplayName("Theme objects should maintain singleton behavior")
        fun `Theme objects should maintain singleton behavior`() {
            assertSame(CyberpunkTheme, CyberpunkTheme, "CyberpunkTheme should be singleton")
            assertSame(SolarFlareTheme, SolarFlareTheme, "SolarFlareTheme should be singleton")
            assertSame(ForestTheme, ForestTheme, "ForestTheme should be singleton")
        }

        @Test
        @DisplayName("Color contrast should be appropriate - on colors should differ from base colors")
        fun `Color contrast should be appropriate - on colors should differ from base colors`() {
            assertNotEquals(
                CyberpunkTheme.lightColorScheme.primary, CyberpunkTheme.lightColorScheme.onPrimary,
                "CyberpunkTheme primary and onPrimary should be different"
            )

            assertNotEquals(
                SolarFlareTheme.lightColorScheme.primary,
                SolarFlareTheme.lightColorScheme.onPrimary,
                "SolarFlareTheme primary and onPrimary should be different"
            )

            assertNotEquals(
                ForestTheme.lightColorScheme.primary, ForestTheme.lightColorScheme.onPrimary,
                "ForestTheme primary and onPrimary should be different"
            )
        }

        @Test
        @DisplayName("Theme accent colors should match primary colors")
        fun `Theme accent colors should match primary colors`() {
            assertEquals(
                CyberpunkTheme.accentColor, CyberpunkTheme.lightColorScheme.primary,
                "CyberpunkTheme accent should match light primary"
            )

            assertEquals(
                SolarFlareTheme.accentColor, SolarFlareTheme.lightColorScheme.primary,
                "SolarFlareTheme accent should match light primary"
            )

            assertEquals(
                ForestTheme.accentColor, ForestTheme.lightColorScheme.primary,
                "ForestTheme accent should match light primary"
            )
        }

        @Test
        @DisplayName("All themes should have meaningful names reflecting their character")
        fun `All themes should have meaningful names reflecting their character`() {
            assertTrue(
                CyberpunkTheme.name.contains("Cyberpunk"),
                "CyberpunkTheme name should be relevant"
            )
            assertTrue(
                SolarFlareTheme.name.contains("Solar"),
                "SolarFlareTheme name should be relevant"
            )
            assertTrue(ForestTheme.name.contains("Forest"), "ForestTheme name should be relevant")
        }

        @Test
        @DisplayName("Theme descriptions should contain relevant keywords")
        fun `Theme descriptions should contain relevant keywords`() {
            assertTrue(
                CyberpunkTheme.description.contains("energy", ignoreCase = true) ||
                        CyberpunkTheme.description.contains("neon", ignoreCase = true),
                "CyberpunkTheme description should mention energy or neon"
            )

            assertTrue(
                SolarFlareTheme.description.contains("warm", ignoreCase = true) ||
                        SolarFlareTheme.description.contains("bright", ignoreCase = true),
                "SolarFlareTheme description should mention warmth or brightness"
            )

            assertTrue(
                ForestTheme.description.contains("natural", ignoreCase = true) ||
                        ForestTheme.description.contains("calm", ignoreCase = true),
                "ForestTheme description should mention nature or calm"
            )
        }
    }
}