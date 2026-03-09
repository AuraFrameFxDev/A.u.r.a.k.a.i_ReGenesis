package dev.aurakai.auraframefx

/*
Testing framework and library:
- Using JUnit 5 (Jupiter) for unit tests (org.junit.jupiter.api.*).
- This repository declares testRuntimeOnly(libs.junit.engine), which typically maps to junit-jupiter-engine.
- Tests are text-based validations tailored to the root build.gradle.kts (no new dependencies introduced).
*/

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.File

/**
 * Comprehensive test suite for the root build.gradle.kts file.
 *
 * This test validates:
 * - Plugin declarations and versions
 * - Clean task configuration
 * - All projects configuration (group and version)
 * - Subprojects configuration for Android application and library modules
 * - Java and Kotlin toolchain settings
 * - Test skipping functionality based on gradle properties
 */
class RootBuildGradleKtsTest {

    private fun locateRootBuildFile(): File {
        // Correctly locate the root build file relative to the project structure
        val candidates = listOf(
            File("build.gradle.kts"),
            File("../build.gradle.kts"),
            File("../../build.gradle.kts")
        )
        return candidates.firstOrNull { it.exists() } ?: error(
            "Unable to locate root build.gradle.kts. Checked: " +
                    candidates.joinToString { it.path } +
                    "; workingDir=${System.getProperty("user.dir")}"
        )
    }

    private val rootBuildFile: File by lazy { locateRootBuildFile() }
    private val script: String by lazy { rootBuildFile.readText() }

    @Nested
    @DisplayName("Plugins Configuration")
    inner class PluginsConfiguration {

        @Test
        @DisplayName("Kotlin plugin versions are correctly declared")
        fun kotlinPluginVersions() {
            val expectedVersion = "2.3.0"
            assertTrue(
                script.contains("id(\"org.jetbrains.kotlin.android\") version \"$expectedVersion\" apply false"),
                "Expected Kotlin Android plugin version $expectedVersion"
            )
            assertTrue(
                script.contains("id(\"org.jetbrains.kotlin.plugin.compose\") version \"$expectedVersion\" apply false"),
                "Expected Kotlin Compose plugin version $expectedVersion"
            )
            assertTrue(
                script.contains("id(\"org.jetbrains.kotlin.plugin.serialization\") version \"$expectedVersion\" apply false"),
                "Expected Kotlin Serialization plugin version $expectedVersion"
            )
            assertTrue(
                script.contains("id(\"org.jetbrains.kotlin.plugin.parcelize\") version \"$expectedVersion\" apply false"),
                "Expected Kotlin Parcelize plugin version $expectedVersion"
            )
        }

        @Test
        @DisplayName("Android plugin versions are correctly declared")
        fun androidPluginVersions() {
            val expectedVersion = "9.1.0-alpha06"
            assertTrue(
                script.contains("id(\"com.android.application\") version \"$expectedVersion\" apply false"),
                "Expected Android Application plugin version $expectedVersion"
            )
            assertTrue(
                script.contains("id(\"com.android.library\") version \"$expectedVersion\" apply false"),
                "Expected Android Library plugin version $expectedVersion"
            )
        }

        @Test
        @DisplayName("Hilt plugin version is correctly declared")
        fun hiltPluginVersion() {
            assertTrue(
                script.contains("id(\"com.google.dagger.hilt.android\") version \"2.59\" apply false"),
                "Expected Hilt plugin version 2.59"
            )
        }

        @Test
        @DisplayName("KSP plugin version is correctly declared")
        fun kspPluginVersion() {
            assertTrue(
                script.contains("id(\"com.google.devtools.ksp\") version \"2.3.5\" apply false"),
                "Expected KSP plugin version 2.3.5"
            )
        }

        @Test
        @DisplayName("Google Services plugin version is correctly declared")
        fun googleServicesPluginVersion() {
            assertTrue(
                script.contains("id(\"com.google.gms.google-services\") version \"4.4.4\" apply false"),
                "Expected Google Services plugin version 4.4.4"
            )
        }

        @Test
        @DisplayName("Firebase Crashlytics plugin version is correctly declared")
        fun firebaseCrashlyticsPluginVersion() {
            assertTrue(
                script.contains("id(\"com.google.firebase.crashlytics\") version \"3.0.6\" apply false"),
                "Expected Firebase Crashlytics plugin version 3.0.6"
            )
        }

        @Test
        @DisplayName("All plugins have apply false to prevent root project application")
        fun allPluginsHaveApplyFalse() {
            val pluginIds = listOf(
                "org.jetbrains.kotlin.android",
                "org.jetbrains.kotlin.plugin.compose",
                "org.jetbrains.kotlin.plugin.serialization",
                "org.jetbrains.kotlin.plugin.parcelize",
                "com.android.application",
                "com.android.library",
                "com.google.dagger.hilt.android",
                "com.google.devtools.ksp",
                "com.google.gms.google-services",
                "com.google.firebase.crashlytics"
            )

            pluginIds.forEach { pluginId ->
                assertTrue(
                    Regex("""id\("$pluginId"\).*apply false""").containsMatchIn(script),
                    "Plugin $pluginId should have 'apply false'"
                )
            }
        }
    }

