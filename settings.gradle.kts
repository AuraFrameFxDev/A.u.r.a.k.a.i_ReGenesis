import org.gradle.api.initialization.resolve.RepositoriesMode.PREFER_SETTINGS

// settings.gradle.kts
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    includeBuild("build-logic")

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/eap") }
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://dl.google.com/dl/android/maven2/") }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://api.xposed.info/") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }

        // Dynamically add libs directories as repositories
        rootDir.walkTopDown()
            .filter { it.isDirectory && File(it, "libs").exists() }
            .map { File(it, "libs") }
            .toSet()
            .forEach { libsDir ->
                maven {
                    url = uri(libsDir.toURI())
                    metadataSources { artifact() }
                }
            }
    }
}

rootProject.name = "aurakai-reactive-intelligence"

/**
 * Includes the Gradle project at the given path if its directory exists.
 *
 * The `path` should be a colon-separated Gradle project path (for example `:a:b`). The function checks
 * the corresponding filesystem directory and calls `include(path)` when present; if not present, it
 * prints a warning indicating the missing directory.
 *
 * @param path Colon-separated Gradle project path to include (e.g., `:module:submodule`).
 */
fun includeIfExists(path: String) {
    val dir = path.removePrefix(":").replace(":", "/")
    if (file(dir).exists()) {
        include(path)
    } else {
        println("⚠️  settings: skip $path (missing $dir)")
    }
}

// --- Application ---
include(":app")

// --- Core Modules ---
include(":core-module")
include(":list")
include(":utilities")

// --- Aura → ReactiveDesign ---
include(":aura")
includeIfExists(":aura:reactivedesign:auraslab")
includeIfExists(":aura:reactivedesign:collabcanvas")
includeIfExists(":aura:reactivedesign:chromacore")
includeIfExists(":aura:reactivedesign:customization")


// --- Kai → SentinelsFortress ---
include(":kai")
includeIfExists(":kai:sentinelsfortress:security")
includeIfExists(":kai:sentinelsfortress:systemintegrity")
includeIfExists(":kai:sentinelsfortress:threatmonitor")

// --- Genesis → OracleDrive ---
include(":genesis")
includeIfExists(":genesis:oracledrive")
includeIfExists(":genesis:oracledrive:rootmanagement")
includeIfExists(":genesis:oracledrive:datavein")

// --- Cascade → DataStream ---
include(":cascade")
includeIfExists(":cascade:datastream:routing")
includeIfExists(":cascade:datastream:delivery")
includeIfExists(":cascade:datastream:taskmanager")

// --- Agents → GrowthMetrics ---
// Note: :agents is just a directory container, not a Gradle module
// Only the submodules under :agents:growthmetrics:* are actual modules
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
