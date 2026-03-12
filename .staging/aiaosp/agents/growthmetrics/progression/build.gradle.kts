import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library.hilt")
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.agents.growthmetrics.progression"
}

dependencies {
    implementation(project(":agents:growthmetrics:metareflection"))
    implementation(project(":agents:growthmetrics:spheregrid"))
}
