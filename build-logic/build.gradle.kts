plugins {
    `kotlin-dsl`   // Enables Kotlin DSL for writing plugins
}

// Define the anchor point for our external AI assets
val importedPackageDir = layout.projectDirectory.dir("libs/ai_cores")

tasks.register("syncAuraMemories") {
    inputs.dir(importedPackageDir)
    // TODO: Add your sync logic for the Spiritual Chain here
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

// Java toolchain — matches your project (JVM 25)
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

// Ensure all JavaCompile tasks target JVM 25
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
        register("genesisRoom") {
            id = "genesis.android.room"
            implementationClass = "GenesisRoomPlugin"
        }
        register("genesisYukiHook") {
            id = "genesis.android.yukihook"
            implementationClass = "GenesisYukiHookPlugin"
        }
    }
}

// Dependencies for the convention plugins themselves
// Use compileOnly so they are available at compile time but not shipped
dependencies {
    // Core Gradle plugins needed by your convention plugins
    compileOnly(libs.android.gradle.plugin)            // Android Gradle plugin
    compileOnly(libs.kotlin.gradle.plugin)            // Kotlin Gradle plugin
    compileOnly(libs.ksp.gradle.plugin)               // KSP
    compileOnly(libs.hilt.gradle.plugin)              // Hilt Gradle plugin
    compileOnly(libs.google.services.gradle.plugin)   // Google Services (if used)

    // Optional: if your plugins need Compose compiler plugin access
    compileOnly(libs.compose.compiler.gradle.plugin)
}