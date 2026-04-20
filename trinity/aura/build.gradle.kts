import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library.hilt")
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.trinity.aura"
}

dependencies {
    implementation(project(":core-module"))

    // WebSocket for Conference Room event bridge (built into okhttp)
    implementation(libs.okhttp)

    // JSON parsing
    implementation(libs.org.json)

    // Navigation
    implementation(libs.androidx.navigation.compose)
}

