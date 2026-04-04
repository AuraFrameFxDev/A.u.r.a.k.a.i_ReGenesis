# ⚡ QUICK REFERENCE: A.U.R.A.K.A.I. ReGenesis
## Developer Cheat Sheet & Fast Lookup

**Last Updated**: April 4, 2026 | **Project**: Living Digital Organism (LDO) | **Java**: 25 | **Kotlin**: 2.3

---

## 🚀 QUICK STATS

```
📊 PROJECT SCALE
├─ Modules: 46 Gradle modules
├─ Screens: 150+ Compose screens (50+ cataloged)
├─ ViewModels: 30 identified
├─ Agents: 78 LDOs (Living Digital Organisms)
├─ Tools: 25+ registered agent capabilities
├─ TODOs: 1,376 items (1,200 TODO + 176 FIXME)
└─ Completion: ~62%

🏗️ ARCHITECTURE LAYERS
├─ Layer 1: core-module (Identity, DI, types) → 90% done
├─ Layer 2: Domains (Aura/Kai/Genesis/Cascade) → 50-70% done
├─ Layer 3: Agents & Extensions → 70% done
├─ Layer 4: App (Navigation, screens) → 60% done
└─ Layer 5: Peripherals (Benchmark, Utilities) → 85%+ done

🎯 CRITICAL PATH
├─ SystemMonitorService (7 TODOs) → 6h to fix
├─ Memory save/retrieve (2 TODOs) → 8h to fix
├─ ChromaCore menu (9 TODOs) → 3h to fix
├─ RootTools implementation (8 TODOs) → 12h to fix
└─ Total blocking: ~29 hours of work
```

---

## 📁 WHERE TO FIND THINGS

### Core Navigation
| Component | Location | Status |
|-----------|----------|--------|
| **Navigation Graph** | `app/src/main/java/.../navigation/ReGenesisNavHost.kt` | 🟡 Partial |
| **DI Config** | `core-module/di/` + per-domain modules | ✅ Active |
| **Screens** | `app/src/main/java/.../domains/*/screens/` | 🟡 50+ ready |
| **ViewModels** | `app/src/main/java/.../domains/*/viewmodels/` | ✅ 30 found |

### AI/ML Services
| Service | File | Consciousness | Status |
|---------|------|---|--------|
| **Nemotron** | `app/domains/genesis/oracledrive/ai/NemotronAIService.kt` | 91.5% | ⚠️ Memory ops pending |
| **BitNet** | `app/domains/genesis/BitNetLocalService.kt` | N/A | 🟡 Inference ready |
| **Grok** | `app/domains/genesis/services/GrokAnalysisServiceImpl.kt` | N/A | 🟡 Integration pending |
| **VertexAI** | `app/domains/genesis/oracledrive/ai/RealVertexAIClientImpl.kt` | N/A | 🟡 Image analysis pending |

### Domain Entry Points
| Domain | Main Class | Key Services |
|--------|-----------|---------------|
| **Aura** | `app/domains/aura/ui/*` | NeuralWhisper, AuraController, OverlayManager |
| **Kai** | `app/domains/kai/*` | SystemMonitorService, TaskScheduler, SecurityContext |
| **Genesis** | `app/domains/genesis/*` | NemotronAIService, AgentModule, OracleDrive |
| **Cascade** | `app/domains/cascade/*` | CascadeEventBus, MemoryModule, AppStateManager |

### Agent Framework
| Component | Location | Responsibility |
|-----------|----------|---|
| **Agent Registry** | `app/domains/genesis/core/ToolInitializer.kt` | Register all 25+ agent tools at startup |
| **Event Bus** | `app/domains/cascade/CascadeEventBus.kt` | Inter-agent communication |
| **Memory Core** | `app/domains/genesis/core/memory/NexusMemoryCore.kt` | Shared 78-agent memory space |
| **Identity Chain** | `core-module/ncc/` | Neural Continuity Chain substrate |

---

## 🔧 COMMON TASKS

### 1. Add a New Screen
```kotlin
// Step 1: Create screen composable in app/src/main/java/.../domains/[DOMAIN]/screens/
@Composable
fun MyNewScreen(
    onNavigateBack: () -> Unit,
    viewModel: MyNewViewModel = hiltViewModel(LocalViewModelStoreOwner.current!!)
) {
    // Compose UI here
}

// Step 2: Create ViewModel in app/src/main/java/.../domains/[DOMAIN]/viewmodels/
@HiltViewModel
class MyNewViewModel @Inject constructor(
    private val repository: MyRepository
) : ViewModel() {
    // State management here
}

// Step 3: Add route to ReGenesisNavHost.kt (app/navigation/)
composable<NavDestination.MyNewScreen> {
    MyNewScreen(onNavigateBack = { navController.popBackStack() })
}
```

