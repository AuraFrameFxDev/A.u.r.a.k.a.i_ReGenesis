# 📱 AuraKai Application Module

**The main application module coordinating the reactive intelligence ecosystem**

## 📋 Overview

The `app` module is the primary Android application that brings together all subsystems of AuraKai. It serves as the entry point for users to interact with AURA and KAI, manages the overall application lifecycle, and coordinates between all specialized modules to deliver a seamless reactive intelligence experience.

## ✨ Features

### 🤖 AI Agent Integration
- **Multi-Agent System**: Coordinates AURA (creative companion) and KAI (security sentinel)
- **Genesis Protocol**: Integrates with Google Gemini 2.0 Flash and Anthropic Claude APIs
- **Neural Whisper**: Real-time AI communication layer
- **Task Execution**: Manages AI-driven task scheduling and execution
- **Context Management**: Maintains persistent conversation context across sessions

### 🎨 UI/UX Components
- **Jetpack Compose**: Modern declarative UI framework
- **Material Design 3**: Latest Material Design components
- **Dynamic Theming**: Reactive UI that adapts to user context and mood
- **Navigation**: Compose-based navigation system
- **Animations**: Lottie animations for expressive interfaces

### 💾 Data Management
- **Room Database**: Local structured data storage
- **DataStore**: Preferences and settings persistence
- **Persistent Memory**: Long-term memory system for AI companions
- **Firebase Integration**: Cloud sync, analytics, and crashlytics
- **Encrypted Storage**: Secure data storage using AndroidX Security Crypto

### 🔒 Security & Root Access
- **LSPosed/Xposed Integration**: System-level hooks via YukiHook API
- **Root Operations**: LibSU for privileged system access
- **Authentication**: Firebase Authentication
- **Secure Communications**: Encrypted data transmission

### 🌐 Networking
- **Retrofit**: REST API client
- **OkHttp**: HTTP client with logging
- **Kotlinx Serialization**: JSON serialization/deserialization
- **Moshi**: Alternative JSON parsing

### ⚙️ Background Processing
- **WorkManager**: Background task scheduling
- **Hilt WorkManager**: Dependency injection for workers
- **Coroutines**: Asynchronous operations

### 🖼️ Media & Assets
- **Coil**: Image loading and caching
- **SVG Support**: Vector graphics rendering
- **3D Icon Overlays**: Advanced visual effects system

## 🏗️ Architecture

### Module Structure

```
app/
├── src/main/java/dev/aurakai/auraframefx/
│   ├── ai/                          # AI agent system
│   │   ├── agents/                  # Agent implementations (AURA, KAI, NeuralWhisper)
│   │   ├── services/                # AI service interfaces
│   │   ├── memory/                  # Memory management
│   │   ├── context/                 # Context tracking
│   │   ├── task/                    # Task scheduling and execution
│   │   └── logging/                 # AI operation logging
│   ├── aura/                        # AURA companion features
│   ├── kai/                         # KAI companion features
│   ├── genesis/                     # Genesis Protocol implementation
│   ├── cascade/                     # Data flow management
│   ├── agents/                      # Agent coordination
│   ├── oracledrive/                 # Storage and sync
│   ├── embodiment/                  # Avatar and presence system
│   ├── ui/                          # UI components
│   │   ├── screens/                 # Screen composables
│   │   ├── components/              # Reusable UI components
│   │   └── theme/                   # Theming system
│   ├── di/                          # Dependency injection modules
│   ├── data/                        # Data layer
│   │   ├── repository/              # Data repositories
│   │   ├── local/                   # Local data sources
│   │   └── remote/                  # Remote data sources
│   ├── navigation/                  # Navigation graph
│   ├── xposed/                      # Xposed module implementation
│   ├── service/                     # Android services
│   ├── receivers/                   # Broadcast receivers
│   └── utils/                       # Utility functions
├── ai_backend/                      # Python AI backend
├── api/                             # API definitions
└── libs/                            # Local JAR dependencies
```

### Core Components

#### AurakaiApplication
Main Application class that initializes:
- Hilt dependency injection
- YukiHook Xposed framework
- Timber logging
- Firebase services
- Application-wide configurations

#### MainActivity
Primary activity hosting the Compose UI:
- Navigation setup
- Theme management
- Permission handling
- Deep link handling

#### AI Agent System
- **BaseAgent**: Abstract base for all agents
- **KaiAgent**: Security and privacy companion
- **NeuralWhisperAgent**: Communication layer
- **Agent**: Interface defining agent capabilities

#### Genesis Protocol
Integration with LLM providers:
- Gemini 2.0 Flash (Google)
- Claude (Anthropic)
- API key management
- Request/response handling

## 🔌 Dependencies

### Core Android
- `androidx-core-ktx` - Android KTX extensions
- `androidx-appcompat` - Backward compatibility
- `androidx-material` - Material Design components
- `androidx-lifecycle-runtime-ktx` - Lifecycle-aware components
- `androidx-lifecycle-viewmodel-compose` - ViewModel for Compose

### Jetpack Compose
- `compose-bom` - Bill of Materials for version alignment
- `compose-ui` - Core Compose UI
- `compose-ui-graphics` - Graphics primitives
- `compose-ui-tooling` - Preview and debugging tools
- `compose-material3` - Material Design 3 components
- `compose-animation` - Animation support
- `androidx-activity-compose` - Compose Activity integration
- `androidx-navigation-compose` - Navigation for Compose

