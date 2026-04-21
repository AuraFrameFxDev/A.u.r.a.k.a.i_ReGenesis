package dev.aurakai.auraframefx

/*
 * Tests for PR-specific changes to app/build.gradle.kts:
 *
 * 1. ksp { arg("moshi.generated.enum.companion", "false") } block removed
 * 2. androidResources { localeFilters += "en" } removed
 * 3. buildTypes { getByName("debug") { isCrunchPngs = false } } removed
 * 4. implementation(project(":trinity:aura")) dependency removed
 * 5. jose4j version forced to 0.9.4 (downgraded from 0.9.6)
 *
 * Framework: JUnit 5 (Jupiter)
 * Pattern: file-based text validation (consistent with existing AppBuildGradleChangesTest.kt)
 */

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AppBuildGradlePrChangesTest {

    private fun locateBuildFile(): File {
        val candidates = listOf(
            File("build.gradle.kts"),
            File("app/build.gradle.kts"),
            File("../app/build.gradle.kts")
        )
        return candidates.firstOrNull { it.exists() } ?: error(
            "Unable to locate app/build.gradle.kts. Checked: ${candidates.joinToString { it.path }}"
        )
    }

    private val buildFile: File by lazy { locateBuildFile() }
    private val script: String by lazy { buildFile.readText() }

    // ─────────────────────────────────────────────────────────────────────────
    // 1. KSP Moshi arg block removed
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("KSP Moshi Configuration Block Removal")
    inner class KspMoshiBlockRemovalTests {

        @Test
        @DisplayName("Top-level ksp block with moshi generated enum companion arg is removed")
        fun kspMoshiEnumCompanionArgRemoved() {
            assertFalse(
                script.contains("moshi.generated.enum.companion"),
                "moshi.generated.enum.companion ksp arg should have been removed"
            )
        }

        @Test
        @DisplayName("No standalone top-level ksp block remains for moshi codegen arguments")
        fun noStandaloneKspBlockForMoshi() {
            // The removed block looked like: ksp { arg("moshi.generated.enum.companion", "false") }
            assertFalse(
                Regex("""^\s*ksp\s*\{\s*\n\s*arg\s*\(""", RegexOption.MULTILINE).containsMatchIn(script),
                "Standalone top-level ksp { arg(...) } block for moshi should have been removed"
            )
        }

        @Test
        @DisplayName("ksp is still used as a dependency configuration (not removed entirely)")
        fun kspStillUsedAsDependencyConfiguration() {
            // ksp() is still used for annotation processors like hilt.compiler, room.compiler, etc.
            assertTrue(
                Regex("""ksp\s*\(\s*libs\.""").containsMatchIn(script),
                "ksp() should still be used as a dependency configuration"
            )
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. androidResources localeFilters removed
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("androidResources localeFilters Removal")
    inner class LocaleFiltersRemovalTests {

        @Test
        @DisplayName("localeFilters configuration is removed")
        fun localeFiltersRemoved() {
            assertFalse(
                script.contains("localeFilters"),
                "localeFilters should have been removed from androidResources"
            )
        }

        @Test
        @DisplayName("androidResources block with localeFilters is not present")
        fun androidResourcesLocaleFilterBlockNotPresent() {
            assertFalse(
                Regex("""androidResources\s*\{[^}]*localeFilters""", RegexOption.DOT_MATCHES_ALL)
                    .containsMatchIn(script),
                "androidResources block with localeFilters should not be present"
            )
        }

        @Test
        @DisplayName("en locale filter specifically is not hardcoded")
        fun enLocaleFilterNotPresent() {
            assertFalse(
                Regex("""localeFilters\s*\+=\s*["']en["']""").containsMatchIn(script),
                "Hardcoded 'en' locale filter should have been removed"
            )
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. Debug buildType isCrunchPngs override removed
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Debug BuildType isCrunchPngs Removal")
    inner class DebugBuildTypeCrunchPngsRemovalTests {

        @Test
        @DisplayName("isCrunchPngs is not set in the build file")
        fun isCrunchPngsRemoved() {
            assertFalse(
                script.contains("isCrunchPngs"),
                "isCrunchPngs should have been removed from debug buildType"
            )
        }

        @Test
        @DisplayName("debug buildType isCrunchPngs = false override is not present")
        fun debugBuildTypecrunchPngsOverrideNotPresent() {
            assertFalse(
                Regex("""isCrunchPngs\s*=\s*false""").containsMatchIn(script),
                "isCrunchPngs = false should not be present in the build file"
            )
        }

        @Test
        @DisplayName("debug buildType block does not exist with crunchPngs setting")
        fun debugBuildTypeWithCrunchPngsNotPresent() {
            // The removed block was: buildTypes { getByName("debug") { isCrunchPngs = false } }
            assertFalse(
                Regex(
                    """buildTypes\s*\{[^}]*getByName\("debug"\)\s*\{[^}]*isCrunchPngs""",
                    RegexOption.DOT_MATCHES_ALL
                ).containsMatchIn(script),
                "buildTypes block containing debug crunchPngs override should have been removed"
            )
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 4. :trinity:aura module dependency removed
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Trinity Aura Module Dependency Removal")
    inner class TrinityAuraDependencyRemovalTests {

        @Test
        @DisplayName("trinity:aura module dependency is removed")
        fun trinityAuraDependencyRemoved() {
            assertFalse(
                script.contains(":trinity:aura"),
                "implementation(project(\":trinity:aura\")) should have been removed"
            )
        }

        @Test
        @DisplayName("LDO DevOps Index comment for trinity:aura is removed")
        fun trinityAuraLdoCommentRemoved() {
            assertFalse(
                script.contains("Trinity → Aura (LDO DevOps Index)"),
                "Comment referencing Trinity Aura LDO DevOps Index should have been removed"
            )
        }

        @Test
        @DisplayName("Other trinity submodules are not accidentally removed")
        fun otherTrinityModulesNotRemoved() {
            // No other :trinity: modules were in the file to begin with,
            // but verify the :aura namespace modules remain (these are different from :trinity:aura)
            assertTrue(
                script.contains(":aura:reactivedesign:auraslab"),
                "Aura reactive design module auraslab should still be present"
            )
            assertTrue(
                script.contains(":aura:reactivedesign:chromacore"),
                "Aura reactive design module chromacore should still be present"
            )
        }

        @Test
        @DisplayName("Core module dependency is still present after trinity:aura removal")
        fun coreModuleDependencyUnaffected() {
            assertTrue(
                Regex("""implementation\s*\(\s*project\s*\(\s*":core-module"\s*\)\s*\)""")
                    .containsMatchIn(script),
                "Core module dependency should still be present"
            )
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 5. jose4j version downgraded to 0.9.4
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("jose4j CVE Version Constraint")
    inner class Jose4jVersionTests {

        @Test
        @DisplayName("jose4j is forced to version 0.9.4")
        fun jose4jForcedToVersion094() {
            assertTrue(
                script.contains("org.bitbucket.b_c:jose4j:0.9.4"),
                "jose4j should be forced to version 0.9.4"
            )
        }

        @Test
        @DisplayName("jose4j is NOT forced to the old 0.9.6 version")
        fun jose4jNotForcedTo096() {
            assertFalse(
                script.contains("org.bitbucket.b_c:jose4j:0.9.6"),
                "jose4j should not be pinned to 0.9.6 - it was downgraded to 0.9.4"
            )
        }

        @Test
        @DisplayName("jose4j force constraint is inside resolutionStrategy")
        fun jose4jForceInsideResolutionStrategy() {
            val resolutionBlock = Regex(
                """resolutionStrategy\s*\{(.*?)^\s*\}""",
                setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.MULTILINE)
            ).find(script)

            assertNotNull(resolutionBlock, "resolutionStrategy block should exist")
            assertTrue(
                resolutionBlock!!.value.contains("jose4j:0.9.4"),
                "jose4j:0.9.4 force should be inside resolutionStrategy"
            )
        }

        @Test
        @DisplayName("Other CVE-related forced versions are still present")
        fun otherCveVersionsPreserved() {
            val expectedForced = listOf(
                "org.jdom:jdom2:2.0.6.1",
                "org.apache.commons:commons-lang3:3.17.0",
                "com.google.guava:guava:33.3.0-jre",
                "org.bouncycastle:bcprov-jdk18on:1.78"
            )
            expectedForced.forEach { dep ->
                assertTrue(
                    script.contains(dep),
                    "CVE-forced dependency $dep should still be present"
                )
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Regression: ensure valid parts of build file are still intact
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Regression - Critical Build Config Preserved")
    inner class RegressionTests {

        @Test
        @DisplayName("Application namespace is unchanged")
        fun applicationNamespaceUnchanged() {
            assertTrue(
                Regex("""namespace\s*=\s*"dev\.aurakai\.auraframefx"""").containsMatchIn(script),
                "Application namespace should be unchanged"
            )
        }

        @Test
        @DisplayName("Firebase BOM is still present after ksp moshi removal")
        fun firebaseBomPreserved() {
            assertTrue(
                Regex("""implementation\s*\(\s*platform\s*\(\s*libs\.firebase\.bom\s*\)\s*\)""")
                    .containsMatchIn(script),
                "Firebase BOM should still be present"
            )
        }

        @Test
        @DisplayName("Hilt compiler KSP configuration is preserved")
        fun hiltCompilerKspPreserved() {
            assertTrue(
                Regex("""ksp\s*\(\s*libs\.hilt\.android\.compiler\s*\)""").containsMatchIn(script) ||
                Regex("""ksp\s*\(\s*libs\.hilt\.compiler\s*\)""").containsMatchIn(script),
                "Hilt compiler KSP should still be present"
            )
        }

        @Test
        @DisplayName("Room database compiler KSP is preserved")
        fun roomCompilerKspPreserved() {
            assertTrue(
                Regex("""ksp\s*\(\s*libs\.androidx\.room\.compiler\s*\)""").containsMatchIn(script),
                "Room compiler KSP should still be present"
            )
        }

        @Test
        @DisplayName("Build file has dependencies block")
        fun dependenciesBlockPresent() {
            assertTrue(
                Regex("""^\s*dependencies\s*\{""", RegexOption.MULTILINE).containsMatchIn(script),
                "Dependencies block should still be present"
            )
        }

        @Test
        @DisplayName("No accidental removal of core Compose BOM")
        fun composeBomPreserved() {
            assertTrue(
                Regex("""implementation\s*\(\s*platform\s*\(\s*libs\.androidx\.compose\.bom\s*\)\s*\)""")
                    .containsMatchIn(script),
                "Compose BOM should still be present"
            )
        }
    }
}