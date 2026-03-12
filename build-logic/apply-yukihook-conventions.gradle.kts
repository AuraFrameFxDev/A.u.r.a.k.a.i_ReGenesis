// ═══════════════════════════════════════════════════════════════════════════
// YukiHook Conventions - AGP 9.0 Compatible (2025 Edition)
// ═══════════════════════════════════════════════════════════════════════════
// Uses com.android.build.api.dsl.LibraryExtension (modern DSL)
// kotlinOptions replaced with KotlinAndroidProjectExtension.compilerOptions

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

subprojects { subproject ->
    // Skip build-logic and other non-Android modules
    if (subproject.name == "build-logic" || subproject.name == "buildSrc") {
        return@subprojects
    }

    // Check if this is an Android module
    val isAndroidModule =
        subproject.plugins.hasPlugin("com.android.library") ||
                subproject.plugins.hasPlugin("com.android.application")

    if (isAndroidModule) {
        // Apply common Android and YukiHook configurations
        with(subproject) {
            pluginManager.apply("com.google.devtools.ksp")
            pluginManager.apply("org.lsposed.lsparanoid")

            extensions.configure(LibraryExtension::class.java) {
                compileSdk = 36

                defaultConfig {
                    minSdk = 33
                    targetSdk = 36

                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }
            }
        }

        if (subproject.plugins.hasPlugin("com.android.application")) {
            subproject.extensions.configure(ApplicationExtension::class.java) {
                compileSdk = 36

                        defaultConfig {
                            minSdk = 33
                            targetSdk = 36

                            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                        }

                        buildTypes {
                            release {
                                isMinifyEnabled = true
                                proguardFiles(
                                    getDefaultProguardFile("proguard-android-optimize.txt"),
                                    "proguard-rules.pro",
                                )
                            }
                        }

                        compileOptions {
                            sourceCompatibility = JavaVersion.VERSION_25
                            targetCompatibility = JavaVersion.VERSION_25
                            isCoreLibraryDesugaringEnabled = true
                        }
                    }
                }
            }

            // Configure Kotlin compiler options (AGP 9.0 replacement for kotlinOptions)
            // Only if Kotlin Android plugin is present
            pluginManager.withPlugin("org.jetbrains.kotlin.android") {
                extensions.configure(KotlinAndroidProjectExtension::class.java) {
                    compilerOptions {
                        jvmTarget.set(JvmTarget.JVM_25)
                        freeCompilerArgs.addAll(
                            "-Xjvm-default=all",
                            "-opt-in=kotlin.RequiresOptIn",
                        )
                    }
                }
            }

            // Add YukiHook dependencies
            dependencies {
                // YukiHook API stack (exact order enforced)
                implementation(libs.yukihookapi.api) {
                    exclude(group = "com.highcapable.yukihookapi", module = "ksp-xposed")
                }
                ksp(libs.yukihookapi.ksp)

                // Xposed API (compile only)
                compileOnly(libs.xposed.api)

                // Legacy Xposed API JARs (compile only)
                compileOnly(files("libs/api-82.jar"))
                compileOnly(files("libs/api-82-sources.jar"))

                // Core Android dependencies
                implementation(libs.bundles.androidx.core)

                // Kotlin coroutines
                implementation(libs.kotlinx.coroutines.android)
                implementation(libs.kotlinx.coroutines.core)

                // Testing
                testImplementation(libs.junit)
                androidTestImplementation(libs.androidx.test.ext.junit)
                androidTestImplementation(libs.androidx.test.espresso.core)
            }

            // Configure LSParanoid
            extensions.configure<org.lsposed.lsparanoid.gradle.ParanoidExtension> {
                seed = 0x2A // Consistent seed across all modules
                includeAsSharedUuid = true
            }

            // Configure KSP
            extensions.configure<com.google.devtools.ksp.gradle.KspExtension> {
                val uniquePackage = "dev.aurakai.auraframefx.generated." +
                        project.path.removePrefix(":").replace(":", ".").replace("-", "_")
                arg("yukihookapi.modulePackageName", uniquePackage)
                // Disable IDE features to prevent IntelliJ API initialization in CI
                arg("yukihookapi.configs.isEnableResourcesCache", "false")
                arg("yukihookapi.configs.isEnableDataChannel", "false")
            }
        }
    }
}
