// Root build.gradle.kts — JVM TOOLCHAIN VERSION (Java 25)
// ═══════════════════════════════════════════════════════════════════════════
// Single source of truth: JVM Toolchain controls ALL Java/Kotlin versions
// NO scattered compileOptions or kotlinOptions per-module
// ═══════════════════════════════════════════════════════════════════════════

plugins {
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
}

val skipTests = providers.gradleProperty("aurafx.skip.tests").orElse("false").map { it.toBoolean() }.getOrElse(false)

subprojects {
    // ═══════════════════════════════════════════════════════════════════════════
    // MASTER CONTROL: JVM Toolchain (Java 25) — controls everything below
    // ═══════════════════════════════════════════════════════════════════════════

    plugins.withType<org.jetbrains.kotlin.gradle.plugin.KotlinBasePluginWrapper> {
        extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension> {
            jvmToolchain {
                languageVersion.set(JavaLanguageVersion.of(25))
            }
        }
    }

    // Force Kotlin and Java to target the correct JVM version based on project type
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
        }
    }

    // Ensure Java compiler also targets JVM 25 correctly via toolchain
    tasks.withType<JavaCompile>().configureEach {
        val javaToolchains = project.extensions.getByType<JavaToolchainService>()
        javaCompiler.set(javaToolchains.compilerFor {
            languageVersion.set(JavaLanguageVersion.of(25))
        })
        options.compilerArgs.add("--enable-preview")
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
