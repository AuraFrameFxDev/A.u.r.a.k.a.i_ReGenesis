import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library.hilt")
    id("org.jetbrains.kotlin.plugin.serialization")
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.agents.growthmetrics.identity"
}

dependencies {
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.room.ktx)
    implementation(libs.kotlinx.serialization.json)
}
