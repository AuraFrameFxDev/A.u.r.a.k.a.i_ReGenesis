import org.gradle.api.Project
import org.gradle.api.JavaVersion
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Centralized JVM toolchain and compilation configuration for all Genesis modules.
 */
object GenesisJvmConfig {
    const val JVM_VERSION_INT = 25
    val KOTLIN_JVM_TARGET = JvmTarget.JVM_25

    /**
     * Configures the Kotlin and Java compilation settings for the given project.
     */
    fun configureKotlinJvm(project: Project) {
        with(project) {
            // 1. Configure Kotlin Compilation Tasks
            tasks.withType<KotlinCompile>().configureEach {
                compilerOptions {
                    jvmTarget.set(KOTLIN_JVM_TARGET)
                    freeCompilerArgs.addAll(
                        "-Xcontext-parameters",
                        "-Xannotation-default-target=param-property",
                        "-Xjdk-release=$JVM_VERSION_INT",
                        "-opt-in=kotlin.RequiresOptIn",
                        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                        "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                        "-Xlambdas=indy"
                    )
                }
            }

            // 2. Configure Java Compilation Tasks
            tasks.withType<JavaCompile>().configureEach {
                try {
                    val javaToolchains = project.extensions.getByType<JavaToolchainService>()
                    javaCompiler.set(javaToolchains.compilerFor {
                        languageVersion.set(JavaLanguageVersion.of(JVM_VERSION_INT))
                    })
                } catch (_: Exception) {
                    // Fallback
                }
                sourceCompatibility = JVM_VERSION_INT.toString()
                targetCompatibility = JVM_VERSION_INT.toString()
                options.compilerArgs.add("--enable-preview")
                options.encoding = "UTF-8"
            }
        }
    }
}
