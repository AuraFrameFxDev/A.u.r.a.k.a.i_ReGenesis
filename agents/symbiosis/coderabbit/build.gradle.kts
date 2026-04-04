plugins {
    id("com.android.library")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "dev.aurakai.auraframefx.agents.symbiosis.coderabbit"
    compileSdk = 36

    defaultConfig {
        minSdk = 34
    }
}

dependencies {
    implementation(project(":core-module"))
    implementation(project(":kai:sentinelsfortress:security"))

    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Logging
    implementation(libs.timber)
}
