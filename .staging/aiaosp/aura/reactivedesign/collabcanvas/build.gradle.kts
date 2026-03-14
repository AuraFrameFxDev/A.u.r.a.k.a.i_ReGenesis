import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library.hilt")
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.aura.reactivedesign.collabcanvas"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
        isCoreLibraryDesugaringEnabled = true
    }
}

dependencies {
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.okhttp)
    implementation(libs.gson)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}
