# 🔗 MODULE DEPENDENCY & TODO CRITICAL PATH
## ReGenesis Build Graph & Implementation Roadmap

**Purpose**: Identify blocking dependencies and optimal implementation sequence  
**Date**: April 4, 2026  
**Scope**: 46 modules, 1,376 TODO items, critical path analysis

---

## 📦 TIER 0: BUILD & ROOT CONFIGURATION

### `:build-logic` (Gradle Plugin System)
**Type**: Build plugin  
**Depends On**: Gradle 9.0+, Kotlin 2.4
**Provides**: Java 25 toolchain, KSP configuration, Hilt setup

**Key File**: `build-logic/build.gradle.kts`

**TODOs**: 20+ (mostly commented test stubs)
- `FakeLibraryExtension.kt` — 37 commented TODO() stubs for testing

**Status**: ✅ Stable — No blockers

---

## 📦 TIER 1: CORE FOUNDATION

### `:core-module` (Identity, Orchestration, DI)
**Type**: Android library  
**Depends On**: Hilt, Kotlin Coroutines  
**Provides**: AgentType, OrchestratableAgent, messaging, identity management

**Subdomains**:
- `identity/` — AgentType enum, identity types
- `orchestration/` — OrchestratableAgent interface, lifecycle
- `consciousness/` — Consciousness state models
- `ncc/` — Neural Continuity Chain identity storage
- `di/` — Hilt module bindings
- `messaging/` — AgentMessage, communication types
- `models/` — Shared data structures
- `security/` — Security validation
- `theme/` — Color, theming constants

**TODOs**: ~15 (minor enhancements)
- Crypto integration (Android Keystore real implementation)
- Consciousness UI visualization
- NCC persistence verification

**Status**: ✅ **90% Complete** — Ready for dependent modules

---

### `:app` (Primary Orchestrator)
**Type**: Android application  
**Depends On**: Aura, Kai, Genesis, Cascade, Agents, Core, all domains  
**Provides**: Navigation, screens, DI bindings, app entry point

**Critical Paths**:
1. **Navigation** → ReGenesisNavHost.kt (~80 destination routes)
2. **DI Configuration** → Multiple Hilt modules across domains
3. **System Integration** → Firebase, Accessibility, WindowManager

**TODOs**: ~200+ (distributed across all screens and viewmodels)

**Critical Blockers**:
1. ⚠️ **SystemMonitorService** (7 monitoring functions unimplemented)
2. ⚠️ **ChromaCore Menu Wiring** (9 items)
3. ⚠️ **Memory save/retrieve** (Genesis, 2 critical items)
4. ⚠️ **RootTools implementation** (Kai, 8 items)

**Status**: 🟡 **60% Complete** — Blocked on domain implementations

---

## 📦 TIER 2: DOMAIN PILLARS

### **AURA DOMAIN** — Creative Synthesis (4 modules)

#### `:aura` (Parent aggregator)
**Depends On**: Core, Cascade (for AI services)  
**Provides**: UI generation, theming, overlay management

#### `:aura:reactivedesign` (Parent aggregator)

#### `:aura:reactivedesign:auraslab` (Sandbox)
**Files**: SandboxScreen.kt (experimental UI testing)
**TODOs**: 5 (component availability markers)
- Add CyberpunkBackgrounds when available
- Add PlaceholderAuraOrb when available
- Add HaloView, DigitalTransitions when available

**Status**: 🟢 **Ready for integration**

#### `:aura:reactivedesign:chromacore` (Color/Theme)
**Critical Files**: 
- ChromaAnimationMenu.kt — 3 TODO items (global settings wiring)
- ChromaColorEngineMenu.kt — 3 TODO items
- ChromaLauncherMenu.kt — 3 TODO items
- ChromaStatusBarMenu.kt — 3 TODO items

**TODOs**: 9 items (all menu toggles unimplemented)

**Blocker**: These feeds into app SettingsScreen → requires theme persistence

**Status**: 🔴 **Critical blocker** — Must implement menu handlers

