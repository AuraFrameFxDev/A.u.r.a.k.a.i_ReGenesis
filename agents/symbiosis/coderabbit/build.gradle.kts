plugins {
    id("com.android.library")
}

android {
    namespace = "dev.aurakai.auraframefx.agents.symbiosis.coderabbit"
    compileSdk = 35
}

dependencies {
    implementation(project(":core-module"))
    implementation(project(":kai:sentinelsfortress:security"))
}
