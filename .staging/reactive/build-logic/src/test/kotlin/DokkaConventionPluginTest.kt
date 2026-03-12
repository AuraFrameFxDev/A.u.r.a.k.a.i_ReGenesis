///*
//  Tests for DokkaConventionPlugin.
//  Frameworks:
//    - JUnit Jupiter (JUnit 5)
//    - Gradle TestKit for functional tests
//    - Gradle ProjectBuilder for unit-style tests
//*/
//package com.genesis.buildlogic
//
//import DokkaConventionPlugin
//import org.gradle.testfixtures.ProjectBuilder
//import org.gradle.testkit.runner.GradleRunner
//import org.gradle.testkit.runner.TaskOutcome
//import org.junit.jupiter.api.*
//import java.io.File
//import kotlin.io.path.createTempDirectory
//import kotlin.test.assertEquals
//import kotlin.test.assertTrue
//
//class DokkaConventionPluginTest {
//
//    @Test
//    fun `applies Dokka plugin when convention plugin is applied`() {
//        val project = ProjectBuilder.builder().build()
//        project.pluginManager.apply(DokkaConventionPlugin::class.java)
//
//        assertTrue(
//            project.pluginManager.hasPlugin("org.jetbrains.dokka"),
//            "Dokka plugin should be applied by the convention plugin"
//        )
//    }
//
//    @Test
//    fun `registers dokkaHtmlMultiModule task on root project only`() {
//        val root = ProjectBuilder.builder().withName("root").build()
//        val subA = ProjectBuilder.builder().withName("moduleA").withParent(root).build()
//        val subB = ProjectBuilder.builder().withName("moduleB").withParent(root).build()
//
//        // Apply plugin to all
//        listOf(
//            root,
//            subA,
//            subB
//        ).forEach { it.pluginManager.apply(DokkaConventionPlugin::class.java) }
//
//        // Root has the aggregate task
//        val aggregate = root.tasks.findByName("dokkaHtmlMultiModule")
//        assertNotNull(aggregate, "Root project must register dokkaHtmlMultiModule task")
//        assertEquals(
//            "documentation",
//            aggregate.group,
//            "Aggregate task should belong to 'documentation' group"
//        )
//        assertEquals("Generate HTML documentation for all modules", aggregate.description)
//
//        // Subprojects do not have this task
//        Assertions.assertNull(
//            subA.tasks.findByName("dokkaHtmlMultiModule"),
//            "Subproject must not register dokkaHtmlMultiModule"
//        )
//        Assertions.assertNull(
//            subB.tasks.findByName("dokkaHtmlMultiModule"),
//            "Subproject must not register dokkaHtmlMultiModule"
//        )
//    }
//
//    @Test
//    fun `aggregate task depends on each subproject's dokkaHtml task`() {
//        val root = ProjectBuilder.builder().withName("root").build()
//        val subA = ProjectBuilder.builder().withName("alpha").withParent(root).build()
//        val subB = ProjectBuilder.builder().withName("beta").withParent(root).build()
//
//        // Pre-create placeholder dokkaHtml tasks on subprojects to allow dependency resolution without Dokka plugin
//        subA.tasks.register("dokkaHtml")
//        subB.tasks.register("dokkaHtml")
//
//        root.pluginManager.apply(DokkaConventionPlugin::class.java)
//        subA.pluginManager.apply(DokkaConventionPlugin::class.java)
//        subB.pluginManager.apply(DokkaConventionPlugin::class.java)
//
//        val aggregate = root.tasks.getByName("dokkaHtmlMultiModule")
//        val deps = aggregate.taskDependencies.getDependencies(aggregate).map { it.path }.toSet()
//
//        assertTrue(
//            deps.containsAll(setOf(":alpha:dokkaHtml", ":beta:dokkaHtml")),
//            "Aggregate task should depend on each subproject's :name:dokkaHtml"
//        )
//    }
//}
//
//@DisplayName("DokkaConventionPlugin Functional Tests (Gradle TestKit)")
//@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
//class DokkaConventionPluginFunctionalTest {
//
//    private lateinit var testDir: File
//
//    @BeforeEach
//    fun setup() {
//        testDir = createTempDirectory("dokka-convention-fn-").toFile()
//        // Basic multi-module structure: root with two included projects
//        File(testDir, "settings.gradle.kts").writeText(
//            """
//            rootProject.name = "test-root"
//            include(":libA", ":libB")
//            """.trimIndent()
//        )
//        // Root build - apply plugin under test on all projects via allprojects
//        File(testDir, "build.gradle.kts").writeText(
//            """
//            plugins {
//                // The convention plugin under test will be injected on classpath by pluginUnderTestMetadata
//                id("org.jetbrains.kotlin.jvm") version "1.9.24" apply false
//            }
//
//            allprojects {
//                // Apply Dokka plugin explicitly to ensure Dokka tasks exist
//                plugins.apply("org.jetbrains.dokka")
//                // Apply the convention plugin by class name via buildscript
//                buildscript {
//                    dependencies {
//                        // classpath provided by TestKit
//                    }
//                }
//                // Create a bridge to apply the plugin from classpath
//                afterEvaluate {
//                    // Using reflection to find the plugin class avoids plugin id wiring
//                    val clazz = Class.forName("DokkaConventionPlugin")
//                    @Suppress("UNCHECKED_CAST")
//                    val plugin = clazz.getDeclaredConstructor().newInstance() as org.gradle.api.Plugin<org.gradle.api.Project>
//                    plugin.apply(this)
//                }
//            }
//            """.trimIndent()
//        )
//        // Minimal subprojects with Kotlin (no source needed)
//        File(
//            testDir,
//            "libA/build.gradle.kts"
//        ).writeText("""plugins { id("org.jetbrains.kotlin.jvm") }""")
//        File(
//            testDir,
//            "libB/build.gradle.kts"
//        ).writeText("""plugins { id("org.jetbrains.kotlin.jvm") }""")
//    }
//
//    @Test
//    @Order(1)
//    fun `dokkaHtmlMultiModule task is listed and has correct group and description`() {
//        val result = GradleRunner.create()
//            .withProjectDir(testDir)
//            .withPluginClasspath() // requires pluginUnderTestMetadata in module config
//            .withArguments("tasks", "--group", "documentation", "--stacktrace")
//            .build()
//
//        // Ensure the task is present in output
//        val out = result.output
//        assertTrue(
//            out.contains("dokkaHtmlMultiModule"),
//            "Expected dokkaHtmlMultiModule to be listed under 'documentation' tasks.\n$out"
//        )
//    }
//
//    @Test
//    @Order(2)
//    fun `running aggregate task triggers lifecycle log messages`() {
//        val result = GradleRunner.create()
//            .withProjectDir(testDir)
//            .withPluginClasspath()
//            .withArguments("dokkaHtmlMultiModule", "--info", "--stacktrace")
//            .build()
//
//        assertTrue(
//            result.output.contains("Genesis Protocol documentation generated"),
//            "Lifecycle message should be printed after aggregate task runs. Output:\n${result.output}"
//        )
//        assertEquals(TaskOutcome.SUCCESS, result.task(":dokkaHtmlMultiModule")?.outcome)
//    }
//}
