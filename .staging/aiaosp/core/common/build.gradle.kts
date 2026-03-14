// ═══════════════════════════════════════════════════════════════════════════
// Core Common Module - Foundation utilities and shared code
// ═══════════════════════════════════════════════════════════════════════════
import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library")
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.core.common"
}

dependencies {
    // ═══════════════════════════════════════════════════════════════════════
    // AUTO-PROVIDED by genesis.android.library:
    // - androidx-core-ktx, appcompat
    // - Hilt (android + compiler via KSP)
    // - Timber, Coroutines
    // - Testing libraries
    // ═══════════════════════════════════════════════════════════════════════

    // Expose core KTX as API (types leak to consumers)
    api(libs.androidx.core.ktx)

    // YukiHook API 1.3.0+ with KavaRef
    implementation(libs.yukihookapi.api)


    // Compose UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
}