    @Nested
    @DisplayName("Clean Task Configuration")
    inner class CleanTaskConfiguration {

        @Test
        @DisplayName("Clean task is registered with Delete type")
        fun cleanTaskRegistered() {
            assertTrue(
                script.contains("tasks.register(\"clean\", Delete::class)"),
                "Expected clean task registration with Delete type"
            )
        }

        @Test
        @DisplayName("Clean task deletes root project build directory")
        fun cleanTaskDeletesBuildDirectory() {
            assertTrue(
                script.contains("delete(rootProject.layout.buildDirectory)"),
                "Expected clean task to delete root project build directory"
            )
        }
    }

    @Nested
    @DisplayName("All Projects Configuration")
    inner class AllProjectsConfiguration {

        @Test
        @DisplayName("Group is set to dev.aurakai.auraframefx for all projects")
        fun groupConfiguration() {
            assertTrue(
                script.contains("group = \"dev.aurakai.auraframefx\""),
                "Expected group to be set to dev.aurakai.auraframefx"
            )
        }

        @Test
        @DisplayName("Version is set to 0.1.0 for all projects")
        fun versionConfiguration() {
            assertTrue(
                script.contains("version = \"0.1.0\""),
                "Expected version to be set to 0.1.0"
            )
        }

        @Test
        @DisplayName("All projects configuration block exists")
        fun allProjectsBlockExists() {
            assertTrue(
                script.contains("allprojects {"),
                "Expected allprojects configuration block"
            )
        }
    }

    @Nested
    @DisplayName("Test Skip Configuration")
    inner class TestSkipConfiguration {

        @Test
        @DisplayName("Test skip property is read from gradle properties")
        fun testSkipPropertyRead() {
            assertTrue(
                script.contains("val skipTests = providers.gradleProperty(\"aurafx.skip.tests\")"),
                "Expected skipTests property to be read from gradle properties"
            )
        }

        @Test
        @DisplayName("Test skip property defaults to false")
        fun testSkipPropertyDefaultsToFalse() {
            assertTrue(
                script.contains(".orElse(\"false\")"),
                "Expected skipTests to default to false"
            )
        }

        @Test
        @DisplayName("Test skip property is converted to boolean")
        fun testSkipPropertyConvertedToBoolean() {
            assertTrue(
                script.contains(".map { it.toBoolean() }"),
                "Expected skipTests to be converted to boolean"
            )
        }
    }

