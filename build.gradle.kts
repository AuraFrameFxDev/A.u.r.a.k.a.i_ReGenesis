// Root build.gradle.kts — CLEAN JVM TOOLCHAIN VERSION (Java 25)
// ═══════════════════════════════════════════════════════════════════════════
// Single source of truth: JVM Toolchain controls ALL Java/Kotlin versions
// NO scattered compileOptions or kotlinOptions per-module
// ═══════════════════════════════════════════════════════════════════════════

plugins {
    id("org.jetbrains.kotlin.android") version "2.3.20" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.20" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.3.20" apply false
    id("org.jetbrains.kotlin.plugin.parcelize") version "2.3.20" apply false
    id("com.android.application") version "9.2.0-alpha07" apply false
    id("com.android.library") version "9.2.0-alpha07" apply false
    id("com.google.dagger.hilt.android") version "2.59.2" apply false
    id("com.google.devtools.ksp") version "2.3.6" apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
    id("com.google.firebase.crashlytics") version "3.0.6" apply false
}

val skipTests = providers.gradleProperty("aurafx.skip.tests").orElse("false").map { it.toBoolean() }.getOrElse(false)

subprojects {
    // ═══════════════════════════════════════════════════════════════════════════
    // MASTER CONTROL: JVM Toolchain (Java 25) — controls everything below
    // ═══════════════════════════════════════════════════════════════════════════

    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Dependency & YukiHook exclusions (unchanged from original)
    // ═══════════════════════════════════════════════════════════════════════════

    configurations.all {
        if (!name.lowercase().contains("ksp") && !name.contains("lint", ignoreCase = true)) {
            exclude(group = "com.highcapable.yukihookapi", module = "ksp-xposed")
        }
        exclude(group = "com.google.protobuf", module = "protobuf-java")
        exclude(group = "com.google.api.grpc", module = "proto-google-common-protos")

        resolutionStrategy {
            force("com.google.protobuf:protobuf-javalite:3.25.5")
            force("com.google.protobuf:protolite-well-known-types:18.4.0")
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Android-specific config (still needed for AGP)
    // ═══════════════════════════════════════════════════════════════════════════

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
                sourceSets {
                    getByName("test") { java.directories.clear() }
                    getByName("androidTest") { java.directories.clear() }
                }
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

    plugins.withId("com.android.library") {
        extensions.configure<com.android.build.api.dsl.LibraryExtension> {
            packaging {
                resources {
                    pickFirsts += "**/YukiHookAPIProperties.class"
                }
            }

            if (skipTests) {
                sourceSets {
                    getByName("test") { java.directories.clear() }
                    getByName("androidTest") { java.directories.clear() }
                }
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
