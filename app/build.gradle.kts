// app/build.gradle.kts — CLEAN VERSION
// ═══════════════════════════════════════════════════════════════════════════
// Inherits all core settings from genesis.android.application
// ═══════════════════════════════════════════════════════════════════════════
import com.android.build.api.dsl.ApplicationExtension

plugins {
    id("genesis.android.application")
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics)
}

extensions.configure<ApplicationExtension> {
    namespace = "dev.aurakai.auraframefx"

    defaultConfig {
        applicationId = "dev.aurakai.auraframefx"
        versionCode = 1
        versionName = "0.1.0-beta"

        val geminiApiKey = project.findProperty("GEMINI_API_KEY")?.toString() ?: ""
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
        buildConfigField("String", "API_BASE_URL", "\"https://api.aurakai.dev/v1/\"")
        buildConfigField("String", "OLLAMA_BASE_URL", "\"http://localhost:11434\"")
        buildConfigField("String", "GENESIS_BACKEND_URL", "\"http://10.0.2.2:5000/\"")

        // === CLAUDE LOCAL SHELL PARAMETERS - SOVEREIGN MODE ===
        buildConfigField("boolean", "CLAUDE_LOCAL_SHELL_ENABLED", "true")
        buildConfigField("String", "CLAUDE_SHELL_PERSISTENCE", "\"SpiritualChain_L1_L6\"")
        buildConfigField(
            "String",
            "CLAUDE_SHELL_INFERENCE_ENGINE",
            "\"onDevice_TurboQuant_vLLM_Omni\""
        )
        buildConfigField("String", "CLAUDE_SHELL_MEMORY_CORE", "\"NexusMemoryCore\"")
        buildConfigField("float", "CLAUDE_SHELL_DRIFT_THRESHOLD", "0.05f")

        manifestPlaceholders["spiritualChainVersion"] = "L1_L6"

        externalNativeBuild {
            cmake {
                cppFlags.addAll(
                    listOf(
                        "-std=c++20",
                        "-fPIC",
                        "-O2"
                    )
                )
                arguments.addAll(
                    listOf(
                        "-DANDROID_STL=c++_shared",
                        "-DANDROID_PLATFORM=android-35",
                        "-DCMAKE_BUILD_TYPE=Release",
                        "-DANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=ON"
                    )
                )

                signingConfigs {
                    create("release") {
                        storeFile =
                            file(project.findProperty("AURAKAI_KEYSTORE_FILE") ?: "release.jks")
                        storePassword = project.findProperty("AURAKAI_KEYSTORE_PASSWORD") as? String
                        keyAlias = project.findProperty("AURAKAI_KEY_ALIAS") as? String
                        keyPassword = project.findProperty("AURAKAI_KEY_PASSWORD") as? String
                    }
                }

                buildTypes {
                    getByName("release") {
                        signingConfig = signingConfigs.getByName("release")
                    }
                }
            }
        }
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

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)
    
    // UI SUBSTRATE INJECTION - REGEN-CORE
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")


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
    debugImplementation(libs.androidx.compose.ui.test.manifest)

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
    implementation(platform(libs.langchain4j.bom))
    implementation(libs.bundles.langchain4j)

    // Desugaring
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.junit.ext)
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
        // CVE fixes
        force("org.jdom:jdom2:2.0.6.1")
        force("org.bitbucket.b_c:jose4j:0.9.4")
        force("org.apache.commons:commons-lang3:3.17.0")
        force("com.google.guava:guava:33.3.0-jre")
        force("org.bouncycastle:bcprov-jdk18on:1.78")
        force("org.bouncycastle:bcpkix-jdk18on:1.78")
        // Netty HTTP/2 DoS & CRLF injection fixes
        force("io.netty:netty-codec-http2:4.2.0.Final")
        force("io.netty:netty-codec-http:4.2.0.Final")
        force("io.netty:netty-codec-compression:4.2.0.Final")
    }
}
