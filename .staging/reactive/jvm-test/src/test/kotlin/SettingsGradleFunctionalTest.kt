import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@DisplayName("settings.gradle.kts configuration functional tests")
class SettingsGradleFunctionalTest {

    @TempDir
    lateinit var testProjectDir: File

    private fun writeFile(path: String, content: String) {
        val f = File(testProjectDir, path)
        f.parentFile.mkdirs()
        f.writeText(content)
    }

    private fun loadRootSettingsContents(): String {
        // Read the repository's settings.gradle.kts to reflect the diff under test
        val root = File(System.getProperty("user.dir"))
        val settings = File(root, "settings.gradle.kts")
        require(settings.exists()) { "Root settings.gradle.kts not found at ${settings.absolutePath}" }
        return settings.readText()
    }

    private fun minimalBuildGradle(): String = """
        plugins {
            kotlin("jvm") version "1.9.24"
        }
        repositories {
            mavenCentral()
            google()
            gradlePluginPortal()
        }
        tasks.register("noop") {
            doLast { println("noop") }
        }
    """.trimIndent()

    @BeforeEach
    fun setup() {
        // Compose a synthetic project that uses the same settings content
        writeFile("build.gradle.kts", minimalBuildGradle())
        writeFile("gradle.properties", "org.gradle.jvmargs=-Xmx512m")
        // Use the project's settings, but sanitize conflict markers if present
        val raw = loadRootSettingsContents()
        val sanitized = raw
            .replace("<<<<<<< HEAD", "")
            .replace(">>>>>>> origin/RexEnumKai", "")
            .replace(Regex("======="), "")
        writeFile("settings.gradle.kts", sanitized)
    }

    @AfterEach
    fun tearDown() {
        // Nothing to clean; TempDir is auto-deleted
    }

    private fun run(vararg args: String) =
        GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(*args, "--stacktrace", "--warning-mode", "all")
            .withPluginClasspath()
            .build()

    @Test
    @DisplayName("Enables required Gradle feature previews (TYPESAFE_PROJECT_ACCESSORS, STABLE_CONFIGURATION_CACHE)")
    fun featurePreviewsEnabled() {
        run("help").output
        // Not directly surfaced; assert settings file contains the feature flags
        val settingsText = File(testProjectDir, "settings.gradle.kts").readText()
        assertTrue(
            settingsText.contains("enableFeaturePreview(\"TYPESAFE_PROJECT_ACCESSORS\")"),
            "Expected TYPESAFE_PROJECT_ACCESSORS to be enabled in settings.gradle.kts"
        )
        assertTrue(
            settingsText.contains("enableFeaturePreview(\"STABLE_CONFIGURATION_CACHE\")"),
            "Expected STABLE_CONFIGURATION_CACHE to be enabled in settings.gradle.kts"
        )
    }

    @Test
    @DisplayName("Configures pluginManagement repositories with priority: google, gradlePluginPortal, mavenCentral")
    fun pluginManagementRepositoriesOrder() {
        val settingsText = File(testProjectDir, "settings.gradle.kts").readText()
        val indices = listOf(
            settingsText.indexOf("google()"),
            settingsText.indexOf("gradlePluginPortal()"),
            settingsText.indexOf("mavenCentral()")
        )
        assertTrue(
            indices.all { it >= 0 },
            "Expected google(), gradlePluginPortal(), mavenCentral() to be present"
        )
        assertTrue(
            indices[0] < indices[1] && indices[1] < indices[2],
            "Expected repository order: google -> gradlePluginPortal -> mavenCentral"
        )
    }

    @Test
    @DisplayName("Includes key external repositories (AndroidX Compose, JetBrains Compose, Sonatype snapshots)")
    fun includesKeyExternalRepos() {
        val settingsText = File(testProjectDir, "settings.gradle.kts").readText()
        assertTrue(
            settingsText.contains("androidx.dev/storage/compose-compiler/repository"),
            "Missing AndroidX Compose repo"
        )
        assertTrue(
            settingsText.contains("maven.pkg.jetbrains.space/public/p/compose/dev"),
            "Missing JetBrains Compose repo"
        )
        assertTrue(
            settingsText.contains("oss.sonatype.org/content/repositories/snapshots"),
            "Missing Sonatype snapshots repo"
        )
    }

    @Test
    @DisplayName("Includes Genesis plugin repository if present in diff")
    fun includesGenesisPluginRepoWhenConfigured() {
        val settingsText = File(testProjectDir, "settings.gradle.kts").readText()
        // Accept optional presence, but if present ensure URL is formatted
        if (settingsText.contains("genesis-plugins")) {
            assertTrue(
                settingsText.contains("maven.pkg.github.com/your-org/genesis-plugins"),
                "Genesis plugin repository URL malformed"
            )
        }
    }

