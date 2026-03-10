// ═══════════════════════════════════════════════════════════════════════════
// Task Manager Module - Background task scheduling and execution
// ═══════════════════════════════════════════════════════════════════════════
import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library.hilt")  // Provides: Android, Kotlin, Compose, KSP, Hilt
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.cascade.datastream.taskmanager"
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

    // WorkManager for background task execution
    implementation(libs.androidx.work.runtime.ktx)

    // Compose UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
}
