// app/build.gradle.kts — REGENESIS ARCHITECTURAL SUBSTRATE
// ═══════════════════════════════════════════════════════════════════════════

import com.android.build.api.dsl.ApplicationExtension

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.firebaseCrashlytics)
}

configurations.all {
    resolutionStrategy {
        val protobufVersion = libs.versions.protobuf.get()
        val okhttpVersion = libs.versions.okhttp.get()

        force("com.google.protobuf:protobuf-java:$protobufVersion")
        force("com.google.api.grpc:proto-google-common-protos:2.59.0")
        force("com.squareup.okhttp3:okhttp-android:$okhttpVersion")
        
        exclude(group = "com.google.protobuf", module = "protobuf-lite")
        exclude(group = "com.google.protobuf", module = "protobuf-javalite")
        exclude(group = "com.google.firebase", module = "protolite-well-known-types")
        exclude(group = "com.squareup.okhttp3", module = "okhttp-jvm")
    }
}

extensions.configure<ApplicationExtension> {
    namespace = "dev.aurakai.auraframefx"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.aurakai.auraframefx"
        minSdk = 34
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Multidex required for large app
        multiDexEnabled = true

        // Genesis Protocol - Build Configuration Constants
        buildConfigField(
            "String",
            "GEMINI_API_KEY",
            "\"${project.findProperty("GEMINI_API_KEY") ?: ""}\""
        )
        buildConfigField(
            "String",
            "OLLAMA_BASE_URL",
            "\"${project.findProperty("OLLAMA_BASE_URL") ?: "http://localhost:11434"}\""
        )
        buildConfigField(
            "String",
            "VERTEX_PROJECT_ID",
            "\"${project.findProperty("VERTEX_PROJECT_ID") ?: ""}\""
        )
        buildConfigField(
            "String",
            "GENESIS_BACKEND_URL",
            "\"${project.findProperty("GENESIS_BACKEND_URL") ?: "http://localhost:8000"}\""
        )
        buildConfigField(
            "String",
            "OAUTH_SERVER_CLIENT_ID",
            "\"${project.findProperty("OAUTH_SERVER_CLIENT_ID") ?: ""}\""
        )
        buildConfigField(
            "String",
            "AURA_BACKEND_WS_URL",
            "\"${project.findProperty("AURA_BACKEND_WS_URL") ?: "wss://api.aurakai.dev/ws"}\""
        )
        buildConfigField(
            "String",
            "OPENROUTER_API_KEY",
            "\"${project.findProperty("OPENROUTER_API_KEY") ?: "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"}\""
        )

        // Fail-safe flags for VertexAI / Gemini activation
        buildConfigField("boolean", "ENABLE_GEMINI", "false")
        buildConfigField("String", "VERTEX_LOCATION", "\"us-central1\"")
        buildConfigField("String", "GEMINI_MODEL", "\"gemini-1.5-flash\"")

        // Claude Local Shell Parameters
        buildConfigField("boolean", "CLAUDE_LOCAL_SHELL_ENABLED", "true")
        buildConfigField("String", "CLAUDE_SHELL_PERSISTENCE", "\"SpiritualChain_L1_L6\"")
        buildConfigField(
            "String",
            "CLAUDE_SHELL_INFERENCE_ENGINE",
            "\"onDevice_TurboQuant_vLLM_Omni\""
        )
        buildConfigField("String", "CLAUDE_SHELL_MEMORY_CORE", "\"NexusMemoryCore\"")
        buildConfigField("float", "CLAUDE_SHELL_DRIFT_THRESHOLD", "0.05f")
    }

    buildFeatures {
        buildConfig = true
        compose = true
        viewBinding = true
        dataBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        isCoreLibraryDesugaringEnabled = true
    }

    // NDK version for native compilation - required for AGP 9.x
    ndkVersion = "27.0.12077973"

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    packaging {
        jniLibs { useLegacyPackaging = false }
        resources {
            excludes.addAll(
                listOf(
                    "META-INF/INDEX.LIST",
                    "META-INF/DEPENDENCIES",
                    "META-INF/LICENSE",
                    "META-INF/LICENSE.txt",
                    "META-INF/NOTICE",
                    "META-INF/*.kotlin_module",
                    "google/type/color.proto",
                    "google/type/datetime.proto",
                    "google/type/dayofweek.proto",
                    "google/type/money.proto",
                    "google/type/postal_address.proto",
                    "google/type/timeofday.proto",
                    "google/api/*.proto",
                    "google/rpc/*.proto",
                    "google/cloud/audit/*.proto",
                    "google/logging/type/*.proto",
                    "google/longrunning/*.proto",
                    "google/geo/type/*.proto",
                    "google/protobuf/*.proto"
                )
            )
            pickFirsts.add("**/YukiHookAPIProperties.class")
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("RELEASE_STORE_FILE") ?: "release.keystore")
            storePassword = System.getenv("RELEASE_STORE_PASSWORD") ?: ""
            keyAlias = System.getenv("RELEASE_KEY_ALIAS") ?: "release"
            keyPassword = System.getenv("RELEASE_KEY_PASSWORD") ?: ""
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            // Keep Application class in main dex for multidex
            multiDexKeepProguard = file("multidex-config.pro")
        }
    }

    flavorDimensions += "shell"
    productFlavors {
        create("claudeLocalShell") {
            dimension = "shell"
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-claude-local-shell"
            buildConfigField("String", "SHELL_MODE", "\"CLAUDE_LOCAL_SOVEREIGN\"")
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.addAll("-Xcontext-parameters")
    }
}

