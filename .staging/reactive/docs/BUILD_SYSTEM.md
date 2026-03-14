# AuraKai Build System Documentation

**Last Updated:** December 5, 2025
**Gradle Version:** 9.4.0-milestone-2
**Kotlin Version:** 2.3.0-RC2
**AGP Version:** 9.0.0-beta02
**JVM Target:** 24

---

## Table of Contents

1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Key Files](#key-files)
4. [Convention Plugins (build-logic)](#convention-plugins-build-logic)
5. [Version Catalog](#version-catalog)
6. [Repository Configuration](#repository-configuration)
7. [JVM Configuration](#jvm-configuration)
8. [Module Structure](#module-structure)
9. [Adding New Modules](#adding-new-modules)
10. [Common Gradle Tasks](#common-gradle-tasks)
11. [Troubleshooting](#troubleshooting)

---

## Overview

The AuraKai project uses a sophisticated multi-module Gradle build system with:

- **Convention Plugins**: Reusable build logic in `build-logic/`
- **Version Catalog**: Centralized dependency management via `gradle/libs.versions.toml`
- **Composite Builds**: `build-logic` is included as a composite build
- **Type-Safe Accessors**: Generated from version catalog for compile-time safety

### Key Technologies

- **Build System**: Gradle 9.4.0-milestone-2 with Kotlin DSL
- **Android Gradle Plugin**: 9.0.0-beta02
- **Kotlin**: 2.3.0-RC2 (from JetBrains EAP repository)
- **Compose Compiler**: 2.3.0-RC2
- **Dependency Injection**: Hilt 2.57.2
- **Symbol Processing**: KSP 2.3.3

---

## Architecture

```
AuraKai/
├── build-logic/                    # Convention plugins (composite build)
│   ├── src/main/kotlin/           # Plugin implementations
│   ├── build.gradle.kts           # build-logic dependencies
│   └── settings.gradle.kts        # build-logic repositories
├── gradle/
│   └── libs.versions.toml         # Version catalog
├── settings.gradle.kts            # Project structure & repositories
├── build.gradle.kts               # Root build configuration
└── [modules]/                     # Application modules
    └── build.gradle.kts           # Module-specific configuration
```

### Build Flow

1. **Settings Phase**: `settings.gradle.kts` configures repositories and includes `build-logic`
2. **Configuration Phase**: Convention plugins are applied to modules
3. **Execution Phase**: Tasks execute based on dependency graph

---

## Key Files

### `settings.gradle.kts`

**Purpose**: Configures the multi-project build structure and dependency resolution.

**Key Sections**:

```kotlin
// Plugin management - repositories for build plugins
pluginManagement {
    includeBuild("build-logic")  // Composite build
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/eap")  // Kotlin 2.3.0-RC2
        maven("https://jitpack.io")
        maven("https://dl.google.com/dl/android/maven2/")
    }
}

// Dependency resolution - repositories for module dependencies
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/eap")
        maven("https://jitpack.io")
        maven("https://api.xposed.info/")
        // ... additional repositories
    }
}

// Module inclusion
include(":app")
include(":aura:reactivedesign:auraslab")
// ... all other modules
```

### `build.gradle.kts` (Root)

**Purpose**: Root-level build configuration and plugin versions.

**Key Features**:
- Applies plugins to all subprojects
- Configures common Android settings
- Sets up Kotlin compiler options
- Manages repository access

### `gradle/libs.versions.toml`

**Purpose**: Centralized version and dependency management.

**Structure**:
```toml
[versions]
kotlin = "2.3.0-RC2"
agp = "9.0.0-beta02"
hilt = "2.57.2"
# ... all version declarations

[libraries]
# Format: alias = { group, name, version.ref }
kotlin-gradle-plugin = { group = "org.jetbrains.kotlin", name = "kotlin-gradle-plugin", version.ref = "kotlin" }
# ... all library declarations

[bundles]
# Grouped dependencies
compose = ["compose-ui", "compose-material3", "compose-foundation"]

[plugins]
# Plugin declarations
android-application = { id = "com.android.application", version.ref = "agp" }
```

---

## Convention Plugins (build-logic)

**Location**: `build-logic/src/main/kotlin/`

### Available Plugins

#### 1. `genesis.android.application`
**File**: `GenesisApplicationPlugin.kt`
**Purpose**: Configures the `:app` module
**Applies**:
- Android Application Plugin
- Kotlin Android
- Hilt
- KSP
- Compose Compiler
- Kotlin Serialization
- Firebase Crashlytics
- Google Services

**Usage**:
```kotlin
plugins {
    id("genesis.android.application")
}
```

#### 2. `genesis.android.library`
**File**: `GenesisLibraryPlugin.kt`
**Purpose**: Base configuration for library modules **without** Hilt
**Applies**:
- Android Library Plugin
- Kotlin Android
- Compose Compiler
- Kotlin Serialization

**Usage**:
```kotlin
plugins {
    id("genesis.android.library")
}
```

#### 3. `genesis.android.library.hilt`
**File**: `GenesisLibraryHiltPlugin.kt`
**Purpose**: Library modules **with** Hilt dependency injection
**Applies**:
- All features from `genesis.android.library`
- Hilt Android Plugin
- KSP for Hilt code generation

**Usage**:
```kotlin
plugins {
    id("genesis.android.library.hilt")
}
```

### build-logic Configuration

**File**: `build-logic/build.gradle.kts`

```kotlin
// JVM Target: 24 (matches Kotlin compilation)
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(24))
    }
    sourceCompatibility = JavaVersion.VERSION_24
    targetCompatibility = JavaVersion.VERSION_24
}

// Kotlin compilation
tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_24)
        freeCompilerArgs.addAll("-opt-in=kotlin.RequiresOptIn")
    }
}

dependencies {
    // Convention plugins depend on these
    implementation(libs.gradle.plugin)              // AGP
    implementation(libs.kotlin.gradle.plugin)       // Kotlin
    implementation(libs.compose.compiler.gradle.plugin)
    implementation(libs.jetbrains.kotlin.serialization)
    implementation(libs.hilt.gradle.plugin)
    implementation(libs.ksp.gradle.plugin)
    implementation(libs.gms.google.services)
}
```

**File**: `build-logic/settings.gradle.kts`

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/eap")  // For Kotlin 2.3.0-RC2
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))  // Share version catalog
        }
    }
}
```

---

## Version Catalog

**File**: `gradle/libs.versions.toml`

### Version Declaration

```toml
[versions]
# Core tooling
kotlin = "2.3.0-RC2"          # Kotlin from EAP repository
agp = "9.0.0-beta02"          # Android Gradle Plugin
ksp = "2.3.3"                 # Kotlin Symbol Processing
hilt = "2.57.2"               # Dependency injection

# Compose
compose-bom = "2025.12.00"    # Compose Bill of Materials

# AndroidX
lifecycle = "2.9.0"
navigation-compose = "2.9.0"
```

### Library Declaration

```toml
[libraries]
# Kotlin
kotlin-gradle-plugin = { group = "org.jetbrains.kotlin", name = "kotlin-gradle-plugin", version.ref = "kotlin" }
compose-compiler-gradle-plugin = { group = "org.jetbrains.kotlin", name = "compose-compiler-gradle-plugin", version.ref = "kotlin" }
jetbrains-kotlin-serialization = { group = "org.jetbrains.kotlin", name = "kotlin-serialization", version.ref = "kotlin" }

# Hilt
hilt-gradle-plugin = { group = "com.google.dagger", name = "hilt-android-gradle-plugin", version.ref = "hilt" }
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-android-compiler = { group = "com.google.dagger", name = "hilt-android-compiler", version.ref = "hilt" }

# Compose BOM
compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "compose.bom" }
```

### Bundles

```toml
[bundles]
compose = [
    "compose-ui",
    "compose-ui-graphics",
    "compose-ui-tooling-preview",
    "compose-material3",
    "compose-foundation",
    "compose-foundation-layout"
]
```

### Using in Module build.gradle.kts

```kotlin
dependencies {
    // Single library
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Bundle
    implementation(libs.bundles.compose)

    // BOM (Bill of Materials)
    implementation(platform(libs.compose.bom))
}
```

---

## Repository Configuration

### Plugin Management Repositories

Used for build plugins (applied in `plugins {}` blocks):

```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal()                                             // Gradle plugins
        google()                                                         // Android plugins
        mavenCentral()                                                   // Kotlin plugins
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/eap")  // Kotlin 2.3.0-RC2
        maven("https://jitpack.io")                                     // Third-party
    }
}
```

### Dependency Resolution Repositories

Used for module dependencies (applied in `dependencies {}` blocks):

```kotlin
dependencyResolutionManagement {
    repositories {
        google()                                                         // Android libraries
        mavenCentral()                                                   // Standard libraries
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/eap")  // Kotlin 2.3.0-RC2
        maven("https://jitpack.io")                                     // GitHub projects
        maven("https://api.xposed.info/")                               // Xposed framework
        // Local libs/ directories for custom JARs
    }
}
```

### Why Kotlin EAP Repository?

Kotlin 2.3.0-RC2 is a release candidate not yet published to Maven Central. The **JetBrains Early Access Program (EAP)** repository hosts preview releases:

```
https://maven.pkg.jetbrains.space/kotlin/p/kotlin/eap
```

This repository is required in:
1. `settings.gradle.kts` - `pluginManagement.repositories`
2. `settings.gradle.kts` - `dependencyResolutionManagement.repositories`
3. `build-logic/settings.gradle.kts` - `repositories`

---

## JVM Configuration

### Target: Java/JVM 24

All modules and build-logic compile to JVM bytecode version 24.

**Reasoning**:
- Latest stable JDK with full Kotlin 2.3.0-RC2 support
- `JvmTarget.JVM_25` doesn't exist in Kotlin Gradle Plugin yet
- Configured in `gradle.properties`:

```properties
org.gradle.java.toolchain.version=24
kotlin.jvm.target=24
```

### build-logic JVM Configuration

**File**: `build-logic/build.gradle.kts`

```kotlin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(24))
    }
    sourceCompatibility = JavaVersion.VERSION_24
    targetCompatibility = JavaVersion.VERSION_24
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_24)
    }
}

tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = "24"
    targetCompatibility = "24"
}
```

### Module JVM Configuration

Configured automatically by convention plugins based on toolchain.

---

## Module Structure

### Project Layout

```
:app                                       # Main application module
:aura                                      # Creative UI & Collaboration
  :reactivedesign
    :auraslab                              # Design lab
    :collabcanvas                          # Collaborative canvas
    :chromacore                            # Color system
    :customization                         # UI customization
:kai                                       # Security & Threat Monitoring
  :sentinelsfortress
    :security                              # Security features
    :systemintegrity                       # System integrity checks
    :threatmonitor                         # Threat detection
:genesis                                   # System & Root Management
  :oracledrive                             # Root access
    :rootmanagement                        # Root operations
    :datavein                              # Data management
:cascade                                   # Data Routing & Delivery
  :datastream
    :routing                               # Data routing
    :delivery                              # Data delivery
    :taskmanager                           # Task management
:agents                                    # AI Agent Evolution
  :growthmetrics
    :metareflection                        # Self-reflection
    :nexusmemory                           # Memory system
    :spheregrid                            # Capability grid
    :identity                              # Agent identity
    :progression                           # Growth tracking
    :tasker                                # Task execution
:core                                      # Core libraries
  :domain                                  # Domain models
  :data                                    # Data layer
  :ui                                      # UI components
  :common                                  # Common utilities
