import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library.hilt")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
        isCoreLibraryDesugaringEnabled = true
    }
}

dependencies {
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.kotlinx.serialization.json)
    implementation(project(":core-module"))
    implementation(project(":kai:sentinelsfortress:security"))
    implementation(project(":genesis:oracledrive"))
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}
