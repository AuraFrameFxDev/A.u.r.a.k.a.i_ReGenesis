package dev.aurakai.auraframefx

/*
 * Tests for the PR change in MainActivity.kt:
 *   requestedOrientation changed from SCREEN_ORIENTATION_PORTRAIT to SCREEN_ORIENTATION_UNSPECIFIED
 *
 * Background: The orientation was locked to portrait to prevent rotation. This PR removes
 * the portrait lock, allowing the system to determine the orientation freely.
 *
 * Framework: JUnit 5 (Jupiter)
 * Pattern: file-based text validation (consistent with other source validation tests)
 */

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MainActivitySourceTest {

    private fun locateMainActivity(): File {
        val candidates = listOf(
            File("src/main/java/dev/aurakai/auraframefx/MainActivity.kt"),
            File("app/src/main/java/dev/aurakai/auraframefx/MainActivity.kt"),
            File("../app/src/main/java/dev/aurakai/auraframefx/MainActivity.kt")
        )
        return candidates.firstOrNull { it.exists() } ?: error(
            "Unable to locate MainActivity.kt. Checked: ${candidates.joinToString { it.path }}"
        )
    }

    private val mainActivityFile: File by lazy { locateMainActivity() }
    private val source: String by lazy { mainActivityFile.readText() }

    // ─────────────────────────────────────────────────────────────────────────
    // Orientation constant changed from PORTRAIT to UNSPECIFIED
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Screen Orientation Change: PORTRAIT → UNSPECIFIED")
    inner class OrientationChangeTests {

        @Test
        @DisplayName("requestedOrientation is set to SCREEN_ORIENTATION_UNSPECIFIED")
        fun requestedOrientationIsUnspecified() {
            assertTrue(
                source.contains("SCREEN_ORIENTATION_UNSPECIFIED"),
                "requestedOrientation should be set to SCREEN_ORIENTATION_UNSPECIFIED"
            )
        }

        @Test
        @DisplayName("requestedOrientation is NOT set to SCREEN_ORIENTATION_PORTRAIT")
        fun requestedOrientationIsNotPortrait() {
            assertFalse(
                source.contains("SCREEN_ORIENTATION_PORTRAIT"),
                "requestedOrientation should no longer be SCREEN_ORIENTATION_PORTRAIT"
            )
        }

        @Test
        @DisplayName("requestedOrientation assignment uses the android.content.pm.ActivityInfo qualifier")
        fun requestedOrientationUsesFullyQualifiedConstant() {
            assertTrue(
                source.contains("android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED"),
                "requestedOrientation should use the fully qualified ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED"
            )
        }

        @Test
        @DisplayName("Only UNSPECIFIED orientation constant appears in orientation assignment line")
        fun orientationAssignmentLineContainsUnspecified() {
            val orientationLine = source.lines()
                .firstOrNull { it.contains("requestedOrientation") && it.contains("ActivityInfo.SCREEN_ORIENTATION") }

            assertTrue(
                orientationLine != null,
                "Should find a requestedOrientation assignment line"
            )
            assertTrue(
                orientationLine!!.contains("SCREEN_ORIENTATION_UNSPECIFIED"),
                "The orientation assignment line should contain SCREEN_ORIENTATION_UNSPECIFIED, was: $orientationLine"
            )
            assertFalse(
                orientationLine.contains("SCREEN_ORIENTATION_PORTRAIT"),
                "The orientation assignment line should not contain SCREEN_ORIENTATION_PORTRAIT"
            )
        }

        @Test
        @DisplayName("SCREEN_ORIENTATION_PORTRAIT does not appear anywhere in the file")
        fun portraitOrientationNotPresentAnywhere() {
            assertFalse(
                Regex("""SCREEN_ORIENTATION_PORTRAIT""").containsMatchIn(source),
                "SCREEN_ORIENTATION_PORTRAIT should not appear anywhere in MainActivity.kt"
            )
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Regression: critical MainActivity structure is intact
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Regression - MainActivity Structure Preserved")
    inner class RegressionTests {

        @Test
        @DisplayName("MainActivity class declaration is present")
        fun mainActivityClassDeclarationPresent() {
            assertTrue(
                Regex("""class\s+MainActivity\s*:""").containsMatchIn(source),
                "MainActivity class declaration should be present"
            )
        }

        @Test
        @DisplayName("@AndroidEntryPoint annotation is present")
        fun androidEntryPointAnnotationPresent() {
            assertTrue(
                source.contains("@AndroidEntryPoint"),
                "@AndroidEntryPoint annotation should be present on MainActivity"
            )
        }

        @Test
        @DisplayName("onCreate override is present")
        fun onCreateOverridePresent() {
            assertTrue(
                Regex("""override\s+fun\s+onCreate""").containsMatchIn(source),
                "onCreate override should be present in MainActivity"
            )
        }

        @Test
        @DisplayName("enableEdgeToEdge is still called after orientation change")
        fun enableEdgeToEdgeStillCalled() {
            assertTrue(
                source.contains("enableEdgeToEdge()"),
                "enableEdgeToEdge() should still be called in onCreate"
            )
        }

        @Test
        @DisplayName("setupFullscreenMode is still called")
        fun setupFullscreenModeStillCalled() {
            assertTrue(
                source.contains("setupFullscreenMode()"),
                "setupFullscreenMode() should still be called in onCreate"
            )
        }

        @Test
        @DisplayName("requestedOrientation assignment appears before enableEdgeToEdge in onCreate")
        fun orientationSetBeforeEdgeToEdge() {
            val orientationIndex = source.indexOf("requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED")
            val edgeToEdgeIndex = source.indexOf("enableEdgeToEdge()")

            assertTrue(orientationIndex > 0, "requestedOrientation assignment should be present")
            assertTrue(edgeToEdgeIndex > 0, "enableEdgeToEdge() call should be present")
            assertTrue(
                orientationIndex < edgeToEdgeIndex,
                "requestedOrientation should be set before enableEdgeToEdge() is called"
            )
        }

        @Test
        @DisplayName("onNewIntent override is present")
        fun onNewIntentPresent() {
            assertTrue(
                Regex("""override\s+fun\s+onNewIntent""").containsMatchIn(source),
                "onNewIntent override should be present"
            )
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Boundary / negative cases
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Boundary and Negative Cases")
    inner class BoundaryTests {

        @Test
        @DisplayName("No other locked orientation constants are set")
        fun noOtherLockedOrientationConstants() {
            // Verify no landscape lock was accidentally introduced
            assertFalse(
                source.contains("SCREEN_ORIENTATION_LANDSCAPE"),
                "SCREEN_ORIENTATION_LANDSCAPE should not be set in MainActivity"
            )
            assertFalse(
                source.contains("SCREEN_ORIENTATION_REVERSE_PORTRAIT"),
                "SCREEN_ORIENTATION_REVERSE_PORTRAIT should not be set in MainActivity"
            )
            assertFalse(
                source.contains("SCREEN_ORIENTATION_REVERSE_LANDSCAPE"),
                "SCREEN_ORIENTATION_REVERSE_LANDSCAPE should not be set in MainActivity"
            )
        }

        @Test
        @DisplayName("requestedOrientation is assigned exactly once in the source")
        fun requestedOrientationAssignedOnce() {
            val count = Regex("""requestedOrientation\s*=""").findAll(source).count()
            assertTrue(
                count == 1,
                "requestedOrientation should be assigned exactly once in MainActivity, found: $count"
            )
        }

        @Test
        @DisplayName("MainActivity source file is not empty")
        fun mainActivityFileNotEmpty() {
            assertTrue(
                source.trim().isNotEmpty(),
                "MainActivity.kt source should not be empty"
            )
        }
    }
}