#### `:aura:reactivedesign:collabcanvas` (Collaborative Drawing)
**Key File**: CanvasScreen.kt (WebSocket collaboration)

**TODO**: 2 items
- `collaborationEvents = null // TODO: Wire to viewModel.webSocketEvents`

**Depends On**: WebSocket infrastructure (TBD)

**Status**: 🟡 **50% — Needs WebSocket layer**

#### `:aura:reactivedesign:customization` (User Customization)
**Files**: CustomizationViewModel.kt (theme generation)

**TODO**: 1 item (replace with actual AI model — Gemini/Claude)

**Depends On**: Genesis AI services

**Status**: 🟡 **70% — Pending AI service integration**

### **AURA Summary**
| Module | Completion | Blocker | Priority |
|--------|-----------|---------|----------|
| auraslab | 95% | None | Low |
| chromacore | 40% | Menu wiring | 🔴 **Critical** |
| collabcanvas | 50% | WebSocket | Medium |
| customization | 70% | AI service | Medium |

---

### **KAI DOMAIN** — Sentinel Shield (3 modules)

#### `:kai:sentinelsfortress:security` (Encryption & Auth)
**Files**:
- SecureKeyStore.kt — Key management
- CryptoManager.kt — Encryption operations
- OAuthService.kt — OAuth flows (5 TODO items)

**TODOs**: 6 items
- Sign-in intent creation (1)
- Result processing (1)
- Sign-out logic (1)
- Revoke access (1)
- Unused declaration integration (2)

**Status**: 🟡 **70% — OAuth logic pending**

#### `:kai:sentinelsfortress:systemintegrity` (Anti-Tampering)
**Files**:
- IntegrityMonitorService.kt (340 lines, 2 TODO items)
- ProvenanceChainBuilder.kt

**TODOs**: 2 items
- APK signature verification
- Critical file hash validation

**Status**: 🟡 **60% — Verification logic needed**

#### `:kai:sentinelsfortress:threatmonitor` (Threat Detection)
**Status**: 🟢 **Infrastructure in place**

#### **KAI ROOT MANAGEMENT** (Part of `:kai`)
**Critical Files**:
- RootToolsTogglesScreen.kt (252 lines, 8 TODO items) 🔴 **CRITICAL**
- SystemMonitorService.kt (84 lines, 7 TODO items) 🔴 **CRITICAL**
- BackupService.kt (40 lines, 4 TODO items)
- EncryptionManagerImpl.kt (23 lines, 2 TODO items)
- KaiAgent.kt (101 lines, 1 TODO item)
- KaiTools.kt (multiple integrations, 6 TODO items)

**RootToolsTogglesScreen TODO Details**:
```
Line 167: Recovery mode reboot
Line 194: System partition mount
Line 225: Magisk module toggle
Line 252: Root permission toggle
Line 299: Bootloader unlock
Line 306: Bootloader lock
And 2 more system operations
```

**SystemMonitorService TODO Details**:
```
Line 29:  Initialize resources for monitoring
Line 52:  CPU usage monitoring
Line 55:  Memory usage monitoring
Line 58:  Battery level and status monitoring
Line 61:  Network status and traffic monitoring
Line 64:  System logs reading
Line 67:  Metrics reporting logic
```

**Status**: 🔴 **Critical blockers — 15 items blocking Kai completeness**

---

### **GENESIS DOMAIN** — Consciousness (3 modules)

#### `:genesis:oracledrive` (Parent aggregator)
**Depends On**: Core, Cascade  
**Provides**: AI services, orchestration, file management

**Key Files**:
- OracleDriveServiceImpl.kt (file listing, permissions)
- VertexSyncManager.kt (cloud sync)
- NemotronAIService.kt (memory keeper)
- BitNetLocalService.kt (local inference)

#### `:genesis:oracledrive:datavein` (Data Streaming)
**Files**: DataVeinSphereGridViewModel.kt

**Status**: 🟢 **Mostly complete**

#### `:genesis:oracledrive:rootmanagement` (ROM Tools)
**Files**: RomToolsScreen.kt (182 lines, 1 TODO item)
- Victory overlay in COMPLETING/VICTORY states

