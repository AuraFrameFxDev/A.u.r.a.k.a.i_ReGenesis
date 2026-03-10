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


// Helper function to include modules only if they exist
fun includeIfExists(path: String) {
    val dir = path.replace(":", "/")
    if (file(dir).exists()) {
        include(path)
    } else {
        println("⚠️  settings: skip $path (missing $dir)")
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
includeIfExists(":aura:reactivedesign:auraslab")
includeIfExists(":aura:reactivedesign:collabcanvas")
includeIfExists(":aura:reactivedesign:chromacore")
includeIfExists(":aura:reactivedesign:customization")

// --- Kai → SentinelsFortress (Security & Threat Monitoring) ---
include(":kai")
includeIfExists(":kai:sentinelsfortress:security")
includeIfExists(":kai:sentinelsfortress:systemintegrity")
includeIfExists(":kai:sentinelsfortress:threatmonitor")

// --- Genesis → OracleDrive (System & Root Management) ---
include(":genesis")
includeIfExists(":genesis:oracledrive")
includeIfExists(":genesis:oracledrive:rootmanagement")
includeIfExists(":genesis:oracledrive:datavein")

// --- Cascade → DataStream (Data Routing & Delivery) ---
include(":cascade")
includeIfExists(":cascade:datastream:routing")
includeIfExists(":cascade:datastream:delivery")
includeIfExists(":cascade:datastream:taskmanager")

// --- Agents → GrowthMetrics (AI Agent Evolution) ---
include(":agents")
includeIfExists(":agents:growthmetrics:metareflection")
includeIfExists(":agents:growthmetrics:nexusmemory")
includeIfExists(":agents:growthmetrics:spheregrid")
includeIfExists(":agents:growthmetrics:identity")
includeIfExists(":agents:growthmetrics:progression")
includeIfExists(":agents:growthmetrics:tasker")

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
