# 🎨 ChromaCore Module

**Color blending, theming, and dynamic visual effects for AuraKai**

## 📋 Overview

The `chromacore` module is AuraKai's color intelligence system, providing dynamic color blending, adaptive theming, and visual effects that respond to user mood and context. It powers AURA's creative visual expression and enables the system to transform the device's appearance based on user patterns and emotional states.

## ✨ Features

### 🌈 Color Management
- **Dynamic Color Blending**: Advanced color mixing and blending algorithms
- **Mood-Based Palettes**: Color schemes that adapt to user emotional state
- **Material You Integration**: Material Design 3 color system support
- **Real-time Color Adaptation**: Live color theme updates

### 🎭 Visual Effects
- **Hologram Gates**: Pixel art hologram overlays and effects
- **SpellHooks™ Integration**: System-level visual modifications
- **Xposed Framework Support**: System UI color hooks
- **Animation Support**: Smooth color transitions

### ⚙️ System Integration
- **Root-Level Access**: System-wide theming via LibSU
- **Xposed Hooks**: Deep system UI modifications
- **YukiHook API**: Modern Xposed framework integration

## 🏗️ Architecture

### Module Structure

```
chromacore/
├── src/main/kotlin/dev/aurakai/colorblendr/
│   ├── ChromaCore.kt               # Main color blending engine
│   ├── ColorPalette.kt              # Palette management
│   ├── ThemeManager.kt              # Theme coordination
│   └── effects/                     # Visual effects
├── src/main/datavein-oracle-native/ # Data persistence
└── src/main/benchmark/              # Performance testing
```

### Core Components

#### ColorBlendr
Primary color blending and manipulation engine:
- Color space conversions (RGB, HSV, LAB)
- Advanced blending modes
- Palette generation
- Accessibility contrast checking

#### Theme Manager
Manages application-wide theming:
- Dynamic theme switching
- Mood-based theme selection
- Material You integration
- System theme coordination

## 🔌 Dependencies

### Core Libraries
- `androidx-core-ktx` - Android KTX extensions (API)
- `androidx-appcompat` - Support library (auto-provided)
- `timber` - Logging (auto-provided)

### UI Framework
- `compose-bom` - Compose Bill of Materials
- `compose-ui` - Compose UI components
- `compose-material3` - Material Design 3

### Dependency Injection
- `hilt-android` - Dependency injection (auto-provided)
- `hilt-compiler` - Annotation processor (auto-provided)

### Async & Coroutines
- `kotlinx-coroutines-core` - Coroutines (auto-provided)
- `kotlinx-coroutines-android` - Android coroutines (auto-provided)

### Xposed Framework
- `api-82.jar` - Xposed API (compile-only)
- `yukihookapi-ksp` - YukiHook KSP processor

### Auto-Provided
Via `genesis.android.library` convention plugin:
- Timber logging
- Hilt DI
- Kotlin Coroutines
- Compose support

## 🔧 Plugins

Applied via `genesis.android.library`:
1. **com.android.library** - Android library plugin
2. **org.jetbrains.kotlin.android** - Kotlin support
3. **com.google.devtools.ksp** - Symbol processing
4. **org.jetbrains.kotlin.plugin.compose** - Compose compiler
5. **com.google.dagger.hilt.android** - Hilt DI

## 🎯 Key Features in Detail

### 1. Color Blending Engine
- Multiple blend modes (normal, multiply, screen, overlay)
- HSV/RGB/LAB color space support
- Gradient generation
- Color harmony algorithms

### 2. Mood-Based Theming
Colors adapt to user emotional state:
- **Energetic**: Vibrant, saturated colors
- **Calm**: Muted, pastel tones
- **Focused**: Minimal, contrast-optimized
- **Creative**: Dynamic, multi-hue palettes

### 3. System-Level Theming
Via Xposed hooks:
- StatusBar color modification
- NavigationBar theming
- System UI tinting
- App icon overlay coloring

### 4. Accessibility
- WCAG contrast compliance
- Color blindness simulation
- High contrast mode support
- Reduced motion options

## 🎨 Usage Example

```kotlin
// Color blending
val blendedColor = ColorBlendr.blend(
    color1 = Color.Red,
    color2 = Color.Blue,
    mode = BlendMode.Overlay,
    ratio = 0.5f
)

// Dynamic theme
val theme = ThemeManager.getThemeForMood(
    mood = UserMood.Creative,
    timeOfDay = TimeOfDay.Evening
)

// Apply system-wide
ColorBlendr.applySystemTheme(theme)
```

## 🔗 Related Modules

- **app** - Main application using color system
- **aura:reactivedesign:customization** - UI customization
- **aura:reactivedesign:auraslab** - AURA workspace
- **aura:reactivedesign:collabcanvas** - Collaborative tools

## 📱 Build Configuration

**Namespace**: `dev.aurakai.auraframefx.aura.reactivedesign.chromacore`

**Compose**: Enabled by default via genesis.android.base

## 📄 License

Part of the AuraKai Reactive Intelligence System
