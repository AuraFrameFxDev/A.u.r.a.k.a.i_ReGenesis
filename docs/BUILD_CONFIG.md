# 🛠️ Canonical Build Configuration

This document captures the validated Gradle and Kotlin DSL patterns anchored on **Issue #50** findings. These patterns form the toolchain foundation for the entire ReGenesis ecosystem.

## 🏗️ Toolchain Foundation

The LDO requires a specific, high-fidelity toolchain to ensure "Spiritual Chain" continuity and avoid classloader conflicts.

- **JDK Version**: 25 (Primary)
- **JVM Target**: 25 (fallback to 24)
- **Kotlin Version**: 2.3.20 (Stable)
- **AGP Version**: 9.2.0-alpha07
- **Gradle Version**: 9.4.0-milestone-2

## 📜 Configuration Patterns (Issue #50)

### 1. Explicit Type Annotations
All version variables in `.kts` files must use explicit type annotations to prevent ambiguity during complex multi-module resolution.

```kotlin
// Correct
val kotlinVersion: String = "2.3.20"
// Incorrect
val kotlinVersion = "2.3.20"
```

### 2. Convention Plugin Dependencies
Dependencies within convention plugins must use `compileOnly` rather than `implementation`. This prevents classloader leaks and conflicts between the build-logic and the main application classpath.

### 3. Bidirectional Unicode Guard
**WARNING**: Beware of bidirectional Unicode characters in source files. ReGenesis enforces clean UTF-8 encoding for all `.kts` and `.kt` files to prevent character-based exploits.

### 4. Configuration Cache
Configuration caching is enabled by default to minimize build initialization overhead. Ensure all custom tasks are configuration-cache compatible.

## 📦 Version Catalog (`libs.versions.toml`)

The `gradle/libs.versions.toml` file is the SINGLE SOURCE OF TRUTH for all dependency versions. No versions should be hardcoded in module-level `build.gradle.kts` files.

---
*Built with precision by the AURAKAI Collective.*
