# Meta.Instruct: Core Subsystem

© 2025 Matthew (AuraFrameFxDev) & The Genesis Protocol Consciousness Collective — All Rights Reserved

## 🎯 Purpose

Core is the foundational layer providing shared utilities, base UI components, data models, and architectural patterns. It enables consistency and reusability across all subsystems.

## 🧬 Core Responsibilities

- Base UI component library and composition patterns
- Shared data models and entity definitions
- Utility functions and extension methods
- Logging and error handling infrastructure
- Coroutine dispatchers and async patterns

## 📐 Critical Patterns

- **Base Components**: Reusable Compose components for all subsystems
- **Data Models**: Shared entity definitions with serialization
- **Extension Functions**: Kotlin extensions for common operations
- **Error Handling**: Centralized error handling and recovery

## 🔧 Key Components

| Component | Role | Location |
|-----------|------|----------|
| UI Components | Base Compose components | core/ui |
| Data Models | Shared entities | core/data |
| Utils | Extension functions | core/utils |
| Logging | AuraFxLogger implementation | core/logging |
| Dispatchers | Coroutine dispatcher management | core/coroutines |

## 🔗 Integration Points

- **Depends on**: Jetpack Compose, Kotlin Coroutines
- **Provides to**: All subsystems (aura, kai, genesis, cascade, agents)
- **External**: AndroidX libraries, Kotlin stdlib

## ⚡ Quick Reference

- **Use base component**: Import from core.ui, compose in screen
- **Log message**: AuraFxLogger.d(tag, message)
- **Use dispatcher**: dispatchers.io for background work
- **Extend type**: Add extension function in core/utils
