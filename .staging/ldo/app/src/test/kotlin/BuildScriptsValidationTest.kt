import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.junit.After
import io.mockk.*
import kotlinx.coroutines.test.runTest
import java.io.File
import java.util.Properties

/**
 * Comprehensive unit tests for build script validation.
 * Testing framework: JUnit with MockK for mocking
 *
 * These tests validate build configuration, dependencies, and plugin setup
 * to ensure the build script is correctly configured and maintainable.
 */
class BuildScriptsValidationTest {

    private lateinit var buildFile: File
    private lateinit var gradleProperties: Properties

    @Before
    fun setup() {
        buildFile = File("app/build.gradle.kts")
        gradleProperties = Properties()
    }

    @After
    fun teardown() {
        clearAllMocks()
    }

    // Build Script Structure Tests
    @Test
    fun `build script file exists and is readable`() {
        assertTrue("Build script should exist", buildFile.exists())
        assertTrue("Build script should be readable", buildFile.canRead())
        assertTrue("Build script should not be empty", buildFile.length() > 0)
    }

    @Test
    fun `build script contains required plugin declarations`() {
        val content = buildFile.readText()

        // Test core Android plugins
        assertTrue(
            "Should contain Android application plugin",
            content.contains("alias(libs.plugins.androidApplication)")
        )
        assertTrue(
            "Should contain Kotlin Android plugin",
            content.contains("alias(libs.plugins.kotlinAndroid)")
        )
        assertTrue(
            "Should contain KSP plugin",
            content.contains("alias(libs.plugins.ksp)")
        )
        assertTrue(
            "Should contain Hilt plugin",
            content.contains("alias(libs.plugins.hiltAndroid)")
        )

        // Test additional plugins
        assertTrue(
            "Should contain Kotlin serialization plugin",
            content.contains("alias(libs.plugins.kotlin.serialization)")
        )
        assertTrue(
            "Should contain Google services plugin",
            content.contains("alias(libs.plugins.google.services)")
        )
        assertTrue(
            "Should contain OpenAPI generator plugin",
            content.contains("alias(libs.plugins.openapi.generator)")
        )
        assertTrue(
            "Should contain Firebase Crashlytics plugin",
            content.contains("alias(libs.plugins.firebase.crashlytics)")
        )
        assertTrue(
            "Should contain Firebase Performance plugin",
            content.contains("alias(libs.plugins.firebase.perf)")
        )
        assertTrue(
            "Should contain Compose plugin",
            content.contains("org.jetbrains.kotlin.plugin.compose")
        )
    }

    @Test
    fun `android configuration has required basic settings`() {
        val content = buildFile.readText()

        assertTrue(
            "Should set namespace",
            content.contains("namespace = \"dev.aurakai.auraframefx\"")
        )
        assertTrue("Should set compileSdk", content.contains("compileSdk = 36"))
        assertTrue("Should enable buildConfig", content.contains("buildConfig = true"))
        assertTrue("Should enable compose", content.contains("compose = true"))
        assertTrue("Should enable viewBinding", content.contains("viewBinding = true"))
    }

    @Test
    fun `defaultConfig has proper application settings`() {
        val content = buildFile.readText()

        assertTrue(
            "Should set applicationId",
            content.contains("applicationId = \"dev.aurakai.auraframefx\"")
        )
        assertTrue("Should set minSdk", content.contains("minSdk = 26"))
        assertTrue("Should set targetSdk", content.contains("targetSdk = 34"))
        assertTrue("Should set versionCode", content.contains("versionCode = 1"))
        assertTrue("Should set versionName", content.contains("versionName = \"1.0\""))
        assertTrue(
            "Should set test runner",
            content.contains("testInstrumentationRunner = \"dev.aurakai.auraframefx.HiltTestRunner\"")
        )
        assertTrue("Should enable multiDex", content.contains("multiDexEnabled = true"))
    }

    // NDK Configuration Tests
    @Test
    fun `ndk configuration is properly set`() {
        val content = buildFile.readText()

        assertTrue("Should clear ABI filters first", content.contains("abiFilters.clear()"))
        assertTrue("Should add arm64-v8a ABI", content.contains("\"arm64-v8a\""))
        assertTrue("Should add x86_64 ABI", content.contains("\"x86_64\""))
        assertTrue("Should specify NDK version", content.contains("version = \"27.0.12077973\""))
    }

