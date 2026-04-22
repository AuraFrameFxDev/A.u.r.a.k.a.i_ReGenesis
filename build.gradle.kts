// Root build.gradle.kts — JVM TOOLCHAIN VERSION (Java 25)
// ═══════════════════════════════════════════════════════════════════════════
// Single source of truth: JVM Toolchain controls ALL Java/Kotlin versions
// NO scattered compileOptions or kotlinOptions per-module
// ═══════════════════════════════════════════════════════════════════════════
plugins {
    // Base plugins with versions - Updated to stable releases
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.20" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.3.20" apply false
    id("org.jetbrains.kotlin.plugin.parcelize") version "2.3.20" apply false

    // Android plugins
    id("com.android.application") version "9.3.0-alpha01" apply false
    id("com.android.library") version "9.3.0-alpha01" apply false

    // Other plugins - Updated to latest stable versions
    id("com.google.dagger.hilt.android") version "2.59.2" apply false
    id("com.google.devtools.ksp") version "2.3.6" apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
    id("com.google.firebase.crashlytics") version "3.0.7" apply false
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
            // NOTE: compileOptions sourceCompatibility/targetCompatibility are now
            // auto-set by the JVM Toolchain. You can omit them. If you need to
            // override, do it here, but KEEP IT IN SYNC with the toolchain version above.

            packaging {
                resources {
                    pickFirsts += "**/YukiHookAPIProperties.class"
                }
            }

            if (skipTests) {
                sourceSets.getByName("test").java.directories.clear()
                sourceSets.getByName("androidTest").java.directories.clear()
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
            packaging {
                resources {
                    pickFirsts += "**/YukiHookAPIProperties.class"
                }
            }

            if (skipTests) {
                sourceSets.getByName("test").java.directories.clear()
                sourceSets.getByName("androidTest").java.directories.clear()
            }
        }

        if (skipTests) {
            extensions.configure<com.android.build.api.variant.AndroidComponentsExtension<*, *, *>>("androidComponents") {
                beforeVariants { builder ->
                    (builder as? com.android.build.api.variant.HasUnitTestBuilder)?.enableUnitTest = false
                    (builder as? com.android.build.api.variant.HasAndroidTestBuilder)?.enableAndroidTest = false
                }
            }
        }
    }

    // Disable tests if needed
    if (skipTests) {
        tasks.configureEach {
            if (name.contains("Test", ignoreCase = true) || this is Test) {
                enabled = false
            }
        }
    }
}


// Root project level tasks/config can go here if needed
// Most logic is now in build-logic convention plugins
