package test

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File

/**
 * Tests to validate that agent instruction documentation exists and contains required sections.
 */
class AgentInstructionsValidationTest {

    private val rootDir = File(".").canonicalFile

    @Test
    @DisplayName("AGENT_INSTRUCTIONS.md exists in repository root")
    fun age-ntInstructionsFileExists()
    {
        val agentInstructionsFile = File(rootDir, "AGENT_INSTRUCTIONS.md")
        assertTrue(
            agentInstructionsFile.exists(),
            "AGENT_INSTRUCTIONS.md should exist in repository root"
        )
    }

    @Test
    @DisplayName("AGENT_INSTRUCTIONS.md contains all required sections")
    fun agentInstructionsContainsRequiredSections() {
        val agentInstructionsFile = File(rootDir, "AGENT_INSTRUCTIONS.md")
        val content = agentInstructionsFile.readText()

        val requiredSections = listOf(
            "1. Always Validate File Paths",
            "2. Confirm Plugin Application",
            "3. Enforce Toolchain Consistency",
            "4. Order of Operations",
            "5. Workflow Robustness",
            "6. Error Reporting",
            "7. Security & Best Practices",
            "8. Continuous Improvement"
        )

        requiredSections.forEach { section ->
            assertTrue(
                content.contains(section),
                "AGENT_INSTRUCTIONS.md should contain section: $section"
            )
        }
    }

    @Test
    @DisplayName("AGENT_WORKFLOW_GUIDE.md exists in .github directory")
    fun workflowGuideExists() {
        val workflowGuideFile = File(rootDir, ".github/AGENT_WORKFLOW_GUIDE.md")
        assertTrue(
            workflowGuideFile.exists(),
            "AGENT_WORKFLOW_GUIDE.md should exist in .github directory"
        )
    }

    @Test
    @DisplayName("AGENT_WORKFLOW_GUIDE.md contains GitHub Actions best practices")
    fun workflowGuideContainsActions() {
        val workflowGuideFile = File(rootDir, ".github/AGENT_WORKFLOW_GUIDE.md")
        val content = workflowGuideFile.readText()

        val requiredPatterns = listOf(
            "actions/setup-java",
            "android-actions/setup-android",
            "gradle/actions/setup-gradle",
            "chmod +x ./gradlew"
        )

        requiredPatterns.forEach { pattern ->
            assertTrue(
                content.contains(pattern),
                "AGENT_WORKFLOW_GUIDE.md should contain pattern: $pattern"
            )
        }
    }

    @Test
    @DisplayName("AGENT_TASK_CHECKLIST.md exists in repository root")
    fun taskChecklistExists() {
        val checklistFile = File(rootDir, "AGENT_TASK_CHECKLIST.md")
        assertTrue(
            checklistFile.exists(),
            "AGENT_TASK_CHECKLIST.md should exist in repository root"
        )
    }

    @Test
    @DisplayName("AGENT_TASK_CHECKLIST.md contains validation checklists")
    fun taskChecklistContainsChecklists() {
        val checklistFile = File(rootDir, "AGENT_TASK_CHECKLIST.md")
        val content = checklistFile.readText()

        val requiredChecklistSections = listOf(
            "Pre-Task Validation",
            "File Path Validation",
            "Plugin Application Checklist",
            "Toolchain Verification",
            "Code Generation Task Order",
            "Workflow Configuration",
            "Error Reporting Standards",
            "Security & Best Practices"
        )

        requiredChecklistSections.forEach { section ->
            assertTrue(
                content.contains(section),
                "AGENT_TASK_CHECKLIST.md should contain checklist section: $section"
            )
        }
    }

    @Test
    @DisplayName("README.md references AGENT_INSTRUCTIONS.md")
    fun readmeReferencesAgentInstructions() {
        val readmeFile = File(rootDir, "README.md")
        val content = readmeFile.readText()

        assertTrue(
            content.contains("AGENT_INSTRUCTIONS.md"),
            "README.md should reference AGENT_INSTRUCTIONS.md"
        )
    }

