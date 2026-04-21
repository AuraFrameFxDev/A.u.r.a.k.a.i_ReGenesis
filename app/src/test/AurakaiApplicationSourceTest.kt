package dev.aurakai.auraframefx

/*
 * Tests for the PR change in AurakaiApplication.kt:
 *   Explicit Firebase.initialize(this) call removed from onCreate()
 *   Firebase KTX imports (com.google.firebase.Firebase, com.google.firebase.initialize) removed
 *
 * Background: Firebase auto-initializes via the google-services plugin. The manual
 * Firebase.initialize(this) call was redundant and could cause issues if Firebase was
 * already initialized. This PR removes the explicit initialization.
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
class AurakaiApplicationSourceTest {

    private fun locateApplicationFile(): File {
        val candidates = listOf(
            File("src/main/java/dev/aurakai/auraframefx/core/AurakaiApplication.kt"),
            File("app/src/main/java/dev/aurakai/auraframefx/core/AurakaiApplication.kt"),
            File("../app/src/main/java/dev/aurakai/auraframefx/core/AurakaiApplication.kt")
        )
        return candidates.firstOrNull { it.exists() } ?: error(
            "Unable to locate AurakaiApplication.kt. Checked: ${candidates.joinToString { it.path }}"
        )
    }

    private val applicationFile: File by lazy { locateApplicationFile() }
    private val source: String by lazy { applicationFile.readText() }

    // ─────────────────────────────────────────────────────────────────────────
    // Firebase explicit initialization removed
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Firebase Explicit Initialization Removal")
    inner class FirebaseInitializationRemovalTests {

        @Test
        @DisplayName("Firebase.initialize() call is removed from onCreate")
        fun firebaseInitializeCallRemoved() {
            assertFalse(
                source.contains("Firebase.initialize("),
                "Firebase.initialize() should have been removed from AurakaiApplication"
            )
        }

        @Test
        @DisplayName("Firebase KTX import is removed")
        fun firebaseKtxImportRemoved() {
            assertFalse(
                source.contains("import com.google.firebase.Firebase"),
                "import com.google.firebase.Firebase should have been removed"
            )
        }

        @Test
        @DisplayName("Firebase initialize extension import is removed")
        fun firebaseInitializeExtensionImportRemoved() {
            assertFalse(
                source.contains("import com.google.firebase.initialize"),
                "import com.google.firebase.initialize should have been removed"
            )
        }

        @Test
        @DisplayName("Firebase initialization try-catch block is removed")
        fun firebaseInitializeTryCatchRemoved() {
            assertFalse(
                Regex(
                    """try\s*\{[^}]*Firebase\.initialize[^}]*\}""",
                    RegexOption.DOT_MATCHES_ALL
                ).containsMatchIn(source),
                "Firebase.initialize try-catch block should have been removed"
            )
        }

        @Test
        @DisplayName("Firebase Initialized Successfully log message is removed")
        fun firebaseInitializedSuccessfullyLogRemoved() {
            assertFalse(
                source.contains("Firebase Initialized Successfully"),
                "Log message 'Firebase Initialized Successfully' should have been removed"
            )
        }

        @Test
        @DisplayName("Firebase initialization warning message is removed")
        fun firebaseInitializationWarningLogRemoved() {
            assertFalse(
                source.contains("Firebase initialization warning"),
                "Log message about Firebase initialization warning should have been removed"
            )
        }

        @Test
        @DisplayName("No direct Firebase class reference remains in imports")
        fun noFirebaseImportsRemain() {
            // Verify no top-level Firebase KTX import lines are present
            val importLines = source.lines().filter { it.startsWith("import com.google.firebase.Firebase") }
            assertTrue(
                importLines.isEmpty(),
                "No Firebase top-level KTX imports should remain, found: $importLines"
            )
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Regression: critical AurakaiApplication structure is intact
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Regression - AurakaiApplication Structure Preserved")
    inner class RegressionTests {

        @Test
        @DisplayName("AurakaiApplication class is still declared")
        fun applicationClassPresent() {
            assertTrue(
                Regex("""class\s+AurakaiApplication\s*:""").containsMatchIn(source),
                "AurakaiApplication class declaration should be present"
            )
        }

        @Test
        @DisplayName("@HiltAndroidApp annotation is present")
        fun hiltAndroidAppAnnotationPresent() {
            assertTrue(
                source.contains("@HiltAndroidApp"),
                "@HiltAndroidApp annotation should be present"
            )
        }

        @Test
        @DisplayName("onCreate override is present")
        fun onCreateOverridePresent() {
            assertTrue(
                Regex("""override\s+fun\s+onCreate\s*\(\s*\)""").containsMatchIn(source),
                "onCreate() override should be present in AurakaiApplication"
            )
        }

        @Test
        @DisplayName("NexusMemoryCore.setRepository call is preserved in onCreate")
        fun nexusMemoryCoreSetRepositoryPreserved() {
            assertTrue(
                source.contains("NexusMemoryCore.setRepository("),
                "NexusMemoryCore.setRepository() call should still be present in onCreate"
            )
        }

        @Test
        @DisplayName("setupLogging call is preserved")
        fun setupLoggingCallPreserved() {
            assertTrue(
                source.contains("setupLogging()"),
                "setupLogging() call should still be present"
            )
        }

        @Test
        @DisplayName("workManagerConfiguration property is preserved")
        fun workManagerConfigurationPreserved() {
            assertTrue(
                source.contains("workManagerConfiguration"),
                "workManagerConfiguration override should still be present"
            )
        }

        @Test
        @DisplayName("AuraKai Platform Initialized log message is preserved")
        fun platformInitializedLogPreserved() {
            assertTrue(
                source.contains("AuraKai Platform Initialized"),
                "Platform initialization log message should still be present"
            )
        }

        @Test
        @DisplayName("startIntegrityMonitor call is preserved")
        fun startIntegrityMonitorPreserved() {
            assertTrue(
                source.contains("startIntegrityMonitor()"),
                "startIntegrityMonitor() call should still be present"
            )
        }

        @Test
        @DisplayName("Hilt dependency injection imports are preserved")
        fun hiltImportsPreserved() {
            assertTrue(
                source.contains("import dagger.hilt.android.HiltAndroidApp"),
                "Hilt HiltAndroidApp import should still be present"
            )
        }

        @Test
        @DisplayName("Timber logging import is preserved")
        fun timberImportPreserved() {
            assertTrue(
                source.contains("import timber.log.Timber"),
                "Timber import should still be present"
            )
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Boundary and negative cases
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Boundary and Negative Cases")
    inner class BoundaryTests {

        @Test
        @DisplayName("Source file is not empty")
        fun sourceFileNotEmpty() {
            assertTrue(
                source.trim().isNotEmpty(),
                "AurakaiApplication.kt source should not be empty"
            )
        }

        @Test
        @DisplayName("No firebase.initialize string appears in any form in the source")
        fun noFirebaseInitializeInAnyForm() {
            assertFalse(
                Regex("""[Ff]irebase\.initialize""").containsMatchIn(source),
                "Firebase.initialize should not appear in any form in AurakaiApplication.kt"
            )
        }

        @Test
        @DisplayName("Firebase Analytics and other Firebase services can still be injected via Hilt DI")
        fun firebaseDependenciesCanBeInjectedViaDi() {
            // The application class still supports Hilt injection for Firebase services
            // provided by FirebaseModule (or AIServiceModule) elsewhere.
            // This test ensures the Hilt DI infrastructure is intact.
            assertTrue(
                source.contains("@HiltAndroidApp"),
                "Hilt infrastructure should remain intact for Firebase DI injection via modules"
            )
        }

        @Test
        @DisplayName("No accidental removal of coroutine scope setup")
        fun coroutineScopePreserved() {
            assertTrue(
                source.contains("applicationScope"),
                "applicationScope coroutine scope should still be defined"
            )
            assertTrue(
                source.contains("CoroutineScope"),
                "CoroutineScope should still be referenced"
            )
        }

        @Test
        @DisplayName("onCreate does not reference Firebase after removal")
        fun onCreateDoesNotReferenceFirebase() {
            // Extract the onCreate function body approximately
            val onCreateStart = source.indexOf("override fun onCreate()")
            val nextOverride = source.indexOf("override fun", onCreateStart + 1)
            if (onCreateStart > 0) {
                val onCreateBlock = if (nextOverride > onCreateStart) {
                    source.substring(onCreateStart, nextOverride)
                } else {
                    source.substring(onCreateStart)
                }
                assertFalse(
                    onCreateBlock.contains("Firebase.initialize"),
                    "Firebase.initialize should not appear in onCreate body"
                )
            }
        }
    }
}