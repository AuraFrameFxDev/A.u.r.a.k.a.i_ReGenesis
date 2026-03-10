# Complete Build System Configuration Guide

## Overview
This guide shows the complete, correct configuration for a multi-module Android project with 30+ modules using convention plugins, Hilt, KSP, and the version catalog.

---

## 1. Root `build.gradle.kts` - CRITICAL HILT WORKAROUND

**File: `/build.gradle.kts`**

```kotlin
// Root build.gradle.kts
// CRITICAL: Apply plugins with `apply false` to make them available on classpath
// for all subprojects. This is the Hilt/KSP workaround for multi-module projects.

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.compose.compiler) apply false
}

// Define common configuration for all subprojects
subprojects {
    // Configure resolution strategy for all subprojects
    configurations.all {
        resolutionStrategy {
            // Force the modern JetBrains annotations version
            force("org.jetbrains:annotations:26.0.2-1")

            // Prefer org.jetbrains over com.intellij for annotations
            eachDependency {
                if (requested.group == "com.intellij" && requested.name == "annotations") {
                    useTarget("org.jetbrains:annotations:26.0.2-1")
                    because("Avoid duplicate annotations classes")
                }
            }
        }

        // Exclude the old IntelliJ annotations from all dependencies
        exclude(group = "com.intellij", module = "annotations")
    }

    // Configure Java toolchain - Use Java 21 (LTS)
    pluginManager.withPlugin("java") {
        configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(21))
            }
        }
    }

    // Configure Kotlin JVM target
    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
            compilerOptions {
                jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
            }
        }
    }
}

// Root-level clean task
tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
```

---

## 2. Root `settings.gradle.kts`

**File: `/settings.gradle.kts`**

```kotlin
// Enable convention plugins from build-logic
pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
    }
}

// Add Foojay toolchain resolver for automatic JDK provisioning
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

// Central declaration of repositories for all modules
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }

        // Dynamically add every module's libs/ directory
        val libsDirs = rootDir.walkTopDown()
            .filter { it.isDirectory && File(it, "libs").exists() }
            .map { File(it, "libs") }
            .toSet()
        libsDirs.forEach { libsDir ->
            maven {
                url = uri(libsDir.toURI())
                metadataSources { artifact() }
            }
        }
    }
}

rootProject.name = "aurakai-reactive-intelligence"

// Include all modules
include(":app")
include(":core:common")
include(":core:data")
include(":core:domain")
include(":core:ui")
include(":core-module")
include(":feature-module")
include(":secure-comm")
include(":romtools")
include(":colorblendr")
include(":oracle-drive-integration")
include(":datavein-oracle-native")
include(":collab-canvas")
include(":list")
// ... add all 30+ modules here
```

---

## 3. `build-logic/settings.gradle.kts`

**File: `/build-logic/settings.gradle.kts`**

```kotlin
// build-logic/settings.gradle.kts

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"
```

---

## 4. `build-logic/build.gradle.kts`

**File: `/build-logic/build.gradle.kts`**

```kotlin
plugins {
    `kotlin-dsl`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    // Plugin dependencies for convention plugins
    // These allow the convention plugins to apply Android, Kotlin, Hilt, and KSP plugins
    implementation(libs.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.hilt.gradle.plugin)
    implementation(libs.ksp.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("genesisApplication") {
            id = "genesis.application"
            implementationClass = "plugins.GenesisApplicationPlugin"
        }
        register("genesisLibrary") {
            id = "genesis.library"
            implementationClass = "plugins.GenesisLibraryPlugin"
        }
        register("genesisBase") {
            id = "genesis.base"
            implementationClass = "plugins.GenesisBasePlugin"
        }
        register("genesisAndroidHilt") {
            id = "genesis.android.hilt"
            implementationClass = "plugins.AndroidHiltConventionPlugin"
        }
        register("genesisAndroidCompose") {
            id = "genesis.android.compose"
            implementationClass = "plugins.AndroidComposeConventionPlugin"
        }
    }
}
```

---

## 5. Convention Plugins

### GenesisApplicationPlugin

**File: `/build-logic/src/main/kotlin/plugins/GenesisApplicationPlugin.kt`**

```kotlin
package plugins

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

/**
 * Convention plugin for Android application modules
 * Applies common configuration for app modules
 */
class GenesisApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("genesis.base")
            }

            extensions.configure<ApplicationExtension> {
                defaultConfig {
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                    vectorDrawables.useSupportLibrary = true
                }

                buildTypes {
                    release {
                        isMinifyEnabled = true
                        isShrinkResources = true
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }
                }

                packaging {
                    resources {
                        excludes += "/META-INF/{AL2.0,LGPL2.1}"
                    }
                }
            }

            dependencies {
                // Common app dependencies
                add("implementation", libs.findLibrary("androidx-core-ktx").get())
                add("implementation", libs.findLibrary("androidx-lifecycle-runtime-ktx").get())
                add("implementation", libs.findLibrary("timber").get())
                add("coreLibraryDesugaring", libs.findLibrary("desugar-jdk-libs").get())
            }
        }
    }
}
```

### GenesisLibraryPlugin

**File: `/build-logic/src/main/kotlin/plugins/GenesisLibraryPlugin.kt`**

```kotlin
package plugins

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * Convention plugin for Android library modules
 */
class GenesisLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("genesis.base")
            }

            extensions.configure<LibraryExtension> {
                defaultConfig {
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }

                buildTypes {
                    release {
                        isMinifyEnabled = false
                        proguardFiles(
                            getDefaultProguardFile("proguard-android-optimize.txt"),
                            "proguard-rules.pro"
                        )
                    }
                }
            }

            dependencies {
                add("implementation", libs.findLibrary("androidx-core-ktx").get())
                add("implementation", libs.findLibrary("timber").get())
            }
        }
    }
}
```

