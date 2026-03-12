// ═══════════════════════════════════════════════════════════════════════════
// Extend System F Module - System extension module F
// AGP 9.0 Compatible - Uses com.android.build.api.dsl.LibraryExtension
// ═══════════════════════════════════════════════════════════════════════════
import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library")
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.extendsysf"
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

    // YukiHook API 1.3.0+ stack
    implementation(libs.yukihookapi.api) {
        exclude(group = "com.highcapable.yukihookapi", module = "ksp-xposed")
    }

    // Compose UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
}
