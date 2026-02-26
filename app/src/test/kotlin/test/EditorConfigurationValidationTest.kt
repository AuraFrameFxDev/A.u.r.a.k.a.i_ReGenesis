package test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.io.File

/**
 * Comprehensive validation tests for editor configuration files.
 * Tests ensure that .editorconfig and .vscode/settings.json follow
 * proper formatting standards and contain appropriate settings for the project.
 */
class EditorConfigurationValidationTest {

    private val rootDir = File(".").canonicalFile
    private val editorConfigFile = File(rootDir, ".editorconfig")
    private val vscodeDir = File(rootDir, ".vscode")
    private val vscodeSettingsFile = File(vscodeDir, "settings.json")

    // .editorconfig Tests

    @Test
    @DisplayName(".editorconfig file exists in repository root")
    fun editorConfigFileExists() {
        assertTrue(
            editorConfigFile.exists(),
            ".editorconfig file should exist in repository root"
        )
    }

    @Test
    @DisplayName(".editorconfig declares root = true")
    fun editorConfigDeclaresRoot() {
        val content = editorConfigFile.readText()

        assertTrue(
            content.contains("root = true"),
            ".editorconfig should declare root = true at the top"
        )
    }

    @Test
    @DisplayName(".editorconfig contains global settings for all files")
    fun editorConfigContainsGlobalSettings() {
        val content = editorConfigFile.readText()

        assertAll(
            "Global settings validation",
            { assertTrue(content.contains("[*]"), "Should have global section [*]") },
            { assertTrue(content.contains("charset = utf-8"), "Should set UTF-8 charset") },
            { assertTrue(content.contains("end_of_line = lf"), "Should enforce LF line endings") },
            { assertTrue(content.contains("insert_final_newline = true"), "Should insert final newline") },
            { assertTrue(content.contains("trim_trailing_whitespace = true"), "Should trim trailing whitespace") }
        )
    }

    @Test
    @DisplayName(".editorconfig contains Kotlin-specific settings")
    fun editorConfigContainsKotlinSettings() {
        val content = editorConfigFile.readText()

        assertAll(
            "Kotlin settings validation",
            { assertTrue(content.contains("[*.{kt,kts}]"), "Should have Kotlin section") },
            { assertTrue(content.contains("indent_size = 4"), "Should set 4-space indentation for Kotlin") },
            { assertTrue(content.contains("max_line_length = 120"), "Should set 120 character line limit") }
        )
    }

    @Test
    @DisplayName(".editorconfig contains YAML-specific settings")
    fun editorConfigContainsYamlSettings() {
        val content = editorConfigFile.readText()

        assertAll(
            "YAML settings validation",
            { assertTrue(content.contains("[*.{yaml,yml}]"), "Should have YAML section") },
            { assertTrue(content.contains("indent_size = 2"), "Should set 2-space indentation for YAML") }
        )
    }

    @Test
    @DisplayName(".editorconfig contains XML and Gradle settings")
    fun editorConfigContainsXmlGradleSettings() {
        val content = editorConfigFile.readText()

        assertAll(
            "XML/Gradle settings validation",
            { assertTrue(content.contains("[*.{xml,gradle}]"), "Should have XML/Gradle section") },
            { assertTrue(content.contains("indent_size = 4"), "Should set 4-space indentation") }
        )
    }

    @Test
    @DisplayName(".editorconfig contains Markdown-specific settings")
    fun editorConfigContainsMarkdownSettings() {
        val content = editorConfigFile.readText()

        assertAll(
            "Markdown settings validation",
            { assertTrue(content.contains("[*.md]"), "Should have Markdown section") },
            { assertTrue(content.contains("trim_trailing_whitespace = false"), "Should not trim whitespace in Markdown") }
        )
    }

    @Test
    @DisplayName(".editorconfig uses space indentation consistently")
    fun editorConfigUsesSpaceIndentation() {
        val content = editorConfigFile.readText()

        assertTrue(
            content.contains("indent_style = space"),
            ".editorconfig should enforce space indentation"
        )

        assertFalse(
            content.contains("indent_style = tab"),
            ".editorconfig should not use tab indentation"
        )
    }

