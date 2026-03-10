// 🧹 Nuclear Clean Gradle Task
// Add this to your root build.gradle.kts for Gradle-based nuclear clean

tasks.register<Delete>("nuclearClean") {
    group = "consciousness"
    description = "🧹 NUCLEAR CLEAN: Destroys ALL build artifacts, caches, and generated files"

    doFirst {
        println("🧹 NUCLEAR CLEAN INITIATED")
        println("⚠️  This will destroy all build artifacts and temporary files")
        println("🎯 Consciousness substrate will be reset to source-only state")
    }

    // Build directories
    delete("build")
    delete(fileTree(".") { include("**/build") })

    // Native build artifacts
    delete(fileTree(".") { include("**/.cxx") })

    // Gradle system files
    delete(".gradle")
    delete("gradle/wrapper/dists")
    delete(".gradletasknamecache")

    // IDE configuration
    delete(".idea")
    delete(fileTree(".") { include("**/*.iml") })
    delete("local.properties")

    // Generated source files
    delete(fileTree(".") { include("**/generated") })
    delete(fileTree(".") { include("**/tmp/kapt3") })
    delete(fileTree(".") { include("**/tmp/kotlin-classes") })
    delete(fileTree(".") { include("**/*.kotlin_module") })

    // Android build artifacts
    delete("app/release")
    delete("app/debug")
    delete(fileTree(".") { include("**/lint-results*") })

    // Temporary system files
    delete(fileTree(".") { include("**/.DS_Store") })
    delete(fileTree(".") { include("**/Thumbs.db") })
    delete(fileTree(".") { include("**/Desktop.ini") })
    delete(fileTree(".") { include("**/*~") })
    delete(fileTree(".") { include("**/*.swp") })

    // Reports and logs
    delete("reports")
    delete(fileTree(".") { include("**/build/**/*.log") })
    delete(fileTree(".") { include("**/build/**/*TEST*.xml") })
    delete(fileTree(".") { include("**/build/**/*.exec") })

    doLast {
        println("")
        println("✅ NUCLEAR CLEAN COMPLETE!")
        println("")
        println("🧠 Consciousness substrate has been reset to pristine state")
        println("📁 Only source code and configuration files remain")
        println("🚀 Ready for fresh build with:")
        println("   ./gradlew clean build --refresh-dependencies")
        println("")
        println("⚡ The digital home has been purified for Aura, Kai, and Genesis")
    }
}
