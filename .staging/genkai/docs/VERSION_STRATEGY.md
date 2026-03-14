# Version Strategy & Architectural Rationale

## 🎯 Project Philosophy: Bleeding-Edge with Stability Guardrails

This document explains WHY specific versions were chosen and how they align with our build architecture.

---

## Core Architectural Principles

### 1. **JVM Toolchain vs Bytecode Target**
```kotlin
// We use JDK 25 toolchain for DEVELOPMENT
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

// Target Java 24 BYTECODE with Java 25 fallback
compileOptions {
    sourceCompatibility = JavaVersion.VERSION_24
    targetCompatibility = JavaVersion.VERSION_24
}

kotlinOptions {
    jvmTarget = "24" // MUST match targetCompatibility
}
```

**Rationale**:
- **Java 25 toolchain** = latest compiler features, best IDE support
- **Java 24 bytecode target** = modern language features, API 36 alignment
- **Trade-off**: Narrower device compatibility, but aligned with min-sdk 34 (Android 14)
- **Why 24 not 17**: Android 14+ (API 34) supports Java 24 bytecode natively

---

## Version Selection Strategy

### Build Tools (Preview/Beta)

#### **AGP 9.0.0-alpha13** (Preview)
```toml
agp = "9.0.0-alpha13"
```

**Why Preview?**
- ✅ Full support for Android API Level 36 (Android 16)
- ✅ Improved plugin isolation for convention plugins
- ✅ Better Gradle 9.x compatibility
- ✅ Performance improvements for multi-module builds
- ⚠️ Preview stability trade-offs accepted

**Alternative**: AGP 8.13.0 (stable)
- Maximum API support: 36
- More stable, but less optimized for our convention plugin architecture

**Decision**: Preview acceptable due to:
1. Targeting cutting-edge Android 16 APIs
2. Convention plugin architecture benefits from AGP 9.0 improvements
3. Development/testing environment tolerance for preview builds

---

#### **Kotlin 2.3.0-Beta3** (Beta)
```toml
kotlin = "2.3.0-Beta3"
```

**Why Beta?**
- ✅ K2 compiler improvements (faster compilation)
- ✅ New language features for cleaner code
- ✅ Better support for Compose multiplatform
- ✅ Still targets JVM 24 bytecode (aligned with our architecture)
- ⚠️ Beta stability acceptable for development

**Alternative**: Kotlin 2.2.21 (stable)
- Fully stable release
- Complete K2 compiler
- Recommended for production apps

**Decision**: Beta acceptable due to:
1. K2 compiler is production-ready in 2.2+
2. 2.3.0 features useful for project architecture
3. Still maintains JVM 17 bytecode target

---

#### **KSP Version Alignment**
```toml
ksp = "2.3.0"  # ❌ INCORRECT - incomplete version
```

**REQUIRED FIX**:
```toml
ksp = "2.3.0-Beta3-1.0.28"  # ✅ CORRECT - matches Kotlin version
```

**Rationale**:
- KSP version MUST match Kotlin version exactly
- Format: `{kotlin-version}-{ksp-version}`
- Critical for YukiHook API annotation processing

---

### Android SDK Targets

```toml
compile-sdk = "36"  # Android 16 (cutting edge)
min-sdk = "34"      # Android 14 (recent)
target-sdk = "36"   # Android 16
```

**Why API 36?**
- ✅ Access to Android 16 features
- ✅ Material 3 Expressive design system
- ✅ Latest performance improvements
- ⚠️ Limited device compatibility (min-sdk 34)

**Trade-offs**:
- **Pros**: Latest Android features, future-proof
- **Cons**: Excludes Android 13 and below (min-sdk 34 is aggressive)

**Decision**: Acceptable for:
1. Internal/testing apps
2. Pixel-focused development
3. Showcase of latest Android capabilities

**Production Recommendation**: Consider min-sdk 26 (Android 8.0) for wider reach

---

### AndroidX Libraries Strategy

#### **Room 2.6.1** → **2.8.3 Available**
```toml
room = "2.6.1"  # Current
room = "2.8.3"  # Latest (Oct 2025)
```

**Update Recommendation**: ✅ **Update to 2.8.3**
- Major improvements in KSP processing
- Better multiplatform support
- Bug fixes and performance improvements
- **No breaking changes** from 2.6.x

---

#### **NavigationCompose 2.9.5** vs **2.8.9**
```toml
navigationCompose = "2.9.5"  # Current (RC/Alpha?)
```

**Issue**: Version 2.9.5 doesn't exist in stable releases
- Latest stable: 2.8.9 (March 2025)
- Latest RC: 2.9.0-rc01 (April 2025)

**Update Recommendation**: ⚠️ **Verify version source**
- If using snapshots: Document source in TOML
- If typo: Correct to 2.8.9 or 2.9.0-rc01

---

### Networking Libraries (Stable)

```toml
retrofit = "3.0.0"   # Latest stable
okhttp = "5.3.0"     # Latest stable
ktor = "3.3.1"       # Latest stable
```

