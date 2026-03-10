import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library.hilt")
    id("org.jetbrains.kotlin.plugin.serialization")
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.genesis.oracledrive.rootmanagement"

    testOptions {
        unitTests.isIncludeAndroidResources = false
    }
}

dependencies {
    // Core Android - Expose as API
    api(libs.androidx.core.ktx)

    // Compose BOM and UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.tooling)

    // Compose / Lifecycle / Navigation / Hilt integrations (Extension modules)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    // Root/System Operations
    implementation(libs.libsu.core)
    implementation(libs.libsu.io)
    implementation(libs.libsu.service)

    // YukiHook API 1.3.0+ stack
    implementation(libs.yukihookapi.api)
    ksp(libs.yukihookapi.ksp)
    // Serialization
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.kotlin.test)
}

ksp {
    arg("yukihookapi.modulePackageName", "dev.aurakai.auraframefx.genesis.oracledrive.rootmanagement")
}

// Force a single annotations artifact to avoid duplicate-class errors
configurations.all {
    if (name.contains("AndroidTest")) {
        return@all
    }

    resolutionStrategy {
        force("org.jetbrains:annotations:26.0.2-1")
    }
}
