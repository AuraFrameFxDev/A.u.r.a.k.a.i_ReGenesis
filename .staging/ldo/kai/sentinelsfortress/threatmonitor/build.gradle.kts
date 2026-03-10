import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library.hilt")
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.kai.sentinelsfortress.threatmonitor"
}

dependencies {
    implementation(project(":kai:sentinelsfortress:security"))
    implementation(libs.libsu.core)
}
