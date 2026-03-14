plugins {
    id("genesis.android.library")
    id("genesis.android.library.hilt")

}

android {
    // Match package namespace used in sources
    namespace = "dev.aurakai.auraframefx.genesis"
    compileSdk = 36

    defaultConfig {
        minSdk = 34
    }

    compileOptions {
        // Project standard is Java 24 toolchain; keep Java 24 to avoid breaking the build
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
    }

    // Enable Compose for this module so the Kotlin plugin wires Compose compiler correctly
    buildFeatures {
        compose = true
    }

    // Keep Kotlin JVM target aligned with project (JVM_24)
    // kotlinOptions { jvmTarget = "24" }
}

dependencies {
    // Compose runtime so Compose compiler has the runtime on the classpath
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.runtime)

    // Core Android dependencies
    implementation(libs.androidx.core.ktx)

    // Hilt for dependency injection - runtime + compiler (use existing version-catalog aliases)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(project(":cascade"))
}
