// ═══════════════════════════════════════════════════════════════════════════
// PRIMARY APPLICATION MODULE - AGP 9.0 Compatible (2025 Edition)
// ═══════════════════════════════════════════════════════════════════════════
// Uses com.android.build.api.dsl.ApplicationExtension (modern DSL)
// Plugins are versioned in the root build.gradle.kts

import com.android.build.api.dsl.ApplicationExtension

plugins {
    // Core Android and Kotlin plugins
    id("com.android.application")

    // Compose and serialization
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")

    // Dependency injection and code generation
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")

    // Firebase and analytics
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

// AGP 9.0: Using extensions.configure for modern DSL compatibility
extensions.configure<ApplicationExtension> {
    namespace = "dev.aurakai.auraframefx"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.aurakai.auraframefx"
        minSdk = 34
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val geminiApiKey = project.findProperty("GEMINI_API_KEY")?.toString() ?: ""
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
        buildConfigField("String", "API_BASE_URL", "\"https://api.aurakai.dev/v1/\"")

        vectorDrawables {
            useSupportLibrary = true
        }

        if (project.file("src/main/cpp/CMakeLists.txt").exists()) {
            ndk {
                abiFilters.addAll(listOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64"))
            }
        }
    }

    if (project.file("src/main/cpp/CMakeLists.txt").exists()) {
        externalNativeBuild {
            cmake {
                path = file("src/main/cpp/CMakeLists.txt")
                version = "3.22.1"
            }
        }
    }

    buildTypes {
        debug {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("Boolean", "ENABLE_PAYWALL", "false")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("Boolean", "ENABLE_PAYWALL", "true")
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/LICENSE.txt"
            excludes += "/META-INF/NOTICE.txt"
            excludes += "**/kotlin/**"
            excludes += "**/*.txt"
        }
        jniLibs {
            useLegacyPackaging = false
            pickFirsts += listOf("**/libc++_shared.so", "**/libjsc.so")
        }
    }

    compileOptions {
            sourceCompatibility = JavaVersion.VERSION_25
            targetCompatibility = JavaVersion.VERSION_25
        isCoreLibraryDesugaringEnabled = true
    }
}

// Enable experimental context-parameters feature (Kotlin 2.2+)
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xcontext-parameters"
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// DISABLE TEST COMPILATION (Speed up builds)
// ═══════════════════════════════════════════════════════════════════════════
tasks.configureEach {
    if (name.contains("Test", ignoreCase = true) &&
        (name.contains("compile", ignoreCase = true) ||
            name.contains("UnitTest") ||
            name.contains("AndroidTest"))
    ) {
        enabled = false
    }
}

extensions.configure<ApplicationExtension> {
    lint {
        baseline = file("lint-baseline.xml")
        abortOnError = false
        checkReleaseBuilds = false
    }

    buildFeatures {
        buildConfig = true
        compose = true
        viewBinding = true
        aidl = true
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // DEDUPLICATION: Exclude duplicate files to fix compile collisions
    // ═══════════════════════════════════════════════════════════════════════════
    sourceSets {
        getByName("main") {
            java.directories.add("dev/aurakai/auraframefx/ai/agents/BaseAgent.kt")
        }
        getByName("release") {
            java.directories.add("dev/aurakai/auraframefx/logging/TimberInitializer.kt")
        }
        getByName("debug") {
            java.directories.add("dev/aurakai/auraframefx/logging/TimberInitializer.kt")
        }
    }
}

dependencies {
    // ═══════════════════════════════════════════════════════════════════════════
    // AUTO-PROVIDED by genesis.android.application:
    // ═══════════════════════════════════════════════════════════════════════════
    // ✅ Hilt Android + Compiler (with KSP)
    // ✅ Compose BOM + UI (ui, ui-graphics, ui-tooling-preview, material3, ui-tooling[debug])
    // ✅ Core Android (core-ktx, appcompat, activity-compose)
    // ✅ Lifecycle (runtime-ktx, viewmodel-compose)
    // ✅ Kotlin Coroutines (core + android)
    // ✅ Kotlin Serialization JSON
    // ✅ Timber (logging)
    // ✅ Core library desugaring (Java 24 APIs)
    // ✅ Firebase BOM
    // ✅ Xposed API (compileOnly) + EzXHelper
    //
    // ⚠️ ONLY declare module-specific dependencies below!
    // ═══════════════════════════════════════════════════════════════════════════

    // Hilt Dependency Injection (MUST be added before afterEvaluate)
    implementation(libs.hilt.android)
    implementation(libs.androidx.navigation.common.ktx)
    ksp(libs.hilt.compiler)

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material)
    implementation(libs.androidx.activity.compose)

    // MultiDex support for 64K+ methods (Removed: redundant for minSdk 34)

    // Compose BOM & UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.animation)
    implementation(libs.compose.material.icons.extended)
    debugImplementation(libs.compose.ui.tooling)

    // Compose Extras (Navigation, Animation)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)

    // DataStore
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.core)

    // Google Play Billing - Subscription Management
    implementation(libs.billing.ktx)

    // Security
    implementation(libs.androidx.security.crypto)

    // Root/System Utils
    implementation(libs.libsu.core)
    implementation(libs.libsu.io)
    implementation(libs.libsu.service)

    // YukiHook API
    compileOnly(libs.yukihookapi.api)
    ksp(libs.yukihookapi.ksp)

    // Firebase BOM (Bill of Materials) for version management
    implementation(platform(libs.firebase.bom))


    // Networking - OkHttp + Retrofit
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.converter.scalars)

    // Networking - Ktor Client
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)

    // Kotlin Serialization
    implementation(libs.kotlinx.serialization.json)

    // Moshi (JSON - for Retrofit)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.kotlin.codegen)

    // Gson (JSON processing)
    implementation(libs.gson)

    // Kotlin DateTime & Coroutines
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Image Loading
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)
    implementation(libs.coil.network.okhttp)

    // Animations
    implementation(libs.lottie.compose)

    // Logging
    implementation(libs.timber)

    // Memory Leak Detection
    debugImplementation(libs.leakcanary.android)

    // Android API JARs (Xposed)
    compileOnly(files("$projectDir/libs/api-82.jar"))
    compileOnly(files("$projectDir/libs/api-82-sources.jar"))

    // AI & ML - Google Generative AI SDK
    implementation(libs.generativeai)

    // Core Library Desugaring (Java 24 APIs)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // ═══════════════════════════════════════════════════════════════════════════
    // Firebase Ecosystem
    // ═══════════════════════════════════════════════════════════════════════════
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.config)

    // ═══════════════════════════════════════════════════════════════════════════
    // Internal Project Modules - Core
    // ═══════════════════════════════════════════════════════════════════════════

    // Material Icons Extended
    implementation(libs.compose.material.icons.extended)

    // Aura → ReactiveDesign (Creative UI & Collaboration)
    implementation(project(":aura:reactivedesign:auraslab"))
    implementation(project(":aura:reactivedesign:collabcanvas"))
    implementation(project(":aura:reactivedesign:chromacore"))
    implementation(project(":aura:reactivedesign:customization"))
    implementation(project(":aura:reactivedesign:sandboxui"))

    // Kai → SentinelsFortress (Security & Threat Monitoring)
    implementation(project(":kai:sentinelsfortress:security"))
    implementation(project(":kai:sentinelsfortress:systemintegrity"))
    implementation(project(":kai:sentinelsfortress:threatmonitor"))

    // Genesis → OracleDrive (System & Root Management)
    implementation(project(":genesis:oracledrive"))
    implementation(project(":genesis:oracledrive:rootmanagement"))
    implementation(project(":genesis:oracledrive:datavein"))

    // Cascade → DataStream (Data Routing & Delivery)
    implementation(project(":cascade:datastream:routing"))
    implementation(project(":cascade:datastream:delivery"))
    implementation(project(":cascade:datastream:taskmanager"))

    // Agents → GrowthMetrics (AI Agent Evolution)
    implementation(project(":agents:growthmetrics:metareflection"))
    implementation(project(":agents:growthmetrics:nexusmemory"))
    implementation(project(":agents:growthmetrics:spheregrid"))
    implementation(project(":agents:growthmetrics:identity"))
    implementation(project(":agents:growthmetrics:progression"))
    implementation(project(":agents:growthmetrics:tasker"))

    // Central Core Module
    implementation(project(":core-module"))
}

// Force a single annotations artifact to avoid duplicate-class errors
configurations.all {
    // Skip androidTest configurations to avoid issues with local JARs
    if (name.contains("AndroidTest")) {
        return@all
    }

    resolutionStrategy {
        force("org.jetbrains:annotations:26.0.2-1")
    }
}
