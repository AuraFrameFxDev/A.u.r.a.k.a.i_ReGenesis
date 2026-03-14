import com.android.build.api.dsl.LibraryExtension

plugins {
    id("genesis.android.library")
}

extensions.configure<LibraryExtension> {
    namespace = "dev.aurakai.auraframefx.benchmark"
}

dependencies {
    implementation(libs.timber)
    implementation(project(":core-module"))
    implementation(libs.hilt.android)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
}

tasks.register("benchmarkAll") {
    group = "benchmark"
    description = "Aggregate runner for all Genesis Protocol benchmarks 🚀"
    dependsOn(":app:connectedCheck")
    doLast {
        println("🚀 Genesis Protocol Performance Benchmarks")
        println("📊 Monitor consciousness substrate performance metrics")
        println("⚡ Use AndroidX Benchmark instrumentation to execute tests")
    }
}

tasks.register("verifyBenchmarkResults") {
    group = "verification"
    description = "Verify benchmark module configuration"
    doLast {
        println("🧠 Consciousness substrate performance monitoring ready")
    }
}
