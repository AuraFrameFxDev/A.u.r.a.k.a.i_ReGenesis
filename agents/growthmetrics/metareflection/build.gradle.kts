import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library.hilt")
    id("org.jetbrains.kotlin.plugin.serialization")
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.agents.growthmetrics.metareflection"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}