### GenesisBasePlugin

**File: `/build-logic/src/main/kotlin/plugins/GenesisBasePlugin.kt`**

```kotlin
package plugins

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Base configuration shared by all Android modules
 */
class GenesisBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            extensions.configure<CommonExtension<*, *, *, *, *, *>> {
                compileSdk = libs.findVersion("compile-sdk").get().toString().toInt()

                defaultConfig {
                    minSdk = libs.findVersion("min-sdk").get().toString().toInt()
                }

                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_21
                    targetCompatibility = JavaVersion.VERSION_21
                    isCoreLibraryDesugaringEnabled = true
                }
            }

            // Configure Kotlin compilation
            tasks.withType<KotlinCompile>().configureEach {
                compilerOptions {
                    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
                }
            }
        }
    }
}
```

### AndroidHiltConventionPlugin

**File: `/build-logic/src/main/kotlin/plugins/AndroidHiltConventionPlugin.kt`**

```kotlin
package plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Applies Hilt dependency injection to Android modules
 */
class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.dagger.hilt.android")
                apply("com.google.devtools.ksp")
            }

            dependencies {
                add("implementation", libs.findLibrary("hilt-android").get())
                add("ksp", libs.findLibrary("hilt-compiler").get())
            }
        }
    }
}
```

### AndroidComposeConventionPlugin

**File: `/build-logic/src/main/kotlin/plugins/AndroidComposeConventionPlugin.kt`**

```kotlin
package plugins

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * Applies Jetpack Compose configuration to Android modules
 */
class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            extensions.configure<CommonExtension<*, *, *, *, *, *>> {
                buildFeatures {
                    compose = true
                }

                composeOptions {
                    kotlinCompilerExtensionVersion = libs.findVersion("composeCompiler").get().toString()
                }
            }

            dependencies {
                val composeBom = libs.findLibrary("androidx-compose-bom").get()
                add("implementation", platform(composeBom))
                add("androidTestImplementation", platform(composeBom))

                add("implementation", libs.findLibrary("androidx-activity-compose").get())
                add("implementation", libs.findLibrary("androidx-lifecycle-viewmodel-compose").get())
                add("implementation", libs.findLibrary("androidx-navigation-compose").get())
                add("implementation", libs.findLibrary("compose-material3").get())
                add("implementation", libs.findLibrary("compose-ui").get())
                add("implementation", libs.findLibrary("compose-ui-graphics").get())
                add("implementation", libs.findLibrary("compose-ui-tooling-preview").get())
                add("debugImplementation", libs.findLibrary("compose-ui-tooling").get())
            }
        }
    }
}
```

---

## 6. Example Module Configuration

### App Module

**File: `/app/build.gradle.kts`**

```kotlin
plugins {
    id("genesis.application")
    id("genesis.android.compose")
    id("genesis.android.hilt")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
}

android {
    namespace = "dev.aurakai.auraframefx"
    ndkVersion = libs.versions.ndk.get()

    defaultConfig {
        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++20"
                arguments += listOf(
                    "-DANDROID_STL=c++_shared",
                    "-DANDROID_PLATFORM=android-${libs.versions.min.sdk.get()}"
                )
            }
        }
    }

    lint {
        baseline = file("lint-baseline.xml")
        abortOnError = true
        checkReleaseBuilds = false
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = libs.versions.cmake.get()
        }
    }
}

dependencies {
    // Xposed & YukiHook
    compileOnly(libs.xposed.api)
    implementation(libs.yukihookapi.api)
    implementation(libs.kavaref.core)
    implementation(libs.kavaref.extension)
    ksp(libs.yukihookapi.ksp.xposed)

    // Feature modules
    implementation(project(":feature-module"))
    implementation(project(":secure-comm"))
    // ... other modules

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.crashlytics.ktx)

    // Debug tools
    debugImplementation(libs.leakcanary.android)
}
```

### Feature Library Module

**File: `/feature-module/build.gradle.kts`**

```kotlin
plugins {
    id("genesis.library")
    id("genesis.android.compose")
    id("genesis.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "dev.aurakai.auraframefx.feature"
}

dependencies {
    // Core modules
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:data"))

    // Module-specific dependencies
    implementation(libs.coil.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
}
```

---

## Key Points Summary

### ✅ CRITICAL: Hilt/KSP Workaround
The root `build.gradle.kts` MUST declare plugins with `apply false`:
```kotlin
plugins {
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    // ... other plugins
}
```

### ✅ Fixed YukiHookAPI Issues
- Removed `yuki = "1.3.0"` (OLD)
- Removed duplicate `yuApiClient`, `yuki-api`, `yuki-processor`
- Kept only `yukihookapi = "1.3.1"` with `yukihookapi-api` and `yukihookapi-ksp-xposed`

### ✅ Added NDK/CMake Configuration
- Added `ndkVersion = "28.2.12479018"`
- Added `externalNativeBuild` blocks for C++ support
- CMake version `3.22.1`

### ✅ Convention Plugins
- **genesis.application**: For app modules
- **genesis.library**: For library modules
- **genesis.base**: Shared base configuration
- **genesis.android.hilt**: Hilt DI
- **genesis.android.compose**: Jetpack Compose

### ✅ Clean Module Files
Module `build.gradle.kts` files are now 10-20 lines instead of 100+

---

## Build Commands

```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run tests
./gradlew test

# Check all modules
./gradlew check
```

---

This configuration provides:
- ✅ Proper Hilt/KSP support for 30+ modules
- ✅ Convention plugins for DRY configuration
- ✅ Type-safe version catalog
- ✅ Automatic JDK provisioning
- ✅ Configuration cache compatibility
- ✅ Scalable architecture
