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

dependencies {
    implementation(project(":core-module"))

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose.ui)
    implementation(libs.bundles.compose.tooling)
    implementation(libs.compose.material.icons.extended)

    // YukiHook API (without KSP processor)
    implementation(libs.yukihookapi.api) {
        exclude(group = "com.highcapable.yukihookapi", module = "ksp-xposed")
    }

    // Networking
    implementation(libs.okhttp)
    implementation(libs.gson)
    testImplementation(libs.junit)
}


