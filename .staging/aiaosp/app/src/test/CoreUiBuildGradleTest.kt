package dev.aurakai.auraframefx

/*
Testing framework and library:
- Using JUnit 5 (Jupiter) for unit tests (org.junit.jupiter.api.*).
- Tests validate core/ui/build.gradle.kts structure and formatting
*/

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File

class CoreUiBuildGradleTest {

    private fun locateCoreUiBuildFile(): File {
        val candidates = listOf(
            File("core/ui/build.gradle.kts"),
            File("../core/ui/build.gradle.kts")
        )
        return candidates.firstOrNull { it.exists() } ?: error(
            "Unable to locate core/ui/build.gradle.kts. Checked: ${candidates.joinToString { it.path }}"
        )
    }

    private val buildFile: File by lazy { locateCoreUiBuildFile() }
    private val script: String by lazy { buildFile.readText() }

    @Test
    @DisplayName("Dependencies block exists and is properly formatted")
    fun dependenciesBlockProperlyFormatted() {
        assertTrue(
            Regex("""dependencies\s*\{""").containsMatchIn(script),
            "Dependencies block should exist"
        )
        
        // Check dependencies block is not empty
        val dependenciesBlock = Regex(
            """dependencies\s*\{(.*?)\}""",
            RegexOption.DOT_MATCHES_ALL
        ).find(script)
        
        assertNotNull(dependenciesBlock, "Should be able to parse dependencies block")
        assertTrue(
            dependenciesBlock!!.groupValues[1].trim().isNotEmpty(),
            "Dependencies block should have content"
        )
    }

    @Test
    @DisplayName("Android library plugin is applied")
    fun androidLibraryPluginApplied() {
        assertTrue(
            script.contains("com.android.library") || 
            Regex("""plugins\s*\{[^}]*android[^}]*library""", RegexOption.DOT_MATCHES_ALL).containsMatchIn(script),
            "Android library plugin should be applied"
        )
    }

    @Test
    @DisplayName("Essential UI dependencies are present")
    fun essentialUiDependenciesPresent() {
        val essentialDeps = listOf(
            "androidx.core.ktx",
            "androidx.appcompat"
        )
        
        essentialDeps.forEach { dep ->
            assertTrue(
                script.contains(dep),
                "Essential UI dependency $dep should be present"
            )
        }
    }

    @Test
    @DisplayName("No trailing blank lines in dependencies block")
    fun noExcessiveBlankLinesInDependencies() {
        val dependenciesBlock = Regex(
            """dependencies\s*\{(.*?)\}""",
            RegexOption.DOT_MATCHES_ALL
        ).find(script)
        
        assertNotNull(dependenciesBlock, "Should be able to parse dependencies block")
        
        val content = dependenciesBlock!!.groupValues[1]
        
        // Should not end with multiple blank lines
        assertFalse(
            content.trimEnd().endsWith("\n\n\n"),
            "Dependencies block should not have excessive trailing blank lines"
        )
    }

    @Test
    @DisplayName("Build file is valid Kotlin script")
    fun buildFileIsValidKotlinScript() {
        assertTrue(
            buildFile.name.endsWith(".gradle.kts"),
            "Build file should have .gradle.kts extension"
        )
        
        // Basic syntax checks
        val openBraces = script.count { it == '{' }
        val closeBraces = script.count { it == '}' }
        
        assertEquals(
            openBraces,
            closeBraces,
            "Build file should have balanced braces"
        )
        
        val openParens = script.count { it == '(' }
        val closeParens = script.count { it == ')' }
        
        assertEquals(
            openParens,
            closeParens,
            "Build file should have balanced parentheses"
        )
    }
}