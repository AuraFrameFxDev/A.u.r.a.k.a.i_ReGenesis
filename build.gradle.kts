// Root build.gradle.kts
// ═══════════════════════════════════════════════════════════════════════════
// A.u.r.a.K.a.I Reactive Intelligence - Root Build Configuration
// ═══════════════════════════════════════════════════════════════════════════

import org.gradle.api.tasks.compile.JavaCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Apply plugin version management to all projects
plugins {
    // Base Kotlin plugins with versions (matching libs.versions.toml)
    // CRITICAL: Use kotlin("android") notation for AGP 9.0 compatibility
    kotlin("android") version "2.3.0-RC2" apply false
    kotlin("plugin.compose") version "2.3.0-RC2" apply false
    kotlin("plugin.serialization") version "2.3.0-RC2" apply false
    kotlin("plugin.parcelize") version "2.3.0-RC2" apply false

    // Android plugins
    id("com.android.application") version "9.0.0-beta05" apply false
    id("com.android.library") version "9.0.0-beta05" apply false

    // Other plugins
    id("com.google.dagger.hilt.android") version "2.57.2" apply false
    id("com.google.devtools.ksp") version "2.3.3" apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
    id("com.google.firebase.crashlytics") version "3.0.6" apply false
}

// Clean task for the root project
tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

// Configure all projects
allprojects {
    // Common configurations can go here
    group = "dev.aurakai.auraframefx"
    version = "0.1.0"


    // CRITICAL: Enforce JVM 24 target consistency across ALL subprojects
    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = "24"
        targetCompatibility = "24"
    }

    tasks.withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_24)
        }
    }

    // Make tests configurable via project property
    // Usage: ./gradlew test -PenableTests=false (to disable)
    // Or add 'enableTests=false' to gradle.properties to disable by default
    val enableTests = project.findProperty("enableTests")?.toString()?.toBoolean() ?: true

    if (!enableTests) {
        tasks.withType<AbstractTestTask> {
            enabled = false
        }

        tasks.matching { task ->
            task.name.startsWith("test") ||
            task.name.endsWith("Test") ||
            task.name.contains("androidTest")
        }.configureEach {
            enabled = false
        }
    }
}