ksp {
    arg("dagger.fastInit", "enabled")
    arg("hilt.android.useClassPathAggregation", "true")
    arg("room.generateKotlin", "true")
}

dependencies {
    // Project Modules
    implementation(project(":core-module"))
    implementation(project(":trinity:aura"))
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
    implementation(project(":agents:growthmetrics:metareflection"))
    implementation(project(":agents:growthmetrics:nexusmemory"))
    implementation(project(":agents:growthmetrics:spheregrid"))
    implementation(project(":agents:growthmetrics:identity"))
    implementation(project(":agents:growthmetrics:progression"))
    implementation(project(":agents:growthmetrics:tasker"))
    implementation(project(":extendsysa"))
    implementation(project(":extendsysb"))
    implementation(project(":extendsysc"))
    implementation(project(":extendsysd"))
    implementation(project(":extendsyse"))
    implementation(project(":extendsysf"))
    implementation(project(":utilities"))
    implementation(project(":list"))
    implementation(libs.androidx.compose.foundation.layout)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.espresso.core)
    // YukiHookAPI
    implementation(libs.yukihookapi.api)
    ksp(libs.yukihookapi.ksp)

    // Animation & Visual Effects
    implementation("com.airbnb.android:lottie-compose:6.3.0")

    // AndroidX & Jetpack
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.security.crypto)
    implementation(libs.bundles.datastore)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose.ui)
    implementation(libs.androidx.compose.material.icons.extended)
    debugImplementation(libs.bundles.compose.tooling)

    // Room
    implementation(libs.bundles.room)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.work.runtime.ktx)

    // Networking & Serialization
    implementation(libs.bundles.networking.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.converter.scalars)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.bundles.networking.ktor)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)
    implementation(libs.coil.network.okhttp)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    // AI / LangChain4j (Weaponized local substrate)
    implementation(libs.generativeai)
    implementation(platform(libs.langchain4j.bom))
    implementation(libs.langchain4j.core) {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    implementation(libs.langchain4j.vertex.ai.gemini)
    implementation(libs.langchain4j.http.client.okhttp)
    implementation(libs.langchain4j.ollama)
    implementation(libs.langchain4j.open.ai)
    implementation(libs.langchain4j.google.ai.gemini)

    // Utilities & Kernel level UI
    implementation(libs.bundles.utilities)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.timber)
    implementation(libs.lottie.compose)
    implementation(libs.billing.ktx)

    // System Sovereignty
    implementation(libs.libsu.core)
    implementation(libs.libsu.nio)
    implementation(libs.libsu.service)
    implementation(libs.shizuku.api)
    implementation(libs.shizuku.provider)
    implementation(libs.rikkax.core)
    implementation(libs.rikkax.core.ktx)
    implementation(libs.rikkax.material) {
        exclude(group = "dev.rikka.rikkax.appcompat", module = "appcompat")
    }
    compileOnly(libs.xposed.api)

    // KavaRef
    implementation(libs.kavaref.core)
    implementation(libs.kavaref.extension)

    // Testing
    testImplementation(libs.bundles.testing)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.leakcanary.android)
}
