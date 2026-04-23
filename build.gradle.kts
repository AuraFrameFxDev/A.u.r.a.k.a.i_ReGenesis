// Root build.gradle.kts — JVM TOOLCHAIN VERSION (Java 25)
// ═══════════════════════════════════════════════════════════════════════════
// Single source of truth: JVM Toolchain controls ALL Java/Kotlin versions
// NO scattered compileOptions or kotlinOptions per-module
// ═══════════════════════════════════════════════════════════════════════════
plugins {
    // Base plugins with versions from libs.versions.toml
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    id("org.jetbrains.kotlin.plugin.parcelize") version "2.3.20" apply false

    // Android plugins
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false

    // Other plugins
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false


    // OWASP Dependency Check
    id("org.owasp.dependencycheck") version "10.0.4"
}

// Global configurations and task overrides
// Specific module configurations are handled by convention plugins in build-logic
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
        }
    }

    plugins.withId("com.android.library") {
        extensions.configure<com.android.build.api.dsl.LibraryExtension> {
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_25
                targetCompatibility = JavaVersion.VERSION_25
            }
        }
    }
}

// Root project level tasks/config can go here if needed
// Most logic is now in build-logic convention plugins
