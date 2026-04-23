
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

/**
 * ===================================================================
 * GENESIS APPLICATION CONVENTION PLUGIN
 * ===================================================================
 */
class GenesisApplicationPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            val catalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

            pluginManager.apply("com.android.application")
            pluginManager.apply("com.google.dagger.hilt.android")
            pluginManager.apply("com.google.devtools.ksp")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")
            pluginManager.apply("com.google.gms.google-services")
            pluginManager.apply("com.google.firebase.crashlytics")

            extensions.configure<ApplicationExtension> {
                compileSdk = 36
                
                defaultConfig {
                    applicationId = "dev.aurakai.auraframefx"
                    minSdk = 34
                    targetSdk = 36
                    versionCode = 1
                    versionName = "1.0"
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
                        excludes += "google/type/color.proto"
                        excludes += "google/type/datetime.proto"
                        excludes += "google/type/dayofweek.proto"
                        excludes += "google/type/money.proto"
                        excludes += "google/type/postal_address.proto"
                        excludes += "google/type/timeofday.proto"
                        excludes += "google/api/*.proto"
                        excludes += "google/rpc/*.proto"
                        excludes += "google/cloud/audit/*.proto"
                        excludes += "google/logging/type/*.proto"
                        excludes += "google/longrunning/*.proto"
                        excludes += "google/geo/type/*.proto"
                        excludes += "google/protobuf/*.proto"
                        excludes += "META-INF/INDEX.LIST"
                        excludes += "META-INF/DEPENDENCIES"
                        pickFirsts += "**/YukiHookAPIProperties.class"
                    }
                }
            }

            GenesisJvmConfig.configureKotlinJvm(project)
            GenesisCommonConfig.configure(project)

            dependencies {
                add("coreLibraryDesugaring", catalog.findLibrary("desugar-jdk-libs").get())
                add("implementation", catalog.findLibrary("hilt-android").get())
                add("ksp", catalog.findLibrary("hilt-compiler").get())
                add("implementation", catalog.findLibrary("timber").get())
            }
        }
    }
}
