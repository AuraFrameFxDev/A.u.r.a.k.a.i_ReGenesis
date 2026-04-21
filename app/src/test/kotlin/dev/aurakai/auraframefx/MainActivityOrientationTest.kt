package dev.aurakai.auraframefx

/*
 * Tests validating the MainActivity orientation change introduced in this pull request.
 *
 * Change tested:
 *   requestedOrientation was changed from SCREEN_ORIENTATION_PORTRAIT to
 *   SCREEN_ORIENTATION_UNSPECIFIED, allowing the system to choose orientation freely.
 *
 * Approach:
 *   Source-file scanning to verify the correct orientation constant is used. This avoids
 *   the complexity of a full Hilt/Robolectric activity launch while still pinning the
 *   specific change that was made.
 *
 * Framework: JUnit 5 (Jupiter)
 */

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("MainActivity — screen-orientation constant")
class MainActivityOrientationTest {

    private fun locateMainActivity(): File {
        val candidates = listOf(
            // When tests run from the module root
            File("src/main/java/dev/aurakai/auraframefx/MainActivity.kt"),
            // When tests run from the repo root
            File("app/src/main/java/dev/aurakai/auraframefx/MainActivity.kt"),
            File("../app/src/main/java/dev/aurakai/auraframefx/MainActivity.kt")
        )
        return candidates.firstOrNull { it.exists() } ?: error(
            "Unable to locate MainActivity.kt. Checked: ${candidates.joinToString { it.path }}"
        )
    }

    private val source: String by lazy { locateMainActivity().readText() }

    // ─── Happy-path: SCREEN_ORIENTATION_UNSPECIFIED is used ──────────────────

    @Nested
    @DisplayName("SCREEN_ORIENTATION_UNSPECIFIED is set (new behaviour)")
    inner class OrientationUnspecifiedTests {

        @Test
        @DisplayName("requestedOrientation assignment uses SCREEN_ORIENTATION_UNSPECIFIED")
        fun requestedOrientationUsesUnspecified() {
            assertTrue(
                source.contains("SCREEN_ORIENTATION_UNSPECIFIED"),
                "MainActivity must set requestedOrientation to SCREEN_ORIENTATION_UNSPECIFIED"
            )
        }

        @Test
        @DisplayName("The full ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED expression is present")
        fun fullActivityInfoUnspecifiedExpressionPresent() {
            assertTrue(
                source.contains("android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED"),
                "Expected the full expression android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED"
            )
        }

        @Test
        @DisplayName("requestedOrientation is assigned the UNSPECIFIED constant (not just referenced)")
        fun requestedOrientationIsAssigned() {
            assertTrue(
                Regex("""requestedOrientation\s*=\s*android\.content\.pm\.ActivityInfo\.SCREEN_ORIENTATION_UNSPECIFIED""")
                    .containsMatchIn(source),
                "requestedOrientation should be directly assigned SCREEN_ORIENTATION_UNSPECIFIED"
            )
        }
    }

    // ─── Regression-guard: SCREEN_ORIENTATION_PORTRAIT must not appear ────────

    @Nested
    @DisplayName("SCREEN_ORIENTATION_PORTRAIT is absent (old behaviour removed)")
    inner class OrientationPortraitRemovedTests {

        @Test
        @DisplayName("SCREEN_ORIENTATION_PORTRAIT constant is not referenced in any form")
        fun screenOrientationPortraitAbsent() {
            assertFalse(
                source.contains("SCREEN_ORIENTATION_PORTRAIT"),
                "SCREEN_ORIENTATION_PORTRAIT should not appear — orientation is now UNSPECIFIED"
            )
        }

        @Test
        @DisplayName("requestedOrientation is not assigned SCREEN_ORIENTATION_PORTRAIT")
        fun requestedOrientationNotPortrait() {
            assertFalse(
                Regex("""requestedOrientation\s*=\s*.*SCREEN_ORIENTATION_PORTRAIT""")
                    .containsMatchIn(source),
                "requestedOrientation must not be set to SCREEN_ORIENTATION_PORTRAIT"
            )
        }
    }

    // ─── Structural checks: the surrounding onCreate code is intact ───────────

    @Nested
    @DisplayName("Structural integrity of onCreate after the change")
    inner class StructuralIntegrityTests {

        @Test
        @DisplayName("enableEdgeToEdge() call follows the orientation assignment")
        fun enableEdgeToEdgeFollowsOrientationAssignment() {
            val orientationIndex = source.indexOf("SCREEN_ORIENTATION_UNSPECIFIED")
            val edgeToEdgeIndex = source.indexOf("enableEdgeToEdge()")
            assertTrue(orientationIndex >= 0, "SCREEN_ORIENTATION_UNSPECIFIED must be present")
            assertTrue(edgeToEdgeIndex >= 0, "enableEdgeToEdge() must be present")
            assertTrue(
                edgeToEdgeIndex > orientationIndex,
                "enableEdgeToEdge() should appear after the requestedOrientation assignment"
            )
        }

        @Test
        @DisplayName("setupFullscreenMode() call is still present in onCreate")
        fun setupFullscreenModeCallPresent() {
            assertTrue(
                source.contains("setupFullscreenMode()"),
                "setupFullscreenMode() must still be called in onCreate"
            )
        }

        @Test
        @DisplayName("Class still extends AppCompatActivity")
        fun classExtendsAppCompatActivity() {
            assertTrue(
                source.contains(": AppCompatActivity()"),
                "MainActivity should still extend AppCompatActivity"
            )
        }

        @Test
        @DisplayName("onCreate override is still present")
        fun onCreateOverridePresent() {
            assertTrue(
                source.contains("override fun onCreate(savedInstanceState: Bundle?)"),
                "onCreate(savedInstanceState: Bundle?) override must be present"
            )
        }
    }

    // ─── Edge-case: no other hardcoded orientation constant anywhere ──────────

    @Nested
    @DisplayName("No other hardcoded portrait-only orientation constants")
    inner class NoOtherPortraitOrientationTests {

        @Test
        @DisplayName("SCREEN_ORIENTATION_SENSOR_PORTRAIT is not used")
        fun sensorPortraitAbsent() {
            assertFalse(
                source.contains("SCREEN_ORIENTATION_SENSOR_PORTRAIT"),
                "SCREEN_ORIENTATION_SENSOR_PORTRAIT should not be used"
            )
        }

        @Test
        @DisplayName("SCREEN_ORIENTATION_REVERSE_PORTRAIT is not used")
        fun reversePortraitAbsent() {
            assertFalse(
                source.contains("SCREEN_ORIENTATION_REVERSE_PORTRAIT"),
                "SCREEN_ORIENTATION_REVERSE_PORTRAIT should not be used"
            )
        }

        @Test
        @DisplayName("SCREEN_ORIENTATION_USER_PORTRAIT is not used")
        fun userPortraitAbsent() {
            assertFalse(
                source.contains("SCREEN_ORIENTATION_USER_PORTRAIT"),
                "SCREEN_ORIENTATION_USER_PORTRAIT should not be used"
            )
        }
    }
}