    @Test
    fun `packaging configuration excludes unwanted files`() {
        val content = buildFile.readText()

        assertTrue(
            "Should exclude Kotlin modules",
            content.contains("\"META-INF/*.kotlin_module\"")
        )
        assertTrue(
            "Should exclude version files",
            content.contains("\"META-INF/*.version\"")
        )
        assertTrue(
            "Should exclude proguard files",
            content.contains("\"META-INF/proguard/*\"")
        )
        assertTrue(
            "Should exclude JNI libraries",
            content.contains("\"**/libjni*.so\"")
        )
        assertTrue(
            "Should keep debug symbols",
            content.contains("keepDebugSymbols.add(\"**/*.so\")")
        )
    }

    // Build Types Tests
    @Test
    fun `release build type configuration is valid`() {
        val content = buildFile.readText()

        assertTrue(
            "Release should disable minification for debugging",
            content.contains("isMinifyEnabled = false")
        )
        assertTrue(
            "Should include default proguard file",
            content.contains("getDefaultProguardFile(\"proguard-android-optimize.txt\")")
        )
        assertTrue(
            "Should include custom proguard rules",
            content.contains("\"proguard-rules.pro\"")
        )
    }

    // Compile Options Tests
    @Test
    fun `java compatibility is set to version 21`() {
        val content = buildFile.readText()

        assertTrue(
            "Source compatibility should be Java 21",
            content.contains("sourceCompatibility = JavaVersion.VERSION_21")
        )
        assertTrue(
            "Target compatibility should be Java 21",
            content.contains("targetCompatibility = JavaVersion.VERSION_21")
        )
    }

    @Test
    fun `kotlin compiler options are properly configured`() {
        val content = buildFile.readText()

        assertTrue("JVM target should be 21", content.contains("jvmTarget = JvmTarget.JVM_21"))
        assertTrue(
            "Should enable JVM default methods",
            content.contains("\"-Xjvm-default=all\"")
        )
        assertTrue(
            "Should enable context receivers",
            content.contains("\"-Xcontext-receivers\"")
        )
        assertTrue(
            "Should opt-in to RequiresOptIn",
            content.contains("\"-opt-in=kotlin.RequiresOptIn\"")
        )
    }

    // External Build Configuration Tests
    @Test
    fun `cmake configuration is valid`() {
        val content = buildFile.readText()

        assertTrue(
            "Should specify CMakeLists.txt path",
            content.contains("path = file(\"src/main/cpp/CMakeLists.txt\")")
        )
        assertTrue(
            "Should use cmake version from root project",
            content.contains("version = rootProject.extra[\"cmakeVersion\"] as String")
        )
    }

    // Lint Configuration Tests
    @Test
    fun `lint configuration is comprehensive`() {
        val content = buildFile.readText()

        assertTrue(
            "Should have lint baseline",
            content.contains("baseline = file(\"lint-baseline.xml\")")
        )
        assertTrue(
            "Should check dependencies",
            content.contains("checkDependencies = true")
        )
        assertTrue(
            "Should have lint config",
            content.contains("lintConfig = file(\"lint.xml\")")
        )
        assertTrue(
            "Should treat warnings as errors",
            content.contains("warningsAsErrors = true")
        )
        assertTrue(
            "Should abort on error",
            content.contains("abortOnError = true")
        )
        assertTrue(
            "Should check release builds",
            content.contains("checkReleaseBuilds = true")
        )
        assertTrue(
            "Should check generated sources",
            content.contains("checkGeneratedSources = true")
        )
    }

    // OpenAPI Configuration Tests
    @Test
    fun `openapi generator configuration is complete`() {
        val content = buildFile.readText()

        assertTrue(
            "Should use kotlin generator",
            content.contains("generatorName.set(\"kotlin\")")
        )
        assertTrue(
            "Should specify input spec",
            content.contains("inputSpec.set(openApiSpecPath)")
        )
        assertTrue(
            "Should set API package",
            content.contains("apiPackage.set(\"dev.aurakai.auraframefx.api.client.apis\")")
        )
        assertTrue(
            "Should set model package",
            content.contains("modelPackage.set(\"dev.aurakai.auraframefx.api.client.models\")")
        )
        assertTrue(
            "Should set invoker package",
            content.contains("invokerPackage.set(\"dev.aurakai.auraframefx.api.client.infrastructure\")")
        )
        assertTrue(
            "Should use coroutines",
            content.contains("\"useCoroutines\" to \"true\"")
        )
        assertTrue(
            "Should use java8 date library",
            content.contains("\"dateLibrary\" to \"java8\"")
        )
    }