:core-module                               # Core module integration
:list                                      # List utilities
:extendsysa-f                              # Extension systems A-F
```

### Module Types

1. **Application Module** (`:app`): Uses `genesis.android.application`
2. **Library Modules (with Hilt)**: Use `genesis.android.library.hilt`
3. **Library Modules (without Hilt)**: Use `genesis.android.library`

---

## Adding New Modules

### Step 1: Create Module Directory

```bash
mkdir -p aura/newfeature
```

### Step 2: Create build.gradle.kts

**For modules WITH Hilt**:

```kotlin
plugins {
    id("genesis.android.library.hilt")
}

android {
    namespace = "dev.aurakai.auraframefx.aura.newfeature"

    // Optional: Configure build features
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Core dependencies
    implementation(project(":core:domain"))
    implementation(project(":core:ui"))

    // AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)

    // Hilt (already applied by convention plugin)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
}
```

**For modules WITHOUT Hilt**:

```kotlin
plugins {
    id("genesis.android.library")
}

android {
    namespace = "dev.aurakai.auraframefx.aura.newfeature"
}

dependencies {
    // Same as above, minus Hilt dependencies
}
```

### Step 3: Register in settings.gradle.kts

```kotlin
include(":aura:newfeature")
```

### Step 4: Sync Gradle

```bash
./gradlew --refresh-dependencies
```

---

## Common Gradle Tasks

### Building

```bash
# Clean build
./gradlew clean build

