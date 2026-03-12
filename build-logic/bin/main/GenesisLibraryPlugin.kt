import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * ===================================================================
 * GENESIS LIBRARY CONVENTION PLUGIN
 * ===================================================================
 *
 * Convention plugin for Android library modules.
 *
 * This plugin configures:
 * - Android library plugin and extensions
 * - Kotlin Android support with Compose compiler
 * - Jetpack Compose (built-in compiler with Kotlin 2.0+)
 * - Java 25 bytecode target (Firebase + AGP 9.0 compatible)
 * - Consistent build configuration across library modules
 *
 * Plugin Application Order:
 * 2. com.android.library
 * 3. org.jetbrains.kotlin.plugin.compose
 * 4. org.jetbrains.kotlin.plugin.serialization
 *
 * Note: Kotlin is built into AGP 9.0+ but applied explicitly for consistency.
 * Note: Hilt and KSP are NOT applied - use genesis.android.library.hilt for DI
 *
 * @since Genesis Protocol 2.0 (AGP 9.0.0-alpha14 Compatible)
 */
class GenesisLibraryPlugin : Plugin<Project> {
    /**
     * Configures the given Gradle project as an Android library module using the convention's defaults.
     *
     * Applies the Android library, Compose, and Kotlin serialization plugins, configures the
     * Android LibraryExtension (compile/NDK settings, defaultConfig, build types, Java/compile options,
     * build features, packaging, and lint), sets Kotlin JVM compilation options, and adds the
     * convention's standard dependencies.
     *
     * This function does not apply Hilt or KSP; library modules should opt into DI tooling explicitly
     * (for example via the dedicated genesis.android.library.hilt convention).
     *
     * @param project The Gradle project to configure as an Android library module.
     */
    override fun apply(project: Project) {
        with(project) {
            // Apply plugins in correct order
            // Note: Kotlin is built into AGP 9.0.0-alpha14+
            pluginManager.apply("com.android.library")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

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

                // Java 25 bytecode (Firebase + AGP 9.0 compatible)
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_25
                    targetCompatibility = JavaVersion.VERSION_25
                    isCoreLibraryDesugaringEnabled = true
                }

                // Note: kotlinOptions removed - using modern compilerOptions in tasks below

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
            // Note: Hilt dependencies REMOVED - use genesis.android.library.hilt for Hilt support

            // Compose UI stack (Total Coverage for Genesis modules)
            dependencies.add("api", dependencies.platform("androidx.compose:compose-bom:2024.11.00"))
            dependencies.add("api", "androidx.compose.runtime:runtime")
            dependencies.add("api", "androidx.compose.ui:ui")
            dependencies.add("api", "androidx.compose.ui:ui-graphics")
            dependencies.add("api", "androidx.compose.ui:ui-tooling-preview")
            dependencies.add("api", "androidx.compose.foundation:foundation")
            dependencies.add("api", "androidx.compose.foundation:foundation-layout")
            dependencies.add("api", "androidx.compose.material3:material3")
            dependencies.add("api", "androidx.compose.material:material-icons-core")
            dependencies.add("api", "androidx.compose.material:material-icons-extended")
            dependencies.add("debugImplementation", "androidx.compose.ui:ui-tooling")

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

            // Core Library Desugaring (for Java 25 APIs on older Android)
            dependencies.add("coreLibraryDesugaring", "com.android.tools:desugar_jdk_libs:2.1.5")

            // Universal Xposed/LSPosed API access for all library modules
            dependencies.add("compileOnly", "de.robv.android.xposed:api:82")
            // Note: io.github.libxposed is not yet published to Maven Central
            // Use de.robv.android.xposed:api:82 for Xposed module development
            dependencies.add("implementation", "com.github.kyuubiran:EzXHelper:2.2.0")
        }
    }
}