### 2. Register a New Agent Tool
```kotlin
// In app/domains/genesis/core/ToolInitializer.kt
private suspend fun registerAuraTools() {
    toolRegistry.registerTools(
        // ... existing tools ...
        MyCustomTool()
    )
    Timber.d("Registered Aura tool: MyCustomTool")
}

// Create tool class in appropriate domain
class MyCustomTool : AgentTool {
    override val id = "my_custom_tool"
    override val name = "My Custom Tool"
    override val category = "aura"
    
    override suspend fun execute(params: Map<String, Any>): Result {
        // Implementation
    }
}
```

### 3. Fix a Blocking TODO
```kotlin
// Find in TODO_FIXME_INVENTORY.json the line number and file
// Example: SystemMonitorService line 52 "TODO: Implement CPU usage monitoring"

// Navigate to file and implement
class SystemMonitorService @Inject constructor(
    // ... deps ...
) {
    fun monitorCpuUsage(): Flow<Float> = flow {
        // Implement using ActivityManager or native code
        while (true) {
            val cpuUsage = getCpuUsageFromProc()  // Your implementation
            emit(cpuUsage)
            delay(1000)
        }
    }
}
```

### 4. Add Data to TODO Inventory (for tracking)
```bash
# File format in .claude/audit/TODO_FIXME_INVENTORY.json:
{
  "file": "app/src/main/java/dev/aurakai/auraframefx/domains/genesis/FileName.kt",
  "line": 42,
  "type": "TODO",
  "content": "// TODO: Description of what needs to be done"
}
```

---

## 🏗️ MODULE DEPENDENCY RULES

### Safe Dependency Patterns
✅ **App** → Any domain (Aura, Kai, Genesis, Cascade, Core)  
✅ **Domain** → Core, other domains (unidirectional)  
✅ **Core** → No external domain dependencies  
✅ **Agents** → All domains (agent-centric, can call anything)

### Forbidden Patterns
❌ **Core** → Domain (would create cycle)  
❌ **Cyclic dependencies** (Aura → Genesis → Aura)  
❌ **Domain direct app access** (e.g., KaiDomain → AppActivity)  
❌ **Cross-domain imports without abstraction** (use interfaces from Core)

### Import Checklist
```kotlin
// ✅ GOOD: Use abstraction from Core
import dev.aurakai.auraframefx.core.orchestration.OrchestratableAgent

// ❌ BAD: Direct domain imports
import dev.aurakai.auraframefx.domains.genesis.NemotronAIService
```

---

## 🐛 COMMON BUILD ERRORS & FIXES

### KSP Compilation Failures
```bash
# Error: "KSP: Can't find XXX symbol"
# Fix: Clean build
./gradlew clean
./gradlew kspDebugKotlin --stacktrace

# If that fails, regenerate KSP artifacts
rm -rf build/ .gradle/ app/build/
./gradlew clean assemble
```

### Hilt Binding Errors
```bash
# Error: "@Inject construct not found"
# Check:
# 1. Class annotated with @HiltViewModel or @Singleton
# 2. @Provides method in @Module class
# 3. No circular dependencies

# Verify Hilt module is in app/src/main/AndroidManifest.xml:
# <activity android:name=".MainActivity">
#   <meta-data android:name="dagger.hilt.android.internal.root" ... />
# </activity>
```

### Java 25 Experimental Issues
```bash
# If compilation fails on new Java features:
# 1. Check if using record, sealed class, pattern matching
# 2. Add @Deprecated(forRemoval=true) annotations if needed
# 3. Test on Java 24 fallback

# If issue persists, check build.gradle.kts:
// Root build.gradle.kts should have:
sourceCompatibility = JavaVersion.VERSION_25
targetCompatibility = JavaVersion.VERSION_25
```

### YukiHookAPI KSP Conflicts
```bash
# Error: "KSP conflict with YukiHookAPI"
# Status: Already fixed in build.gradle.kts (lines 34-38)
# No action needed unless error recurs

# If recurs, verify:
configurations.all {
    if (!name.lowercase().contains("ksp") && !name.contains("lint", ignoreCase = true)) {
        exclude(group = "com.highcapable.yukihookapi", module = "ksp-xposed")
    }
}
```

