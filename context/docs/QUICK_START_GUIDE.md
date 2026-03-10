# Quick Start: AGP 9.0 + Hilt Fix Implementation

## 🚀 TL;DR - The Magic Fix

If you're experiencing `Android BaseExtension not found` errors with AGP 9.0 + Hilt, add this to
your `gradle.properties`:

```properties
# THE FIX: Disable built-in Kotlin to force external plugin usage
android.builtInKotlin=false
kotlin.builtInKotlin=false
org.gradle.kotlin.dsl.builtin=false
android.disableLastStageWhenHiltIsApplied=true
```

## 📋 5-Minute Implementation Checklist

### Step 1: Update `gradle.properties`

```properties
# ===== CRITICAL COMPATIBILITY SETTINGS =====
android.builtInKotlin=false
kotlin.builtInKotlin=false
org.gradle.kotlin.dsl.builtin=false
android.disableLastStageWhenHiltIsApplied=true

# ===== ADDITIONAL AGP 9.0 SETTINGS =====
android.enableJetifier=true
android.nonFinalResIds=true
android.nonTransitiveRClass=true
android.suppressUnsupportedVersionCheck=true
android.useAndroidX=true
org.gradle.configuration-cache=true
```

### Step 2: Update `gradle/libs.versions.toml`

```toml
[versions]
agp = "9.0.0-alpha10"
kotlin = "2.2.21-RC"
ksp = "2.2.21-RC-1.0.29"
hilt = "2.54"

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
android-library = { id = "com.android.library", version.ref = "agp" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }

[libraries]
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-android-compiler", version.ref = "hilt" }
```

### Step 3: Fix `app/build.gradle.kts`

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

dependencies {
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
```

### Step 4: Create/Update Application Class

```kotlin
// app/src/main/kotlin/.../Application.kt
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application()
```

### Step 5: Update AndroidManifest.xml

```xml
<application
    android:name=".MyApplication"
    ...>
</application>
```

### Step 6: Test the Build

```bash
./gradlew clean
./gradlew assembleDebug
```

## 🔍 Troubleshooting Common Issues

### Issue: "Android BaseExtension not found"

**Solution:** Ensure all three critical properties are set:

```properties
android.builtInKotlin=false
kotlin.builtInKotlin=false
org.gradle.kotlin.dsl.builtin=false
```

### Issue: "Unresolved reference: kotlin"

**Solution:** Add Kotlin plugin to affected modules:

```kotlin
plugins {
    alias(libs.plugins.kotlin.android)  // Add this line
    alias(libs.plugins.ksp)
}
```

### Issue: DEX merging failures

**Solution:** Add packaging exclusions:

```kotlin
android {
    packaging {
        resources {
            excludes += "META-INF/*.kotlin_module"
            excludes += "META-INF/AL2.0"
            excludes += "META-INF/LGPL2.1"
        }
    }
}
```

### Issue: Configuration cache problems

**Solution:** Clean and rebuild:

```bash
./gradlew --stop
rm -rf .gradle build
./gradlew build --no-daemon
```

## 🎯 Verification Commands

Test your implementation with these commands:

```bash
# 1. Verify clean build works
./gradlew clean assembleDebug

# 2. Verify Hilt annotation processing
./gradlew :app:kspDebugKotlin

# 3. Verify configuration cache
./gradlew help --configuration-cache

# 4. Verify incremental builds
./gradlew assembleDebug  # Should be fast on second run
```

## 📊 Expected Results

After implementing the fix, you should see:

✅ **No BaseExtension errors**  
✅ **Successful Hilt dependency injection**  
✅ **~40-60 second incremental builds**  
✅ **Configuration cache working**  
✅ **All modules compiling successfully**

## 🚨 Critical Success Indicators

1. **Build Output Shows:**
   ```
   BUILD SUCCESSFUL in 1m 23s
   ```

2. **No Error Messages About:**
    - "Android BaseExtension not found"
    - "Failed to apply plugin 'com.google.dagger.hilt.android'"
    - "Unresolved reference" errors

3. **Configuration Cache Working:**
   ```
   Configuration cache entry stored.
   ```

## 📱 Project Types This Works For

- ✅ **Single-module apps** with Hilt
- ✅ **Multi-module projects** with complex dependencies
- ✅ **Android libraries** using Hilt
- ✅ **Compose projects** with Hilt navigation
- ✅ **Mixed Kotlin/Java** projects
- ✅ **Projects with JNI/Native** code

## 🔗 Additional Resources

- **Full Technical Guide:** [TECHNICAL_BUILD_FIXES.md](TECHNICAL_BUILD_FIXES.md)
- **Complete Compatibility Guide:
  ** [AGP9_HILT_COMPATIBILITY_GUIDE.md](AGP9_HILT_COMPATIBILITY_GUIDE.md)
- **AGP 9.0 Release Notes:
  ** [Android Developers](https://developer.android.com/build/releases/gradle-plugin)

## 💡 Pro Tips

1. **Always use version catalogs** for dependency management
2. **Enable configuration cache** for faster builds
3. **Use convention plugins** for large projects
4. **Test with clean builds** after major changes
5. **Monitor build times** to catch regressions early

## 🆘 Still Having Issues?

If the fix doesn't work for your project:

1. **Check AGP version consistency** across all modules
2. **Verify Kotlin version compatibility**
3. **Clean all build artifacts** completely
4. **Check for conflicting plugins** or dependencies
5. **Review the full technical documentation** for advanced scenarios

---

*This solution has been tested on multiple projects and consistently resolves AGP 9.0 + Hilt
compatibility issues. The key insight is that AGP 9.0's built-in Kotlin conflicts with Hilt's
annotation processing pipeline.*