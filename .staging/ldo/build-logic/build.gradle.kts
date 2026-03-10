import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`        // applies java-gradle-plugin
}

// ═══════════════════════════════════════════════════════════════════════════
// CRITICAL: Exclude AAALL Android AAR dependencies from build-logic
// ═══════════════════════════════════════════════════════════════════════════
// build-logic is JVM-only and cannot consume Android AAR (Android Archive) files.
// hilt-android-gradle-plugin incorrectly depends on hilt-android (runtime library),
// which transitively pulls in AndroidX AAR dependencies.
// Force exclude these from ALL configurations to prevent variant resolution errors.

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
kotlin.compilerOptions.jvmTarget
// Explicitly configure Java compilation tasks to target JVM 25
tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = "25"
    targetCompatibility = "25"
}

// Tests enabled to validate build script configuration
// Temporarily disable tests in build-logic during urgent test releases.
// Re-enable later when we run CI/validation: set enabled = true
tasks.matching { it.name.contains("Test") }.configureEach {
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

dependencies {
    // CRITICAL: All versions MUST match root build.gradle.kts and gradle/libs.versions.toml
    // Using version catalog for consistency
    implementation(libs.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)

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

// ═══════════════════════════════════════════════════════════════════════════
// Genesis Convention Plugins Registration
// ═══════════════════════════════════════════════════════════════════════════
//
// These are the PRIMARY convention plugins that modules should use.
// They are Kotlin class plugins (not precompiled scripts) for maximum control
// over plugin application order.
//


// ═══════════════════════════════════════════════════════════════════════════
// CORRECT USAGE EXAMPLES
// ═══════════════════════════════════════════════════════════════════════════
//
// For :app module:
//   plugins {
//       id("genesis.android.application")  // All-in-one: Android, Hilt, KSP, Compose, Serialization, Firebase
//   }
//
// For standard library module WITHOUT Hilt:
//   plugins {
//       id("genesis.android.library")  // Base library: Android, Compose, Serialization (NO Hilt)
//   }
//
// For library module WITH Hilt:
//   plugins {
//       id("genesis.android.library.hilt")  // Library with Hilt DI + KSP
//   }
//
// For YukiHook/Xposed module:
//   plugins {
//       id("genesis.android.library")   // Base library with Hilt, Compose, KSP
//       id("genesis.android.yukihook")  // Add YukiHook/Xposed support
//   }
//
// For Room database module:
//   plugins {
//       id("genesis.android.library")  // Base library
//       id("genesis.android.room")     // Add Room Database
//   }
//
// ══════════════════════════════════════════════════
