import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library.hilt")
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.aura.reactivedesign.collabcanvas"

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation(project(":core-module"))
    implementation(libs.compose.material.icons.extended)
    implementation(libs.okhttp)
    implementation(libs.gson)
    testImplementation(libs.junit)
}
