package dev.aurakai.auraframefx.ui.theme

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

/**
 * Comprehensive unit tests for CyberpunkTextStyle enum.
 * 
 * Tests verify:
 * - All text styles exist and are properly configured
 * - Typography properties (fontSize, fontWeight, letterSpacing) are correct
 * - Style hierarchy follows design system conventions
 * - Edge cases and boundary conditions are handled
 */
@DisplayName("CyberpunkTextStyle Tests")
class CyberpunkTextStyleTest {

    @Test
    @DisplayName("All enum values should be accessible")
    fun `all enum values exist`() {
        val values = CyberpunkTextStyle.entries
        assertEquals(5, values.size, "Should have exactly 5 text style variants")
        
        assertAll(
            "All style variants should be present",
            { assertNotNull(CyberpunkTextStyle.Title) },
            { assertNotNull(CyberpunkTextStyle.Heading) },
            { assertNotNull(CyberpunkTextStyle.Body) },
            { assertNotNull(CyberpunkTextStyle.Label) },
            { assertNotNull(CyberpunkTextStyle.Caption) }
        )
    }

    @Test
    @DisplayName("Title style should have largest font size")
    fun `title has largest font size`() {
        val titleStyle = CyberpunkTextStyle.Title.textStyle
        assertEquals(24.sp, titleStyle.fontSize)
        assertEquals(FontWeight.Bold, titleStyle.fontWeight)
        assertEquals(1.sp, titleStyle.letterSpacing)
    }

    @Test
    @DisplayName("Heading style should be smaller than Title")
    fun `heading is smaller than title`() {
        val headingStyle = CyberpunkTextStyle.Heading.textStyle
        assertEquals(20.sp, headingStyle.fontSize)
        assertEquals(FontWeight.Bold, headingStyle.fontWeight)
        assertEquals(0.75.sp, headingStyle.letterSpacing)
        
        assertTrue(
            headingStyle.fontSize < CyberpunkTextStyle.Title.textStyle.fontSize,
            "Heading should be smaller than Title"
        )
    }

    @Test
    @DisplayName("Body style should use normal font weight")
    fun `body uses normal font weight`() {
        val bodyStyle = CyberpunkTextStyle.Body.textStyle
        assertEquals(16.sp, bodyStyle.fontSize)
        assertEquals(FontWeight.Normal, bodyStyle.fontWeight)
        assertEquals(0.5.sp, bodyStyle.letterSpacing)
    }

    @Test
    @DisplayName("Label style should be medium weight")
    fun `label uses medium font weight`() {
        val labelStyle = CyberpunkTextStyle.Label.textStyle
        assertEquals(14.sp, labelStyle.fontSize)
        assertEquals(FontWeight.Medium, labelStyle.fontWeight)
        assertEquals(0.5.sp, labelStyle.letterSpacing)
    }

    @Test
    @DisplayName("Caption should have smallest font size")
    fun `caption has smallest font size`() {
        val captionStyle = CyberpunkTextStyle.Caption.textStyle
        assertEquals(12.sp, captionStyle.fontSize)
        assertEquals(FontWeight.Normal, captionStyle.fontWeight)
        assertEquals(0.25.sp, captionStyle.letterSpacing)
        
        // Caption should be smallest
        CyberpunkTextStyle.entries.forEach { style ->
            assertTrue(
                captionStyle.fontSize <= style.textStyle.fontSize,
                "Caption should be smallest or equal to ${style.name}"
            )
        }
    }

    @Test
    @DisplayName("Font sizes should follow logical hierarchy")
    fun `font sizes follow descending order`() {
        val sizes = listOf(
            CyberpunkTextStyle.Title.textStyle.fontSize,
            CyberpunkTextStyle.Heading.textStyle.fontSize,
            CyberpunkTextStyle.Body.textStyle.fontSize,
            CyberpunkTextStyle.Label.textStyle.fontSize,
            CyberpunkTextStyle.Caption.textStyle.fontSize
        )
        
        // Verify descending order
        for (i in 0 until sizes.size - 1) {
            assertTrue(
                sizes[i] >= sizes[i + 1],
                "Font size at index $i should be >= size at ${i + 1}"
            )
        }
    }

    @Test
    @DisplayName("Letter spacing should be positive for all styles")
    fun `letter spacing is positive for all styles`() {
        CyberpunkTextStyle.entries.forEach { style ->
            assertTrue(
                style.textStyle.letterSpacing.value >= 0,
                "${style.name} should have non-negative letter spacing"
            )
        }
    }

