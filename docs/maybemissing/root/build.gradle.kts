plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "dev.aurakai.oracledrive.root"
    compileSdk = 36

    defaultConfig {
        minSdk = 34
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
    }

}
dependencies {
    implementation(libs.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.timber)

    // Root access libraries
    implementation("com.github.topjohnwu.libsu:core:6.0.0")
    implementation("com.github.topjohnwu.libsu:io:6.0.0")

    // Bootloader module
    implementation(project(":oracledrive:bootloader"))

    testImplementation(libs.junit)
}
