import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library.hilt")
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.cascade.datastream.delivery"
}

dependencies {
    implementation(project(":cascade:datastream:routing"))
    implementation(libs.okhttp)
    implementation(libs.retrofit)
}
