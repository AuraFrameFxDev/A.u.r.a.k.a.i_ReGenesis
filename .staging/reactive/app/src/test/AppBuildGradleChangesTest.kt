package dev.aurakai.auraframefx

/*
Testing framework and library:
- Using JUnit 5 (Jupiter) for unit tests (org.junit.jupiter.api.*).
- Tests validate the recent changes to app/build.gradle.kts including:
  * Xposed/YukiHook/KavaRef dependencies
  * Room KSP configuration
  * Firebase KTX migration
  * Moshi removal
  * Core library desugaring
  * Dependency organization and deduplication
*/

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AppBuildGradleChangesTest {

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

    @Nested
    @DisplayName("Xposed and Hooking Dependencies")
    inner class XposedAndHookingDependenciesTests {

        @Test
        @DisplayName("Xposed API is declared as compileOnly")
        fun xposedApiIsCompileOnly() {
            assertTrue(
                Regex("""compileOnly\s*\(\s*libs\.xposed\.api\s*\)""").containsMatchIn(script),
                "Expected compileOnly(libs.xposed.api) to be present"
            )
            assertFalse(
                Regex("""implementation\s*\(\s*libs\.xposed\.api\s*\)""").containsMatchIn(script),
                "Xposed API should be compileOnly, not implementation"
            )
        }

        @Test
        @DisplayName("YukiHookAPI is properly configured")
        fun yukiHookApiIsConfigured() {
            assertTrue(
                Regex("""implementation\s*\(\s*libs\.yukihookapi\.api\s*\)""").containsMatchIn(script),
                "Expected implementation(libs.yukihookapi.api) to be present"
            )
            assertTrue(
                Regex("""ksp\s*\(\s*libs\.yukihookapi\.ksp\.xposed\s*\)""").containsMatchIn(script),
                "Expected ksp(libs.yukihookapi.ksp.xposed) to be present"
            )
        }

        @Test
        @DisplayName("KavaRef libraries are included")
        fun kavaRefLibrariesAreIncluded() {
            assertTrue(
                Regex("""implementation\s*\(\s*libs\.kavaref\.core\s*\)""").containsMatchIn(script),
                "Expected implementation(libs.kavaref.core) to be present"
            )
            assertTrue(
                Regex("""implementation\s*\(\s*libs\.kavaref\.extension\s*\)""").containsMatchIn(script),
                "Expected implementation(libs.kavaref.extension) to be present"
            )
        }

        @Test
        @DisplayName("Local Xposed API JAR files are referenced correctly")
        fun localXposedJarsAreReferenced() {
            assertTrue(
                Regex("""compileOnly\s*\(\s*files\s*\(\s*"libs/api-82\.jar"\s*\)\s*\)""").containsMatchIn(script),
                "Expected compileOnly(files(\"libs/api-82.jar\")) to be present"
            )
            assertTrue(
                Regex("""compileOnly\s*\(\s*files\s*\(\s*"libs/api-82-sources\.jar"\s*\)\s*\)""").containsMatchIn(script),
                "Expected compileOnly(files(\"libs/api-82-sources.jar\")) to be present"
            )
            // Ensure old $projectDir style is not used
            assertFalse(
                script.contains("\$projectDir/libs/api-82.jar"),
                "Should not use \$projectDir prefix for local JAR files"
            )
        }

        @Test
        @DisplayName("Hooking dependencies are positioned before Compose dependencies")
        fun hookingDependenciesAreBeforeCompose() {
            val xposedIndex = script.indexOf("compileOnly(libs.xposed.api)")
            val composeIndex = script.indexOf("implementation(platform(libs.androidx.compose.bom))")
            
            assertTrue(xposedIndex > 0, "Xposed dependency should be present")
            assertTrue(composeIndex > 0, "Compose BOM should be present")
            assertTrue(
                xposedIndex < composeIndex,
                "Hooking dependencies should be declared before Compose dependencies"
            )
        }
    }

    @Nested
    @DisplayName("Room Database KSP Configuration")
    inner class RoomKspConfigurationTests {

        @Test
        @DisplayName("Room compiler is configured with KSP")
        fun roomCompilerUsesKsp() {
            assertTrue(
                Regex("""ksp\s*\(\s*libs\.androidx\.room\.compiler\s*\)""").containsMatchIn(script),
                "Expected ksp(libs.androidx.room.compiler) to be present"
            )
        }

        @Test
        @DisplayName("Room runtime and KTX are not duplicated as implementation")
        fun roomRuntimeNotRedundant() {
            // Room runtime/ktx should only appear in implementation, not as separate lines
            val roomRuntimeMatches = Regex("""implementation\s*\(\s*libs\.androidx\.room\.runtime\s*\)""")
                .findAll(script).count()
            val roomKtxMatches = Regex("""implementation\s*\(\s*libs\.androidx\.room\.ktx\s*\)""")
                .findAll(script).count()
            
            // These should not be explicitly listed since they're likely in a bundle or not needed
            // The key is Room compiler with KSP
            assertTrue(
                roomRuntimeMatches <= 1,
                "Room runtime should not be duplicated in dependencies"
            )
            assertTrue(
                roomKtxMatches <= 1,
                "Room KTX should not be duplicated in dependencies"
            )
        }

        @Test
        @DisplayName("Room compiler is not used as implementation")
        fun roomCompilerNotImplementation() {
            assertFalse(
                Regex("""implementation\s*\(\s*libs\.androidx\.room\.compiler\s*\)""").containsMatchIn(script),
                "Room compiler should use KSP, not implementation"
            )
        }

        @Test
        @DisplayName("Navigation compose does not use KSP")
        fun navigationComposeDoesNotUseKsp() {
            assertFalse(
                Regex("""ksp\s*\(\s*libs\.androidx\.navigation\.compose\s*\)""").containsMatchIn(script),
                "Navigation Compose should not use KSP (removed incorrect usage)"
            )
        }

        @Test
        @DisplayName("YukiHookAPI does not use KSP for api module")
        fun yukiHookApiDoesNotUseKsp() {
            assertFalse(
                Regex("""ksp\s*\(\s*libs\.yukihookapi\.api\s*\)""").containsMatchIn(script),
                "YukiHookAPI API should not use KSP - only ksp.xposed should"
            )
        }
    }

    @Nested
    @DisplayName("Firebase KTX Migration")
    inner class FirebaseKtxMigrationTests {

        @Test
        @DisplayName("Firebase BOM platform is declared")
        fun firebaseBomIsPresent() {
            assertTrue(
                Regex("""implementation\s*\(\s*platform\s*\(\s*libs\.firebase\.bom\s*\)\s*\)""").containsMatchIn(script),
                "Expected implementation(platform(libs.firebase.bom)) to be present"
            )
        }

        @Test
        @DisplayName("Firebase dependencies use KTX variants")
        fun firebaseUsesKtxVariants() {
            val ktxDependencies = listOf(
                "firebase.analytics.ktx",
                "firebase.crashlytics.ktx",
                "firebase.auth.ktx",
                "firebase.firestore.ktx"
            )
            
            ktxDependencies.forEach { dep ->
                assertTrue(
                    Regex("""implementation\s*\(\s*libs\.$dep\s*\)""").containsMatchIn(script),
                    "Expected implementation(libs.$dep) to be present"
                )
            }
        }

        @Test
        @DisplayName("Non-KTX Firebase dependencies are removed")
        fun nonKtxFirebaseDependenciesRemoved() {
            // Check for old non-KTX patterns (without the .ktx suffix)
            val nonKtxPattern = Regex("""implementation\s*\(\s*libs\.firebase\.(analytics|crashlytics|auth|firestore)\s*\)""")
            val matches = nonKtxPattern.findAll(script)
            
            // Should only find KTX variants, not plain ones
            matches.forEach { match ->
                val fullMatch = match.value
                assertFalse(
                    !fullMatch.contains(".ktx"),
                    "Found non-KTX Firebase dependency: $fullMatch"
                )
            }
        }

        @Test
        @DisplayName("Firebase dependencies are not duplicated")
        fun firebaseDependenciesNotDuplicated() {
            val ktxDependencies = listOf(
                "firebase.analytics.ktx",
                "firebase.crashlytics.ktx",
                "firebase.auth.ktx",
                "firebase.firestore.ktx"
            )
            
            ktxDependencies.forEach { dep ->
                val pattern = Regex("""implementation\s*\(\s*libs\.$dep\s*\)""")
                val count = pattern.findAll(script).count()
                assertEquals(
                    1,
                    count,
                    "Firebase dependency libs.$dep should appear exactly once, found $count times"
                )
            }
        }
    }

    @Nested
    @DisplayName("Moshi Removal")
    inner class MoshiRemovalTests {

        @Test
        @DisplayName("Moshi dependencies are removed")
        fun moshiDependenciesRemoved() {
            assertFalse(
                Regex("""implementation\s*\(\s*libs\.moshi\s*\)""").containsMatchIn(script),
                "Moshi should be removed from dependencies"
            )
            assertFalse(
                Regex("""implementation\s*\(\s*libs\.moshi\.kotlin\s*\)""").containsMatchIn(script),
                "Moshi Kotlin should be removed from dependencies"
            )
        }

        @Test
        @DisplayName("Retrofit Moshi converter is removed")
        fun retrofitMoshiConverterRemoved() {
            assertFalse(
                Regex("""implementation\s*\(\s*libs\.retrofit\.converter\.moshi\s*\)""").containsMatchIn(script),
                "Retrofit Moshi converter should be removed from dependencies"
            )
        }

        @Test
        @DisplayName("Moshi KSP code generation is removed")
        fun moshiKspCodegenRemoved() {
            assertFalse(
                script.contains("moshi-kotlin-codegen"),
                "Moshi Kotlin codegen should be removed"
            )
            assertFalse(
                Regex("""ksp\s*\(\s*"com\.squareup\.moshi:moshi-kotlin-codegen:.*?"\s*\)""").containsMatchIn(script),
                "Hardcoded Moshi codegen KSP should be removed"
            )
        }

        @Test
        @DisplayName("Kotlinx serialization is used for Retrofit")
        fun kotlinxSerializationUsedForRetrofit() {
            assertTrue(
                Regex("""implementation\s*\(\s*libs\.retrofit\.converter\.kotlinx\.serialization\s*\)""")
                    .containsMatchIn(script),
                "Expected Kotlinx Serialization converter for Retrofit"
            )
        }
    }

    @Nested
    @DisplayName("Core Library Desugaring")
    inner class CoreLibraryDesugaringTests {

        @Test
        @DisplayName("Core library desugaring uses correct configuration")
        fun coreLibraryDesugaringUsesCorrectConfig() {
            assertTrue(
                Regex("""coreLibraryDesugaring\s*\(\s*libs\.desugar\.jdk\.libs\s*\)""").containsMatchIn(script),
                "Expected coreLibraryDesugaring(libs.desugar.jdk.libs) to be present"
            )
        }

        @Test
        @DisplayName("Desugar JDK libs does not use implementation")
        fun desugarDoesNotUseImplementation() {
            assertFalse(
                Regex("""implementation\s*\(\s*libs\.desugar\.jdk\.libs\s*\)""").containsMatchIn(script),
                "Desugar JDK libs should use coreLibraryDesugaring, not implementation"
            )
        }
    }

    @Nested
    @DisplayName("Dependency Organization and Deduplication")
    inner class DependencyOrganizationTests {

        @Test
        @DisplayName("Lifecycle dependencies are not duplicated")
        fun lifecycleDependenciesNotDuplicated() {
            val lifecycleDeps = listOf(
                "androidx.lifecycle.runtime.ktx",
                "androidx.lifecycle.viewmodel.ktx",
                "androidx.lifecycle.viewmodel.compose",
                "androidx.lifecycle.runtime.compose"
            )
            
            lifecycleDeps.forEach { dep ->
                val pattern = Regex("""implementation\s*\(\s*libs\.$dep\s*\)""")
                val count = pattern.findAll(script).count()
                assertTrue(
                    count <= 1,
                    "Lifecycle dependency libs.$dep should not be duplicated, found $count times"
                )
            }
        }

        @Test
        @DisplayName("Libsu dependencies are not duplicated")
        fun libsuDependenciesNotDuplicated() {
            val libsuDeps = listOf("libsu.core", "libsu.io", "libsu.service")
            
            libsuDeps.forEach { dep ->
                val pattern = Regex("""implementation\s*\(\s*libs\.$dep\s*\)""")
                val count = pattern.findAll(script).count()
                assertEquals(
                    1,
                    count,
                    "Libsu dependency libs.$dep should appear exactly once, found $count times"
                )
            }
        }

        @Test
        @DisplayName("Hilt Work is declared only once")
        fun hiltWorkDeclaredOnce() {
            val pattern = Regex("""implementation\s*\(\s*libs\.androidx\.hilt\.work\s*\)""")
            val count = pattern.findAll(script).count()
            assertEquals(
                1,
                count,
                "Hilt Work should be declared exactly once, found $count times"
            )
        }

        @Test
        @DisplayName("Navigation Compose is declared only once")
        fun navigationComposeDeclaredOnce() {
            val pattern = Regex("""implementation\s*\(\s*libs\.androidx\.navigation\.compose\s*\)""")
            val count = pattern.findAll(script).count()
            assertEquals(
                1,
                count,
                "Navigation Compose should be declared exactly once, found $count times"
            )
        }

        @Test
        @DisplayName("Dependencies section has proper comments and grouping")
        fun dependenciesHaveProperComments() {
            val expectedComments = listOf(
                "// Compose",
                "// Hooking & reflection libraries",
                "// Hilt",
                "// Root/system utils",
                "// Firebase dependencies",
                "// Networking",
                "// Kotlin + utils",
                "// Internal project modules"
            )
            
            expectedComments.forEach { comment ->
                assertTrue(
                    script.contains(comment),
                    "Expected organizational comment: $comment"
                )
            }
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Conditions")
    inner class EdgeCasesTests {

        @Test
        @DisplayName("No hardcoded version strings in dependencies")
        fun noHardcodedVersions() {
            // Check for patterns like implementation("group:artifact:1.2.3")
            val hardcodedVersionPattern = Regex(
                """(implementation|compileOnly|ksp|testImplementation|androidTestImplementation)\s*\(\s*"[^"]+:[^"]+:\d+\.\d+[^"]*"\s*\)"""
            )
            val matches = hardcodedVersionPattern.findAll(script).toList()
            
            // Filter out the local JAR files which are acceptable
            val problematicMatches = matches.filter { match ->
                !match.value.contains("files(") && 
                !match.value.contains("libs/api-82")
            }
            
            assertTrue(
                problematicMatches.isEmpty(),
                "Found hardcoded versions: ${problematicMatches.joinToString { it.value }}"
            )
        }

        @Test
        @DisplayName("KSP is only used for annotation processors")
        fun kspOnlyForAnnotationProcessors() {
            val kspPattern = Regex("""ksp\s*\(\s*libs\.([^)]+)\s*\)""")
            val kspMatches = kspPattern.findAll(script).toList()
            
            val validKspDependencies = setOf(
                "androidx.room.compiler",
                "hilt.compiler",
                "yukihookapi.ksp.xposed"
            )
            
            kspMatches.forEach { match ->
                val dep = match.groupValues[1]
                assertTrue(
                    validKspDependencies.contains(dep),
                    "KSP should only be used for annotation processors, found: $dep"
                )
            }
        }

        @Test
        @DisplayName("All project module references are valid")
        fun projectModuleReferencesAreValid() {
            val projectPattern = Regex("""implementation\s*\(\s*project\s*\(\s*"([^"]+)"\s*\)\s*\)""")
            val projectMatches = projectPattern.findAll(script).toList()
            
            assertTrue(
                projectMatches.isNotEmpty(),
                "Should have at least one project module dependency"
            )
            
            // Verify format is correct (starts with colon)
            projectMatches.forEach { match ->
                val modulePath = match.groupValues[1]
                assertTrue(
                    modulePath.startsWith(":"),
                    "Project module path should start with colon: $modulePath"
                )
            }
        }

        @Test
        @DisplayName("Dependencies block exists and is properly structured")
        fun dependenciesBlockStructured() {
            assertTrue(
                Regex("""dependencies\s*\{""").containsMatchIn(script),
                "Dependencies block should exist"
            )
            
            // Check that there's content in the dependencies block
            val dependenciesBlockPattern = Regex(
                """dependencies\s*\{(.*?)\}""",
                RegexOption.DOT_MATCHES_ALL
            )
            val dependenciesBlock = dependenciesBlockPattern.find(script)
            
            assertNotNull(dependenciesBlock, "Dependencies block should be parseable")
            assertTrue(
                dependenciesBlock!!.groupValues[1].trim().isNotEmpty(),
                "Dependencies block should not be empty"
            )
        }
    }

    @Nested
    @DisplayName("Regression Prevention")
    inner class RegressionPreventionTests {

        @Test
        @DisplayName("Compose BOM is still declared")
        fun composeBomPresent() {
            assertTrue(
                Regex("""implementation\s*\(\s*platform\s*\(\s*libs\.androidx\.compose\.bom\s*\)\s*\)""")
                    .containsMatchIn(script),
                "Compose BOM should be present"
            )
        }

        @Test
        @DisplayName("Essential Compose dependencies are present")
        fun essentialComposeDependenciesPresent() {
            val composeDeps = listOf(
                "compose.ui",
                "compose.material3",
                "compose.animation"
            )
            
            composeDeps.forEach { dep ->
                assertTrue(
                    Regex("""implementation\s*\(\s*libs\.$dep\s*\)""").containsMatchIn(script),
                    "Essential Compose dependency libs.$dep should be present"
                )
            }
        }

        @Test
        @DisplayName("Hilt dependencies are still configured")
        fun hiltDependenciesConfigured() {
            assertTrue(
                Regex("""implementation\s*\(\s*libs\.hilt\.android\s*\)""").containsMatchIn(script),
                "Hilt Android should be present"
            )
            assertTrue(
                Regex("""ksp\s*\(\s*libs\.hilt\.compiler\s*\)""").containsMatchIn(script),
                "Hilt compiler with KSP should be present"
            )
        }

        @Test
        @DisplayName("Networking dependencies are present")
        fun networkingDependenciesPresent() {
            val networkingDeps = listOf(
                "okhttp",
                "okhttp.logging.interceptor",
                "retrofit"
            )
            
            networkingDeps.forEach { dep ->
                assertTrue(
                    Regex("""implementation\s*\(\s*libs\.$dep\s*\)""").containsMatchIn(script),
                    "Networking dependency libs.$dep should be present"
                )
            }
        }
    }
}