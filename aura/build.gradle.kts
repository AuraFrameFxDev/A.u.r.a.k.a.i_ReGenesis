plugins {
    id("genesis.android.library")
}

android {
    namespace = "dev.aurakai.auraframefx.aura"
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
}
