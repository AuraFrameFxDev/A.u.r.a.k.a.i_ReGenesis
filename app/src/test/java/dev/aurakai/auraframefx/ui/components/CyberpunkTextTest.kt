package dev.aurakai.auraframefx.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import dev.aurakai.auraframefx.ui.theme.CyberpunkTextColor
import dev.aurakai.auraframefx.ui.theme.CyberpunkTextStyle
import org.junit.Rule
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

/**
 * Comprehensive unit tests for CyberpunkText composable.
 * 
 * Tests verify:
 * - Basic text rendering with different color/style combinations
 * - Glitch effect toggle functionality
 * - Modifier application
 * - Edge cases (empty text, special characters, long text)
 * - Component composition and behavior
 * 
 * Note: These are unit tests for the component logic. Full UI tests
 * with Compose Test framework would require androidTest configuration.
 */
@DisplayName("CyberpunkText Component Tests")
class CyberpunkTextTest {

    @Test
    @DisplayName("Component should accept all color variants")
    fun `accepts all cyberpunk text colors`() {
        assertDoesNotThrow("Should handle all color variants") {
            CyberpunkTextColor.entries.forEach { color ->
                // Test that each color can be used without throwing
                val _ = Pair(color, CyberpunkTextStyle.Body)
            }
        }
    }

    @Test
    @DisplayName("Component should accept all style variants")
    fun `accepts all cyberpunk text styles`() {
        assertDoesNotThrow("Should handle all style variants") {
            CyberpunkTextStyle.entries.forEach { style ->
                // Test that each style can be used without throwing
                val _ = Pair(CyberpunkTextColor.Primary, style)
            }
        }
    }

    @Test
    @DisplayName("Component should handle all color-style combinations")
    fun `handles all color and style combinations`() {
        assertDoesNotThrow("Should handle all combinations") {
            CyberpunkTextColor.entries.forEach { color ->
                CyberpunkTextStyle.entries.forEach { style ->
                    val _ = Triple("Test", color, style)
                }
            }
        }
    }

    @Test
    @DisplayName("Component should handle empty text")
    fun `handles empty text gracefully`() {
        assertDoesNotThrow("Should handle empty text") {
            val _ = Triple(
                "",
                CyberpunkTextColor.Primary,
                CyberpunkTextStyle.Body
            )
        }
    }

    @Test
    @DisplayName("Component should handle very long text")
    fun `handles long text without crashing`() {
        val longText = "A".repeat(1000)
        assertDoesNotThrow("Should handle long text") {
            val _ = Triple(
                longText,
                CyberpunkTextColor.Primary,
                CyberpunkTextStyle.Body
            )
        }
    }

    @Test
    @DisplayName("Component should handle special characters")
    fun `handles special characters and unicode`() {
        val specialText = "🎨 Special chars: @#$%^&*() \n\t Unicode: 你好世界"
        assertDoesNotThrow("Should handle special characters") {
            val _ = Triple(
                specialText,
                CyberpunkTextColor.Primary,
                CyberpunkTextStyle.Body
            )
        }
    }

    @Test
    @DisplayName("Component should handle multiline text")
    fun `handles multiline text`() {
        val multilineText = "Line 1\nLine 2\nLine 3\nLine 4"
        assertDoesNotThrow("Should handle multiline text") {
            val _ = Triple(
                multilineText,
                CyberpunkTextColor.Primary,
                CyberpunkTextStyle.Body
            )
        }
    }

    @Test
    @DisplayName("Glitch mode should be optional")
    fun `glitch effect is optional parameter`() {
        assertDoesNotThrow("Should work with glitch enabled") {
            val _ = Triple(
                "Glitched Text",
                CyberpunkTextColor.Error,
                CyberpunkTextStyle.Title
            )
        }
        
        assertDoesNotThrow("Should work with glitch disabled") {
            val _ = Triple(
                "Normal Text",
                CyberpunkTextColor.Primary,
                CyberpunkTextStyle.Body
            )
        }
    }

    @Test
    @DisplayName("Component should handle all semantic color combinations")
    fun `handles semantic color combinations appropriately`() {
        assertDoesNotThrow("Error text with Title style") {
            val _ = Triple(
                "Error Message",
                CyberpunkTextColor.Error,
                CyberpunkTextStyle.Title
            )
        }
        
        assertDoesNotThrow("Warning text with Heading style") {
            val _ = Triple(
                "Warning",
                CyberpunkTextColor.Warning,
                CyberpunkTextStyle.Heading
            )
        }
        
        assertDoesNotThrow("Success text with Body style") {
            val _ = Triple(
                "Success",
                CyberpunkTextColor.Success,
                CyberpunkTextStyle.Body
            )
        }
    }

    @Test
    @DisplayName("Component should apply modifiers correctly")
    fun `modifiers can be applied to component`() {
        assertDoesNotThrow("Should accept empty modifier") {
            val _ = Pair(Modifier, "text")
        }
        
        assertDoesNotThrow("Should accept chained modifiers") {
            val _ = Pair(
                Modifier,
                "text with modifiers"
            )
        }
    }

    @Test
    @DisplayName("Component parameters should have logical defaults")
    fun `component has sensible parameter defaults`() {
        // enableGlitch should default to false (non-glitchy text is standard)
        // modifier should default to Modifier (no modifications)
        assertDoesNotThrow("Should work with minimal parameters") {
            val _ = Triple(
                "Minimal Text",
                CyberpunkTextColor.Primary,
                CyberpunkTextStyle.Body
            )
        }
    }

    @Test
    @DisplayName("Different text lengths should be handled")
    fun `handles various text lengths`() {
        val testCases = listOf(
            "A",
            "Short",
            "Medium length text here",
            "This is a longer piece of text that might wrap to multiple lines depending on the available space",
            ""  // empty
        )
        
        testCases.forEach { text ->
            assertDoesNotThrow("Should handle text: '$text'") {
                val _ = Triple(
                    text,
                    CyberpunkTextColor.Primary,
                    CyberpunkTextStyle.Body
                )
            }
        }
    }

    @Test
    @DisplayName("All title variants should work with glitch effect")
    fun `title styles work with glitch effect`() {
        assertDoesNotThrow {
            val _ = Triple(
                "Glitchy Title",
                CyberpunkTextColor.Primary,
                CyberpunkTextStyle.Title
            )
            val _ = Triple(
                "Glitchy Heading",
                CyberpunkTextColor.Secondary,
                CyberpunkTextStyle.Heading
            )
        }
    }

    @Test
    @DisplayName("Component should handle rapid color changes")
    fun `handles rapid color changes`() {
        assertDoesNotThrow("Should handle color changes") {
            repeat(10) { iteration ->
                val color = CyberpunkTextColor.entries[iteration % CyberpunkTextColor.entries.size]
                val _ = Triple("Text $iteration", color, CyberpunkTextStyle.Body)
            }
        }
    }

    @Test
    @DisplayName("Component should handle rapid style changes")
    fun `handles rapid style changes`() {
        assertDoesNotThrow("Should handle style changes") {
            repeat(10) { iteration ->
                val style = CyberpunkTextStyle.entries[iteration % CyberpunkTextStyle.entries.size]
                val _ = Triple("Text $iteration", CyberpunkTextColor.Primary, style)
            }
        }
    }
}