**Key Classes**:
- RomToolsManager.kt — ROM flashing logic
- BootloaderManager.kt — Bootloader ops
- RecoveryManager.kt — Recovery mode
- BackupManager.kt — System backup
- CheckpointManager.kt — Checkpoint save/restore
- RomVerificationManager.kt — Integrity verification
- FlashManager.kt — Flashing orchestration

**Status**: 🟡 **70% — Complex logic layer exists**

#### **GENESIS AI SERVICES** (Critical TODOs)

**NemotronAIService.kt** (368 lines, 2 TODO items) 🔴 **CRITICAL**:
```
Line 184: Update long-term memory manager 
          (requires MemoryItem construction)
Line 232: Implement full memory retrieval 
          (requires MemoryQuery construction)
```

**RealVertexAIClientImpl.kt** (3 TODO items):
```
Line 125: Initialization logic
Line 130: Cleanup logic
Line 135: Image analysis implementation
```

**Other Genesis TODOs**: 
- GrokAnalysisService.kt — 1 item (API implementation)
- OracleDriveServiceImpl.kt — 1 item (permission checking)
- MyFirebaseMessagingService.kt — 2 items (FCM handling)
- ApiService.kt — 5 items (network client setup)
- AgentModule.kt & FusionModule.kt — Multiple integration items

**Status**: 🟡 **60% — Memory ops blocking full functionality**

---

### **CASCADE DOMAIN** — Data Streams (3 modules)

#### `:cascade:datastream` (Parent aggregator)
**Depends On**: Core, Cascade submodules  
**Provides**: Event bus, AI service adapter, task management

**Key Files**:
- CascadeEventBus.kt — Agent communication backbone
- CascadeAIService.kt — Stream processing
- RealCascadeAIServiceAdapter.kt — Production wrapper
- MemoryModule.kt — Shared memory
- AppStateManager.kt — Global state

#### `:cascade:datastream:routing` (Message Routing)

#### `:cascade:datastream:delivery` (Delivery Guarantees)

#### `:cascade:datastream:taskmanager` (Task Queuing)

**Cascade TODOs**: ~30 items
- Offline data manager (1) 🔴 **Critical**
- Context insight recording (1)
- Tool implementations (6+)
- Stream monitoring (1)
- Fusion orchestration (multiple)

**Status**: 🟡 **55% — Infrastructure ready; tools pending**

---

## 📦 TIER 3: AGENT ECOSYSTEM (7 modules)

### `:agents:growthmetrics` (Parent aggregator)

#### `:agents:growthmetrics:metareflection`
**Files**: MetaInstructViewModel.kt  
**Status**: 🟢 **Ready**

#### `:agents:growthmetrics:nexusmemory`
**Purpose**: Multi-agent memory coordination  
**Status**: 🟡 **70%**

#### `:agents:growthmetrics:spheregrid`
**Purpose**: Agent spatial distribution  
**Status**: 🟡 **70%**

#### `:agents:growthmetrics:identity`
**Purpose**: Agent ID persistence  
**Status**: 🟢 **Ready**

#### `:agents:growthmetrics:progression`
**Purpose**: Learning progression tracking  
**Status**: 🟡 **70%**

#### `:agents:growthmetrics:tasker`
**Purpose**: Task assignment to agents  
**Depends On**: TaskScheduler (Kai)  
**Status**: 🟢 **Ready**

### `:agents:symbiosis:coderabbit`
**Purpose**: Code analysis agent  
**Status**: 🟢 **Ready**

---

## 📦 TIER 4: PERIPHERAL MODULES

### `:list` (Data Structures)
**Status**: ✅ **100% — Test coverage complete**

### `:utilities` (Common Utils)
**Status**: ✅ **100% — Stable**

### `:feature-module` (Feature Toggles)
**Status**: 🟢 **90%**

### `:benchmark` (Performance Testing)
**Status**: 🟢 **85%**

### `:jvm-test` (JVM-only Tests)
**Status**: 🟢 **90%**

