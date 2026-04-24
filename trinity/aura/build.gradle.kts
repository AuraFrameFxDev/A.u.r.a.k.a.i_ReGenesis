import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library.hilt")
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.trinity.aura"
    
    defaultConfig {
        buildConfigField("String", "AURA_BACKEND_WS_URL", "\"${project.findProperty("AURA_BACKEND_WS_URL") ?: "wss://api.aurakai.dev/ws"}\"")
    }
    
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core-module"))

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose.ui)

    // WebSocket for Conference Room event bridge (built into okhttp)
    implementation(libs.okhttp)

    // JSON parsing
    implementation(libs.org.json)

    // Navigation
    implementation(libs.androidx.navigation.compose)
}

