// Note: Framework detection performed via build files; tests target libs.versions.toml integrity.
// GradleVersionCatalogTest.kt
// Testing library/framework: JUnit 5 (JUnit Jupiter) with kotlin.test assertions (if available)
@file:Suppress("SameParameterValue", "UNCHECKED_CAST", "MemberVisibilityCanBePrivate")

package testplaceholder

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.charset.StandardCharsets
import kotlin.io.path.exists
import kotlin.io.path.readText
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * These tests validate the Gradle Version Catalog (libs.versions.toml).
 * They avoid introducing TOML parser deps by using structured text checks.
 */
class GradleVersionCatalogTest {

    companion object {
        private lateinit var catalogPaths: List<Path>
        private lateinit var catalogTextByPath: Map<Path, String>

        @BeforeAll
        @JvmStatic
        fun loadCatalogs() {
            val repoRoot = Paths.get("").toAbsolutePath()
            val found = mutableListOf<Path>()

            // Typical locations
            val typical = listOf(
                repoRoot.resolve("gradle/libs.versions.toml"),
                repoRoot.resolve("libs.versions.toml"),
            )
            found += typical.filter { Files.exists(it) }

            // Fallback: scan for any libs.versions.toml
            if (found.isEmpty()) {
                Files.walk(repoRoot).use { stream ->
                    stream.filter { it.fileName?.toString() == "libs.versions.toml" }
                        .forEach(found::add)
                }
            }

            assertTrue(found.isNotEmpty(), "No libs.versions.toml found")
            catalogPaths = found.toList()
            catalogTextByPath = catalogPaths.associateWith { it.readText(StandardCharsets.UTF_8) }
        }

        @AfterAll
        @JvmStatic
        fun cleanup() {
            // No cleanup needed for this test
        }
    }

    @Test
    @DisplayName("Version catalog file exists and is readable")
    fun testCatalogExists() {
        assertTrue(catalogPaths.isNotEmpty(), "At least one version catalog should exist")
        catalogPaths.forEach { path ->
            assertTrue(path.exists(), "Catalog should exist at: $path")
            assertTrue(catalogTextByPath[path]!!.isNotEmpty(), "Catalog should not be empty")
        }
    }

    @Nested
    @DisplayName("Version catalog structure validation")
    inner class StructureValidation {

        @Test
        @DisplayName("Contains required sections")
        fun testRequiredSections() {
            catalogPaths.forEach { path ->
                val content = catalogTextByPath[path]!!
                assertAll(
                    "Catalog at $path should contain required sections",
                    {
                        assertTrue(
                            content.contains("[versions]"),
                            "Should contain [versions] section"
                        )
                    },
                    {
                        assertTrue(
                            content.contains("[libraries]"),
                            "Should contain [libraries] section"
                        )
                    },
                    {
                        assertTrue(
                            content.contains("[plugins]"),
                            "Should contain [plugins] section"
                        )
                    }
                )
            }
        }

        @Test
        @DisplayName("Contains AuraFrameFX specific versions")
        fun testAuraFrameFXVersions() {
            catalogPaths.forEach { path ->
                val content = catalogTextByPath[path]!!
                assertAll(
                    "Catalog should contain AuraFrameFX specific versions",
                    { assertTrue(content.contains("agp"), "Should define AGP version") },
                    { assertTrue(content.contains("kotlin"), "Should define Kotlin version") },
                    { assertTrue(content.contains("yukihook"), "Should define YukiHook version") }
                )
            }
        }
    }
}
