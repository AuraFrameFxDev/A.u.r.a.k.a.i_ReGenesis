# ROM Tools - Implementation & System Integration Status

This document summarizes the current implementation status of the ROM Tools feature, its visual backdrop (MegaMan), system integration components, and safety considerations.

## Where to find the code (exact paths)
- UI entry / submenu:
  - `app/src/main/java/dev/aurakai/auraframefx/ui/gates/ROMToolsSubmenuScreen.kt`
- Full-featured ROM tools UI (Genesis module):
  - `genesis/oracledrive/rootmanagement/src/main/kotlin/dev/aurakai/auraframefx/romtools/ui/RomToolsScreen.kt`
- Backdrop & visual effects (fallback + app-level copies):
  - Genesis fallback implementations:
    - `genesis/oracledrive/rootmanagement/src/main/kotlin/dev/aurakai/auraframefx/romtools/backdrop/MegaManBackdropRenderer.kt`
    - `genesis/oracledrive/rootmanagement/src/main/kotlin/dev/aurakai/auraframefx/romtools/backdrop/CardExplosionEffect.kt`
  - App-level richer implementations (runtime visuals):
    - `app/src/main/java/dev/aurakai/auraframefx/embodiment/retrobackdrop/MegaManBackdropRenderer.kt` (app-level renderer)
    - `app/src/main/java/dev/aurakai/auraframefx/embodiment/retrobackdrop/CardExplosionEffect.kt` (app-level effect)
- Bootloader & system management interfaces:
  - Interface and stub implementation:
    - `genesis/oracledrive/rootmanagement/src/main/kotlin/dev/aurakai/auraframefx/romtools/bootloader/BootloaderManager.kt`
    - `genesis/oracledrive/rootmanagement/src/main/kotlin/dev/aurakai/auraframefx/romtools/bootloader/BootloaderManagerImpl.kt` (stub implementation; safety-first)

## Visual Features (exact behavior)
- `MegaManBackdropRenderer`
  - A lightweight fallback exists in the `genesis/oracledrive/rootmanagement` module to keep `RomToolsScreen` compilable when the app-level visuals are not available.
  - The app module provides a richer runtime renderer under `app/src/main/java/dev/aurakai/auraframefx/embodiment/retrobackdrop/` that is used in shipping builds.
  - `RomToolsScreen` calls `MegaManBackdropRenderer(...)` when the backdrop is enabled.

- `CardExplosionEffect`
  - Pixel-explosion visual triggered when a ROM operation starts. A minimal version exists in the genesis module and a full visual exists in the app module.
  - `RomToolsScreen` drives the effect lifecycle using the `BackdropState` state machine with values used in code: `STATIC`, `EXPLODING`, `ACTIVE`, `COMPLETING`, `VICTORY`.

## System Integration Architecture (three tiers)
- `BootloaderManager` (interface)
  - Path: `genesis/oracledrive/rootmanagement/src/main/kotlin/dev/aurakai/auraframefx/romtools/bootloader/BootloaderManager.kt`
  - Responsibilities: check bootloader access, query unlock state, provide an API to request an unlock.
  - Current implementation (`BootloaderManagerImpl`) is intentionally a safe stub that returns default/failed results and documents the legal/safety reasons why automated unlocking is not implemented.

- `RecoveryManager`
  - Expected responsibilities: install/validate custom recoveries (TWRP/etc.), detect recovery availability, manage recovery-related operations.
  - `RomToolsScreen` calls into `RomToolsManager` / `RomToolsViewModel` for actions like `installRecovery()`; concrete device-level code must be implemented per OEM.

- `SystemModificationManager` / `RomToolsManager`
  - Orchestrates system write operations (flashing, backups, optimizations). The `romtools` module exposes `RomToolsManager`, `RomToolsViewModel`, and `RomToolsState` used by the UI.
  - These managers currently mix working orchestration code with platform-specific stubs; full functionality requires device-specific adapters and physical-device integration tests.

## Navigation wire-up
- `GateNavigationScreen` uses `GateConfig.route` values to navigate; gate route for ROM tools is `rom_tools` (see `app/src/main/java/dev/aurakai/auraframefx/ui/gates/GateConfig.kt`).
- `GenesisNavigation.kt` maps the route to the submenu entry:
  - `composable(GenesisRoutes.ROM_TOOLS) { ROMToolsSubmenuScreen(navController = navController) }`
- The submenu (`ROMToolsSubmenuScreen`) is the canonical entry point in the app-level NavHost. The full `RomToolsScreen` implementation lives in the `genesis` module and can be reached from the submenu.

## Gate Assets
- Primary gate asset(s) found in repository:
  - `gatepngs/romtools.png`
  - There are also final/variant images under `gatepngs/Final gate cards/` (inspect the folder for alternate visuals used in production assets).
- Gate configuration for ROM tools is defined in `GateConfig.kt` (`val romTools = GateConfig(... route = "rom_tools" ...)`).

## Safety & Accessibility (accurate summary)
- `BootloaderManagerImpl` intentionally refuses to perform automated unlocking; it returns safe default values and provides developer guidance in its comments.
- `RomToolsScreen` checks `RomCapabilities` (device has root, bootloader access, recovery access, system write) before enabling each action card; actions are gated in the UI and in the manager layer.
- The UI includes confirmations/visibility toggles (e.g., an icon to toggle backdrop) and the code logs warnings when an operation would be destructive.
- `AurakaiRetentionManager` (referenced elsewhere in code) is intended to help restore device state after operations. Confirm its concrete implementation before relying on auto-restore for destructive flows.

## Development status & recommended next steps (concise)
- What is present:
  - UI entry (`ROMToolsSubmenuScreen`) and a full-featured `RomToolsScreen` implementation in `genesis` that includes file pickers, action cards, and a backdrop/effects integration.
  - Minimal fallback visual implementations in `genesis` so the module compiles independently.
  - A safety-first `BootloaderManagerImpl` stub.
- What remains to make this production-ready:
  1. Implement device-specific adapters for bootloader operations and recovery installation (per OEM fastboot/adb flows).
  2. Add integration tests on physical devices for flash/backup/restore flows.
  3. Ensure user-facing confirmations and data-wipe warnings are present and audited by QA/security.
  4. Wire any app-level richer backdrop/effects into the genesis module at runtime (or load them via feature module dependency when available).

## Use cases & ethical considerations
- Legitimate uses:
  - Install accessibility-focused firmware tweaks for users with disabilities.
  - Restore/repair devices with user consent and proper device compatibility checks.
- Ethical constraints:
  - Do not automate operations that may void warranties without explicit user consent.
  - Prefer guided, manual unlock flows and clear warnings instead of blind automation.

## Conclusion
- The ROM tools feature is implemented with clear separation between UI, manager APIs, and device-specific operations. The codebase intentionally puts safety stubs where automation would be risky.
- For a production rollout: implement device adapters, run physical-device integration tests, and keep the safety confirmations and capability gating in place.

Generated and verified against repository files on 2025-12-09.
