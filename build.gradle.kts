// ═══════════════════════════════════════════════════════════════════════════
// Root build.gradle.kts — ReGenesis Living Digital Organism
// ═══════════════════════════════════════════════════════════════════════════

plugins {
    // Base plugins with versions - Updated to stable releases
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.parcelize) apply false

    // Android plugins
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false

    // Other plugins - Updated to latest stable versions
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
}

val skipTests =
    providers.gradleProperty("aurafx.skip.tests").orElse("false").map { it.toBoolean() }
        .getOrElse(false)

subprojects {
    // ═══════════════════════════════════════════════════════════════════════════
    // CRITICAL: Global YukiHook KSP Exclusion
    // ═══════════════════════════════════════════════════════════════════════════
    configurations.all {
        if (!name.lowercase().contains("ksp") && !name.contains("lint", ignoreCase = true)) {
            exclude(group = "com.highcapable.yukihookapi", module = "ksp-xposed")
        }
    }

    // Configure Java Toolchain and Compile Options for Android Modules
    plugins.withId("com.android.application") {
        extensions.configure<com.android.build.api.dsl.ApplicationExtension> {
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_25
                targetCompatibility = JavaVersion.VERSION_25
            }

            packaging {
                resources {
                    pickFirsts += "**/YukiHookAPIProperties.class"
                }
            }

            if (skipTests) {
                sourceSets {
                    getByName("test") {
                        java.directories.clear()
                    }
                    getByName("androidTest") {
                        java.directories.clear()
                    }
                }
            }
        }

        if (skipTests) {
            extensions.configure<com.android.build.api.variant.AndroidComponentsExtension<*, *, *>>(
                "androidComponents"
            ) {
                beforeVariants { builder ->
                    (builder as? com.android.build.api.variant.HasUnitTestBuilder)?.enableUnitTest =
                        false
                    (builder as? com.android.build.api.variant.HasAndroidTestBuilder)?.enableAndroidTest =
                        false
                }
            }
        }
    }

    plugins.withId("com.android.library") {
        extensions.configure<com.android.build.api.dsl.LibraryExtension> {
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_25
                targetCompatibility = JavaVersion.VERSION_25
            }

            packaging {
                resources {
                    pickFirsts += "**/YukiHookAPIProperties.class"
                }
            }

            if (skipTests) {
                sourceSets {
                    getByName("test") {
                        java.directories.clear()
                    }
                    getByName("androidTest") {
                        java.directories.clear()
                    }
                }
            }
        }

// Global build properties
        val skipTests =
            providers.gradleProperty("aurafx.skip.tests").orElse("false").map { it.toBoolean() }
                .getOrElse(false)
    }
}

// Root project level tasks/config can go here if needed
// Most logic is now in build-logic convention plugins
