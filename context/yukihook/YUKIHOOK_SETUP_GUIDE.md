# YukiHookAPI Universal Setup Guide

This guide provides comprehensive instructions for setting up YukiHookAPI across all modules in your
project.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Version Catalog Setup](#version-catalog-setup)
3. [Module Configuration](#module-configuration)
4. [Basic Xposed Module Setup](#basic-xposed-module-setup)
5. [Advanced Configuration](#advanced-configuration)
6. [Troubleshooting](#troubleshooting)

## Prerequisites

- Android Studio Flamingo (2022.2.1) or later
- Gradle 8.0+
- Android Gradle Plugin 8.0.0+
- Kotlin 1.8.0+
- JDK 17+

## Version Catalog Setup

Your `libs.versions.toml` should include these YukiHookAPI dependencies:

```toml
[versions]
yuki = "1.3.0"
yukihookapi = "1.3.0"

[plugins]
yukihook = { id = "com.highcapable.yukihook", version = "1.3.0" }
yukihook-ksp = { id = "com.highcapable.yukihook.ksp", version = "1.3.0" }

[libraries]
# Core YukiHookAPI
yuki = { group = "com.highcapable.yukihookapi", name = "api", version.ref = "yukihookapi" }
yuki-ksp = { group = "com.highcapable.yukihookapi", name = "ksp-xposed", version.ref = "yukihookapi" }
yuki-prefs = { group = "com.highcapable.yukihookapi", name = "prefs", version.ref = "yukihookapi" }
yuki-bridge = { group = "com.highcapable.yukihookapi", name = "bridge", version.ref = "yukihookapi" }

# Xposed Framework
xposed = { group = "de.robv.android.xposed", name = "api", version = "82" }
```

## Module Configuration

### For Xposed Modules

1. Apply plugins in your module's `build.gradle.kts`:

```kotlin
plugins {
    id("com.android.application")  // or id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.highcapable.yukihook")
    id("com.highcapable.yukihook.ksp")
}

android {
    // Enable data binding and view binding if needed
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
    
    // Required for YukiHook
    buildTypes {
        debug {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    // YukiHook API
    implementation(libs.yuki)
    ksp(libs.yuki.ksp)
    
    // Optional: Preferences for module settings
    implementation(libs.yuki.prefs)
    
    // Optional: Bridge for cross-process communication
    implementation(libs.yuki.bridge)
}

yukihook {
    // Enable the API for the current build type
    isEnable = true
    
    // Enable debug mode
    isDebug = true
    
    // Configure the module name (optional)
    name = "YourModuleName"
    
    // Configure the module package name (optional)
    // This should match your applicationId
    // packageName = "com.your.package.name"
}
```

## Basic Xposed Module Setup

1. Create your module entry class:

```kotlin
@ModuleEntry
class YourModuleEntry : IModuleEntry {
    override fun onHook() = YukiHookAPI.encase {
        // Your module code here
    }
}
```

2. Update `AndroidManifest.xml`:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.your.package.name">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.YourApp">
        
        <meta-data
            android:name="xposedmodule"
            android:value="true" />
            
        <meta-data
            android:name="xposeddescription"
            android:value="Your module description" />
            
        <meta-data
            android:name="xposedminversion"
            android:value="93" />
            
    </application>
</manifest>
```

## Advanced Configuration

### For Multi-Module Projects

For non-Xposed modules that need YukiHook functionality:

```kotlin
// In your module's build.gradle.kts
dependencies {
    // Core YukiHook API without Xposed dependencies
    implementation("com.highcapable.yukihookapi:api:1.3.0")
    
    // Optional: Use the annotation processor if needed
    ksp("com.highcapable.yukihookapi:ksp-xposed:1.3.0")
}
```

### ProGuard/R8 Rules

Add to your `proguard-rules.pro`:

```
# YukiHookAPI
-keep class com.highcapable.yukihookapi.** { *; }
-keepclassmembers class * extends com.highcapable.yukihookapi.hook.xposed.prefs.YukiHookModulePrefs {
    <init>(...);
}
```

## Troubleshooting

### Common Issues

1. **Class not found: YukiHookAPI**
    - Ensure you have applied the YukiHook plugin
    - Check your internet connection and repository settings
    - Verify the version in your build.gradle matches the version in libs.versions.toml

2. **KSP not generating files**
    - Make sure KSP plugin is applied
    - Clean and rebuild the project
    - Invalidate caches and restart Android Studio

3. **Xposed module not loading**
    - Check Xposed logs for errors
    - Verify your module is enabled in LSPosed/EdXposed
    - Ensure your module's package name matches in build.gradle and AndroidManifest.xml

### Debugging

Enable debug logging in your application class:

```kotlin
class YourApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            YLog.Config().apply {
                isDebug = true
                isRecord = true
                elements(3)
                tag = "YukiHookAPI"
            }
        }
    }
}
```

## Best Practices

1. Always use the latest stable version of YukiHookAPI
2. Use the `@ModuleEntry` annotation for your main module class
3. Implement proper error handling for hooks
4. Use the preferences API for module settings
5. Follow the principle of least privilege when requesting permissions
6. Test thoroughly on different Android versions

## Additional Resources

- [YukiHookAPI Documentation](https://highcapable.github.io/YukiHookAPI/)
- [GitHub Repository](https://github.com/HighCapable/YukiHookAPI)
- [Sample Module](https://github.com/HighCapable/YukiHookAPI-Sample-Module)
- [Telegram Group](https://t.me/YukiHookAPI)
