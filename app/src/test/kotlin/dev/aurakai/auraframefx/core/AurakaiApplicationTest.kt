package dev.aurakai.auraframefx.core

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File

/**
 * Tests for [AurakaiApplication].
 *
 * PR changes:
 * 1. Firebase initialization (Firebase.initialize(this)) was removed from onCreate().
 * 2. Firebase import was removed.
 *
 * These tests verify:
 * - The source code no longer contains Firebase.initialize() calls
 * - The Firebase import is absent from the application class
 * - Core behaviors like workManagerConfiguration and the langchain4j system property are preserved
 * - The application class structure is intact
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("AurakaiApplication Tests")
class AurakaiApplicationTest {

    private fun locateApplicationSource(): File {
        val candidates = listOf(
            File("app/src/main/java/dev/aurakai/auraframefx/core/AurakaiApplication.kt"),
            File("../app/src/main/java/dev/aurakai/auraframefx/core/AurakaiApplication.kt"),
            File("src/main/java/dev/aurakai/auraframefx/core/AurakaiApplication.kt")
        )
        return candidates.firstOrNull { it.exists() }
            ?: error(
                "Cannot locate AurakaiApplication.kt. CWD=${System.getProperty("user.dir")}. " +
                    "Checked: ${candidates.joinToString { it.path }}"
            )
    }

    private val applicationSource: String by lazy { locateApplicationSource().readText() }

    // ──────────────────────────────────────────────────────────────────────────
    // Firebase removal tests (PR change)
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Firebase Initialization Removal Tests (PR Change)")
    inner class FirebaseInitializationRemovalTests {

        @Test
        @DisplayName("Firebase.initialize() call should NOT be present in AurakaiApplication")
        fun firebaseInitializeShouldNotBePresentInOnCreate() {
            assertFalse(
                applicationSource.contains("Firebase.initialize"),
                "Firebase.initialize() should have been removed from AurakaiApplication.onCreate()"
            )
        }

        @Test
        @DisplayName("Firebase import should NOT be in AurakaiApplication")
        fun firebaseImportShouldNotBePresent() {
            assertFalse(
                applicationSource.contains("import com.google.firebase.Firebase"),
                "Firebase import should have been removed from AurakaiApplication.kt"
            )
        }

        @Test
        @DisplayName("Firebase initialize import should NOT be in AurakaiApplication")
        fun firebaseInitializeImportShouldNotBePresent() {
            assertFalse(
                applicationSource.contains("import com.google.firebase.initialize"),
                "Firebase initialize import should have been removed from AurakaiApplication.kt"
            )
        }

        @Test
        @DisplayName("Firebase initialization try-catch block should NOT be present")
        fun firebaseInitializationTryCatchShouldNotBePresent() {
            // The removed code had a try-catch around Firebase.initialize(this)
            // Verify the whole Firebase initialization block is gone
            assertFalse(
                applicationSource.contains("Firebase Initialized Successfully"),
                "Firebase initialization success log should have been removed"
            )
        }

        @Test
        @DisplayName("Firebase initialization warning message should NOT be present")
        fun firebaseInitializationWarningMessageShouldNotBePresent() {
            assertFalse(
                applicationSource.contains("Firebase initialization warning"),
                "Firebase initialization warning log should have been removed"
            )
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Preserved functionality tests (should still exist after PR)
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Preserved Application Functionality Tests")
    inner class PreservedFunctionalityTests {

        @Test
        @DisplayName("AurakaiApplication should still be annotated with @HiltAndroidApp")
        fun applicationShouldHaveHiltAnnotation() {
            assertTrue(
                applicationSource.contains("@HiltAndroidApp"),
                "AurakaiApplication should remain annotated with @HiltAndroidApp"
            )
        }

        @Test
        @DisplayName("AurakaiApplication should extend Application")
        fun applicationShouldExtendApplication() {
            assertTrue(
                applicationSource.contains(": Application()"),
                "AurakaiApplication should extend Application"
            )
        }

        @Test
        @DisplayName("AurakaiApplication should implement Configuration.Provider")
        fun applicationShouldImplementConfigurationProvider() {
            assertTrue(
                applicationSource.contains("Configuration.Provider"),
                "AurakaiApplication should implement Configuration.Provider for WorkManager"
            )
        }

        @Test
        @DisplayName("workManagerConfiguration should be present")
        fun workManagerConfigurationShouldBePresent() {
            assertTrue(
                applicationSource.contains("workManagerConfiguration"),
                "AurakaiApplication should override workManagerConfiguration"
            )
        }

        @Test
        @DisplayName("NexusMemoryCore.setRepository bridge call should be preserved")
        fun nexusMemoryCoreSetRepositoryBridgeShouldBePresent() {
            assertTrue(
                applicationSource.contains("NexusMemoryCore.setRepository"),
                "NexusMemoryCore bridge wiring should be preserved in onCreate()"
            )
        }

        @Test
        @DisplayName("setupLogging() call should be preserved in onCreate()")
        fun setupLoggingCallShouldBePresent() {
            assertTrue(
                applicationSource.contains("setupLogging()"),
                "setupLogging() call should remain in onCreate()"
            )
        }

        @Test
        @DisplayName("Integrity monitor should still be started")
        fun integrityMonitorStartShouldBePresent() {
            assertTrue(
                applicationSource.contains("startIntegrityMonitor()"),
                "startIntegrityMonitor() call should remain in onCreate()"
            )
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // System property for langchain4j (init block)
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("System Property Configuration Tests")
    inner class SystemPropertyConfigurationTests {

        @Test
        @DisplayName("langchain4j system property should be set in init block")
        fun langchain4jSystemPropertyShouldBeSet() {
            assertTrue(
                applicationSource.contains("langchain4j.http.clientBuilderFactory"),
                "langchain4j system property should be configured in init block"
            )
        }

        @Test
        @DisplayName("OkHttpClientBuilderFactory should be configured for langchain4j")
        fun okHttpClientBuilderFactoryShouldBeConfigured() {
            assertTrue(
                applicationSource.contains("OkHttpClientBuilderFactory"),
                "langchain4j should use OkHttpClientBuilderFactory"
            )
        }

        @Test
        @DisplayName("System.setProperty() call should exist in init block")
        fun systemSetPropertyShouldExist() {
            assertTrue(
                applicationSource.contains("System.setProperty("),
                "System.setProperty() should be called in the init block"
            )
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // WorkManagerConfiguration direct tests
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("WorkManagerConfiguration Source Validation Tests")
    inner class WorkManagerConfigurationSourceTests {

        @Test
        @DisplayName("workManagerConfiguration should use Configuration.Builder")
        fun workManagerConfigurationUsesBuilder() {
            assertTrue(
                applicationSource.contains("Configuration.Builder()"),
                "workManagerConfiguration should use Configuration.Builder()"
            )
        }

        @Test
        @DisplayName("workManagerConfiguration should set minimum logging level")
        fun workManagerConfigurationSetsMinimumLoggingLevel() {
            assertTrue(
                applicationSource.contains("setMinimumLoggingLevel"),
                "workManagerConfiguration should set a minimum logging level"
            )
        }

        @Test
        @DisplayName("workManagerConfiguration should use Log.INFO as minimum logging level")
        fun workManagerConfigurationUsesInfoLoggingLevel() {
            assertTrue(
                applicationSource.contains("Log.INFO"),
                "workManagerConfiguration should use Log.INFO as the minimum log level"
            )
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Regression tests
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Regression Tests")
    inner class RegressionTests {

        @Test
        @DisplayName("Firebase references should be entirely absent from AurakaiApplication")
        fun noFirebaseReferencesInApplication() {
            // Comprehensive check: no Firebase references should remain in this file
            assertFalse(
                applicationSource.contains("Firebase.initialize"),
                "Firebase.initialize should not be in AurakaiApplication"
            )
            assertFalse(
                applicationSource.contains("com.google.firebase.Firebase"),
                "Firebase class import should not be in AurakaiApplication"
            )
        }

        @Test
        @DisplayName("Genesis orchestrator initialization should still be present")
        fun genesisOrchestratorInitializationPreserved() {
            assertTrue(
                applicationSource.contains("orchestrator.initializePlatform()"),
                "Genesis orchestrator initialization should be preserved"
            )
        }

        @Test
        @DisplayName("Trinity consciousness synchronization should still be called")
        fun trinityConsciousnessSynchronizationPreserved() {
            assertTrue(
                applicationSource.contains("trinityCoordinatorService"),
                "Trinity coordinator service initialization should be preserved"
            )
        }

        @Test
        @DisplayName("ApplicationScope should use SupervisorJob for resilient initialization")
        fun applicationScopeUsesSupervisorJob() {
            assertTrue(
                applicationSource.contains("SupervisorJob"),
                "applicationScope should use SupervisorJob for resilient coroutine handling"
            )
        }
    }
}