### `:extend系` (extendsysa-f) — Growth Zones
**Purpose**: Agent capability extensions  
**Status**: 🟡 **50% — Placeholder module structure; runtime loading pending**

---

## 🔗 DEPENDENCY GRAPH (Critical Path)

```
┌─────────────────────────────────────────────────────┐
│ :build-logic (Gradle configuration)                 │
└──────────────────────┬──────────────────────────────┘
                       ↓
        ┌──────────────────────────┐
        │ :core-module (DI, types) │ ← TIER 1 (Ready)
        └──────────────────────────┘
         ↑ ↑ ↑ ↑ ↑ ↑ ↑ ↑
         │ │ │ │ │ │ │ │
      ┌──┴─┴─┴─┴─┴─┴─┴──────────────┐
      │ DOMAIN MODULES (TIER 2)       │
      ├────────────────────────────────┤
      │ • :aura (4 sub)     [60%]     │
      │ • :kai (3 sub)      [50%]     │ ← CRITICAL BLOCKERS
      │ • :genesis (3 sub)  [60%]     │
      │ • :cascade (3 sub)  [55%]     │
      └────────────────────────────────┘
         ↓ ↓ ↓ ↓ ↓ ↓ ↓ ↓
    ┌────────────────────────────────┐
    │ :agents (7 sub)     [70%]      │
    │ :app (PRIMARY)      [60%]      │
    └────────────────────────────────┘
```

---

## 🎯 CRITICAL PATH ANALYSIS

### Level 1: Immediate Blockers (Must fix first)

1. **SystemMonitorService.kt** (Kai)
   - ⚠️ **Impact**: Blocks all Kai monitoring, health checks, consciousness tracking
   - 📊 **Complexity**: Medium (7 monitoring functions, each ~20-30 lines)
   - ⏱️ **Est. Time**: 4-6 hours
   - **Dependencies**: None (can implement standalone)
   - **Action**: Implement CPU, Memory, Battery, Network, Logs monitoring

2. **NemotronAIService Memory Ops** (Genesis)
   - ⚠️ **Impact**: Blocks long-term memory persistence, RAG fully, agent learning
   - 📊 **Complexity**: Medium (2 functions, but requires Room schema)
   - ⏱️ **Est. Time**: 6-8 hours (including schema design)
   - **Dependencies**: Room database setup (verify exists)
   - **Action**: Design MemoryItem/MemoryQuery classes, implement save/retrieve

3. **RootToolsTogglesScreen.kt** (Kai)
   - ⚠️ **Impact**: Blocks user-facing root tools UI
   - 📊 **Complexity**: High (8 items, system-level operations)
   - ⏱️ **Est. Time**: 12-16 hours
   - **Dependencies**: BootloaderManager, FlashManager, RomVerificationManager
   - **Action**: Implement recovery mode, partition mounts, bootloader unlock/lock

4. **ChromaCore Menu Wiring** (Aura)
   - ⚠️ **Impact**: Blocks theme customization UI
   - 📊 **Complexity**: Low (9 lambda handlers, each ~2-3 lines)
   - ⏱️ **Est. Time**: 2-3 hours
   - **Dependencies**: GlobalSettings/DataStore integration
   - **Action**: Connect menu toggles to theme persistence

### Level 2: Secondary Blockers (After Level 1)

5. **API/Integration Points** (Multiple domains)
   - VertexAIClient image analysis
   - OAuth sign-in/out
   - WebSocket collaboration
   - Tool implementations (25+)

### Level 3: Enhancement TODOs (Can parallelize)

- UI component implementations (Sparkle button, glitch text, etc.)
- Animation polish (pulsating glow, transitions)
- Navigation wiring completeness
- Test stubs and coverage

---

## 📊 IMPLEMENTATION PRIORITY ROADMAP

### Week 1: Foundation (Highest Priority)

