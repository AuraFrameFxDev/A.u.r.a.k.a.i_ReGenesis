// ═══════════════════════════════════════════════════════════════════════════
// Root build.gradle.kts — ReGenesis Living Digital Organism
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

// Global build properties
val skipTests = providers.gradleProperty("aurafx.skip.tests").orElse("false").map { it.toBoolean() }.getOrElse(false)

// Root project level tasks/config can go here if needed
// Most logic is now in build-logic convention plugins
