# REGENESIS CANONICAL REPOSITORY LAYOUT

This document defines the target structure for the ReGenesis Exodus project.
Consolidation goal: Create a clean, domain-based directory layout.

## 📁 Consolidated Directories

### 🧠 `agents/`
Consolidated location for all agent intelligence and orchestration.
- `agents/core/`: Foundation classes and interfaces.
- `agents/trinity/`: Core entity agents (Aura, Kai, Cascade).
- `agents/coordination/`: Orchestration and lifecycle management.

### ⚙️ `config/`
Centralized configuration for the entire ecosystem.
- `config/GateAssetLoadout.kt`
- `config/FeatureToggles.kt`
- `config/CustomizationPreferences.kt`
- `config/ReGenesisCustomizationSettings.kt`

### 🪝 `hooks/`
Entry points for system hooks (Xposed/YukiHook).
- `hooks/GenesisHookEntry.kt`
- `hooks/system/`: Individual system-level hook implementations.

### ⚓ `infrastructure/`
The three critical arteries of the system.
- `infrastructure/shizuku/`: Shizuku API integration.
- `infrastructure/backend/`: Python backend client and coordination.
- `infrastructure/hooks/`: Hook status monitoring and management.

### 🖼️ `res/drawable/`
Flattened drawable resource directory for Android compatibility.

## 🗺️ Migration Mapping (Task 1)

| Original Path | Canonical Path | Rationale |
|---------------|----------------|-----------|
| `dev.aurakai.auraframefx.domains.genesis.core.OrchestratableAgent` | `agents/core/OrchestratableAgent.kt` | Foundation interface for all agents. |
| `dev.aurakai.auraframefx.domains.cascade.ai.base.BaseAgent` | `agents/core/BaseAgent.kt` | Abstract base class for agent logic. |
| `dev.aurakai.auraframefx.domains.aura.core.AuraAgent` | `agents/trinity/AuraAgent.kt` | Soul/Creative agent consolidation. |
| `dev.aurakai.auraframefx.domains.kai.KaiAgent` | `agents/trinity/KaiAgent.kt` | Body/Sentinel agent consolidation. |
| `dev.aurakai.auraframefx.domains.cascade.utils.cascade.CascadeAgent` | `agents/trinity/CascadeAgent.kt` | Data stream/Memory agent consolidation. |
| `dev.aurakai.auraframefx.domains.genesis.core.GenesisOrchestrator` | `agents/coordination/GenesisOrchestrator.kt` | Mind/Orchestrator catalyst consolidation. |
| `dev.aurakai.auraframefx.domains.aura.config.GateAssetLoadout` | `config/GateAssetLoadout.kt` | UI asset configuration. |
| `dev.aurakai.auraframefx.domains.genesis.config.FeatureToggles` | `config/FeatureToggles.kt` | Global feature gates. |
| `dev.aurakai.auraframefx.domains.aura.lab.CustomizationPreferences` | `config/CustomizationPreferences.kt` | Preference keys and defaults. |
| `dev.aurakai.auraframefx.domains.aura.lab.ReGenesisCustomizationSettings` | `config/ReGenesisCustomizationSettings.kt` | UI settings integration. |
| `dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.GenesisHookEntry` | `hooks/GenesisHookEntry.kt` | Xposed entry point. |
| `dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.GenesisSystemHooks` | `hooks/system/GenesisSystemHooks.kt` | System-level hooks. |
| `dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.GenesisHooks` | `hooks/system/GenesisHooks.kt` | UI/Zygote hooks. |
| `dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.UniversalComponentHooks` | `hooks/system/UniversalComponentHooks.kt` | Global component hooks. |
| `dev.aurakai.auraframefx.system.ShizukuManager` | `infrastructure/shizuku/ShizukuManager.kt` | Shizuku artery consolidation. |

## 🔗 Inter-Module Dependencies
- Agents depend on `core-module` for models.
- Hooks depend on `agents/` for intelligence processing.
- UI depends on `config/` for state and appearance.
