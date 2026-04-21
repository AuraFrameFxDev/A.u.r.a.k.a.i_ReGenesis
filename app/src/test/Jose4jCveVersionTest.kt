package dev.aurakai.auraframefx

/*
 * Testing framework and library:
 * - Using JUnit 5 (Jupiter) for unit tests (org.junit.jupiter.api.*).
 * - Tests validate the jose4j version pin change in this PR:
 *   * gradle/libs.versions.toml: jose4j version downgraded from 0.9.6 to 0.9.4
 *   * app/build.gradle.kts: force("org.bitbucket.b_c:jose4j:...") updated to 0.9.4
 *
 * Background: jose4j 0.9.4 is the pinned CVE-safe version. These tests guard against
 * accidental re-introduction of the 0.9.6 version or any other unreviewed upgrade.
 */

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("jose4j CVE Version Pin – PR Regression Tests")
class Jose4jCveVersionTest {

    // -------------------------------------------------------------------------
    // File helpers (mirror the pattern used in VersionCatalogChangesTest and
    // AppBuildGradleChangesTest so this file follows the same conventions).
    // -------------------------------------------------------------------------

    private fun locateVersionCatalog(): File {
        val candidates = listOf(
            File("gradle/libs.versions.toml"),
            File("../gradle/libs.versions.toml")
        )
        return candidates.firstOrNull { it.exists() } ?: error(
            "Unable to locate gradle/libs.versions.toml. Checked: ${candidates.joinToString { it.path }}"
        )
    }

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

    /** Expected pinned version for jose4j after this PR. */
    private val expectedJose4jVersion = "0.9.4"

    /** The version that was present before this PR and must no longer appear. */
    private val disallowedJose4jVersion = "0.9.6"

    private val catalogContent: String by lazy { locateVersionCatalog().readText() }
    private val buildScript: String by lazy { locateBuildFile().readText() }

    // =========================================================================
    // gradle/libs.versions.toml
    // =========================================================================

