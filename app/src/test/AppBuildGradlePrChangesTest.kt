package dev.aurakai.auraframefx

/*
 * Tests validating the app/build.gradle.kts changes introduced in this pull request:
 *
 * Changes tested:
 *  1. Removal of the top-level ksp{} block that set the moshi.generated.enum.companion argument
 *  2. Removal of androidResources { localeFilters += "en" }
 *  3. Removal of buildTypes { debug { isCrunchPngs = false } }
 *  4. Removal of implementation(project(":trinity:aura")) dependency
 *  5. Downgrade of jose4j forced version from 0.9.6 to 0.9.4
 *
 * Framework: JUnit 5 (Jupiter)
 * File parsing: reads app/build.gradle.kts as raw text and validates with regex/string checks
 */

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("app/build.gradle.kts — PR Changes")
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

    // ─── Moshi KSP argument block ────────────────────────────────────────────

    @Nested
    @DisplayName("Moshi KSP argument block removed")
    inner class MoshiKspArgBlockTests {

        @Test
        @DisplayName("Top-level ksp{} block with moshi argument is absent")
        fun moshiKspArgBlockIsAbsent() {
            assertFalse(
                script.contains("moshi.generated.enum.companion"),
                "The moshi.generated.enum.companion KSP argument should have been removed"
            )
        }

        @Test
        @DisplayName("No standalone top-level ksp{} block that precedes extensions.configure")
        fun noStandaloneKspBlockBeforeExtensionsConfigure() {
            // The removed block was placed before the extensions.configure block.
            // Verify that if a ksp{} token exists it is only inside dependencies, not top-level.
            val topLevelKspBlock = Regex("""^ksp\s*\{""", RegexOption.MULTILINE)
            assertFalse(
                topLevelKspBlock.containsMatchIn(script),
                "There should be no top-level ksp{} block outside of dependencies"
            )
        }

        @Test
        @DisplayName("moshi-generated-enum-companion argument string is not present in any form")
        fun moshiEnumCompanionArgStringAbsent() {
            assertFalse(
                script.contains("\"moshi.generated.enum.companion\""),
                "The moshi.generated.enum.companion string argument should be absent"
            )
        }
    }

    // ─── androidResources locale filter ──────────────────────────────────────

    @Nested
    @DisplayName("androidResources localeFilters removed")
    inner class LocaleFiltersTests {

        @Test
        @DisplayName("localeFilters assignment is absent from build script")
        fun localeFiltersAssignmentAbsent() {
            assertFalse(
                script.contains("localeFilters"),
                "localeFilters should have been removed from androidResources block"
            )
        }

        @Test
        @DisplayName("androidResources block does not contain English-only locale restriction")
        fun noEnglishOnlyLocaleRestriction() {
            assertFalse(
                script.contains("localeFilters += \"en\""),
                "The English-only locale filter localeFilters += \"en\" should be absent"
            )
        }

        @Test
        @DisplayName("androidResources block itself is removed (no androidResources block present)")
        fun androidResourcesBlockAbsent() {
            assertFalse(
                Regex("""androidResources\s*\{""").containsMatchIn(script),
                "The androidResources { } block should be absent after removing localeFilters"
            )
        }
    }

    // ─── Debug buildType isCrunchPngs ─────────────────────────────────────────

    @Nested
    @DisplayName("Debug buildType isCrunchPngs configuration removed")
    inner class DebugBuildTypeCrunchPngsTests {

        @Test
        @DisplayName("isCrunchPngs property is absent from build script")
        fun isCrunchPngsAbsent() {
            assertFalse(
                script.contains("isCrunchPngs"),
                "isCrunchPngs should have been removed from the debug buildType block"
            )
        }

        @Test
        @DisplayName("Debug buildType block that only contained isCrunchPngs is removed")
        fun debugBuildTypeBlockWithCrunchPngsAbsent() {
            assertFalse(
                Regex("""getByName\s*\(\s*"debug"\s*\)\s*\{[^}]*isCrunchPngs""").containsMatchIn(script),
                "The debug buildType block setting isCrunchPngs = false should be absent"
            )
        }

        @Test
        @DisplayName("buildTypes block containing only the debug isCrunchPngs entry is removed")
        fun standaloneOuterBuildTypesBlockAbsent() {
            // The removed block was an outer buildTypes {} that only set isCrunchPngs.
            // The script should not have a buildTypes block outside of the cmake externalNativeBuild.
            val outerBuildTypesCount = Regex("""^\s{4}buildTypes\s*\{""", RegexOption.MULTILINE)
                .findAll(script)
                .count()
            // The remaining build type config is nested inside cmake, not at the
            // ApplicationExtension level directly. Accept 0 or that it only appears inside cmake.
            assertTrue(
                outerBuildTypesCount == 0 || script.indexOf("buildTypes") > script.indexOf("cmake"),
                "No top-level buildTypes block with isCrunchPngs should remain outside cmake nesting"
            )
        }
    }

    // ─── trinity:aura module dependency ───────────────────────────────────────

    @Nested
    @DisplayName("trinity:aura project dependency removed")
    inner class TrinityAuraDependencyTests {

        @Test
        @DisplayName("implementation(project(':trinity:aura')) is absent")
        fun trinityAuraDependencyAbsent() {
            assertFalse(
                script.contains("\":trinity:aura\""),
                "The :trinity:aura module dependency should have been removed"
            )
        }

        @Test
        @DisplayName("No implementation referencing any trinity submodule")
        fun noTrinityModuleDependency() {
            assertFalse(
                Regex("""implementation\s*\(\s*project\s*\(\s*":trinity""").containsMatchIn(script),
                "No trinity module should be declared as an implementation dependency"
            )
        }

        @Test
        @DisplayName("LDO DevOps Index comment referencing trinity:aura is absent")
        fun ldoDevOpsIndexCommentAbsent() {
            assertFalse(
                script.contains("Trinity → Aura"),
                "The 'Trinity → Aura (LDO DevOps Index)' comment should be absent along with the dependency"
            )
        }
    }

    // ─── jose4j version pin ────────────────────────────────────────────────────

    @Nested
    @DisplayName("jose4j forced to version 0.9.4 (downgraded from 0.9.6)")
    inner class Jose4jVersionTests {

        @Test
        @DisplayName("jose4j is forced to 0.9.4")
        fun jose4jForcedTo094() {
            assertTrue(
                script.contains("jose4j:0.9.4"),
                "jose4j should be forced to version 0.9.4 in resolutionStrategy"
            )
        }

        @Test
        @DisplayName("jose4j is not forced to old version 0.9.6")
        fun jose4jNotForcedTo096() {
            assertFalse(
                script.contains("jose4j:0.9.6"),
                "jose4j 0.9.6 should not appear — it was downgraded to 0.9.4"
            )
        }

        @Test
        @DisplayName("jose4j force declaration uses the correct group and artifact")
        fun jose4jForceDeclarationCorrect() {
            assertTrue(
                script.contains("org.bitbucket.b_c:jose4j:0.9.4"),
                "Expected force(\"org.bitbucket.b_c:jose4j:0.9.4\") in resolutionStrategy"
            )
        }

        @Test
        @DisplayName("Exactly one jose4j force entry is present")
        fun exactlyOneJose4jForceEntry() {
            val count = Regex("""force\s*\(\s*"org\.bitbucket\.b_c:jose4j:[^"]+"\s*\)""")
                .findAll(script).count()
            assertEquals(
                1, count,
                "There should be exactly one jose4j force entry, found $count"
            )
        }
    }

    // ─── Regression: previously present items still intact ───────────────────

    @Nested
    @DisplayName("Regression: previously intact configuration remains unchanged")
    inner class RegressionTests {

        @Test
        @DisplayName("core-module project dependency is still present")
        fun coreModuleDependencyPresent() {
            assertTrue(
                script.contains("\":core-module\""),
                "The :core-module dependency must still be present"
            )
        }

        @Test
        @DisplayName("Bouncycastle force versions are still in resolutionStrategy")
        fun bouncycastleForceVersionsPresent() {
            assertTrue(
                script.contains("bcprov-jdk18on:1.78"),
                "Bouncycastle bcprov-jdk18on:1.78 CVE fix should still be present"
            )
            assertTrue(
                script.contains("bcpkix-jdk18on:1.78"),
                "Bouncycastle bcpkix-jdk18on:1.78 CVE fix should still be present"
            )
        }

        @Test
        @DisplayName("firebase.crashlytics plugin is still declared")
        fun firebaseCrashlyticPluginPresent() {
            assertTrue(
                script.contains("firebase.crashlytics"),
                "firebase.crashlytics plugin alias should remain declared"
            )
        }

        @Test
        @DisplayName("Namespace is still set to dev.aurakai.auraframefx")
        fun namespacePresentAndCorrect() {
            assertTrue(
                script.contains("namespace = \"dev.aurakai.auraframefx\""),
                "App namespace should still be dev.aurakai.auraframefx"
            )
        }

        @Test
        @DisplayName("resolutionStrategy block still contains netty force entries")
        fun nettyForceEntriesPresent() {
            assertTrue(
                script.contains("netty-codec-http2:4.2.0.Final"),
                "Netty HTTP/2 force entry should still be present in resolutionStrategy"
            )
        }
    }
}