plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "dev.aurakai.oracledrive.bootloader"
    compileSdk = 36

    defaultConfig {
        minSdk = 34
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
    }

    buildFeatures {
        compose = true
        buildConfig = true
        aidl = false
        shaders = false
    }
}

dependencies {
    implementation(libs.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.timber)

    // Root access
    implementation("com.github.topjohnwu.libsu:core:6.0.0")
    implementation("com.github.topjohnwu.libsu:io:6.0.0")

    testImplementation(libs.junit)
}
