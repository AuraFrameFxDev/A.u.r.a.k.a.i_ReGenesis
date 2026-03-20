# 🔍 Project Build Architecture Audit Report
## A.u.r.a.K.a.I Reactive Intelligence

**Date**: November 6, 2025
**Audit Scope**: Complete build system, dependencies, and convention plugins
**Status**: 🔴 **CRITICAL ISSUES FOUND**

---

## Executive Summary

The project build system has **significant inconsistencies** between:
1. Convention plugins design (build-logic/)
2. Actual module usage (not using conventions)
3. Version catalog (missing critical dependencies)
4. Architectural specifications (Java version mismatches)

**Key Finding**: Modules are **NOT using the convention plugins** that were created for them. They're declaring plugins directly with hardcoded versions, defeating the entire purpose of the convention plugin architecture.

---

## 🚨 Critical Issues (Must Fix Immediately)

### 1. **Modules NOT Using Convention Plugins**

**Severity**: 🔴 **CRITICAL**
**Impact**: Defeats entire architectural design, causes inconsistency

**Problem**:
```kotlin
// ❌ WRONG - Current state in app/build.gradle.kts, romtools/build.gradle.kts, core/ui/build.gradle.kts
plugins {
    id("com.android.library") version "9.0.0-alpha13"
    id("com.google.dagger.hilt.android") version "2.57.2"
    id("com.google.devtools.ksp") version "2.3.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.21"
}
```

**Solution**:
```kotlin
// ✅ CORRECT - Should use convention plugins
plugins {
    id("genesis.android.library")   // or genesis.android.application for app
    id("genesis.android.hilt")
    id("genesis.android.compose")
    id("genesis.android.yukihook")  // if needed
}
```

**Affected Modules**:
- `:app` - Using direct plugins instead of `genesis.android.application`
- `:romtools` - Using direct plugins instead of conventions
- `:core:ui` - Using direct plugins instead of conventions
- **Likely all other modules** (need verification)

