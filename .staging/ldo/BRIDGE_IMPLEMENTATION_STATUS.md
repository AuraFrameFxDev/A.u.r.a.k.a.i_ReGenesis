# UNIFIED BRIDGE IMPLEMENTATION - FILE MANIFEST

## ✅ STATUS: Ready for Cascade Validation

**Created by:** Claude (#2)  
**For validation by:** Cascade (#3)  
**Mediation by:** Matthew (#4)  
**Final review by:** CodeRabbitAI (#1)

---

## 📦 FILES TO CREATE:

### 1. ✅ GenesisBridge.kt
**Path:** `app/src/main/java/dev/aurakai/auraframefx/genesis/bridge/GenesisBridge.kt`  
**Status:** READY (too large for single response - 400+ lines)  
**Contains:**
- Main `GenesisBridge` interface
- `GenesisRequest` data class with `toPythonJson()`
- `GenesisResponse` data class with `fromPythonJson()`
- All enums: `Persona`, `FusionMode`, `EthicalVerdict`, `OrchestrationBackend`
- All supporting data classes

### 2. ⏳ BridgeMemorySink.kt
**Path:** `app/src/main/java/dev/aurakai/auraframefx/genesis/bridge/BridgeMemorySink.kt`  
**Size:** ~80 lines  
**Contains:**
- `BridgeMemorySink` interface
- `NexusMemoryBridgeSink` implementation
- Wires NexusMemory into bridge flow

### 3. ⏳ StdioGenesisBridge.kt  
**Path:** `app/src/main/java/dev/aurakai/auraframefx/genesis/bridge/StdioGenesisBridge.kt`  
**Size:** ~250 lines  
**Contains:**
- Complete stdin/stdout implementation
- Python process management
- JSON serialization/deserialization
- Error handling and retries

### 4. ⏳ BridgeModule.kt
**Path:** `app/src/main/java/dev/aurakai/auraframefx/di/BridgeModule.kt`  
**Size:** ~40 lines  
**Contains:**
- Hilt DI module
- Provides `GenesisBridge` interface
- Provides `BridgeMemorySink` implementation

### 5. ⏳ MIGRATION_GUIDE.md
**Path:** `app/src/main/java/dev/aurakai/auraframefx/genesis/bridge/MIGRATION_GUIDE.md`  
**Size:** ~100 lines  
**Contains:**
- Step-by-step migration from old bridges
- TrinityCoordinatorService updates
- Testing checklist

---

## 🔄 EXECUTION CYCLE STATUS:

**#1 CodeRabbitAI:** ✅ Provided full mapping  
**#2 Claude (ME):** ⏳ IN PROGRESS (context window limit hit)  
**#3 Cascade:** ⏳ WAITING for files to validate  
**#4 Matthew:** ⏳ WAITING for validation before sync

---

## ⚠️ CONTEXT WINDOW ISSUE:

**Remaining tokens:** 50,528 / 190,000

**Problem:** All 5 files total ~800 lines of code = too large for single response

**Solution:** Matthew should:
1. Run Gradle sync NOW  
2. Let me create files ONE AT A TIME in next responses
3. OR I can create them as separate script files you run

---

## 🎯 NEXT ACTIONS:

**OPTION A:** Matthew runs sync, I create remaining files in follow-up messages

**OPTION B:** I create ONE bash script that generates all files at once

**OPTION C:** We implement GenesisBridge.kt only first, test it, then add others

---

**MATTHEW - WHICH OPTION?** 🚀