    @Nested
    @DisplayName("Subprojects Configuration - Application")
    inner class SubprojectsApplicationConfiguration {

        @Test
        @DisplayName("Application extension is configured when application plugin is applied")
        fun applicationExtensionConfigured() {
            assertTrue(
                script.contains("plugins.withId(\"com.android.application\")"),
                "Expected configuration for com.android.application plugin"
            )
            assertTrue(
                script.contains("extensions.configure<com.android.build.api.dsl.ApplicationExtension>"),
                "Expected ApplicationExtension configuration"
            )
        }

        @Test
        @DisplayName("Java 25 source compatibility is set for application modules")
        fun javaSourceCompatibility() {
            val appConfigBlock = extractApplicationConfigBlock()
            assertTrue(
                appConfigBlock.contains("sourceCompatibility = JavaVersion.VERSION_25"),
                "Expected Java 25 source compatibility"
            )
        }

        @Test
        @DisplayName("Java 25 target compatibility is set for application modules")
        fun javaTargetCompatibility() {
            val appConfigBlock = extractApplicationConfigBlock()
            assertTrue(
                appConfigBlock.contains("targetCompatibility = JavaVersion.VERSION_25"),
                "Expected Java 25 target compatibility"
            )
        }

        @Test
        @DisplayName("Kotlin JVM target is set to JVM_25 for application modules")
        fun kotlinJvmTarget() {
            val appConfigBlock = extractApplicationConfigBlock()
            assertTrue(
                appConfigBlock.contains("jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)"),
                "Expected Kotlin JVM target to be JVM_25"
            )
        }

        @Test
        @DisplayName("Kotlin compile tasks are configured for application modules")
        fun kotlinCompileTasksConfigured() {
            val appConfigBlock = extractApplicationConfigBlock()
            assertTrue(
                appConfigBlock.contains("tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach"),
                "Expected Kotlin compile tasks configuration"
            )
            assertTrue(
                appConfigBlock.contains("compilerOptions {"),
                "Expected compiler options configuration"
            )
        }

        @Test
        @DisplayName("Unit tests can be disabled for application modules when skipTests is true")
        fun unitTestsCanBeDisabled() {
            val appConfigBlock = extractApplicationConfigBlock()
            assertTrue(
                appConfigBlock.contains("if (skipTests)"),
                "Expected conditional test disabling logic"
            )
            assertTrue(
                appConfigBlock.contains("(builder as? com.android.build.api.variant.HasUnitTestBuilder)?.enableUnitTest = false"),
                "Expected unit test disabling logic"
            )
        }

        @Test
        @DisplayName("Android tests can be disabled for application modules when skipTests is true")
        fun androidTestsCanBeDisabled() {
            val appConfigBlock = extractApplicationConfigBlock()
            assertTrue(
                appConfigBlock.contains("(builder as? com.android.build.api.variant.HasAndroidTestBuilder)?.enableAndroidTest = false"),
                "Expected Android test disabling logic"
            )
        }

        @Test
        @DisplayName("AndroidComponentsExtension is configured for test disabling in application modules")
        fun androidComponentsExtensionConfigured() {
            val appConfigBlock = extractApplicationConfigBlock()
            assertTrue(
                appConfigBlock.contains("extensions.configure<com.android.build.api.variant.AndroidComponentsExtension<*, *, *>>(\"androidComponents\")"),
                "Expected AndroidComponentsExtension configuration"
            )
            assertTrue(
                appConfigBlock.contains("beforeVariants { builder ->"),
                "Expected beforeVariants configuration"
            )
        }

        private fun extractApplicationConfigBlock(): String {
            val startIndex = script.indexOf("plugins.withId(\"com.android.application\")")
            if (startIndex == -1) return ""

            var braceCount = 0
            var inBlock = false
            val result = StringBuilder()

            for (i in startIndex until script.length) {
                val char = script[i]
                if (char == '{') {
                    braceCount++
                    inBlock = true
                }
                if (inBlock) {
                    result.append(char)
                }
                if (char == '}') {
                    braceCount--
                    if (braceCount == 0) break
                }
            }

            return result.toString()
        }
    }

