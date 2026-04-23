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

    // Add BuildConfig to source sets so KSP can find it
    sourceSets["main"].java {
        srcDir("build/generated/source/buildConfig/debug")
        srcDir("build/generated/source/buildConfig/release")
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
    implementation(libs.bundles.compose.tooling)
    implementation(libs.compose.material.icons.extended)

    // YukiHook
    implementation(libs.yukihookapi.api) {
        exclude(group = "com.highcapable.yukihookapi", module = "ksp-xposed")
    }
    debugImplementation(libs.compose.ui.tooling)
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
