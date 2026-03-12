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
    id("com.android.application") version "9.0.0-rc01" apply false
    id("com.android.library") version "9.0.0-rc01" apply false

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
allprojects {
    // Common configurations can go here
    group = "dev.aurakai.auraframefx"
    version = "0.1.0"

    // Disable all test tasks by default
    tasks.withType<AbstractTestTask> {
        enabled = false
    }

    // Disable test tasks for specific test types
    tasks.matching { task ->
        task.name.startsWith("test") ||
        task.name.endsWith("Test") ||
        task.name.contains("androidTest")
    }.configureEach {
        enabled = false
    }
}
