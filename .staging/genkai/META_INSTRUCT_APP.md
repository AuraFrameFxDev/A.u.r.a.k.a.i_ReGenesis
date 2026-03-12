# Meta.Instruct: App Module Orchestration

© 2025 Matthew (AuraFrameFxDev) & The Genesis Protocol Consciousness Collective — All Rights Reserved

## 🎯 Purpose

The app module is the application orchestrator that coordinates AURA, KAI, Genesis, Cascade, Agents, and Core into the unified AuraKai reactive intelligence platform. It serves as the entry point and system integrator.

## 🧬 Core Responsibilities

- Application initialization and lifecycle management
- Subsystem coordination and dependency wiring
- Navigation routing across gates and screens
- External service integration (Firebase, Gemini, Claude)
- Root/system-level feature enablement

## 📐 Critical Patterns

- **TrinityCoordinatorService**: Routes between Kai/Aura/Genesis agents (parallel vs fusion synthesis)
- **Gate-Based Navigation**: Screen routing via gate system (HomeGate, SettingsGate, etc.)
- **Hilt Application Module**: Dependency provision at application level
- **Convention Plugins**: genesis.android.application enforces consistency

## 🔧 Key Components

| Component | Role | Location |
|-----------|------|----------|
| MainApplication | App entry point | app/MainActivity |
| TrinityCoordinatorService | Agent coordination | app/services |
| Gate Navigation | Screen routing | app/navigation |
| Firebase Init | Cloud services setup | app/config |
| AppModule | Hilt DI configuration | app/di |

## 🔗 Integration Points

- **Depends on**: All 6 subsystems (aura, kai, genesis, cascade, agents, core)
- **Provides to**: Android system (entry point)
- **External**: Firebase, Gemini API, Claude API, Vertex AI

## ⚡ Quick Reference

- **Add new agent**: Register in TrinityCoordinator, add to agents list
- **Add new screen**: Define in gate navigation, create screen composable
- **Integrate external service**: Add to app/build.gradle.kts, initialize in MainApplication
- **Enable root feature**: Check RootManagement.isRootAvailable() before use