# Build specific module
./gradlew :app:build
./gradlew :aura:reactivedesign:auraslab:build

# Build and install debug APK
./gradlew installDebug

# Build release APK
./gradlew assembleRelease
```

### Testing

```bash
# Run all tests
./gradlew test

# Run specific module tests
./gradlew :core:domain:test

# Run Android instrumentation tests
./gradlew connectedAndroidTest
```

### Dependency Management

```bash
# Update dependencies
./gradlew --refresh-dependencies

# Show dependency tree
./gradlew :app:dependencies

# Check for dependency updates
./gradlew dependencyUpdates
```

### Code Quality

```bash
# Run lint
./gradlew lint

# Generate lint report
./gradlew lintDebug

# Format code (if detekt configured)
./gradlew detekt
```

### Gradle Daemon

```bash
# Stop daemon
./gradlew --stop

# Status
./gradlew --status

# Run without daemon
./gradlew --no-daemon build
```

---

## Troubleshooting

### Common Issues

#### 1. **Unresolved reference errors in build-logic**

**Symptom**: `Unresolved reference 'libs'` or plugin errors

**Solution**:
```bash
./gradlew --stop
./gradlew clean
./gradlew --refresh-dependencies
```

Ensure `build-logic/settings.gradle.kts` includes the version catalog:

```kotlin
versionCatalogs {
    create("libs") {
        from(files("../gradle/libs.versions.toml"))
    }
}
```

#### 2. **JVM Target Mismatch**

**Symptom**: "Inconsistent JVM-target compatibility detected"

**Solution**: Match all JVM targets in:
- `gradle.properties`: `kotlin.jvm.target=24`
- `build-logic/build.gradle.kts`: `jvmTarget.set(JvmTarget.JVM_24)`

#### 3. **Kotlin 2.3.0-RC2 not found**

**Symptom**: "Could not find org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.0-RC2"

**Solution**: Ensure Kotlin EAP repository is added to:
1. `settings.gradle.kts` - `pluginManagement.repositories`
2. `settings.gradle.kts` - `dependencyResolutionManagement.repositories`
3. `build-logic/settings.gradle.kts` - `repositories`

```kotlin
maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/eap")
```

#### 4. **Version catalog reference doesn't exist**

**Symptom**: "version reference 'xyz' doesn't exist"

**Solution**: Add missing version to `gradle/libs.versions.toml`:

```toml
[versions]
xyz = "1.0.0"

[libraries]
my-lib = { group = "com.example", name = "lib", version.ref = "xyz" }
```

#### 5. **Corrupted package.xml warnings**

**Symptom**: `[CXX5304] Found corrupted package.xml`

**Solution**: These are non-critical SDK metadata warnings. To fix:

```bash
# Delete corrupted package.xml files
rm C:\Users\<user>\AppData\Local\Android\Sdk\cmake\*\package.xml
rm C:\Users\<user>\AppData\Local\Android\Sdk\cmdline-tools\*\package.xml