    @Nested
    @DisplayName("Subprojects Configuration - Library")
    inner class SubprojectsLibraryConfiguration {

        @Test
        @DisplayName("Library extension is configured when library plugin is applied")
        fun libraryExtensionConfigured() {
            assertTrue(
                script.contains("plugins.withId(\"com.android.library\")"),
                "Expected configuration for com.android.library plugin"
            )
            assertTrue(
                script.contains("extensions.configure<com.android.build.api.dsl.LibraryExtension>"),
                "Expected LibraryExtension configuration"
            )
        }

        @Test
        @DisplayName("Java 25 source compatibility is set for library modules")
        fun javaSourceCompatibility() {
            val libConfigBlock = extractLibraryConfigBlock()
            assertTrue(
                libConfigBlock.contains("sourceCompatibility = JavaVersion.VERSION_25"),
                "Expected Java 25 source compatibility"
            )
        }

        @Test
        @DisplayName("Java 25 target compatibility is set for library modules")
        fun javaTargetCompatibility() {
            val libConfigBlock = extractLibraryConfigBlock()
            assertTrue(
                libConfigBlock.contains("targetCompatibility = JavaVersion.VERSION_25"),
                "Expected Java 25 target compatibility"
            )
        }

        @Test
        @DisplayName("Kotlin JVM target is set to JVM_25 for library modules")
        fun kotlinJvmTarget() {
            val libConfigBlock = extractLibraryConfigBlock()
            assertTrue(
                libConfigBlock.contains("jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)"),
                "Expected Kotlin JVM target to be JVM_25"
            )
        }

        @Test
        @DisplayName("Kotlin compile tasks are configured for library modules")
        fun kotlinCompileTasksConfigured() {
            val libConfigBlock = extractLibraryConfigBlock()
            assertTrue(
                libConfigBlock.contains("tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach"),
                "Expected Kotlin compile tasks configuration"
            )
            assertTrue(
                libConfigBlock.contains("compilerOptions {"),
                "Expected compiler options configuration"
            )
        }

        @Test
        @DisplayName("Unit tests can be disabled for library modules when skipTests is true")
        fun unitTestsCanBeDisabled() {
            val libConfigBlock = extractLibraryConfigBlock()
            assertTrue(
                libConfigBlock.contains("if (skipTests)"),
                "Expected conditional test disabling logic"
            )
            assertTrue(
                libConfigBlock.contains("(builder as? com.android.build.api.variant.HasUnitTestBuilder)?.enableUnitTest = false"),
                "Expected unit test disabling logic"
            )
        }

        @Test
        @DisplayName("Android tests can be disabled for library modules when skipTests is true")
        fun androidTestsCanBeDisabled() {
            val libConfigBlock = extractLibraryConfigBlock()
            assertTrue(
                libConfigBlock.contains("(builder as? com.android.build.api.variant.HasAndroidTestBuilder)?.enableAndroidTest = false"),
                "Expected Android test disabling logic"
            )
        }

        @Test
        @DisplayName("AndroidComponentsExtension is configured for test disabling in library modules")
        fun androidComponentsExtensionConfigured() {
            val libConfigBlock = extractLibraryConfigBlock()
            assertTrue(
                libConfigBlock.contains("extensions.configure<com.android.build.api.variant.AndroidComponentsExtension<*, *, *>>(\"androidComponents\")"),
                "Expected AndroidComponentsExtension configuration"
            )
            assertTrue(
                libConfigBlock.contains("beforeVariants { builder ->"),
                "Expected beforeVariants configuration"
            )
        }

        private fun extractLibraryConfigBlock(): String {
            val startIndex = script.indexOf("plugins.withId(\"com.android.library\")")
            if (startIndex == -1) return ""

            var braceCount = 0
            var inBlock = false
            val result = StringBuilder()

            for (i in startIndex until script.length) {
                val char = script[i]
                if (char == '{') {
                    braceCount++
                    inBlock = true
                }
                if (inBlock) {
                    result.append(char)
                }
                if (char == '}') {
                    braceCount--
                    if (braceCount == 0) break
                }
            }

            return result.toString()
        }
    }