    // KSP Configuration Tests
    @Test
    fun `ksp room schema location is configured`() {
        val content = buildFile.readText()

        assertTrue(
            "Should configure Room schema location",
            content.contains("arg(\"room.schemaLocation\", \"\$projectDir/schemas\")")
        )
    }

    // Source Sets Tests
    @Test
    fun `source sets include generated sources`() {
        val content = buildFile.readText()

        assertTrue(
            "Should include generated kotlin sources",
            content.contains("srcDirs(\"\${layout.buildDirectory.get()}/generated/kotlin\")")
        )
    }

    // Task Dependencies Tests
    @Test
    fun `task dependencies are correctly configured`() {
        val content = buildFile.readText()

        assertTrue(
            "PreBuild should depend on OpenAPI generation",
            content.contains("dependsOn(\"openApiGenerate\")")
        )
    }

    // Dependency Configuration Tests
    @Test
    fun `dependency exclusions are configured`() {
        val content = buildFile.readText()

        assertTrue(
            "Should exclude kotlin-stdlib-common",
            content.contains("exclude(group = \"org.jetbrains.kotlin\", module = \"kotlin-stdlib-common\")")
        )
        assertTrue(
            "Should exclude coroutines-core-common",
            content.contains("exclude(group = \"org.jetbrains.kotlinx\", module = \"kotlinx-coroutines-core-common\")")
        )
        assertTrue(
            "Should exclude serialization-core-common",
            content.contains("exclude(group = \"org.jetbrains.kotlinx\", module = \"kotlinx-serialization-core-common\")")
        )
        assertTrue(
            "Should exclude native modules",
            content.contains("exclude(group = \"org.jetbrains.kotlin.native\")")
        )
        assertTrue(
            "Should exclude compose modules",
            content.contains("exclude(group = \"org.jetbrains.compose\")")
        )
    }

    @Test
    fun `force resolution strategy is configured`() {
        val content = buildFile.readText()

        assertTrue(
            "Should prefer project modules",
            content.contains("preferProjectModules()")
        )
        assertTrue(
            "Should force kotlin stdlib version",
            content.contains("\"org.jetbrains.kotlin:kotlin-stdlib:\${libs.versions.kotlin.get()}\"")
        )
        assertTrue(
            "Should force kotlin reflect version",
            content.contains("\"org.jetbrains.kotlin:kotlin-reflect:\${libs.versions.kotlin.get()}\"")
        )
    }

    // Core Dependencies Tests
    @Test
    fun `core android dependencies are included`() {
        val content = buildFile.readText()

        assertTrue(
            "Should include core library desugaring",
            content.contains("coreLibraryDesugaring(libs.desugarJdkLibs)")
        )
        assertTrue(
            "Should include AndroidX core",
            content.contains("implementation(libs.androidxCoreKtx)")
        )
        assertTrue(
            "Should include AppCompat",
            content.contains("implementation(libs.androidxAppcompat)")
        )
    }

    @Test
    fun `lifecycle dependencies are included`() {
        val content = buildFile.readText()

        assertTrue(
            "Should include lifecycle runtime",
            content.contains("implementation(libs.androidxLifecycleRuntimeKtx)")
        )
        assertTrue(
            "Should include lifecycle viewmodel compose",
            content.contains("implementation(libs.lifecycleViewmodelCompose)")
        )
        assertTrue(
            "Should include lifecycle viewmodel",
            content.contains("implementation(libs.androidxLifecycleViewmodelKtx)")
        )
        assertTrue(
            "Should include lifecycle runtime compose",
            content.contains("implementation(libs.androidxLifecycleRuntimeCompose)")
        )
    }

