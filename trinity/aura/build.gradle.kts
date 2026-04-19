import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library.hilt")
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.trinity.aura"
}

dependencies {
    implementation(project(":core-module"))

    // WebSocket for Conference Room event bridge
    implementation(libs.okhttp3)
    implementation(libs.okhttp3.websocket)

    // JSON parsing
    implementation("org.json:json:20240303")
}

