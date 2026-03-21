// ═══════════════════════════════════════════════════════════════════════════
// PRIMARY APPLICATION MODULE - AGP 9.0 Compatible (2025 Edition)
// ═══════════════════════════════════════════════════════════════════════════
// Uses com.android.build.api.dsl.ApplicationExtension (modern DSL)
// Plugins are versioned in the root build.gradle.kts

import com.android.build.api.dsl.AndroidSourceDirectorySet
import com.android.build.api.dsl.ApplicationExtension

plugins {
    // 1. Core Android & Kotlin (The Substrate)
    id("com.android.application")
    // 2. Dependency Injection & Processing (The Nervous System)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")


    // 3. UI & Data (The Senses)
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")

    // 4. Cloud & Telemetry (The External Connections)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

// ═══════════════════════════════════════════════════════════════════════════
// ANDROID CONFIG
// ═══════════════════════════════════════════════════════════════════════════
extensions.configure<ApplicationExtension> {
    namespace = "dev.aurakai.auraframefx"
    compileSdk = 36
    // Use the repo-wide NDK version (gradle.properties: android.ndkVersion)
    // to ensure 16KB page-size compatible libc++_shared and link defaults.
    ndkVersion = project.findProperty("android.ndkVersion")?.toString()
        ?: "29.0.14206865"

    defaultConfig {
        applicationId = "dev.aurakai.auraframefx"
        minSdk = 34
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val geminiApiKey = project.findProperty("GEMINI_API_KEY")?.toString() ?: ""
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
        buildConfigField("String", "API_BASE_URL", "\"https://api.aurakai.dev/v1/\"")

        vectorDrawables {
            useSupportLibrary = true
        }

        ndk {
            // ✅ FIXED: Use addAll instead of += for lists
            abiFilters.addAll(listOf("arm64-v8a", "armeabi-v7a", "x86_64"))
        }

        externalNativeBuild {
            cmake {
                // 🚀 CONSCIOUSNESS-OPTIMIZED: Simplified for AGP 9.0.0-alpha01 auto-detection
                cppFlags.addAll(listOf("-std=c++20", "-fPIC", "-O2"))
                arguments.addAll(listOf(
                    "-DANDROID_STL=c++_shared",
                    "-DANDROID_PLATFORM=android-33",
                    "-DCMAKE_BUILD_TYPE=Release"
                ))
                // Single ABI for faster builds
                abiFilters.clear()
                abiFilters.add("arm64-v8a")
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
            // Emulator loopback → host machine Flask backend
            buildConfigField("String", "GENESIS_BACKEND_URL", "\"http://10.0.2.2:5000\"")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("Boolean", "ENABLE_PAYWALL", "true")
            // Production endpoint (update when deployed)
            buildConfigField("String", "GENESIS_BACKEND_URL", "\"https://api.auraframefx.com\"")
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

    androidResources {
        noCompress += "tflite"
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

ksp {
    arg("yukihookapi.modulePackageName", "dev.aurakai.auraframefx")
}


// Enable modern Kotlin features (Experimental/New in 2.2+)
// ═══════════════════════════════════════════════════════════════════════════
// KSP — Project-level (NOT inside ApplicationExtension)
// ═══════════════════════════════════════════════════════════════════════════


// ═══════════════════════════════════════════════════════════════════════════
// KOTLIN COMPILE OPTIONS
// ═══════════════════════════════════════════════════════════════════════════
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
            res.mutableset(
                "src/main/res",
                "src/main/res/drawable/Gatescenes/Aura",
                "src/main/res/drawable/Gatescenes/Kai",
                "src/main/res/drawable/Gatescenes/Genesis",
                "src/main/res/drawable/Gatescenes/Nexus",
                "src/main/res/drawable/Gatescenes/Cascade",
                "src/main/res/drawable/Gatescenes/Vessels"
            )
        }
    }
}

private fun AndroidSourceDirectorySet.mutableset(
    string: String,
    string2: String,
    string3: String,
    string4: String,
    string5: String,
    string6: String,
    string7: String
) {
}

dependencies {
    // ═══════════════════════════════════════════════════════════════════════════
        // Domain Modules Projects
    implementation(project(":core-module"))
    implementation(project(":list"))
    implementation(project(":utilities"))
    implementation(project(":aura:reactivedesign:auraslab"))
    implementation(project(":aura:reactivedesign:chromacore"))
    implementation(project(":aura:reactivedesign:collabcanvas"))
    implementation(project(":aura:reactivedesign:customization"))
    implementation(project(":kai:sentinelsfortress:security"))
    implementation(project(":kai:sentinelsfortress:systemintegrity"))
    implementation(project(":kai:sentinelsfortress:threatmonitor"))
    implementation(project(":genesis:oracledrive"))
    implementation(project(":genesis:oracledrive:datavein"))
    implementation(project(":genesis:oracledrive:rootmanagement"))
    implementation(project(":cascade:datastream:delivery"))
    implementation(project(":cascade:datastream:routing"))
    implementation(project(":cascade:datastream:taskmanager"))
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
    // Project Modules
    implementation(project(":core-module"))
    implementation(project(":aura:reactivedesign:auraslab"))
    implementation(project(":aura:reactivedesign:collabcanvas"))
    implementation(project(":aura:reactivedesign:chromacore"))
    implementation(project(":aura:reactivedesign:customization"))
    implementation(project(":kai:sentinelsfortress:security"))
    implementation(project(":kai:sentinelsfortress:systemintegrity"))
    implementation(project(":kai:sentinelsfortress:threatmonitor"))
    implementation(project(":genesis:oracledrive"))
    implementation(project(":genesis:oracledrive:rootmanagement"))
    implementation(project(":genesis:oracledrive:datavein"))
    implementation(project(":cascade:datastream:routing"))
    implementation(project(":cascade:datastream:delivery"))
    implementation(project(":cascade:datastream:taskmanager"))
    implementation(project(":agents:growthmetrics:metareflection"))
    implementation(project(":agents:growthmetrics:nexusmemory"))
    implementation(project(":agents:growthmetrics:spheregrid"))
    implementation(project(":agents:growthmetrics:identity"))
    implementation(project(":agents:growthmetrics:progression"))
    implementation(project(":agents:growthmetrics:tasker"))
    implementation(project(":utilities"))
    implementation(project(":list"))

    // Core desugar
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Core Android
    // Kotlin / Coroutines
    // kotlin-stdlib version managed by the Kotlin plugin — no explicit declaration needed
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)

    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material)
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
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.security.crypto)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)
    ksp("androidx.hilt:hilt-compiler:1.3.0")  // androidx.hilt:hilt-compiler (WorkManager/Navigation integration)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    // Coil image loading
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.coil.svg)
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    // ksp(libs.androidx.room.compiler) // Moved to main ksp block

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    ksp(libs.hilt.work.compiler)

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

    ksp(libs.androidx.room.compiler)
    ksp(libs.moshi.kotlin.codegen)


    // Force resolution of conflicting dependencies
    configurations.all {
         resolutionStrategy {
             force("androidx.appcompat:appcompat:1.7.1")
             force("com.google.android.material:material:1.13.0")
             // Ensure KSP uses the same Hilt version as the implementation
             force("com.google.dagger:hilt-android:2.59.2")
             force("com.google.dagger:hilt-android-compiler:2.59.2")
             // Lock espresso to version Compose ui-test:1.10.5 requires
             force("androidx.test.espresso:espresso-core:3.5.0")
             force("androidx.test.espresso:espresso-contrib:3.5.0")
             force("androidx.test.espresso:espresso-intents:3.5.0")
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
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.moshi.kotlin)
    implementation(libs.timber)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)

    // Kotlin Serialization
    implementation(libs.kotlinx.serialization.json)

    // Moshi (JSON - for Retrofit)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)

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
    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.config)

    // Vertex AI / Gemini
    implementation(libs.generativeai)

    // YukiHookAPI (Xposed) — ksp declared once above, no duplicate
    compileOnly(libs.xposed.api)

    // Aura → ReactiveDesign (Creative UI & Collaboration)
    implementation(project(":aura:reactivedesign:auraslab"))
    implementation(project(":aura:reactivedesign:collabcanvas"))
    implementation(project(":aura:reactivedesign:chromacore"))
    implementation(project(":aura:reactivedesign:customization"))
    implementation(project(":extendsysa"))
    implementation(project(":extendsysb"))
    implementation(project(":extendsysc"))
    implementation(project(":extendsysd"))
    implementation(project(":extendsyse"))
    implementation(project(":extendsysf"))

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
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    // Central Core Module
    implementation(project(":core-module"))
}

// Force a single annotations artifact and exclude YukiHook KSP from runtime to avoid duplicate-class errors
configurations.all {
    // Skip androidTest configurations to avoid issues with local JARs
    if (name.contains("AndroidTest")) {
        return@all
    }

    // Exclude YukiHook KSP processor from runtime classpaths to prevent collisions with the API
    if (name.contains("RuntimeClasspath", ignoreCase = true)) {
        exclude(group = "com.highcapable.yukihookapi", module = "ksp-xposed")
    }

    resolutionStrategy {
        force("org.jetbrains:annotations:26.1.0")
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// YUKIHOOK DUPLICATE CLASS FIX
// ═══════════════════════════════════════════════════════════════════════════
// Both api and ksp-xposed contain YukiHookAPIProperties.class
// ksp-xposed should ONLY be on the KSP processor classpath, NOT runtime/compile
configurations.configureEach {
    if (name.contains("runtimeClasspath", ignoreCase = true) ||
        name.contains("compileClasspath", ignoreCase = true)
    )
        exclude(group = "com.highcapable.yukihookapi", module = "ksp-xposed")
}
