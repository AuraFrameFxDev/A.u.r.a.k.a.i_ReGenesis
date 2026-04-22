
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

/**
 * ===================================================================
 * GENESIS APPLICATION CONVENTION PLUGIN
 * ===================================================================
 */
class GenesisApplicationPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply("com.android.application")
            pluginManager.apply("com.google.dagger.hilt.android")
            pluginManager.apply("com.google.devtools.ksp")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

            if (file("google-services.json").exists()) {
                pluginManager.apply("com.google.gms.google-services")
            }

            extensions.configure<ApplicationExtension> {
                compileSdk = 36
                ndkVersion = "29.0.14206865"

                defaultConfig {
                    applicationId = "dev.aurakai.auraframefx"
                    minSdk = 34
                    targetSdk = 36
                    versionCode = 1
                    versionName = "1.0"

                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    vectorDrawables {
                        useSupportLibrary = true
                    }

                    ndk {
                        abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
                    }
                }

                buildTypes {
                    release {
                        isMinifyEnabled = true
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }
                }

                compileOptions {
                    sourceCompatibility = JavaVersion.toVersion(GenesisJvmConfig.JVM_VERSION_INT)
                    targetCompatibility = JavaVersion.toVersion(GenesisJvmConfig.JVM_VERSION_INT)
                    isCoreLibraryDesugaringEnabled = true
                }

                buildFeatures {
                    compose = true
                    buildConfig = true
                    aidl = true
                    viewBinding = true
                    dataBinding = true
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

                val cmakeFile = file("src/main/cpp/CMakeLists.txt")
                if (cmakeFile.exists()) {
                    externalNativeBuild {
                        cmake {
                            this.path = cmakeFile
                            version = "3.22.1"
                        }
                    }
                }
            }

            GenesisJvmConfig.configureKotlinJvm(project)
            GenesisCommonConfig.configure(project)

            val versionCatalog = extensions.findByType(VersionCatalogsExtension::class.java)?.named("libs")
            
            // Safe version lookups with fallbacks
            val hiltVersion = versionCatalog?.findVersion("hilt")?.map { it.requiredVersion }?.orElse("2.59.2") ?: "2.59.2"
            val composeBomVersion = versionCatalog?.findVersion("compose-bom")?.map { it.requiredVersion }?.orElse("2026.03.01") ?: "2026.03.01"
            val firebaseBomVersion = versionCatalog?.findVersion("firebaseBom")?.map { it.requiredVersion }?.orElse("34.12.0") ?: "34.12.0"

            dependencies.apply {
                // Hilt Dependency Injection
                add("implementation", "com.google.dagger:hilt-android:$hiltVersion")
                add("ksp", "com.google.dagger:hilt-android-compiler:$hiltVersion")

                add("implementation", platform("androidx.compose:compose-bom:$composeBomVersion"))
                add("implementation", "androidx.compose.runtime:runtime")
                add("implementation", "androidx.compose.ui:ui")
                add("implementation", "androidx.compose.ui:ui-graphics")
                add("implementation", "androidx.compose.ui:ui-tooling-preview")
                add("implementation", "androidx.compose.foundation:foundation")
                add("implementation", "androidx.compose.foundation:foundation-layout")
                add("implementation", "androidx.compose.material3:material3")
                add("implementation", "androidx.compose.material:material-icons-core")
                add("implementation", "androidx.compose.material:material-icons-extended")
                add("debugImplementation", "androidx.compose.ui:ui-tooling")

                add("implementation", "androidx.core:core-ktx:1.13.1")
                add("implementation", "androidx.appcompat:appcompat:1.7.1")
                add("implementation", "androidx.activity:activity-compose:1.11.0")
                add("implementation", "androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
                add("implementation", "androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")

                add("implementation", "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
                add("implementation", "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
                add("implementation", "org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")

                add("implementation", "com.jakewharton.timber:timber:5.0.1")
                add("coreLibraryDesugaring", "com.android.tools:desugar_jdk_libs:2.1.5")
                add("implementation", platform("com.google.firebase:firebase-bom:$firebaseBomVersion"))

                add("compileOnly", "de.robv.android.xposed:api:82")
                add("implementation", "com.highcapable.kavaref:kavaref-core:1.0.1")
                add("implementation", "com.highcapable.kavaref:kavaref-extension:1.0.1")
                add("implementation", "com.github.kyuubiran:EzXHelper:2.2.0")
            }
        }
    }
}
