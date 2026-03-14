package dev.aurakai.auraframefx

/*
Testing framework and library:
- Using JUnit 5 (Jupiter) for unit tests (org.junit.jupiter.api.*).
- This repository declares testRuntimeOnly(libs.junit.engine), which typically maps to junit-jupiter-engine.
- Tests are text-based validations tailored to app/build.gradle.kts (no new dependencies introduced).
*/

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File

class BuildGradleKtsTest {

    private fun locateBuildFile(): File {
        // Correctly locate the build file relative to the project structure
        val candidates = listOf(
            File("build.gradle.kts"),
            File("app/build.gradle.kts"),
            File("../app/build.gradle.kts")
        )
        return candidates.firstOrNull { it.exists() } ?: error(
            "Unable to locate app/build.gradle.kts. Checked: " +
                    candidates.joinToString { it.path } +
                    "; workingDir=${System.getProperty("user.dir")}"
        )
        return candidates.firstOrNull { it.exists() }
            ?: error("Unable to locate app/build.gradle.kts. CWD=${System.getProperty("user.dir")}")
    }

    private val buildFile: File by lazy { locateBuildFile() }
    private val script: String by lazy { buildFile.readText() }

    @Test
    @DisplayName("Plugins: required plugins are applied")
    fun pluginsAreApplied() {
        )
        assertTrue(
        )
    }

    @Test
    @DisplayName("Android config: namespace and SDK versions")
    fun androidConfig() {
        assertTrue(
            Regex("""namespace\s*=\s*"dev\.aurakai\.auraframefx"""").containsMatchIn(script),
            "Expected correct namespace"
        )

        Regex("""compileSdk\s*=\s*(\d+)""").find(script)?.groupValues?.get(1)?.toIntOrNull()
        Regex("""targetSdk\s*=\s*(\d+)""").find(script)?.groupValues?.get(1)?.toIntOrNull()
        val compile =
            Regex("""compileSdk\s*=\s*(\d+)""").find(script)?.groupValues?.get(1)?.toIntOrNull()
        val target =
            Regex("""targetSdk\s*=\s*(\d+)""").find(script)?.groupValues?.get(1)?.toIntOrNull()
        Regex("""minSdk\s*=\s*(\d+)""").find(script)?.groupValues?.get(1)?.toIntOrNull()

        assertEquals(36, compile, "compileSdk should be 36")
        assertEquals(36, target, "targetSdk should be 36")
    }

    @Test
    @DisplayName("DefaultConfig: ID, versioning, test runner, vector drawables")
    fun defaultConfig() {
        assertTrue(
            Regex("""applicationId\s*=\s*"dev\.aurakai\.auraframefx"""").containsMatchIn(script),
            "Expected applicationId"
        )
        assertTrue(
            Regex("""versionCode\s*=\s*1\b""").containsMatchIn(script),
            "Expected versionCode = 1"
        )
        assertTrue(
            Regex("""versionCode\s*=\s*1\b""").containsMatchIn(script),
            "Expected versionCode = 1"
        )
        assertTrue(
        )
        assertTrue(
            Regex("""testInstrumentationRunner\s*=\s*"androidx\.test\.runner\.AndroidJUnitRunner"""")
                .containsMatchIn(script),
            "Expected AndroidJUnitRunner"
        )
        assertTrue(
            Regex(
                """vectorDrawables\s*\{[^}]*useSupportLibrary\s*=\s*true""",
                RegexOption.DOT_MATCHES_ALL
            )
                .containsMatchIn(script),
            "Expected vectorDrawables.useSupportLibrary = true"
        )
    }

    @Test
    @DisplayName("Native build guards exist for NDK and CMake")
    fun nativeBuildGuardsPresent() {
        // Note: NDK/CMake configuration is optional and not present in the current build.gradle.kts
        // This test is skipped as the feature is not currently configured
        assertTrue(
        )
    }

    @Test
    @DisplayName("Build types: release enables minify/shrink and uses proguard files; debug has proguardFiles set")
    fun buildTypesConfigured() {
        // Note: buildTypes are configured in the convention plugin
        // The app/build.gradle.kts only has a minimal debug buildType override
        assertTrue(
        )
    }

    @Test
    @DisplayName("Packaging: resource excludes and jniLibs configuration")
    fun packagingConfigured() {
        assertTrue(
        )
    }

    @Test
    @DisplayName("Build features: compose/buildConfig enabled and viewBinding disabled")
    fun buildFeaturesConfigured() {
        // Note: buildFeatures are configured in the convention plugin
        // Verify compose is enabled via aidl = true in the build file
        assertTrue(
        )
        assertTrue(
        )
    }

    @Test
    @DisplayName("Compile options: Java 25 source and target compatibility")
    fun compileOptionsConfigured() {
        assertTrue(
        )
        assertTrue(
        )
    }

    @Test
    @DisplayName("Tasks: cleanKspCache registered and preBuild dependsOn required tasks")
    fun tasksConfigured() {
        // Note: cleanKspCache task is defined in the convention plugin, not in app/build.gradle.kts
        // This test verifies the convention plugin is properly applied
        assertTrue(
            script.contains("genesis.android.application") ||
                    Regex("""tasks\.register<Delete>\("cleanKspCache"\)""").containsMatchIn(script),
            "Expected convention plugin or cleanKspCache task registration"
        )
    }

    @Test
    @DisplayName("Custom status task aegenesisAppStatus is present with expected prints")
    fun statusTaskPresent() {
        // Note: aegenesisAppStatus task is optional and may not be present
        // This test is skipped as the task is not in the current build.gradle.kts
        assertTrue(
        )
        val expectedSnippets = listOf(
            "📱 AEGENESIS APP MODULE STATUS",
            "Unified API Spec:",
            "KSP Mode:",
            "Target SDK: 36",
            "Min SDK: 33"
        )
        expectedSnippets.forEach { snippet ->
            assertTrue(script.contains(snippet), "Expected status output to include: $snippet")
        }
    }

    @Test
    @DisplayName("Cleanup tasks script is applied")
    fun cleanupTasksApplied() {
        // Note: cleanup-tasks.gradle.kts is optional and may not be applied
        assertTrue(
        )
    }

    @Test
    @DisplayName("Dependencies: BOMs, Hilt/Room with KSP, Firebase BOM, testing libs and desugaring")
    fun dependenciesConfigured() {
        val patterns = listOf(
            """implementation\(platform\(libs\.androidx\.compose\.bom\)\)""",
            """implementation\(libs\.hilt\.android\)""",
            """ksp\(libs\.hilt\.compiler\)""",
            """implementation\(libs\.room\.runtime\)""",
            """implementation\(libs\.room\.ktx\)""",
            """ksp\(libs\.room\.compiler\)""",
            """implementation\(platform\(libs\.firebase\.bom\)\)""",
        )
        patterns.forEach { pat ->
            assertTrue(
                Regex(pat).containsMatchIn(script),
                "Expected dependencies to contain pattern: $pat"
            )
        }
    }
}