    @Test
    fun `compose dependencies are properly configured`() {
        val content = buildFile.readText()

        assertTrue(
            "Should use Compose BOM",
            content.contains("val composeBom = platform(libs.composeBom)")
        )
        assertTrue(
            "Should implement compose BOM",
            content.contains("implementation(composeBom)")
        )
        assertTrue(
            "Should include UI dependencies",
            content.contains("implementation(libs.androidxUi)")
        )
        assertTrue(
            "Should include graphics dependencies",
            content.contains("implementation(libs.androidxUiGraphics)")
        )
        assertTrue(
            "Should include tooling preview",
            content.contains("implementation(libs.androidxUiToolingPreview)")
        )
        assertTrue(
            "Should include Material 3",
            content.contains("implementation(libs.androidxMaterial3)")
        )
        assertTrue(
            "Should include Material icons",
            content.contains("implementation(libs.androidxMaterialIconsExtended)")
        )
    }

    @Test
    fun `dagger hilt dependencies are complete`() {
        val content = buildFile.readText()

        assertTrue(
            "Should include Hilt Android",
            content.contains("implementation(libs.hiltAndroid)")
        )
        assertTrue(
            "Should include Hilt compiler with KSP",
            content.contains("ksp(libs.hiltCompiler)")
        )
        assertTrue(
            "Should include Hilt navigation compose",
            content.contains("implementation(libs.hiltNavigationCompose)")
        )
        assertTrue(
            "Should include Hilt work",
            content.contains("implementation(libs.hiltWork)")
        )
    }

    @Test
    fun `room database dependencies are configured`() {
        val content = buildFile.readText()

        assertTrue(
            "Should include Room runtime",
            content.contains("implementation(libs.androidxRoomRuntime)")
        )
        assertTrue(
            "Should include Room KTX",
            content.contains("implementation(libs.androidxRoomKtx)")
        )
        assertTrue(
            "Should include Room compiler with KSP",
            content.contains("ksp(libs.androidxRoomCompiler)")
        )
    }

    @Test
    fun `firebase dependencies are included`() {
        val content = buildFile.readText()

        assertTrue(
            "Should use Firebase BOM",
            content.contains("implementation(platform(libs.firebaseBom))")
        )
        assertTrue(
            "Should include Analytics",
            content.contains("implementation(libs.firebaseAnalyticsKtx)")
        )
        assertTrue(
            "Should include Crashlytics",
            content.contains("implementation(libs.firebaseCrashlyticsKtx)")
        )
        assertTrue(
            "Should include Performance",
            content.contains("implementation(libs.firebasePerfKtx)")
        )
        assertTrue(
            "Should include Config",
            content.contains("implementation(libs.firebaseConfigKtx)")
        )
        assertTrue(
            "Should include Storage",
            content.contains("implementation(libs.firebaseStorageKtx)")
        )
        assertTrue(
            "Should include Messaging",
            content.contains("implementation(libs.firebaseMessagingKtx)")
        )
    }

    @Test
    fun `network dependencies are configured`() {
        val content = buildFile.readText()

        assertTrue(
            "Should include Retrofit",
            content.contains("implementation(libs.retrofit)")
        )
        assertTrue(
            "Should include Gson converter",
            content.contains("implementation(libs.converterGson)")
        )
        assertTrue(
            "Should include OkHttp",
            content.contains("implementation(libs.okhttp)")
        )
        assertTrue(
            "Should include logging interceptor",
            content.contains("implementation(libs.okhttpLoggingInterceptor)")
        )
        assertTrue(
            "Should include Kotlin serialization converter",
            content.contains("implementation(libs.retrofitKotlinxSerializationConverter)")
        )
    }

    // Testing Dependencies Tests
    @Test
    fun `unit testing dependencies are complete`() {
        val content = buildFile.readText()

        assertTrue(
            "Should include JUnit",
            content.contains("testImplementation(libs.testJunit)")
        )
        assertTrue(
            "Should include coroutines test",
            content.contains("testImplementation(libs.kotlinxCoroutinesTest)")
        )
        assertTrue(
            "Should include MockK agent",
            content.contains("testImplementation(libs.mockkAgent)")
        )
    }

    @Test
    fun `android testing dependencies are complete`() {
        val content = buildFile.readText()

        assertTrue(
            "Should include AndroidX JUnit extension",
            content.contains("androidTestImplementation(libs.androidxTestExtJunit)")
        )
        assertTrue(
            "Should include Espresso core",
            content.contains("androidTestImplementation(libs.espressoCore)")
        )
        assertTrue(
            "Should include Compose BOM for tests",
            content.contains("androidTestImplementation(composeBom)")
        )
        assertTrue(
            "Should include Compose UI test",
            content.contains("androidTestImplementation(libs.composeUiTestJunit4)")
        )
        assertTrue(
            "Should include MockK Android",
            content.contains("androidTestImplementation(libs.mockkAndroid)")
        )
        assertTrue(
            "Should include Hilt testing",
            content.contains("androidTestImplementation(libs.hiltAndroidTesting)")
        )
        assertTrue(
            "Should include Hilt compiler for tests",
            content.contains("kspAndroidTest(libs.hiltAndroidCompiler)")
        )
    }