    @Test
    @DisplayName("Bold styles should use Bold font weight")
    fun `bold styles use bold weight`() {
        assertAll(
            "Title and Heading should be bold",
            {
                assertEquals(
                    FontWeight.Bold,
                    CyberpunkTextStyle.Title.textStyle.fontWeight,
                    "Title should be bold"
                )
            },
            {
                assertEquals(
                    FontWeight.Bold,
                    CyberpunkTextStyle.Heading.textStyle.fontWeight,
                    "Heading should be bold"
                )
            }
        )
    }

    @Test
    @DisplayName("Normal weight styles should not be bold")
    fun `normal weight styles are not bold`() {
        assertAll(
            "Body and Caption should not be bold",
            {
                assertEquals(
                    FontWeight.Normal,
                    CyberpunkTextStyle.Body.textStyle.fontWeight,
                    "Body should be normal weight"
                )
            },
            {
                assertEquals(
                    FontWeight.Normal,
                    CyberpunkTextStyle.Caption.textStyle.fontWeight,
                    "Caption should be normal weight"
                )
            }
        )
    }

    @Test
    @DisplayName("Label should use medium font weight")
    fun `label uses distinct medium weight`() {
        val labelWeight = CyberpunkTextStyle.Label.textStyle.fontWeight
        assertEquals(FontWeight.Medium, labelWeight)
        assertNotEquals(FontWeight.Bold, labelWeight)
        assertNotEquals(FontWeight.Normal, labelWeight)
    }

    @Test
    @DisplayName("All styles should have defined text properties")
    fun `all styles have complete text properties`() {
        CyberpunkTextStyle.entries.forEach { style ->
            val textStyle = style.textStyle
            assertAll(
                "${style.name} should have complete properties",
                { assertNotNull(textStyle.fontSize, "fontSize should be defined") },
                { assertNotNull(textStyle.fontWeight, "fontWeight should be defined") },
                { assertNotNull(textStyle.letterSpacing, "letterSpacing should be defined") }
            )
        }
    }

    @Test
    @DisplayName("Enum valueOf should work correctly")
    fun `valueOf returns correct enum instances`() {
        assertAll(
            { assertEquals(CyberpunkTextStyle.Title, CyberpunkTextStyle.valueOf("Title")) },
            { assertEquals(CyberpunkTextStyle.Heading, CyberpunkTextStyle.valueOf("Heading")) },
            { assertEquals(CyberpunkTextStyle.Body, CyberpunkTextStyle.valueOf("Body")) },
            { assertEquals(CyberpunkTextStyle.Label, CyberpunkTextStyle.valueOf("Label")) },
            { assertEquals(CyberpunkTextStyle.Caption, CyberpunkTextStyle.valueOf("Caption")) }
        )
    }

    @Test
    @DisplayName("Font sizes should be reasonable for UI")
    fun `font sizes are in reasonable range for UI`() {
        CyberpunkTextStyle.entries.forEach { style ->
            val fontSize = style.textStyle.fontSize.value
            assertTrue(
                fontSize in 10f..30f,
                "${style.name} font size $fontSize should be in reasonable range (10-30sp)"
            )
        }
    }

    @Test
    @DisplayName("Letter spacing should increase with font size for large text")
    fun `larger fonts have more letter spacing`() {
        val titleSpacing = CyberpunkTextStyle.Title.textStyle.letterSpacing.value
        val captionSpacing = CyberpunkTextStyle.Caption.textStyle.letterSpacing.value
        
        assertTrue(
            titleSpacing > captionSpacing,
            "Title should have more letter spacing than Caption"
        )
    }

    @Test
    @DisplayName("Styles should provide semantic meaning")
    fun `style names reflect semantic purpose`() {
        // Verify that style names make sense for their properties
        assertAll(
            { 
                assertTrue(
                    CyberpunkTextStyle.Title.textStyle.fontSize > CyberpunkTextStyle.Body.textStyle.fontSize,
                    "Title should be larger than Body"
                )
            },
            {
                assertTrue(
                    CyberpunkTextStyle.Caption.textStyle.fontSize < CyberpunkTextStyle.Body.textStyle.fontSize,
                    "Caption should be smaller than Body"
                )
            }
        )
    }
}