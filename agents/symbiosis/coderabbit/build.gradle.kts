plugins {
    id("genesis.android.library")
}

android {
    namespace = "dev.aurakai.auraframefx.agents.symbiosis.coderabbit"
}

dependencies {
    implementation(project(":core-module"))
    implementation(project(":app"))

    // LangChain4j and common utilities are provided by the genesis.android.library convention plugin.
    // Logging (no BOM, explicit version for Android compatibility)
    implementation(libs.slf4j.android)

    // Existing Android dependencies
    // ... rest remains unchanged
}