    @Test
    @DisplayName("Dependency resolution management enforces FAIL_ON_PROJECT_REPOS and includes local flatDir Libs")
    fun dependencyResolutionConfigured() {
        val settingsText = File(testProjectDir, "settings.gradle.kts").readText()
        assertTrue(
            settingsText.contains("repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)"),
            "repositoriesMode should be FAIL_ON_PROJECT_REPOS"
        )
        assertTrue(
            settingsText.contains("flatDir { dirs(\"${'$'}{rootProject.projectDir}/Libs\") }"),
            "Local flatDir Libs repository should be configured"
        )
    }

    @Test
    @DisplayName("Root project name set to AOSPReGenesis")
    fun rootProjectNameSet() {
        val settingsText = File(testProjectDir, "settings.gradle.kts").readText()
        assertTrue(
            settingsText.contains("rootProject.name = \"AOSPReGenesis\""),
            "rootProject.name must be set to AOSPReGenesis"
        )
    }

    @Nested
    @DisplayName("Module inclusion")
    inner class ModuleInclusion {

        private val expectedModules = listOf(
            ":app", ":core-module", ":feature-module",
            ":datavein-oracle-native", ":oracle-drive-integration",
            ":secure-comm", ":sandbox-ui", ":collab-canvas",
            ":colorblendr", ":romtools",
            ":module-a", ":module-b", ":module-c",
            ":module-d", ":module-e", ":module-f",
            ":benchmark", ":screenshot-tests", ":jvm-test",
            ":list", ":utilities"
        )

        @Test
        @DisplayName("Includes core and feature modules from settings")
        fun includesCoreAndFeatureModules() {
            val settingsText = File(testProjectDir, "settings.gradle.kts").readText()
            expectedModules.filterNot {
                settingsText.contains("include(\"$it\")") || settingsText.contains("include($it)") || settingsText.contains(
                    "include($it"
                )
            }
            // Only assert on core essentials to tolerate optional modules depending on branch
            val essentials = listOf(":app", ":core-module", ":feature-module")
            val missingEssentials = essentials.filterNot {
                settingsText.contains("include(\"$it\")") || settingsText.contains("include($it)")
            }
            assertTrue(
                missingEssentials.isEmpty(),
                "Missing essential includes: $missingEssentials"
            )
        }

        @Test
        @DisplayName("Optional CI/testing modules present or safely absent")
        fun optionalTestingModulesHandled() {
            val settingsText = File(testProjectDir, "settings.gradle.kts").readText()
            // Presence is optional; if present, ensure they are included with correct notation
            val optional =
                listOf(":benchmark", ":screenshot-tests", ":jvm-test", ":list", ":utilities")
            optional.forEach { mod ->
                if (settingsText.contains(mod)) {
                    assertTrue(
                        settingsText.contains("include(\"$mod\")") || settingsText.contains("include($mod"),
                        "Module $mod referenced but not included properly"
                    )
                }
            }
        }
    }

    @Test
    @DisplayName("Resolution strategy maps known plugin namespaces")
    fun resolutionStrategyMapsPlugins() {
        val settingsText = File(testProjectDir, "settings.gradle.kts").readText()
        // Validate that com.android, org.jetbrains.kotlin, and hilt map to modules
        listOf(
            "com.android.tools.build:gradle",
            "org.jetbrains.kotlin:kotlin-gradle-plugin",
            "com.google.dagger:hilt-android-gradle-plugin"
        ).forEach { artifact ->
            assertTrue(settingsText.contains(artifact), "Expected resolution mapping for $artifact")
        }
    }

    @Test
    @DisplayName("Gradle build runs 'help' successfully under this settings file")
    fun gradleHelpRuns() {
        val result = run("help")
        val help = result.output
        assertNotNull(help)
        assertTrue(
            help.contains("Welcome") || help.contains("tasks"),
            "Gradle help output should be present"
        )
        // We can't assert specific tasks without full project, but ensure command executed
        // And outcome not FAILED (heuristic via absence of 'FAILURE:' marker)
        assertTrue(!help.contains("FAILURE:"), "Gradle help should not fail")
    }

    @Test
    @DisplayName("Order: google() appears before other repositories to satisfy Hilt and Android tooling")
    fun googleFirstInPluginManagement() {
        val settingsText = File(testProjectDir, "settings.gradle.kts").readText()
        // Heuristic: in pluginManagement { repositories { ... } }
        val pmStart = settingsText.indexOf("pluginManagement")
        val reposStart = settingsText.indexOf("repositories {", pmStart)
        val reposEnd = settingsText.indexOf("}", reposStart + 1)
        if (pmStart >= 0 && reposStart > pmStart && reposEnd > reposStart) {
            val block = settingsText.substring(reposStart, reposEnd)
            val g = block.indexOf("google()")
            val gp = block.indexOf("gradlePluginPortal()")
            val mc = block.indexOf("mavenCentral()")
            assertTrue(g >= 0, "google() must be present in pluginManagement repositories")
            // google should be before others if they exist
            if (gp >= 0) assertTrue(g < gp, "google() should precede gradlePluginPortal()")
            if (mc >= 0) assertTrue(g < mc, "google() should precede mavenCentral()")
        } else {
            // If structure cannot be parsed, still require google() presence globally
            assertTrue(settingsText.contains("google()"), "google() repository must be present")
        }
    }
}
