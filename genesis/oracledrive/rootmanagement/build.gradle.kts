import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library.hilt")
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.genesis.oracledrive.rootmanagement"

    testOptions {
        unitTests.isIncludeAndroidResources = false
    }
}

dependencies {
    implementation(project(":core-module"))
    
    // Compose / Lifecycle / Navigation / Hilt integrations (those not in plugin)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.media3.exoplayer)

    // Root/System Operations
    implementation(libs.libsu.core)
    implementation(libs.libsu.nio)
    implementation(libs.libsu.service)

    // Unit Test dependencies
    testImplementation(libs.kotlin.test)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}

ksp {
    arg("yukihookapi.modulePackageName", "dev.aurakai.auraframefx.genesis.oracledrive.rootmanagement")
}
