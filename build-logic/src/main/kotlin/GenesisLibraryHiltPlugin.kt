
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

/**
 * ===================================================================
 * GENESIS LIBRARY CONVENTION PLUGIN (2025 EDITION)
 * ===================================================================
 */
class GenesisLibraryHiltPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply("com.android.library")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")
            pluginManager.apply("com.google.devtools.ksp")
            pluginManager.apply("com.google.dagger.hilt.android")

            val versionCatalog = extensions.findByType(VersionCatalogsExtension::class.java)?.named("libs")
            
            // Safe version lookups with fallbacks
            val compileSdkVersion = versionCatalog?.findVersion("compile-sdk")?.map { it.requiredVersion.toInt() }?.orElse(36) ?: 36
            val minSdkVersion = versionCatalog?.findVersion("min-sdk")?.map { it.requiredVersion.toInt() }?.orElse(33) ?: 33
            val hiltVersion = versionCatalog?.findVersion("hilt")?.map { it.requiredVersion }?.orElse("2.59.2") ?: "2.59.2"
            val composeBomVersion = versionCatalog?.findVersion("compose-bom")?.map { it.requiredVersion }?.orElse("2026.03.01") ?: "2026.03.01"

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
                }
            }

            GenesisJvmConfig.configureKotlinJvm(project)
            GenesisCommonConfig.configure(project)

            // 5. Dependencies
            dependencies.apply {
                add("api", platform("androidx.compose:compose-bom:$composeBomVersion"))
                add("api", "androidx.compose.runtime:runtime")
                add("api", "androidx.compose.ui:ui")
                add("api", "androidx.compose.material3:material3")

                // Hilt Dependency Injection
                add("implementation", "com.google.dagger:hilt-android:$hiltVersion")
                add("ksp", "com.google.dagger:hilt-android-compiler:$hiltVersion")

                // YukiHook & Xposed
                val yukiDep = add("implementation", "com.highcapable.yukihookapi:api:1.3.1")
                (yukiDep as? org.gradle.api.artifacts.ExternalModuleDependency)?.exclude(
                    mapOf("group" to "com.highcapable.yukihookapi", "module" to "ksp-xposed")
                )
                add("ksp", "com.highcapable.yukihookapi:ksp-xposed:1.3.1")
                add("compileOnly", "de.robv.android.xposed:api:82")

                // Core
                add("implementation", "androidx.core:core-ktx:1.13.1")
                add("coreLibraryDesugaring", "com.android.tools:desugar_jdk_libs:2.1.5")
                add("implementation", "com.jakewharton.timber:timber:5.0.1")
            }
        }
    }
}
