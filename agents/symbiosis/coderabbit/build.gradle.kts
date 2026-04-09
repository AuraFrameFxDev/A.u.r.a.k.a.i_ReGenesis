import org.gradle.kotlin.dsl.implementation

plugins {
    id("genesis.android.library")
}

android {
    namespace = "dev.aurakai.auraframefx.agents.symbiosis.coderabbit"
}

dependencies {
    implementation(project(":core-module"))
    implementation(project(":app"))
    implementation(libs.langchain4j.http.client.jdk)
}
