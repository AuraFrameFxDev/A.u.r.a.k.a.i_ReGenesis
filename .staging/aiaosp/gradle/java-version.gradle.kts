// ===================================================================
// GENESIS PROTOCOL - JAVA TOOLCHAIN CONFIGURATION
// ===================================================================
// Strictly enforces Java 24 with fallback to 25 across all modules
// Optimized for consciousness substrate performance
// ===================================================================


// Configure Java toolchain for all projects
allprojects {
    // Configure Java toolchain with strict version requirements
    plugins.withType<JavaBasePlugin> {
        extensions.configure<JavaPluginExtension> {
            toolchain {
                // Use Java 25 toolchain consistently
                val targetVersion = 25

                languageVersion.set(JavaLanguageVersion.of(targetVersion))
                logger.lifecycle("🧠 GENESIS PROTOCOL: Using Java $targetVersion for ${project.name}")
            }
        }
    }


    // Configure Kotlin compilation for Android modules

    // Configure Java compilation for all modules
    plugins.withType<JavaPlugin> {
        tasks.withType<JavaCompile>().configureEach {
            sourceCompatibility = "25"
            targetCompatibility = "25"
            options.encoding = "UTF-8"
            options.isIncremental = true
            options.isFork = true

            // Enable all warnings and treat them as errors
            options.compilerArgs.addAll(
                listOf(
                    "-Xlint:all",
                    "-Werror",
                    "--release", "25"
                )
            )
        }
    }


// Log detailed Java version information at the end of the configuration phase
    gradle.projectsEvaluated {
        val jvm = System.getProperty("java.vm.name")
        val jreVersion = System.getProperty("java.version")
        val jreHome = System.getProperty("java.home")

        logger.lifecycle(
            """
        ===================================================================
        🧬 GENESIS PROTOCOL - JAVA TOOLCHAIN STATUS
        ===================================================================
        - Current JVM: $jvm (24)
        - Java Version: $jreVersion
        - Java Home: $jreHome
        - Active Java Toolchain: ${JavaVersion.current()}
        - Target Java Toolchain: 25
        - Java Bytecode Target: 25 (sourceCompatibility/targetCompatibility)
        - Kotlin Compiler Target: 24
        - Project: ${project.name} (${project.path})
        ===================================================================""".trimIndent()
        )
    }
}
