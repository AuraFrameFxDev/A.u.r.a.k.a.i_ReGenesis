// Ensure this matches your directory structure

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

/**
 * ===================================================================
 * GENESIS LIBRARY CONVENTION PLUGIN (2025 EDITION)
 * ===================================================================
 * Configured for AGP 9.0+ and Kotlin 2.x "Built-in" Support.
 */
class GenesisLibraryHiltPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            // 1. Apply Essential Plugins
            // Note: 'org.jetbrains.kotlin.android' is REMOVED as it's now built-in to AGP 9.0
            pluginManager.apply("com.android.library")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")
            pluginManager.apply("com.google.devtools.ksp")
            pluginManager.apply("com.google.dagger.hilt.android")

            // 2. Configure Android Library Extension using the NEW DSL interface
            extensions.configure(LibraryExtension::class.java) {
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
                }
            }

            // 3. Replace deprecated 'kotlinOptions' with new Kotlin DSL
            extensions.configure(KotlinAndroidProjectExtension::class.java) {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_25)
                    // Enable K2 features for 2025 performance
                    freeCompilerArgs.add("-Xjdk-release=25")
                }
            }

            // 4. YukiHook KSP Configuration
            extensions.configure(com.google.devtools.ksp.gradle.KspExtension::class.java) {
                // Generate a unique package name per module based on its full Gradle path
                val uniquePackage = "dev.aurakai.auraframefx.generated." +
                        project.path.removePrefix(":").replace(":", ".").replace("-", "_")
                arg("yukihookapi.modulePackageName", uniquePackage)
            }

            // 5. Dependencies
            dependencies.apply {
                // Hilt
                add("implementation", "com.google.dagger:hilt-android:2.57.2")
                add("ksp", "com.google.dagger:hilt-android-compiler:2.57.2")

                // Compose (Using Version Catalog references is better, but hardcoded here for logic)
                add("api", platform("androidx.compose:compose-bom:2025.12.01"))
                add("api", "androidx.compose.runtime:runtime")
                add("api", "androidx.compose.ui:ui")
                add("api", "androidx.compose.material3:material3")

                // YukiHook & Xposed
                val yukiDep = add("implementation", "com.highcapable.yukihookapi:api:1.3.1")
                (yukiDep as? org.gradle.api.artifacts.ExternalModuleDependency)?.exclude(
                    mapOf("group" to "com.highcapable.yukihookapi", "module" to "ksp-xposed")
                )
                add("ksp", "com.highcapable.yukihookapi:ksp-xposed:1.3.1")
                add("compileOnly", "de.robv.android.xposed:api:82")

                // Core
                add("implementation", "androidx.core:core-ktx:1.17.0")
                add("coreLibraryDesugaring", "com.android.tools:desugar_jdk_libs:2.1.5")
                add("implementation", "com.jakewharton.timber:timber:5.0.1")
            }
        }
    }
}
