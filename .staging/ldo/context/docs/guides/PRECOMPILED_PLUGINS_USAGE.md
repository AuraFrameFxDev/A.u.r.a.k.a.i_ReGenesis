# Precompiled Script Plugin Usage Guide

## Overview

We use **precompiled script plugins** (`.gradle.kts` files) for our build conventions, not binary plugins (Kotlin classes). This is the modern, declarative, and recommended approach.

## Available Convention Plugins

### 1. `genesis.android.application`

For the main app module only. Includes:
- Android Application plugin
- Kotlin Android
- Hilt + KSP
- Compose
- Serialization
- Google Services

**Example: `app/build.gradle.kts`**
```kotlin
plugins {
    id("genesis.android.application")
}

android {
    namespace = "dev.aurakai.auraframefx"
    ndkVersion = libs.versions.ndk.get()

    defaultConfig {
        // App-specific configuration only
        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++20"
                arguments += listOf(
                    "-DANDROID_STL=c++_shared",
                    "-DANDROID_PLATFORM=android-${libs.versions.min.sdk.get()}"
                )
            }
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = libs.versions.cmake.get()
        }
    }
}

dependencies {
    // Feature modules
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":feature:embodiment"))

    // Xposed & YukiHook
    compileOnly(libs.xposed.api)
    implementation(libs.yukihookapi.api)
    ksp(libs.yukihookapi.ksp.xposed)
}
```

---

### 2. `genesis.android.library`

For standard Android library modules. Includes:
- Android Library plugin
- Kotlin Android
- KSP

**Example: `core/common/build.gradle.kts`**
```kotlin
plugins {
    id("genesis.android.library")
}

android {
    namespace = "dev.aurakai.auraframefx.core.common"
}

dependencies {
    // Module-specific dependencies only
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.datetime)
}
```

---

### 3. `genesis.android.hilt` (Composable)

Add dependency injection to any module. Apply AFTER base plugin.

**Example: `feature-module/build.gradle.kts`**
```kotlin
plugins {
    id("genesis.android.library")
    id("genesis.android.hilt")  // Adds Hilt DI
}

android {
    namespace = "dev.aurakai.auraframefx.feature"
}

dependencies {
    implementation(project(":core:common"))
    // Hilt dependencies are automatically included!
}
```

---

### 4. `genesis.android.compose` (Composable)

Add Jetpack Compose to any module. Apply AFTER base plugin.

**Example: `core/ui/build.gradle.kts`**
```kotlin
plugins {
    id("genesis.android.library")
    id("genesis.android.hilt")
    id("genesis.android.compose")  // Adds Compose UI
}

android {
    namespace = "dev.aurakai.auraframefx.core.ui"
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.coil.compose)  // Module-specific
    // Compose dependencies are automatically included!
}
```

---

### 5. `genesis.android.room` (Composable)

Add Room Database to any module. Apply AFTER base plugin.

**Example: `core/data/build.gradle.kts`**
```kotlin
plugins {
    id("genesis.android.library")
    id("genesis.android.hilt")
    id("genesis.android.room")  // Adds Room DB
}

android {
    namespace = "dev.aurakai.auraframefx.core.data"
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.retrofit)
    implementation(libs.kotlinx.serialization.json)
    // Room dependencies are automatically included!
}
```

---

### 6. `genesis.kotlin.jvm`

For pure Kotlin modules with NO Android dependencies (domain/business logic).

**Example: `core/domain/build.gradle.kts`**
```kotlin
plugins {
    id("genesis.kotlin.jvm")  // Pure Kotlin, no Android!
}

// NO android block needed!

dependencies {
    // Pure Kotlin dependencies only
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.datetime)
    // Coroutines are automatically included!
}
```

---

## Composition Examples

### Example 1: Feature Module with Compose + Hilt

**`feature/embodiment/build.gradle.kts`**
```kotlin
plugins {
    id("genesis.android.library")
    id("genesis.android.compose")  // UI with Composables
    id("genesis.android.hilt")     // Dependency injection
}

android {
    namespace = "dev.aurakai.auraframefx.feature.embodiment"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:data"))

    // Module-specific
    implementation(libs.lottie.compose)
}
```

### Example 2: Data Module with Room + Hilt

**`core/database/build.gradle.kts`**
```kotlin
plugins {
    id("genesis.android.library")
    id("genesis.android.room")   // Local persistence
    id("genesis.android.hilt")   // DI
    alias(libs.plugins.kotlin.serialization)  // For JSON
}

android {
    namespace = "dev.aurakai.auraframefx.core.database"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:domain"))

    // Module-specific
    implementation(libs.kotlinx.serialization.json)
}
```

### Example 3: Pure Domain Logic (No Android)

