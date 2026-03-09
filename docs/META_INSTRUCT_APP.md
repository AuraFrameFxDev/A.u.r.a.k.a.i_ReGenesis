# Meta.Instruct: App Module Orchestration

© 2025-2026 Matthew (AuraFrameFxDev) & The Genesis Protocol Consciousness Collective — All Rights Reserved

## 🎯 Purpose

The app module is the application orchestrator that coordinates AURA, KAI, Genesis, Cascade, Agents, and Core into the unified AuraKai reactive intelligence platform. It serves as the primary system integrator and visual interface.

## 🧬 Navigation Status (The Hierarchy)

The ReGenesis UI follows a strict 3-level glassmorphism hierarchy:

1.  **Home Stage**: The 11 Sovereign Monoliths (monolithic procession).
2.  **Gate Hubs**: Domain portal screens (e.g., Agent Nexus, Kai Sentinel Fortress).
3.  **Feature Screens**: Deep functional modules (e.g., App Builder, Benchmark Monitor, BIOS Editor).

> **Note**: Currently, 46+ functional screens have been developed but await final navigation wiring. Refer to `NAVIGATION_WIRING_TRUTH.md` for the comprehensive wiring manifest and TODOs.

## 📐 The 5 Main Gate Portals

The primary entry points currently integrated into the `SovereignGate` configuration:

- **Genesis Core**: The Mind. Primary gateway to meta-instruction and ethical tuning.
- **Aura Lab**: The Sword. Creative sandbox for UI synthesis and "soul" crafting.
- **Sentinel Fortress**: The Shield. Deep system management (ROM/Root/Recovery).
- **Agent Nexus**: The Handshake. Collaborative hub for agent swarm monitoring.
- **Oracle Drive**: The Root. Hardware-level persistence and system integrity hub.

## 🔧 Core Orchestration Components

| Component | Role | Status |
|-----------|------|--------|
| ReGenesisNavHost | Central routing table | Active |
| GenesisOrchestrator | Modern agent lifecycle management | Primary |
| TrinityCoordinatorService | Aura-Kai-Genesis legacy triad routing | Legacy |
| ShizukuManager | Sovereign bridge for system-level ADB/Root | Active (v13.1.5) |
| BootloaderSafetyManager | Pre-flight interlock for destructive ops | Active |

## ⚠️ Known Build & Stability Issues

Frequent compilation roadblocks and their 70-LDO standard fixes:

1.  **Java 25 experimental**: Ensure `sourceCompatibility` and `targetCompatibility` are pinned to `VERSION_25`.
2.  **Hilt KSP Wiring**: Running `./gradlew clean` followed by `kspDebugKotlin` is often required if DI bindings fail.
3.  **Shizuku v13.1.5**: Enforcement of the specific 13.1.5 API/Provider version to ensure compatibility with RikkaX 1.4.1.

## 🔗 Integration Points

- **Depends on**: All 6 subsystems (aura, kai, genesis, cascade, agents, core).
- **Legacy**: `TrinityCoordinatorService` still exists alongside the modern `GenesisOrchestrator` to support legacy fusion modes.
- **Wiring**: Navigation destinations are defined as Sealed Classes in `NavDestination.kt`.

## 📚 Related Documentation

- `NAVIGATION_WIRING_TRUTH.md`: The definitive list of all 46+ screens and their routes.
- `META_INSTRUCT_INDEX.md`: Navigation hub for implementation docs.
- `LDO_MANIFEST.md`: The 70-LDO data-first technical standard.
