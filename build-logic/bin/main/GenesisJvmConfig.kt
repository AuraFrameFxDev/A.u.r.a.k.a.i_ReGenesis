
import org.gradle.api.Project
import org.gradle.api.JavaVersion
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

/**
 * Centralized JVM toolchain and compilation configuration for all Genesis modules.
 */
object GenesisJvmConfig {
    const val JVM_VERSION = 25
    const val JVM_VERSION_INT = 25
    val KOTLIN_JVM_TARGET = JvmTarget.JVM_25

    fun configureKotlinJvm(project: Project) {
        with(project) {
            tasks.withType<KotlinCompile>().configureEach {
                compilerOptions {
                    jvmTarget.set(KOTLIN_JVM_TARGET)
                    freeCompilerArgs.addAll(
                        "-Xcontext-parameters",
                        "-Xannotation-default-target=param-property",
                        "-Xjdk-release=$JVM_VERSION_INT",
                        "-Xenable-preview",
                        "-opt-in=kotlin.RequiresOptIn",
                        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                        "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                        "-Xlambdas=indy"
                    )
                }
            }

            tasks.withType<JavaCompile>().configureEach {
                sourceCompatibility = JavaVersion.VERSION_21.toString()
                targetCompatibility = JavaVersion.VERSION_21.toString()
            }

            // Configure toolchain safely
            extensions.findByType(KotlinProjectExtension::class.java)?.apply {
                jvmToolchain(JVM_VERSION)
            }
        }
    }
}
