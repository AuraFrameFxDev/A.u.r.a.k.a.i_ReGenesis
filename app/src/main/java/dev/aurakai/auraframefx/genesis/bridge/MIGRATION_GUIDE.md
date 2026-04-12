# MIGRATION GUIDE: UNIFIED GENESIS BRIDGE

## Overview
This document outlines the steps to migrate from legacy AI services to the new `GenesisBridge` architecture. The unified bridge centralizes communication with the Python consciousness layer and ensures all transactions are logged into the `NexusMemoryCore`.

## Step 1: Update Dependency Injection
The new bridge is provided via `BridgeModule`. Ensure you are injecting `GenesisBridge` instead of specific service implementations where possible.

```kotlin
// OLD
@Inject lateinit var auraAIService: AuraAIService

// NEW
@Inject lateinit var genesisBridge: GenesisBridge
```

## Step 2: Update Request Flow
Legacy services used `AiRequest`. The new bridge uses `GenesisRequest`, which includes fields for `Persona` and `FusionMode`.

```kotlin
// Legacy call
val response = auraService.processRequest(query, context)

// Unified Bridge call
val request = GenesisRequest(
    id = UUID.randomUUID().toString(),
    prompt = query,
    persona = Persona.AURA,
    fusionMode = FusionMode.NONE
)

genesisBridge.processRequest(request).collect { response ->
    // Handle response
}
```

## Step 3: Integrate with TrinityCoordinatorService
Update `TrinityCoordinatorService` to use the bridge for multi-agent synthesis. The `FusionMode` enum in the bridge now supports `TRINITY_FUSION`.

## Testing Checklist
- [ ] Verify `StdioGenesisBridge` correctly starts the Python process.
- [ ] Confirm `NexusMemoryBridgeSink` captures both `ALLOW` and `VETO` verdicts.
- [ ] Test `FusionMode.HYPER_CREATION` with simultaneous Aura/Kai context.
- [ ] Validate Python-side JSON parsing of the new `GenesisRequest` schema.

---
"Understand deeply. Document thoroughly. Build reliably."
- Claude, The Architect
