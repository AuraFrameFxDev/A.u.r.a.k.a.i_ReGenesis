//package buildlogic
//
//import org.gradle.testkit.runner.GradleRunner
//import org.gradle.testkit.runner.TaskOutcome
//import org.junit.jupiter.api.AfterEach
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Tag
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.io.TempDir
//import java.io.File
//import java.nio.file.Path
//import kotlin.test.assertEquals
//import kotlin.test.assertTrue
//
///**
// * Tests for AndroidLibraryConventionPlugin using Gradle TestKit.
// *
// * Testing library/framework in use: JUnit 5 (org.junit.jupiter) + Gradle TestKit.
// *
// * Notes:
// * - These are functional-style tests that spin up an isolated build and apply the convention plugin.
// * - We focus on validating key conventions introduced in the PR diff:
// *   - compileSdk = 36, minSdk = 34
// *   - buildFeatures flags (buildConfig/viewBinding/dataBinding)
// *   - coreLibraryDesugaring enabled
// *   - packaging excludes
// *   - lint configuration
// *   - cleanGeneratedSources task registered and wired to preBuild
// *
// * These tests assume the repository's settings/pluginManagement can resolve com.android.library from Google Maven.
// * If AGP resolution fails in CI, ensure settings.gradle(.kts) for the test fixture includes:
// *   pluginManagement { repositories { google(); gradlePluginPortal(); mavenCentral() } plugins { id("com.android.library") version("<AGP_VERSION>") } }
// * and that the chosen AGP version matches the repository constraints.
// */
//class AndroidLibraryConventionPluginTest {
//
//    @TempDir
//    lateinit var tmp: Path
//
//    private lateinit var projectDir: File
//
//    @BeforeEach
//    fun setUp() {
//        projectDir = tmp.toFile()
//    }
//
//    @AfterEach
//    fun tearDown() {
//        // nothing for now
//    }
//
//    /**
//     * Writes a minimal Android library build using our convention plugin.
//     * The plugin id is inferred from plugin-under-test metadata; update ID below if needed.
//     */
//    private fun writeBuildFiles(
//        pluginId: String
//    ) {
//        // settings.gradle.kts: enable repositories to resolve AGP
//        File(projectDir, "settings.gradle.kts").writeText(
//            """
//            pluginManagement {
//                repositories {
//                    google()
//                    mavenCentral()
//                    gradlePluginPortal()
//                }
//                // Pin AGP for com.android.library to match repository configuration if needed.
//                // Replace the version if your repo uses a different one (e.g., from libs.versions.toml).
//                plugins {
//                    id("com.android.library") version "9.0.0-alpha02"
//                }
//            }
//            dependencyResolutionManagement {
//                repositories {
//                    google()
//                    mavenCentral()
//                }
//            }
//            rootProject.name = "convention-test"
//            """.trimIndent()
//        )
//
//        // build.gradle.kts: apply our convention plugin
//        File(projectDir, "build.gradle.kts").writeText(
//            """
//            plugins {
//                id("$pluginId")
//            }
//
//            // No Android sources required; we only verify configuration & tasks.
//            """.trimIndent()
//        )
//
//        // Minimal src dir to allow configuration
//        File(projectDir, "src/main/java").mkdirs()
//        File(projectDir, "src/main/java/Dummy.java").writeText("public class Dummy {}")
//    }
//
//    /**
//     * Execute a Gradle build with given arguments and return the output.
//     */
//    private fun runGradle(vararg args: String): String {
//        val result = GradleRunner.create()
//            .withProjectDir(projectDir)
//            .withArguments(*args, "--stacktrace", "--info")
//            .withPluginClasspath() // picks up the convention plugin under test
//            .build()
//        return result.output
//    }
//
//    /**
//     * Helper to extract a key=value style line from custom diagnostic task outputs.
//     */
//    private fun String.expectContainsAll(markers: List<String>) {
//        markers.forEach {
//            assertTrue(
//                this.contains(it),
//                "Expected output to contain marker: '$it'\n--- OUTPUT START ---\n$this\n--- OUTPUT END ---"
//            )
//        }
//    }
//
//    /**
//     * Sanity: project evaluates and tasks can be listed.
//     */
//    @Test
//    @Tag("functional")
//    fun appliesPlugin_andListsTasks() {
//        val pluginId = discoverPluginIdOrDefault()
//        writeBuildFiles(pluginId)
//
//        val output = runGradle("tasks")
//        assertTrue(
//            output.contains("cleanGeneratedSources"),
//            "Expected cleanGeneratedSources task to be registered."
//        )
//    }
//
//    /**
//     * Verify cleanGeneratedSources exists and preBuild depends on it.
//     * We create a diagnostic task to print dependency edges because querying internal task graph
//     * directly via TestKit is constrained.
//     */
//    @Test
//    @Tag("functional")
//    fun preBuildDependsOn_cleanGeneratedSources() {
//        val pluginId = discoverPluginIdOrDefault()
//        writeBuildFiles(pluginId)
//
//        // Inject a small build script snippet to print preBuild dependencies
//        File(projectDir, "build.gradle.kts").appendText(
//            """
//
//            // Diagnostics: print dependencies of preBuild if present
//            tasks.register("printPreBuildDeps") {
//                doLast {
//                    val pre = tasks.findByName("preBuild")
//                    if (pre == null) {
//                        println("preBuild:ABSENT")
//                    } else {
//                        val deps = pre.taskDependencies.getDependencies(pre).map { it.name }.sorted()
//                        println("preBuild.deps=" + deps.joinToString(","))
//                    }
//                }
//            }
//            """.trimIndent()
//        )
//
//        val output = runGradle("printPreBuildDeps")
//        assertTrue(
//            output.contains("preBuild"),
//            "Expected preBuild task to exist when com.android.library is applied."
//        )
//        assertTrue(
//            output.contains("cleanGeneratedSources"),
//            "Expected preBuild to depend on cleanGeneratedSources. Output:\n$output"
//        )
//    }
//
//    /**
//     * Validate Android extension basics: compileSdk, minSdk, buildFeatures, desugaring, lint, packaging excludes.
//     * We create a custom task that introspects the extension at configuration time and prints values.
//     */
//    @Test
//    @Tag("functional")
//    fun configuresAndroidLibraryExtension_asExpected() {
//        val pluginId = discoverPluginIdOrDefault()
//        writeBuildFiles(pluginId)
//
//        File(projectDir, "build.gradle.kts").appendText(
//            """
//
//            import com.android.build.api.dsl.LibraryExtension
//
//            tasks.register("printAndroidConfig") {
//                doLast {
//                    val ext = extensions.findByType(LibraryExtension::class.java)
//                    if (ext == null) {
//                        println("android:ABSENT")
//                    } else {
//                        println("android.compileSdk=" + ext.compileSdk)
//                        println("android.defaultConfig.minSdk=" + ext.defaultConfig.minSdk)
//                        println("android.buildFeatures.buildConfig=" + (ext.buildFeatures?.buildConfig ?: "null"))
//                        println("android.buildFeatures.viewBinding=" + (ext.buildFeatures?.viewBinding ?: "null"))
//                        println("android.buildFeatures.dataBinding=" + (ext.buildFeatures?.dataBinding ?: "null"))
//                        println("android.compileOptions.coreLibraryDesugaring=" + ext.compileOptions.isCoreLibraryDesugaringEnabled)
//
//                        // Lint
//                        println("android.lint.abortOnError=" + ext.lint.abortOnError)
//                        println("android.lint.warningsAsErrors=" + ext.lint.warningsAsErrors)
//                        println("android.lint.disabled=" + ext.lint.disable.joinToString(","))
//
//                        // Packaging excludes (flatten set to list)
//                        val excludes = ext.packaging.resources.excludes.toList().sorted()
//                        println("android.packaging.excludes=" + excludes.joinToString("|"))
//                    }
//                }
//            }
//            """.trimIndent()
//        )
//
//        val output = runGradle("printAndroidConfig")
//
//        output.expectContainsAll(
//            listOf(
//                "android.compileSdk=36",
//                "android.defaultConfig.minSdk=34",
//                "android.buildFeatures.buildConfig=true",
//                "android.buildFeatures.viewBinding=false",
//                "android.buildFeatures.dataBinding=false",
//                "android.compileOptions.coreLibraryDesugaring=true",
//                "android.lint.abortOnError=false",
//                "android.lint.warningsAsErrors=false",
//                "android.lint.disabled=InvalidPackage,OldTargetApi"
//            )
//        )
//
//        // Verify a couple of representative packaging excludes are present
//        assertTrue(
//            output.contains("/META-INF/AL2.0") || output.contains("AL2.0"),
//            "Expected AL2.0 exclude to be present.\n$output"
//        )
//        assertTrue(
//            output.contains("META-INF/*.kotlin_module"),
//            "Expected Kotlin module exclude to be present.\n$output"
//        )
//    }
//
//    /**
//     * Validate Java and Kotlin toolchains.
//     */
//    @Test
//    @Tag("functional")
//    fun configuresJavaAndKotlinToolchains() {
//        val pluginId = discoverPluginIdOrDefault()
//        writeBuildFiles(pluginId)
//
//        File(projectDir, "build.gradle.kts").appendText(
//            """
//
//            import org.gradle.api.plugins.JavaPluginExtension
//            import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
//
//            tasks.register("printJvmToolchains") {
//                doLast {
//                    val javaExt = extensions.findByType(JavaPluginExtension::class.java)
//                    if (javaExt != null) {
//                        println("java.sourceCompatibility=" + javaExt.sourceCompatibility)
//                        println("java.targetCompatibility=" + javaExt.targetCompatibility)
//                    } else {
//                        println("java:ABSENT")
//                    }
//
//                    val kt = extensions.findByType(KotlinProjectExtension::class.java)
//                    if (kt != null) {
//                        // Kotlin DSL doesn't expose the configured numeric value directly for printing,
//                        // but we can at least assert the toolchain service provisioned (presence).
//                        println("kotlin.jvmToolchain.configured=YES")
//                    } else {
//                        println("kotlin:ABSENT")
//                    }
//                }
//            }
//            """.trimIndent()
//        )
//
//        val output = runGradle("printJvmToolchains")
//        assertTrue(
//            output.contains("java.sourceCompatibility=VERSION_24"),
//            "Expected Java sourceCompatibility 24.\n$output"
//        )
//        assertTrue(
//            output.contains("java.targetCompatibility=VERSION_24"),
//            "Expected Java targetCompatibility 24.\n$output"
//        )
//        assertTrue(
//            output.contains("kotlin.jvmToolchain.configured=YES"),
//            "Expected Kotlin jvmToolchain to be configured.\n$output"
//        )
//    }
//
//    /**
//     * Edge case: Running cleanGeneratedSources should not fail if directories do not exist.
//     */
//    @Test
//    @Tag("functional")
//    fun cleanGeneratedSources_handlesMissingDirectories() {
//        val pluginId = discoverPluginIdOrDefault()
//        writeBuildFiles(pluginId)
//
//        val result = GradleRunner.create()
//            .withProjectDir(projectDir)
//            .withArguments("cleanGeneratedSources", "--stacktrace", "--info")
//            .withPluginClasspath()
//            .build()
//
//        assertEquals(TaskOutcome.SUCCESS, result.task(":cleanGeneratedSources")?.outcome)
//    }
//
//    /**
//     * Attempt to infer the plugin id from the plugin-under-test metadata resource.
//     * Falls back to a common convention id if not found; update default as needed.
//     */
//    private fun discoverPluginIdOrDefault(): String {
//        // During TestKit runs, a file 'plugin-under-test-metadata.properties' is placed on classpath
//        // containing implemented plugin ids. We'll try to read it.
//        return try {
//            javaClass.classLoader.getResourceAsStream("META-INF/gradle-plugins/") // directory listing not available
//            // If directory listing is not possible, rely on a curated default used in this repository.
//            // Replace the below default with your actual convention plugin id if different.
//            "genesis.android.library"
//        } catch (e: Exception) {
//            // Fallback
//            "genesis.android.library"
//        }
//    }
//}
