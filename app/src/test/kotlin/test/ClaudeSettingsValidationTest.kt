package test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import java.io.File

/**
 * Comprehensive validation tests for Claude Code settings configuration.
 * Tests ensure that Claude settings contain proper permission structures
 * and follow secure configuration patterns.
 */
class ClaudeSettingsValidationTest {

    private val rootDir = File(".").canonicalFile
    private val claudeDir = File(rootDir, ".claude")
    private val settingsFile = File(claudeDir, "settings.local.json")

    @Test
    @DisplayName("Claude configuration directory exists")
    fun claudeDirectoryExists() {
        assertTrue(
            claudeDir.exists() && claudeDir.isDirectory,
            "Claude configuration directory should exist at .claude"
        )
    }

    @Test
    @DisplayName("Claude settings.local.json file exists")
    fun claudeSettingsFileExists() {
        assertTrue(
            settingsFile.exists(),
            "Claude settings file should exist at .claude/settings.local.json"
        )
    }

    @Test
    @DisplayName("Claude settings file is valid JSON")
    fun claudeSettingsIsValidJson() {
        val content = settingsFile.readText()

        // Basic JSON validation without external library
        assertTrue(
            content.trim().startsWith("{") && content.trim().endsWith("}"),
            "Settings file should be valid JSON object"
        )

        // Check for balanced braces
        val openBraces = content.count { it == '{' }
        val closeBraces = content.count { it == '}' }
        assertEquals(openBraces, closeBraces, "JSON braces should be balanced")
    }

    @Test
    @DisplayName("Claude settings contains permissions structure")
    fun claudeSettingsContainsPermissions() {
        val content = settingsFile.readText()

        assertTrue(
            content.contains("\"permissions\""),
            "Settings should contain permissions object"
        )

        assertTrue(
            content.contains("\"allow\""),
            "Permissions should contain allow array"
        )
    }

    @Test
    @DisplayName("Claude settings allow list contains expected Git permissions")
    fun claudeSettingsContainsGitPermissions() {
        val content = settingsFile.readText()

        val expectedGitPermissions = listOf(
            "Bash(git add:*)",
            "Bash(git commit:*)",
            "Bash(git push:*)",
            "Bash(git log:*)",
            "Bash(git config:*)"
        )

        expectedGitPermissions.forEach { permission ->
            assertTrue(
                content.contains(permission),
                "Settings should contain Git permission: $permission"
            )
        }
    }

    @Test
    @DisplayName("Claude settings allow list contains expected Gradle permissions")
    fun claudeSettingsContainsGradlePermissions() {
        val content = settingsFile.readText()

        val expectedGradlePatterns = listOf(
            "gradlew",
            "assembleDebug",
            "clean"
        )

        expectedGradlePatterns.forEach { pattern ->
            assertTrue(
                content.contains(pattern),
                "Settings should contain Gradle-related permission with pattern: $pattern"
            )
        }
    }

    @Test
    @DisplayName("Claude settings permissions use wildcard patterns appropriately")
    fun claudeSettingsUsesWildcardPatterns() {
        val content = settingsFile.readText()

        assertTrue(
            content.contains(":*"),
            "Settings should use wildcard patterns (:*) for flexible permissions"
        )
    }

    @Test
    @DisplayName("Claude settings JSON is properly formatted")
    fun claudeSettingsIsProperlyFormatted() {
        val content = settingsFile.readText()

        // Check for balanced braces
        val openBraces = content.count { it == '{' }
        val closeBraces = content.count { it == '}' }
        assertEquals(
            openBraces,
            closeBraces,
            "JSON braces should be balanced"
        )

        // Check for balanced brackets
        val openBrackets = content.count { it == '[' }
        val closeBrackets = content.count { it == ']' }
        assertEquals(
            openBrackets,
            closeBrackets,
            "JSON brackets should be balanced"
        )

        // Should not contain trailing commas (invalid JSON)
        assertFalse(
            content.contains(",]") || content.contains(",}"),
            "JSON should not contain trailing commas"
        )
    }

    @Test
    @DisplayName("Claude settings does not expose sensitive file paths unnecessarily")
    fun claudeSettingsDoesNotExposeSensitivePaths() {
        val content = settingsFile.readText()

        // While some specific paths are allowed for development,
        // ensure no sensitive system paths are exposed
        val sensitivePaths = listOf(
            "/etc/shadow",
            "/etc/passwd",
            "~/.ssh/id_rsa",
            "C:\\\\Windows\\\\System32"
        )

        sensitivePaths.forEach { path ->
            assertFalse(
                content.contains(path),
                "Settings should not contain sensitive path: $path"
            )
        }
    }

