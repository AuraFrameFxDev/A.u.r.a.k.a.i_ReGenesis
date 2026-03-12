import org.gradle.api.Project
import org.gradle.api.JavaVersion
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

/**
 * ===================================================================
 * GENESIS JVM CONFIGURATION
 * ===================================================================
 *
 * Centralized JVM toolchain and compilation configuration for all Genesis modules.
 *
 * This object provides:
 * - Single source of truth for JVM version across all modules
 * - Shared utility functions for configuring Kotlin JVM toolchain
 * - Consistent compiler options across all convention plugins
 *
 * @since Genesis Protocol 2.0 (AGP 9.0.0-alpha14 Compatible)
 */
object GenesisJvmConfig {
    /**
     * The JVM version used throughout the Genesis project.
     *
     * Java 25 bytecode is:
     * - Firebase compatible
     * - Maximum target supported by Kotlin 2.2.x/2.3.x
     * - Enables modern Java features with backward compatibility via desugaring
     */
    const val JVM_VERSION = 25

    /**
     * Configure the Kotlin JVM toolchain and Kotlin compilation options for the given Gradle project.
     *
     * Explicitly sets:
     * - Kotlin `compilerOptions.jvmTarget` to JVM_VERSION (25)
     * - Java compilation tasks to target JVM_VERSION (25)
     * - Compiler opt-in flags for experimental APIs
     * - JDK release target via `-Xjdk-release`
     *
     * @param project The Gradle project to configure.
     */
    fun configureKotlinJvm(project: Project) {
        with(project) {
            // Configure Kotlin compilation to match Java toolchain
            // MUST match the target used in GenesisApplicationPlugin and GenesisLibraryHiltPlugin (JVM 25)
            tasks.withType<KotlinJvmCompile>().configureEach {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_25)
                    freeCompilerArgs.addAll(
                        "-Xcontext-parameters",
                        "-Xannotation-default-target=param-property",
                        "-Xjdk-release=25",
                        "-opt-in=kotlin.RequiresOptIn",
                        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                        "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
                    )
                }
            }

            // Explicitly configure Java compilation tasks to target JVM 25
            tasks.withType<JavaCompile>().configureEach {
                sourceCompatibility = JavaVersion.VERSION_25.toString()
                targetCompatibility = JavaVersion.VERSION_25.toString()
            }

            // Configure toolchain - use afterEvaluate so extensions are ready
            afterEvaluate {
                // Technique 1: Try via 'kotlin' extension (Standard/External)
                try {
                    extensions.findByType(org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension::class.java)?.apply {
                        jvmToolchain(JVM_VERSION)
                    }
                } catch (e: Exception) {
                    logger.debug("Could not configure toolchain via 'kotlin' extension: ${e.message}")
                }

                // Technique 2: Try via 'android' extension (AGP 9.0 Built-in)
                try {
                    // This is harder to do type-safely without importing AGP internal types,
                    // but we can try to find the extension named "android"
                    val android = extensions.findByName("android")
                    if (android != null) {
                        // AGP 9.0 built-in Kotlin might expose a 'kotlin' block
                        // Alternatively, we rely on compileOptions.sourceCompatibility/targetCompatibility
                        // which are already set in the convention plugins.
                    }
                } catch (e: Exception) {
                    logger.debug("Could not configure toolchain via 'android' extension: ${e.message}")
                }
            }
        }
    }
}
