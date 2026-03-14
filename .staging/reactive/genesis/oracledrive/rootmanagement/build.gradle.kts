// ═══════════════════════════════════════════════════════════════════════════
// ROM Tools Module - System and ROM modification utilities
// ═══════════════════════════════════════════════════════════════════════════
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "dev.aurakai.auraframefx.genesis.oracledrive.rootmanagement"
    compileSdk = libs.versions.compile.sdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.min.sdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        compose = true
    }

    testOptions {
        unitTests.isIncludeAndroidResources = false
    }
}

dependencies {
    // ═══════════════════════════════════════════════════════════════════════
    // Hilt Dependency Injection
    // ═══════════════════════════════════════════════════════════════════════
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Core Android - Expose as API
    api(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    // Compose UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)

    // Compose / Lifecycle / Navigation / Hilt integrations
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    // Kotlin Serialization
    implementation(libs.kotlinx.serialization.json)

    // Root/System Operations
    implementation(libs.libsu.core)
    implementation(libs.libsu.io)
    implementation(libs.libsu.service)

    // YukiHook API 1.3.0+ stack
    implementation(libs.yukihookapi.api)
    // KSP processor should be added to the KSP configuration (not packaged at runtime)
    ksp(libs.yukihookapi.ksp)

    // Logging
    implementation(libs.timber)

    // Core Library Desugaring
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}

ksp {
    arg("yukihookapi.modulePackageName", "dev.aurakai.auraframefx.genesis.oracledrive.rootmanagement")
}

// Force a single annotations artifact to avoid duplicate-class errors
configurations.all {
    // Skip androidTest configurations to avoid issues with local JARs
    if (name.contains("AndroidTest")) {
        return@all
    }

    resolutionStrategy {
        force("org.jetbrains:annotations:26.0.2-1")
    }
}
