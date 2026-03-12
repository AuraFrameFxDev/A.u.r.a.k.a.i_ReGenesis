//@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
//
//
//import org.gradle.testkit.runner.BuildResult
//import org.gradle.testkit.runner.GradleRunner
//import org.gradle.testkit.runner.TaskOutcome
//import org.junit.jupiter.api.*
//import org.junit.jupiter.api.io.TempDir
//import java.io.File
//import kotlin.test.assertEquals
//import kotlin.test.assertTrue
//
///**
// * Testing library and framework: JUnit 5 (Jupiter) with Gradle TestKit.
// *
// * This suite verifies:
// * - The Detekt plugin is applied by DetektConventionPlugin.
// * - DetektExtension is configured (reports toggled, config paths).
// * - Custom 'detektGenesisRules' task exists, is in verification group, and runs after 'detekt'.
// * - Genesis rules behavior across success and failure scenarios.
// *
// * Note: These are TestKit functional tests because Gradle plugin behavior is best validated via real builds.
// */
//class DetektConventionPluginTest {
//
//    @TempDir
//    lateinit var testProjectDir: File
//
//    private fun writeFile(relativePath: String, content: String) {
//        val f = File(testProjectDir, relativePath)
//        f.parentFile.mkdirs()
//        f.writeText(content.trimIndent())
//    }
//
//    private fun gradleRunner(vararg args: String): BuildResult =
//        GradleRunner.create()
//            .withProjectDir(testProjectDir)
//            .withArguments(*args, "--stacktrace")
//            .withPluginClasspath()
//            .build()
//
//    private fun gradleRunnerFail(vararg args: String): BuildResult =
//        GradleRunner.create()
//            .withProjectDir(testProjectDir)
//            .withArguments(*args, "--stacktrace")
//            .withPluginClasspath()
//            .buildAndFail()
//
//    @BeforeEach
//    fun setupRoot() {
//        writeFile(
//            "settings.gradle.kts", """
//            rootProject.name = "root"
//            include(":app", ":feature-home", ":module-user", ":feature-settings")
//        """
//        )
//
//        // Config files referenced by plugin
//        writeFile("config/detekt/detekt.yml", "# detekt config")
//        writeFile("config/detekt/baseline.xml", "<baseline/>")
//    }
//
//    private fun writeModuleBuild(name: String, buildContent: String) {
//        val modulePath = if (name == ":") "" else name.removePrefix(":")
//        if (modulePath.isNotEmpty()) File(testProjectDir, modulePath).mkdirs()
//        val target =
//            if (modulePath.isEmpty()) "build.gradle.kts" else "$modulePath/build.gradle.kts"
//        writeFile(target, buildContent)
//    }
//
//    @Test
//    fun `detekt plugin applied and custom task registered`() {
//        writeModuleBuild(
//            ":app", """
//            plugins {
//                id("org.jetbrains.kotlin.jvm") version "1.9.24"
//            }
//            apply<DetektConventionPlugin>()
//        """
//        )
//        writeFile("app/src/main/kotlin/App.kt", "object App")
//
//        val tasks = gradleRunner(":app:tasks", "--all")
//        assertTrue(tasks.output.contains("detekt"), "Detekt task should be present")
//        assertTrue(
//            tasks.output.contains("detektGenesisRules"),
//            "Custom detektGenesisRules task should be registered"
//        )
//    }
//
//    @Test
//    fun `detekt finalizedBy detektGenesisRules`() {
//        writeModuleBuild(
//            ":module-user", """
//            plugins {
//                id("org.jetbrains.kotlin.jvm") version "1.9.24"
//            }
//            apply<DetektConventionPlugin>()
//        """
//        )
//        writeFile("module-user/src/main/kotlin/U.kt", "object U")
//
//        val result = gradleRunner(":module-user:detekt")
//        assertTrue(
//            result.output.contains("Genesis Protocol architecture rules verified") ||
//                    result.output.contains("✅ Genesis Protocol architecture rules verified"),
//            "detekt should finalizeBy detektGenesisRules"
//        )
//    }
//
//    @Test
//    fun `Genesis Rule 1 - violation when feature depends on feature`() {
//        writeModuleBuild(
//            ":feature-home", """
//            plugins {
//                id("org.jetbrains.kotlin.jvm") version "1.9.24"
//            }
//            apply<DetektConventionPlugin>()
//            dependencies {
//                implementation(project(":feature-settings"))
//            }
//        """
//        )
//        writeModuleBuild(
//            ":feature-settings", """
//            plugins {
//                id("org.jetbrains.kotlin.jvm") version "1.9.24"
//            }
//            apply<DetektConventionPlugin>()
//        """
//        )
//        writeFile("feature-home/src/main/kotlin/F.kt", "object F")
//        writeFile("feature-settings/src/main/kotlin/S.kt", "object S")
//
//        val failure = gradleRunnerFail(":feature-home:detektGenesisRules")
//        assertTrue(
//            failure.output.contains("Feature modules cannot depend on other feature modules") ||
//                    failure.output.contains("Genesis architecture rule violated")
//        )
//    }
//
//    @Test
//    fun `Genesis Rule 2 - violation when non-app uses desugaring`() {
//        writeModuleBuild(
//            ":module-user", """
//            plugins {
//                id("org.jetbrains.kotlin.jvm") version "1.9.24"
//            }
//            apply<DetektConventionPlugin>()
//            dependencies {
//                coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
//            }
//        """
//        )
//        writeFile("module-user/src/main/kotlin/U.kt", "object U")
//
//        val failure = gradleRunnerFail(":module-user:detektGenesisRules")
//        assertTrue(
//            failure.output.contains("Only app module should have coreLibraryDesugaring dependency") ||
//                    failure.output.contains("Genesis architecture rule violated")
//        )
//    }
//
//    @Test
//    fun `Genesis recommendation - warn when genesis conventions not found but do not fail`() {
//        writeModuleBuild(
//            ":module-user", """
//            plugins {
//                id("org.jetbrains.kotlin.jvm") version "1.9.24"
//            }
//            apply<DetektConventionPlugin>()
//            // Deliberately no 'genesis.android.' usage
//        """
//        )
//        writeFile("module-user/src/main/kotlin/U.kt", "object U")
//
//        val ok = gradleRunner(":module-user:detektGenesisRules")
//        assertEquals(TaskOutcome.SUCCESS, ok.task(":module-user:detektGenesisRules")?.outcome)
//        assertTrue(
//            ok.output.contains("Genesis Recommendation: Module should use Genesis convention plugins") ||
//                    ok.output.contains("⚠️ Genesis Recommendation"),
//            "Should log a recommendation warning"
//        )
//    }
//
//    @Test
//    fun `Genesis success - app module allows desugaring and no feature-feature deps`() {
//        writeModuleBuild(
//            ":app", """
//            plugins {
//                id("org.jetbrains.kotlin.jvm") version "1.9.24"
//            }
//            apply<DetektConventionPlugin>()
//            dependencies {
//                coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
//            }
//        """
//        )
//        writeFile("app/src/main/kotlin/App.kt", "object App")
//
//        val ok = gradleRunner(":app:detektGenesisRules")
//        assertEquals(TaskOutcome.SUCCESS, ok.task(":app:detektGenesisRules")?.outcome)
//        assertTrue(
//            ok.output.contains("Genesis Protocol architecture rules verified") ||
//                    ok.output.contains("✅ Genesis Protocol architecture rules verified")
//        )
//    }
//}
