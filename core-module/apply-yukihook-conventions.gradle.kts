//// ═══════════════════════════════════════════════════════════════════════════
//// YukiHook Conventions - AGP 9.0 Compatible (2025 Edition)
//// ═══════════════════════════════════════════════════════════════════════════
//// Uses com.android.build.api.dsl.LibraryExtension (modern DSL)
//// kotlinOptions replaced with KotlinAndroidProjectExtension.compilerOptions
//
//import com.android.build.api.dsl.LibraryExtension
//import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
//import org.jetbrains.kotlin.gradle.dsl.JvmTarget
//
//subprojects { subproject ->
//    // Skip build-logic and other non-Android modules
//    if (subproject.name == "build-logic" || subproject.name == "buildSrc") {
//        return@subprojects
//    }
//
//    // Check if this is an Android module
//    val isAndroidModule =
//        subproject.plugins.hasPlugin("com.android.library") ||
//            subproject.plugins.hasPlugin("com.android.application")
//
//    if (isAndroidModule) {
//        // Apply common Android and YukiHook configurations
//        with(subproject) {
//            // Apply common plugins if not already applied
//            if (!pluginManager.hasPlugin("com.android.library") && !pluginManager.hasPlugin("com.android.application")) {
//                pluginManager.apply("com.android.library")
//            }
//            pluginManager.apply("com.google.devtools.ksp")
//            pluginManager.apply("org.lsposed.lsparanoid")
//
//            // Configure Android settings using AGP 9.0 Public DSL
//            extensions.configure(LibraryExtension::class.java) {
//                compileSdk = 36
//
//                defaultConfig {
//                    minSdk = 33
//                    targetSdk = 36
//
//                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//                    consumerProguardFiles("consumer-rules.pro")
//                }
//
//                buildTypes {
//                    release {
//                        isMinifyEnabled = true
//                        proguardFiles(
//                            getDefaultProguardFile("proguard-android-optimize.txt"),
//                            "proguard-rules.pro",
//                        )
//                    }
//                }
//
//                compileOptions {
//                    sourceCompatibility = JavaVersion.VERSION_25
//                    targetCompatibility = JavaVersion.VERSION_25
//                    isCoreLibraryDesugaringEnabled = true
//                }
//            }
//
//            // Configure Kotlin compiler options (AGP 9.0 replacement for kotlinOptions)
//            extensions.configure(KotlinAndroidProjectExtension::class.java) {
//                compilerOptions {
//                    jvmTarget.set(JvmTarget.JVM_25)
//                    freeCompilerArgs.addAll(
//                        "-Xjvm-default=all",
//                        "-opt-in=kotlin.RequiresOptIn",
//                    )
//                }
//            }
//
//            // Add YukiHook dependencies
//            dependencies {
//                // YukiHook API stack (exact order enforced)
//                implementation(libs.yukihookapi.api)
//                ksp(libs.yukihookapi.ksp)
//
//                // Xposed API (compile only)
//                compileOnly(libs.xposed.api)
//
//                // Legacy Xposed API JARs (compile only)
//                compileOnly(files("libs/api-82.jar"))
//                compileOnly(files("libs/api-82-sources.jar"))
//
//                // Core Android dependencies
//                implementation(libs.bundles.androidx.core)
//
//                // Kotlin coroutines
//                implementation(libs.kotlinx.coroutines.android)
//                implementation(libs.kotlinx.coroutines.core)
//
//                // Testing
//                testImplementation(libs.junit)
//                androidTestImplementation(libs.androidx.test.ext.junit)
//                androidTestImplementation(libs.androidx.test.espresso.core)
//            }
//
//            // Configure LSParanoid
//            extensions.configure<org.lsposed.lsparanoid.gradle.ParanoidExtension> {
//                seed = 0x2A // Consistent seed across all modules
//                includeAsSharedUuid = true
//            }
//
//            // Configure KSP
//            extensions.configure<com.google.devtools.ksp.gradle.KspExtension> {
//                arg("YUKIHOOK_PACKAGE_NAME", project.group.toString())
//            }
//        }
//    }
//}