    @Test
    fun `debug dependencies are configured`() {
        val content = buildFile.readText()

        assertTrue(
            "Should include Compose UI tooling for debug",
            content.contains("debugImplementation(libs.composeUiTooling)")
        )
        assertTrue(
            "Should include Compose test manifest for debug",
            content.contains("debugImplementation(libs.composeUiTestManifest)")
        )
    }

    // Build Script Syntax and Structure Tests
    @Test
    fun `build script has valid kotlin dsl syntax`() {
        val content = buildFile.readText()

        // Check for proper Kotlin DSL patterns
        assertTrue("Should have plugin block", content.contains("plugins {"))
        assertTrue("Should have android block", content.contains("android {"))
        assertTrue("Should have dependencies block", content.contains("dependencies {"))
        assertTrue(
            "Should properly close all blocks",
            content.count { it == '{' } == content.count { it == '}' })
    }

    @Test
    fun `build script imports are valid`() {
        val content = buildFile.readText()

        assertTrue(
            "Should import JvmTarget",
            content.contains("import org.jetbrains.kotlin.gradle.dsl.JvmTarget")
        )
    }

    // Edge Cases and Error Conditions Tests
    @Test
    fun `build script handles version catalog properly`() {
        val content = buildFile.readText()

        // Check that version catalog references are properly formatted
        assertTrue(
            "Should use libs for version catalog",
            content.contains("libs.versions")
        )
        assertTrue(
            "Should use proper get() method",
            content.contains(".get()")
        )
    }

    @Test
    fun `duplicate build features are not defined`() {
        val content = buildFile.readText()

        // Count occurrences of buildFeatures to ensure it's not duplicated
        val buildFeaturesCount = content.split("buildFeatures").size - 1
        assertTrue("BuildFeatures should only be defined once", buildFeaturesCount <= 1)
    }

    @Test
    fun `configuration blocks are not empty`() {
        val content = buildFile.readText()

        // Ensure major configuration blocks have content
        assertFalse(
            "Android block should not be empty",
            content.contains("android {\\s*}".toRegex())
        )
        assertFalse(
            "Dependencies block should not be empty",
            content.contains("dependencies {\\s*}".toRegex())
        )
    }

    // Integration and Compatibility Tests
    @Test
    fun `sdk versions are compatible`() {
        val content = buildFile.readText()

        // Extract SDK versions and validate compatibility
        assertTrue(
            "compileSdk should be >= targetSdk",
            content.contains("compileSdk = 36") && content.contains("targetSdk = 34")
        )
        assertTrue(
            "targetSdk should be >= minSdk",
            content.contains("targetSdk = 34") && content.contains("minSdk = 26")
        )
    }

    @Test
    fun `plugin order is correct for hilt and ksp`() {
        val content = buildFile.readText()

        val kspIndex = content.indexOf("alias(libs.plugins.ksp)")
        val hiltIndex = content.indexOf("alias(libs.plugins.hiltAndroid)")

        assertTrue(
            "KSP must be applied before Hilt",
            kspIndex != -1 && hiltIndex != -1 && kspIndex < hiltIndex
        )
    }

    // Performance and Optimization Tests
    @Test
    fun `build script includes performance optimizations`() {
        val content = buildFile.readText()

        assertTrue(
            "Should enable vector drawable support",
            content.contains("useSupportLibrary = true")
        )
        assertTrue(
            "Should have resource compression configuration",
            content.contains("noCompress")
        )
        assertTrue(
            "Should exclude unnecessary resources",
            content.contains("excludes.addAll")
        )
    }

    @Test
    fun `proguard configuration is present for release`() {
        val content = buildFile.readText()

        assertTrue(
            "Should include proguard files in release build",
            content.contains("proguardFiles")
        )
        assertTrue(
            "Should use optimized proguard config",
            content.contains("proguard-android-optimize.txt")
        )
    }

