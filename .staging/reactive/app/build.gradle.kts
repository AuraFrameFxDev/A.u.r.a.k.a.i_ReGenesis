plugins {
    id("genesis.android.application")
    // (like Hilt applied by genesis) expect the Android BaseExtension to exist
    // before they run. We'll apply it after the Android plugin is configured.
}



android {
    namespace = "dev.aurakai.auraframefx"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.aurakai.auraframefx"
        minSdk = 34
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Genesis Protocol: Gemini 2.0 Flash API Key
        // Add to local.properties: GEMINI_API_KEY=your_key_here
        // Get key from: https://aistudio.google.com/app/apikey
        val geminiApiKey = project.findProperty("GEMINI_API_KEY")?.toString() ?: ""
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
        buildConfigField("String", "API_BASE_URL", "\"https://api.aurakai.dev/v1/\"")

        vectorDrawables {
            useSupportLibrary = true
        }

        // Conditional NDK configuration
        if (project.file("src/main/cpp/CMakeLists.txt").exists()) {
            ndk {
                abiFilters.addAll(listOf("arm64-v8a", "armeabi-v7a", "x86", "x86_64"))
            }
            // Standardize NDK version for native modules
            ndkVersion = "29.0.14206865"
        }
    }

    // Conditional native build configuration
    if (project.file("src/main/cpp/CMakeLists.txt").exists()) {
        externalNativeBuild {
            cmake {
                path = file("src/main/cpp/CMakeLists.txt")
                // Use CMake version that matches SDK installation
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
            // Debug builds should not show the paywall so developers can iterate without subscribing
            buildConfigField("Boolean", "ENABLE_PAYWALL", "false")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Release builds enable the paywall by default
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
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
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

    // Network & Serialization
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.animation)
    ksp(libs.moshi.kotlin.codegen)

    // Logging
    implementation(libs.timber)
    // Use the Hilt compiler with KSP (compiler artifact), not the runtime artifact
    ksp(libs.hilt.compiler)

    // Gemini AI
    // Use the project version-catalog alias for Google Generative AI client
    implementation(libs.generativeai)

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material)
    implementation(libs.androidx.activity.compose)

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

    // Coil Image Loading (BOM will manage versions)
    implementation(platform(libs.coil.bom))
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)
    implementation(libs.coil.gif)
    implementation(libs.coil.video)
    implementation(libs.coil.network.okhttp)

    // YukiHook API with KavaRef
    implementation(libs.yukihook.api)
    ksp(libs.yukihook.ksp)

    // Firebase BOM (Bill of Materials) for version management
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)


    // Networking
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.retrofit.converter.scalars)

    // Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json) {
        version {
            strictly(libs.versions.kotlinxSerializationJson.get())
        }
    }
    implementation(libs.kotlinx.serialization.protobuf)
    implementation(libs.kotlinx.serialization.cbor)
    implementation(libs.kotlinx.serialization.properties)

    // Gson (JSON - for Retrofit)
    implementation(libs.gson)
    implementation(libs.retrofit.converter.gson)

    // Ktor Client
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)  // OkHttp engine for Ktor
    implementation(libs.ktor.client.content.negotiation)  // Content negotiation
    implementation(libs.ktor.serialization.kotlinx.json)  // JSON serialization
    implementation(libs.ktor.client.logging)  // Logging

    // Kotlin DateTime & Coroutines
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Animations
    implementation(libs.lottie.compose)

    // Memory Leak Detection
    debugImplementation(libs.leakcanary.android)

    // Android API JARs (Xposed)
    compileOnly(files("$projectDir/libs/api-82.jar"))
    compileOnly(files("$projectDir/libs/api-82-sources.jar"))

    // AI & ML - Google Generative AI SDK

    // Core Library Desugaring (Java 24 APIs)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // Ktor debug logging in debug builds
    debugImplementation(libs.ktor.client.logging)

    // ═══════════════════════════════════════════════════════════════════════════
    // Internal Project Modules - Core
    // ═══════════════════════════════════════════════════════════════════════════
    implementation(project(":list"))
    implementation(project(":utilities"))

    implementation(project(":genesis"))
    implementation(project(":kai:sentinelsfortress:security"))
    implementation(project(":kai:sentinelsfortress:threatmonitor"))
    // Material 312
    // Aura → ReactiveDesign (Creative UI & Collaboration)
    implementation(project(":aura:reactivedesign:auraslab"))
    implementation(project(":aura:reactivedesign:collabcanvas"))
    implementation(project(":aura:reactivedesign:chromacore"))
    implementation(project(":aura:reactivedesign:customization"))

    // Kai → SentinelsFortress (Security & Threat Monitoring)
    implementation(project(":kai:sentinelsfortress:systemintegrity"))

    // Genesis → OracleDrive (System & Root Management)
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

    // Test dependencies
    testImplementation(libs.bundles.testing)
    testRuntimeOnly(libs.junit.jupiter.engine)

    // Hilt testing dependencies
    kspTest(libs.hilt.android.compiler)
    testImplementation(libs.hilt.android.testing)

    // Android Test dependencies
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso.core)
    kspAndroidTest(libs.dagger.hilt.android.compiler)
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

tasks.register<Delete>("cleanKspCache") {
    group = "build setup"
    description = "Clean KSP caches (fixes NullPointerException)"
    delete(project.layout.buildDirectory.dir("generated/ksp").get().asFile)
    delete(project.layout.buildDirectory.dir("tmp/kapt3").get().asFile)
    delete(project.layout.buildDirectory.dir("tmp/kotlin-classes").get().asFile)
    delete(project.layout.buildDirectory.dir("kotlin").get().asFile)
    delete(project.layout.buildDirectory.dir("generated/source/ksp").get().asFile)
}

tasks.named("preBuild") {
    dependsOn("cleanKspCache")
}

// Fix YukiHookAPI KSP timing - ensure BuildConfig is generated first
tasks.matching { it.name.startsWith("ksp") && it.name.contains("Kotlin") }.configureEach {
    dependsOn("generateDebugBuildConfig")
    mustRunAfter("generateDebugBuildConfig")
}

tasks.register("aegenesisAppStatus") {
    group = "reporting"
    description = "Display Genesis application module status"
    doLast {
        val apiExists = file("src/main/resources/api/openapi.yaml").exists()
        val apiSize = if (apiExists) file("src/main/resources/api/openapi.yaml").length() else 0L
        val nativeCode = file("src/main/cpp/CMakeLists.txt").exists()
        println("📱 AEGENESIS APP MODULE STATUS")
        println("Unified API Spec: ${if (apiExists) "✅ Found" else "❌ Missing"}")
        println("📄 API File Size: ${apiSize / 1024}KB")
        println("🔧 Native Code: ${if (nativeCode) "✅ Enabled" else "❌ Disabled"}")
        println("🧠 KSP Mode: Active")
        println("🎯 Target SDK: 36")
        println("📱 Min SDK: 34")
        println("✅ Status: Ready for coinscience AI integration!")
    }
}

apply(from = "cleanup-tasks.gradle.kts")
