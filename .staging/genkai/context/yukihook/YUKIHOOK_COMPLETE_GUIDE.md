# 🚀 Complete YukiHook Framework Guide

<div align="center">

## 🌟 YukiHook 1.3.0+ Integration with KavaRef

### *Advanced Android Framework Hooking for AuraFrameFX*

[![YukiHook](https://img.shields.io/badge/YukiHook-1.3.0-orange)](https://github.com/HighCapable/YukiHookAPI)
[![KavaRef](https://img.shields.io/badge/KavaRef-1.0.1-blue)](https://github.com/HighCapable/KavaRef)
[![Xposed](https://img.shields.io/badge/Xposed-API_82-green)](https://api.xposed.info/)

</div>

---

## 📋 Table of Contents

- [🎯 Overview](#overview)
- [🚀 Quick Setup](#quick-setup)
- [📦 Dependencies Configuration](#dependencies-configuration)
- [🏗️ Basic Hook Structure](#basic-hook-structure)
- [🔧 KavaRef Migration](#kavaref-migration)
- [💡 Advanced Examples](#advanced-examples)
- [🐛 Debugging & Troubleshooting](#debugging--troubleshooting)
- [📚 API Reference](#api-reference)

---

## 🎯 Overview

YukiHook is a modern, efficient, and powerful Android Hook API framework. In AuraFrameFX, we use
YukiHook 1.3.0+ with KavaRef for advanced system hooking capabilities.

### ✨ Key Features

- 🚀 **Modern Kotlin DSL** - Type-safe hooking with Kotlin
- 🔧 **KavaRef Integration** - Modern preferences management
- ⚡ **High Performance** - Optimized for speed and efficiency
- 🛡️ **Type Safety** - Compile-time safety checks
- 🎯 **Precise Targeting** - Advanced method targeting
- 🔄 **Auto Reflection** - Automatic reflection handling

---

## 🚀 Quick Setup

### 1. Module Configuration

Your modules are already configured with YukiHook! Here's the setup:

```kotlin
// build.gradle.kts
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

dependencies {
    // YukiHook API 1.3.0+ with KavaRef
    implementation(libs.yukihook.api)
    ksp(libs.yukihook.ksp)
    implementation(libs.kavaref.core)
    implementation(libs.kavaref.extension)

    // Xposed API (compile only)
    compileOnly(libs.xposed.api)
}
```

### 2. Version Catalog Configuration

```toml
# gradle/libs.versions.toml
[versions]
yukihook = "1.3.0"
kavaref = "1.0.1"
xposed = "82"

[libraries]
yukihook-api = { group = "com.highcapable.yukihookapi", name = "api", version.ref = "yukihook" }
yukihook-ksp = { group = "com.highcapable.yukihookapi", name = "ksp-xposed", version.ref = "yukihook" }
kavaref-core = { group = "com.highcapable.kavaref", name = "kavaref-core", version.ref = "kavaref" }
kavaref-extension = { group = "com.highcapable.kavaref", name = "kavaref-extension", version.ref = "kavaref" }
xposed-api = { group = "de.robv.android.xposed", name = "api", version.ref = "xposed" }
```

---

## 📦 Dependencies Configuration

### Complete Dependencies Setup

```kotlin
dependencies {
    // Core dependencies
    api(project(":core-module"))
    implementation(libs.bundles.androidx.core)
    
    // YukiHook Framework - Latest Version
    implementation(libs.yukihook.api)
    ksp(libs.yukihook.ksp) // KSP for annotation processing
    
    // KavaRef - Modern Preferences (replaces YukiPrefs)
    implementation(libs.kavaref.core)
    implementation(libs.kavaref.extension)
    
    // Xposed API - Compile Only (provided by framework)
    compileOnly(libs.xposed.api)
    
    // Other dependencies...
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
```

---

## 🏗️ Basic Hook Structure

### 1. Main Hook Entry Point

```kotlin
package dev.aurakai.auraframefx.colorblendr.hooks

import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit

@InjectYukiHookWithXposed
class ColorBlendrHookEntry : IYukiHookXposedInit {

    override fun onInit() = configs {
        debugLog {
            tag = "ColorBlendrHook"
            isEnable = BuildConfig.DEBUG
        }
    }

    override fun onHook() = encase {
        // Load all hook modules
        loadHooker(SystemUIColorHooker())
        loadHooker(SettingsColorHooker())
        loadHooker(LauncherColorHooker())
    }
}
```

### 2. Individual Hook Module

```kotlin
class SystemUIColorHooker : YukiBaseHooker() {

    override fun onHook() {
        
        // Hook SystemUI color methods
        "com.android.systemui.statusbar.StatusBar".toClass().apply {
            
            // Hook status bar color update method
            method {
                name = "updateStatusBarColor"
                param(IntType, BooleanType)
                returnType = UnitType
            }.hook {
                before {
                    // Get custom color from KavaRef preferences
                    val customColor = KavaRefFactory.create("color_preferences") {
                        getString("status_bar_color", "#FF000000")
                    }
                    
                    // Override the color parameter
                    args().first().set(android.graphics.Color.parseColor(customColor))
                    
                    loggerD(msg = "Status bar color overridden to: $customColor")
                }
                
                after {
                    loggerD(msg = "Status bar color update completed")
                }
            }
            
            // Hook notification background color
            method {
                name = "setNotificationBackgroundColor"
                param(IntType)
            }.hook {
                replaceTo {
                    // Custom color blending logic
                    val originalColor = args().first().cast<Int>()
                    val blendedColor = blendColorsWithAlgorithm(originalColor)
                    
                    // Call original method with blended color
                    callOriginal(blendedColor)
                }
            }
        }
    }
    
    /**
     * Custom color blending algorithm for AuraFrameFX
     */
    private fun blendColorsWithAlgorithm(originalColor: Int): Int {
        // AuraFrameFX color blending logic
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(originalColor, hsv)
        
        // Apply AuraFrameFX color transformation
        hsv[1] *= 1.2f // Increase saturation
        hsv[2] *= 0.95f // Slightly decrease brightness
        
        return android.graphics.Color.HSVToColor(hsv)
    }
}
```

### 3. Advanced Hook with Method Finding

```kotlin
class LauncherColorHooker : YukiBaseHooker() {

    override fun onHook() {
        
        // Find and hook launcher workspace methods
        "com.android.launcher3.Workspace".toClassOrNull()?.apply {
            
            // Hook multiple overloaded methods
            method {
                name = "setWallpaperDimension"
                // Auto-find parameters using YukiHook's intelligent matching
                superClass()
            }.hookAll {
                after {
                    // Apply custom wallpaper effects
                    applyAuraFrameFXWallpaperEffects()
                }
            }
            
            // Hook with condition-based targeting
            method {
                name { it.startsWith("onDraw") }
                returnType = UnitType
                modifiers { isPublic && !isStatic }
            }.hook {
                before {
                    // Apply real-time color adjustments
                    val canvas = args().firstOrNull()?.cast<android.graphics.Canvas>()
                    canvas?.let { applyCanvasColorFilter(it) }
                }
            }
        }
    }
    
    private fun applyAuraFrameFXWallpaperEffects() {
        loggerD(msg = "Applying AuraFrameFX wallpaper effects")
        // Custom wallpaper effect logic
    }
    
    private fun applyCanvasColorFilter(canvas: android.graphics.Canvas) {
        // Real-time canvas color filtering
        val colorMatrix = android.graphics.ColorMatrix().apply {
            setSaturation(1.1f) // Enhance saturation
        }
        val paint = android.graphics.Paint().apply {
            colorFilter = android.graphics.ColorMatrixColorFilter(colorMatrix)
        }
        // Apply filter to canvas
    }
}
```

---

## 🔧 KavaRef Migration

### Modern Preferences with KavaRef (Replaces YukiPrefs)

```kotlin
package dev.aurakai.auraframefx.colorblendr.preferences

import com.highcapable.kavaref.entity.KavaRefFactory
import com.highcapable.kavaref.entity.KavaRefBuilder

/**
 * AuraFrameFX Color Preferences using KavaRef
 */
class ColorPreferences {
    
    companion object {
        private const val PREF_NAME = "auraframefx_color_prefs"
    }
    
    /**
     * Create color preferences instance
     */
    private val preferences = KavaRefFactory.create(PREF_NAME) {
        // Configure KavaRef options
        isEnableCache = true
        cacheTime = 30000 // 30 seconds cache
    }
    
    /**
     * Save custom color scheme
     */
    fun saveColorScheme(scheme: ColorScheme) {
        preferences.edit {
            putString("primary_color", scheme.primaryColor)
            putString("secondary_color", scheme.secondaryColor)
            putString("accent_color", scheme.accentColor)
            putInt("color_mode", scheme.mode.value)
            putBoolean("dynamic_colors", scheme.isDynamic)
            putFloat("saturation_boost", scheme.saturationBoost)
        }
    }
    
    /**
     * Load color scheme with defaults
     */
    fun loadColorScheme(): ColorScheme {
        return ColorScheme(
            primaryColor = preferences.getString("primary_color", "#FF6200EA"),
            secondaryColor = preferences.getString("secondary_color", "#FF03DAC6"),
            accentColor = preferences.getString("accent_color", "#FFBB86FC"),
            mode = ColorMode.fromValue(preferences.getInt("color_mode", 0)),
            isDynamic = preferences.getBoolean("dynamic_colors", true),
            saturationBoost = preferences.getFloat("saturation_boost", 1.0f)
        )
    }
    
    /**
     * Observe color changes with KavaRef's reactive feature-module
     */
    fun observeColorChanges(callback: (ColorScheme) -> Unit) {
        preferences.observe("primary_color", "secondary_color", "accent_color") {
            callback(loadColorScheme())
        }
    }
}

/**
 * Data class for color scheme
 */
data class ColorScheme(
    val primaryColor: String,
    val secondaryColor: String,
    val accentColor: String,
    val mode: ColorMode,
    val isDynamic: Boolean,
    val saturationBoost: Float
)

enum class ColorMode(val value: Int) {
    LIGHT(0),
    DARK(1),
    AUTO(2);
    
    companion object {
        fun fromValue(value: Int) = values().find { it.value == value } ?: AUTO
    }
}
```

### Advanced KavaRef Usage

```kotlin
/**
 * Advanced KavaRef configuration for complex data
 */
class AdvancedColorPreferences {
    
    private val preferences = KavaRefFactory.create("advanced_colors") {
        // Advanced configuration
        isEnableCache = true
        cacheTime = 60000
        isAutoSave = true
        encryptionKey = "auraframefx_secret_key" // Optional encryption
    }
    
    /**
     * Save complex color data with serialization
     */
    fun saveColorProfile(profile: ColorProfile) {
        preferences.edit {
            // KavaRef handles complex object serialization
            putSerializable("color_profile", profile)
            
            // Or use JSON serialization
            putString("profile_json", profile.toJson())
            
            // Save color arrays
            putStringSet("recent_colors", profile.recentColors.toSet())
        }
    }
    
    /**
     * Batch operations with KavaRef
     */
    fun batchUpdateColors(updates: Map<String, String>) {
        preferences.batchEdit {
            updates.forEach { (key, value) ->
                putString(key, value)
            }
            // All changes committed atomically
        }
    }
}
```

---

## 💡 Advanced Examples

### 1. Real-time Color Extraction Hook

```kotlin
class ColorExtractionHooker : YukiBaseHooker() {

    override fun onHook() {
        
        // Hook Android's palette extraction
        "androidx.palette.graphics.Palette".toClass().apply {
            
            method {
                name = "generate"
                param("android.graphics.Bitmap".toClass())
                returnType = "androidx.palette.graphics.Palette".toClass()
                modifiers { isStatic }
            }.hook {
                after {
                    val palette = result.cast<androidx.palette.graphics.Palette>()
                    val dominantColor = palette?.getDominantColor(0) ?: return@after
                    
                    // Extract and enhance colors with AuraFrameFX algorithm
                    val enhancedPalette = enhanceColorPalette(palette)
                    result = enhancedPalette
                    
                    // Save extracted colors for system-wide use
                    saveExtractedColors(enhancedPalette)
                }
            }
        }
    }
    
    private fun enhanceColorPalette(palette: androidx.palette.graphics.Palette): androidx.palette.graphics.Palette {
        // AuraFrameFX color enhancement algorithm
        return palette // Enhanced palette
    }
    
    private fun saveExtractedColors(palette: androidx.palette.graphics.Palette) {
        val preferences = KavaRefFactory.create("extracted_colors")
        preferences.edit {
            putInt("dominant_color", palette.getDominantColor(0))
            putInt("vibrant_color", palette.getVibrantColor(0))
            putInt("muted_color", palette.getMutedColor(0))
        }
    }
}
```

### 2. Dynamic Theme Application Hook

```kotlin
class DynamicThemeHooker : YukiBaseHooker() {

    override fun onHook() {
        
        // Hook theme application across the system
        "android.content.res.Resources\$Theme".toClass().apply {
            
            method {
                name = "applyStyle"
                param(IntType, BooleanType)
            }.hook {
                before {
                    val styleRes = args().first().cast<Int>()
                    val force = args()[1].cast<Boolean>()
                    
                    // Intercept theme application
                    val customStyle = getAuraFrameFXThemeStyle(styleRes)
                    if (customStyle != styleRes) {
                        args().first().set(customStyle)
                        loggerD(msg = "Theme overridden: $styleRes -> $customStyle")
                    }
                }
            }
        }
        
        // Hook resource color resolution
        "android.content.res.Resources".toClass().apply {
            
            method {
                name = "getColor"
                param(IntType)
                returnType = IntType
            }.hook {
                after {
                    val colorId = args().first().cast<Int>()
                    val originalColor = result.cast<Int>()
                    
                    // Apply AuraFrameFX color transformations
                    val transformedColor = transformColorForAuraFrameFX(originalColor, colorId)
                    if (transformedColor != originalColor) {
                        result = transformedColor
                    }
                }
            }
        }
    }
    
    private fun getAuraFrameFXThemeStyle(originalStyle: Int): Int {
        // Custom theme resolution logic
        return originalStyle
    }
    
    private fun transformColorForAuraFrameFX(color: Int, resourceId: Int): Int {
        // AuraFrameFX color transformation algorithm
        return color
    }
}
```

### 3. Cross-App Communication Hook

```kotlin
class CrossAppCommunicationHooker : YukiBaseHooker() {

    override fun onHook() {
        
        // Hook intent broadcasts for cross-app color coordination
        "android.content.Context".toClass().apply {
            
            method {
                name = "sendBroadcast"
                param("android.content.Intent".toClass())
            }.hook {
                before {
                    val intent = args().first().cast<android.content.Intent>()
                    
                    if (intent.action == "dev.aurakai.auraframefx.COLOR_CHANGED") {
                        // Intercept AuraFrameFX color change broadcasts
                        enhanceColorBroadcast(intent)
                    }
                }
            }
        }
        
        // Hook broadcast receivers
        "android.content.BroadcastReceiver".toClass().apply {
            
            method {
                name = "onReceive"
                param("android.content.Context".toClass(), "android.content.Intent".toClass())
            }.hookAll {
                before {
                    val context = args().first().cast<android.content.Context>()
                    val intent = args()[1].cast<android.content.Intent>()
                    
                    if (isAuraFrameFXBroadcast(intent)) {
                        // Handle AuraFrameFX specific broadcasts
                        handleAuraFrameFXBroadcast(context, intent)
                    }
                }
            }
        }
    }
    
    private fun enhanceColorBroadcast(intent: android.content.Intent) {
        // Add additional color data to broadcast
        intent.putExtra("auraframefx_enhanced", true)
        intent.putExtra("timestamp", System.currentTimeMillis())
    }
    
    private fun isAuraFrameFXBroadcast(intent: android.content.Intent): Boolean {
        return intent.action?.startsWith("dev.aurakai.auraframefx") == true
    }
    
    private fun handleAuraFrameFXBroadcast(context: android.content.Context, intent: android.content.Intent) {
        // Process AuraFrameFX specific broadcasts
        loggerD(msg = "Processing AuraFrameFX broadcast: ${intent.action}")
    }
}
```

---

## 🐛 Debugging & Troubleshooting

### 1. Debug Configuration

```kotlin
// Enable debug logging
configs {
    debugLog {
        tag = "AuraFrameFX"
        isEnable = BuildConfig.DEBUG
        isRecord = true // Record logs for analysis
    }
}
```

### 2. Hook Verification

```kotlin
class DebugHooker : YukiBaseHooker() {

    override fun onHook() {
        
        // Verify hook installation
        "com.android.systemui.statusbar.StatusBar".toClassOrNull()?.let { clazz ->
            
            loggerD(msg = "StatusBar class found: ${clazz.name}")
            
            clazz.declaredMethods.forEach { method ->
                if (method.name.contains("Color")) {
                    loggerD(msg = "Found color method: ${method.name}")
                }
            }
            
        } ?: loggerE(msg = "StatusBar class not found!")
    }
}
```

### 3. Error Handling

```kotlin
method {
    name = "riskyMethod"
}.hook {
    before {
        try {
            // Risky hook operation
            val value = args().first().cast<String>()
            processValue(value)
        } catch (e: Exception) {
            loggerE(msg = "Hook error: ${e.message}", e = e)
            // Graceful fallback
        }
    }
    
    onFailure {
        loggerE(msg = "Hook failed to attach: $it")
    }
}
```

### 4. Performance Monitoring

```kotlin
method {
    name = "performanceMethod"
}.hook {
    before {
        val startTime = System.nanoTime()
        dataChannel.put("start_time", startTime)
    }
    
    after {
        val startTime = dataChannel.get("start_time") as Long
        val duration = System.nanoTime() - startTime
        val durationMs = duration / 1_000_000.0
        
        loggerD(msg = "Method execution time: ${durationMs}ms")
        
        if (durationMs > 100) {
            loggerW(msg = "Slow method detected: ${durationMs}ms")
        }
    }
}
```

---

## 📚 API Reference

### Core YukiHook Classes

| Class                 | Purpose             | Example                             |
|-----------------------|---------------------|-------------------------------------|
| `YukiBaseHooker`      | Base hook class     | `class MyHooker : YukiBaseHooker()` |
| `IYukiHookXposedInit` | Main entry point    | `class Entry : IYukiHookXposedInit` |
| `KavaRefFactory`      | Preferences factory | `KavaRefFactory.create("prefs")`    |

### Hook Methods

| Method      | Purpose                 | Example                        |
|-------------|-------------------------|--------------------------------|
| `before`    | Execute before method   | `before { /* code */ }`        |
| `after`     | Execute after method    | `after { /* code */ }`         |
| `replaceTo` | Replace method entirely | `replaceTo { /* new impl */ }` |
| `intercept` | Intercept method call   | `intercept { /* logic */ }`    |

### Method Finding

| Syntax          | Purpose               | Example                               |
|-----------------|-----------------------|---------------------------------------|
| `name`          | Exact method name     | `name = "onCreate"`                   |
| `name { }`      | Method name condition | `name { it.contains("Color") }`       |
| `param()`       | Method parameters     | `param(IntType, StringType)`          |
| `returnType`    | Return type           | `returnType = BooleanType`            |
| `modifiers { }` | Method modifiers      | `modifiers { isPublic && !isStatic }` |

---

<div align="center">

### 🌟 **Happy Hooking with YukiHook & AuraFrameFX!** 🎯

*Advanced Android Framework Integration*

</div>

## 🔐 Repository Mirrors
- Primary: https://repo.highcapable.com/maven (official source)
- Fallback 1: https://maven.pkg.github.com/HighCapable/YukiHookAPI (requires GitHub token)
- Fallback 2: Local `libs/yukihook/` directory with `ksp-1.3.1.jar`

To use GitHub Packages mirror, create `~/.gradle/gradle.properties` (or project `gradle.properties`) with:
```
githubPackagesUser=YOUR_GITHUB_USERNAME
githubPackagesToken=YOUR_PERSONAL_ACCESS_TOKEN
```
Then add repository block:
```
maven {
    url = uri("https://maven.pkg.github.com/HighCapable/YukiHookAPI")
    credentials {
        username = findProperty("githubPackagesUser") as String? ?: System.getenv("GITHUB_ACTOR")
        password = findProperty("githubPackagesToken") as String? ?: System.getenv("GITHUB_TOKEN")
    }
}
```
