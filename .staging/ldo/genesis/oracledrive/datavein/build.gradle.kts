import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library.hilt")
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.genesis.oracledrive.datavein"
}

dependencies {
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}
