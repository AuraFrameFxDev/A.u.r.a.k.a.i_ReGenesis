// ═══════════════════════════════════════════════════════════════════════════
// Core Data Module - Data layer, repositories, and data sources
// ═══════════════════════════════════════════════════════════════════════════
import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library")
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.core.data"
}

dependencies {
    // ═══════════════════════════════════════════════════════════════════════
    // AUTO-PROVIDED by genesis.android.library:
    // - androidx-core-ktx, appcompat
    // - Hilt (android + compiler via KSP)
    // - Timber, Coroutines
    // - Compose enabled by default
    // ═══════════════════════════════════════════════════════════════════════

    // Expose core KTX as API (types leak to consumers)
    api(libs.androidx.core.ktx)

    // Compose UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
}