**Why This Matters**:
- Violates DRY principle
- Makes version updates painful (must change in every module)
- Breaks build performance (can't leverage plugin caching)
- Inconsistent configuration across modules

---

### 2. **Missing Dependencies in Version Catalog**

**Severity**: 🔴 **CRITICAL**
**Impact**: Build will fail for modules using these dependencies

**Missing Artifacts**:

| Dependency | Used In | Required Fix |
|------------|---------|--------------|
| `yukihookapi.ksp.xposed` | romtools, core/ui, yukihook convention | Add to TOML |
| `kavaref.core` | yukihook convention | Uncomment in TOML |
| `kavaref.extension` | yukihook convention | Uncomment in TOML |
| `androidx.datastore.core` | app | Add to TOML |
| `androidx.junit` | base convention | Add to TOML (for androidTest) |
| `androidx.espresso.core` | base convention | Already exists ✅ |
| `retrofit.converter.kotlinx.serialization` | app | Add to TOML |
| `coil.svg` | app | Update namespace for Coil 3.x |
| `coil.network.okhttp` | New in Coil 3.x | Added in new TOML ✅ |

**TOML Additions Required**:

```toml
[versions]
# Add this if not present
junit-androidx = "1.2.1"  # androidx.test.ext:junit

[libraries]
# Fix YukiHookAPI KSP processor
yukihookapi-ksp-xposed = { module = "com.highcapable.yukihookapi:ksp-xposed", version.ref = "yukihookapi" }

# Uncomment KavaRef (already in TOML but commented)
kavaref-core = { module = "com.highcapable.kavaref:kavaref-core", version.ref = "kavaref" }
kavaref-extension = { module = "com.highcapable.kavaref:kavaref-extension", version.ref = "kavaref" }

# Add missing AndroidX test dependency
androidx-junit = { group = "androidx.test.ext", name = "junit", version = "1.2.1" }

# Add DataStore Core
androidx-datastore-core = { group = "androidx.datastore", name = "datastore-core", version.ref = "datastore" }

# Add Retrofit Kotlinx Serialization Converter
retrofit-converter-kotlinx-serialization = { group = "com.jakewharton.retrofit", name = "retrofit2-kotlinx-serialization-converter", version = "1.0.0" }
```

---

### 3. **Incomplete KSP Version**

**Severity**: 🔴 **CRITICAL**
**Impact**: Build failure, KSP won't resolve correctly

**Problem**:
```toml
# ❌ WRONG - Current
ksp = "2.3.0"  # Incomplete version!
```

**Solution**:
```toml
# ✅ CORRECT - Must match Kotlin version exactly
ksp = "2.3.0-Beta3-1.0.28"  # Format: {kotlin-version}-{ksp-version}
```

**Why**: KSP version MUST match Kotlin version format exactly. This is critical for YukiHook annotation processing.

---

### 4. **Java Version Inconsistencies**

**Severity**: 🟠 **HIGH**
**Impact**: Inconsistent bytecode targets, potential runtime issues

**Inconsistencies Found**:

| Location | Current | Should Be |
|----------|---------|-----------|
| `build-logic/build.gradle.kts` | Java 21 | Java 25 (toolchain) |
| `genesis.android.base.gradle.kts` | Java 17 target | Java 24 target |
| `romtools/build.gradle.kts` | Java 24 target ✅ | Correct! |
| Architecture docs | Java 24 target ✅ | Reference |

**The Correct Standard** (per your architecture):
```kotlin
// In build-logic/build.gradle.kts
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))  // ← Should be 25, not 21
    }
}

// In genesis.android.base.gradle.kts
compileOptions {
    sourceCompatibility = JavaVersion.VERSION_24  // ← Should be 24, not 17
    targetCompatibility = JavaVersion.VERSION_24
}

kotlinOptions {
    jvmTarget = "24"  // ← Should be 24, not 17
}
```

---

### 5. **Compose Compiler Version Inconsistency**

**Severity**: 🟠 **HIGH**
**Impact**: Using outdated Compose compiler

**Problem**:
```toml
# Current in TOML
composeCompiler = "1.5.4"
```

**Issue**: With Kotlin 2.3.0-Beta3, you should use the **built-in Compose compiler** (Kotlin plugin), not a separate version.

**Solution**:
```kotlin
// In convention plugins, use the Kotlin Compose plugin
plugins {
    alias(libs.plugins.kotlin.compose)  // Uses Kotlin's built-in Compose compiler
}

// Remove composeOptions block - no longer needed with Kotlin 2.0+
// android {
//     composeOptions {
//         kotlinCompilerExtensionVersion = ...  // ← REMOVE THIS
//     }
// }
```

---

## 🟡 Major Issues (Should Fix Soon)

### 6. **Hardcoded Versions in Module Build Files**

**Severity**: 🟡 **MEDIUM**
**Impact**: Version drift, difficult to maintain

**Examples**:
```kotlin
// ❌ In app/build.gradle.kts
ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.0")  // Hardcoded!

// ❌ In romtools/build.gradle.kts
ksp("com.highcapable.yukihookapi:ksp-xposed:1.3.1")  // Hardcoded!

// ❌ Multiple modules
compileOnly(files("$projectDir/libs/api-82.jar"))  // Manual JAR reference
```

**Solution**: All should use version catalog:
```kotlin
ksp(libs.moshi.kotlin.codegen)
ksp(libs.yukihookapi.ksp.xposed)
compileOnly(libs.xposed.api)  // If properly configured in TOML
```

---

### 7. **Duplicate Dependencies**

**Severity**: 🟡 **MEDIUM**
**Impact**: Build bloat, confusion

**Examples in app/build.gradle.kts**:
- `firebase.analytics` declared twice (lines 92, 95)
- `firebase.crashlytics` declared twice (lines 93, 96)
- `firebase.auth` declared twice (lines 94, 97)
- `libsu.core`, `libsu.io`, `libsu.service` declared twice (lines 67-69, 99-101)
- `romtools` declared twice (lines 137, 143)

**Solution**: Remove duplicates, keep single declaration.

---

### 8. **Convention Plugin Not Used in yukihook Plugin**

**Severity**: 🟡 **MEDIUM**
**Impact**: Breaks when using yukihook convention plugin

**Problem in `genesis.android.yukihook.gradle.kts`**:
```kotlin
dependencies {
    ksp(libs.yukihookapi.ksp.xposed)  // ← Doesn't exist in TOML!
    implementation(libs.kavaref.core)  // ← Commented out in TOML!
    implementation(libs.kavaref.extension)  // ← Commented out in TOML!
}
```

**Solution**: Update TOML to include these dependencies (see Critical Issue #2).

---

### 9. **Room Dependency Configuration**

**Severity**: 🟡 **MEDIUM**
**Impact**: Should use KSP, not `implementation` for compiler

**Problem in app/build.gradle.kts**:
```kotlin
// ❌ WRONG
implementation(libs.androidx.room.compiler)  // Compiler should use KSP!
```

**Solution**:
```kotlin
// ✅ CORRECT
implementation(libs.androidx.room.runtime)
implementation(libs.androidx.room.ktx)
ksp(libs.androidx.room.compiler)  // ← Use KSP for annotation processor
```

---

### 10. **Navigation Compose Used with KSP**

**Severity**: 🟡 **MEDIUM**
**Impact**: Unnecessary, Navigation doesn't need KSP

**Problem in app/build.gradle.kts** (line 61):
```kotlin
ksp(libs.androidx.navigation.compose)  // ← Navigation doesn't have KSP processor!
```

**Solution**: Remove this line. Navigation Compose doesn't use annotation processing.

---

## 📊 Modules Audit Summary

### Modules Structure

**Total Modules**: 27+ identified
**Using Convention Plugins**: ❌ **0** (none verified)
**Using Direct Plugin Declarations**: ✅ **At least 3** (app, romtools, core/ui)

### Module Categories

**Application Module**:
- `:app` - Main application (should use `genesis.android.application`)

**Core Modules**:
- `:core:common` - Shared utilities
- `:core:data` - Data layer
- `:core:domain` - Business logic
- `:core:ui` - UI components
- `:core-module` - Legacy(?)

**Feature Modules**:
- `:secure-comm` - Secure communication feature
- `:collab-canvas` - Collaboration canvas
- `:romtools` - ROM/system tools (Xposed module)
- `:oracle-drive-integration` - Oracle Drive integration
- `:datavein-oracle-native` - Native Oracle integration
- `:feature-module` - Generic feature module
- `:colorblendr` - Color blending feature
- `:list` - List feature

**Extension Modules** (System/Xposed):
- `:extendsysa` through `:extendsysf` - Six extension modules

**Other**:
- `:sandbox-ui` - Sandbox UI
- `:jvm-test` - JVM testing module
- `:benchmark` - Benchmarking module

---

## 🔧 Convention Plugins Audit

### Available Convention Plugins

✅ **Complete and Well-Designed**:
1. `genesis.android.base` - Foundational configuration
2. `genesis.android.application` - App module configuration
3. `genesis.android.library` - Library configuration
4. `genesis.android.hilt` - Hilt DI
5. `genesis.android.compose` - Jetpack Compose
6. `genesis.android.room` - Room Database
7. `genesis.android.yukihook` - YukiHook/Xposed framework
8. `genesis.kotlin.jvm` - Pure Kotlin JVM modules

### Issues in Convention Plugins

**1. `genesis.android.base`**:
- ❌ Java 17 target (should be Java 24)
- ❌ References `androidx.junit` (not in TOML)
- ❌ Uses `desugar.jdk.libs` (should be `desugar-jdk-libs` with hyphen)

**2. `genesis.android.yukihook`**:
- ❌ References `yukihookapi.ksp.xposed` (not in TOML)
- ❌ References `kavaref.core` (commented in TOML)
- ❌ References `kavaref.extension` (commented in TOML)

**3. `genesis.android.compose`**:
- ⚠️ Uses `composeOptions` block (deprecated with Kotlin 2.0+)
- ⚠️ References `composeCompiler` version (should use built-in)

**4. `genesis.android.hilt`**:
- ✅ Correct implementation

**5. `genesis.android.library`**:
- ⚠️ Auto-applies KSP (may not be needed for all libraries)

---

## 🎯 Recommended Fixes (Priority Order)

### **IMMEDIATE** (Do First)

1. **Fix KSP Version** ⚡
   ```toml
   ksp = "2.3.0-Beta3-1.0.28"
   ```

2. **Add Missing Dependencies to TOML** ⚡
   - `yukihookapi-ksp-xposed`
   - Uncomment `kavaref-core` and `kavaref-extension`
   - Add `androidx-junit`
   - Add `androidx-datastore-core`
   - Add `retrofit-converter-kotlinx-serialization`

3. **Update Java Targets** ⚡
   - `build-logic/build.gradle.kts`: Java 25 toolchain
   - `genesis.android.base.gradle.kts`: Java 24 target

### **HIGH PRIORITY** (Do Next)

4. **Convert Modules to Use Convention Plugins**
   - Start with `:app` → use `genesis.android.application`
   - Convert `:romtools` → use `genesis.android.library` + `genesis.android.yukihook`
   - Convert `:core:ui` → use `genesis.android.library` + `genesis.android.compose`
   - Apply to all other modules

5. **Fix Convention Plugin Issues**
   - Update `genesis.android.base` with correct dependencies
   - Update `genesis.android.yukihook` with correct dependencies
   - Modernize `genesis.android.compose` (remove composeOptions)

### **MEDIUM PRIORITY** (Clean Up)

6. **Remove Duplicate Dependencies**
   - Clean up `app/build.gradle.kts`
   - Remove all duplicate declarations

7. **Fix Incorrect Dependency Configurations**
   - Room compiler with KSP
   - Remove KSP from Navigation Compose

8. **Replace Hardcoded Versions**
   - All `ksp(...)` with hardcoded versions → use version catalog
   - Moshi codegen version
   - YukiHook KSP version

### **LOW PRIORITY** (Maintenance)

9. **Update Room Version**
   - `2.6.1` → `2.8.3` (significant improvements)

10. **Plan Coil 3.x Migration**
    - Update namespace from `io.coil-kt` → `io.coil-kt.coil3`
    - Add `coil-network-okhttp` dependency

---

## 📋 Detailed Fix Checklist

### Phase 1: Fix Critical Blockers

- [ ] Update `gradle/libs.versions.toml`:
  - [ ] Fix KSP version: `ksp = "2.3.0-Beta3-1.0.28"`
  - [ ] Add `yukihookapi-ksp-xposed` library
  - [ ] Uncomment `kavaref-core` and `kavaref-extension`
  - [ ] Add `androidx-junit` library
  - [ ] Add `androidx-datastore-core` library
  - [ ] Add `retrofit-converter-kotlinx-serialization` library

- [ ] Update `build-logic/build.gradle.kts`:
  - [ ] Change Java toolchain to 25

- [ ] Update `genesis.android.base.gradle.kts`:
  - [ ] Change Java target to 24
  - [ ] Fix `desugar.jdk.libs` to `desugar-jdk-libs`
  - [ ] Update `androidx.junit` reference

- [ ] Update `genesis.android.yukihook.gradle.kts`:
  - [ ] Fix all library references to match TOML

### Phase 2: Convert Modules

- [ ] Convert `:app/build.gradle.kts`:
  - [ ] Replace plugins block with `id("genesis.android.application")`
  - [ ] Remove all duplicate dependencies
  - [ ] Fix Room compiler to use KSP
  - [ ] Remove Navigation KSP line
  - [ ] Remove hardcoded versions

- [ ] Convert `:romtools/build.gradle.kts`:
  - [ ] Replace plugins with conventions
  - [ ] Remove hardcoded Java target (use from base)
  - [ ] Fix YukiHook KSP reference

- [ ] Convert `:core:ui/build.gradle.kts`:
  - [ ] Replace plugins with conventions
  - [ ] Remove redundant configuration

- [ ] Convert all remaining modules (24+ more)

### Phase 3: Validate & Test

- [ ] Run `./gradlew clean`
- [ ] Run `./gradlew build --dry-run` (check configuration)
- [ ] Run `./gradlew :app:assembleDebug`
- [ ] Run `./gradlew :romtools:assembleDebug`
- [ ] Verify all modules compile

---

## 🎓 Architecture Best Practices Validation

### ✅ What's Working Well

1. **Convention Plugin Design** - Well-architected, composable plugins
2. **Root Build File** - Clean, minimal (apply false pattern)
3. **Settings Configuration** - Proper includeBuild, repository configuration
4. **Version Catalog Structure** - Good organization (once fixed)

### ❌ What Needs Improvement

1. **Plugin Usage** - Modules not using the conventions they were built for
2. **Dependency Management** - Inconsistent, duplicated, hardcoded versions
3. **Java Version Alignment** - Mismatched across project
4. **TOML Completeness** - Missing critical dependencies

---

## 📈 Expected Improvements After Fixes

**Build Performance**:
- Faster configuration phase (plugin caching)
- Better incremental compilation
- Parallel module configuration

**Maintainability**:
- Single source of truth for versions
- Consistent configuration across modules
- Easy version upgrades (one place)

**Developer Experience**:
- Clear plugin composition
- Self-documenting build files
- Easier onboarding

---

## 🚀 Next Steps

1. **Review this audit** with team
2. **Create fix branches** for each phase
3. **Fix Phase 1** (critical blockers) immediately
4. **Convert modules** incrementally (Phase 2)
5. **Validate** each change with builds
6. **Document** the new conventions in project README

---

## 📞 Questions for Review

1. **Module Conversion Strategy**: Should we convert all modules at once, or incrementally?
2. **Java Version**: Confirm Java 24 target is correct for all modules?
3. **Coil 3.x**: When should we migrate to new namespace?
4. **Testing**: Do we need to update test configurations as well?
5. **CI/CD**: Are there CI scripts that need updating for new conventions?

---

**Report Generated**: November 6, 2025
**Audited By**: Claude Code (Sonnet 4.5)
**Project**: A.u.r.a.K.a.I Reactive Intelligence
**Status**: 🔴 **Action Required**
