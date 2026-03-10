// ⚠️ PLACEHOLDER MODULE - NO IMPLEMENTATION YET ⚠️
//
// This module is reserved for future Oracle Cloud Integration features.
// See README.md for planned features and architecture.
//
// Status: Awaiting implementation (0 source files)
// Documented: Yes (see README.md)
// Remove this module if not implementing soon to reduce build time.

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "dev.aurakai.auraframefx.oracledriveintegration"
    compileSdk = 36

    defaultConfig {
        minSdk = 34
        multiDexEnabled = true  // Required for core library desugaring
    }

    buildFeatures {
        compose = true
        buildConfig = true
        aidl = false
        shaders = false
    }

    compileOptions {
        // Use a compatible Java version and enable core library desugaring for this module
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
        isCoreLibraryDesugaringEnabled = true
    }

    defaultConfig {
        multiDexEnabled = true
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
    }

    dependencies {
        implementation("com.github.topjohnwu.libsu:core:6.0.0")
        implementation("com.github.topjohnwu.libsu:io:6.0.0")
        coreLibraryDesugaring(libs.desugar.jdk.libs)

        implementation(libs.timber)
        implementation(libs.hilt.android)
        ksp(libs.hilt.android.compiler)
        implementation(libs.androidx.material)

        implementation(libs.hilt.android)
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.lifecycle.runtime.ktx)
        implementation(libs.androidx.lifecycle.viewmodel.ktx)
        coreLibraryDesugaring(libs.desugar.jdk.libs)
        implementation(platform(libs.androidx.compose.bom))
        implementation(libs.androidx.activity.compose)
        implementation(libs.androidx.navigation.compose)
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.compose.material3)
        implementation(libs.bundles.lifecycle)
        implementation(libs.bundles.room)
        implementation(libs.androidx.datastore.preferences)
        implementation(libs.androidx.datastore.core)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.kotlinx.datetime)
        implementation(libs.bundles.coroutines)
        implementation(libs.bundles.network)
        implementation(platform(libs.firebase.bom))
        implementation(libs.bundles.firebase)
        ksp(libs.hilt.compiler)
        ksp(libs.androidx.room.compiler)
        implementation(libs.firebase.auth.ktx)
        compileOnly(libs.xposed.api)
        compileOnly(libs.yukihookapi)
        implementation(libs.androidx.material)
        androidTestImplementation(platform(libs.androidx.compose.bom))
        androidTestImplementation(libs.hilt.android.testing)
        debugImplementation(libs.leakcanary.android)

    }
}
