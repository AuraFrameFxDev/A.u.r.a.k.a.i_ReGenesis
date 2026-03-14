//@file:OptIn(ExperimentalKotlinGradlePluginApi::class)
//
//import org.gradle.api.tasks.Delete
//import org.gradle.testfixtures.ProjectBuilder
//import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
//import org.junit.jupiter.api.AfterEach
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.Assertions.assertFalse
//import org.junit.jupiter.api.Assertions.assertNotNull
//import org.junit.jupiter.api.Assertions.assertTrue
//import org.junit.jupiter.api.DisplayName
//import org.junit.jupiter.api.Nested
//import org.junit.jupiter.api.Test
//import org.openapitools.generator.gradle.plugin.extensions.OpenApiGeneratorGenerateExtension
//import java.io.File
//
//@DisplayName("OpenApiConventionPlugin")
//class OpenApiConventionPluginTest {
//
//    private val project = ProjectBuilder.builder()
//        .withName("test-project")
//        .build()
//        .also {
//            it.pluginManager.apply("com.aurakai.memoria.buildlogic.openapi-convention")
//        }
//
//    @AfterEach
//    fun tearDown() {
//        val genDir = File(project.buildDir, "generated/source/openapi")
//        if (genDir.exists()) genDir.deleteRecursively()
//    }
//
//    @Test
//    @DisplayName("applies org.openapi.generator plugin")
//    fun appliesOpenApiPlugin() {
//        assertTrue(
//            project.plugins.hasPlugin("org.openapi.generator"),
//            "Expected org.openapi.generator plugin to be applied."
//        )
//    }
//
//    @Test
//    @DisplayName("registers cleanApiGeneration task with expected group and description")
//    fun registersCleanApiGenerationTask() {
//        val t = project.tasks.findByName("cleanApiGeneration")
//        assertNotNull(t, "Task cleanApiGeneration should be registered")
//        assertTrue(t is Delete, "cleanApiGeneration should be of type Delete")
//        assertEquals("build", t!!.group, "Task group should be 'build'")
//        assertEquals("Clean generated API files", t.description, "Task description should match")
//    }
//
//    @Test
//    @DisplayName("configures OpenApiGeneratorGenerateExtension with expected defaults")
//    fun configuresOpenApiExtension() {
//        val ext = project.extensions.findByType(OpenApiGeneratorGenerateExtension::class.java)
//        assertNotNull(ext, "OpenApiGeneratorGenerateExtension should be configured")
//
//        ext!!
//        println("OpenApiGeneratorGenerateExtension properties: " + ext.javaClass.declaredFields.map { it.name })
//
//        // Core generator options
//        assertEquals("kotlin", ext.generatorName.get(), "generatorName should be 'kotlin'")
//
//        val expectedInputSpecSuffix = "/app/api/unified-aegenesis-api.yml"
//        assertTrue(
//            ext.inputSpec.get().endsWith(expectedInputSpecSuffix),
//            "inputSpec should end with $expectedInputSpecSuffix, was: ${ext.inputSpec.get()}"
//        )
//
//        val outputDir = ext.outputDir.get()
//        assertTrue(
//            outputDir.replace(File.separatorChar, '/').endsWith("/build/generated/source/openapi/"),
//            "outputDir should point to build/generated/source/openapi/, was: $outputDir"
//        )
//
//        assertEquals("dev.aurakai.aegenesis.api", ext.packageName.get(), "packageName mismatch")
//        assertEquals("dev.aurakai.aegenesis.api", ext.apiPackage.get(), "apiPackage mismatch")
//        assertEquals("dev.aurakai.aegenesis.model", ext.modelPackage.get(), "modelPackage mismatch")
//        assertEquals(
//            "dev.aurakai.aegenesis.client",
//            ext.invokerPackage.get(),
//            "invokerPackage mismatch"
//        )
//
//        // Use == true to handle nullable Boolean
//        assertFalse(ext.skipOverwrite.get() == true, "skipOverwrite should be false")
//        assertFalse(ext.validateSpec.get(), "validateSpec should be false")
//        assertFalse(ext.generateApiTests.get(), "generateApiTests should be false")
//        assertFalse(ext.generateModelTests.get(), "generateModelTests should be false")
//        assertTrue(ext.generateApiDocumentation.get(), "generateApiDocumentation should be true")
//        assertTrue(
//            ext.generateModelDocumentation.get(),
//            "generateModelDocumentation should be true"
//        )
//
//        val opts = ext.configOptions.get()
//        assertEquals("jvm-retrofit2", opts["library"], "library should be jvm-retrofit2")
//        assertEquals("true", opts["useCoroutines"], "useCoroutines should be 'true'")
//        assertEquals(
//            "kotlinx_serialization",
//            opts["serializationLibrary"],
//            "serializationLibrary mismatch"
//        )
//        assertEquals("kotlinx-datetime", opts["dateLibrary"], "dateLibrary mismatch")
//        assertEquals("src/main/kotlin", opts["sourceFolder"], "sourceFolder mismatch")
//        assertEquals("true", opts["hilt"], "hilt should be 'true'")
//        assertEquals("UPPERCASE", opts["enumPropertyNaming"], "enumPropertyNaming mismatch")
//        assertEquals("list", opts["collectionType"], "collectionType mismatch")
//    }
//
//    @Test
//    @DisplayName("wires clean -> dependsOn(cleanApiGeneration)")
//    fun cleanDependsOnCleanApiGeneration() {
//        val clean = project.tasks.named("clean").get()
//        val depNames = clean.taskDependencies.getDependencies(clean).map { it.name }.toSet()
//        assertTrue(
//            depNames.contains("cleanApiGeneration"),
//            "clean task should depend on cleanApiGeneration"
//        )
//    }
//
//    @Nested
//    @DisplayName("cleanApiGeneration behavior")
//    inner class CleanApiGenerationBehavior {
//
//        @Test
//        @DisplayName("Delete task targets build/generated/source/openapi directory")
//        fun deleteTargetsGeneratedDir() {
//            val t = project.tasks.named("cleanApiGeneration").get() as Delete
//            val genDir = project.layout.buildDirectory.dir("generated/source/openapi").get().asFile
//            genDir.mkdirs()
//            assertTrue(
//                genDir.exists() && genDir.isDirectory,
//                "Generated directory should exist for the test setup"
//            )
//
//            val targets = t.delete.map {
//                when (it) {
//                    is File -> it
//                    is String -> File(it)
//                    else -> File(it.toString())
//                }.canonicalFile
//            }.toSet()
//
//            val expected = genDir.canonicalFile
//            assertTrue(
//                targets.any { it == expected || it.path.endsWith("build${File.separator}generated${File.separator}source${File.separator}openapi") },
//                "cleanApiGeneration should be configured to delete $expected"
//            )
//        }
//    }
//}
