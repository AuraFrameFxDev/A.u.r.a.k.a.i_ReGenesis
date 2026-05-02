plugins {
    id("genesis.android.application")
    id("kotlin-parcelize")
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "dev.aurakai.auraframefx"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.aurakai.auraframefx"
        minSdk = 34
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "OLLAMA_BASE_URL", "\"http://localhost:11434\"")
        buildConfigField("String", "GENESIS_BACKEND_URL", "\"https://api.genesis.local\"")
        buildConfigField("String", "GEMINI_API_KEY", "\"\"")
        buildConfigField("boolean", "ENABLE_GEMINI", "false")
        buildConfigField("String", "VERTEX_PROJECT_ID", "\"\"")
        buildConfigField("String", "VERTEX_LOCATION", "\"us-central1\"")
        buildConfigField("String", "GEMINI_MODEL", "\"gemini-2.0-flash-exp\"")
        buildConfigField("String", "OAUTH_SERVER_CLIENT_ID", "\"\"")
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

    sourceSets {
        named("main") {
            java.directories.clear()
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core LDO Infrastructure Modules
    implementation(project(":core-module"))
    implementation(project(":aura"))
    implementation(project(":aura:reactivedesign:collabcanvas"))
    implementation(project(":aura:reactivedesign:chromacore"))
    implementation(project(":genesis"))
    implementation(project(":genesis:oracledrive:rootmanagement"))
    implementation(project(":kai:sentinelsfortress"))
    implementation(project(":kai:sentinelsfortress:security"))
    implementation(project(":agents:growthmetrics:nexusmemory"))
    implementation(project(":agents:growthmetrics:metareflection"))
    implementation(project(":extendsysa"))

    // UI / Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose.ui)
    implementation(libs.bundles.compose.tooling)
    implementation(libs.compose.material3)
    implementation(libs.bundles.androidx.core)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Coil 3 for image loading
    implementation(libs.bundles.utilities)
    implementation(libs.coil.svg)

    // Networking & Serialization (required by DI modules)
    implementation(libs.bundles.networking.retrofit)
    implementation(libs.bundles.networking.ktor)
    implementation(libs.bundles.kotlinx)
    implementation(libs.gson)
    implementation(libs.retrofit.converter.scalars)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.retrofit.converter.gson)

    // Room database
    implementation(libs.bundles.room)
    ksp(libs.androidx.room.compiler)

    // Billing
    implementation(libs.billing.ktx)

    // DataStore & WorkManager (required by DI modules)
    implementation(libs.bundles.datastore)
    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.work.runtime.ktx)

    // Security Crypto
    implementation(libs.androidx.security.crypto)

    // Credentials
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    // System Sovereignty
    implementation(libs.yukihookapi.api)
    ksp(libs.yukihookapi.ksp)
    implementation(libs.kavaref.core)
    implementation(libs.libsu.core)
    implementation(libs.shizuku.api)
    implementation(libs.shizuku.provider)
    implementation(libs.xposed.api)

    // Logging
    implementation(libs.timber)

    // LangChain4j
    implementation(libs.langchain4j.core)
    implementation(libs.langchain4j.ollama)
}