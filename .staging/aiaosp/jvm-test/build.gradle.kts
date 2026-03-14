plugins {
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    // Test dependencies
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:6.0.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:6.0.0")
    testImplementation(gradleTestKit())
}

kotlin {
    jvmToolchain(24)
}