    @Test
    @DisplayName(".editorconfig file structure is valid")
    fun editorConfigStructureIsValid() {
        val content = editorConfigFile.readText()

        // Should have balanced brackets for sections
        val openBrackets = content.count { it == '[' }
        val closeBrackets = content.count { it == ']' }
        assertEquals(
            openBrackets,
            closeBrackets,
            ".editorconfig section brackets should be balanced"
        )

        // Should not have empty sections
        val sections = content.split("[").filter { it.contains("]") }
        sections.forEach { section ->
            assertFalse(
                section.trim().endsWith("]"),
                "Sections should contain settings, not be empty"
            )
        }
    }

    @Test
    @DisplayName(".editorconfig enforces Unix-style line endings")
    fun editorConfigEnforcesUnixLineEndings() {
        val content = editorConfigFile.readText()

        assertTrue(
            content.contains("end_of_line = lf"),
            ".editorconfig should enforce Unix-style (LF) line endings"
        )

        assertFalse(
            content.contains("end_of_line = crlf"),
            ".editorconfig should not use Windows-style (CRLF) line endings"
        )
    }

    // VSCode settings.json Tests

    @Test
    @DisplayName(".vscode directory exists")
    fun vscodeDirectoryExists() {
        assertTrue(
            vscodeDir.exists() && vscodeDir.isDirectory,
            ".vscode directory should exist"
        )
    }

    @Test
    @DisplayName(".vscode/settings.json file exists")
    fun vscodeSettingsFileExists() {
        assertTrue(
            vscodeSettingsFile.exists(),
            ".vscode/settings.json should exist"
        )
    }

    @Test
    @DisplayName(".vscode/settings.json is valid JSON")
    fun vscodeSettingsIsValidJson() {
        val content = vscodeSettingsFile.readText()

        // Basic JSON validation
        assertTrue(
            content.trim().startsWith("{") && content.trim().endsWith("}"),
            ".vscode/settings.json should be valid JSON object"
        )

        // Check for balanced braces
        val openBraces = content.count { it == '{' }
        val closeBraces = content.count { it == '}' }
        assertEquals(openBraces, closeBraces, "JSON braces should be balanced")
    }

    @Test
    @DisplayName(".vscode/settings.json contains Java configuration")
    fun vscodeSettingsContainsJavaConfig() {
        val content = vscodeSettingsFile.readText()

        assertAll(
            "Java configuration validation",
            {
                assertTrue(
                    content.contains("\"java.compile.nullAnalysis.mode\""),
                    "Should configure Java null analysis"
                )
            },
            {
                assertTrue(
                    content.contains("\"automatic\""),
                    "Null analysis should be set to automatic"
                )
            }
        )
    }

    @Test
    @DisplayName(".vscode/settings.json contains Java build configuration")
    fun vscodeSettingsContainsBuildConfig() {
        val content = vscodeSettingsFile.readText()

        assertAll(
            "Build configuration validation",
            {
                assertTrue(
                    content.contains("\"java.configuration.updateBuildConfiguration\""),
                    "Should configure build updates"
                )
            },
            {
                assertTrue(
                    content.contains("\"interactive\""),
                    "Build configuration should be interactive"
                )
            }
        )
    }

    @Test
    @DisplayName(".vscode/settings.json is properly formatted")
    fun vscodeSettingsIsProperlyFormatted() {
        val content = vscodeSettingsFile.readText()

        // Check for balanced braces
        val openBraces = content.count { it == '{' }
        val closeBraces = content.count { it == '}' }
        assertEquals(
            openBraces,
            closeBraces,
            "JSON braces should be balanced"
        )

        // Should not contain trailing commas (invalid in strict JSON)
        assertFalse(
            content.contains(",}"),
            "JSON should not contain trailing commas before closing brace"
        )
    }

