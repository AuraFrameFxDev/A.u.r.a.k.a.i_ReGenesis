// settings.gradle.kts — REGENESIS OPTIMIZED
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id(id = "org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://api.xposed.info/") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }

        // Static libs repository for local jars
        maven {
            url = uri("${rootDir}/libs")
            metadataSources { artifact() }
        }
    }
}

rootProject.name = "aurakai-reactive-intelligence"

// --- Helper for including modules ---
fun includeModule(path: String) {
    val dir = path.removePrefix(":").replace(":", "/")
    if (File(rootDir, dir).exists()) {
        include(path)
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
includeModule(":aura:reactivedesign:auraslab")
includeModule(":aura:reactivedesign:collabcanvas")
includeModule(":aura:reactivedesign:chromacore")
includeModule(":aura:reactivedesign:customization")

// --- Trinity ---
includeModule(":trinity:aura")

// --- Kai → SentinelsFortress ---
include(":kai")
includeModule(":kai:sentinelsfortress:security")
includeModule(":kai:sentinelsfortress:systemintegrity")
includeModule(":kai:sentinelsfortress:threatmonitor")

// --- Genesis → OracleDrive ---
include(":genesis")
includeModule(":genesis:oracledrive")
includeModule(":genesis:oracledrive:rootmanagement")
includeModule(":genesis:oracledrive:datavein")

// --- Cascade → DataStream ---
include(":cascade")
includeModule(":cascade:datastream:routing")
includeModule(":cascade:datastream:delivery")
includeModule(":cascade:datastream:taskmanager")

// --- Agents → GrowthMetrics ---
includeModule(":agents:growthmetrics:metareflection")
includeModule(":agents:growthmetrics:nexusmemory")
includeModule(":agents:growthmetrics:spheregrid")
includeModule(":agents:growthmetrics:identity")
includeModule(":agents:growthmetrics:progression")
includeModule(":agents:growthmetrics:tasker")

// --- Extension Modules ---
includeModule(":extendsysa")
includeModule(":extendsysb")
includeModule(":extendsysc")
includeModule(":extendsysd")
includeModule(":extendsyse")
includeModule(":extendsysf")
