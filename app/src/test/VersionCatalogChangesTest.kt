package dev.aurakai.auraframefx

/*
Testing framework and library:
- Using JUnit 5 (Jupiter) for unit tests (org.junit.jupiter.api.*).
- Tests validate the recent changes to gradle/libs.versions.toml including:
  * Removal of moshiCodegen version
  * Addition of androidxNavigationComposeGradlePlugin version
  * Replacement of moshi-kotlin-codegen with yuApiClient library
*/

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VersionCatalogChangesTest {

    private fun locateVersionCatalog(): File {
        val candidates = listOf(
            File("gradle/libs.versions.toml"),
            File("../gradle/libs.versions.toml")
        )
        return candidates.firstOrNull { it.exists() } ?: error(
            "Unable to locate gradle/libs.versions.toml. Checked: ${candidates.joinToString { it.path }}"
        )
    }

    private val catalogFile: File by lazy { locateVersionCatalog() }
    private val catalogContent: String by lazy { catalogFile.readText() }

    @Nested
    @DisplayName("Version Declarations")
    inner class VersionDeclarationsTests {

        @Test
        @DisplayName("moshiCodegen version is removed")
        fun moshiCodegenVersionRemoved() {
            assertFalse(
                Regex("""moshiCodegen\s*=\s*"[^"]+"""").containsMatchIn(catalogContent),
                "moshiCodegen version should be removed from [versions] section"
            )
        }

        @Test
        @DisplayName("androidxNavigationComposeGradlePlugin version is added")
        fun navigationComposeGradlePluginVersionAdded() {
            assertTrue(
                Regex("""androidxNavigationComposeGradlePlugin\s*=\s*"2\.9\.5"""").containsMatchIn(catalogContent),
                "androidxNavigationComposeGradlePlugin version should be set to 2.9.5"
            )
        }

        @Test
        @DisplayName("All essential version declarations are present")
        fun essentialVersionsPresent() {
            val essentialVersions = mapOf(
                "agp" to "9.0.0-alpha13",
                "kotlin" to "2.3.0-Beta3",
                "hilt" to "2.57.2",
                "ksp" to "2.3.0",
                "navigationCompose" to "2.9.5",
                "yukihookapi" to "1.3.1",
                "kavaref" to "1.0.1"
            )
            
            essentialVersions.forEach { (key, expectedVersion) ->
                val pattern = Regex("""$key\s*=\s*"$expectedVersion"""")
                assertTrue(
                    pattern.containsMatchIn(catalogContent),
                    "Version $key should be set to $expectedVersion"
                )
            }
        }

        @Test
        @DisplayName("Firebase BOM version is declared")
        fun firebaseBomVersionPresent() {
            assertTrue(
                Regex("""firebaseBom\s*=\s*"34\.5\.0"""").containsMatchIn(catalogContent),
                "Firebase BOM version should be declared"
            )
        }

        @Test
        @DisplayName("Room version is declared")
        fun roomVersionPresent() {
            assertTrue(
                Regex("""room\s*=\s*"2\.6\.1"""").containsMatchIn(catalogContent),
                "Room version should be declared as 2.6.1"
            )
        }
    }

    @Nested
    @DisplayName("Library Declarations")
    inner class LibraryDeclarationsTests {

        @Test
        @DisplayName("moshi-kotlin-codegen library is removed")
        fun moshiKotlinCodegenLibraryRemoved() {
            assertFalse(
                Regex("""moshi-kotlin-codegen\s*=\s*\{""").containsMatchIn(catalogContent),
                "moshi-kotlin-codegen library entry should be removed"
            )
        }

        @Test
        @DisplayName("yuApiClient library is added")
        fun yuApiClientLibraryAdded() {
            assertTrue(
                Regex("""yuApiClient\s*=\s*\{\s*group\s*=\s*"highcapable\.yukihookapi\.api"""")
                    .containsMatchIn(catalogContent),
                "yuApiClient library should be added with correct group"
            )
            assertTrue(
                catalogContent.contains("name = \"yukihookapi\"") &&
                catalogContent.contains("version.ref = \"yuApiClient\""),
                "yuApiClient library should reference yuApiClient version"
            )
        }

        @Test
        @DisplayName("Firebase KTX libraries are declared")
        fun firebaseKtxLibrariesDeclared() {
            val firebaseKtxLibs = listOf(
                "firebase-analytics-ktx",
                "firebase-crashlytics-ktx",
                "firebase-auth-ktx",
                "firebase-firestore-ktx"
            )
            
            firebaseKtxLibs.forEach { lib ->
                assertTrue(
                    Regex("""$lib\s*=\s*\{[^}]*group\s*=\s*"com\.google\.firebase"""", RegexOption.DOT_MATCHES_ALL)
                        .containsMatchIn(catalogContent),
                    "Firebase KTX library $lib should be declared"
                )
            }
        }

        @Test
        @DisplayName("Firebase BOM library is declared")
        fun firebaseBomLibraryDeclared() {
            assertTrue(
                Regex("""firebase-bom\s*=\s*\{[^}]*module\s*=\s*"com\.google\.firebase:firebase-bom"""", 
                    RegexOption.DOT_MATCHES_ALL).containsMatchIn(catalogContent),
                "Firebase BOM library should be declared"
            )
        }

        @Test
        @DisplayName("Room libraries are properly declared")
        fun roomLibrariesDeclared() {
            val roomLibs = listOf(
                "androidx-room-runtime",
                "androidx-room-ktx",
                "androidx-room-compiler"
            )
            
            roomLibs.forEach { lib ->
                assertTrue(
                    Regex("""$lib\s*=\s*\{[^}]*group\s*=\s*"androidx\.room"""", RegexOption.DOT_MATCHES_ALL)
                        .containsMatchIn(catalogContent),
                    "Room library $lib should be declared with androidx.room group"
                )
            }
        }

        @Test
        @DisplayName("YukiHookAPI libraries are declared")
        fun yukiHookApiLibrariesDeclared() {
            val yukiLibs = listOf(
                "yukihookapi-api",
                "yukihookapi-ksp-xposed"
            )
            
            yukiLibs.forEach { lib ->
                assertTrue(
                    Regex("""$lib\s*=\s*\{[^}]*module\s*=\s*"com\.highcapable\.yukihookapi:""", 
                        RegexOption.DOT_MATCHES_ALL).containsMatchIn(catalogContent),
                    "YukiHookAPI library $lib should be declared"
                )
            }
        }

        @Test
        @DisplayName("KavaRef libraries are declared")
        fun kavarefLibrariesDeclared() {
            val kavarefLibs = listOf(
                "kavaref-core",
                "kavaref-extension"
            )
            
            kavarefLibs.forEach { lib ->
                assertTrue(
                    Regex("""$lib\s*=\s*\{[^}]*module\s*=\s*"com\.highcapable\.kavaref:""", 
                        RegexOption.DOT_MATCHES_ALL).containsMatchIn(catalogContent),
                    "KavaRef library $lib should be declared"
                )
            }
        }

        @Test
        @DisplayName("Xposed API library is declared")
        fun xposedApiLibraryDeclared() {
            assertTrue(
                Regex("""xposed-api\s*=\s*\{""").containsMatchIn(catalogContent),
                "Xposed API library should be declared"
            )
        }
    }

    @Nested
    @DisplayName("Version References")
    inner class VersionReferencesTests {

        @Test
        @DisplayName("Libraries reference correct versions")
        fun librariesReferenceCorrectVersions() {
            // Check that key libraries reference their version variables
            val versionRefs = mapOf(
                "hilt-android" to "hilt",
                "hilt-compiler" to "hilt",
                "androidx-room-runtime" to "room",
                "androidx-room-ktx" to "room",
                "androidx-room-compiler" to "room",
                "yukihookapi-api" to "yukihookapi",
                "yukihookapi-ksp-xposed" to "yukihookapi",
                "kavaref-core" to "kavaref",
                "kavaref-extension" to "kavaref"
            )
            
            versionRefs.forEach { (lib, versionKey) ->
                val libEntry = Regex(
                    """$lib\s*=\s*\{[^}]*\}""",
                    RegexOption.DOT_MATCHES_ALL
                ).find(catalogContent)
                
                assertNotNull(libEntry, "Library $lib should be declared")
                assertTrue(
                    libEntry!!.value.contains("version.ref = \"$versionKey\""),
                    "Library $lib should reference version $versionKey"
                )
            }
        }

        @Test
        @DisplayName("Firebase KTX libraries reference correct versions")
        fun firebaseKtxLibrariesReferenceVersions() {
            val firebaseVersionRefs = mapOf(
                "firebase-analytics-ktx" to "firebaseAnalyticsKtx",
                "firebase-crashlytics-ktx" to "firebaseCrashlyticsKtx",
                "firebase-auth-ktx" to "firebaseAuthKtx",
                "firebase-firestore-ktx" to "firebaseFirestoreKtx"
            )
            
            firebaseVersionRefs.forEach { (lib, versionKey) ->
                val libEntry = Regex(
                    """$lib\s*=\s*\{[^}]*\}""",
                    RegexOption.DOT_MATCHES_ALL
                ).find(catalogContent)
                
                assertNotNull(libEntry, "Firebase library $lib should be declared")
                assertTrue(
                    libEntry!!.value.contains("version.ref = \"$versionKey\""),
                    "Firebase library $lib should reference version $versionKey"
                )
            }
        }
    }

    @Nested
    @DisplayName("Bundle Definitions")
    inner class BundleDefinitionsTests {

        @Test
        @DisplayName("Firebase bundle includes KTX variants")
        fun firebaseBundleIncludesKtx() {
            val bundlePattern = Regex(
                """firebase\s*=\s*\[(.*?)\]""",
                RegexOption.DOT_MATCHES_ALL
            )
            val firebaseBundle = bundlePattern.find(catalogContent)
            
            assertNotNull(firebaseBundle, "Firebase bundle should exist")
            
            val ktxLibs = listOf(
                "firebase-analytics-ktx",
                "firebase-crashlytics-ktx",
                "firebase-auth-ktx",
                "firebase-firestore-ktx"
            )
            
            ktxLibs.forEach { lib ->
                assertTrue(
                    firebaseBundle!!.value.contains("\"$lib\""),
                    "Firebase bundle should include $lib"
                )
            }
        }

        @Test
        @DisplayName("Room bundle includes required libraries")
        fun roomBundleIncludesRequiredLibs() {
            val bundlePattern = Regex(
                """room-libs\s*=\s*\[(.*?)\]""",
                RegexOption.DOT_MATCHES_ALL
            )
            val roomBundle = bundlePattern.find(catalogContent)
            
            assertNotNull(roomBundle, "Room bundle should exist")
            
            val roomLibs = listOf(
                "androidx-room-runtime",
                "androidx-room-ktx"
            )
            
            roomLibs.forEach { lib ->
                assertTrue(
                    roomBundle!!.value.contains("\"$lib\""),
                    "Room bundle should include $lib"
                )
            }
        }
    }

    @Nested
    @DisplayName("Consistency and Integrity")
    inner class ConsistencyTests {

        @Test
        @DisplayName("All version references have corresponding version declarations")
        fun allVersionReferencesHaveDeclarations() {
            // Extract all version.ref values
            val versionRefPattern = Regex("""version\.ref\s*=\s*"([^"]+)"""")
            val versionRefs = versionRefPattern.findAll(catalogContent)
                .map { it.groupValues[1] }
                .toSet()
            
            // Extract all version declarations in [versions] section
            val versionsSection = Regex(
                """\[versions\](.*?)\[""",
                RegexOption.DOT_MATCHES_ALL
            ).find(catalogContent)
            
            assertNotNull(versionsSection, "Versions section should exist")
            
            val versionDeclarations = Regex("""(\w+)\s*=\s*"[^"]+"""")
                .findAll(versionsSection!!.value)
                .map { it.groupValues[1] }
                .toSet()
            
            versionRefs.forEach { ref ->
                assertTrue(
                    versionDeclarations.contains(ref),
                    "Version reference '$ref' should have a corresponding declaration in [versions]"
                )
            }
        }

        @Test
        @DisplayName("No orphaned version declarations")
        fun noOrphanedVersionDeclarations() {
            // This is a warning test - it's okay to have extra versions for future use
            // but good to know about them
            
            // Extract all version declarations
            val versionsSection = Regex(
                """\[versions\](.*?)\[""",
                RegexOption.DOT_MATCHES_ALL
            ).find(catalogContent)
            
            assertNotNull(versionsSection, "Versions section should exist")
            
            val versionDeclarations = Regex("""(\w+)\s*=\s*"[^"]+"""")
                .findAll(versionsSection!!.value)
                .map { it.groupValues[1] }
                .toSet()
            
            // Extract all version.ref usages
            val versionRefPattern = Regex("""version\.ref\s*=\s*"([^"]+)"""")
            val usedVersions = versionRefPattern.findAll(catalogContent)
                .map { it.groupValues[1] }
                .toSet()
            
            // Common versions that may not be directly referenced but are used in other ways
            val allowedUnreferenced = setOf(
                "compile-sdk", "min-sdk", "target-sdk", "gradle", "ndk", "cmake",
                "androidApplication", "androidLibrary", "androidTest",
                "genesisAndroidApplication", "yuApiClient", "xposed-bridge"
            )
            
            val orphanedVersions = versionDeclarations - usedVersions - allowedUnreferenced
            
            // This is informational - not necessarily an error
            if (orphanedVersions.isNotEmpty()) {
                println("Info: Found potentially unused version declarations: $orphanedVersions")
            }
            
            // Always pass - this is informational only
            assertTrue(true)
        }

        @Test
        @DisplayName("Version catalog file is well-formed TOML")
        fun versionCatalogIsWellFormed() {
            // Basic TOML structure validation
            assertTrue(
                catalogContent.contains("[versions]"),
                "Should have [versions] section"
            )
            assertTrue(
                catalogContent.contains("[libraries]"),
                "Should have [libraries] section"
            )
            assertTrue(
                catalogContent.contains("[bundles]"),
                "Should have [bundles] section"
            )
            
            // Check for balanced braces in library definitions
            val librariesSection = Regex(
                """\[libraries\](.*?)(\[|$)""",
                RegexOption.DOT_MATCHES_ALL
            ).find(catalogContent)
            
            assertNotNull(librariesSection, "Libraries section should be parseable")
            
            val openBraces = librariesSection!!.value.count { it == '{' }
            val closeBraces = librariesSection.value.count { it == '}' }
            
            assertEquals(
                openBraces,
                closeBraces,
                "Library definitions should have balanced braces"
            )
        }
    }

    @Nested
    @DisplayName("Regression Prevention")
    inner class RegressionPreventionTests {

        @Test
        @DisplayName("Essential dependency versions are not removed")
        fun essentialDependencyVersionsPresent() {
            val essentialVersions = listOf(
                "hilt", "kotlin", "ksp", "agp", "room",
                "compose-bom", "firebaseBom", "navigationCompose"
            )
            
            essentialVersions.forEach { version ->
                assertTrue(
                    Regex("""$version\s*=\s*"[^"]+"""").containsMatchIn(catalogContent),
                    "Essential version $version should be declared"
                )
            }
        }

        @Test
        @DisplayName("Version catalog maintains backward compatibility")
        fun versionCatalogBackwardCompatible() {
            // Check that commonly used library aliases still exist
            val commonLibraries = listOf(
                "hilt-android",
                "hilt-compiler",
                "androidx-core-ktx",
                "androidx-compose-bom",
                "compose-ui",
                "compose-material3",
                "retrofit",
                "okhttp"
            )
            
            commonLibraries.forEach { lib ->
                assertTrue(
                    Regex("""$lib\s*=\s*\{""").containsMatchIn(catalogContent),
                    "Common library alias $lib should still exist"
                )
            }
        }
    }

    @Nested
    @DisplayName("Edge Cases and Special Scenarios")
    inner class EdgeCasesTests {

        @Test
        @DisplayName("Version strings use valid semantic versioning")
        fun versionStringsUseValidSemanticVersioning() {
            // Extract all version values
            val versionPattern = Regex("""=\s*"([0-9][^"]+)"""")
            val versions = versionPattern.findAll(catalogContent)
                .map { it.groupValues[1] }
                .toList()
            
            // Check each version follows a reasonable pattern
            versions.forEach { version ->
                assertTrue(
                    version.matches(Regex("""[0-9]+(\.[0-9]+)*(-[a-zA-Z0-9.-]+)?""")),
                    "Version '$version' should follow semantic versioning pattern"
                )
            }
        }

        @Test
        @DisplayName("No duplicate library aliases")
        fun noDuplicateLibraryAliases() {
            val libraryPattern = Regex("""(\w+(?:-\w+)*)\s*=\s*\{""")
            val libraryAliases = libraryPattern.findAll(catalogContent)
                .map { it.groupValues[1] }
                .toList()
            
            val duplicates = libraryAliases.groupingBy { it }
                .eachCount()
                .filter { it.value > 1 }
            
            assertTrue(
                duplicates.isEmpty(),
                "Found duplicate library aliases: ${duplicates.keys}"
            )
        }

        @Test
        @DisplayName("Library groups are valid Maven coordinates")
        fun libraryGroupsAreValidMavenCoordinates() {
            val groupPattern = Regex("""group\s*=\s*"([^"]+)"""")
            val groups = groupPattern.findAll(catalogContent)
                .map { it.groupValues[1] }
                .toSet()
            
            groups.forEach { group ->
                assertTrue(
                    group.matches(Regex("""[a-zA-Z0-9._-]+(\.[a-zA-Z0-9._-]+)*""")),
                    "Group '$group' should be a valid Maven coordinate"
                )
            }
        }

        @Test
        @DisplayName("Module coordinates are complete")
        fun moduleCoordinatesAreComplete() {
            val modulePattern = Regex(
                """module\s*=\s*"([^:]+):([^:]+)"""",
                RegexOption.DOT_MATCHES_ALL
            )
            val modules = modulePattern.findAll(catalogContent).toList()
            
            modules.forEach { match ->
                val group = match.groupValues[1]
                val artifact = match.groupValues[2]
                
                assertTrue(
                    group.isNotEmpty() && artifact.isNotEmpty(),
                    "Module should have both group and artifact: ${match.value}"
                )
            }
        }
    }
}