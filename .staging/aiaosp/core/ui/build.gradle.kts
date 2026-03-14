// ═══════════════════════════════════════════════════════════════════════════
// Core UI Module - Shared UI components and Compose utilities
// ═══════════════════════════════════════════════════════════════════════════
import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library")

}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.core.ui"
}

dependencies {
    // ═══════════════════════════════════════════════════════════════════════
    // AUTO-PROVIDED by genesis.android.library.hilt:
    // - androidx-core-ktx, appcompat
    // - Hilt (android + compiler viaversion KSP)
    // - Timber, Coroutines
    // - Compose enabled by default
    // ═══════════════════════════════════════════════════════════════════════

    // Expose core KTX as API (types leak to consumers)
    api(libs.androidx.core.ktx)

    // Compose UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.androidx.compose.material3)

    // Xposed API (compile-only, not bundled in APK)
    compileOnly(files("$projectDir/libs/api-82.jar"))

    // YukiHookAPI provided by genesis.android.library.hilt convention
}

