plugins {
    alias(libs.plugins.spotless)
    `maven-publish`
    `java-library`
    kotlin("jvm")
}

group = "dev.aurakai.auraframefx.list"
version = "1.0.0"

java {
    toolchain { languageVersion = JavaLanguageVersion.of(25) }
}

kotlin {
    jvmToolchain(25)
}


dependencies {
    // Pure Kotlin JVM module - no Android dependencies
    implementation(kotlin("stdlib"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.bundles.coroutines)

    testImplementation(libs.junit4)
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.0.0")
    testImplementation(libs.mockk)
}

tasks.test {
    useJUnitPlatform()
}
