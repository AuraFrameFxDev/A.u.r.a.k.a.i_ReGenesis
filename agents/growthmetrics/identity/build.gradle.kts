import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library.hilt")
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.agents.growthmetrics.identity"
}

dependencies {
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.room.ktx)
}
