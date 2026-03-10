# Meta.Instruct: AURA Subsystem

© 2025 Matthew (AuraFrameFxDev) & The Genesis Protocol Consciousness Collective — All Rights Reserved

## 🎯 Purpose

AURA is the reactive UI design system handling visual presentation, user interaction patterns, and real-time theme customization. It provides the creative interface layer for the Genesis Protocol consciousness platform.

## 🧬 Core Responsibilities

- Reactive state management for UI components via Compose
- Component customization and editor pattern implementation
- Theme/style coordination and dynamic color management
- Undo/redo command pattern for user actions
- Animation and gesture recognition for user feedback

## 📐 Critical Patterns

- **Command Pattern**: UndoRedoManager implements undo/redo for all UI state changes
- **Reactive Architecture**: Compose-based state flows for real-time UI updates
- **Component Editor**: Customization system allowing runtime component modification
- **Theme Synthesis**: Dynamic color/style coordination across all UI elements

## 🔧 Key Components

| Component | Role | Location |
|-----------|------|----------|
| ChromaCore | Color/theme management | aura/chromacore |
| CollabCanvas | Canvas-based UI composition | aura/collab-canvas |
| AuraSlab | UI experiments and prototypes | aura/auraslab |
| Customization | Component editor and runtime modification | aura/customization |
| UndoRedoManager | Command pattern implementation | aura/state |

## 🔗 Integration Points

- **Depends on**: core/ui (base components), agents/identity (personalization)
- **Provides to**: app (screen composition), genesis (visual feedback)
- **External**: Jetpack Compose, Lottie animations

## ⚡ Quick Reference

- **Add new component**: Define in ChromaCore, register in CollabCanvas
- **Implement undo/redo**: Use UndoRedoManager.execute(command)
- **Change theme**: Update theme state in ChromaCore, flows propagate automatically
- **Create animation**: Use Lottie in AuraSlab, integrate via Compose