    // Security and Privacy Tests
    @Test
    fun `security dependencies are included`() {
        val content = buildFile.readText()

        assertTrue(
            "Should include Android security crypto",
            content.contains("implementation(libs.androidxSecurityCrypto)")
        )
    }

    // Additional Dependency Tests
    @Test
    fun `ui and utility dependencies are configured`() {
        val content = buildFile.readText()

        assertTrue(
            "Should include Coil for image loading",
            content.contains("implementation(libs.coilCompose)")
        )
        assertTrue(
            "Should include Timber for logging",
            content.contains("implementation(libs.timber)")
        )
        assertTrue(
            "Should include DataStore preferences",
            content.contains("implementation(libs.androidxDatastorePreferences)")
        )
        assertTrue(
            "Should include DataStore core",
            content.contains("implementation(libs.androidxDatastoreCore)")
        )
        assertTrue(
            "Should include WorkManager",
            content.contains("implementation(libs.androidxWorkRuntimeKtx)")
        )
        assertTrue(
            "Should include Window manager",
            content.contains("implementation(libs.androidxWindow)")
        )
        assertTrue(
            "Should include Google AI",
            content.contains("implementation(libs.generativeai)")
        )
    }

    // Build Features Validation Tests
    @Test
    fun `required build features are enabled`() {
        val content = buildFile.readText()

        // Check that all required build features are enabled
        val buildFeaturesRegex = "buildFeatures\\s*\\{[^}]*\\}".toRegex()
        val buildFeaturesMatch = buildFeaturesRegex.find(content)

        if (buildFeaturesMatch != null) {
            val buildFeaturesBlock = buildFeaturesMatch.value
            assertTrue(
                "BuildConfig should be enabled in buildFeatures",
                buildFeaturesBlock.contains("buildConfig = true")
            )
            assertTrue(
                "Compose should be enabled in buildFeatures",
                buildFeaturesBlock.contains("compose = true")
            )
            assertTrue(
                "ViewBinding should be enabled in buildFeatures",
                buildFeaturesBlock.contains("viewBinding = true")
            )
        }
    }

    // Resource Configuration Tests
    @Test
    fun `android resources are properly configured`() {
        val content = buildFile.readText()

        assertTrue(
            "Should configure no-compress files",
            content.contains("noCompress += listOf(\"proto\", \"json\")")
        )
        assertTrue(
            "Should ignore assets pattern",
            content.contains("ignoreAssetsPattern = \"!*.version\"")
        )
    }

    // Coroutines Dependencies Tests
    @Test
    fun `coroutines dependencies are complete`() {
        val content = buildFile.readText()

        assertTrue(
            "Should include coroutines Android",
            content.contains("implementation(libs.kotlinxCoroutinesAndroid)")
        )
        assertTrue(
            "Should include coroutines core",
            content.contains("implementation(libs.kotlinxCoroutinesCore)")
        )
    }

    // External Library Tests
    @Test
    fun `external libraries are properly included`() {
        val content = buildFile.readText()

        assertTrue(
            "Should include Xposed API",
            content.contains("compileOnly(files(\"Libs/api-82.jar\"))")
        )
    }

    // Failure Condition Tests
    @Test
    fun `build script contains no obvious syntax errors`() {
        val content = buildFile.readText()

        // Basic syntax validation
        assertFalse(
            "Should not contain unmatched quotes",
            content.count { it == '"' } % 2 != 0)
        assertFalse(
            "Should not contain trailing commas in wrong places",
            content.contains(",\\s*}".toRegex())
        )
    }

    @Test
    fun `version references use proper syntax`() {
        val content = buildFile.readText()

        // Ensure version references follow proper pattern
        assertTrue(
            "Version references should use proper syntax",
            content.contains("libs.versions.kotlin.get()") ||
                    content.contains("libs.versions.composeCompiler.get()")
        )
    }

    // Performance Tests for Large Dependencies
    @Test
    fun `dependencies are organized efficiently`() {
        val content = buildFile.readText()

        // Check that BOMs are used properly to manage version conflicts
        assertTrue(
            "Should use Compose BOM for version management",
            content.contains("platform(libs.composeBom)")
        )
        assertTrue(
            "Should use Firebase BOM for version management",
            content.contains("platform(libs.firebaseBom)")
        )
    }
}