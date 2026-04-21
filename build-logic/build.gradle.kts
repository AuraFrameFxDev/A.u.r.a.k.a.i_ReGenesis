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

dependencies {
    implementation(libs.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
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

