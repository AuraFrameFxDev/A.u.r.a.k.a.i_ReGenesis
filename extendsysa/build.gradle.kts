// ═══════════════════════════════════════════════════════════════════════════
// Extend System A Module - System extension module A
// AGP 9.0 Compatible - Uses com.android.build.api.dsl.LibraryExtension
// ═══════════════════════════════════════════════════════════════════════════
import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library")
    id("com.google.devtools.ksp")
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.extendsysa"
}


dependencies {
    // ═══════════════════════════════════════════════════════════════════════
    // AUTO-PROVIDED by genesis.android.library:
    // - androidx-core-ktx, appcompat, timber
    // - Hilt (android + compiler via KSP)
    // - Coroutines (core + android)
    // - Compose enabled by default
    // ═══════════════════════════════════════════════════════════════════════

    // Internal module dependencies
    implementation(project(":core-module"))
    // implementation(project(":app")) // Circular dependency! Moved necessary components to :core-module

    // Hilt - Explicit dependencies needed if not inherited correctly
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Expose core KTX as API
    api(libs.androidx.core.ktx)

    // Compose UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
}
