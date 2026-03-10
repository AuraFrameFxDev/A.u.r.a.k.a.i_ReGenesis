# 🎨 CollabCanvas Module

**Real-time collaborative drawing and whiteboard for creative co-creation**

## 📋 Overview

The `collabcanvas` module provides a real-time collaborative drawing and whiteboard system for AuraKai. It enables multiple users (or AI agents) to work together on creative projects, sketch ideas, and visualize concepts in real-time, fostering creative expression and collaboration within the AURA ecosystem.

## ✨ Features

### ✏️ Drawing Tools
- **Freehand Drawing**: Smooth brush strokes with pressure sensitivity
- **Shape Tools**: Rectangles, circles, lines, and custom shapes
- **Text Annotations**: Add text labels and comments
- **Eraser**: Selective content removal
- **Color Picker**: Full RGB/HSV color selection
- **Brush Customization**: Size, opacity, and style controls

### 🔄 Real-Time Collaboration
- **Multi-User Support**: Multiple participants drawing simultaneously
- **Live Synchronization**: Real-time stroke and action sync
- **Cursor Tracking**: See other users' cursor positions
- **User Presence**: Active participant indicators
- **Conflict Resolution**: Intelligent merge of simultaneous edits

### 🎨 Canvas Features
- **Infinite Canvas**: Unlimited drawing space with pan and zoom
- **Layers**: Multiple drawing layers with blend modes
- **Undo/Redo**: Full action history
- **Export**: Save as PNG, SVG, or project file
- **Import**: Load images and previous sessions

### 🌐 Networking
- **WebSocket Communication**: Low-latency real-time sync
- **OkHttp Integration**: Robust HTTP client
- **JSON Serialization**: Efficient data format
- **Offline Mode**: Continue drawing offline, sync when connected

### 🔒 System Integration
- **Root Operations**: System-level canvas overlays via LibSU
- **Xposed Hooks**: Screenshot and screen recording protection
- **YukiHook API**: Modern Xposed framework integration

## 🏗️ Architecture

### Module Structure

```
collabcanvas/
├── src/main/kotlin/dev/aurakai/auraframefx/aura/reactivedesign/collabcanvas/
│   ├── canvas/                      # Canvas rendering
│   │   ├── CanvasView.kt           # Main canvas composable
│   │   ├── DrawingEngine.kt        # Drawing logic
│   │   └── LayerManager.kt         # Layer management
│   ├── tools/                       # Drawing tools
│   │   ├── BrushTool.kt
│   │   ├── ShapeTool.kt
│   │   └── TextTool.kt
│   ├── sync/                        # Real-time sync
│   │   ├── SyncManager.kt
│   │   ├── WebSocketClient.kt
│   │   └── CollaborationSession.kt
│   ├── model/                       # Data models
│   │   ├── Stroke.kt
│   │   ├── Layer.kt
│   │   └── CanvasState.kt
│   └── export/                      # Export functionality
│       ├── PngExporter.kt
│       └── SvgExporter.kt
```

### Core Components

#### CanvasView
Main Compose-based canvas component:
- Touch input handling
- Gesture recognition (pan, zoom, rotate)
- Rendering pipeline
- Performance optimization

#### SyncManager
Real-time collaboration coordinator:
- WebSocket connection management
- Action serialization/deserialization
- Conflict resolution
- Presence tracking

#### DrawingEngine
Core drawing functionality:
- Stroke rendering
- Path smoothing
- Pressure curve handling
- Performance optimization

## 🔌 Dependencies

### Core Libraries
- `androidx-core-ktx` - Android KTX extensions (API)
- `androidx-appcompat` - Support library (auto-provided)
- `timber` - Logging (auto-provided)

### UI Framework
- `compose-bom` - Compose Bill of Materials
- `compose-ui` - Compose UI components
- `compose-material3` - Material Design 3
- `compose-material-icons-extended` - Extended icon set
- `compose-ui-tooling` - Preview and debugging tools

### Networking
- `okhttp` - HTTP client
- `okhttp-logging-interceptor` - Request/response logging
- `gson` - JSON serialization/deserialization

### System Operations
- `libsu-core` - Root shell interface
- `libsu-io` - Root I/O operations
- `libsu-service` - Root service management

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
Via `genesis.android.library`:
- Timber logging
- Hilt DI
- Kotlin Coroutines
- Compose support
- Java 24 bytecode target

## 🔧 Plugins

Applied via `genesis.android.library`:
1. **com.android.library** - Android library plugin
2. **org.jetbrains.kotlin.android** - Kotlin support
3. **com.google.devtools.ksp** - Symbol processing
4. **org.jetbrains.kotlin.plugin.compose** - Compose compiler
5. **com.google.dagger.hilt.android** - Hilt DI

## 🎯 Key Features in Detail

### 1. Real-Time Synchronization
- WebSocket-based communication for minimal latency
- Differential updates (only send changes)
- Automatic reconnection
- Message queuing for offline support

### 2. Drawing Engine
- Anti-aliased rendering
- Pressure-sensitive strokes
- Hardware-accelerated drawing
- Optimized for touch and stylus input

### 3. Collaboration Features
- See other users' cursors in real-time
- User color-coding for identification
- Chat integration for communication
- Session history and replay

### 4. Export Options
- **PNG**: Raster export with configurable resolution
- **SVG**: Vector export for scalability
- **Project File**: Save and restore full session state
- **Share**: Direct sharing to other apps

## 🌐 Usage Example

```kotlin
// Initialize canvas
val canvasState = rememberCanvasState()

CollabCanvas(
    state = canvasState,
    sessionId = "creative-session-123",
    onStrokeAdded = { stroke ->
        // Sync to other participants
        syncManager.broadcastStroke(stroke)
    }
)

// Drawing tools
val brush = BrushTool(
    color = Color.Red,
    size = 10.dp,
    opacity = 0.8f
)

// Export canvas
val png = canvasState.exportToPng(resolution = 1080)
```

## 🔗 Related Modules

- **app** - Main application integration
- **aura:reactivedesign:chromacore** - Color system
- **aura:reactivedesign:customization** - UI customization
- **cascade:datastream:routing** - Data routing
- **cascade:datastream:delivery** - Real-time delivery

## 📱 Build Configuration

**Namespace**: `dev.aurakai.auraframefx.aura.reactivedesign.collabcanvas`

**Compose**: Enabled by default
**Java Target**: Java 24 bytecode

## 🎨 Design Philosophy

CollabCanvas embraces the creative spirit of AURA:
- Intuitive, natural drawing experience
- Real-time collaboration without friction
- Express ideas visually with AI companions
- Foster creative co-creation

## 📄 License

Part of the AuraKai Reactive Intelligence System
