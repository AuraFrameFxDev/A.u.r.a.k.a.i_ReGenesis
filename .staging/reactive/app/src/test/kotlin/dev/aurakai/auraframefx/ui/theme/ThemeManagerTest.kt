package dev.aurakai.auraframefx.ui.theme

import android.content.Context
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.aura.themes.getColorScheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

// Prefer MockK if available; otherwise fall back to Mockito's inline mock.
// We avoid introducing new deps: test should compile with either MockK or Mockito if one is on classpath.
@Suppress("UNCHECKED_CAST")
class ThemeManagerTest {

    // Provide a minimal fake Context to avoid dependency on Android runtime.
    // If Robolectric or Mockito/MockK is present, this can be replaced transparently at compile time.
    private class FakeContext : Context()

    private lateinit var context: Context
    private lateinit var themeManager: ThemeManager

    @BeforeEach
    fun setUp() {
        context = try {
            // Try to mock if Mockito is available
            val mockito = Class.forName("org.mockito.Mockito")
            val mockMethod = mockito.getMethod("mock", Class::class.java)
            mockMethod.invoke(null, Context::class.java) as Context
        } catch (_: Throwable) {
            try {
                // Try MockK if available
                Class.forName("io.mockk.MockK")
                // If class exists, create relaxed mock through reflection helper method
                // but if unavailable, fall back to FakeContext
                FakeContext()
            } catch (_: Throwable) {
                FakeContext()
            }
        }
        themeManager = ThemeManager(context)
    }

    @Test
    fun `applyTheme updates current configuration`() {
        val config = ThemeManager.ThemeConfig(
            isDarkMode = true,
            useSystemTheme = false,
            primaryColor = Color(0xFF0000FF),
            secondaryColor = Color(0xFF00FF00),
            accentColor = Color(0xFFFF0000)
        )

        themeManager.applyTheme(config)

        val current = themeManager.getCurrentTheme()
        assertTrue(current.isDarkMode)
        assertFalse(current.useSystemTheme)
        assertEquals(Color(0xFF0000FF), current.primaryColor)
        assertEquals(Color(0xFF00FF00), current.secondaryColor)
        assertEquals(Color(0xFFFF0000), current.accentColor)
    }

    @Test
    fun `toggleDarkMode flips isDarkMode and disables system theme`() {
        // Initial default from ThemeConfig(): isDarkMode=false, useSystemTheme=true
        var current = themeManager.getCurrentTheme()
        assertFalse(current.isDarkMode)
        assertTrue(current.useSystemTheme)

        themeManager.toggleDarkMode()
        current = themeManager.getCurrentTheme()
        assertTrue(current.isDarkMode)
        assertFalse("Toggling dark mode should disable system theme", current.useSystemTheme)

        // Toggle again to ensure it flips back
        themeManager.toggleDarkMode()
        current = themeManager.getCurrentTheme()
        assertFalse(current.isDarkMode)
        assertFalse("System theme remains disabled after manual toggles", current.useSystemTheme)
    }

    @Test
    fun `enableSystemTheme sets useSystemTheme true without altering colors or isDarkMode`() {
        // First, set a custom theme and flip dark mode
        themeManager.applyTheme(
            ThemeManager.ThemeConfig(
                isDarkMode = true,
                useSystemTheme = false,
                primaryColor = Color(0xFF123456),
                secondaryColor = Color(0xFF654321),
                accentColor = Color(0xFFABCDEF)
            )
        )
        val before = themeManager.getCurrentTheme()

        themeManager.enableSystemTheme()
        val after = themeManager.getCurrentTheme()

        assertTrue(after.useSystemTheme)
        assertEquals(
            "enableSystemTheme should not change isDarkMode",
            before.isDarkMode,
            after.isDarkMode
        )
        assertEquals(before.primaryColor, after.primaryColor)
        assertEquals(before.secondaryColor, after.secondaryColor)
        assertEquals(before.accentColor, after.accentColor)
    }

    @Test
    fun `setConsciousnessColors applies custom provided colors`() {
        themeManager.setConsciousnessColors(
            primary = Color(0xFF111111),
            secondary = Color(0xFF222222),
            accent = Color(0xFF333333)
        )
        val current = themeManager.getCurrentTheme()
        assertEquals(Color(0xFF111111), current.primaryColor)
        assertEquals(Color(0xFF222222), current.secondaryColor)
        assertEquals(Color(0xFF333333), current.accentColor)
    }

