// ═══════════════════════════════════════════════════════════════════════════
// Feature Module - Feature implementations
// ═══════════════════════════════════════════════════════════════════════════
import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library")
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.featuremodule"
}

dependencies {
    // ═══════════════════════════════════════════════════════════════════════
    // AUTO-PROVIDED by genesis.android.library:
    // - androidx-core-ktx, appcompat, timber
    // - Hilt (android + compiler via KSP)
    // - Coroutines (core + android)
    // - Compose enabled by default
    // ═══════════════════════════════════════════════════════════════════════

    // Expose core KTX as API
    api(libs.androidx.core.ktx)

    // Compose UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)

    // Root/System Operations
    implementation(libs.libsu.core)
    implementation(libs.libsu.io)
    implementation(libs.libsu.service)

    // Xposed API (compile-only, not bundled in APK)
    compileOnly(files("$projectDir/libs/api-82.jar"))

    // YukiHook API 1.3.0+ stack
    implementation(libs.yukihookapi.api) {
        exclude(group = "com.highcapable.yukihookapi", module = "ksp-xposed")
    }
}
