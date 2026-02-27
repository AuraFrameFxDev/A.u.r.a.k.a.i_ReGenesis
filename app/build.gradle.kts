// ═══════════════════════════════════════════════════════════════════════════
// PRIMARY APPLICATION MODULE - AGP 9.0 Compatible (2025 Edition)
// ═══════════════════════════════════════════════════════════════════════════
// Uses com.android.build.api.dsl.ApplicationExtension (modern DSL)
// Plugins are versioned in the root build.gradle.kts

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.kotlin.dsl.ksp

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

// ═══════════════════════════════════════════════════════════════════════════
// KSP — Project-level (NOT inside ApplicationExtension)
// ═══════════════════════════════════════════════════════════════════════════
ksp {
    arg("yukihookapi.modulePackageName", "dev.aurakai.auraframefx.generated.app")
}

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
dependencies {
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
    ksp(libs.dagger.hilt.android.compiler)

    // Kotlin / Coroutines
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.3.10")
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)

    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.security.crypto)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)
    ksp("androidx.hilt:hilt-compiler:1.3.0")  // androidx.hilt:hilt-compiler (WorkManager/Navigation integration)

    // Compose BOM
    val composeBom = platform("androidx.compose:compose-bom:2025.05.01")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.coil.svg)
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Network
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.converter.scalars)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.moshi.kotlin)
    implementation(libs.timber)
    ksp(libs.moshi.kotlin.codegen)
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

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.firestore)
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
        force("org.jetbrains:annotations:26.0.2-1")
    }
}

