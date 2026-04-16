// app/build.gradle.kts — CLEAN VERSION
// ═══════════════════════════════════════════════════════════════════════════
// Inherits all core settings from genesis.android.application
// ═══════════════════════════════════════════════════════════════════════════
import com.android.build.api.dsl.ApplicationExtension

plugins{
    id("genesis.android.application")
    // Hilt, KSP, and Serialization are applied by the convention plugin.
    id("com.google.gms.google-services") apply false
    id(
        "com.google.firebase.crashlytics"
    )
}



    extensions.configure<ApplicationExtension> {
        namespace = "dev.aurakai.auraframefx"

        defaultConfig {
            applicationId = "dev.aurakai.auraframefx"
            versionCode = 1
            versionName = "0.1.0-beta"

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
                "\"${project.findProperty("GENESIS_BACKEND_URL") ?: "https://ais-dev-wli45m6aqwcfphhayj5w5o-16460197508.us-east5.run.app"}\""
            )
            buildConfigField(
                "String",
                "API_BASE_URL",
                "\"https://ais-dev-wli45m6aqwcfphhayj5w5o-16460197508.us-east5.run.app/v1/\""
            )
            buildConfigField(
                "String",
                "GOOGLE_OAUTH_CLIENT_ID",
                "\"${project.findProperty("GOOGLE_OAUTH_CLIENT_ID") ?: ""}\""
            )
            buildConfigField(
                "String",
                "COLLAB_CANVAS_WS_URL",
                "\"${project.findProperty("COLLAB_CANVAS_WS_URL") ?: "wss://api.auraframefx.com/canvas"}\""
            )

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

        signingConfigs {
            create("release") {
                storeFile = file(project.findProperty("AURAKAI_KEYSTORE_FILE") ?: "release.jks")
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
        // App-specific Dependencies (those not in convention plugin)
        // ═══════════════════════════════════════════════════════════════════════════
        implementation(libs.androidx.hilt.navigation.compose)
        implementation(libs.androidx.hilt.work)
        implementation(libs.androidx.credentials)
        implementation(libs.androidx.credentials.play.services.auth)
        implementation(libs.googleid)
        implementation(libs.androidx.work.runtime.ktx)
        implementation(libs.androidx.security.crypto)
        implementation(libs.androidx.datastore.preferences)
        implementation(libs.androidx.datastore.core)
        implementation(libs.androidx.navigation.compose)
        implementation(libs.androidx.room.runtime)
        implementation(libs.androidx.room.ktx)
        ksp(libs.androidx.room.compiler)
        implementation(libs.okhttp3.client.mobile.library)
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
        implementation(libs.moshi)
        implementation(libs.moshi.kotlin)
        ksp(libs.moshi.kotlin.codegen)
        implementation(libs.gson)
        implementation(libs.kotlinx.datetime)
        implementation(libs.coil.compose)
        implementation(libs.coil.svg)
        implementation(libs.coil.network.okhttp)
        implementation(libs.lottie.compose)
        implementation(libs.billing.ktx)
        implementation(libs.shizuku.api)
        implementation(libs.shizuku.provider)
        implementation(libs.rikkax.core)
        implementation(libs.rikkax.core.ktx)
        implementation(libs.rikkax.material) {
            exclude(group = "dev.rikka.rikkax.appcompat", module = "appcompat")
        }
        compileOnly(libs.xposed.api)
        compileOnly(files("libs/api-82.jar"))
        implementation(libs.firebase.analytics)
        implementation(libs.firebase.crashlytics)
        implementation(libs.firebase.messaging)
        implementation(libs.firebase.firestore)
        implementation(libs.firebase.storage)
        implementation(libs.firebase.auth)
        implementation(libs.firebase.config)
        implementation(libs.langchain4j.vertex.ai.gemini)

        // Testing
        testImplementation(libs.junit)
        testImplementation(libs.bundles.junit.jupiter)
        testImplementation(libs.mockk)
        testImplementation(libs.kotlinx.coroutines.test)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.espresso.core)
        androidTestImplementation(libs.androidx.compose.ui.test.junit4)
        debugImplementation(libs.leakcanary.android)
    }