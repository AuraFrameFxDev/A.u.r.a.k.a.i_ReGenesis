@file:Suppress("SpellCheckingInspection")

package buildscripts

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Path

/**
 * Testing library and framework in use:
 * - JUnit 5 (Jupiter)
 *
 * Notes:
 * - We intentionally avoid Gradle TestKit here to prevent resolving Android/third‑party plugins at configuration time.
 * - These tests validate the build script text (app/build.gradle.kts) added/changed in the diff:
 *   plugins, Android DSL values, conditional native config, tasks (cleanKspCache, preBuild deps, aegenesisAppStatus),
 *   packaging options, build features, compile options, and dependency notations.
 */
class BuildScriptsFunctionalTest {

    private fun repoRoot(): File {
        // Walk up from CWD until we find settings.gradle.kts
        var dir = File(System.getProperty("user.dir"))
        repeat(8) {
            if (File(dir, "settings.gradle.kts").exists()) return dir
            dir = dir.parentFile ?: return@repeat
        }
        return fail(
            "Could not locate repository root containing settings.gradle.kts from: ${
                System.getProperty(
                    "user.dir"
                )
            }"
        )
    }

    private fun appBuildFile(): File {
        val root = repoRoot().toPath()
        val expected = root.resolve("app/build.gradle.kts").toFile()
        if (expected.exists()) return expected

        // Fallback: scan for a build.gradle.kts that contains the target namespace to remain resilient to layout shifts
        val candidate = root.toFile().walkTopDown()
            .filter { it.isFile && it.name == "build.gradle.kts" }
            .firstOrNull { it.readText().contains("namespace = \"dev.aurakai.auraframefx\"") }
        if (candidate != null) {
            return candidate
        } else {
            return fail("Could not find app/build.gradle.kts with expected namespace in repository.")
        }
    }

    private fun script(): String = appBuildFile().readText()

    @Nested
    @DisplayName("Plugins and basic Android configuration")
    inner class PluginsAndAndroidDsl {

        @Test
        fun `required plugins are declared`() {
            val s = script()
            listOf(
                "com.android.application",
                "org.jetbrains.kotlin.android",
                "org.jetbrains.kotlin.plugin.compose",
                "org.jetbrains.kotlin.plugin.serialization",
                "com.google.devtools.ksp",
                "com.google.dagger.hilt.android",
                "com.google.gms.google-services"
            ).forEach { id ->
                assertTrue(s.contains("id(\"$id\")"), "Expected plugin id $id")
            }
        }

        @Test
        fun `android namespace and SDK versions configured`() {
            val s = script()
            assertTrue(s.contains("namespace = \"dev.aurakai.auraframefx\""))
            assertTrue(Regex("""compileSdk\s*=\s*\d+""").containsMatchIn(s))
            assertTrue(Regex("""minSdk\s*=\s*\d+""").containsMatchIn(s))
            assertTrue(Regex("""targetSdk\s*=\s*\d+""").containsMatchIn(s))
            assertTrue(Regex("""versionCode\s*=\s*\d+""").containsMatchIn(s))
            assertTrue(Regex("""versionName\s*=\s*\".+\"""").containsMatchIn(s))
            assertTrue(s.contains("testInstrumentationRunner"))
        }

        @Test
        fun `vectorDrawables support library enabled`() {
            assertTrue(script().contains("vectorDrawables") && script().contains("useSupportLibrary = true"))
        }
    }

    @Nested
    @DisplayName("Conditional native configuration")
    inner class ConditionalNativeConfig {

        @Test
        fun `ndk abiFilters added only when CMakeLists exists`() {
            val s = script()
            assertTrue(s.contains("if (project.file(\"src/main/cpp/CMakeLists.txt\").exists())"))
            assertTrue(s.contains("ndk {") && s.contains("abiFilters.addAll(listOf(\"arm64-v8a\", \"armeabi-v7a\"))"))
        }

        @Test
        fun `externalNativeBuild cmake path and version set`() {
            val s = script()
            assertTrue(s.contains("externalNativeBuild"))
            assertTrue(s.contains("cmake {"))
            assertTrue(s.contains("path = file(\"src/main/cpp/CMakeLists.txt\")"))
            assertTrue(s.contains("version = \"3.22.1\""))
        }
    }

    @Nested
    @DisplayName("Build types and packaging")
    inner class BuildTypesAndPackaging {

        @Test
        fun `release and debug build types configured with proguard`() {
            val s = script()
            assertTrue(s.contains("buildTypes"))
            assertTrue(
                s.contains("release {") && s.contains("isMinifyEnabled = true") && s.contains(
                    "isShrinkResources = true"
                )
            )
            assertTrue(s.contains("getDefaultProguardFile(\"proguard-android-optimize.txt\")"))
            assertTrue(s.contains("\"proguard-rules.pro\""))
            assertTrue(s.contains("debug {") && s.contains("proguardFiles("))
        }

        @Test
        fun `packaging resources excludes and jni pickFirsts configured`() {
            val s = script()
            // Representative subset of excludes
            listOf(
                "\"/META-INF/{AL2.0,LGPL2.1}\"",
                "\"/META-INF/DEPENDENCIES\"",
                "\"/META-INF/LICENSE.txt\"",
                "\"/META-INF/NOTICE.txt\"",
                "\"META-INF/*.kotlin_module\"",
                "\"**/kotlin/**\"",
                "\"**/*.txt\""
            ).forEach { entry ->
                assertTrue(s.contains(entry), "Missing packaging exclude: $entry")
            }
            assertTrue(s.contains("jniLibs {"))
            assertTrue(s.contains("useLegacyPackaging = false"))
            assertTrue(s.contains("pickFirsts += listOf(\"**/libc++_shared.so\", \"**/libjsc.so\")"))
        }
    }

    @Nested
    @DisplayName("Build features and compile options")
    inner class BuildFeaturesAndCompileOptions {

        @Test
        fun `compose buildConfig and viewBinding flags set`() {
            val s = script()
            assertTrue(s.contains("buildFeatures"))
            assertTrue(s.contains("compose = true"))
            assertTrue(s.contains("buildConfig = true"))
            assertTrue(s.contains("viewBinding = false"))
        }

        @Test
        fun `Java 24 compile options configured`() {
            val s = script()
            assertTrue(s.contains("sourceCompatibility = JavaVersion.VERSION_24"))
            assertTrue(s.contains("targetCompatibility = JavaVersion.VERSION_24"))
        }
    }

    @Nested
    @DisplayName("Tasks - cleanKspCache, preBuild wiring, aegenesisAppStatus")
    inner class TasksValidation {

        @Test
        fun `cleanKspCache task registered with Delete type and correct delete targets`() {
            val s = script()
            assertTrue(s.contains("tasks.register<Delete>(\"cleanKspCache\")"))
            assertTrue(s.contains("group = \"build setup\""))
            assertTrue(s.contains("description = \"Clean KSP caches (fixes NullPointerException)\""))
            listOf(
                "buildDirProvider.dir(\"generated/ksp\")",
                "buildDirProvider.dir(\"tmp/kapt3\")",
                "buildDirProvider.dir(\"tmp/kotlin-classes\")",
                "buildDirProvider.dir(\"kotlin\")",
                "buildDirProvider.dir(\"generated/source/ksp\")"
            ).forEach { target ->
                assertTrue(s.contains(target), "Missing delete target: $target")
            }
        }

        @Test
        fun `preBuild depends on cleanKspCache and API generation tasks`() {
            val s = script()
            assertTrue(s.contains("tasks.named(\"preBuild\")"))
            assertTrue(s.contains("dependsOn(\"cleanKspCache\")"))
            assertTrue(s.contains("dependsOn(\":openApiGenerate\")"))
            assertTrue(s.contains("dependsOn(\":cleanApiGeneration\")"))
        }

        @Test
        fun `aegenesisAppStatus task prints expected lines`() {
            val s = script()
            // Key lines and emojis
            listOf(
                "📱 AEGENESIS APP MODULE STATUS",
                "Unified API Spec: \${if (apiExists) \"✅ Found\" else \"❌ Missing\"}",
                "📄 API File Size: \${apiSize / 1024}KB",
                "🔧 Native Code: \${if (nativeCode) \"✅ Enabled\" else \"❌ Disabled\"}",
                "🧠 KSP Mode:",
                "🎯 Target SDK: 36",
                "📱 Min SDK: 33",
                "✅ Status: Ready for coinscience AI integration!"
            ).forEach { line ->
                assertTrue(s.contains(line), "Expected aegenesisAppStatus to contain: $line")
            }
        }

        @Test
        fun `cleanup tasks script is applied`() {
            assertTrue(script().contains("apply(from = \"cleanup-tasks.gradle.kts\")"))
        }
    }

    @Nested
    @DisplayName("Dependencies block coverage")
    inner class DependenciesBlock {

        @Test
        fun `core test dependencies declared`() {
            val s = script()
            assertTrue(s.contains("testImplementation(libs.bundles.testing)"))
            assertTrue(s.contains("testRuntimeOnly(libs.junit.engine)"))
        }

        @Test
        fun `project dependency hierarchy respected`() {
            val s = script()
            listOf(
                ":core-module",
                ":oracle-drive-integration",
                ":romtools",
                ":secure-comm",
                ":collab-canvas"
            ).forEach { path ->
                assertTrue(
                    s.contains("implementation(project(\"$path\"))"),
                    "Missing project dependency: $path"
                )
            }
        }

        @Test
        fun `notable library notations declared`() {
            val s = script()
            listOf(
                "implementation(platform(libs.androidx.compose.bom))",
                "implementation(libs.androidx.core.ktx)",
                "implementation(libs.hilt.android)",
                "ksp(libs.hilt.compiler)",
                "coreLibraryDesugaring(libs.coreLibraryDesugaring)",
                "implementation(platform(libs.firebase.bom))",
                "implementation(fileTree(\"../Libs\") { include(\"*.jar\") })"
            ).forEach { notation ->
                assertTrue(s.contains(notation), "Expected dependency notation: $notation")
            }
        }
    }
}