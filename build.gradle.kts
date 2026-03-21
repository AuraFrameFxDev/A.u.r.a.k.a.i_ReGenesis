
// Root build.gradle.kts
// ═══════════════════════════════════════════════════════════════════════════
// A.u.r.a.K.a.I Reactive Intelligence - Root Build Configuration
// ═══════════════════════════════════════════════════════════════════════════

// Apply plugin version management to all projects
plugins {
    // Base plugins with versions - Updated to stable releases
    id("org.jetbrains.kotlin.android") version "2.3.20" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.20" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.3.20" apply false
    id("org.jetbrains.kotlin.plugin.parcelize") version "2.3.20" apply false

    // Android plugins
    id("com.android.application") version "9.2.0-alpha05" apply false
    id("com.android.library") version "9.2.0-alpha05" apply false

    // Other plugins - Updated to latest stable versions
    id("com.google.dagger.hilt.android") version "2.59.2" apply false
    id("com.google.devtools.ksp") version "2.3.5" apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
    id("com.google.firebase.crashlytics") version "3.0.6" apply false
}

val skipTests =
    providers.gradleProperty("aurafx.skip.tests").orElse("false").map { it.toBoolean() }
        .getOrElse(false)

subprojects {
    // ═══════════════════════════════════════════════════════════════════════════
    // CRITICAL: Global YukiHook KSP Exclusion
    // ═══════════════════════════════════════════════════════════════════════════
    configurations.all {
        if (!name.lowercase().contains("ksp") && !name.contains("lint", ignoreCase = true)) {
            exclude(group = "com.highcapable.yukihookapi", module = "ksp-xposed")
        }
    }

    // Configure Java Toolchain and Compile Options for Android Modules
    plugins.withId("com.android.application") {
        extensions.configure<com.android.build.api.dsl.ApplicationExtension> {
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_25
                targetCompatibility = JavaVersion.VERSION_25
            }

            packaging {
                resources {
                    pickFirsts += "**/YukiHookAPIProperties.class"
                }
            }
        }

        if (skipTests) {
             extensions.configure<com.android.build.api.variant.AndroidComponentsExtension<*, *, *>>("androidComponents") {
                 beforeVariants { builder ->
                     (builder as? com.android.build.api.variant.HasUnitTestBuilder)?.enableUnitTest = false
                     (builder as? com.android.build.api.variant.HasAndroidTestBuilder)?.enableAndroidTest = false
                 }
             }
        }
    }

    plugins.withId("com.android.library") {
        extensions.configure<com.android.build.api.dsl.LibraryExtension> {
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_25
                targetCompatibility = JavaVersion.VERSION_25
            }

            packaging {
                resources {
                    pickFirsts += "**/YukiHookAPIProperties.class"
                }
            }
        }

        if (skipTests) {
            extensions.configure<com.android.build.api.variant.AndroidComponentsExtension<*, *, *>>("androidComponents") {
                beforeVariants { builder ->
                    (builder as? com.android.build.api.variant.HasUnitTestBuilder)?.enableUnitTest = false
                    (builder as? com.android.build.api.variant.HasAndroidTestBuilder)?.enableAndroidTest = false
                }
            }
        }
    }

    // Set JVM Target for ALL Kotlin tasks in ALL subprojects
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
        }
    }
}
