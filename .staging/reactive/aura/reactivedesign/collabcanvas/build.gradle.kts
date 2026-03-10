// ═══════════════════════════════════════════════════════════════════════════
// Collaborative Canvas Module - Real-time collaborative drawing/whiteboard
// ═══════════════════════════════════════════════════════════════════════════
plugins {
    id("genesis.android.library.hilt")  // Use Hilt-enabled variant for dependency injection

}

android {
    namespace = "dev.aurakai.auraframefx.aura.reactivedesign.collabcanvas"
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
        buildConfig = true
    }
    // Java 24 compileOptions and Compose are set by genesis.android.base
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
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)

    // Networking
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation("com.google.code.gson:gson:2.13.2")

    // Root/System Operations
    implementation(libs.libsu.core)
    implementation(libs.libsu.io)
    implementation(libs.libsu.service)

    // Xposed API (compile-only, not bundled in APK)
    compileOnly(files("$projectDir/libs/api-82.jar"))

    // Core Library Desugaring (Java 24 APIs)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}

ksp {
    arg("yukihookapi.modulePackageName", "dev.aurakai.auraframefx.aura.reactivedesign.collabcanvas")
}
