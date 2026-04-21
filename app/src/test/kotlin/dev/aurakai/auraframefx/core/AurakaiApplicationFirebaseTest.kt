package dev.aurakai.auraframefx.core

/*
 * Tests validating the AurakaiApplication changes introduced in this pull request.
 *
 * Changes tested:
 *   - Firebase.initialize(this) call removed from onCreate()
 *   - com.google.firebase.Firebase import removed
 *   - com.google.firebase.initialize import removed
 *
 * The Firebase SDK auto-initializes through the google-services plugin when
 * google-services.json is present, so an explicit Firebase.initialize() call is
 * redundant. This PR removes that manual initialization.
 *
 * Approach:
 *   Source-file scanning to verify the Firebase initialization code is absent.
 *   This does not require an Android runtime and cleanly validates the specific
 *   code that was changed.
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
@DisplayName("AurakaiApplication — Firebase manual initialization removed")
class AurakaiApplicationFirebaseTest {

    private fun locateApplicationClass(): File {
        val candidates = listOf(
            // When tests run from the module root
            File("src/main/java/dev/aurakai/auraframefx/core/AurakaiApplication.kt"),
            // When tests run from the repo root
            File("app/src/main/java/dev/aurakai/auraframefx/core/AurakaiApplication.kt"),
            File("../app/src/main/java/dev/aurakai/auraframefx/core/AurakaiApplication.kt")
        )
        return candidates.firstOrNull { it.exists() } ?: error(
            "Unable to locate AurakaiApplication.kt. Checked: ${candidates.joinToString { it.path }}"
        )
    }

    private val source: String by lazy { locateApplicationClass().readText() }

    // ─── Firebase.initialize removed from imports ─────────────────────────────

    @Nested
    @DisplayName("Firebase import statements are absent")
    inner class FirebaseImportTests {

        @Test
        @DisplayName("com.google.firebase.Firebase import is absent")
        fun firebaseKtxImportAbsent() {
            assertFalse(
                source.contains("import com.google.firebase.Firebase"),
                "The 'import com.google.firebase.Firebase' statement should be removed"
            )
        }

        @Test
        @DisplayName("com.google.firebase.initialize import is absent")
        fun firebaseInitializeImportAbsent() {
            assertFalse(
                source.contains("import com.google.firebase.initialize"),
                "The 'import com.google.firebase.initialize' extension function import should be removed"
            )
        }

        @Test
        @DisplayName("No firebase.* imports are present in AurakaiApplication")
        fun noFirebaseImportsPresent() {
            val firebaseImportPattern = Regex("""^import com\.google\.firebase\.""", RegexOption.MULTILINE)
            assertFalse(
                firebaseImportPattern.containsMatchIn(source),
                "No com.google.firebase.* import statements should be present in AurakaiApplication"
            )
        }
    }

    // ─── Firebase.initialize() call removed from onCreate ────────────────────

    @Nested
    @DisplayName("Firebase.initialize() call is absent from onCreate")
    inner class FirebaseInitializeCallTests {

        @Test
        @DisplayName("Firebase.initialize() call is not present")
        fun firebaseInitializeCallAbsent() {
            assertFalse(
                source.contains("Firebase.initialize("),
                "Firebase.initialize() should not be called — auto-init handles this via google-services plugin"
            )
        }

        @Test
        @DisplayName("The try/catch block wrapping Firebase.initialize is absent")
        fun firebaseInitializeTryCatchAbsent() {
            assertFalse(
                Regex("""try\s*\{[^}]*Firebase\.initialize""", RegexOption.DOT_MATCHES_ALL)
                    .containsMatchIn(source),
                "The try/catch block around Firebase.initialize() should be absent"
            )
        }

        @Test
        @DisplayName("'Firebase Initialized Successfully' log message is absent")
        fun firebaseInitializedLogMessageAbsent() {
            assertFalse(
                source.contains("Firebase Initialized Successfully"),
                "The log message confirming Firebase initialization should be removed"
            )
        }

        @Test
        @DisplayName("'Firebase initialization warning' log message is absent")
        fun firebaseInitializationWarningLogAbsent() {
            assertFalse(
                source.contains("Firebase initialization warning"),
                "The Firebase initialization warning log should be removed"
            )
        }
    }

    // ─── Structural checks: critical Application code is intact ──────────────

    @Nested
    @DisplayName("Structural integrity of AurakaiApplication after the change")
    inner class StructuralIntegrityTests {

        @Test
        @DisplayName("Class is still annotated with @HiltAndroidApp")
        fun hiltAndroidAppAnnotationPresent() {
            assertTrue(
                source.contains("@HiltAndroidApp"),
                "@HiltAndroidApp annotation must still be present on AurakaiApplication"
            )
        }

        @Test
        @DisplayName("onCreate() override is still present")
        fun onCreateOverridePresent() {
            assertTrue(
                source.contains("override fun onCreate()"),
                "onCreate() override must still be present in AurakaiApplication"
            )
        }

        @Test
        @DisplayName("NexusMemoryCore.setRepository bridge call is still present")
        fun nexusMemoryCoreSetRepositoryPresent() {
            assertTrue(
                source.contains("NexusMemoryCore.setRepository("),
                "NexusMemoryCore.setRepository() bridge wiring must still be present after Firebase removal"
            )
        }

        @Test
        @DisplayName("Platform initialized log message is still present")
        fun platformInitializedLogPresent() {
            assertTrue(
                source.contains("AuraKai Platform Initialized"),
                "The 'AuraKai Platform Initialized' Timber.i() log must still be present"
            )
        }

        @Test
        @DisplayName("Application still implements Configuration.Provider")
        fun implementsWorkManagerConfigurationProvider() {
            assertTrue(
                source.contains("Configuration.Provider"),
                "AurakaiApplication must still implement Configuration.Provider for WorkManager"
            )
        }
    }

    // ─── Regression: NexusMemory and other init steps untouched ─────────────

    @Nested
    @DisplayName("Regression: non-Firebase initialization is not affected")
    inner class NonFirebaseInitTests {

        @Test
        @DisplayName("setupLogging() call remains in onCreate")
        fun setupLoggingCallPresent() {
            assertTrue(
                source.contains("setupLogging()"),
                "setupLogging() should still be called in onCreate"
            )
        }

        @Test
        @DisplayName("startIntegrityMonitor() call remains in onCreate")
        fun startIntegrityMonitorCallPresent() {
            assertTrue(
                source.contains("startIntegrityMonitor()"),
                "startIntegrityMonitor() should still be called in onCreate"
            )
        }

        @Test
        @DisplayName("applicationScope coroutine launch block is present")
        fun applicationScopeLaunchPresent() {
            assertTrue(
                source.contains("applicationScope.launch"),
                "applicationScope.launch for async init must still be present"
            )
        }

        @Test
        @DisplayName("Genesis Orchestrator initialization is still present")
        fun orchestratorInitPresent() {
            assertTrue(
                source.contains("orchestrator.initializePlatform()"),
                "Genesis Orchestrator initializePlatform() must still be called"
            )
        }

        @Test
        @DisplayName("LangChain4j HTTP client factory system property is still set")
        fun langChain4jHttpClientPropertyPresent() {
            assertTrue(
                source.contains("langchain4j.http.clientBuilderFactory"),
                "The LangChain4j HTTP client factory system property init must remain"
            )
        }
    }
}