// ═══════════════════════════════════════════════════════════════════════════
// Core Module - Central core module
// ═══════════════════════════════════════════════════════════════════════════
import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library")
    // Hilt, KSP, and Serialization are applied by the convention plugin.
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.core.module"
    // Java 25 compileOptions are set by genesis.android.library
}

dependencies {
    // ═══════════════════════════════════════════════════════════════════════
    // AUTO-PROVIDED by genesis.android.library:
    // - androidx-core-ktx, appcompat
    // - Hilt (android + compiler via KSP)
    // - Timber, Coroutines
    // - Compose enabled by default
    // - LangChain4j (BOM + Bundle as API)
    // - Java 25 bytecode target
    // ═══════════════════════════════════════════════════════════════════════
    
    // Expose core KTX as API (types leak to consumers)
    api(libs.androidx.core.ktx)

    // Security
    implementation(libs.androidx.security.crypto)

    // Unit test dependencies
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}
