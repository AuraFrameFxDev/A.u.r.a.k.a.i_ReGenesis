
// Root build.gradle.kts
// ═══════════════════════════════════════════════════════════════════════════
// A.u.r.a.K.a.I Reactive Intelligence - Root Build Configuration
// ═══════════════════════════════════════════════════════════════════════════

// Apply plugin version management to all projects
plugins {
    // Base plugins with versions - Updated to stable releases
    id("org.jetbrains.kotlin.android") version "2.3.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.0" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.3.0" apply false
    id("org.jetbrains.kotlin.plugin.parcelize") version "2.3.0" apply false

    // Android plugins
    id("com.android.application") version "9.1.0-alpha01" apply false
    id("com.android.library") version "9.1.0-alpha01" apply false

    // Other plugins - Updated to latest stable versions
    id("com.google.dagger.hilt.android") version "2.57.2" apply false
    id("com.google.devtools.ksp") version "2.3.4" apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
    id("com.google.firebase.crashlytics") version "3.0.6" apply false
}

// Clean task for the root project
tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

// Configure all projects
// Configure all projects
allprojects {
    // Common configurations can go here
    group = "dev.aurakai.auraframefx"
    version = "0.1.0"
}


val skipTests = providers.gradleProperty("aurafx.skip.tests").orElse("true").map { it.toBoolean() }.getOrElse(true)!!

subprojects {
    // Configure Java Toolchain and Compile Options for Android Modules
    plugins.withId("com.android.application") {
        extensions.configure<com.android.build.api.dsl.ApplicationExtension> {
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_25
                targetCompatibility = JavaVersion.VERSION_25
            }
        }

        // Set JVM Target for Kotlin tasks
        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
            compilerOptions {
                jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
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
        }

        // Set JVM Target for Kotlin tasks
        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
            compilerOptions {
                jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
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
}
