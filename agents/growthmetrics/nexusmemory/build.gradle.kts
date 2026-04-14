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
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
}