# Re-sync SDK
./gradlew --refresh-dependencies
```

#### 6. **Out of Memory**

**Symptom**: "OutOfMemoryError: Java heap space"

**Solution**: Increase heap in `gradle.properties`:

```properties
org.gradle.jvmargs=-Xmx10g -XX:+HeapDumpOnOutOfMemoryError -XX:+UseParallelGC
```

---

## Best Practices

### 1. **Use Convention Plugins**

❌ **Don't**:
```kotlin
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    // ... manual configuration
}
```

✅ **Do**:
```kotlin
plugins {
    id("genesis.android.library.hilt")  // All-in-one
}
```

### 2. **Use Version Catalog**

❌ **Don't**:
```kotlin
dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
}
```

✅ **Do**:
```kotlin
dependencies {
    implementation(libs.androidx.core.ktx)
}
```

### 3. **Use Bundles for Related Dependencies**

❌ **Don't**:
```kotlin
dependencies {
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.foundation)
    implementation(libs.compose.ui.tooling.preview)
}
```

✅ **Do**:
```kotlin
dependencies {
    implementation(libs.bundles.compose)
}
```

### 4. **Use BOM for Compose**

```kotlin
dependencies {
    implementation(platform(libs.compose.bom))  // BOM manages versions
    implementation(libs.compose.ui)             // No version specified
    implementation(libs.compose.material3)      // Version from BOM
}
```

### 5. **Keep Convention Plugins Synchronized**

When updating AGP, Kotlin, or other core tools, update in this order:
1. `gradle/libs.versions.toml` - Update version numbers
2. `build-logic/build.gradle.kts` - Update if needed
3. Test `build-logic` compiles: `./gradlew :build-logic:build`
4. Test full project: `./gradlew build`

---

## Configuration Reference

### gradle.properties

```properties
# JDK Configuration
org.gradle.java.installations.auto-detect=true
org.gradle.java.installations.auto-download=true
org.gradle.java.toolchain.version=24

# Kotlin Configuration
kotlin.jvm.target=24
kotlin.jvm.target.validation.mode=warning
kotlin.code.style=official
kotlin.incremental=true

# Android Configuration
android.useAndroidX=true
android.nonFinalResIds=true
android.nonTransitiveRClass=true
android.enableAppCompileTimeRClass=true
android.builtInKotlin=false

# Build Performance
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.daemon=true
org.gradle.configureondemand=true
org.gradle.jvmargs=-Xmx10g -XX:+HeapDumpOnOutOfMemoryError -XX:+UseParallelGC

# KSP Configuration
ksp.incremental=true
ksp.kotlinApiVersion=2.3
ksp.kotlinLanguageVersion=2.3

# Compose Configuration
android.compose.experimentalStrongSkippingEnabled=true
```

---

## Maintenance

### Updating Dependencies

1. Check for updates with Dependabot or manually
2. Update versions in `gradle/libs.versions.toml`
3. Test build: `./gradlew build`
4. Run tests: `./gradlew test`
5. Commit changes

### Migrating to New Kotlin Version

1. Update `kotlin` version in `gradle/libs.versions.toml`
2. Update repository if needed (e.g., EAP for RC versions)
3. Check for breaking changes in release notes
4. Update `kotlinApiVersion` and `kotlinLanguageVersion` in `gradle.properties`
5. Test compilation

### Migrating to New AGP Version

1. Update `agp` version in `gradle/libs.versions.toml`
2. Check for required Gradle version upgrade
3. Update `gradle-wrapper.properties` if needed
4. Review AGP migration guide for breaking changes
5. Update convention plugins if API changes

---

## Resources

- [Gradle Documentation](https://docs.gradle.org/)
- [Android Gradle Plugin](https://developer.android.com/build)
- [Kotlin Gradle Plugin](https://kotlinlang.org/docs/gradle.html)
- [Version Catalogs](https://docs.gradle.org/current/userguide/platforms.html)
- [Composite Builds](https://docs.gradle.org/current/userguide/composite_builds.html)
- [JetBrains EAP Repository](https://kotlinlang.org/docs/eap.html)

---

## Support

For issues specific to the AuraKai build system, refer to:
- `build/reports/` - Build reports
- `build/reports/kotlin-build/` - Kotlin compilation reports
- `build/reports/problems/` - Gradle problems report

**Build system maintained by**: Development Team
**Last major update**: December 2025 (Kotlin 2.3.0-RC2 migration)