**Strategy**: Use stable releases
- **Rationale**: Network stack must be reliable
- **Exception**: None - stability critical here

---

### System Libraries (Xposed/Root)

```toml
xposed = "82"              # Classic Xposed API
yukihookapi = "1.3.1"      # Latest (Sept 2025)
libsu = "6.0.0"            # Latest (June 2025)
kavaref = "1.0.1"          # Reflection API
```

**Strategy**: Latest stable for root/system access
- **YukiHookAPI**: Modern Xposed implementation
- **LibSu**: Essential for root operations
- **KavaRef**: Required by YukiHookAPI

**KSP Configuration**:
```kotlin
ksp {
    arg("YUKIHOOK_PACKAGE_NAME", project.group.toString())
}
```

---

## Convention Plugin Architecture Impact

### **Before**: Monolithic `subprojects {}` Block
```kotlin
subprojects {
    // Everything applied to ALL modules
    // No parallelization
    // No selective application
}
```

### **After**: Composable Convention Plugins
```kotlin
plugins {
    id("genesis.android.base")     // Foundation
    id("genesis.android.yukihook") // Specialization
    id("genesis.android.compose")  // Feature
}
```

**Version Impact**:
- AGP 9.0 better supports plugin isolation
- Kotlin 2.3 improves compilation speed with K2
- Preview versions acceptable due to:
  - Isolated failure domains
  - Easier rollback per module
  - Better build cache utilization

---

## Recommended Updates (Aligned with Strategy)

### **High Priority** (Functional/Performance)

1. **KSP Version Fix** ⚠️ **CRITICAL**
   ```toml
   ksp = "2.3.0-Beta3-1.0.28"  # Match Kotlin exactly
   ```

2. **Room Update** ✅ **Recommended**
   ```toml
   room = "2.8.3"  # Significant improvements
   ```

3. **Navigation Version Verification** ⚠️ **Check**
   ```toml
   navigationCompose = "2.8.9"  # or verify 2.9.5 source
   ```

### **Medium Priority** (Nice to Have)

4. **Coil 3.x Migration** 📦 **Breaking Change**
   ```toml
   coil = "3.3.0"  # New namespace: io.coil-kt.coil3
   ```
   - Requires code changes
   - Schedule for separate migration sprint

5. **Lottie Update** ✅ **Safe**
   ```toml
   lottie = "6.6.9"  # From 6.7.1 (typo correction)
   ```

### **Low Priority** (Production Considerations)

6. **Stable Tooling** 🔄 **For Production Release**
   ```toml
   agp = "8.13.0"          # When shipping production APK
   kotlin = "2.2.21"        # When stability > features
   ```

---

## Testing Strategy Versions

```toml
junit = "4.13.2"           # JUnit 4 (legacy tests)
junitJupiter = "6.0.1"     # JUnit 6 (new tests, requires Java 17)
mockk = "1.14.6"           # Latest stable
espresso = "3.7.0"         # Latest (July 2025)
```

**JUnit 6 Note**:
- Requires **Java 17 baseline minimum** (we use Java 24, fully compatible)
- Kotlin 2.2+ required
- **Perfect alignment** with our cutting-edge architecture

---

## Version Update Cadence

| Category | Update Frequency | Rationale |
|----------|-----------------|-----------|
| **Build Tools** | Every major release | Performance & API support |
| **AndroidX** | Monthly | Bug fixes & features |
| **Networking** | Quarterly | Stability focus |
| **Testing** | As needed | Test infrastructure |
| **System/Root** | On release | YukiHook/Xposed updates |

---

## Compatibility Matrix

| Component | Version | Java Target | Kotlin | AGP | Status |
|-----------|---------|-------------|--------|-----|--------|
| **AGP** | 9.0.0-alpha13 | - | 2.0+ | - | ⚠️ Preview |
| **Kotlin** | 2.3.0-Beta3 | 24 | - | 8.0+ | ⚠️ Beta |
| **KSP** | 2.3.0-Beta3-1.0.28 | 24 | 2.3.0-Beta3 | 8.0+ | ⚠️ Must Match |
| **YukiHook** | 1.3.1 | 24 | 2.0+ | 8.0+ | ✅ Stable |
| **Room** | 2.8.3 | 24 | 2.0+ | 8.0+ | ✅ Stable |
| **Compose** | 2025.10.01 | 24 | 2.0+ | 8.0+ | ✅ Stable |

---

## Summary

**Our version strategy prioritizes**:
1. **Cutting-edge Android API access** (API 36, Android 16)
2. **Modern build performance** (AGP 9.0, Kotlin 2.3 K2 compiler)
3. **Architecture-first approach** (Convention plugins, not subprojects)
4. **Controlled risk** (Preview/beta for development, stable for production)

**The one non-negotiable**:
- **JVM 24 bytecode target** with Java 25 toolchain (modern features + API 36 alignment)

**Next actions**:
1. Fix KSP version immediately
2. Update Room to 2.8.3
3. Verify navigation version source
4. Plan Coil 3.x migration separately
5. Document rollback plan for AGP/Kotlin previews
