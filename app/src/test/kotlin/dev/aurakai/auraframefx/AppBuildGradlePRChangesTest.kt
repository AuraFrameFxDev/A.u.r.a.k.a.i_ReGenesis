package dev.aurakai.auraframefx

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File

/**
 * Tests for app/build.gradle.kts PR changes.
 *
 * PR changes to app/build.gradle.kts:
 * 1. Removed ksp { arg("moshi.generated.enum.companion", "false") } block
 * 2. Removed androidResources { localeFilters += "en" }
 * 3. Changed force("org.bitbucket.b_c:jose4j:0.9.6") → force("org.bitbucket.b_c:jose4j:0.9.4")
 * 4. Removed implementation(project(":trinity:aura"))
 * 5. Removed buildTypes { getByName("debug") { isCrunchPngs = false } }
 *
 * These are text-based validations following the existing BuildGradleKtsTest pattern.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("App Build Gradle PR Changes Tests")
class AppBuildGradlePRChangesTest {

    private fun locateBuildFile(): File {
        val candidates = listOf(
            File("app/build.gradle.kts"),
            File("../app/build.gradle.kts"),
            File("build.gradle.kts")
        )
        return candidates.firstOrNull { it.exists() }
            ?: error(
                "Unable to locate app/build.gradle.kts. " +
                    "CWD=${System.getProperty("user.dir")}. " +
                    "Checked: ${candidates.joinToString { it.path }}"
            )
    }

    private val buildScript: String by lazy { locateBuildFile().readText() }

    // ──────────────────────────────────────────────────────────────────────────
    // jose4j version change tests (0.9.6 → 0.9.4)
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("jose4j Version Downgrade Tests (0.9.6 → 0.9.4)")
    inner class Jose4jVersionTests {

        @Test
        @DisplayName("jose4j should be forced to version 0.9.4")
        fun jose4jShouldBeVersion094() {
            assertTrue(
                buildScript.contains("jose4j:0.9.4"),
                "jose4j should be forced to version 0.9.4 in configurations.all block"
            )
        }

        @Test
        @DisplayName("jose4j version 0.9.6 should NOT be present (regression: PR changed this)")
        fun jose4jOldVersionShouldNotBePresent() {
            assertFalse(
                buildScript.contains("jose4j:0.9.6"),
                "jose4j 0.9.6 should have been replaced with 0.9.4 in this PR"
            )
        }

        @Test
        @DisplayName("jose4j force declaration should use org.bitbucket.b_c group")
        fun jose4jForceShouldUseCorrectGroup() {
            assertTrue(
                buildScript.contains("org.bitbucket.b_c:jose4j"),
                "jose4j force should use 'org.bitbucket.b_c:jose4j' group:artifact"
            )
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Removed KSP block tests
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("KSP Configuration Removal Tests")
    inner class KspConfigurationRemovalTests {

        @Test
        @DisplayName("moshi.generated.enum.companion KSP arg should NOT be present")
        fun moshiGeneratedEnumCompanionKspArgShouldNotBePresent() {
            assertFalse(
                buildScript.contains("moshi.generated.enum.companion"),
                "moshi.generated.enum.companion KSP arg should have been removed from app/build.gradle.kts"
            )
        }

        @Test
        @DisplayName("Standalone ksp block with moshi arg should NOT be present")
        fun standaloneKspBlockWithMoshiArgShouldNotBePresent() {
            // The removed block was: ksp { arg("moshi.generated.enum.companion", "false") }
            assertFalse(
                buildScript.contains("\"moshi.generated.enum.companion\""),
                "moshi.generated.enum.companion argument should have been removed"
            )
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Removed locale filters tests
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Locale Filters Removal Tests")
    inner class LocaleFiltersRemovalTests {

        @Test
        @DisplayName("localeFilters should NOT restrict to 'en' only")
        fun localeFiltersShouldNotRestrictToEnglish() {
            // The removed block was: androidResources { localeFilters += "en" }
            assertFalse(
                buildScript.contains("localeFilters += \"en\""),
                "localeFilters += \"en\" should have been removed to support all locales"
            )
        }

        @Test
        @DisplayName("androidResources block with localeFilters should NOT be present")
        fun androidResourcesLocaleFilterBlockShouldNotBePresent() {
            // Check for the specific pattern that was removed
            val hasLocaleFilterPattern = buildScript.contains("localeFilters") &&
                buildScript.contains("\"en\"") &&
                // But only if they appear together in context
                buildScript.contains("localeFilters += \"en\"")

            assertFalse(
                hasLocaleFilterPattern,
                "The androidResources localeFilters = [\"en\"] block should have been removed"
            )
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Removed trinity:aura dependency tests
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Trinity Aura Dependency Removal Tests")
    inner class TrinityAuraDependencyRemovalTests {

        @Test
        @DisplayName("trinity:aura module dependency should NOT be present")
        fun trinityAuraDependencyShouldNotBePresent() {
            assertFalse(
                buildScript.contains(":trinity:aura"),
                "implementation(project(\":trinity:aura\")) should have been removed from dependencies"
            )
        }

        @Test
        @DisplayName("trinity:aura LDO DevOps Index comment should NOT be present")
        fun trinityAuraLdoDevOpsCommentShouldNotBePresent() {
            // The removed code also had: // Trinity → Aura (LDO DevOps Index) comment
            assertFalse(
                buildScript.contains("Trinity → Aura (LDO DevOps Index)"),
                "Trinity → Aura LDO DevOps Index comment should have been removed"
            )
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Removed debug buildTypes block tests
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Debug BuildTypes isCrunchPngs Removal Tests")
    inner class DebugBuildTypesRemovalTests {

        @Test
        @DisplayName("isCrunchPngs = false should NOT be in buildTypes debug block")
        fun isCrunchPngsShouldNotBePresent() {
            assertFalse(
                buildScript.contains("isCrunchPngs = false"),
                "isCrunchPngs = false should have been removed from debug buildType configuration"
            )
        }

        @Test
        @DisplayName("isCrunchPngs should not appear in build script at all")
        fun isCrunchPngsShouldNotAppearAtAll() {
            assertFalse(
                buildScript.contains("isCrunchPngs"),
                "isCrunchPngs property should not be present in app/build.gradle.kts"
            )
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Preserved dependencies that should still be present
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Preserved Dependencies Tests")
    inner class PreservedDependenciesTests {

        @Test
        @DisplayName("core-module dependency should still be present")
        fun coreModuleDependencyShouldBePresent() {
            assertTrue(
                buildScript.contains(":core-module"),
                "implementation(project(\":core-module\")) should be preserved"
            )
        }

        @Test
        @DisplayName("guava forced version should still be present")
        fun guavaForcedVersionShouldBePresent() {
            assertTrue(
                buildScript.contains("com.google.guava:guava"),
                "Guava force declaration should be preserved in configurations.all block"
            )
        }

        @Test
        @DisplayName("bouncycastle forced version should still be present")
        fun bouncycastleForcedVersionShouldBePresent() {
            assertTrue(
                buildScript.contains("bcprov-jdk18on"),
                "Bouncycastle force declaration should be preserved"
            )
        }

        @Test
        @DisplayName("jdom2 forced version should still be present")
        fun jdom2ForcedVersionShouldBePresent() {
            assertTrue(
                buildScript.contains("jdom2"),
                "jdom2 force declaration should be preserved"
            )
        }

        @Test
        @DisplayName("commons-lang3 forced version should still be present")
        fun commonsLang3ForcedVersionShouldBePresent() {
            assertTrue(
                buildScript.contains("commons-lang3"),
                "commons-lang3 force declaration should be preserved"
            )
        }

        @Test
        @DisplayName("Firebase BOM implementation should still be present")
        fun firebaseBomShouldBePresent() {
            assertTrue(
                buildScript.contains("firebase.bom"),
                "Firebase BOM dependency should be preserved"
            )
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Core configuration that should be preserved
    // ──────────────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Core Build Configuration Tests")
    inner class CoreBuildConfigurationTests {

        @Test
        @DisplayName("namespace should still be dev.aurakai.auraframefx")
        fun namespaceShouldBePreserved() {
            assertTrue(
                buildScript.contains("dev.aurakai.auraframefx"),
                "namespace should remain 'dev.aurakai.auraframefx'"
            )
        }

        @Test
        @DisplayName("applicationId should still be dev.aurakai.auraframefx")
        fun applicationIdShouldBePreserved() {
            assertTrue(
                Regex("""applicationId\s*=\s*"dev\.aurakai\.auraframefx"""").containsMatchIn(buildScript),
                "applicationId should remain 'dev.aurakai.auraframefx'"
            )
        }
    }
}