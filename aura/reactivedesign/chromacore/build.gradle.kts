import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library.hilt")
    alias(libs.plugins.kotlin.serialization)
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.aura.reactivedesign.chromacore"

    buildFeatures {
        compose = true
    }

    composeOptions {
        // Using common compose configuration
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
        isCoreLibraryDesugaringEnabled = true
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui.tooling.preview)

    // Serialization
    implementation(libs.kotlinx.serialization.json)
}
