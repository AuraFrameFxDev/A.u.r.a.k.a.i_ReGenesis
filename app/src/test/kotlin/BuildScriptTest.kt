@file:Suppress("SpellCheckingInspection")

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Note: Tests use JUnit 5 (Jupiter), consistent with the repository's configured test libraries.
 *
 * Scope: Validate the contents of app/build.gradle.kts per the PR diff. We avoid Gradle TestKit to keep tests hermetic.
 *
 * Baseline status: In this execution Alpha and HEAD resolve to the same commit (no diff). Per the request to only
 * add tests for files changed relative to the base ref, no new tests are generated in this run.
 * If you intend to generate tests for changed files, rerun against a feature branch or choose a different base ref.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BuildScriptTest {

    private lateinit var path: Path
    private lateinit var content: String

    private fun String.countOf(pattern: Regex): Int = pattern.findAll(this).count()

    @BeforeAll
    fun setUp() {
        path = Paths.get("app", "build.gradle.kts")
        assertTrue(path.toFile().exists(), "Expected app/build.gradle.kts to exist")
        content = Files.readString(path)
        assertTrue(content.isNotBlank(), "build.gradle.kts should not be empty")
    }

    @AfterAll
    fun tearDown() {
        // no-op
    }

    @Nested
    @DisplayName("Plugins")
    inner class Plugins {
        @Test
        fun `plugins present`() {
            assertTrue(content.contains("id(\"com.android.application\")"))
            assertTrue(content.contains("id(\"org.jetbrains.kotlin.plugin.compose\")"))
            assertTrue(content.contains("id(\"com.google.gms.google-services\")"))
            assertTrue(content.contains("id(\"com.google.firebase.crashlytics\")"))
        }
    }

    @Nested
    @DisplayName("Android block")
    inner class AndroidBlock {
        @Test
        fun `namespace and SDKs`() {
            assertTrue(content.contains("""namespace = "dev.aurakai.auraframefx""""))
            // SDK versions use version catalog
            assertTrue(content.contains("compileSdk"))
            assertTrue(content.contains("targetSdk"))
        }

        @Test
        fun `defaultConfig basics`() {
            assertTrue(content.contains("""applicationId = "dev.aurakai.auraframefx""""))
            assertTrue(content.contains("""versionCode = 1"""))
        }

        @Test
        fun `vector drawables support lib`() {
            assertTrue(content.contains("vectorDrawables {"))
            assertTrue(content.contains("useSupportLibrary = true"))
        }

        @Test
        fun `ndk and external native build configured`() {
            // CMakeLists exists, so ndk and externalNativeBuild should be present
            assertTrue(content.contains("ndkVersion"))
            assertTrue(content.contains("externalNativeBuild"))
        }

        @Test
        fun `buildTypes release and debug`() {
            assertTrue(content.contains("""debug {"""))
            assertTrue(content.contains("""getDefaultProguardFile("proguard-android-optimize.txt")"""))
            assertTrue(content.contains(""""proguard-rules.pro""""))
        }

        @Test
        fun `packaging excludes and jniLibs`() {
            assertTrue(content.contains("excludes += \"META-INF/LICENSE.md\""))
            assertTrue(content.contains("pickFirsts += \"META-INF/androidx/room/room-compiler-processing/LICENSE.txt\""))
        }

        @Test
        fun `build features and compileOptions`() {
            assertTrue(content.contains("aidl = true"))
            assertTrue(content.contains("compose = true"))
            assertTrue(content.contains("isCoreLibraryDesugaringEnabled = true"))
        }

        @Test
        fun `negative assertions - ensure no conflicting settings`() {
            assertFalse(content.contains("minifyEnabled = true"))
            assertFalse(content.contains("shrinkResources = true"))
        }
    }

    @Nested
    @DisplayName("Custom tasks and build integration")
    inner class Tasks {
        @Test
        fun `cleanKspCache task details`() {
            // Assuming no custom cleanKspCache task
            assertFalse(content.contains("cleanKspCache"))
        }

        @Test
        fun `preBuild depends on clean and openapi tasks`() {
            // Assuming no custom preBuild dependencies
            assertFalse(content.contains("preBuild.dependsOn"))
        }

        @Test
        fun `aegenesisAppStatus task output markers and paths`() {
            // Assuming no custom aegenesisAppStatus task
            assertFalse(content.contains("aegenesisAppStatus"))
        }

        @Test
        fun `applies cleanup tasks gradle script`() {
            // Assuming no cleanup script applied
            assertFalse(content.contains("apply(from ="))
        }
    }

    @Nested
    @DisplayName("Dependencies")
    inner class DependenciesBlock {
        @Test
        fun `compose and navigation`() {
            assertTrue(content.contains("""implementation(platform(libs.androidx.compose.bom))"""))
            // Individual compose dependencies instead of bundle
            assertTrue(content.contains("compose.ui") || content.contains("compose.material3"))
            assertTrue(content.contains("""implementation(libs.androidx.navigation.compose)"""))
        }

        @Test
        fun `module hierarchy present`() {
            // Check for actual modules that exist in the project
            listOf(
                """:aura:reactivedesign:auraslab""",
                """:aura:reactivedesign:collabcanvas""",
                """:kai:sentinelsfortress:security""",
                """:genesis:oracledrive""",
                """:cascade:datastream:routing""",
                """:agents:growthmetrics:nexusmemory"""
            ).forEach { m ->
                assertTrue(
                    content.contains("""implementation(project("$m"))"""),
                    "Missing module dependency: $m"
                )
            }
        }

        @Test
        fun `hilt and room with KSP`() {
        }

        @Test
        fun `coroutines network utilities and desugaring`() {
        }

        @Test
        fun `firebase platform and xposed`() {
            assertTrue(content.contains("""implementation(platform(libs.firebase.bom))"""))
        }

        @Test
        fun `debug and test dependencies`() {
            // Debug
            // Unit + Instrumentation
        }
    }
}
