import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Centralized JVM toolchain and compilation configuration for all Genesis modules.
 */
object GenesisJvmConfig {
    const val JVM_VERSION_INT = 25
    val JAVA_VERSION = JavaVersion.VERSION_25
    val KOTLIN_JVM_TARGET = JvmTarget.JVM_25

    /**
     * Configures the Kotlin and Java compilation settings for the given project.
     */
    fun configureJvm(project: Project) {
        with(project) {
            // 1. Configure Java Toolchain (Standard Gradle)
            plugins.withType<org.gradle.api.plugins.JavaBasePlugin> {
                extensions.configure<JavaPluginExtension> {
                    toolchain {
                        languageVersion.set(JavaLanguageVersion.of(JVM_VERSION_INT))
                    }
                }
            }

            // 2. Configure Kotlin JVM Toolchain (Kotlin Gradle Plugin)
            plugins.withType<org.jetbrains.kotlin.gradle.plugin.KotlinBasePluginWrapper> {
                extensions.configure<KotlinProjectExtension> {
                    jvmToolchain {
                        languageVersion.set(JavaLanguageVersion.of(JVM_VERSION_INT))
                    }
                }
            }

            // 3. Configure Kotlin Compilation Tasks
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

            // 4. Configure Java Compilation Tasks
            tasks.withType<JavaCompile>().configureEach {
                val javaToolchains = project.extensions.getByType<JavaToolchainService>()
                javaCompiler.set(javaToolchains.compilerFor {
                    languageVersion.set(JavaLanguageVersion.of(JVM_VERSION_INT))
                })
                sourceCompatibility = JAVA_VERSION.toString()
                targetCompatibility = JAVA_VERSION.toString()
                options.compilerArgs.add("--enable-preview")
                options.encoding = "UTF-8"
            }
        }
    }

    /**
     * Configures Android-specific compile options.
     */
    fun configureAndroidJvm(extension: CommonExtension<*, *, *, *, *, *>) {
        extension.apply {
            compileOptions {
                sourceCompatibility = JAVA_VERSION
                targetCompatibility = JAVA_VERSION
                isCoreLibraryDesugaringEnabled = true
            }
        }
    }
}
