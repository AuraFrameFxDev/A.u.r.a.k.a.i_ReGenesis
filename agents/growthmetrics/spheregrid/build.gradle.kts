import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library.hilt")
    id("org.jetbrains.kotlin.plugin.serialization")
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.agents.growthmetrics.spheregrid"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
        isCoreLibraryDesugaringEnabled = true
    }
}

dependencies {
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.room.ktx)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}