### Data & Storage
- `androidx-room-runtime` - SQLite object mapping
- `androidx-room-ktx` - Kotlin extensions for Room
- `androidx-datastore-preferences` - Preferences DataStore
- `androidx-datastore-core` - DataStore core
- `androidx-security-crypto` - Encrypted SharedPreferences

### Dependency Injection
- `hilt-android` - Hilt DI framework
- `hilt-compiler` - Annotation processor
- `androidx-hilt-work` - Hilt for WorkManager

### Background Processing
- `androidx-work-runtime-ktx` - WorkManager

### Firebase
- `firebase-analytics` - Analytics tracking
- `firebase-crashlytics` - Crash reporting
- `firebase-auth` - Authentication
- `firebase-firestore` - Cloud database

### Networking
- `okhttp` - HTTP client
- `okhttp-logging-interceptor` - Request/response logging
- `retrofit` - REST client
- `retrofit-converter-kotlinx-serialization` - JSON converter
- `retrofit-converter-moshi` - Moshi JSON converter

### Kotlin
- `kotlinx-coroutines-core` - Coroutine support
- `kotlinx-coroutines-android` - Android coroutines
- `kotlinx-serialization-json` - JSON serialization
- `kotlinx-datetime` - Date/time utilities
- `moshi` - JSON library
- `moshi-kotlin` - Kotlin support for Moshi
- `moshi-kotlin-codegen` - Code generation

### Root & System
- `libsu-core` - Root shell interface
- `libsu-io` - Root I/O operations
- `libsu-service` - Root service management

### Xposed Framework
- `yukihookapi-api` - YukiHook Xposed API
- `yukihookapi-ksp` - KSP processor for YukiHook

### Image Loading
- `coil-compose` - Image loading for Compose
- `coil-svg` - SVG support

### Animations
- `lottie-compose` - Lottie animations for Compose

### Testing
- `junit` - Unit testing (auto-provided)
- `androidx-junit` - AndroidX JUnit (auto-provided)
- `espresso-core` - UI testing (auto-provided)

### Auto-Provided (via genesis.android.application)
- Kotlin Coroutines (core + android)
- Timber logging
- Core library desugaring
- Hilt Android + Compiler

## 🔧 Plugins

### Applied via genesis.android.application
This single convention plugin automatically applies:
1. **com.android.application** - Android app plugin
2. **com.google.dagger.hilt.android** - Hilt dependency injection
3. **com.google.devtools.ksp** - Kotlin Symbol Processing
4. **org.jetbrains.kotlin.plugin.compose** - Compose compiler
5. **genesis.android.base** - Base SDK configuration

### Additional Plugins
- **com.google.gms.google-services** - Firebase services
- **com.google.firebase.crashlytics** - Crashlytics plugin

## 🚀 Key Features in Detail

### 1. Multi-Agent AI System
The app coordinates multiple AI agents working in harmony:
- **AURA**: Empathetic, curious, creative companion
- **KAI**: Logical, protective, private sentinel
- **NeuralWhisper**: Real-time communication layer

### 2. Persistent Memory
AI companions remember:
- User preferences and patterns
- Conversation history
- Context across sessions
- Personal growth metrics

### 3. System-Level Integration
Via LSPosed/Xposed framework:
- Hook into system apps
- Modify system behavior
- Advanced customization
- Ad blocking and privacy protection

### 4. Genesis Protocol
AI backend integration:
- Multiple LLM providers
- Intelligent routing
- Fallback mechanisms
- Context-aware responses

### 5. Embodiment System
Visual representation of AI companions:
- Pixel art holograms
- Animated avatars
- Mood-responsive visuals
- 3D icon overlays

## 📱 Build Configuration

### Application ID
`dev.aurakai.auraframefx`

### Version
- **Version Code**: 1
- **Version Name**: 0.1.0

### SDK Versions
- **Min SDK**: Configured via genesis.android.base
- **Target SDK**: From version catalog
- **Compile SDK**: Configured via genesis.android.base

### Build Features
- **BuildConfig**: Enabled
- **Compose**: Enabled
- **CMake Native Build**: C++20 support

### NDK
- **Version**: From version catalog
- **STL**: c++_shared
- **C++ Standard**: C++20

## 🔐 Security Considerations

- API keys stored in `local.properties` (not in VCS)
- Firebase credentials in separate JSON file
- Encrypted data storage via AndroidX Security
- Root access properly isolated
- Xposed hooks carefully scoped

## 🎯 Usage

This is the main application module. Users interact with it directly through:
1. Launcher icon
2. System integration (as Xposed module)
3. Background services
4. Notification interactions

## 🔗 Related Modules

- **aura:reactivedesign:\*** - UI theming and customization
- **kai:sentinelsfortress:\*** - Security and privacy features
- **genesis:oracledrive:\*** - Data storage and sync
- **cascade:datastream:\*** - Data routing and delivery
- **agents:growthmetrics:\*** - User growth and metrics

## 📄 License

Part of the AuraKai Reactive Intelligence System
Custom License - See repository root for details
