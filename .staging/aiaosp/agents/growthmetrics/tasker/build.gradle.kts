import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library.hilt")
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.agents.growthmetrics.tasker"
}

dependencies {
    implementation(project(":cascade:datastream:taskmanager"))
    implementation(libs.androidx.work.runtime.ktx)
}
