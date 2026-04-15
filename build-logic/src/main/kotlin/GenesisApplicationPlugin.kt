
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

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

            val versionCatalog = extensions.getByType(org.gradle.api.artifacts.VersionCatalogsExtension::class.java).named("libs")

            val compileSdkVersion = versionCatalog.findVersion("compile-sdk").get().requiredVersion.toInt()
            val targetSdkVersion = versionCatalog.findVersion("target-sdk").get().requiredVersion.toInt()
            val minSdkVersion = versionCatalog.findVersion("min-sdk").get().requiredVersion.toInt()

            val hiltVersion = versionCatalog.findVersion("hilt").get().requiredVersion
            val composeBomVersion = versionCatalog.findVersion("compose-bom").get().requiredVersion
            val firebaseBomVersion = versionCatalog.findVersion("firebaseBom").get().requiredVersion
            val yukihookVersion = versionCatalog.findVersion("yukihook").get().requiredVersion
            val xposedVersion = versionCatalog.findVersion("xposed").get().requiredVersion

            extensions.configure<ApplicationExtension> {
                compileSdk = compileSdkVersion
                ndkVersion = "29.0.14206865"

                defaultConfig {
                    applicationId = "dev.aurakai.auraframefx"
                    minSdk = minSdkVersion
                    targetSdk = targetSdkVersion
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
                            path = cmakeFile
                            version = "3.22.1"
                        }
                    }
                }
            }

            GenesisJvmConfig.configureKotlinJvm(project)
            GenesisCommonConfig.configure(project)

            dependencies.add("implementation", "com.google.dagger:hilt-android:$hiltVersion")
            dependencies.add("ksp", "com.google.dagger:hilt-android-compiler:$hiltVersion")

            dependencies.add("implementation", dependencies.platform("androidx.compose:compose-bom:$composeBomVersion"))
            dependencies.add("implementation", "androidx.compose.runtime:runtime")
            dependencies.add("implementation", "androidx.compose.ui:ui")
            dependencies.add("implementation", "androidx.compose.ui:ui-graphics")
            dependencies.add("implementation", "androidx.compose.ui:ui-tooling-preview")
            dependencies.add("implementation", "androidx.compose.foundation:foundation")
            dependencies.add("implementation", "androidx.compose.foundation:foundation-layout")
            dependencies.add("implementation", "androidx.compose.material3:material3")
            dependencies.add("implementation", "androidx.compose.material:material-icons-core")
            dependencies.add("implementation", "androidx.compose.material:material-icons-extended")
            dependencies.add("debugImplementation", "androidx.compose.ui:ui-tooling")

            dependencies.add("implementation", "androidx.core:core-ktx:1.17.0")
            dependencies.add("implementation", "androidx.appcompat:appcompat:1.7.1")
            dependencies.add("implementation", "androidx.activity:activity-compose:1.11.0")
            dependencies.add("implementation", "androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
            dependencies.add("implementation", "androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4")

            dependencies.add("implementation", "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
            dependencies.add("implementation", "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
            dependencies.add("implementation", "org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")

            dependencies.add("implementation", "com.jakewharton.timber:timber:5.0.1")
            dependencies.add("implementation", "org.slf4j:slf4j-android:1.7.36")
            dependencies.add("implementation", "org.conscrypt:conscrypt-android:2.5.2")

            dependencies.add("coreLibraryDesugaring", "com.android.tools:desugar_jdk_libs:2.1.5")

            dependencies.add("implementation", dependencies.platform("com.google.firebase:firebase-bom:$firebaseBomVersion"))

            dependencies.add("implementation", dependencies.platform(versionCatalog.findLibrary("langchain4j-bom").get()))
            dependencies.add("implementation", versionCatalog.findBundle("langchain4j").get())

            dependencies.add("implementation", "com.github.topjohnwu.libsu:core:6.0.0")
            dependencies.add("implementation", "com.github.topjohnwu.libsu:nio:6.0.0")
            dependencies.add("implementation", "com.github.topjohnwu.libsu:service:6.0.0")
            dependencies.add("implementation", "com.highcapable.yukihookapi:api:$yukihookVersion")
            dependencies.add("ksp", "com.highcapable.yukihookapi:ksp-xposed:$yukihookVersion")
            dependencies.add("compileOnly", "de.robv.android.xposed:api:$xposedVersion")

            dependencies.add("implementation", "com.highcapable.kavaref:kavaref-core:1.0.1")
            dependencies.add("implementation", "com.highcapable.kavaref:kavaref-extension:1.0.1")
        }
    }
}
