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
    /**
     * Configures the given Gradle project as an Android library module using the convention's defaults.
     *
     * Applies the Android library, Hilt, KSP, Compose, and Kotlin serialization plugins.
     * Configures the Android LibraryExtension (compile/NDK settings, defaultConfig, build types,
     * Java/compile options, build features, packaging, and lint), sets Kotlin JVM compilation options,
     * and adds the convention's standard dependencies (Hilt, Compose, YukiHook, etc.).
     *
     * @param project The Gradle project to configure as an Android library module.
     */
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply("com.android.library")
            pluginManager.apply("com.google.dagger.hilt.android")
            pluginManager.apply("com.google.devtools.ksp")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

            extensions.configure<LibraryExtension> {
                compileSdk = 36
                ndkVersion = "29.0.14206865"

                defaultConfig {
                    minSdk = 34
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

                // Java 25 bytecode (Firebase + AGP 9.0 compatible)
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
            }

            GenesisJvmConfig.configureKotlinJvm(project)
            GenesisCommonConfig.configure(project)

            // ═══════════════════════════════════════════════════════════════════════════
            // Auto-configured dependencies (provided by convention plugin)
            // ═══════════════════════════════════════════════════════════════════════════
            // Note: KSP configuration is handled by apply-yukihook-conventions.gradle.kts

            // ═══════════════════════════════════════════════════════════════════════
            // Versions read from libs.versions.toml — single source of truth
            // ═══════════════════════════════════════════════════════════════════════
            val versionCatalog =
                extensions.findByType(org.gradle.api.artifacts.VersionCatalogsExtension::class.java)
                    ?.named("libs")
            
            // Safe version lookup with fallbacks
            val hiltVersion = versionCatalog?.findVersion("hilt")?.map { it.requiredVersion }?.orElse("2.59.2") ?: "2.59.2"
            val composeBomVersion = versionCatalog?.findVersion("compose-bom")?.map { it.requiredVersion }?.orElse("2026.03.01") ?: "2026.03.01"

            // Hilt Dependency Injection
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

            // Core Library Desugaring (for Java 25 APIs on older Android)
            dependencies.add("coreLibraryDesugaring", "com.android.tools:desugar_jdk_libs:2.1.5")

            // Universal Xposed/LSPosed API access for all library modules
            dependencies.add("compileOnly", "de.robv.android.xposed:api:82")

            // YukiHook runtime (api only, ksp handled above)
            dependencies.add("implementation", "com.highcapable.yukihookapi:api:1.3.1")

            // KavaRef for modern reflection (YukiHook 2.0 replacement)
            dependencies.add("implementation", "com.highcapable.kavaref:kavaref-core:1.0.1")
            dependencies.add("implementation", "com.highcapable.kavaref:kavaref-extension:1.0.1")

            // EzXHelper for simplified Xposed development
            dependencies.add("implementation", "com.github.kyuubiran:EzXHelper:2.2.0")
        }
    }
}
