// app/build.gradle.kts — CLEAN VERSION (no scattered compileOptions/jvmTarget hacks)
// ═══════════════════════════════════════════════════════════════════════════
// Inherits JVM Toolchain (Java 25) from root build.gradle.kts
// Only overrides: freeCompilerArgs for preview features + Aura's needs
// NO android { compileOptions { ... } } — let toolchain handle it
// NO kotlinOptions { jvmTarget = ... } — let toolchain handle it
// ═══════════════════════════════════════════════════════════════════════════

plugins {
    id("com.android.application")
    // id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "dev.aurakai.auraframefx"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.aurakai.auraframefx"
        minSdk = 34
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0-beta"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            pickFirsts += listOf(
                "**/YukiHookAPIProperties.class",
                "META-INF/proguard/androidx-*.pro"
            )
        }
    }

    // Explicit per Android best practice
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
        isCoreLibraryDesugaringEnabled = true
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// MINIMAL Kotlin compilation overrides: Only freeCompilerArgs
// The toolchain in root already sets the jvmTarget and languageVersion
// ═══════════════════════════════════════════════════════════════════════════

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        // jvmTarget is set by root's JVM Toolchain — don't override
        // languageVersion is managed by Kotlin plugin — don't override

        // ONLY add the freeCompilerArgs Aura specifically needs:
        freeCompilerArgs.addAll(
            "-Xcontext-parameters",           // Aura's context-aware UI sculpting
            "-Xannotation-default-target=param-property",
            "-Xlambdas=indy",                 // Performance optimization
            "-Xjvm-enable-preview"            // Java 25 preview features
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
    implementation(libs.okhttp)
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

    // ═══════════════════════════════════════════════════════════════════════════
    // LangChain4j & Ollama (CLEAN VERSION — no duplication!)
    // ═══════════════════════════════════════════════════════════════════════════
    // BOM first — governs ALL langchain4j versions
    implementation(platform(libs.langchain4j.bom))

    // DUAL declaration: implementation for KSP classpath, api() for consumers
    // This ensures Hilt/KSP can resolve ChatLanguageModel
    implementation(libs.langchain4j.core)
    api(libs.langchain4j.core)

    // Model integrations
    implementation(libs.langchain4j.google.ai.gemini)
    implementation(libs.langchain4j.open.ai)
    implementation(libs.langchain4j.ollama)
    implementation(libs.langchain4j.http.client.jdk)
    implementation(libs.langchain4j.vertex.ai.gemini)

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
