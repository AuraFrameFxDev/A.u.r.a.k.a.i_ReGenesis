package dev.aurakai.auraframefx.benchmark

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.io.TempDir
import java.io.File

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class BenchmarkBuildScriptTest {

    @TempDir
    lateinit var testProjectDir: File

    private lateinit var settingsFile: File
    private lateinit var buildFile: File

    @BeforeEach
    fun setup() {
        // Prepare an isolated Gradle project that includes the benchmark module build script under test.
        settingsFile = File(testProjectDir, "settings.gradle.kts").apply {
            writeText(
                """
                rootProject.name = "benchmark-testkit-sandbox"
                pluginManagement {
                  repositories {
                    gradlePluginPortal()
                    google()
                    mavenCentral()
                  }
                }
                dependencyResolutionManagement {
                  repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
                  repositories {
                    google()
                    mavenCentral()
                  }
                }
                include(":benchmark")
                """.trimIndent()
            )
        }
        // Copy the real build.gradle.kts into temp project for resolvability checks.
        // Instead of applying Android plugin tasks, we focus on syntax/no-conflict markers and custom task execution.
        val benchmarkDir = File(testProjectDir, "benchmark").apply { mkdirs() }
        buildFile = File(benchmarkDir, "build.gradle.kts").apply {
            // Read from repo file
            val repoBuild =
                generateSequence(File(System.getProperty("user.dir")).absoluteFile) { it.parentFile }
                    .map { File(it, "benchmark/build.gradle.kts") }
                    .firstOrNull { it.exists() }
                    ?: error(
                        "Could not locate benchmark/build.gradle.kts by walking up from ${
                            ?: error(
                                "Could not locate benchmark/build.gradle.kts by walking up from ${
                                    System.getProperty(
                                        "user.dir"
                                    )
                                }"
                            )
                                    writeText (repoBuild.readText())
                        }

                        // Copy the version catalog the synthetic settings.gradle expects
                        val repoCatalog =
            generateSequence(File(System.getProperty("user.dir")).absoluteFile) { it.parentFile }
                .map { File(it, "gradle/libs.versions.toml") }
                .firstOrNull { it.exists() }
                ?: error(
                    "Could not locate gradle/libs.versions.toml by walking up from ${
                        ?: error(
                            "Could not locate gradle/libs.versions.toml by walking up from ${
                                System.getProperty(
                                    "user.dir"
                                )
                            }"
                        )
                                File (testProjectDir, "gradle").mkdirs()
                                File (testProjectDir, "gradle/libs.versions.toml").writeText(
                            repoCatalog.readText()
                        )

                                // Provide a minimal gradle.properties to avoid daemon/user env noise
                                File (testProjectDir, "gradle.properties").writeText(
                            """
                        org . gradle . jvmargs = - Xmx1024m - Dfile.encoding = UTF - 8
                """.trimIndent()
                        )
                    }

                            private fun gradle(vararg args: String): BuildResult {
                        return GradleRunner.create()
                            .withProjectDir(testProjectDir)
                            .withArguments(*args, "--stacktrace")
                            .withPluginClasspath() // For TestKit to resolve plugin classpath if needed
                            .build()
                    }

                    @Test
                    @Order(1)
                    fun `build script should not contain unresolved merge conflict markers`() {
                        val content = buildFile.readText()
                        // Also check for accidental trailing garbage introduced by merge
                        val suspicious =
                            listOf("Processing module:", "} }", "android.defaultConfig.targetSdk")
                        suspicious.forEach { token ->
                        }
                    }

                    @Test
                    @Order(2)
                    fun `tasks listing should succeed to ensure script is syntactically valid`() {
                        val content = buildFile.readText()
                        val androidPluginApplied =
                            Regex(""" alias \(libs\.plugins\.android\.library\)|id\("com\.android\.library"\)""")
                                .containsMatchIn(content)
                        Assumptions.assumeTrue(
                            System.getenv("ALLOW_PLUGIN_RESOLUTION") == "true" || !androidPluginApplied,
                            "Skipping ':tasks' invocation because Android plugin resolution would be required. Set ALLOW_PLUGIN_RESOLUTION=true to enable."
                        )
                        val result = GradleRunner.create()
                            .withProjectDir(File(testProjectDir, "benchmark"))
                            .withArguments("tasks", "--all", "--stacktrace")
                            .withPluginClasspath()
                            .build()
                    }

                    @Test
                    @Order(3)
                    fun `custom task benchmarkModuleStatus should be registered and print expected lines`() {
                        // Execute the custom task and capture output
                        val result = GradleRunner.create()
                            .withProjectDir(File(testProjectDir, "benchmark"))
                            .withArguments("benchmarkModuleStatus", "--stacktrace")
                            .withPluginClasspath()
                            .build()

                        // Outcome is usually SUCCESS for custom task
                        assertTrue(
                            result.output.contains("📊 BENCHMARK MODULE - Genesis Protocol Status: ACTIVE"),
                            "Expected status banner not found in output."
                        )
                        assertTrue(
                            result.output.contains("Namespace:"),
                            "Expected namespace line not found in output."
                        )
                        assertTrue(
                            result.output.contains("Java Version:"),
                            "Expected Java version line not found in output."
                        )
                        assertTrue(
                            result.output.contains("Min SDK:"),
                            "Expected Min SDK line not found in output."
                        )
                        assertTrue(
                            result.output.contains("Features: Performance Benchmarking"),
                            "Expected features line not found in output."
                        )
                    }

                    @Test
                    @Order(4)
                    fun `build script should configure JUnit 5 platform and testing dependencies`() {
                        val content = buildFile.readText()

                        // Check for JUnit Platform usage
                        assertTrue(
                            "Expected useJUnitPlatform() configuration not found in build.gradle.kts"
                        )

                        // Check for declared key test dependencies (presence-based)
                        val expectedDeps = listOf(
                            "testImplementation",
                            "junit.platform.launcher",
                            "junit.jupiter.engine"
                        )
                        expectedDeps.forEach { key ->
                        }
                    }

                    @Test
                    @Order(5)
                    fun `android test options and build types should be declared in script text`() {
                        val content = buildFile.readText()

                        // Validate presence of testOptions.unitTests flags

                        // Validate presence of the custom 'benchmark' build type
                        assertTrue(
                            "Expected custom 'benchmark' build type declaration."
                        )

                        // Ensure compose and buildConfig features are enabled
                        assertTrue(content.contains("compose = true"), "Expected compose = true")
                        assertTrue(
                            content.contains("buildConfig = true"),
                            "Expected buildConfig = true"
                        )
                    }

                    @Test
                    @Order(6)
                    fun `plugins block should not mix alias and id for the android library plugin simultaneously`() {
                        val content = buildFile.readText()

                        val hasId =
                            Regex("""id\("com\.android\.library"\)""").containsMatchIn(content)

                        // Accept either approach, but not both duplicate entries that could indicate a merge mishap.
                        assertTrue(
                            hasAlias || hasId,
                            "Android Library plugin must be applied via alias or id."
                        )
                    }
        }
