// ═══════════════════════════════════════════════════════════════════════════
// PRIMARY APPLICATION MODULE - AGP 9.0 Compatible (2025 Edition)
// ═══════════════════════════════════════════════════════════════════════════
// Uses com.android.build.api.dsl.ApplicationExtension (modern DSL)
// Plugins are versioned in the root build.gradle.kts

import com.android.build.api.dsl.ApplicationExtension

plugins {
    id("com.android.application")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")

}

// ═══════════════════════════════════════════════════════════════════════════
// ANDROID CONFIG
// ═══════════════════════════════════════════════════════════════════════════
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
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/NOTICE.md"
            excludes += "**/kotlin/**"
            excludes += "**/*.txt"
            // YukiHook: Pick first occurrence of duplicate class
            pickFirsts += "**/YukiHookAPIProperties.class"
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
}

// Enable modern Kotlin features (Experimental/New in 2.2+)
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
        freeCompilerArgs.addAll(
            "-Xcontext-parameters",
            "-Xannotation-default-target=param-property"
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

// ═══════════════════════════════════════════════════════════════════════════
// DEPENDENCIES
// ═══════════════════════════════════════════════════════════════════════════
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
    // ✅ Core library desugaring (Java 25 APIs)
    // ✅ Firebase BOM
    // ✅ Xposed API (compileOnly) + EzXHelper
    //
    // ⚠️ ONLY declare module-specific dependencies below!
    // ═══════════════════════════════════════════════════════════════════════════

    // Core desugar
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.dagger.hilt.android.compiler)

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
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
    implementation(libs.androidx.hilt.work)
    ksp("androidx.hilt:hilt-compiler:1.3.0")  // androidx.hilt:hilt-compiler (WorkManager/Navigation integration)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Room
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
    implementation(libs.libsu.nio)
    implementation(libs.libsu.service)

    // Shizuku & Rikka
    implementation(libs.shizuku.api)
    implementation(libs.shizuku.provider)
    implementation(libs.rikkax.core)
    implementation(libs.rikkax.core.ktx)
    implementation(libs.rikkax.material) {
        exclude(group = "dev.rikka.rikkax.appcompat", module = "appcompat")
    }

    // YukiHook API
    compileOnly(libs.yukihookapi.api) {
        exclude(group = "com.highcapable.yukihookapi", module = "ksp-xposed")
    }
    ksp(libs.yukihookapi.ksp)

    // Force resolution of conflicting dependencies
    configurations.all {
         resolutionStrategy {
             force("androidx.appcompat:appcompat:1.7.1")
             force("com.google.android.material:material:1.13.0")
         }
    }

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

    // Core Library Desugaring (Java 25 APIs)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // ═══════════════════════════════════════════════════════════════════════════
    // Firebase Ecosystem
    // ═══════════════════════════════════════════════════════════════════════════
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)

    // Vertex AI / Gemini
    implementation(libs.generativeai)

    // YukiHookAPI (Xposed)
    implementation(libs.yukihookapi.api)
    ksp(libs.yukihookapi.ksp)

    // LeakCanary (debug)
    debugImplementation(libs.leakcanary.android)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}

