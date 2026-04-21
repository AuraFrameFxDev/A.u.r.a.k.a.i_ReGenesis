import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

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

            val versionCatalog = extensions.getByType(org.gradle.api.artifacts.VersionCatalogsExtension::class.java).named("libs")

            val compileSdkVersion = versionCatalog.findVersion("compile-sdk").get().requiredVersion.toInt()
            val minSdkVersion = versionCatalog.findVersion("min-sdk").get().requiredVersion.toInt()

            val hiltVersion = versionCatalog.findVersion("hilt").get().requiredVersion
            val composeBomVersion = versionCatalog.findVersion("compose-bom").get().requiredVersion
            val yukihookVersion = versionCatalog.findVersion("yukihook").get().requiredVersion
            val xposedVersion = versionCatalog.findVersion("xposed").get().requiredVersion

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
            val versionCatalog =
                extensions.getByType(org.gradle.api.artifacts.VersionCatalogsExtension::class.java)
                    .named("libs")
            val hiltVersion = versionCatalog.findVersion("hilt").get().requiredVersion
            val composeBomVersion = versionCatalog.findVersion("compose-bom").get().requiredVersion

            dependencies.add("api", dependencies.platform("androidx.compose:compose-bom:$composeBomVersion"))
            dependencies.add("api", "androidx.compose.runtime:runtime")
            dependencies.add("api", "androidx.compose.ui:ui")
            dependencies.add("api", "androidx.compose.material3:material3")

                // Compose BOM — version from libs.versions.toml (single source of truth)
                add("api", platform("androidx.compose:compose-bom:$composeBomVersion"))
                add("api", "androidx.compose.runtime:runtime")
                add("api", "androidx.compose.ui:ui")
                add("api", "androidx.compose.material3:material3")

                // YukiHook & Xposed
                val yukiDep = add("implementation", "com.highcapable.yukihookapi:api:1.3.1")
                (yukiDep as? org.gradle.api.artifacts.ExternalModuleDependency)?.exclude(
                    mapOf("group" to "com.highcapable.yukihookapi", "module" to "ksp-xposed")
                )
                add("ksp", "com.highcapable.yukihookapi:ksp-xposed:1.3.1")
                add("compileOnly", "de.robv.android.xposed:api:82")

                // Core
                add("implementation", "androidx.core:core-ktx:1.17.0")
                add("coreLibraryDesugaring", "com.android.tools:desugar_jdk_libs:2.1.5")
                add("implementation", "com.jakewharton.timber:timber:5.0.1")
            }
        }
    }
}
