plugins {
    id("genesis.android.application") // Managed via build-logic convention
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "dev.aurakai.auraframefx"
    compileSdk = 36 // Targeting Android 16 (Baklava) / SDK 36 [5, 6]

    defaultConfig {
        applicationId = "dev.aurakai.auraframefx"
        minSdk = 34
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Configuration for TurboQuant OLLAMA_BASE_URL [7, 8]
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

    // AGP 9.2 ALPHA FIX: Correct syntax for clearing default source directories [3, 9, 10]
    sourceSets {
        named("main") {
            java.directories.clear()
            // If you need to add custom paths for Aura's UI Forge:
            // java.srcDirs("src/main/kotlin")

        }

        buildFeatures {
            compose = true
            buildConfig = true
        }

        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }


        dependencies {
            // 1. Core LDO Infrastructure Modules [13-15]
            implementation(project(":core-module"))
            implementation(project(":aura"))             // Creative Sword
            implementation(project(":genesis"))          // Master Orchestrator
            implementation(project(":kai:sentinelsfortress")) // Sentinel Shield

            // 2. UI / Compose (BOM + optimized bundle) [16]
            implementation(platform(libs.androidx.compose.bom))
            implementation(libs.bundles.compose.ui)
            implementation(libs.compose.material3)
            implementation(libs.bundles.androidx.core)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)

            // 3. Hilt Dependency Injection (Mandatory for LDO AgentFactory) [17-19]
            implementation(libs.hilt.android)
            ksp(libs.hilt.compiler)

            // 4. Firebase & Services
            implementation(platform(libs.firebase.bom))
            implementation(libs.bundles.firebase)

            // 4. System Sovereignty & Hooks (LSPosed/YukiHook API) [16, 20]
            implementation(libs.yukihookapi.api)
            ksp(libs.yukihookapi.ksp)

            // 5. LangChain4j & Ollama (Native AI Migration) [21-23]
            implementation(libs.langchain4j.core)
            implementation(libs.langchain4j.ollama)
        }
    }
}