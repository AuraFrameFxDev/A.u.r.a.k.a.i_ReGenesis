import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library.hilt")
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory"
}

dependencies {
    implementation(project(":core-module"))
    implementation(project(":kai:sentinelsfortress:security"))
    implementation(project(":genesis:oracledrive"))
    implementation(project(":genesis:oracledrive:rootmanagement"))

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose.ui)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.room.ktx)
    implementation(libs.kotlinx.serialization.json)
    ksp(libs.androidx.room.compiler)
}
