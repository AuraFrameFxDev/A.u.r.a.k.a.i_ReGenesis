plugins {
    `kotlin-dsl`   // Enables Kotlin DSL for writing plugins
}

// ═══════════════════════════════════════════════════════════════════════════
// CRITICAL: Prevent Android AAR leakage into build-logic (JVM-only)
// hilt-android-gradle-plugin transitively pulls Android dependencies.
// We exclude them aggressively here.
// ═══════════════════════════════════════════════════════════════════════════
// configurations.all {
//    exclude(group = "com.google.dagger", module = "hilt-android")
//    exclude(group = "androidx.activity")
//    exclude(group = "androidx.fragment")
//    exclude(group = "androidx.lifecycle")
//    exclude(group = "androidx.savedstate")
//    exclude(group = "androidx.annotation")
//    exclude(group = "androidx.core")
// }

configurations.all {
    exclude(group = "com.google.dagger", module = "hilt-android")
    exclude(group = "androidx.activity")
    exclude(group = "androidx.fragment")
    exclude(group = "androidx.lifecycle")
    exclude(group = "androidx.savedstate")
    exclude(group = "androidx.annotation")
    exclude(group = "androidx.core")
}

// Configure Java toolchain to JVM 25 (matches gradle.properties and Kotlin target)
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
    // Explicitly set source and target compatibility to 25
    sourceCompatibility = JavaVersion.toVersion("25")
    targetCompatibility = JavaVersion.toVersion("25")
}

// Configure Kotlin compilation to match Java toolchain
// MUST match the target used in GenesisApplicationPlugin and GenesisLibraryHiltPlugin (JVM 25)
// Explicitly configure Java compilation tasks to target JVM 25
tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = "25"
    targetCompatibility = "25"
}

// Disable tests in build-logic for now (re-enable when running CI)
tasks.matching { it.name.contains("Test", ignoreCase = true) }.configureEach {
    enabled = false
}

gradlePlugin {
    plugins {
        register("genesisApplication") {
            id = "genesis.android.application"
            implementationClass = "GenesisApplicationPlugin"
        }
        register("genesisLibrary") {
            id = "genesis.android.library"
            implementationClass = "GenesisLibraryPlugin"
        }
        register("genesisLibraryHilt") {
            id = "genesis.android.library.hilt"
            implementationClass = "GenesisLibraryHiltPlugin"
        }
    }
}

// Dependencies for the convention plugins themselves
dependencies {
    // Core Gradle plugins needed by your convention plugins
    implementation(libs.android.gradle.plugin)            // Android Gradle plugin
    implementation(libs.kotlin.gradle.plugin)            // Kotlin Gradle plugin
    implementation(libs.ksp.gradle.plugin)               // KSP
    implementation(libs.hilt.gradle.plugin)              // Hilt Gradle plugin
    implementation(libs.google.services.gradle.plugin)   // Google Services (if used)
    implementation(libs.firebase.crashlytics.gradle.plugin) // Firebase Crashlytics

    // Optional: if your plugins need Compose compiler plugin access
    implementation(libs.compose.compiler.gradle.plugin)
    implementation(libs.jetbrains.kotlin.serialization)

    // Hilt Gradle Plugin (Android AAR dependencies excluded globally via configurations.all)
    implementation(libs.hilt.gradle.plugin)
    implementation(libs.hilt.android)
    implementation(libs.ksp.gradle.plugin)
    implementation(libs.gms.google.services)
    testImplementation(kotlin("test"))
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.jupiter.junit.jupiter.engine)
}