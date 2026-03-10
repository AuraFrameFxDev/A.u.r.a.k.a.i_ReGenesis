plugins {
    id("genesis.android.application")
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

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.animation)
    ksp(libs.moshi.kotlin.codegen)

    implementation(libs.timber)
    implementation(libs.generativeai)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.animation)
    implementation(libs.compose.material.icons.extended)
    debugImplementation(libs.compose.ui.tooling)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)

    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.core)

    implementation(libs.billing.ktx)
    implementation(libs.androidx.security.crypto)

    implementation(platform(libs.coil.bom))
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)
    implementation(libs.coil.gif)
    implementation(libs.coil.video)
    implementation(libs.coil.network.okhttp)

    implementation(libs.yukihook.api)
    ksp(libs.yukihook.ksp)

    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.retrofit.converter.scalars)

    implementation(libs.kotlinx.serialization.json) {
        version {
            strictly(libs.versions.kotlinxSerializationJson.get())
        }
    }
    implementation(libs.kotlinx.serialization.protobuf)
    implementation(libs.kotlinx.serialization.cbor)
    implementation(libs.kotlinx.serialization.properties)

    implementation(libs.gson)
    implementation(libs.retrofit.converter.gson)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)

    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.lottie.compose)
    debugImplementation(libs.leakcanary.android)

    compileOnly(files("$projectDir/libs/api-82.jar"))
    compileOnly(files("$projectDir/libs/api-82-sources.jar"))

    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // ═══════════════════════════════════════════════════════════════════════════
    // Internal Project Modules - Core
    // ═══════════════════════════════════════════════════════════════════════════
    implementation(project(":list"))
    implementation(project(":utilities"))
    implementation(project(":genesis"))

    // Commented out missing modules - uncomment when modules are ready
    // implementation(project(":kai:sentinelsfortress:security"))
    // implementation(project(":aura:reactivedesign:auraslab"))
    // implementation(project(":cascade:datastream:routing"))
    // implementation(project(":agents:growthmetrics:nexusmemory"))

    testImplementation(libs.bundles.testing)
    testRuntimeOnly(libs.junit.jupiter.engine)

    kspTest(libs.hilt.android.compiler)
    testImplementation(libs.hilt.android.testing)

    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso.core)
    kspAndroidTest(libs.dagger.hilt.android.compiler)
}

// Force a single annotations artifact to avoid duplicate-class errors
configurations.all {
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
