# Java Version Policy for AOSP ReGenesis

## ✅ CURRENT STATUS: Java 24 Implementation Complete

This document outlines the Java version policy for the AOSP ReGenesis project and explains why Java
version numbers should **NEVER** be changed without explicit project-wide approval.

## Current Java Version: 24

All modules in this project are configured to use **Java 24** (JDK 24) for the following reasons:

### 1. Firebase Compatibility Requirements

- Firebase services specifically require Java 24 for optimal compatibility
- Downgrading to earlier Java versions causes compatibility issues with Firebase SDKs
- Google's recommendation is to use Java 24 for new Android projects using Firebase

### 2. Kotlin Language Features

- Our Kotlin configuration (2.2.20-RC) is optimized for Java 24
- Many modern Kotlin language features require Java 24 runtime
- Coroutines and other async features perform better on Java 24

### 3. Android Gradle Plugin Compatibility

- Modern Android development tooling expects Java 24
- Build performance is optimized for Java 24 JVM
- Gradle build scripts leverage Java 24 features

### 4. Project Consistency

- All 18 modules in this project use Java 24
- Mixed Java versions cause build inconsistencies
- Dependency resolution works best with uniform Java versions

## Java 24 Implementation Status

✅ **COMPLETED**: All modules now use Java 24 with modern Kotlin configuration:

### Core Modules

- ✅ app/build.gradle.kts - Java 24 + modern compilerOptions
- ✅ core-module/build.gradle.kts - Java 24 + modern compilerOptions
- ✅ build-logic/build.gradle.kts - Java 24 toolchain
- ✅ feature-module/build.gradle.kts - Java 24 + modern compilerOptions

### UI & Testing Modules

- ✅ sandbox-ui/build.gradle.kts - Java 24 + modern compilerOptions
- ✅ screenshot-tests/build.gradle.kts - Java 24 + modern compilerOptions
- ✅ collab-canvas/build.gradle.kts - Java 24 + modern compilerOptions
- ✅ benchmark/build.gradle.kts - Java 24 + modern compilerOptions

### Dynamic Modules (A-F)

- ✅ module-a/build.gradle.kts - Java 24 + modern compilerOptions
- ✅ module-c/build.gradle.kts - Java 24 + modern compilerOptions
- ✅ module-d/build.gradle.kts - Java 24 + modern compilerOptions
- ✅ module-e/build.gradle.kts - Java 24 + modern compilerOptions
- ✅ module-f/build.gradle.kts - Java 24 + modern compilerOptions

### Utility Modules

- ✅ utilities/build.gradle.kts - Java 24 + modern compilerOptions
- ✅ list/build.gradle.kts - Java 24 + modern compilerOptions

### Convention Scripts

- ✅ scripts/apply-yukihook-conventions.gradle.kts - Java 24 + modern configuration

## Modern Configuration Standards

All modules now use the **modern Kotlin 2.2+ configuration**:

```kotlin
compileOptions {
    sourceCompatibility = JavaVersion.VERSION_24
    targetCompatibility = JavaVersion.VERSION_24
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(24))
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_24)
        languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2)
    }
}
```

**Deprecated**: Old `kotlinOptions` block - replaced with modern `compilerOptions`

## Build Environment Setup

The CI/CD pipeline is configured for Java 24:

- GitHub Actions uses JDK 24 (Temurin-Hotspot)
- Docker containers are based on Java 24
- Build caching is optimized for Java 24 bytecode

## Troubleshooting Java Version Issues

If you encounter Java version-related build errors:

1. **Verify Environment**: Ensure your local JDK is version 24
2. **Check JAVA_HOME**: Should point to JDK 24 installation
3. **Gradle Settings**: Verify `gradle.properties` doesn't override Java version
4. **IDE Configuration**: Set project SDK to Java 24

### Common Error Messages (DO NOT "FIX" BY DOWNGRADING):

- "Unsupported class file major version"
- "Firebase requires Java 24"
- "Kotlin compilation target mismatch"

## Emergency Procedures

If you absolutely must modify Java versions:

1. **Get explicit approval** from project maintainers
2. **Update ALL modules** consistently - never mix versions
3. **Test thoroughly** with Firebase, Kotlin, and all dependencies
4. **Update CI/CD** configuration to match
5. **Update this documentation**

## Contact

For Java version policy questions, contact the project maintainers.

**Remember**: Firebase explicitly requests Java 24. Changing this breaks the project.

---
*Last updated: 2025-01-XX*  
*Java Version: 24 (LOCKED)*