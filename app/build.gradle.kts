// app/build.gradle.kts — REGENESIS THIN SHELL
plugins {
    id("genesis.android.application")
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "dev.aurakai.auraframefx"

    defaultConfig {
        applicationId = "dev.aurakai.auraframefx"
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    // Core internal modules
    implementation(project(":core-module"))
    implementation(project(":aura"))
    implementation(project(":kai"))
    implementation(project(":genesis"))
    implementation(project(":cascade"))

    // UI / Compose (BOM + bundle from convention plugin base)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose.ui)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // System Sovereignty
    implementation(libs.yukihookapi.api)
    ksp(libs.yukihookapi.ksp)
}