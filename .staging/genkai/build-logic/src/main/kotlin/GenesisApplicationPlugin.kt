import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * ===================================================================
 * GENESIS APPLICATION CONVENTION PLUGIN
 * ===================================================================
 *
 * The primary convention plugin for Android application modules.
 *
 * This plugin configures:
 * - Android application plugin and extensions
 * - Kotlin Android support with Compose compiler
 * - Hilt dependency injection
 * - KSP annotation processing
 * - Jetpack Compose (built-in compiler with Kotlin 2.0+)
 * - Google Services (Firebase)
 * - Java 25 bytecode target (Firebase + AGP 9.0 compatible)
 * - Consistent build configuration across app modules
 *
 * Plugin Application Order:
 * 1. org.jetbrains.kotlin.android (Required for Hilt even with built-in Kotlin)
 * 2. com.android.application
 * 3. org.jetbrains.kotlin.plugin.compose
 * 4. com.google.dagger.hilt.android
 * 5. com.google.devtools.ksp
 * 6. org.jetbrains.kotlin.plugin.serialization
 * 7. com.google.gms.google-services
 *
 * Note: AGP 9.0+ has built-in Kotlin, but Hilt requires explicit plugin application.
 * This hybrid approach provides maximum compatibility.
 *
 * @since Genesis Protocol 2.0 (AGP 9.0.0-alpha14 Compatible)
 */
class GenesisApplicationPlugin : Plugin<Project> {
    /**
     * Configure a Gradle Project as an Android application module using Genesis conventions.
     *
     * Applies required plugins; configures the Android ApplicationExtension (compile and target SDKs,
     * NDK, defaultConfig, build types, Java compatibility, Compose and other build features, packaging,
     * lint, and optional CMake external native build); adjusts Kotlin compiler options; and adds the
     * standard set of dependencies used by Genesis application modules.
     *
     * @param project The Gradle Project to configure as an Android application module.
     */
    override fun apply(project: Project) {
        with(project) {
            // Apply core plugins. Android must be first.
            pluginManager.apply("com.android.application")
            pluginManager.apply("org.jetbrains.kotlin.android")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

            // Apply dependent plugins only after the Android plugin has been configured.
            // This is the key to fixing the "Android BaseExtension not found" error.
            plugins.withId("com.android.application") {
                pluginManager.apply("com.google.dagger.hilt.android")
                pluginManager.apply("com.google.devtools.ksp")
                pluginManager.apply("com.google.gms.google-services")
            }

            extensions.configure<ApplicationExtension> {
                compileSdk = 36
                ndkVersion = "29.0.14206865"

                defaultConfig {
                    applicationId = "dev.aurakai.auraframefx"
                    minSdk = 34
                    targetSdk = 36
                    versionCode = 1
                    versionName = "1.0"

                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    vectorDrawables {
                        useSupportLibrary = true
                    }

                    ndk {
                        abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
                    }
                }

                buildTypes {
                    release {
                        isMinifyEnabled = true
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }
                }

                // Java 24 bytecode (Firebase + AGP 9.0 compatible)
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_24
                    targetCompatibility = JavaVersion.VERSION_24

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

                // NDK/CMake configuration (if present)
                val cmakeFile = file("src/main/cpp/CMakeLists.txt")
                if (cmakeFile.exists()) {
                    externalNativeBuild {
                        cmake {
                            path = cmakeFile
                            version = "3.22.1"
                        }
                    }
                }
            }

            // Configure Kotlin JVM toolchain and compilation options
            GenesisJvmConfig.configureKotlinJvm(project)

            // ═══════════════════════════════════════════════════════════════════════════
            // Auto-configured dependencies (provided by convention plugin)
            // ═══════════════════════════════════════════════════════════════════════════

            // Hilt Dependency Injection
            dependencies.add("implementation", "com.google.dagger:hilt-android:2.57.2")
            dependencies.add("ksp", "com.google.dagger:hilt-android-compiler:2.57.2")

            // Compose BOM (Bill of Materials)
            dependencies.add("implementation", dependencies.platform("androidx.compose:compose-bom:2024.11.00"))
            dependencies.add("implementation", "androidx.compose.ui:ui")
            dependencies.add("implementation", "androidx.compose.ui:ui-graphics")
            dependencies.add("implementation", "androidx.compose.ui:ui-tooling-preview")
            dependencies.add("implementation", "androidx.compose.material3:material3")
            dependencies.add("debugImplementation", "androidx.compose.ui:ui-tooling")

            // Core Android libraries
            dependencies.add("implementation", "androidx.core:core-ktx:1.17.0")
            dependencies.add("implementation", "androidx.appcompat:appcompat:1.7.1")
            dependencies.add("implementation", "androidx.activity:activity-compose:1.11.0")
            dependencies.add("implementation", "androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
            dependencies.add("implementation", "androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")

            // Kotlin Coroutines
            dependencies.add("implementation", "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
            dependencies.add("implementation", "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

            // Kotlin Serialization
            dependencies.add("implementation", "org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

            // Timber Logging
            dependencies.add("implementation", "com.jakewharton.timber:timber:5.0.1")

            // Core Library Desugaring (for Java 24 APIs on older Android)
            dependencies.add("coreLibraryDesugaring", "com.android.tools:desugar_jdk_libs:2.1.5")

            // Firebase BOM (Bill of Materials)
            dependencies.add("implementation", dependencies.platform("com.google.firebase:firebase-bom:34.5.0"))

            // Universal Xposed/LSPosed API access
            dependencies.add("compileOnly", "de.robv.android.xposed:api:82")
            // Note: io.github.libxposed is not yet published to Maven Central
            // Use de.robv.android.xposed:api:82 for Xposed module development
            dependencies.add("implementation", "com.github.kyuubiran:EzXHelper:2.2.0")
        }
    }
}