    @Nested
    @DisplayName("gradle/libs.versions.toml – [versions] section")
    inner class VersionCatalogVersionsTests {

        @Test
        @DisplayName("jose4j version is pinned to $expectedJose4jVersion")
        fun jose4jVersionIsPinnedToExpected() {
            val pattern = Regex("""jose4j\s*=\s*"$expectedJose4jVersion"""")
            assertTrue(
                pattern.containsMatchIn(catalogContent),
                "Expected jose4j = \"$expectedJose4jVersion\" in [versions] section of " +
                        "gradle/libs.versions.toml but it was not found."
            )
        }

        @Test
        @DisplayName("jose4j version is NOT $disallowedJose4jVersion (regression guard)")
        fun jose4jVersionIsNotDisallowed() {
            val disallowedPattern = Regex("""jose4j\s*=\s*"$disallowedJose4jVersion"""")
            assertFalse(
                disallowedPattern.containsMatchIn(catalogContent),
                "jose4j must NOT be set to \"$disallowedJose4jVersion\" – " +
                        "this version was reverted as part of a CVE fix."
            )
        }

        @Test
        @DisplayName("jose4j version entry exists exactly once in [versions]")
        fun jose4jVersionEntryExistsExactlyOnce() {
            val pattern = Regex("""jose4j\s*=\s*"[^"]+"""")
            val matches = pattern.findAll(catalogContent).toList()
            assertEquals(
                1,
                matches.size,
                "jose4j version should be declared exactly once in gradle/libs.versions.toml, " +
                        "found ${matches.size} occurrences."
            )
        }

        @Test
        @DisplayName("jose4j version value matches semantic version pattern")
        fun jose4jVersionFollowsSemanticVersioning() {
            val versionExtractPattern = Regex("""jose4j\s*=\s*"([^"]+)"""")
            val match = versionExtractPattern.find(catalogContent)
            assertNotNull(match, "jose4j version declaration should be present")
            val version = match!!.groupValues[1]
            assertTrue(
                version.matches(Regex("""[0-9]+\.[0-9]+(\.[0-9]+)?(-[a-zA-Z0-9._-]+)?""")),
                "jose4j version '$version' should follow semantic versioning (e.g. 0.9.4)"
            )
        }
    }

    // =========================================================================
    // gradle/libs.versions.toml – [libraries] section
    // =========================================================================

    @Nested
    @DisplayName("gradle/libs.versions.toml – [libraries] section")
    inner class VersionCatalogLibrariesTests {

        @Test
        @DisplayName("jose4j library entry exists in [libraries]")
        fun jose4jLibraryEntryExists() {
            assertTrue(
                Regex("""jose4j\s*=\s*\{""").containsMatchIn(catalogContent),
                "A jose4j library entry should be declared in the [libraries] section."
            )
        }

        @Test
        @DisplayName("jose4j library group is org.bitbucket.b_c")
        fun jose4jLibraryGroupIsCorrect() {
            val libraryEntryPattern = Regex(
                """jose4j\s*=\s*\{[^}]*\}""",
                RegexOption.DOT_MATCHES_ALL
            )
            val entry = libraryEntryPattern.find(catalogContent)
            assertNotNull(entry, "jose4j library entry should exist")
            assertTrue(
                entry!!.value.contains(""""org.bitbucket.b_c""""),
                "jose4j library group should be \"org.bitbucket.b_c\", got: ${entry.value}"
            )
        }

        @Test
        @DisplayName("jose4j library name is jose4j")
        fun jose4jLibraryNameIsCorrect() {
            val libraryEntryPattern = Regex(
                """jose4j\s*=\s*\{[^}]*\}""",
                RegexOption.DOT_MATCHES_ALL
            )
            val entry = libraryEntryPattern.find(catalogContent)
            assertNotNull(entry, "jose4j library entry should exist")
            assertTrue(
                entry!!.value.contains(""""jose4j""""),
                "jose4j library artifact name should be \"jose4j\", got: ${entry.value}"
            )
        }

        @Test
        @DisplayName("jose4j library references the jose4j version via version.ref")
        fun jose4jLibraryReferencesVersionRef() {
            val libraryEntryPattern = Regex(
                """jose4j\s*=\s*\{[^}]*\}""",
                RegexOption.DOT_MATCHES_ALL
            )
            val entry = libraryEntryPattern.find(catalogContent)
            assertNotNull(entry, "jose4j library entry should exist")
            assertTrue(
                entry!!.value.contains("""version.ref = "jose4j""""),
                "jose4j library should use version.ref = \"jose4j\" to stay in sync with " +
                        "the [versions] declaration, got: ${entry.value}"
            )
        }

        @Test
        @DisplayName("jose4j library does NOT hard-code version $disallowedJose4jVersion inline")
        fun jose4jLibraryDoesNotHardCodeDisallowedVersion() {
            val libraryEntryPattern = Regex(
                """jose4j\s*=\s*\{[^}]*\}""",
                RegexOption.DOT_MATCHES_ALL
            )
            val entry = libraryEntryPattern.find(catalogContent)
            assertNotNull(entry, "jose4j library entry should exist")
            assertFalse(
                entry!!.value.contains(disallowedJose4jVersion),
                "jose4j library entry must not reference the disallowed version " +
                        "\"$disallowedJose4jVersion\": ${entry.value}"
            )
        }
    }

    // =========================================================================
    // app/build.gradle.kts – resolutionStrategy
    // =========================================================================

    @Nested
    @DisplayName("app/build.gradle.kts – resolutionStrategy force()")
    inner class BuildGradleResolutionStrategyTests {

        @Test
        @DisplayName("resolutionStrategy forces jose4j to $expectedJose4jVersion")
        fun resolutionStrategyForcesExpectedVersion() {
            val pattern = Regex(
                """force\s*\(\s*"org\.bitbucket\.b_c:jose4j:$expectedJose4jVersion"\s*\)"""
            )
            assertTrue(
                pattern.containsMatchIn(buildScript),
                "Expected force(\"org.bitbucket.b_c:jose4j:$expectedJose4jVersion\") in " +
                        "resolutionStrategy block of app/build.gradle.kts."
            )
        }

        @Test
        @DisplayName("resolutionStrategy does NOT force jose4j to $disallowedJose4jVersion")
        fun resolutionStrategyDoesNotForceDisallowedVersion() {
            val disallowedPattern = Regex(
                """force\s*\(\s*"org\.bitbucket\.b_c:jose4j:$disallowedJose4jVersion"\s*\)"""
            )
            assertFalse(
                disallowedPattern.containsMatchIn(buildScript),
                "resolutionStrategy must NOT force jose4j to \"$disallowedJose4jVersion\". " +
                        "This version was reverted as part of a CVE fix."
            )
        }

        @Test
        @DisplayName("resolutionStrategy contains exactly one jose4j force() declaration")
        fun resolutionStrategyHasExactlyOneJose4jForce() {
            val pattern = Regex("""force\s*\(\s*"org\.bitbucket\.b_c:jose4j:[^"]+"\s*\)""")
            val count = pattern.findAll(buildScript).count()
            assertEquals(
                1,
                count,
                "There should be exactly one jose4j force() in app/build.gradle.kts, found $count."
            )
        }

        @Test
        @DisplayName("jose4j force() is within the CVE fixes comment block")
        fun jose4jForceIsInCveBlock() {
            val cveSectionPattern = Regex(
                """// CVE fixes.*?force\s*\(\s*"org\.bitbucket\.b_c:jose4j:$expectedJose4jVersion"\s*\)""",
                RegexOption.DOT_MATCHES_ALL
            )
            assertTrue(
                cveSectionPattern.containsMatchIn(buildScript),
                "The jose4j force() should appear in the '// CVE fixes' section of the " +
                        "resolutionStrategy block."
            )
        }

        @Test
        @DisplayName("jose4j force() version string has valid format")
        fun jose4jForceVersionHasValidFormat() {
            val versionExtractPattern = Regex(
                """force\s*\(\s*"org\.bitbucket\.b_c:jose4j:([^"]+)"\s*\)"""
            )
            val match = versionExtractPattern.find(buildScript)
            assertNotNull(match, "jose4j force() declaration should exist in app/build.gradle.kts")
            val version = match!!.groupValues[1]
            assertTrue(
                version.matches(Regex("""[0-9]+\.[0-9]+(\.[0-9]+)?(-[a-zA-Z0-9._-]+)?""")),
                "Forced jose4j version '$version' should be a valid semantic version."
            )
        }
    }

    // =========================================================================
    // Cross-file consistency
    // =========================================================================

    @Nested
    @DisplayName("Cross-file consistency")
    inner class CrossFileConsistencyTests {

        @Test
        @DisplayName("jose4j version in libs.versions.toml matches the forced version in build.gradle.kts")
        fun jose4jVersionsAreConsistentAcrossFiles() {
            val catalogVersionPattern = Regex("""jose4j\s*=\s*"([^"]+)"""")
            val buildForcePattern = Regex(
                """force\s*\(\s*"org\.bitbucket\.b_c:jose4j:([^"]+)"\s*\)"""
            )

            val catalogVersion = catalogVersionPattern.find(catalogContent)?.groupValues?.get(1)
            val buildVersion = buildForcePattern.find(buildScript)?.groupValues?.get(1)

            assertNotNull(catalogVersion, "jose4j version should be declared in libs.versions.toml")
            assertNotNull(buildVersion, "jose4j force() should be declared in app/build.gradle.kts")
            assertEquals(
                catalogVersion,
                buildVersion,
                "The jose4j version in libs.versions.toml (\"$catalogVersion\") must match " +
                        "the forced version in app/build.gradle.kts (\"$buildVersion\")."
            )
        }

        @Test
        @DisplayName("Both files agree on jose4j $expectedJose4jVersion (not $disallowedJose4jVersion)")
        fun bothFilesAgreeOnExpectedVersion() {
            val catalogHasExpected = Regex("""jose4j\s*=\s*"$expectedJose4jVersion"""")
                .containsMatchIn(catalogContent)
            val buildHasExpected = Regex(
                """force\s*\(\s*"org\.bitbucket\.b_c:jose4j:$expectedJose4jVersion"\s*\)"""
            ).containsMatchIn(buildScript)

            assertTrue(
                catalogHasExpected,
                "libs.versions.toml should declare jose4j = \"$expectedJose4jVersion\""
            )
            assertTrue(
                buildHasExpected,
                "app/build.gradle.kts should force jose4j to \"$expectedJose4jVersion\""
            )
        }

        @Test
        @DisplayName("Neither file references the disallowed jose4j version $disallowedJose4jVersion")
        fun neitherFileReferencesDisallowedVersion() {
            assertFalse(
                catalogContent.contains(disallowedJose4jVersion),
                "libs.versions.toml must not reference jose4j $disallowedJose4jVersion"
            )
            assertFalse(
                buildScript.contains(disallowedJose4jVersion),
                "app/build.gradle.kts must not reference jose4j $disallowedJose4jVersion"
            )
        }
    }
}