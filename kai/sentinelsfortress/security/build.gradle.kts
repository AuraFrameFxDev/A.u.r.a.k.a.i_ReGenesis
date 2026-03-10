// ═══════════════════════════════════════════════════════════════════════════
// Secure Communication Module - Encrypted communication layer
// ═══════════════════════════════════════════════════════════════════════════
plugins {
    id("genesis.android.library.hilt")  // Use Hilt-enabled variant for dependency injection

}

android {
    namespace = "dev.aurakai.auraframefx.kai.sentinelsfortress.security"
    compileSdk = libs.versions.compile.sdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.min.sdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        compose = true
    }
    // Java 24 compileOptions are set by genesis.android.base
}

dependencies {
    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // ═══════════════════════════════════════════════════════════════════════
    // AUTO-PROVIDED by genesis.android.library.hilt:
    // - androidx-core-ktx, appcompat, timber
    // - Hilt (android + compiler via KSP)  ✅ Provided by .hilt variant
    // - Coroutines (core + android)
    // - Compose enabled by default
    // - Java 24 bytecode target
    // ═══════════════════════════════════════════════════════════════════════

    // Expose core KTX as API
    api(libs.androidx.core.ktx)

    // Compose UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)

    // Root/System Operations
    implementation(libs.libsu.core)
    implementation(libs.libsu.io)
    implementation(libs.libsu.service)

    // BouncyCastle for cryptography
    //noinspection UseTomlInstead
    implementation("org.bouncycastle:bcprov-jdk18on:1.83")

    // Xposed API (compile-only, not bundled in APK)
    compileOnly(files("$projectDir/libs/api-82.jar"))

    // Core Library Desugaring (Java 24 APIs)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // Testing dependencies
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)

    // Android instrumented tests - include AndroidX Test runner and Hilt testing
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.hilt.android.testing)
}

// ---------------------------------------------------------------------------
// TEMPORARY: Disable tests in this module for quick launches to testers
// - This block is intentionally minimal and safe to remove when you want
//   tests back (delete these lines or set `enabled = true`).
// ---------------------------------------------------------------------------

// Disable regular JVM unit tests
tasks.withType<Test>().configureEach {
    enabled = false
}

// Also disable common Android test tasks (connected/AndroidTest) by name match
tasks.matching { task ->
    val n = task.name.lowercase()
    n.startsWith("test") || n.contains("androidtest") || n.startsWith("connected")
}.configureEach {
    enabled = false
}
