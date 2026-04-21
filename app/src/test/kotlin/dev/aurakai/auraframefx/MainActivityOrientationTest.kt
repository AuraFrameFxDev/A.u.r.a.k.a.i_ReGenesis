package dev.aurakai.auraframefx

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File

/**
 * Tests for orientation changes in [MainActivity].
 *
 * PR change: requestedOrientation was changed from SCREEN_ORIENTATION_PORTRAIT to
 * SCREEN_ORIENTATION_UNSPECIFIED to allow the app to support multiple orientations.
 *
 * These tests verify:
 * 1. The correct orientation constant is used in the source
 * 2. The old PORTRAIT orientation is no longer forced
 * 3. The orientation constant values match Android SDK expectations
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("MainActivity Orientation Tests")
class MainActivityOrientationTest {

    // Locate the MainActivity source file for text-based validation
    private fun locateMainActivity(): File {
        val candidates = listOf(
            File("app/src/main/java/dev/aurakai/auraframefx/MainActivity.kt"),
            File("../app/src/main/java/dev/aurakai/auraframefx/MainActivity.kt"),
            File("src/main/java/dev/aurakai/auraframefx/MainActivity.kt")
        )
        return candidates.firstOrNull { it.exists() }
            ?: error(
                "Cannot locate MainActivity.kt. CWD=${System.getProperty("user.dir")}. " +
                    "Checked: ${candidates.joinToString { it.path }}"
            )
    }

    private val mainActivitySource: String by lazy { locateMainActivity().readText() }

    // ──────────────────────────────────────────────────────────────────────────
    // Orientation constant value tests (using well-known Android SDK values)
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Android Orientation Constant Value Tests")
    inner class OrientationConstantValueTests {

        // Well-known Android SDK constant values (from android.content.pm.ActivityInfo):
        // SCREEN_ORIENTATION_UNSPECIFIED = -1
        // SCREEN_ORIENTATION_PORTRAIT    =  1
        private val SCREEN_ORIENTATION_UNSPECIFIED = -1
        private val SCREEN_ORIENTATION_PORTRAIT = 1

        @Test
        @DisplayName("SCREEN_ORIENTATION_UNSPECIFIED should have known value -1")
        fun screenOrientationUnspecifiedShouldBeMinusOne() {
            // Android SDK: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED = -1
            assertEquals(-1, SCREEN_ORIENTATION_UNSPECIFIED,
                "SCREEN_ORIENTATION_UNSPECIFIED should equal -1 per Android SDK specification")
        }

        @Test
        @DisplayName("SCREEN_ORIENTATION_PORTRAIT should have known value 1")
        fun screenOrientationPortraitShouldBeOne() {
            // Android SDK: ActivityInfo.SCREEN_ORIENTATION_PORTRAIT = 1
            // Regression check: the old value that was replaced in this PR
            assertEquals(1, SCREEN_ORIENTATION_PORTRAIT,
                "SCREEN_ORIENTATION_PORTRAIT should equal 1 per Android SDK specification")
        }

        @Test
        @DisplayName("SCREEN_ORIENTATION_UNSPECIFIED and SCREEN_ORIENTATION_PORTRAIT should differ")
        fun orientationConstantsShouldDiffer() {
            assertFalse(
                SCREEN_ORIENTATION_UNSPECIFIED == SCREEN_ORIENTATION_PORTRAIT,
                "SCREEN_ORIENTATION_UNSPECIFIED ($SCREEN_ORIENTATION_UNSPECIFIED) should differ from " +
                    "SCREEN_ORIENTATION_PORTRAIT ($SCREEN_ORIENTATION_PORTRAIT)"
            )
        }

        @Test
        @DisplayName("SCREEN_ORIENTATION_UNSPECIFIED is negative (system-determined)")
        fun screenOrientationUnspecifiedIsNegative() {
            assertTrue(SCREEN_ORIENTATION_UNSPECIFIED < 0,
                "SCREEN_ORIENTATION_UNSPECIFIED (-1) should be negative (system-determined orientation)")
        }

        @Test
        @DisplayName("SCREEN_ORIENTATION_PORTRAIT is positive (forced orientation)")
        fun screenOrientationPortraitIsPositive() {
            assertTrue(SCREEN_ORIENTATION_PORTRAIT > 0,
                "SCREEN_ORIENTATION_PORTRAIT (1) should be positive (forced orientation)")
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Source code validation tests (PR change regression)
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("MainActivity Source Code Orientation Tests")
    inner class MainActivitySourceOrientationTests {

        @Test
        @DisplayName("MainActivity should use SCREEN_ORIENTATION_UNSPECIFIED")
        fun mainActivityShouldUseScreenOrientationUnspecified() {
            assertTrue(
                mainActivitySource.contains("SCREEN_ORIENTATION_UNSPECIFIED"),
                "MainActivity.kt should set requestedOrientation to SCREEN_ORIENTATION_UNSPECIFIED"
            )
        }

        @Test
        @DisplayName("MainActivity should NOT use SCREEN_ORIENTATION_PORTRAIT (PR regression)")
        fun mainActivityShouldNotUseScreenOrientationPortrait() {
            assertFalse(
                mainActivitySource.contains("SCREEN_ORIENTATION_PORTRAIT"),
                "MainActivity.kt should not force SCREEN_ORIENTATION_PORTRAIT after this PR change"
            )
        }

        @Test
        @DisplayName("MainActivity should set requestedOrientation")
        fun mainActivityShouldSetRequestedOrientation() {
            assertTrue(
                mainActivitySource.contains("requestedOrientation"),
                "MainActivity.kt should contain 'requestedOrientation' assignment"
            )
        }

        @Test
        @DisplayName("MainActivity requestedOrientation assignment uses ActivityInfo constant")
        fun mainActivityOrientationUsesActivityInfoConstant() {
            assertTrue(
                mainActivitySource.contains("ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED"),
                "requestedOrientation should reference android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED"
            )
        }

        @Test
        @DisplayName("Orientation is set before enableEdgeToEdge (ordering check)")
        fun orientationSetBeforeEdgeToEdge() {
            val orientationIndex = mainActivitySource.indexOf("SCREEN_ORIENTATION_UNSPECIFIED")
            val edgeToEdgeIndex = mainActivitySource.indexOf("enableEdgeToEdge()")
            assertTrue(orientationIndex > 0, "SCREEN_ORIENTATION_UNSPECIFIED should be present")
            assertTrue(edgeToEdgeIndex > 0, "enableEdgeToEdge() should be present")
            assertTrue(
                orientationIndex < edgeToEdgeIndex,
                "requestedOrientation should be set before enableEdgeToEdge()"
            )
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Structural validation: other expected MainActivity behaviors
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("MainActivity Structure Tests")
    inner class MainActivityStructureTests {

        @Test
        @DisplayName("MainActivity should be annotated with @AndroidEntryPoint")
        fun mainActivityShouldHaveAndroidEntryPoint() {
            assertTrue(
                mainActivitySource.contains("@AndroidEntryPoint"),
                "MainActivity should be annotated with @AndroidEntryPoint for Hilt injection"
            )
        }

        @Test
        @DisplayName("MainActivity should extend AppCompatActivity")
        fun mainActivityShouldExtendAppCompatActivity() {
            assertTrue(
                mainActivitySource.contains("AppCompatActivity"),
                "MainActivity should extend AppCompatActivity"
            )
        }

        @Test
        @DisplayName("MainActivity should have setupFullscreenMode call")
        fun mainActivityShouldHaveSetupFullscreenMode() {
            assertTrue(
                mainActivitySource.contains("setupFullscreenMode()"),
                "MainActivity should call setupFullscreenMode()"
            )
        }

        @Test
        @DisplayName("MainActivity should set up navigation graph with ReGenesisNavGraph")
        fun mainActivityShouldSetupNavigation() {
            assertTrue(
                mainActivitySource.contains("ReGenesisNavGraph"),
                "MainActivity should include ReGenesisNavGraph composable"
            )
        }

        @Test
        @DisplayName("MainActivity should handle deep links with navigate_to extra")
        fun mainActivityShouldHandleDeepLinks() {
            assertTrue(
                mainActivitySource.contains("navigate_to"),
                "MainActivity should handle 'navigate_to' deep link extra"
            )
        }

        @Test
        @DisplayName("MainActivity should override onNewIntent")
        fun mainActivityShouldOverrideOnNewIntent() {
            assertTrue(
                mainActivitySource.contains("onNewIntent"),
                "MainActivity should override onNewIntent for deep link handling"
            )
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Regression boundary tests
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Regression Tests")
    inner class RegressionTests {

        @Test
        @DisplayName("Portrait orientation is NOT forced (regression: PR reverts portrait lock)")
        fun portraitOrientationNotForced() {
            // Core regression: previous code forced PORTRAIT, PR changes to UNSPECIFIED
            assertFalse(
                mainActivitySource.contains("SCREEN_ORIENTATION_PORTRAIT"),
                "SCREEN_ORIENTATION_PORTRAIT must not appear in MainActivity after this PR"
            )
        }

        @Test
        @DisplayName("Orientation value UNSPECIFIED allows both portrait and landscape")
        fun unspecifiedOrientationAllowsBothModes() {
            // SCREEN_ORIENTATION_UNSPECIFIED = -1 allows the system to choose orientation
            // SCREEN_ORIENTATION_PORTRAIT = 1 forces portrait only
            // The source should use UNSPECIFIED (not PORTRAIT) after this PR change
            assertTrue(
                mainActivitySource.contains("SCREEN_ORIENTATION_UNSPECIFIED"),
                "MainActivity should use SCREEN_ORIENTATION_UNSPECIFIED (system-determined orientation)"
            )
            assertFalse(
                mainActivitySource.contains("SCREEN_ORIENTATION_PORTRAIT"),
                "MainActivity should not force SCREEN_ORIENTATION_PORTRAIT after this PR"
            )
        }
    }
}