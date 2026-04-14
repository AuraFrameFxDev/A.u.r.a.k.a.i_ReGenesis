import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library.hilt")
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.cascade.datastream.taskmanager"
}

dependencies {
    implementation(project(":cascade:datastream:routing"))
    implementation(libs.androidx.work.runtime.ktx)
}