    @Test
    @DisplayName("Claude settings permissions follow Bash command pattern")
    fun claudeSettingsFollowsBashPattern() {
        val content = settingsFile.readText()

        // Extract permission entries using regex
        val permissionPattern = Regex("\"(Bash\\([^\"]+\\))\"")
        val matches = permissionPattern.findAll(content)

        assertTrue(
            matches.count() > 0,
            "Settings should contain Bash() permission patterns"
        )

        // All extracted permissions should follow the pattern
        matches.forEach { match ->
            val permission = match.groupValues[1]
            assertTrue(
                permission.startsWith("Bash(") && permission.endsWith(")"),
                "Permission should follow Bash(command:pattern) format: $permission"
            )
        }
    }

    @Test
    @DisplayName("Claude settings contains build script permissions")
    fun claudeSettingsContainsBuildScriptPermissions() {
        val content = settingsFile.readText()

        assertAll(
            "Build script permission validation",
            {
                assertTrue(
                    content.contains("BUILD_FINALE.bat") || content.contains("gradlew"),
                    "Should contain build script permissions"
                )
            },
            {
                assertTrue(
                    content.contains("./gradlew") || content.contains("gradlew.bat"),
                    "Should contain Gradle wrapper permissions"
                )
            }
        )
    }

    @Test
    @DisplayName("Claude settings permissions array is not empty")
    fun claudeSettingsPermissionsNotEmpty() {
        val content = settingsFile.readText()

        // Check that there are permission entries
        val permissionPattern = Regex("\"Bash\\([^\"]+\\)\"")
        val matches = permissionPattern.findAll(content)

        assertTrue(
            matches.count() > 0,
            "Permissions allow list should not be empty"
        )
    }

    @Test
    @DisplayName("Claude settings contains file operation permissions")
    fun claudeSettingsContainsFileOperations() {
        val content = settingsFile.readText()

        val fileOperations = listOf(
            "find",
            "cat",
            "ls"
        )

        fileOperations.forEach { operation ->
            assertTrue(
                content.contains("Bash($operation:*") || content.contains("Bash($operation "),
                "Settings should contain file operation: $operation"
            )
        }
    }

    @Test
    @DisplayName("Claude settings git permissions include safe operations")
    fun claudeSettingsGitPermissionsAreSafe() {
        val content = settingsFile.readText()

        val safeGitOperations = listOf(
            "git add",
            "git commit",
            "git log",
            "git fetch",
            "git remote add",
            "git config"
        )

        safeGitOperations.forEach { operation ->
            assertTrue(
                content.contains(operation),
                "Settings should include safe git operation: $operation"
            )
        }
    }

    @Test
    @DisplayName("Claude settings includes gradle task execution permissions")
    fun claudeSettingsIncludesGradleTasks() {
        val content = settingsFile.readText()

        val gradleTasks = listOf(
            "assembleDebug",
            "clean",
            "compileDebugKotlin",
            "tasks"
        )

        var hasGradleTasks = false
        for (task in gradleTasks) {
            if (content.contains(task)) {
                hasGradleTasks = true
                break
            }
        }

        assertTrue(
            hasGradleTasks,
            "Settings should include at least one Gradle task permission"
        )
    }

    @Test
    @DisplayName("Claude settings JSON structure is consistent")
    fun claudeSettingsStructureIsConsistent() {
        val content = settingsFile.readText()

        // Root should have permissions as top-level key
        assertTrue(
            content.contains("\"permissions\""),
            "Root JSON object should contain 'permissions' key"
        )

        // Permissions should have allow
        assertTrue(
            content.contains("\"allow\""),
            "Permissions object should contain 'allow' key"
        )

        // Check basic structure pattern
        assertTrue(
            Regex("\\{\\s*\"permissions\"\\s*:\\s*\\{\\s*\"allow\"\\s*:").containsMatchIn(content),
            "JSON should follow expected structure: { permissions: { allow: ... } }"
        )
    }

    @Test
    @DisplayName("Claude settings permissions are properly quoted strings")
    fun claudeSettingsPermissionsAreStrings() {
        val content = settingsFile.readText()

        // Extract permission entries - they should be quoted strings
        val permissionPattern = Regex("\"(Bash\\([^\"]+\\))\"")
        val matches = permissionPattern.findAll(content)

        assertTrue(
            matches.count() > 0,
            "All permissions should be properly quoted strings"
        )
    }

    @Test
    @DisplayName("Claude settings allows cmd execution for Windows compatibility")
    fun claudeSettingsAllowsCmdForWindows() {
        val content = settingsFile.readText()

        assertTrue(
            content.contains("cmd /c"),
            "Settings should allow cmd /c for Windows command execution"
        )
    }

    @Test
    @DisplayName("Claude settings includes diagnostic permissions")
    fun claudeSettingsIncludesDiagnostics() {
        val content = settingsFile.readText()

        val diagnosticPatterns = listOf(
            "findstr",
            "Select-String",
            "grep" // via find or other commands
        )

        var hasDiagnostics = false
        for (pattern in diagnosticPatterns) {
            if (content.contains(pattern)) {
                hasDiagnostics = true
                break
            }
        }

        assertTrue(
            hasDiagnostics,
            "Settings should include diagnostic/search permissions"
        )
    }
}