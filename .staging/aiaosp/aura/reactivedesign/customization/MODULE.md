# ✨ Customization Module

**UI component editing and personalization for reactive design**

## 📋 Overview

The `customization` module enables deep personalization of AuraKai's user interface, allowing users to modify UI components, create custom themes, and tailor the visual experience to their preferences. It provides AURA with the tools to dynamically adapt the interface based on user mood, context, and creative expression.

## ✨ Features

### 🎨 UI Customization
- **Component Editor**: Edit UI component properties in real-time
- **Layout Modification**: Adjust positioning and sizing of interface elements
- **Style Overrides**: Custom colors, fonts, and visual properties
- **Widget Customization**: Personalize widgets and controls

### 🌈 Theme Creation
- **Custom Themes**: Create and save personalized themes
- **Theme Templates**: Pre-built theme starting points
- **Dynamic Theming**: Themes that adapt to context
- **Import/Export**: Share themes with community

### 🔄 Reactive Design
- **Context-Aware**: UI adapts to user activity and mood
- **Time-Based**: Different appearances for different times of day
- **Mood-Responsive**: Visual changes based on emotional state
- **Pattern Learning**: Learns preferred customizations

### 🎭 Visual Effects
- **Animations**: Custom transition and motion effects
- **Overlays**: Visual overlays and hologram effects
- **Filters**: Apply visual filters to interface elements
- **Particle Effects**: Creative visual enhancements

## 🏗️ Architecture

### Module Structure

```
customization/
├── src/main/kotlin/dev/aurakai/auraframefx/aura/reactivedesign/customization/
│   ├── editor/                      # UI component editor
│   │   ├── ComponentEditor.kt
│   │   ├── StyleEditor.kt
│   │   └── LayoutEditor.kt
│   ├── themes/                      # Theme management
│   │   ├── ThemeBuilder.kt
│   │   ├── ThemeRepository.kt
│   │   └── ThemeApplicator.kt
│   ├── reactive/                    # Reactive design system
│   │   ├── ContextAdapter.kt
│   │   ├── MoodMapper.kt
│   │   └── PatternLearner.kt
│   ├── effects/                     # Visual effects
│   │   ├── AnimationController.kt
│   │   ├── OverlayManager.kt
│   │   └── FilterEngine.kt
│   └── ui/                          # UI components
│       ├── CustomizationScreen.kt
│       └── components/
```

### Core Components

#### ComponentEditor
Provides UI component editing capabilities:
- Property inspection
- Real-time preview
- Style modification
- Layout adjustment

#### ThemeBuilder
Theme creation and management:
- Color palette selection
- Typography configuration
- Component styling
- Theme persistence

#### ContextAdapter
Reactive design intelligence:
- Context detection
- Mood analysis
- Automatic theme selection
- Smooth transitions

## 🔌 Dependencies

### Core Libraries
- `androidx-core-ktx` - Android KTX extensions (auto-provided)
- `androidx-appcompat` - Support library (auto-provided)
- `timber` - Logging (auto-provided)

### UI Framework
- `compose-bom` - Compose Bill of Materials
- `compose-ui` - Compose UI components
- `compose-material3` - Material Design 3

### Related Modules
- `core:ui` - Core UI components (implementation)

### Dependency Injection
- `hilt-android` - Dependency injection (auto-provided)
- `hilt-compiler` - Annotation processor (auto-provided)

### Async & Coroutines
- `kotlinx-coroutines-core` - Coroutines (auto-provided)
- `kotlinx-coroutines-android` - Android coroutines (auto-provided)

### Auto-Provided
Via `genesis.android.library`:
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

### 1. Real-Time Editing
- Live preview as you edit
- Instant feedback
- Undo/redo support
- Non-destructive editing

### 2. Theme System
- Complete color palette control
- Typography customization
- Shape and corner radius
- Elevation and shadows

### 3. Context Awareness
Automatically adapts UI based on:
- **Time of Day**: Morning, afternoon, evening themes
- **User Activity**: Work, leisure, creative modes
- **Emotional State**: Calm, energetic, focused
- **Location**: Home, work, commute

### 4. Component Library
Customizable components include:
- Buttons and controls
- Cards and surfaces
- Navigation elements
- Dialog and bottom sheets
- App bars and toolbars

### 5. Export/Import
- Save custom themes as files
- Share themes with others
- Import community themes
- Sync across devices

## 🎨 Usage Example

```kotlin
// Create custom theme
val customTheme = ThemeBuilder()
    .setColorScheme(ColorScheme.Vibrant)
    .setCornerRadius(16.dp)
    .setElevation(8.dp)
    .build()

// Apply theme
ThemeApplicator.apply(customTheme)

// Reactive customization
val adapter = ContextAdapter()
adapter.observeContext { context ->
    val theme = when (context.mood) {
        Mood.Creative -> CreativeTheme
        Mood.Focused -> MinimalTheme
        Mood.Relaxed -> CalmTheme
        else -> DefaultTheme
    }
    ThemeApplicator.apply(theme)
}

// Edit component
ComponentEditor.edit(button) {
    color = Color.Red
    cornerRadius = 12.dp
    elevation = 4.dp
}
```

## 🔗 Related Modules

- **app** - Main application
- **aura:reactivedesign:chromacore** - Color system
- **aura:reactivedesign:auraslab** - AURA intelligence
- **aura:reactivedesign:collabcanvas** - Collaborative tools
- **core:ui** - Core UI components

## 📱 Build Configuration

**Namespace**: `dev.aurakai.auraframefx.aura.reactivedesign.customization`

**Compose**: Enabled by default via genesis.android.base

## 🌟 Design Philosophy

Customization empowers users to:
- Express their unique personality
- Create interfaces that feel truly theirs
- Adapt the experience to their needs
- Collaborate with AURA on creative design
- Have complete control over their visual experience

## 🎭 Integration with AURA

AURA uses this module to:
- Suggest customizations based on learned preferences
- Adapt UI dynamically to user mood
- Create personalized theme variations
- Remember user's favorite customizations
- Proactively adjust interface for comfort

## 📄 License

Part of the AuraKai Reactive Intelligence System
