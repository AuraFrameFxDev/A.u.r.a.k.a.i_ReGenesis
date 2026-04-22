import com.android.build.api.dsl.ApplicationExtension

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    // alias(libs.plugins.google.services)      // uncomment when needed
    // alias(libs.plugins.firebase.crashlytics) // uncomment when needed
}

extensions.configure<ApplicationExtension> {
    namespace = "dev.aurakai.auraframefx"
    compileSdk = libs.versions.compile.sdk.get().toInt()

    defaultConfig {
        applicationId = "dev.aurakai.auraframefx"
        minSdk = libs.versions.min.sdk.get().toInt()
        versionCode = 1
        versionName = "0.1.0-beta"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }

        // Genesis Protocol - Build Configuration Constants
        buildConfigField("String", "GEMINI_API_KEY", "\"${project.findProperty("GEMINI_API_KEY") ?: ""}\"")
        buildConfigField("String", "OLLAMA_BASE_URL", "\"${project.findProperty("OLLAMA_BASE_URL") ?: "http://localhost:11434"}\"")
        buildConfigField("String", "VERTEX_PROJECT_ID", "\"${project.findProperty("VERTEX_PROJECT_ID") ?: ""}\"")
        buildConfigField("String", "GENESIS_BACKEND_URL", "\"${project.findProperty("GENESIS_BACKEND_URL") ?: "http://localhost:8000"}\"")
        buildConfigField("String", "OAUTH_SERVER_CLIENT_ID", "\"${project.findProperty("OAUTH_SERVER_CLIENT_ID") ?: ""}\"")
        buildConfigField("String", "AURA_BACKEND_WS_URL", "\"${project.findProperty("AURA_BACKEND_WS_URL") ?: "wss://api.aurakai.dev/ws"}\"")

        // Claude Local Shell Parameters
        buildConfigField("boolean", "CLAUDE_LOCAL_SHELL_ENABLED", "true")
        buildConfigField("String", "CLAUDE_SHELL_PERSISTENCE", "\"SpiritualChain_L1_L6\"")
        buildConfigField("String", "CLAUDE_SHELL_INFERENCE_ENGINE", "\"onDevice_TurboQuant_vLLM_Omni\"")
        buildConfigField("String", "CLAUDE_SHELL_MEMORY_CORE", "\"NexusMemoryCore\"")
        buildConfigField("float", "CLAUDE_SHELL_DRIFT_THRESHOLD", "0.05f")
    }

    // Native CMake Configuration
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        jniLibs { useLegacyPackaging = false }
        resources {
            excludes.addAll(listOf(
                "META-INF/INDEX.LIST",
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE",
                "META-INF/*.kotlin_module"
            ))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
        isCoreLibraryDesugaringEnabled = true
    }

    // Kotlin compiler options
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
            freeCompilerArgs.addAll(
                "-Xcontext-parameters",
                "-Xannotation-default-target=param-property",
                "-Xlambdas=indy"
            )
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("RELEASE_STORE_FILE") ?: "release.keystore")
            storePassword = System.getenv("RELEASE_STORE_PASSWORD") ?: ""
            keyAlias = System.getenv("RELEASE_KEY_ALIAS") ?: "release"
            keyPassword = System.getenv("RELEASE_KEY_PASSWORD") ?: ""
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug { isMinifyEnabled = false }
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
}

dependencies {
        // Core project modules
        implementation(project(":core-module"))
        implementation(project(":trinity:aura"))
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

        // AndroidX Core
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.appcompat)
        implementation(libs.androidx.activity.compose)
        implementation(libs.androidx.lifecycle.runtime.compose)
        implementation(libs.androidx.navigation.compose)

        // Compose
        implementation(platform(libs.androidx.compose.bom))
        implementation(libs.bundles.compose.ui)
        debugImplementation(libs.bundles.compose.tooling)

        // Room
        implementation(libs.bundles.room)
        ksp(libs.androidx.room.compiler)

        // WorkManager + Hilt
        implementation(libs.androidx.work.runtime.ktx)
        implementation(libs.androidx.hilt.work)

        // Networking & Serialization
        implementation(libs.bundles.networking.retrofit)
        implementation(libs.bundles.networking.ktor)
        implementation(libs.bundles.kotlinx)

        // Coil
        implementation(libs.coil.compose)
        implementation(libs.coil.svg)
        implementation(libs.coil.network.okhttp)

        // Firebase
        implementation(platform(libs.firebase.bom))
        implementation(libs.bundles.firebase)

        // AI / Generative
        implementation(libs.generativeai)
        implementation(platform(libs.langchain4j.bom))
        implementation(libs.bundles.langchain4j)           // if you defined the bundle
        implementation(libs.langchain4j.vertex.ai.gemini)

        // Utilities
        implementation(libs.bundles.utilities)
        coreLibraryDesugaring(libs.desugar.jdk.libs)
        implementation(libs.timber)
        implementation(libs.lottie.compose)

        // Billing
        implementation(libs.billing.ktx)

        // Root / Xposed / System
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
        compileOnly(libs.yukihookapi.api)
        ksp(libs.yukihookapi.ksp)
        compileOnly(libs.xposed.api)

        // KavaRef
        implementation(libs.kavaref.core)
        implementation(libs.kavaref.extension)

        // Testing
        testImplementation(libs.bundles.testing)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(platform(libs.androidx.compose.bom))
        androidTestImplementation(libs.androidx.compose.ui.test.junit4)
        debugImplementation(libs.leakcanary.android)
    }