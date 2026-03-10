// ═══════════════════════════════════════════════════════════════════════════
// DataVein Oracle Native Module - Native Oracle cloud data access
// ═══════════════════════════════════════════════════════════════════════════
plugins {
    id("genesis.android.library.hilt")  // Use Hilt-enabled variant for dependency injection

}

android {
    namespace = "dev.aurakai.auraframefx.genesis.oracledrive.datavein"
    compileSdk = libs.versions.compile.sdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.min.sdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        compose = true
    }


}



dependencies {
    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // YukiHook API 1.3.0+ stack
    implementation(libs.yukihookapi.api)
    ksp(libs.yukihookapi.ksp)

    // ═══════════════════════════════════════════════════════════════════════
    // AUTO-PROVIDED by genesis.android.library:
    // - androidx-core-ktx, appcompat, timber
    // - Coroutines (core + android)
    // - Compose enabled by default
    // ═══════════════════════════════════════════════════════════════════════

    // Expose core KTX as API
    api(libs.androidx.core.ktx)

    // Compose UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.runtime)
    implementation(libs.compose.material3)

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
    arg("yukihookapi.modulePackageName", "dev.aurakai.auraframefx.genesis.oracledrive.datavein")
}
