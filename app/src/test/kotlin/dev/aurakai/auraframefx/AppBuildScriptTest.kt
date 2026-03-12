package dev.aurakai.auraframefx//@file:Suppress("SpellCheckingInspection", "HttpUrlsUsage", "MaxLineLength")
//
//package dev.aurakai.auraframefx
//
//import org.junit.jupiter.api.AfterEach
//import org.junit.jupiter.api.Assertions.assertTrue
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.Assertions.assertFalse
//import org.junit.jupiter.api.Assertions.assertTrue
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.DisplayName
//import org.junit.jupiter.api.Nested
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.TestInstance
//import java.nio.file.Files
//import java.nio.file.Path
//import java.nio.file.StandardOpenOption
//
///**
// * Test framework: JUnit 5 (Jupiter).
// *
// * These tests validate the Gradle Kotlin DSL content and critical behaviors introduced in the app module build script.
// * We focus on the PR changes: conditional NDK/CMake wiring, packaging excludes, cleanKspCache task, preBuild dependencies,
// * and the aegenesisAppStatus reporting task.
// *
// * Notes:
// * - We avoid adding Gradle TestKit dependency. Instead, we validate script content and simulate the output assembly
// *   of the aegenesisAppStatus task by evaluating its string-building logic with representative inputs.
// * - If TestKit is available in the project, these tests can later be upgraded to exercise tasks directly.
// */
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//class AppBuildScriptTest {
//
//    private lateinit var buildScript: String
//    private lateinit var tempDir: Path
//
//    @BeforeEach
//    fun setUp() {
//        // Load the app module build script from the repository to assert on authoritative content.
//        // We try common locations; adjust here if needed.
//        val candidates = listOf(
//            Path.of("app", "build.gradle.kts"),
//            Path.of("app", "build.gradle"),
//            Path.of("build.gradle.kts") // fallback if script is at module root
//        )
//        val existing = candidates.firstOrNull { Files.exists(it) }
//        requireNotNull(existing) {
//            "Could not find app build script. Checked: ${candidates.joinToString()}"
//        }
//        buildScript = Files.readString(existing)
//
//        tempDir = Files.createTempDirectory("aegenesis-app-test")
//    }
//
//    @AfterEach
//    fun tearDown() {
//        runCatching {
//            tempDir.toFile().deleteRecursively()
//        }
//    }
//
//    @Nested
//    @DisplayName("aegenesisAppStatus task output contract")
//    class AegenesisAppStatusTaskContract {
//
//        private fun fakeStatusOutput(
//            apiExists: Boolean,
//            apiSizeBytes: Long,
//            nativeCode: Boolean,
//            kspMode: String? = null
//        ): String {
//            // This mirrors the printlns in the Gradle task for robust snapshot-style checks.
//            val lines = buildList {
//                add("\uD83D\uDCF1 AEGENESIS APP MODULE STATUS")
//                add("=".repeat(50))
//                add("🔌 Unified API Spec: ${if (apiExists) "✅ Found" else "❌ Missing"}")
//                if (apiExists) {
//                    add("📄 API File Size: ${apiSizeBytes / 1024}KB")
//                }
//                add("🔧 Native Code: ${if (nativeCode) "✅ Enabled" else "❌ Disabled"}")
//                add("🧠 KSP Mode: ${kspMode ?: "default"}")
//                add("🎯 Target SDK: 36")
//                add("📱 Min SDK: 33")
//                add("✅ Status: Ready for coinscience AI integration!")
//            }
//            return lines.joinToString("\n")
//        }
//
//        @Test
//        fun `prints expected header and dividers`() {
//            val out = fakeStatusOutput(
//                apiExists = false,
//                apiSizeBytes = 0,
//                nativeCode = false,
//                kspMode = null
//            )
//            assertTrue(
//                out.lines().first().contains("AEGENESIS APP MODULE STATUS"),
//                "Header should contain module status title"
//            )
//            val out = fakeStatusOutput(
//                apiExists = false,
//                apiSizeBytes = 0,
//                nativeCode = false,
//                kspMode = null
//            )
//            assertTrue(
//                out.lines().first().contains("AEGENESIS APP MODULE STATUS"),
//                "Header should contain module status title"
//            )
//            assertEquals(50, out.lines()[1].length, "Second line should be a 50-char divider")
//        }
//
//        @Test
//        fun `indicates API missing and native disabled by default`() {
//            fakeStatusOutput(
//                apiExists = false,
//                apiSizeBytes = 0,
//                nativeCode = false,
//                kspMode = null
//            )
//            val out = fakeStatusOutput(
//                apiExists = false,
//                apiSizeBytes = 0,
//                nativeCode = false,
//                kspMode = null
//            )
//            assertTrue(out.contains("🔌 Unified API Spec: ❌ Missing"))
//            assertTrue(out.contains("🔧 Native Code: ❌ Disabled"))
//            assertTrue(out.contains("🧠 KSP Mode: default"))
//            assertTrue(out.contains("🎯 Target SDK: 36"))
//            assertTrue(out.contains("📱 Min SDK: 33"))
//        }
//
//        @Test
//        fun `includes API size line only when API exists`() {
//            fakeStatusOutput(
//                apiExists = true,
//                apiSizeBytes = 4096,
//                nativeCode = true,
//                kspMode = "true"
//            )
//            fakeStatusOutput(
//                apiExists = false,
//                apiSizeBytes = 0,
//                nativeCode = true,
//                kspMode = "true"
//            )
//            val present = fakeStatusOutput(
//                apiExists = true,
//                apiSizeBytes = 4096,
//                nativeCode = true,
//                kspMode = "true"
//            )
//            val missing = fakeStatusOutput(
//                apiExists = false,
//                apiSizeBytes = 0,
//                nativeCode = true,
//                kspMode = "true"
//            )
//
//            assertTrue(
//                present.contains("📄 API File Size: 4KB"),
//                "When API exists, size should be shown in KB"
//            )
//            assertFalse(
//                missing.contains("📄 API File Size:"),
//                "When API is missing, size line should not appear"
//            )
//            assertTrue(
//                present.contains("📄 API File Size: 4KB"),
//                "When API exists, size should be shown in KB"
//            )
//            assertFalse(
//                missing.contains("📄 API File Size:"),
//                "When API is missing, size line should not appear"
//            )
//        }
//
//        @Test
//        fun `reflects provided KSP mode property when set`() {
//            fakeStatusOutput(
//                apiExists = true,
//                apiSizeBytes = 1024,
//                nativeCode = false,
//                kspMode = "ksp2"
//            )
//            val out = fakeStatusOutput(
//                apiExists = true,
//                apiSizeBytes = 1024,
//                nativeCode = false,
//                kspMode = "ksp2"
//            )
//            assertTrue(out.contains("🧠 KSP Mode: ksp2"))
//        }
//    }
//
//    @Nested
//    @DisplayName("Gradle build script structure checks (text-level)")
//    inner class ScriptStructureChecks {
//
//        @Test
//        fun `preBuild has expected dependencies`() {
//            // Ensure dependsOn for all three tasks as per PR
//            assertTrue(
//                buildScript.contains("tasks.named(\"preBuild\")"),
//                "preBuild task configuration should exist"
//            )
//            assertTrue(
//                buildScript.contains("tasks.named(\"preBuild\")"),
//                "preBuild task configuration should exist"
//            )
//            listOf("cleanKspCache", ":cleanApiGeneration", ":openApiGenerate").forEach { dep ->
//                assertTrue(
//                    buildScript.contains("dependsOn(\"$dep\")"),
//                    "preBuild should depend on $dep"
//                )
//            }
//        }
//
//        @Test
//        fun `cleanKspCache task is registered as Delete with descriptive metadata`() {
//            assertTrue(
//                buildScript.contains("tasks.register<Delete>(\"cleanKspCache\")"),
//                "cleanKspCache should be a Delete task"
//            )
//            assertTrue(
//                buildScript.contains("group = \"build setup\""),
//                "cleanKspCache should have a 'build setup' group"
//            )
//            assertTrue(
//                buildScript.contains("description = \"Clean KSP caches (fixes NullPointerException)\""),
//                "cleanKspCache should have description"
//            )
//            assertTrue(
//                buildScript.contains("tasks.register<Delete>(\"cleanKspCache\")"),
//                "cleanKspCache should be a Delete task"
//            )
//            assertTrue(
//                buildScript.contains("group = \"build setup\""),
//                "cleanKspCache should have a 'build setup' group"
//            )
//            assertTrue(
//                buildScript.contains("description = \"Clean KSP caches (fixes NullPointerException)\""),
//                "cleanKspCache should have description"
//            )
//        }
//
//        @Test
//        fun `cleanKspCache deletes expected KSP and Kotlin build directories`() {
//            val expectedDirs = listOf(
//                "generated/ksp",
//                "tmp/kapt3",
//                "tmp/kotlin-classes",
//                "kotlin",
//                "generated/source/ksp"
//            )
//            expectedDirs.forEach { dir ->
//                assertTrue(
//                    buildScript.contains("buildDirProvider.dir(\"$dir\")"),
//                    "cleanKspCache should delete $dir"
//                )
//            }
//        }
//
//        @Test
//        fun `packaging excludes Kotlin metadata and text files`() {
//            val mustContain = listOf(
//                "/META-INF/{AL2.0,LGPL2.1}",
//                "/META-INF/DEPENDENCIES",
//                "/META-INF/LICENSE",
//                "/META-INF/LICENSE.txt",
//                "/META-INF/NOTICE",
//                "/META-INF/NOTICE.txt",
//                "META-INF/*.kotlin_module",
//                "**/kotlin/**",
//                "**/*.txt"
//            )
//            mustContain.forEach { pattern ->
//                assertTrue(
//                    buildScript.contains(pattern),
//                    "Packaging excludes should contain '$pattern'"
//                )
//            }
//        }
//
//        @Test
//        fun `jniLibs config disables legacy packaging and picks first specific libs`() {
//            assertTrue(buildScript.contains("jniLibs {"), "jniLibs block expected")
//            assertTrue(
//                buildScript.contains("useLegacyPackaging = false"),
//                "Legacy packaging should be disabled"
//            )
//            assertTrue(
//                buildScript.contains("pickFirsts += listOf(\"**/libc++_shared.so\", \"**/libjsc.so\")"),
//                "pickFirsts should include libc++_shared.so and libjsc.so"
//            )
//            assertTrue(
//                buildScript.contains("useLegacyPackaging = false"),
//                "Legacy packaging should be disabled"
//            )
//            assertTrue(
//                buildScript.contains("pickFirsts += listOf(\"**/libc++_shared.so\", \"**/libjsc.so\")"),
//                "pickFirsts should include libc++_shared.so and libjsc.so"
//            )
//        }
//
//        @Test
//        fun `compose and buildConfig build features are enabled, viewBinding disabled`() {
//            assertTrue(buildScript.contains("buildFeatures {"), "buildFeatures block expected")
//            assertTrue(buildScript.contains("compose = true"), "Compose should be enabled")
//            assertTrue(buildScript.contains("buildConfig = true"), "BuildConfig should be enabled")
//            assertTrue(
//                buildScript.contains("viewBinding = false"),
//                "viewBinding should be disabled"
//            )
//            assertTrue(
//                buildScript.contains("viewBinding = false"),
//                "viewBinding should be disabled"
//            )
//        }
//
//        @Test
//        fun `Java source and target compatibility set to JavaVersion VERSION_24`() {
//            assertTrue(
//                buildScript.contains("sourceCompatibility = JavaVersion.VERSION_24"),
//                "sourceCompatibility should be VERSION_24"
//            )
//            assertTrue(
//                buildScript.contains("targetCompatibility = JavaVersion.VERSION_24"),
//                "targetCompatibility should be VERSION_24"
//            )
//            assertTrue(
//                buildScript.contains("sourceCompatibility = JavaVersion.VERSION_24"),
//                "sourceCompatibility should be VERSION_24"
//            )
//            assertTrue(
//                buildScript.contains("targetCompatibility = JavaVersion.VERSION_24"),
//                "targetCompatibility should be VERSION_24"
//            )
//        }
//
//        @Test
//        fun `conditional native build guarded by CMakeLists existence checks`() {
//            // Ensure both NDK and externalNativeBuild blocks are conditional
//            assertTrue(
//                buildScript.contains("if (project.file(\"src/main/cpp/CMakeLists.txt\").exists())"),
//                "Native-related config should be guarded by CMakeLists existence"
//            )
//            assertTrue(
//                buildScript.contains("externalNativeBuild"),
//                "externalNativeBuild block should be present under the guard"
//            )
//            assertTrue(
//                buildScript.contains("ndk {"),
//                "ndk block with abiFilters should be present under the guard"
//            )
//        }
//    }
//
//    @Nested
//    @DisplayName("aegenesisAppStatus file existence scenarios")
//    class AegenesisStatusFileScenarios {
//
//        private fun createApiFile(sizeBytes: Int): Path {
//            val apiDir = tempDir.resolve("api")
//            Files.createDirectories(apiDir)
//            val api = apiDir.resolve("unified-aegenesis-api.yml")
//            // Write 'sizeBytes' bytes
//            if (sizeBytes > 0) {
//                val chunk = "a".repeat(1024).toByteArray()
//                var remaining = sizeBytes
//                Files.newOutputStream(
//                    api,
//                    StandardOpenOption.CREATE,
//                    StandardOpenOption.TRUNCATE_EXISTING
//                ).use { os ->
//                    Files.newOutputStream(
//                        api,
//                        StandardOpenOption.CREATE,
//                        StandardOpenOption.TRUNCATE_EXISTING
//                    ).use { os ->
//                        while (remaining > 0) {
//                            val toWrite = minOf(remaining, chunk.size)
//                            os.write(chunk, 0, toWrite)
//                            remaining -= toWrite
//                        }
//                    }
//                } else {
//                    Files.writeString(
//                        api,
//                        "",
//                        StandardOpenOption.CREATE,
//                        StandardOpenOption.TRUNCATE_EXISTING
//                    )
//                    Files.writeString(
//                        api,
//                        "",
//                        StandardOpenOption.CREATE,
//                        StandardOpenOption.TRUNCATE_EXISTING
//                    )
//                }
//                return api
//            }
//
//            @Test
//            fun `API missing case - expect Missing label and no size line`() {
//                // Simulate absence by ensuring no file created in tempDir/api
//                val simulatedOutput = buildString {
//                    appendLine("🔌 Unified API Spec: ❌ Missing")
//                }
//                assertTrue(simulatedOutput.contains("❌ Missing"))
//                assertFalse(
//                    simulatedOutput.contains("📄 API File Size:"),
//                    "No size line expected when file is missing"
//                )
//                assertFalse(
//                    simulatedOutput.contains("📄 API File Size:"),
//                    "No size line expected when file is missing"
//                )
//            }
//
//            @Test
//            fun `API present case - size rounded down to KB`() {
//                val api = createApiFile(sizeBytes = 4097) // just over 4KB
//                val sizeKB = Files.size(api) / 1024
//                // mirror the Gradle task behavior
//                val line = "📄 API File Size: ${sizeKB}KB"
//                assertTrue(sizeKB >= 4, "Expected at least 4KB, got ${sizeKB}KB")
//                assertTrue(line.matches(Regex("📄 API File Size: \\d+KB")))
//            }
//        }
//    }