    @Test
    fun `setConsciousnessColors uses documented defaults when no args provided`() {
        // Defaults in production code:
        // primary = 0xFF9333EA, secondary = 0xFF0EA5E9, accent = 0xFF10B981
        themeManager.setConsciousnessColors()
        val current = themeManager.getCurrentTheme()
        assertEquals(Color(0xFF9333EA), current.primaryColor)
        assertEquals(Color(0xFF0EA5E9), current.secondaryColor)
        assertEquals(Color(0xFF10B981), current.accentColor)
    }

    @Test
    fun `getLockScreenTheme reflects dark mode and accent correctly (light mode)`() {
        // Ensure light mode
        val map = themeManager.getLockScreenTheme()

        assertEquals(false, map["isDarkMode"])
        assertEquals(Color.Black, map["clockColor"]) // Light mode -> Black clock
        assertEquals(Color.White, map["backgroundColor"]) // Light mode -> White background
        assertEquals(themeManager.getCurrentTheme().accentColor, map["accentColor"])
    }

    @Test
    fun `getLockScreenTheme reflects dark mode and accent correctly (dark mode)`() {
        themeManager.applyTheme(ThemeManager.ThemeConfig(isDarkMode = true))
        val map = themeManager.getLockScreenTheme()

        assertEquals(true, map["isDarkMode"])
        assertEquals(Color.White, map["clockColor"]) // Dark mode -> White clock
        assertEquals(Color.Black, map["backgroundColor"]) // Dark mode -> Black background
        assertEquals(themeManager.getCurrentTheme().accentColor, map["accentColor"])
    }

    // Composable test for getColorScheme():
    // We run the composable within a composition and capture the returned ColorScheme.
    @Test
    fun `getColorScheme returns lightColorScheme with configured colors when light mode`() {
        themeManager.applyTheme(
            ThemeManager.ThemeConfig(
                isDarkMode = false,
                primaryColor = Color(0xFF101010),
                secondaryColor = Color(0xFF202020),
                accentColor = Color(0xFF303030)
            )
        )

        // Minimal inline composition without relying on androidx.compose.ui.test.*,
        // to maximize compatibility with local unit tests:
        // We utilize Compose runtime directly if available.
        val scheme: ColorScheme = androidx.compose.runtime.testing.runComposeBlocking {
            themeManager.getColorScheme()
        }

        assertEquals(Color(0xFF101010), scheme.primary)
        assertEquals(Color(0xFF202020), scheme.secondary)
        assertEquals(Color(0xFF303030), scheme.tertiary)
    }

    @Test
    fun `getColorScheme returns darkColorScheme with configured colors when dark mode`() {
        themeManager.applyTheme(
            ThemeManager.ThemeConfig(
                isDarkMode = true,
                primaryColor = Color(0xFFAAAAAA),
                secondaryColor = Color(0xFFBBBBBB),
                accentColor = Color(0xFFCCCCCC)
            )
        )

        val scheme: ColorScheme = androidx.compose.runtime.testing.runComposeBlocking {
            themeManager.getColorScheme()
        }

        assertEquals(Color(0xFFAAAAAA), scheme.primary)
        assertEquals(Color(0xFFBBBBBB), scheme.secondary)
        assertEquals(Color(0xFFCCCCCC), scheme.tertiary)
    }
}

//
// Fallback alternative (commented) using Compose UI test rule if runtime-testing isn't present.
// Uncomment if the project has `androidx.compose.ui:ui-test-junit4` dependency.
//
// import androidx.compose.ui.test.junit4.createComposeRule
// import org.junit.Rule
//
// class ThemeManagerComposeRuleTest {
//     @get:Rule
//     val composeTestRule = createComposeRule()
//
//     private class FakeContext : android.content.Context()
//     private val themeManager = ThemeManager(FakeContext())
//
//     @Test
//     fun getColorScheme_withComposeRule_light() {
//         themeManager.applyTheme(
//             ThemeManager.ThemeConfig(
//                 isDarkMode = false,
//                 primaryColor = Color(0xFF101010),
//                 secondaryColor = Color(0xFF202020),
//                 accentColor = Color(0xFF303030)
//             )
//         )
//
//         var captured: ColorScheme? = null
//         composeTestRule.setContent {
//             captured = themeManager.getColorScheme()
//         }
//         composeTestRule.waitForIdle()
//         val scheme = requireNotNull(captured)
//         assertEquals(Color(0xFF101010), scheme.primary)
//         assertEquals(Color(0xFF202020), scheme.secondary)
//         assertEquals(Color(0xFF303030), scheme.tertiary)
//     }
// }
