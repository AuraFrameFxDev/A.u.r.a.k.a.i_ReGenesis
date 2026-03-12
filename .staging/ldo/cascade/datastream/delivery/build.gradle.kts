// ═══════════════════════════════════════════════════════════════════════════
// Data Delivery Module - Data delivery and synchronization
// ═══════════════════════════════════════════════════════════════════════════
import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library.hilt")  // Provides: Android, Kotlin, Compose, KSP, Hilt
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.cascade.datastream.delivery"
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

    // Module dependencies
    implementation(project(":cascade:datastream:routing"))

    // Networking
    implementation(libs.okhttp)
    implementation(libs.retrofit)
}
