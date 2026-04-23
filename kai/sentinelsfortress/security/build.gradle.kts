import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library.hilt")
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.kai.sentinelsfortress.security"
}

dependencies {
    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose.ui)

    implementation("org.bouncycastle:bcprov-jdk18on:1.79")
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}
