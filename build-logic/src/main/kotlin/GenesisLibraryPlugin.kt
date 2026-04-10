import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * ===================================================================
 * GENESIS LIBRARY CONVENTION PLUGIN
 * ===================================================================
 *
 * Convention plugin for Android library modules.
 *
 * This plugin configures:
 * - Android library plugin and extensions
 * - Kotlin Android support with Compose compiler
 * - Jetpack Compose (built-in compiler with Kotlin 2.0+)
 * - Java 25 bytecode target (Firebase + AGP 9.0 compatible)
 * - Consistent build configuration across library modules
 *
 * Plugin Application Order:
 * 2. com.android.library
 * 3. org.jetbrains.kotlin.plugin.compose
 * 4. org.jetbrains.kotlin.plugin.serialization
 *
 * Note: Kotlin is built into AGP 9.0+ but applied explicitly for consistency.
 * Note: Hilt, KSP, and YukiHook support are applied by default to ensure DI consistency across all modules.
 *
 * @since Genesis Protocol 2.0 (AGP 9.0.0-alpha14 Compatible)
 */
class GenesisLibraryPlugin : Plugin<Project> {
    /**
     * Configures the given Gradle project with the convention defaults for an Android library module.
     *
     * Applies standard plugins (Android library, Hilt, KSP, Compose, Kotlin serialization), configures
     * the Android LibraryExtension (SDK, NDK, defaultConfig, buildTypes, compile/packaging/lint/build features),
     * delegates Kotlin JVM toolchain/compilation setup, and adds the convention's standard dependencies.
     *
     * @param project The Gradle project to configure as an Android library module.
     */
    override fun apply(project: Project) {
        with(project) {
            // Apply plugins in correct order
            // Note: Kotlin is built into AGP 9.0.0-alpha14+
            pluginManager.apply("com.android.library")
            pluginManager.apply("com.google.dagger.hilt.android")
            pluginManager.apply("com.google.devtools.ksp")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
            pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

            // ═══════════════════════════════════════════════════════════════════════
            // Versions read from libs.versions.toml — single source of truth
            // ═══════════════════════════════════════════════════════════════════════
            val versionCatalog =
                extensions.getByType(org.gradle.api.artifacts.VersionCatalogsExtension::class.java)
                    .named("libs")

            val compileSdkVersion = versionCatalog.findVersion("compile-sdk").get().requiredVersion.toInt()
            val minSdkVersion = versionCatalog.findVersion("min-sdk").get().requiredVersion.toInt()

            val hiltVersion = versionCatalog.findVersion("hilt").get().requiredVersion
            val composeBomVersion = versionCatalog.findVersion("compose-bom").get().requiredVersion
            val langchainVersion = versionCatalog.findVersion("langchain4j").get().requiredVersion

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

                // Java 25 bytecode (Centralized for Android modules)
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_25
                    targetCompatibility = JavaVersion.VERSION_25
                    isCoreLibraryDesugaringEnabled = true
                }

                // Note: kotlinOptions removed - using modern compilerOptions in tasks below

                buildFeatures {
                    compose = true
                    buildConfig = true
                    aidl = true
                }

                packaging {
                    resources {
                        excludes += setOf(
                            "/META-INF/{AL2.0,LGPL2.1}",
                            "/META-INF/LICENSE*",
                            "/META-INF/NOTICE*"
                        )
                    }
                }

                lint {
                    baseline = file("lint-baseline.xml")
                    abortOnError = false
                    checkReleaseBuilds = false
                }
            }

            // Configure Kotlin JVM toolchain and compilation options
            GenesisJvmConfig.configureKotlinJvm(project)

            // ═══════════════════════════════════════════════════════════════════════════
            // Auto-configured dependencies (provided by convention plugin)
            // ═══════════════════════════════════════════════════════════════════════════

            // Hilt Dependency Injection
            dependencies.add("implementation", "com.google.dagger:hilt-android:$hiltVersion")
            dependencies.add("ksp", "com.google.dagger:hilt-android-compiler:$hiltVersion")

            // Compose UI stack (Total Coverage for Genesis modules)
            dependencies.add(
                "api",
                dependencies.platform("androidx.compose:compose-bom:$composeBomVersion")
            )
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

            // Core Android libraries
            dependencies.add("implementation", "androidx.core:core-ktx:1.17.0")
            dependencies.add("implementation", "androidx.appcompat:appcompat:1.7.1")

            // Kotlin Coroutines
            dependencies.add("implementation", "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
            dependencies.add("implementation", "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

            // Kotlin Serialization
            dependencies.add("implementation", "org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

            // Timber Logging
            dependencies.add("implementation", "com.jakewharton.timber:timber:5.0.1")

            // AI — LangChain4j (using BOM and bundle)
            dependencies.add(
                "api",
                dependencies.platform("dev.langchain4j:langchain4j-bom:$langchainVersion")
            )
            val langchainBundle = versionCatalog.findBundle("langchain4j").get()
            dependencies.add("api", langchainBundle)

            // Core Library Desugaring (for Java 25 APIs on older Android)
            dependencies.add("coreLibraryDesugaring", "com.android.tools:desugar_jdk_libs:2.1.5")

            // KavaRef for modern reflection
            dependencies.add("implementation", "com.highcapable.kavaref:kavaref-core:1.0.1")
            dependencies.add("implementation", "com.highcapable.kavaref:kavaref-extension:1.0.1")
        }
    }
}
