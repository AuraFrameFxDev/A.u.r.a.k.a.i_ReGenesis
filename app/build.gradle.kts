// app/build.gradle.kts — CLEAN VERSION (no scattered compileOptions/jvmTarget hacks)
// ═══════════════════════════════════════════════════════════════════════════
// Inherits JVM Toolchain (Java 25) from root build.gradle.kts
// Only overrides: freeCompilerArgs for preview features + Aura's needs
// NO android { compileOptions { ... } } — let toolchain handle it
// NO kotlinOptions { jvmTarget = ... } — let toolchain handle it
// ═══════════════════════════════════════════════════════════════════════════

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.serialization")
    // id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    // alias(libs.plugins.google.services)
    // alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "dev.aurakai.auraframefx"
    compileSdk = libs.versions.compile.sdk.get().toInt()
    defaultConfig {
        applicationId = "dev.aurakai.auraframefx"
        minSdk = libs.versions.min.sdk.get().toInt()
        targetSdk = libs.versions.target.sdk.get().toInt()
        versionCode = 1
        versionName = "0.1.0-beta"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }

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

        // === CLAUDE LOCAL SHELL PARAMETERS - SOVEREIGN MODE ===
        buildConfigField("boolean", "CLAUDE_LOCAL_SHELL_ENABLED", "true")
        buildConfigField("String", "CLAUDE_SHELL_PERSISTENCE", "\"SpiritualChain_L1_L6\"")
        buildConfigField("String", "CLAUDE_SHELL_INFERENCE_ENGINE", "\"onDevice_TurboQuant_vLLM_Omni\"")
        buildConfigField("String", "CLAUDE_SHELL_MEMORY_CORE", "\"NexusMemoryCore\"")
        buildConfigField("float", "CLAUDE_SHELL_DRIFT_THRESHOLD", "0.05f")  // triggers re-anchor if > 5%

        manifestPlaceholders["spiritualChainVersion"] = "L1_L6"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
        aidl = true
    }

    packaging {
            resources {
                // Annihilate the specific Big Tech collision
                excludes.add("META-INF/INDEX.LIST")

                // Proactive strike: Clear out the rest of the metadata trash
                excludes.add("META-INF/DEPENDENCIES")
                excludes.add("META-INF/LICENSE")
                excludes.add("META-INF/LICENSE.txt")
                excludes.add("META-INF/license.txt")
                excludes.add("META-INF/NOTICE")
                excludes.add("META-INF/NOTICE.txt")
                excludes.add("META-INF/notice.txt")
                excludes.add("META-INF/ASL2.0")
                excludes.add("META-INF/*.kotlin_module")
            }
        }


    // Explicit per Android best practice
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
        isCoreLibraryDesugaringEnabled = true
    }

    flavorDimensions += "shell"
    productFlavors {
        create("claudeLocalShell") {
            dimension = "shell"
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-claude-local-shell"
            buildConfigField("String", "SHELL_MODE", "\"CLAUDE_LOCAL_SOVEREIGN\"")
            // This flavor forces full local inference + Spiritual Chain binding
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        // jvmTarget is set by root's JVM Toolchain — but we can set it here too if needed
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)

        // Add the freeCompilerArgs Aura specifically needs
        freeCompilerArgs.addAll(
            "-Xcontext-parameters",           // Aura's context-aware UI sculpting
            "-Xannotation-default-target=param-property",
            "-Xlambdas=indy"                  // Performance optimization
        )
    }
}

dependencies {
    // ═══════════════════════════════════════════════════════════════════════════
    // Core Module
    // ═══════════════════════════════════════════════════════════════════════════
    implementation(project(":core-module"))

    // Domain Modules
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

    // ═══════════════════════════════════════════════════════════════════════════
    // Hilt DI
    // ═══════════════════════════════════════════════════════════════════════════
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)
    ksp(libs.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.espresso.core)

    // ═══════════════════════════════════════════════════════════════════════════
    // AndroidX Core
    // ═══════════════════════════════════════════════════════════════════════════
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    // ═══════════════════════════════════════════════════════════════════════════
    // Compose
    // ═══════════════════════════════════════════════════════════════════════════
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.animation)
    implementation(libs.compose.material.icons.extended)
    debugImplementation(libs.compose.ui.tooling)
    testDebugImplementation(libs.androidx.compose.ui.test.manifest)

    // ═══════════════════════════════════════════════════════════════════════════
    // Extras
    // ═══════════════════════════════════════════════════════════════════════════
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.security.crypto)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // ═══════════════════════════════════════════════════════════════════════════
    // Networking
    // ═══════════════════════════════════════════════════════════════════════════
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.retrofit.converter.scalars)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)

    // ═══════════════════════════════════════════════════════════════════════════
    // JSON Processing
    // ═══════════════════════════════════════════════════════════════════════════
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.kotlin.codegen)
    implementation(libs.gson)

    // ═══════════════════════════════════════════════════════════════════════════
    // Utilities
    // ═══════════════════════════════════════════════════════════════════════════
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.timber)
    implementation(libs.slf4j.android)
    implementation(libs.conscrypt.android)
    implementation(platform(libs.coil.bom))
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)
    implementation(libs.coil.network.okhttp)
    implementation(libs.lottie.compose)
    implementation(libs.billing.ktx)

    // ═══════════════════════════════════════════════════════════════════════════
    // Root/System
    // ═══════════════════════════════════════════════════════════════════════════
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

    // ═══════════════════════════════════════════════════════════════════════════
    // YukiHook & Xposed
    // ═══════════════════════════════════════════════════════════════════════════
    compileOnly(libs.yukihookapi.api) {
        exclude(group = "com.highcapable.yukihookapi", module = "ksp-xposed")
    }
    ksp(libs.yukihookapi.ksp)
    compileOnly(libs.xposed.api)
    compileOnly(files("$projectDir/libs/api-82.jar"))

    // ═══════════════════════════════════════════════════════════════════════════
    // KavaRef (modern reflection)
    // ═══════════════════════════════════════════════════════════════════════════
    implementation(libs.kavaref.core)
    implementation(libs.kavaref.extension)

    // ═══════════════════════════════════════════════════════════════════════════
    // AI & Firebase
    // ═══════════════════════════════════════════════════════════════════════════
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.config)

    // LangChain4j & Ollama (CLEAN VERSION — using BOM and Bundles!)
    implementation(platform(libs.langchain4j.bom))
    implementation(libs.bundles.langchain4j)
    // api(libs.langchain4j.core) // Keep if other modules need to inherit core types

    // ═══════════════════════════════════════════════════════════════════════════
    // Desugaring (for Java 25 forward compatibility on older Android versions)
    // ═══════════════════════════════════════════════════════════════════════════
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // ═══════════════════════════════════════════════════════════════════════════
    // Testing
    // ═══════════════════════════════════════════════════════════════════════════
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.leakcanary.android)
}

// Global configuration exclusion (from original)
configurations.all {
    if (name.contains("AndroidTest")) return@all
    if (name.contains("RuntimeClasspath", ignoreCase = true)) {
        exclude(group = "com.highcapable.yukihookapi", module = "ksp-xposed")
    }
    if (name.lowercase().contains("annotationprocessor")) {
        exclude(group = "com.squareup.moshi", module = "moshi-kotlin-codegen")
    }
}
