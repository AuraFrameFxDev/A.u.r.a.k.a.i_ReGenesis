plugins {
    id("genesis.android.library")
}

android {
    namespace = "dev.aurakai.auraframefx.agents.symbiosis.coderabbit"
}

dependencies {
    implementation(project(":core-module"))
    implementation(project(":app"))

    // Use BOM for centralized version management
    implementation(platform(libs.langchain4j.bom))

    // LangChain4j Core Services (versions from BOM)
    implementation(libs.langchain4j.core)
    implementation(libs.langchain4j.open.ai)
    implementation(libs.langchain4j.http.client.okhttp)

    // Logging (no BOM, explicit version for Android compatibility)
    implementation(libs.slf4j.android)

    // Existing Android dependencies
    // ... rest remains unchanged
}