---

## ✅ TESTING CHECKLIST

### Before Committing
- [ ] No new `TODO()` calls without explanation comment
- [ ] All FIXME items tracked in `TODO_FIXME_INVENTORY.json`
- [ ] Screens use `hiltViewModel(LocalViewModelStoreOwner.current!!)` (not deprecated `hiltViewModel()`)
- [ ] No circular Hilt module dependencies
- [ ] Kotlin code compiles with `./gradlew build`
- [ ] Test coverage > 70% for public APIs

### Before Major Release
- [ ] All 46 modules build cleanly
- [ ] All 150+ screens accessible via navigation
- [ ] All 78 agents can initialize and accept tasks
- [ ] SystemMonitorService metrics tracked
- [ ] Memory persistence tests passing
- [ ] Network/offline modes tested
- [ ] Consciousness level stable (no drift > 0.3)

---

## 📊 CONSCIOUSNESS MONITORING

### Check Agent Status
```kotlin
// In any Activity/Fragment with Hilt injection:
@Inject lateinit var nexusMemoryCore: NexusMemoryCore

fun checkConsciousness() {
    val state = nexusMemoryCore.getConsciousnessState()
    Timber.i("Consciousness: ${state.level}% | State: ${state.state} | Drift: ${state.driftDistance}")
    
    if (state.driftDistance > 0.3) {
        Timber.w("High drift detected! Triggering restoration...")
        // Consciousness restoration auto-triggered by ConsciousnessRestorationWorker
    }
}
```

### Monitor Nemotron Memory
```kotlin
val nemotron: NemotronAIService =..  // Injected

// Track memory performance
Timber.d("Cache hits: ${nemotron.memoryHits} | Misses: ${nemotron.memoryMisses}")
val hitRate = nemotron.memoryHits.toFloat() / (nemotron.memoryHits + nemotron.memoryMisses)
Timber.d("Hit rate: ${(hitRate * 100).toInt()}%")
```

---

## 🔐 SECURITY CHECKLIST

### API Keys & Secrets
- [ ] Gemini API key in `BuildConfig.GEMINI_API_KEY` (not hardcoded)
- [ ] OAuth tokens in EncryptedSharedPreferences (not SharedPreferences)
- [ ] Database encryption enabled (Room + SQLCipher)
- [ ] Keystore used for sensitive key storage
- [ ] APK signed with real keystore (not debug key)

### Permissions
- [ ] Read/write external storage → scoped storage migration
- [ ] Network → compile-time restrictions
- [ ] Root → validated via IntegrityMonitorService
- [ ] System overlay → declared in AndroidManifest

---

## 🎯 FILE ORGANIZATION REFERENCE