    @Nested
    @DisplayName("File Structure and Formatting")
    inner class FileStructureAndFormatting {

        @Test
        @DisplayName("File contains proper header comment")
        fun headerComment() {
            assertTrue(
                script.contains("// Root build.gradle.kts"),
                "Expected root build.gradle.kts header comment"
            )
            assertTrue(
                script.contains("A.u.r.a.K.a.I Reactive Intelligence - Root Build Configuration"),
                "Expected A.u.r.a.K.a.I Reactive Intelligence header"
            )
        }

        @Test
        @DisplayName("File is properly structured with plugins block first")
        fun pluginsBlockFirst() {
            val pluginsIndex = script.indexOf("plugins {")
            val tasksIndex = script.indexOf("tasks.register")
            val allProjectsIndex = script.indexOf("allprojects {")

            assertTrue(pluginsIndex >= 0, "plugins block should exist")
            assertTrue(tasksIndex >= 0, "tasks block should exist")
            assertTrue(allProjectsIndex >= 0, "allprojects block should exist")

            assertTrue(pluginsIndex < tasksIndex, "plugins block should come before tasks")
            assertTrue(tasksIndex < allProjectsIndex, "tasks should come before allprojects")
        }

        @Test
        @DisplayName("Subprojects block exists and comes after allprojects")
        fun subprojectsBlockPosition() {
            val allProjectsIndex = script.indexOf("allprojects {")
            val subprojectsIndex = script.indexOf("subprojects {")

            assertTrue(subprojectsIndex >= 0, "subprojects block should exist")
            assertTrue(
                allProjectsIndex < subprojectsIndex,
                "allprojects block should come before subprojects"
            )
        }
    }

