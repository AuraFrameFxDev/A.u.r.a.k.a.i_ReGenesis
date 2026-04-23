
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

/**
 * ===================================================================
 * GENESIS LIBRARY CONVENTION PLUGIN
 * ===================================================================
 */
class GenesisLibraryPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            pluginManager.apply("com.android.library")
            pluginManager.apply("com.google.dagger.hilt.android")
            pluginManager.apply("com.google.devtools.ksp")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

            extensions.configure<LibraryExtension> {
                compileSdk = libs.findVersion("compile-sdk").get().requiredVersion.toInt()
                
                defaultConfig {
                    minSdk = libs.findVersion("min-sdk").get().requiredVersion.toInt()
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }

                compileOptions {
                    sourceCompatibility = JavaVersion.toVersion(GenesisJvmConfig.JVM_VERSION_INT)
                    targetCompatibility = JavaVersion.toVersion(GenesisJvmConfig.JVM_VERSION_INT)
                    isCoreLibraryDesugaringEnabled = true
                }

                buildFeatures {
                    compose = true
                    buildConfig = true
                }

                packaging {
                    resources {
                        excludes += "/META-INF/{AL2.0,LGPL2.1}"
                        pickFirsts += "**/YukiHookAPIProperties.class"
                    }
                }
            }

            GenesisJvmConfig.configureKotlinJvm(project)
            GenesisCommonConfig.configure(project)

            dependencies {
                add("coreLibraryDesugaring", libs.findLibrary("desugar-jdk-libs").get())
                add("implementation", libs.findLibrary("hilt-android").get())
                add("ksp", libs.findLibrary("hilt-compiler").get())
                add("implementation", libs.findLibrary("timber").get())
            }
        }
    }
}