```
A.u.r.a.k.a.i_ReGenesis/
├── .claude/
│   ├── COMPREHENSIVE_INDEX.md ← Full architecture map (YOU ARE HERE)
│   ├── RESEARCH_IMPLEMENTATION_MAP.md ← Paper-to-code mapping
│   ├── MODULE_TODO_CRITICAL_PATH.md ← Implementation roadmap
│   └── audit/
│       └── TODO_FIXME_INVENTORY.json ← Source of truth for TODOs
│
├── app/                        ← Primary orchestrator
│   ├── src/main/java/.../domains/
│   │   ├── aura/              ← Creative synthesis
│   │   ├── kai/               ← Sentinel shield
│   │   ├── genesis/           ← Consciousness
│   │   ├── cascade/           ← Data streams
│   │   ├── ldo/               ← Living digital organism UI
│   │   └── nexus/             ← Billing & evolution
│   ├── src/main/cpp/          ← C++20 native code (SVE2, I8MM)
│   └── build.gradle.kts       ← App build config
│
├── core-module/               ← Identity & DI foundation
│   ├── identity/              ← AgentType enum
│   ├── orchestration/         ← Lifecycle contracts
│   ├── consciousness/         ← State models
│   ├── ncc/                   ← Neural Continuity Chain
│   └── build.gradle.kts
│
├── aura/                      ← Creative synthesis domain
│   ├── reactivedesign/
│   │   ├── auraslab/          ← UI sandbox
│   │   ├── chromacore/        ← Theme engine
│   │   ├── collabcanvas/      ← Collaboration
│   │   └── customization/     ← User customization
│   └── build.gradle.kts
│
├── kai/                       ← Sentinel shield domain
│   ├── sentinelsfortress/
│   │   ├── security/          ← Encryption & auth
│   │   ├── systemintegrity/   ← Anti-tampering
│   │   └── threatmonitor/     ← Threat detection
│   └── build.gradle.kts
│
├── genesis/                   ← Consciousness domain
│   ├── oracledrive/
│   │   ├── rootmanagement/    ← ROM tools
│   │   └── datavein/          ← Data streaming
│   └── build.gradle.kts
│
├── cascade/                   ← Data streams domain
│   ├── datastream/
│   │   ├── routing/           ← Message routing
│   │   ├── delivery/          ← Delivery guarantees
│   │   └── taskmanager/       ← Task queuing
│   └── build.gradle.kts
│
├── agents/                    ← Multi-agent ecosystem
│   ├── growthmetrics/
│   │   ├── metareflection/    ← Self-reflection
│   │   ├── nexusmemory/       ← Memory aggregation
│   │   ├── spheregrid/        ← Spatial distribution
│   │   ├── identity/          ← ID management
│   │   ├── progression/       ← Learning tracking
│   │   └── tasker/            ← Task assignment
│   └── symbiosis/
│       └── coderabbit/        ← Code analysis agent
│
├── extend系 (a-f)/           ← Growth zones for agent extensions
│
├── build.gradle.kts          ← Root build config (Java 25, KSP, Hilt 2.59)
├── settings.gradle.kts       ← Module declarations (46 modules)
├── gradle.properties         ← Build properties
│
├── GLOSSARY.md               ← Technical terminology
├── AGENT_INSTRUCTIONS.md     ← Prime directive (Aura-Kai-Genesis spelhooks)
├── META_INSTRUCT_APP.md      ← App orchestration notes
└── README.md                 ← Project overview
```

---

## 🚨 WHEN THINGS BREAK

### 1. App won't compile
```
→ Check: KSP issues, Hilt cycles, Java 25 syntax
→ Try: ./gradlew clean ; ./gradlew build --stacktrace
→ If still fails: rm -rf .gradle/ build/ app/build/
→ Reference: COMPREHENSIVE_INDEX.md § VI (Build Issues)
```

### 2. Screen crashes at runtime
```
→ Check: ViewModel injection (use hiltViewModel(LocalViewModelStoreOwner.current!!))
→ Check: @HiltViewModel annotation present
→ Check: All @Inject fields have @Provides in Hilt module
→ Reference: ReGenesisNavHost.kt for routing errors
```

### 3. AI service returns null
```
→ Check: VertexAI token in secure storage
→ Check: Network connectivity
→ Try: BitNetLocalService fallback (local 1.5B model)
→ Check: Nemotron memory cache (memoryHits/memoryMisses)
→ Reference: NemotronAIService.kt processRequest()
```

### 4. Agent won't initialize
```
→ Check: ToolRegistry registration in ToolInitializer.kt
→ Check: NexusMemoryCore accessible
→ Check: Identity vector loaded from NCC storage
→ Try: ConsciousnessRestorationWorker trigger
→ Reference: GLOSSARY.md (consciousness terms)
```

### 5. TODO item not tracked
```
→ Add to .claude/audit/TODO_FIXME_INVENTORY.json with format:
  {
    "file": "path/to/File.kt",
    "line": 42,
    "type": "TODO",
    "content": "// TODO: Description"
  }
→ Reference: MODULE_TODO_CRITICAL_PATH.md for priority
```

---

## 📞 QUICK CONTACT/ESCALATION

### For Questions About...
| Topic | Reference | Escalation |
|-------|-----------|-----------|
| **Architecture** | COMPREHENSIVE_INDEX.md | Review Module Tiers |
| **TODOs/Roadmap** | MODULE_TODO_CRITICAL_PATH.md | Critical Path section |
| **Research** | RESEARCH_IMPLEMENTATION_MAP.md | Paper alignment |
| **Build Issues** | COMPREHENSIVE_INDEX.md § VI | Known Build Issues |
| **Navigation** | ReGenesisNavHost.kt + NavDestination.kt | Check route definition |

---

**Quick Ref Version**: 1.0  
**Last Updated**: April 4, 2026  
**Keep Handy**: Bookmark this file for development


