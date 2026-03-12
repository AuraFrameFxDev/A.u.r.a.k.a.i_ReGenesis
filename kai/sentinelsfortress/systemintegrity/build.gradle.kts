// ═══════════════════════════════════════════════════════════════════════════
// System Integrity Module - System health and integrity monitoring
// ═══════════════════════════════════════════════════════════════════════════
import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library.hilt")  // Provides: Android, Kotlin, Compose, KSP, Hilt
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.kai.sentinelsfortress.systemintegrity"
}

dependencies {
    // ═══════════════════════════════════════════════════════════════════════
    // AUTO-PROVIDED by genesis.android.library.hilt:
    // ✅ Hilt (android + compiler via KSP)
    // ✅ androidx-core-ktx, appcompat, timber
    // ✅ Coroutines (core + android)
    // ✅ Serialization JSON
    // ✅ Compose enabled
    // ✅ Core library desugaring (Java 25 APIs)
    // ✅ Xposed API (compileOnly) + EzXHelper
    // ═══════════════════════════════════════════════════════════════════════

    // Root/System access
    implementation(libs.libsu.core)
    implementation(libs.libsu.nio)

    // Compose UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
}