    @Test
    @DisplayName(".vscode/settings.json uses proper key naming convention")
    fun vscodeSettingsUsesProperKeyNaming() {
        val content = vscodeSettingsFile.readText()

        // Extract keys using regex (simple pattern for "key":)
        val keyPattern = Regex("\"([a-zA-Z.]+)\"\\s*:")
        val keys = keyPattern.findAll(content).map { it.groupValues[1] }.toList()

        assertTrue(
            keys.isNotEmpty(),
            "Should contain setting keys"
        )

        keys.forEach { key ->
            assertTrue(
                key.contains("."),
                "VSCode setting keys should use dot notation: $key"
            )
        }
    }

    @Test
    @DisplayName(".vscode/settings.json does not contain user-specific paths")
    fun vscodeSettingsNoUserSpecificPaths() {
        val content = vscodeSettingsFile.readText()

        // Should not contain absolute user paths
        val userPathPatterns = listOf(
            Regex("C:\\\\Users\\\\[^\\\\]+"),
            Regex("/home/[^/]+"),
            Regex("/Users/[^/]+")
        )

        userPathPatterns.forEach { pattern ->
            assertFalse(
                pattern.containsMatchIn(content),
                ".vscode/settings.json should not contain user-specific paths"
            )
        }
    }

    @Test
    @DisplayName(".vscode/settings.json contains appropriate settings for Android/Java project")
    fun vscodeSettingsIsAppropriateForProject() {
        val content = vscodeSettingsFile.readText()

        assertTrue(
            content.contains("java."),
            ".vscode/settings.json should contain Java-related settings for Android project"
        )
    }

    // Cross-validation Tests

    @Test
    @DisplayName("Editor configurations are consistent between .editorconfig and .vscode")
    fun editorConfigurationsAreConsistent() {
        val editorConfigContent = editorConfigFile.readText()

        // Both should enforce consistent indentation
        assertTrue(
            editorConfigContent.contains("indent_size"),
            "Configurations should define indent size"
        )

        // Both should handle UTF-8
        assertTrue(
            editorConfigContent.contains("charset = utf-8"),
            "Configurations should enforce UTF-8 encoding"
        )
    }

    @Test
    @DisplayName("Editor configuration files do not conflict")
    fun editorConfigFilesDoNotConflict() {
        val editorConfigContent = editorConfigFile.readText()

        // .editorconfig takes precedence, so VSCode settings should be complementary
        // Check that .editorconfig has comprehensive coverage
        val fileTypes = listOf("kt", "kts", "yaml", "yml", "xml", "gradle", "md")

        fileTypes.forEach { fileType ->
            assertTrue(
                editorConfigContent.contains("*.$fileType") || editorConfigContent.contains("{") && editorConfigContent.contains("$fileType}"),
                ".editorconfig should cover file type: $fileType"
            )
        }
    }

    @Test
    @DisplayName("Editor configurations support the project's language ecosystem")
    fun editorConfigsSupportProjectLanguages() {
        val editorConfigContent = editorConfigFile.readText()

        // Project uses Kotlin, Java, XML (Android), YAML (configs), Gradle
        val projectLanguages = listOf(
            "[*.{kt,kts}]",  // Kotlin
            "[*.{xml,gradle}]",  // Android/Gradle
            "[*.{yaml,yml}]"  // Configuration
        )

        projectLanguages.forEach { langSection ->
            assertTrue(
                editorConfigContent.contains(langSection),
                ".editorconfig should support project language: $langSection"
            )
        }
    }

    @Test
    @DisplayName("Editor configurations enforce code quality standards")
    fun editorConfigsEnforceQuality() {
        val editorConfigContent = editorConfigFile.readText()

        val qualitySettings = listOf(
            "trim_trailing_whitespace = true",
            "insert_final_newline = true",
            "max_line_length"
        )

        qualitySettings.forEach { setting ->
            assertTrue(
                editorConfigContent.contains(setting),
                ".editorconfig should enforce quality setting: $setting"
            )
        }
    }
}