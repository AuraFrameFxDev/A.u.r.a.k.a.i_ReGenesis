//@file:Suppress("FunctionName")
//
//package buildlogic
//
//import org.gradle.api.Project
//import org.gradle.api.tasks.Delete
//import org.gradle.testfixtures.ProjectBuilder
//import org.junit.jupiter.api.*
//import org.junit.jupiter.api.io.TempDir
//import java.io.ByteArrayOutputStream
//import java.io.File
//import java.io.PrintStream
//import kotlin.io.path.createDirectories
//import kotlin.io.path.createFile
//import kotlin.io.path.exists
//
//class AndroidNativeConventionPluginTest {
//
//    private lateinit var project: Project
//
//    @BeforeEach
//    fun setup(@TempDir tempDir: File) {
//        project = ProjectBuilder.builder()
//            .withProjectDir(tempDir)
//            .build()
//        // Apply the plugin under test
//        project.plugins.apply(AndroidNativeConventionPlugin::class.java)
//    }
//
//    @Test
//    fun applies_and_registers_verifyNativeConfig_task_with_metadata() {
//        val task = project.tasks.findByName("verifyNativeConfig")
//        Assertions.assertNotNull(task, "verifyNativeConfig task should be registered")
//        Assertions.assertEquals("aegenesis", task!!.group, "verifyNativeConfig group mismatch")
//        Assertions.assertEquals(
//            "Verify native build configuration",
//            task.description,
//            "verifyNativeConfig description mismatch"
//        )
//    }
//
//    @Test
//    fun verifyNativeConfig_prints_no_cmake_message_when_absent() {
//        val out = captureStdout {
//            runTaskActions("verifyNativeConfig")
//        }
//        Assertions.assertTrue(
//            out.contains("No CMakeLists.txt"),
//            "Expected message indicating CMakeLists.txt is absent. Output:\n$out"
//        )
//        Assertions.assertFalse(
//            out.contains("CMake: ✅ Found"),
//            "Should not indicate CMake found when file is absent. Output:\n$out"
//        )
//    }
//
//    @Test
//    fun verifyNativeConfig_detects_cmake_when_created_after_apply(@TempDir tempDir: File) {
//        // Create CMakeLists.txt AFTER plugin apply to avoid triggering LibraryExtension configuration
//        val cmakePath = tempDir.toPath().resolve("src/main/cpp")
//        if (!cmakePath.exists()) cmakePath.createDirectories()
//        cmakePath.resolve("CMakeLists.txt").createFile()
//
//        val out = captureStdout {
//            runTaskActions("verifyNativeConfig")
//        }
//
//        Assertions.assertTrue(
//            out.contains("CMake: ✅ Found"),
//            "Expected to indicate CMake found. Output:\n$out"
//        )
//        Assertions.assertTrue(
//            out.contains("🎯 ABIs: arm64-v8a, armeabi-v7a, x86_64"),
//            "Expected ABIs line in output. Output:\n$out"
//        )
//        Assertions.assertTrue(
//            out.contains("🧠 Consciousness Features: V3 Matrix + Neural Acceleration"),
//            "Expected features line in output. Output:\n$out"
//        )
//    }
//
//    @Test
//    fun cleanGeneratedSources_deletes_convention_build_dirs(@TempDir tempDir: File) {
//        // Arrange: Ensure stub plugin's task exists (added via stub plugin applied by the convention)
//        val clean = project.tasks.findByName("cleanGeneratedSources") as? Delete
//        Assertions.assertNotNull(
//            clean,
//            "cleanGeneratedSources should be present (from stub plugin)"
//        )
//        // Create build dirs/files that convention's enhancement will delete.
//        val buildDir = project.layout.buildDirectory.asFile.get()
//        val targets = listOf(
//            "generated/ksp",
//            "generated/source/ksp",
//            "tmp/kapt3",
//            "tmp/kotlin-classes",
//            "kotlin",
//            "intermediates/cmake"
//        )
//        targets.forEach { rel ->
//            val dir = File(buildDir, rel)
//            dir.mkdirs()
//            File(dir, ".keep").writeText("x")
//            Assertions.assertTrue(dir.exists(), "Setup failed: ${dir.path} should exist")
//        }
//
//        // Act: run doLast actions of cleanGeneratedSources
//        runTaskActions("cleanGeneratedSources")
//
//        // Assert: all targets removed
//        targets.forEach { rel ->
//            val dir = File(buildDir, rel)
//            Assertions.assertFalse(
//                dir.exists(),
//                "Expected ${dir.path} to be deleted by cleanGeneratedSources"
//            )
//        }
//    }
//
//    // --- helpers ---
//
//    private fun runTaskActions(taskName: String) {
//        val task = project.tasks.getByName(taskName)
//        // Execute in-order all attached actions (doFirst/doLast). Sufficient for unit-level verification.
//        task.actions.forEach { it.execute(task) }
//    }
//
//    private fun captureStdout(block: () -> Unit): String {
//        val original = System.out
//        val baos = ByteArrayOutputStream()
//        System.setOut(PrintStream(baos, true, Charsets.UTF_8))
//        return try {
//            block()
//            baos.toString(Charsets.UTF_8)
//        } finally {
//            System.setOut(original)
//        }
//    }
//}
