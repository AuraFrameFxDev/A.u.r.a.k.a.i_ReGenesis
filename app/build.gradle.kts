plugins {
    id("genesis.android.application")
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
    implementation(libs.compose.material3)
    implementation(libs.bundles.androidx.core)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Networking & Serialization (required by DI modules)
    implementation(libs.bundles.networking.retrofit)
    implementation(libs.bundles.networking.ktor)
    implementation(libs.bundles.kotlinx)
    implementation(libs.gson)

    // DataStore & WorkManager (required by DI modules)
    implementation(libs.bundles.datastore)
    implementation(libs.androidx.hilt.work)
    implementation(libs.androidx.work.runtime.ktx)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    // System Sovereignty
    implementation(libs.yukihookapi.api)
    ksp(libs.yukihookapi.ksp)

    // LangChain4j
    implementation(libs.langchain4j.core)
    implementation(libs.langchain4j.ollama)
}