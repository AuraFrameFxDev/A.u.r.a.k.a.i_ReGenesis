plugins {
    id("genesis.android.library")
}

android {
    namespace = "dev.aurakai.auraframefx.kai.sentinelsfortress"
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
}
