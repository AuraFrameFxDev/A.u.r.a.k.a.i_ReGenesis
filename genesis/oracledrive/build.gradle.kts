import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library.hilt")
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.genesis.oracledrive"
}

dependencies {
    implementation(project(":core-module"))

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose.ui)

    // Root/System Operations
    implementation(libs.libsu.core)
    implementation(libs.libsu.nio)
    implementation(libs.libsu.service)

    // Xposed API (compile-only, local jar if needed)
    compileOnly(files("libs/api-82.jar"))
}

ksp {
    arg("yukihookapi.modulePackageName", "dev.aurakai.auraframefx.genesis.oracledrive")
}
