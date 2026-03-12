package test// ====== APPENDED TESTS (JUnit 5 + Gradle TestKit) ======
// Note: Testing library/framework used: JUnit 5 (Jupiter) + Gradle TestKit with Kotlin assertions.

class AndroidComposeConventionPluginMoreTests {

    private lateinit var testProjectDir: java.io.File

    @org.junit.jupiter.api.BeforeEach
    fun setUp() {
        testProjectDir = createTempDir(prefix = "compose-convention-more-")
    }

    @org.junit.jupiter.api.AfterEach
    fun tearDown() {
        testProjectDir.deleteRecursively()
    }

    private fun runner(vararg args: String) =
        org.gradle.testkit.runner.GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(*args, "--stacktrace")
            .withPluginClasspath()
            .forwardOutput()

    private fun writeSettings(name: String = "test-app-more") {
        testProjectDir.resolve("settings.gradle.kts").writeText(
            """
            rootProject.name = "$name"
            """.trimIndent()
        )
    }

    @org.junit.jupiter.api.Test
    fun appliesBaseLibraryConventionAndComposePlugin() {
        writeSettings()
        testProjectDir.resolve("build.gradle.kts").writeText(
            """
            plugins {
                id("com.android.library")
                kotlin("android")
            }
            // Apply plugin under test by class to avoid id mapping assumptions
            apply<dev.genesis.android.AndroidComposeConventionPlugin>()

            android {
                namespace = "dev.genesis.android.more"
                compileSdk = 34
                defaultConfig { minSdk = 24 }
            }

            tasks.register("probeBaseAndCompose") {
                doLast {
                    val pm = project.pluginManager
                    println("HAS_BASE_GENESIS_LIBRARY=" + pm.hasPlugin("genesis.android.library"))
                    println("HAS_KOTLIN_COMPOSE=" + pm.hasPlugin("org.jetbrains.kotlin.plugin.compose"))

                    val androidExt = extensions.findByName("android")
                    val features = androidExt!!.javaClass.methods.first { it.name == "getBuildFeatures" }.invoke(androidExt)
                    val composeEnabled = features.javaClass.methods.first { it.name == "getCompose" }.invoke(features) as? Boolean
                    println("PROBE_COMPOSE_ENABLED_MORE_C=" + composeEnabled)
                }
            }
            """.trimIndent()
        )
        val result = runner("probeBaseAndCompose").build()
        kotlin.test.assertEquals(
            org.gradle.testkit.runner.TaskOutcome.SUCCESS,
            result.task(":probeBaseAndCompose")?.outcome
        )
        kotlin.test.assertTrue(
            result.output.contains("HAS_BASE_GENESIS_LIBRARY=true"),
            "Expected genesis.android.library to be applied."
        )
        kotlin.test.assertTrue(
            result.output.contains("HAS_KOTLIN_COMPOSE=true"),
            "Expected Kotlin Compose plugin to be applied."
        )
        kotlin.test.assertTrue(
            result.output.contains("PROBE_COMPOSE_ENABLED_MORE_C=true"),
            "Expected compose build feature to be enabled."
        )
    }

    @org.junit.jupiter.api.Test
    fun enablesComposeWhenPreconfiguredFalse() {
        writeSettings()
        testProjectDir.resolve("build.gradle.kts").writeText(
            """
            plugins {
                id("com.android.library")
                kotlin("android")
            }

            // Preconfigure compose to false
            android {
                namespace = "dev.genesis.android.more"
                compileSdk = 34
                defaultConfig { minSdk = 24 }
                buildFeatures { compose = false }
            }

            // Apply plugin under test which should enable compose
            apply<dev.genesis.android.AndroidComposeConventionPlugin>()

            tasks.register("probeComposeMoreD") {
                doLast {
                    val androidExt = extensions.findByName("android")
                    val features = androidExt!!.javaClass.methods.first { it.name == "getBuildFeatures" }.invoke(androidExt)
                    val composeEnabled = features.javaClass.methods.first { it.name == "getCompose" }.invoke(features) as? Boolean
                    println("PROBE_COMPOSE_ENABLED_MORE_D=" + composeEnabled)
                }
            }
            """.trimIndent()
        )
        val result = runner("probeComposeMoreD").build()
        kotlin.test.assertEquals(
            org.gradle.testkit.runner.TaskOutcome.SUCCESS,
            result.task(":probeComposeMoreD")?.outcome
        )
        kotlin.test.assertTrue(
            result.output.contains("PROBE_COMPOSE_ENABLED_MORE_D=true"),
            "Convention should override compose=false to true."
        )
    }

    @org.junit.jupiter.api.Test
    fun keepsComposeTrueWhenAlreadyTrue() {
        writeSettings()
        testProjectDir.resolve("build.gradle.kts").writeText(
            """
            plugins {
                id("com.android.library")
                kotlin("android")
            }

            // Preconfigure compose to true
            android {
                namespace = "dev.genesis.android.more"
                compileSdk = 34
                defaultConfig { minSdk = 24 }
                buildFeatures { compose = true }
            }

            // Apply plugin under test; compose should remain true
            apply<dev.genesis.android.AndroidComposeConventionPlugin>()

            tasks.register("probeComposeMoreE") {
                doLast {
                    val androidExt = extensions.findByName("android")
                    val features = androidExt!!.javaClass.methods.first { it.name == "getBuildFeatures" }.invoke(androidExt)
                    val composeEnabled = features.javaClass.methods.first { it.name == "getCompose" }.invoke(features) as? Boolean
                    println("PROBE_COMPOSE_ENABLED_MORE_E=" + composeEnabled)
                }
            }
            """.trimIndent()
        )
        val result = runner("probeComposeMoreE").build()
        kotlin.test.assertEquals(
            org.gradle.testkit.runner.TaskOutcome.SUCCESS,
            result.task(":probeComposeMoreE")?.outcome
        )
        kotlin.test.assertTrue(
            result.output.contains("PROBE_COMPOSE_ENABLED_MORE_E=true"),
            "Compose should remain enabled when already true."
        )
    }
}
