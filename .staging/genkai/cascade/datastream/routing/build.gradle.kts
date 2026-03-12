// ═══════════════════════════════════════════════════════════════════════════
// Data Routing Module - Data flow routing and management
// ═══════════════════════════════════════════════════════════════════════════
plugins {
    id("genesis.android.library.hilt")  // Provides: Android, Kotlin, Compose, KSP, Hilt
}

android {
    namespace = "dev.aurakai.auraframefx.cascade.datastream.routing"
}

dependencies {
    // ═══════════════════════════════════════════════════════════════════════
    // AUTO-PROVIDED by genesis.android.library.hilt:
    // ✅ Hilt (android + compiler via KSP)
    // ✅ androidx-core-ktx, appcompat, timber
    // ✅ Coroutines (core + android)
    // ✅ Serialization JSON
    // ✅ Compose enabled
    // ✅ Core library desugaring (Java 24 APIs)
    // ✅ Xposed API (compileOnly) + EzXHelper
    // ═══════════════════════════════════════════════════════════════════════

    // Module-specific Compose dependencies
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
}