    @Test
    @DisplayName("Convention plugins reference AGENT_INSTRUCTIONS.md in comments")
    fun conventionPluginsReferenceAgentInstructions() {
        val libraryPlugin =
            File(rootDir, "build-logic/src/main/kotlin/AndroidLibraryConventionPlugin.kt")
        val applicationPlugin =
            File(rootDir, "build-logic/src/main/kotlin/AndroidApplicationConventionPlugin.kt")
        assertTrue(libraryPlugin.exists(), "AndroidLibraryConventionPlugin.kt should exist")
        assertTrue(applicationPlugin.exists(), "AndroidApplicationConventionPlugin.kt should exist")

        val libraryContent = libraryPlugin.readText()
        val applicationContent = applicationPlugin.readText()

        assertTrue(
            libraryContent.contains("AGENT_INSTRUCTIONS.md"),
            "AndroidLibraryConventionPlugin should reference AGENT_INSTRUCTIONS.md"
        )

        assertTrue(
            applicationContent.contains("AGENT_INSTRUCTIONS.md"),
            "AndroidApplicationConventionPlugin should reference AGENT_INSTRUCTIONS.md"
        )
    }

    @Test
    @DisplayName("Convention plugins include error handling with clear messages")
    fun conventionPluginsIncludeErrorHandling() {
        val libraryPlugin =
            File(rootDir, "build-logic/src/main/kotlin/AndroidLibraryConventionPlugin.kt")
        val applicationPlugin =
            File(rootDir, "build-logic/src/main/kotlin/AndroidApplicationConventionPlugin.kt")
        val libraryContent = libraryPlugin.readText()
        val applicationContent = applicationPlugin.readText()

        // Check for proper error handling patterns
        assertTrue(
            libraryContent.contains("GradleException"),
            "AndroidLibraryConventionPlugin should use GradleException for errors"
        )

        assertTrue(
            applicationContent.contains("GradleException"),
            "AndroidApplicationConventionPlugin should use GradleException for errors"
        )

        // Check for error message structure
        assertTrue(
            libraryContent.contains("ERROR:"),
            "AndroidLibraryConventionPlugin should include ERROR: prefix in messages"
        )

        assertTrue(
            applicationContent.contains("ERROR:"),
            "AndroidApplicationConventionPlugin should include ERROR: prefix in messages"
        )

        // Check for documentation references in errors
        assertTrue(
            libraryContent.contains("Documentation: See AGENT_INSTRUCTIONS.md"),
            "AndroidLibraryConventionPlugin errors should reference documentation"
        )

        assertTrue(
            applicationContent.contains("Documentation: See AGENT_INSTRUCTIONS.md"),
            "AndroidApplicationConventionPlugin errors should reference documentation"
        )
    }

    @Test
    @DisplayName("Convention plugins use pluginManager.withPlugin for safe configuration")
    fun conventionPluginsUseSafeConfiguration() {
        val libraryPlugin =
            File(rootDir, "build-logic/src/main/kotlin/AndroidLibraryConventionPlugin.kt")
        val applicationPlugin =
            File(rootDir, "build-logic/src/main/kotlin/AndroidApplicationConventionPlugin.kt")
        val libraryContent = libraryPlugin.readText()
        val applicationContent = applicationPlugin.readText()

        // Check for pluginManager.withPlugin usage
        assertTrue(
            libraryContent.contains("pluginManager.withPlugin"),
            "AndroidLibraryConventionPlugin should use pluginManager.withPlugin"
        )

        assertTrue(
            applicationContent.contains("pluginManager.withPlugin"),
            "AndroidApplicationConventionPlugin should use pluginManager.withPlugin"
        )
    }

    @Test
    @DisplayName("Convention plugins configure Java 24 toolchain")
    fun conventionPluginsConfigureToolchain() {
        val libraryPlugin =
            File(rootDir, "build-logic/src/main/kotlin/AndroidLibraryConventionPlugin.kt")
        val applicationPlugin =
            File(rootDir, "build-logic/src/main/kotlin/AndroidApplicationConventionPlugin.kt")
        val libraryContent = libraryPlugin.readText()
        val applicationContent = applicationPlugin.readText()

        // Check for jvmToolchain(24) configuration
        assertTrue(
            libraryContent.contains("jvmToolchain(24)"),
            "AndroidLibraryConventionPlugin should configure jvmToolchain(24)"
        )

        assertTrue(
            applicationContent.contains("jvmToolchain(24)"),
            "AndroidApplicationConventionPlugin should configure jvmToolchain(24)"
        )
    }
}