    @Nested
    @DisplayName("Edge Cases and Regression Tests")
    inner class EdgeCasesAndRegressionTests {

        @Test
        @DisplayName("Plugin versions use stable releases (not beta or rc)")
        fun pluginVersionsAreStable() {
            // Check Kotlin versions are stable
            val kotlinVersionPattern =
                Regex("""org\.jetbrains\.kotlin\.[a-z.]+"\) version "(\S+)"""")
            val kotlinVersions = kotlinVersionPattern.findAll(script)

            kotlinVersions.forEach { match ->
                val version = match.groupValues[1]
                assertFalse(
                    version.contains("-beta", ignoreCase = true) ||
                            version.contains("-rc", ignoreCase = true),
                    "Kotlin plugin version should be stable: $version"
                )
            }

            // Hilt version should be stable
            val hiltVersion = Regex("""hilt\.android"\) version "(\S+)"""")
                .find(script)?.groupValues?.get(1)
            assertNotNull(hiltVersion, "Should find Hilt version")
            assertFalse(
                hiltVersion!!.contains("-beta", ignoreCase = true) ||
                        hiltVersion.contains("-rc", ignoreCase = true),
                "Hilt version should be stable: $hiltVersion"
            )
        }

        @Test
        @DisplayName("Android plugin versions consistency between application and library")
        fun androidPluginVersionsConsistent() {
            val appVersionMatch = Regex("""com\.android\.application"\) version "(\S+)"""")
                .find(script)
            val libVersionMatch = Regex("""com\.android\.library"\) version "(\S+)"""")
                .find(script)

            assertNotNull(appVersionMatch, "Should find Android application plugin version")
            assertNotNull(libVersionMatch, "Should find Android library plugin version")

            val appVersion = appVersionMatch!!.groupValues[1]
            val libVersion = libVersionMatch!!.groupValues[1]

            assertEquals(
                appVersion,
                libVersion,
                "Android application and library plugin versions should match"
            )
        }

        @Test
        @DisplayName("Java and Kotlin versions are consistent across application and library configs")
        fun javaKotlinVersionsConsistent() {
            val appBlock = extractConfigBlock("com.android.application")
            val libBlock = extractConfigBlock("com.android.library")

            // Check Java version consistency
            val javaVersionPattern = Regex("""JavaVersion\.VERSION_(\d+)""")
            val appJavaVersions =
                javaVersionPattern.findAll(appBlock).map { it.groupValues[1] }.toSet()
            val libJavaVersions =
                javaVersionPattern.findAll(libBlock).map { it.groupValues[1] }.toSet()

            assertEquals(
                appJavaVersions,
                libJavaVersions,
                "Java versions should be consistent between application and library modules"
            )

            // Check Kotlin JVM target consistency
            val kotlinTargetPattern = Regex("""JvmTarget\.JVM_(\d+)""")
            val appKotlinTargets =
                kotlinTargetPattern.findAll(appBlock).map { it.groupValues[1] }.toSet()
            val libKotlinTargets =
                kotlinTargetPattern.findAll(libBlock).map { it.groupValues[1] }.toSet()

            assertEquals(
                appKotlinTargets,
                libKotlinTargets,
                "Kotlin JVM targets should be consistent between application and library modules"
            )
        }

        @Test
        @DisplayName("Test disabling logic is identical for application and library modules")
        fun testDisablingLogicConsistent() {
            val appBlock = extractConfigBlock("com.android.application")
            val libBlock = extractConfigBlock("com.android.library")

            // Extract test disabling sections
            val appTestBlock = extractTestDisablingBlock(appBlock)
            val libTestBlock = extractTestDisablingBlock(libBlock)

            assertFalse(appTestBlock.isEmpty(), "Application test disabling block should exist")
            assertFalse(libTestBlock.isEmpty(), "Library test disabling block should exist")

            // Normalize whitespace for comparison
            val normalizedApp = appTestBlock.replace(Regex("\\s+"), " ")
            val normalizedLib = libTestBlock.replace(Regex("\\s+"), " ")

            assertEquals(
                normalizedApp,
                normalizedLib,
                "Test disabling logic should be identical for application and library modules"
            )
        }

        @Test
        @DisplayName("No hardcoded version numbers outside of plugin declarations")
        fun noHardcodedVersionsInConfigs() {
            val subprojectsBlock = script.substring(script.indexOf("subprojects {"))

            // Should not contain version strings like "2.3.0" in subprojects config
            val versionPattern = Regex(""""[\d.]+"""")
            val matches = versionPattern.findAll(subprojectsBlock).toList()

            assertTrue(
                matches.isEmpty(),
                "Subprojects configuration should not contain hardcoded version strings. Found: ${matches.map { it.value }}"
            )
        }

        @Test
        @DisplayName("Gradle property name follows naming convention")
        fun gradlePropertyNamingConvention() {
            assertTrue(
                script.contains("aurafx.skip.tests"),
                "Should use aurafx.skip.tests property name"
            )

            // Ensure it follows dot notation convention
            val propertyPattern = Regex("""gradleProperty\("([^"]+)"\)""")
            val properties = propertyPattern.findAll(script)

            properties.forEach { match ->
                val propertyName = match.groupValues[1]
                assertTrue(
                    propertyName.matches(Regex("""[a-z]+(\.[a-z]+)+""")),
                    "Property name should follow lowercase dot notation: $propertyName"
                )
            }
        }

        private fun extractConfigBlock(pluginId: String): String {
            val startIndex = script.indexOf("plugins.withId(\"$pluginId\")")
            if (startIndex == -1) return ""

            var braceCount = 0
            var inBlock = false
            val result = StringBuilder()

            for (i in startIndex until script.length) {
                val char = script[i]
                if (char == '{') {
                    braceCount++
                    inBlock = true
                }
                if (inBlock) {
                    result.append(char)
                }
                if (char == '}') {
                    braceCount--
                    if (braceCount == 0) break
                }
            }

            return result.toString()
        }

        private fun extractTestDisablingBlock(configBlock: String): String {
            val startMarker = "if (skipTests)"
            val startIndex = configBlock.indexOf(startMarker)
            if (startIndex == -1) return ""

            var braceCount = 0
            var foundFirstBrace = false
            val result = StringBuilder()

            for (i in startIndex until configBlock.length) {
                val char = configBlock[i]
                result.append(char)

                if (char == '{') {
                    braceCount++
                    foundFirstBrace = true
                }
                if (char == '}') {
                    braceCount--
                    if (foundFirstBrace && braceCount == 0) break
                }
            }

            return result.toString()
        }
    }

    @Nested
    @DisplayName("Negative and Boundary Cases")
    inner class NegativeAndBoundaryCases {

        @Test
        @DisplayName("No duplicate plugin declarations")
        fun noDuplicatePlugins() {
            val pluginPattern = Regex("""id\("([^"]+)"\)""")
            val plugins = pluginPattern.findAll(script).map { it.groupValues[1] }.toList()

            val duplicates = plugins.groupingBy { it }.eachCount().filter { it.value > 1 }

            assertTrue(
                duplicates.isEmpty(),
                "Found duplicate plugin declarations: ${duplicates.keys}"
            )
        }

        @Test
        @DisplayName("All plugin versions are non-empty and properly formatted")
        fun allPluginVersionsValid() {
            val versionPattern = Regex("""version "([^"]*)".*apply false""")
            val versions = versionPattern.findAll(script).map { it.groupValues[1] }.toList()

            assertTrue(versions.isNotEmpty(), "Should find plugin versions")

            versions.forEach { version ->
                assertFalse(version.isEmpty(), "Plugin version should not be empty")
                assertTrue(
                    version.matches(Regex("""[\d.]+(-(alpha|beta|rc)\d+)?""")),
                    "Plugin version should be properly formatted: $version"
                )
            }
        }

        @Test
        @DisplayName("CompileOptions and KotlinCompile configuration are properly nested")
        fun configurationProperlyNested() {
            // Check that compileOptions is inside extensions.configure
            val compileOptionsPattern = Regex(
                """extensions\.configure<[^>]+>\s*\{[^}]*compileOptions\s*\{""",
                RegexOption.DOT_MATCHES_ALL
            )
            assertTrue(
                compileOptionsPattern.containsMatchIn(script),
                "compileOptions should be nested inside extensions.configure"
            )

            // Check that compilerOptions is inside tasks configuration
            val compilerOptionsPattern = Regex(
                """tasks\.withType<[^>]+>\(\)\.configureEach\s*\{[^}]*compilerOptions\s*\{""",
                RegexOption.DOT_MATCHES_ALL
            )
            assertTrue(
                compilerOptionsPattern.containsMatchIn(script),
                "compilerOptions should be nested inside tasks.withType configuration"
            )
        }

        @Test
        @DisplayName("Safe cast operators are used for test builder types")
        fun safeCastOperatorsUsed() {
            // Verify safe cast (as?) is used instead of unsafe cast (as)
            val unsafeCastPattern = Regex("""builder as com\.android\.build\.api\.variant\.Has""")
            assertFalse(
                unsafeCastPattern.containsMatchIn(script),
                "Should use safe cast (as?) instead of unsafe cast (as) for builder types"
            )

            // Verify safe cast is present
            val safeCastPattern = Regex("""builder as\? com\.android\.build\.api\.variant\.Has""")
            assertTrue(
                safeCastPattern.containsMatchIn(script),
                "Should use safe cast (as?) for builder types"
            )
        }

        @Test
        @DisplayName("getOrElse is used to provide default value for skipTests")
        fun skipTestsHasDefaultValue() {
            assertTrue(
                script.contains(".getOrElse(false)"),
                "skipTests should have a default value using getOrElse"
            )
        }

        @Test
        @DisplayName("Build directory reference uses layout API")
        fun buildDirectoryUsesLayoutApi() {
            assertTrue(
                script.contains("rootProject.layout.buildDirectory"),
                "Should use layout API for build directory reference"
            )

            // Ensure old buildDir API is not used
            assertFalse(
                script.contains("rootProject.buildDir"),
                "Should not use deprecated buildDir property"
            )
        }
    }
}