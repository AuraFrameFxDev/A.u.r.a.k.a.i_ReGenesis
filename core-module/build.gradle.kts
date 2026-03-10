// ═══════════════════════════════════════════════════════════════════════════
// Core Module - Central core module
// ═══════════════════════════════════════════════════════════════════════════
import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.core.module"
    // Java 24 compileOptions are set by genesis.android.base
}

dependencies {
    // ═══════════════════════════════════════════════════════════════════════
    // AUTO-PROVIDED by genesis.android.library:
    // - androidx-core-ktx, appcompat
    // - Hilt (android + compiler via KSP)
    // - Timber, Coroutines
    // - Compose enabled by default
    // - Java 24 bytecode target
    // ═══════════════════════════════════════════════════════════════════════
    
    // Hilt - Explicit dependencies
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    
    // Expose core KTX as API (types leak to consumers)
    api(libs.androidx.core.ktx)

    // Compose UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
}

