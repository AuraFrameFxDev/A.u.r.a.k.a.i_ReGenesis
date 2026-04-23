import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library.hilt")
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.aura.reactivedesign.collabcanvas"

    buildFeatures {
        buildConfig = true
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

ksp {
    arg("yukihookapi.modulePackageName", "dev.aurakai.auraframefx.aura.reactivedesign.collabcanvas")
}

dependencies {
    implementation(project(":core-module"))

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose.ui)
    implementation(libs.compose.material.icons.extended)

    // YukiHook
    implementation(libs.yukihookapi.api) {
        exclude(group = "com.highcapable.yukihookapi", module = "ksp-xposed")
    }
    ksp(libs.yukihookapi.ksp)

    // Networking
    implementation(libs.okhttp)
    implementation(libs.gson)
    testImplementation(libs.junit)
}

// Ensure BuildConfig is generated before KSP runs
afterEvaluate {
    tasks.named("kspDebugKotlin") {
        dependsOn("generateDebugBuildConfig")
    }
    tasks.named("kspReleaseKotlin") {
        dependsOn("generateReleaseBuildConfig")
    }
}
