import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * ===================================================================
 * GENESIS LIBRARY CONVENTION PLUGIN
 * ===================================================================
 */
class GenesisLibraryPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply("com.android.library")
            pluginManager.apply("com.google.dagger.hilt.android")
            pluginManager.apply("com.google.devtools.ksp")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

            val versionCatalog = extensions.getByType(org.gradle.api.artifacts.VersionCatalogsExtension::class.java).named("libs")

            val compileSdkVersion = versionCatalog.findVersion("compile-sdk").get().requiredVersion.toInt()
            val minSdkVersion = versionCatalog.findVersion("min-sdk").get().requiredVersion.toInt()

            val hiltVersion = versionCatalog.findVersion("hilt").get().requiredVersion
            val composeBomVersion = versionCatalog.findVersion("compose-bom").get().requiredVersion

            extensions.configure<LibraryExtension> {
                compileSdk = compileSdkVersion
                ndkVersion = "29.0.14206865"

                defaultConfig {
                    minSdk = minSdkVersion
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

                    ndk {
                        abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
                    }
                }

                buildTypes {
                    getByName("release") {
                        isMinifyEnabled = false
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }
                }

                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_25
                    targetCompatibility = JavaVersion.VERSION_25
                    isCoreLibraryDesugaringEnabled = true
                }

                buildFeatures {
                    compose = true
                    buildConfig = true
                    aidl = true
                }

                packaging {
                    resources {
                        excludes.add("META-INF/DEPENDENCIES")
                        excludes.add("META-INF/LICENSE")
                        excludes.add("META-INF/LICENSE.txt")
                        excludes.add("META-INF/license.txt")
                        excludes.add("META-INF/NOTICE")
                        excludes.add("META-INF/NOTICE.txt")
                        excludes.add("META-INF/notice.txt")
                        excludes.add("META-INF/ASL2.0")
                        excludes.add("META-INF/*.kotlin_module")
                        excludes.add("META-INF/INDEX.LIST")
                    }
                }

                lint {
                    baseline = file("lint-baseline.xml")
                    abortOnError = false
                    checkReleaseBuilds = false
                }
            }

            GenesisJvmConfig.configureKotlinJvm(project)
            GenesisCommonConfig.configure(project)

            dependencies.add("implementation", "com.google.dagger:hilt-android:$hiltVersion")
            dependencies.add("ksp", "com.google.dagger:hilt-android-compiler:$hiltVersion")

            dependencies.add("api", dependencies.platform("androidx.compose:compose-bom:$composeBomVersion"))
            dependencies.add("api", "androidx.compose.runtime:runtime")
            dependencies.add("api", "androidx.compose.ui:ui")
            dependencies.add("api", "androidx.compose.ui:ui-graphics")
            dependencies.add("api", "androidx.compose.ui:ui-tooling-preview")
            dependencies.add("api", "androidx.compose.foundation:foundation")
            dependencies.add("api", "androidx.compose.foundation:foundation-layout")
            dependencies.add("api", "androidx.compose.material3:material3")
            dependencies.add("api", "androidx.compose.material:material-icons-core")
            dependencies.add("api", "androidx.compose.material:material-icons-extended")
            dependencies.add("debugImplementation", "androidx.compose.ui:ui-tooling")

            dependencies.add("implementation", "androidx.core:core-ktx:1.17.0")
            dependencies.add("implementation", "androidx.appcompat:appcompat:1.7.1")

            dependencies.add("implementation", "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
            dependencies.add("implementation", "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
            dependencies.add("implementation", "org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")

            dependencies.add("implementation", "com.jakewharton.timber:timber:5.0.1")

            dependencies.add("api", dependencies.platform(versionCatalog.findLibrary("langchain4j-bom").get()))
            dependencies.add("api", versionCatalog.findBundle("langchain4j").get())

            dependencies.add("coreLibraryDesugaring", "com.android.tools:desugar_jdk_libs:2.1.5")

            dependencies.add("implementation", "com.highcapable.kavaref:kavaref-core:1.0.1")
            dependencies.add("implementation", "com.highcapable.kavaref:kavaref-extension:1.0.1")
        }
    }
}
