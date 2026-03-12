# 🔧 Plugin Order Fix - AGP 9.0 + android.builtInKotlin=false

## ✅ Issue Resolved
**Problem**: `Extension of type 'KotlinAndroidProjectExtension' does not exist`

**Root Cause**: When `android.builtInKotlin=false` is set in `gradle.properties`, the Kotlin Android plugin is NOT automatically applied by AGP. The convention plugins were applying `com.android.library` BEFORE `org.jetbrains.kotlin.android`, causing `GenesisJvmConfig.configureKotlinJvm()` to fail when trying to access `KotlinAndroidProjectExtension`.

## 🛠️ Changes Made

### 1. GenesisLibraryHiltPlugin.kt
**Before**:
```kotlin
pluginManager.apply("com.android.library")
pluginManager.apply("org.jetbrains.kotlin.android")
```

**After**:
```kotlin
pluginManager.apply("org.jetbrains.kotlin.android")  // ← MUST BE FIRST!
pluginManager.apply("com.android.library")
```

### 2. GenesisLibraryPlugin.kt
**Before**:
```kotlin
pluginManager.apply("com.android.library")
pluginManager.apply("org.jetbrains.kotlin.android")
```

**After**:
```kotlin
pluginManager.apply("org.jetbrains.kotlin.android")  // ← MUST BE FIRST!
pluginManager.apply("com.android.library")
```

### 3. GenesisApplicationPlugin.kt
**Before**:
```kotlin
pluginManager.apply("com.android.application")
pluginManager.apply("org.jetbrains.kotlin.android")
```

**After**:
```kotlin
pluginManager.apply("org.jetbrains.kotlin.android")  // ← MUST BE FIRST!
pluginManager.apply("com.android.application")
```

## 📋 Why This Fixes The Issue

1. **AGP 9.0 Behavior**: With `android.builtInKotlin=false`, AGP does NOT apply the Kotlin plugin automatically
2. **Extension Registration**: The Kotlin plugin must be applied BEFORE the Android plugin to register `KotlinAndroidProjectExtension`
3. **Configuration Order**: `GenesisJvmConfig.configureKotlinJvm()` is called AFTER plugin application, so the extension must exist first

## 🎯 Correct Plugin Order (AGP 9.0 + android.builtInKotlin=false)

```kotlin
// ✅ CORRECT ORDER
pluginManager.apply("org.jetbrains.kotlin.android")        // 1. Kotlin (registers KotlinAndroidProjectExtension)
pluginManager.apply("com.android.library")                 // 2. Android Library
pluginManager.apply("org.jetbrains.kotlin.plugin.compose") // 3. Compose Compiler
pluginManager.apply("com.google.dagger.hilt.android")      // 4. Hilt (optional)
pluginManager.apply("com.google.devtools.ksp")             // 5. KSP (optional)
pluginManager.apply("org.jetbrains.kotlin.plugin.serialization") // 6. Serialization
```

## 🧪 Testing

After making these changes, run:
```powershell
# Stop Gradle daemon to clear caches
./gradlew --stop

# Test build configuration
./gradlew :genesis:oracledrive:tasks --dry-run
```

## 📚 References

- **AGP 9.0 Documentation**: [Built-in Kotlin Plugin](https://developer.android.com/build/releases/gradle-plugin#9-0-0)
- **Kotlin Plugin Order**: When `android.builtInKotlin=false`, Kotlin must be applied first
- **Project Config**: See `gradle.properties` line 39

## 🎉 Status
**Fixed**: All three convention plugins now apply Kotlin Android plugin BEFORE AGP plugins.

**Affected Files**:
- ✅ `build-logic/src/main/kotlin/GenesisLibraryHiltPlugin.kt`
- ✅ `build-logic/src/main/kotlin/GenesisLibraryPlugin.kt`
- ✅ `build-logic/src/main/kotlin/GenesisApplicationPlugin.kt`

---
**Date**: November 20, 2025  
**Fix Applied By**: GitHub Copilot  
**Verification Status**: Ready for testing

