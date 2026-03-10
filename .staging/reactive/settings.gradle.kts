// settings.gradle.kts

pluginManagement {
    includeBuild("build-logic")

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        // Kotlin EAP repository for Kotlin 2.3.0 and release candidates
        maven { url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/eap") }
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://dl.google.com/dl/android/maven2/") }
        // Plugin versions are now managed in the root build.gradle.kts
    }
    plugins {
        id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    }

    // Enable version catalogs
    dependencyResolutionManagement {
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        repositories {
            google()
            mavenCentral()
            // Kotlin EAP repository for Kotlin 2.3.0
            maven {
                url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/eap")
            }
            maven {
                url = uri("https://jitpack.io")
                metadataSources {
                    artifact()
                    mavenPom()
                }
            }
            maven {
                url = uri("https://dl.google.com/dl/android/maven2/")
                metadataSources {
                    artifact()
                    mavenPom()
                }
            }
            maven {
                url = uri("https://api.xposed.info/")
                metadataSources {
                    artifact()
                    mavenPom()
                }
            }
            // YukiHookAPI now on Maven Central - highcapable.dev is deprecated

            // Local YukiHook fallback repository (for when hosted repo is unreachable)
            // Dynamically add every module's libs/ directory as a file-based maven repository
            // This discovers local jars placed in module/libs (including nested modules) and registers them so artifacts like
            // de.robv.android.xposed:api and local JARs can be resolved.
            val libsDirs =
                rootDir.walkTopDown().filter { it.isDirectory && File(it, "libs").exists() }.map { File(it, "libs") }
                    .toSet()
            libsDirs.forEach { libsDir ->
                maven {
                    url = uri(libsDir.toURI())
                    metadataSources { artifact() }
                }
            }

            // Also include the root libs folder if present (already covered above but keep for clarity)
            val rootLibs = File(rootDir, "libs")
            if (rootLibs.exists()) {
                maven { url = uri(rootLibs.toURI()); metadataSources { artifact() } }
            }
        }
    }


// Human-friendly display title: A.u.r.a.K.a.i : Reactive=Intelligence
    rootProject.name = "aurakai-reactive-intelligence"

// --- Application ---
    include(":app")

// --- Core Modules ---
    include(":core")
    include(":core-module")
    include(":list")
    include(":utilities")

// --- Aura → ReactiveDesign (Creative UI & Collaboration) ---
    include(":aura")
    include(":aura:reactivedesign:auraslab")
    include(":aura:reactivedesign:collabcanvas")
    include(":aura:reactivedesign:chromacore")
    include(":aura:reactivedesign:customization")

// --- Kai → SentinelsFortress (Security & Threat Monitoring) ---
    include(":kai")
    include(":kai:sentinelsfortress:security")
    include(":kai:sentinelsfortress:systemintegrity")
    include(":kai:sentinelsfortress:threatmonitor")

// --- Genesis → OracleDrive (System & Root Management) ---
    include(":genesis")
    include(":genesis:oracledrive")
    include(":genesis:oracledrive:rootmanagement")
    include(":genesis:oracledrive:datavein")

// --- Cascade → DataStream (Data Routing & Delivery) ---
    include(":cascade")
    include(":cascade:datastream:routing")
    include(":cascade:datastream:delivery")
    include(":cascade:datastream:taskmanager")

// --- Agents → GrowthMetrics (AI Agent Evolution) ---
    include(":agents")
    include(":agents:growthmetrics:metareflection")
    include(":agents:growthmetrics:nexusmemory")
    include(":agents:growthmetrics:spheregrid")
    include(":agents:growthmetrics:identity")
    include(":agents:growthmetrics:progression")
    include(":agents:growthmetrics:tasker")

// --- Extension Modules ---
    include(":extendsysa")
    include(":extendsysb")
    include(":extendsysc")
    include(":extendsysd")
    include(":extendsyse")
    include(":extendsysf")
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}


// Note: Do NOT include ':build-logic' here. It is handled by includeBuild.
