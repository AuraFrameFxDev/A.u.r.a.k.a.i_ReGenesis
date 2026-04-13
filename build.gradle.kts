// Root build.gradle.kts — JVM TOOLCHAIN VERSION (Java 26)
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
    // MASTER CONTROL: JVM Toolchain (Java 26) — controls everything below
    // ═══════════════════════════════════════════════════════════════════════════

    plugins.withType<org.jetbrains.kotlin.gradle.plugin.KotlinBasePluginWrapper> {
        extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension> {
            jvmToolchain {
                languageVersion.set(JavaLanguageVersion.of(26))
            }
        }
    }

    // Force Kotlin and Java to target the correct JVM version based on project type
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget("25"))
        }
    }

    // Ensure Java compiler also targets JVM 26 correctly via toolchain
    tasks.withType<JavaCompile>().configureEach {
        val javaToolchains = project.extensions.getByType<JavaToolchainService>()
        javaCompiler.set(javaToolchains.compilerFor {
            languageVersion.set(JavaLanguageVersion.of(26))
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
        exclude(group = "org.conscrypt", module = "conscrypt-openjdk-uber")

        // Stabilize ReGenesis Substrate: Favor full Protobuf over Lite to support Vertex AI / Gemini
        exclude(group = "com.google.protobuf", module = "protobuf-javalite")
        exclude(group = "com.google.protobuf", module = "protobuf-lite")
        exclude(group = "com.google.firebase", module = "protolite-well-known-types")

        resolutionStrategy {
            val okhttpVersion = libs.versions.okhttp.get()
            dependencySubstitution {
                substitute(module("com.squareup.okhttp3:okhttp")).using(module("com.squareup.okhttp3:okhttp-android:$okhttpVersion"))
                substitute(module("com.squareup.okhttp3:okhttp-jvm")).using(module("com.squareup.okhttp3:okhttp-android:$okhttpVersion"))
                
                val protobufVersion = libs.versions.protobuf.get()
                substitute(module("com.google.protobuf:protobuf-javalite")).using(module("com.google.protobuf:protobuf-java:$protobufVersion"))
                substitute(module("com.google.protobuf:protobuf-lite")).using(module("com.google.protobuf:protobuf-java:$protobufVersion"))
            }
            force("org.conscrypt:conscrypt-android:2.5.3")
            force("com.google.protobuf:protobuf-java:3.25.8")
            force("com.google.api.grpc:proto-google-common-protos:2.59.0")

            // High-Sovereignty Security Hardening (April 2026 Audit Fixes)
            val nettyVer = libs.versions.netty.get()
            force("io.netty:netty-all:$nettyVer")
            force("io.netty:netty-codec-http2:$nettyVer")
            force("io.netty:netty-handler:$nettyVer")
            force("io.netty:netty-codec-http:$nettyVer")
            force("io.netty:netty-common:$nettyVer")
            force("io.netty:netty-codec:$nettyVer")
            force("io.grpc:grpc-netty-shaded:1.80.0")
            force("org.jdom:jdom2:2.0.6.1")
            force("org.bitbucket.b_c:jose4j:0.9.7")
            force("org.apache.commons:commons-lang3:3.20.0")
            force("org.apache.httpcomponents:httpclient:4.5.14")
            force("org.apache.httpcomponents:httpcore:4.4.16")
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