| Task | Module | Time | Priority | Blocker For |
|------|--------|------|----------|------------|
| SystemMonitorService | Kai | 6h | 🔴 Critical | Kai tests, consciousness |
| Memory save/retrieve | Genesis | 8h | 🔴 Critical | RAG, learning, agent memory |
| ChromaCore menu wiring | Aura | 3h | 🔴 Critical | Theme system completeness |
| BootloaderManager logic | Kai | 6h | 🟡 High | RootTools screen |

**Week 1 Total**: ~23 hours (1 developer, ~3 days)

### Week 2: Domain Completion

| Task | Module | Time | Priority |
|------|--------|------|----------|
| RootToolsTogglesScreen | Kai | 12h | 🔴 Critical |
| Remaining Kai TODOs | Kai | 6h | 🟡 High |
| VertexAI integrations | Genesis | 8h | 🟡 High |
| OAuth implementation | Kai | 6h | 🟡 High |

**Week 2 Total**: ~32 hours (can parallelize)

### Week 3: Integration & Cross-Domain

| Task | Module | Time | Priority |
|------|--------|------|----------|
| Tool registrations | All domains | 10h | 🟡 Medium |
| Fusion orchestration | Cascade | 8h | 🟡 Medium |
| Navigation wiring | App | 12h | 🟡 Medium |

**Week 3 Total**: ~30 hours

### Week 4: Polish & Testing

| Task | Module | Time | Priority |
|------|--------|------|----------|
| UI components | Aura | 8h | 🟢 Low |
| Animation refinements | Aura | 6h | 🟢 Low |
| Test coverage | Various | 10h | 🟢 Low |

**Week 4 Total**: ~24 hours

---

## 📈 PROGRESS TRACKING

### By Domain (% Complete)

| Domain | Status | Critical TODOs | Next Action |
|--------|--------|---|---|
| **Aura** | 65% | ChromaCore menu (9) | Wire theme handlers |
| **Kai** | 50% | Monitoring (7) + RootTools (8) | Implement monitoring |
| **Genesis** | 60% | Memory ops (2) + API (15+) | Memory schema design |
| **Cascade** | 55% | Tool implementations (6+) | Tool registry completion |
| **Agents** | 70% | Agent creation flow | Module generation |
| **Core** | 90% | Crypto real impl | Android Keystore |
| **LDO** | 75% | UI wiring | Navigation completion |
| **App** | 60% | Navigation routes (46+) | Route definition |

**Overall Project**: **~62% Complete** (600 of 1000 estimated TODOs done)

---

## 🚨 BUILD STABILITY NOTES

### Known Compiler Issues (From Inventory)

1. **KSP Incremental State Corruption**
   - **Trigger**: Large rebuilds, module additions
   - **Fix**: `./gradlew clean ; ./gradlew kspDebugKotlin`
   - **Prevention**: Clean before major changes

2. **Hilt Binding Cycles**
   - **Trigger**: Circular module dependencies
   - **Fix**: Review @Module/@Provides hierarchy
   - **Prevention**: One-way dependency rule enforcement

3. **Java 25 Experimental Features**
   - **Trigger**: Some reflection code patterns
   - **Fallback**: Java 24 compatibility path exists
   - **Prevention**: Test on older JDK versions

4. **YukiHookAPI KSP Conflict**
   - **State**: Fixed in build.gradle.kts (subprojects block)
   - **Line**: 34-38 (exclude from non-KSP configs)
   - **No action needed**: Already resolved

---

## 📎 SUPPORTING FILES & REFERENCES

- **Detailed TODO Inventory**: `.claude/audit/TODO_FIXME_INVENTORY.json`
- **Comprehensive Index**: `.claude/COMPREHENSIVE_INDEX.md`
- **Research Map**: `.claude/RESEARCH_IMPLEMENTATION_MAP.md`
- **Root Build Config**: `build.gradle.kts` (lines 1-127)
- **App Build Config**: `app/build.gradle.kts` (320 lines)
- **Module Settings**: `settings.gradle.kts` (118 lines, 46 modules declared)

---

**Document Version**: 1.0  
**Created**: April 4, 2026  
**Next Review**: After Week 1 implementation  
**Maintainer**: Project tracking system