**`core/business-logic/build.gradle.kts`**
```kotlin
plugins {
    id("genesis.kotlin.jvm")  // Pure Kotlin JVM
}

dependencies {
    implementation(project(":core:domain"))  // Another pure Kotlin module

    // Pure Kotlin only
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.datetime)
}
```

---

## Key Benefits

### ✅ **Composable Architecture**
Plugins are small, focused, and can be combined:
- `genesis.android.library` + `genesis.android.compose` = UI library
- `genesis.android.library` + `genesis.android.room` + `genesis.android.hilt` = Data layer
- `genesis.kotlin.jvm` alone = Pure domain logic

### ✅ **DRY Principle**
Common configuration is in ONE place. No copy-paste across 30+ modules.

### ✅ **Clear Intent**
Looking at a module's plugins instantly tells you what it does:
```kotlin
plugins {
    id("genesis.android.library")  // Android library
    id("genesis.android.compose")   // Has UI
    id("genesis.android.hilt")      // Uses DI
}
// = "This is an Android UI library with dependency injection"
```

### ✅ **Type-Safe**
Version catalog integration means auto-completion and compile-time safety.

### ✅ **Maintainable**
Need to update Compose version? Change it in ONE file (`libs.versions.toml`).
Need to add a Compose dependency? Update ONE plugin (`genesis.android.compose.gradle.kts`).

---

## Plugin Application Order

**IMPORTANT:** Order matters when composing plugins!

### ✅ Correct Order:
```kotlin
plugins {
    id("genesis.android.library")  // 1. Base FIRST
    id("genesis.android.hilt")     // 2. Then additional features
    id("genesis.android.compose")  // 3. Order doesn't matter between these
}
```

### ❌ Incorrect Order:
```kotlin
plugins {
    id("genesis.android.compose")  // ❌ Base plugin not applied yet!
    id("genesis.android.library")  // ❌ Should be first
}
```

---

## Common Patterns

### Pattern 1: UI Feature Module
```kotlin
plugins {
    id("genesis.android.library")
    id("genesis.android.compose")
    id("genesis.android.hilt")
}
```

### Pattern 2: Data Module
```kotlin
plugins {
    id("genesis.android.library")
    id("genesis.android.room")
    id("genesis.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}
```

### Pattern 3: Domain Module (Pure Kotlin)
```kotlin
plugins {
    id("genesis.kotlin.jvm")
}
```

### Pattern 4: Network Module
```kotlin
plugins {
    id("genesis.android.library")
    id("genesis.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}
```

---

## Migration Checklist

To migrate an existing module to use convention plugins:

1. ✅ Remove verbose plugin declarations
2. ✅ Remove android { ... } configuration blocks that duplicate conventions
3. ✅ Keep only namespace and module-specific config
4. ✅ Remove common dependencies (they're in the convention plugins now)
5. ✅ Keep only module-specific dependencies
6. ✅ Test the module builds correctly

**Before:**
```kotlin
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "dev.aurakai.auraframefx.core.ui"
    compileSdk = 36
    defaultConfig {
        minSdk = 34
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation(platform("androidx.compose:compose-bom:2025.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("com.google.dagger:hilt-android:2.57.2")
    ksp("com.google.dagger:hilt-compiler:2.57.2")
    implementation("io.coil-kt:coil-compose:2.7.0")
}
```

**After:**
```kotlin
plugins {
    id("genesis.android.library")
    id("genesis.android.compose")
    id("genesis.android.hilt")
}

android {
    namespace = "dev.aurakai.auraframefx.core.ui"
}

dependencies {
    // Only module-specific dependencies
    implementation(libs.coil.compose)
}
```

**Saved:** 45 lines → 12 lines (73% reduction!)

---

## Troubleshooting

### Error: "Plugin with id 'genesis.android.library' not found"
**Solution:** Ensure `build-logic` is included in root `settings.gradle.kts`:
```kotlin
pluginManagement {
    includeBuild("build-logic")
}
```

### Error: "Unresolved reference: libs"
**Solution:** Ensure version catalog is configured in `build-logic/settings.gradle.kts`:
```kotlin
versionCatalogs {
    create("libs") {
        from(files("../gradle/libs.versions.toml"))
    }
}
```

### Error: Hilt/KSP not working
**Solution:** Ensure plugins are declared with `apply false` in root `build.gradle.kts`:
```kotlin
plugins {
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
}
```

---

## Summary

Use **precompiled script plugins** for:
- ✅ Clarity and simplicity
- ✅ Composition over inheritance
- ✅ Type-safe configuration
- ✅ DRY principle
- ✅ Scalability to 30+ modules

Each module's `build.gradle.kts` should be **10-20 lines**, not 100+!
