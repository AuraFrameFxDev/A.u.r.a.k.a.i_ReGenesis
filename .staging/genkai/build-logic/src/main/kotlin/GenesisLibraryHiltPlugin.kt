import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * ===================================================================
 * GENESIS LIBRARY CONVENTION PLUGIN WITH HILT
 * ===================================================================
 *
 * Convention plugin for Android library modules that REQUIRE Hilt dependency injection.
 *
 * This plugin configures everything from GenesisLibraryPlugin PLUS:
 * - Hilt dependency injection
 * - KSP annotation processing for Hilt
 *
 * Plugin Application Order:
 * 1. org.jetbrains.kotlin.android (Required for Hilt even with built-in Kotlin)
 * 2. com.android.library
 * 3. org.jetbrains.kotlin.plugin.compose
 * 4. com.google.dagger.hilt.android (HILT - only in this variant)
 * 5. com.google.devtools.ksp (KSP - only in this variant)
 * 6. org.jetbrains.kotlin.plugin.serialization
 *
 * Usage:
 * plugins {
 *     id("genesis.android.library.hilt")  // Use this variant for modules needing Hilt
 * }
 *
 * @since Genesis Protocol 2.0 (AGP 9.0.0-alpha14 Compatible)
 */
class GenesisLibraryHiltPlugin : Plugin<Project> {
    /**
     * Configures the given Gradle project as an Android library module with Hilt, KSP, Jetpack Compose, and Kotlin serialization support.
     *
     * Configures the Android Library extension (SDK/NDK, build types, compile options, build features, packaging exclusions, and lint),
     * delegates Kotlin/JVM toolchain and compiler settings to GenesisJvmConfig, and adds convention-managed dependencies including Hilt and its KSP compiler.
     *
     * @param project The Gradle project to configure.
     */
    override fun apply(project: Project) {
        with(project) {
            // Apply core plugins. Android must be first.
            pluginManager.apply("com.android.library")
            pluginManager.apply("org.jetbrains.kotlin.android")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

            // Apply dependent plugins only after the Android plugin has been configured.
            plugins.withId("com.android.library") {
                pluginManager.apply("com.google.dagger.hilt.android")
                pluginManager.apply("com.google.devtools.ksp")
            }

            extensions.configure<LibraryExtension> {
                compileSdk = 36
                ndkVersion = "29.0.14206865"

                defaultConfig {
                    minSdk = 34
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

                    ndk {
                        abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
                    }
                }

                buildTypes {
                    getByName("release") {
                        isMinifyEnabled = false
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }
                }

                // Java 21 bytecode (Compatible with current JVM)
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_25
                    targetCompatibility = JavaVersion.VERSION_25
                    isCoreLibraryDesugaringEnabled = true
                }

                buildFeatures {
                    compose = true
                    buildConfig = true
                    aidl = true
                }

                packaging {
                    resources {
                        excludes += setOf(
                            "/META-INF/{AL2.0,LGPL2.1}",
                            "/META-INF/LICENSE*",
                            "/META-INF/NOTICE*"
                        )
                    }
                }

                lint {
                    baseline = file("lint-baseline.xml")
                    abortOnError = false
                    checkReleaseBuilds = false
                }
            }

            // Configure Kotlin JVM toolchain and compilation options
            GenesisJvmConfig.configureKotlinJvm(project)

            // ═══════════════════════════════════════════════════════════════════════════
            // Auto-configured dependencies (provided by convention plugin)
            // ═══════════════════════════════════════════════════════════════════════════

            // ✅ Hilt Dependency Injection (ONLY in Hilt variant)
            dependencies.add("implementation", "com.google.dagger:hilt-android:2.57.2")
            dependencies.add("ksp", "com.google.dagger:hilt-android-compiler:2.57.2")

            // YukiHookAPI KSP processor (for modules using YukiHook)
            dependencies.add("implementation", "com.highcapable.yukihookapi:api:1.3.1")
            dependencies.add("ksp", "com.highcapable.yukihookapi:ksp-xposed:1.3.1")

            // Core Android libraries
            dependencies.add("implementation", "androidx.core:core-ktx:1.17.0")
            dependencies.add("implementation", "androidx.appcompat:appcompat:1.7.1")

            // Kotlin Coroutines
            dependencies.add("implementation", "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
            dependencies.add("implementation", "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

            // Kotlin Serialization
            dependencies.add("implementation", "org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

            // Timber Logging
            dependencies.add("implementation", "com.jakewharton.timber:timber:5.0.1")

            // Core Library Desugaring (for Java 24 APIs on older Android)
            dependencies.add("coreLibraryDesugaring", "com.android.tools:desugar_jdk_libs:2.1.5")

            // Universal Xposed/LSPosed API access for all library modules
            dependencies.add("compileOnly", "de.robv.android.xposed:api:82")
            dependencies.add("implementation", "com.github.kyuubiran:EzXHelper:2.2.0")
        }
    }
}
