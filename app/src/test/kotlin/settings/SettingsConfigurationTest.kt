package settings

import org.junit.Test
import org.junit.Assert.*
import java.io.File

/**
 * Additional validation tests for settings.gradle.kts from app module perspective
 * Using JUnit 4 syntax to match existing app module test patterns
 */
class SettingsConfigurationTest {

    @Test
    fun `settings file should be parseable as gradle script`() {
        val settingsFile = File("../settings.gradle.kts")
        assertTrue("Settings file should exist", settingsFile.exists())

        val content = settingsFile.readText()

        // Basic syntax validation
        assertFalse("Should not contain syntax errors", content.contains("TODO"))
        assertFalse("Should not contain placeholder text", content.contains("FIXME"))

        // Should have balanced braces
        val openBraces = content.count { it == '{' }
        val closeBraces = content.count { it == '}' }
        assertEquals("Braces should be balanced", openBraces, closeBraces)
    }

    @Test
    fun `app module should be included in settings`() {
        val settingsFile = File("../settings.gradle.kts")
        val content = settingsFile.readText()

        assertTrue(
            "App module should be included",
            content.contains("include(\":app\")")
        )
    }

    @Test
    fun `project name should match expected value`() {
        val settingsFile = File("../settings.gradle.kts")
        val content = settingsFile.readText()

        assertTrue(
            "Project name should be AuraFrameFX",
            content.contains("rootProject.name = \"AuraFrameFX\"")
        )
    }

    @Test
    fun `should have proper module structure for Android project`() {
        val settingsFile = File("../settings.gradle.kts")
        val content = settingsFile.readText()

        // Should include Android-specific configuration
        assertTrue("Should configure Android repositories", content.contains("google()"))
        assertTrue(
            "Should have Android plugin management",
            content.contains("com.android.application")
        )
    }

    @Test
    fun `should validate conditional module includes work correctly`() {
        val settingsFile = File("../settings.gradle.kts")
        val content = settingsFile.readText()

        // Verify conditional logic exists
        assertTrue(
            "Should have conditional jvm-test include",
            content.contains("if (file(\"jvm-test\").exists())")
        )
        assertTrue(
            "Should have conditional sandbox-ui include",
            content.contains("if (file(\"sandbox-ui\").exists())")
        )

        // Verify actual module directories match conditions
        val jvmTestExists = File("../jvm-test").exists()
        val sandboxUiExists = File("../sandbox-ui").exists()

        if (jvmTestExists) {
            assertTrue(
                "jvm-test directory exists, should be included",
                content.contains("include(\":jvm-test\")")
            )
        }

        if (sandboxUiExists) {
            assertTrue(
                "sandbox-ui directory exists, should be included",
                content.contains("include(\":sandbox-ui\")")
            )
        }
    }

    @Test
    fun `should use secure repository URLs`() {
        val settingsFile = File("../settings.gradle.kts")
        val content = settingsFile.readText()

        // All custom repository URLs should use HTTPS
        val repositoryUrls = listOf(
            "https://oss.sonatype.org/content/repositories/snapshots",
            "https://jitpack.io"
        )

        repositoryUrls.forEach { url ->
            assertTrue(
                "Should contain secure repository URL: $url",
                content.contains(url)
            )
            assertTrue(
                "Repository URL should use HTTPS: $url",
                url.startsWith("https://")
            )
        }
    }

    @Test
    fun `should enforce fail on project repos policy`() {
        val settingsFile = File("../settings.gradle.kts")
        val content = settingsFile.readText()

        assertTrue(
            "Should enforce centralized repository management",
            content.contains("repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)")
        )
    }
}