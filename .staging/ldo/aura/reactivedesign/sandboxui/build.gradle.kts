import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library.hilt")
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.aura.reactivedesign.sandboxui"
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}
