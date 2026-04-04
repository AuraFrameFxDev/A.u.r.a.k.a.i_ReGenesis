// ═══════════════════════════════════════════════════════════════════════════
// PRIMARY APPLICATION MODULE - AGP 9.0 Compatible (2025 Edition)
// ═══════════════════════════════════════════════════════════════════════════
import com.android.build.api.dsl.ApplicationExtension

plugins {
    id("com.android.application")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

extensions.configure<ApplicationExtension> {
    namespace = "dev.aurakai.auraframefx"
    compileSdk = 37
    ndkVersion = project.findProperty("android.ndkVersion")?.toString() ?: "29.0.14206865"

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
        buildConfigField("String", "OLLAMA_BASE_URL", "\"http://localhost:11434\"")

        vectorDrawables { useSupportLibrary = true }

        ndk {
            abiFilters.addAll(listOf("arm64-v8a", "armeabi-v7a", "x86_64"))
        }

        externalNativeBuild {
            cmake {
                cppFlags.addAll(listOf(
                    "-std=c++20", 
                    "-fPIC", 
                    "-O2",
                    "-march=armv8.2-a+sve2+i8mm+dotprod" // Enable advanced NEON/SVE features for IDE
                ))
                arguments.addAll(listOf(
                    "-DANDROID_STL=c++_shared",
                    "-DANDROID_PLATFORM=android-33",
                    "-DCMAKE_BUILD_TYPE=Release",
                    "-DANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=ON" // Play Store 16KB page size compliance
                ))
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
            pickFirsts += "**/YukiHookAPIProperties.class"
        }
        jniLibs {
            useLegacyPackaging = false
            pickFirsts += listOf("**/libc++_shared.so", "**/libjsc.so")
        }
    }

    androidResources { noCompress += "tflite" }

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

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
        freeCompilerArgs.addAll(
            "-Xcontext-parameters",
            "-Xannotation-default-target=param-property"
        )
    }
}

dependencies {
    // ═══════════════════════════════════════════════════════════════════════════
    // Core Module
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

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Compose BOM & UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.animation)
    implementation(libs.compose.material.icons.extended)
    debugImplementation(libs.compose.ui.tooling)
    testDebugImplementation(libs.androidx.compose.ui.test.manifest)

    // Extras
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.security.crypto)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Networking
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

    // JSON Processing
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.kotlin.codegen)
    implementation(libs.gson)

    // Utilities
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.timber)
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)
    implementation(libs.coil.network.okhttp)
    implementation(libs.lottie.compose)
    implementation(libs.billing.ktx)

    // Root/System
    implementation(libs.libsu.core)
    implementation(libs.libsu.nio)
    implementation(libs.libsu.service)
    implementation(libs.shizuku.api)
    implementation(libs.shizuku.provider)
    implementation(libs.rikkax.core)
    implementation(libs.rikkax.core.ktx)
    implementation(libs.rikkax.material) {
        exclude(
            group = "dev.rikka.rikkax.appcompat",
            module = "appcompat"
        )
    }

    // YukiHook & Xposed
    compileOnly(libs.yukihookapi.api) {
        exclude(
            group = "com.highcapable.yukihookapi",
            module = "ksp-xposed"
        )
    }
    ksp(libs.yukihookapi.ksp)
    compileOnly(libs.xposed.api)
    compileOnly(files("$projectDir/libs/api-82.jar"))
    
    // KavaRef for modern reflection
    implementation(libs.kavaref.core)
    implementation(libs.kavaref.extension)

    // AI & Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.config)
    implementation(libs.generativeai)

    // LangChain4j & Ollama
    implementation(libs.bundles.langchain4j)

    // Desugaring
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // Testing
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

configurations.all {
    if (name.contains("AndroidTest")) return@all
    if (name.contains("RuntimeClasspath", ignoreCase = true)) {
        exclude(group = "com.highcapable.yukihookapi", module = "ksp-xposed")
    }
    resolutionStrategy {
        force("org.jetbrains:annotations:26.1.0")
        force("androidx.appcompat:appcompat:1.7.1")
        force("com.google.android.material:material:1.13.0")
        force("com.google.dagger:hilt-android:2.59.2")
        force("com.google.dagger:hilt-android-compiler:2.59.2")
        force("androidx.test.espresso:espresso-core:3.7.0")
    }
}