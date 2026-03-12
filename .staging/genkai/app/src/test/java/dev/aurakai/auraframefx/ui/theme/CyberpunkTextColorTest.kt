package dev.aurakai.auraframefx.ui.theme

import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.ui.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

/**
 * Comprehensive unit tests for CyberpunkTextColor enum.
 * 
 * Tests verify:
 * - All enum values exist and are accessible
 * - Color mappings are correct and consistent
 * - Color properties (alpha, RGB values) are as expected
 * - Enum provides complete coverage of the cyberpunk color palette
 */
@DisplayName("CyberpunkTextColor Tests")
class CyberpunkTextColorTest {

    @Test
    @DisplayName("All enum values should be accessible")
    fun `all enum values exist`() {
        val values = CyberpunkTextColor.entries
        assertEquals(7, values.size, "Should have exactly 7 color variants")
        
        assertAll(
            "All color variants should be present",
            { assertNotNull(CyberpunkTextColor.Primary) },
            { assertNotNull(CyberpunkTextColor.Secondary) },
            { assertNotNull(CyberpunkTextColor.Tertiary) },
            { assertNotNull(CyberpunkTextColor.White) },
            { assertNotNull(CyberpunkTextColor.Warning) },
            { assertNotNull(CyberpunkTextColor.Error) },
            { assertNotNull(CyberpunkTextColor.Success) }
        )
    }

    @Test
    @DisplayName("Primary color should map to CyberpunkCyan")
    fun `primary color is cyberpunk cyan`() {
        val primaryColor = CyberpunkTextColor.Primary.color
        assertEquals(CyberpunkCyan, primaryColor)
        assertNotEquals(Color.Transparent, primaryColor)
        assertTrue(primaryColor.alpha > 0f, "Primary color should be visible")
    }

    @Test
    @DisplayName("Secondary color should map to CyberpunkPink")
    fun `secondary color is cyberpunk pink`() {
        val secondaryColor = CyberpunkTextColor.Secondary.color
        assertEquals(CyberpunkPink, secondaryColor)
        assertNotEquals(Color.Transparent, secondaryColor)
        assertTrue(secondaryColor.alpha > 0f, "Secondary color should be visible")
    }

    @Test
    @DisplayName("Tertiary color should map to CyberpunkPurple")
    fun `tertiary color is cyberpunk purple`() {
        val tertiaryColor = CyberpunkTextColor.Tertiary.color
        assertEquals(CyberpunkPurple, tertiaryColor)
        assertNotEquals(Color.Transparent, tertiaryColor)
        assertTrue(tertiaryColor.alpha > 0f, "Tertiary color should be visible")
    }

    @Test
    @DisplayName("White color should be pure white")
    fun `white color is standard white`() {
        val whiteColor = CyberpunkTextColor.White.color
        assertEquals(Color.White, whiteColor)
        assertEquals(1f, whiteColor.alpha, "White should be fully opaque")
        assertEquals(1f, whiteColor.red, "White should have max red")
        assertEquals(1f, whiteColor.green, "White should have max green")
        assertEquals(1f, whiteColor.blue, "White should have max blue")
    }

    @Test
    @DisplayName("Warning color should map to NeonYellow")
    fun `warning color is neon yellow`() {
        val warningColor = CyberpunkTextColor.Warning.color
        assertEquals(NeonYellow, warningColor)
        assertNotEquals(Color.Transparent, warningColor)
        assertTrue(warningColor.alpha > 0f, "Warning color should be visible")
    }

    @Test
    @DisplayName("Error color should map to NeonRed")
    fun `error color is neon red`() {
        val errorColor = CyberpunkTextColor.Error.color
        assertEquals(NeonRed, errorColor)
        assertNotEquals(Color.Transparent, errorColor)
        assertTrue(errorColor.alpha > 0f, "Error color should be visible")
    }

    @Test
    @DisplayName("Success color should map to NeonGreen")
    fun `success color is neon green`() {
        val successColor = CyberpunkTextColor.Success.color
        assertEquals(NeonGreen, successColor)
        assertNotEquals(Color.Transparent, successColor)
        assertTrue(successColor.alpha > 0f, "Success color should be visible")
    }

    @Test
    @DisplayName("All colors should be fully opaque")
    fun `all colors have full opacity`() {
        CyberpunkTextColor.entries.forEach { colorEnum ->
            assertEquals(
                1f,
                colorEnum.color.alpha,
                0.01f,
                "${colorEnum.name} should be fully opaque"
            )
        }
    }

    @Test
    @DisplayName("No two colors should be identical")
    fun `all colors are distinct`() {
        val colors = CyberpunkTextColor.entries.map { it.color }
        val uniqueColors = colors.toSet()
        assertEquals(
            colors.size,
            uniqueColors.size,
            "All color values should be unique"
        )
    }

    @Test
    @DisplayName("Enum valueOf should work correctly")
    fun `valueOf returns correct enum instances`() {
        assertAll(
            { assertEquals(CyberpunkTextColor.Primary, CyberpunkTextColor.valueOf("Primary")) },
            { assertEquals(CyberpunkTextColor.Secondary, CyberpunkTextColor.valueOf("Secondary")) },
            { assertEquals(CyberpunkTextColor.Warning, CyberpunkTextColor.valueOf("Warning")) },
            { assertEquals(CyberpunkTextColor.Error, CyberpunkTextColor.valueOf("Error")) }
        )
    }

    @Test
    @DisplayName("Enum should provide semantic color grouping")
    fun `semantic colors are logically grouped`() {
        // Status colors (Error, Warning, Success) should be distinct from theme colors
        val statusColors = setOf(
            CyberpunkTextColor.Error.color,
            CyberpunkTextColor.Warning.color,
            CyberpunkTextColor.Success.color
        )
        val themeColors = setOf(
            CyberpunkTextColor.Primary.color,
            CyberpunkTextColor.Secondary.color,
            CyberpunkTextColor.Tertiary.color
        )
        
        // No overlap between status and theme colors
        assertTrue(
            statusColors.intersect(themeColors).isEmpty(),
            "Status and theme colors should be distinct"
        )
    }

    @Test
    @DisplayName("Color values should be suitable for UI rendering")
    fun `colors are suitable for text rendering`() {
        CyberpunkTextColor.entries.forEach { colorEnum ->
            val color = colorEnum.color
            // All channels should be in valid range
            assertAll(
                { assertTrue(color.red in 0f..1f, "${colorEnum.name} red channel out of range") },
                { assertTrue(color.green in 0f..1f, "${colorEnum.name} green channel out of range") },
                { assertTrue(color.blue in 0f..1f, "${colorEnum.name} blue channel out of range") },
                { assertTrue(color.alpha in 0f..1f, "${colorEnum.name} alpha channel out of range") }
            )
        }
    }
}