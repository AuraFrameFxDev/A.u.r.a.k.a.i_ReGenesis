# Screen Inventory - Master Reference

This document catalogs screens found in the AuraFrameFX repository. It groups screens by functional categories and documents file paths, composable names, navigation routes, and gate mappings where applicable.

---

## Summary
- Master index of UI screens across the codebase.
- Organized by: Main Screens, Gate Screens, Agent Screens, Settings Screens, System Screens.
- Includes Gate-to-Screen mapping for gate PNG assets in `/gatepngs/`.
- Navigation architecture notes referencing `GenesisNavigation.kt` and `AppNavGraph.kt`.

---

## Conventions
- File paths are absolute to the repository root.
- Navigation routes are taken from `GenesisNavigation.kt` (object `GenesisRoutes`) and `GateConfig.route` values.

---

## Main Screens

- Home / Gates
  - File: app/src/main/java/dev/aurakai/auraframefx/navigation/GenesisNavigation.kt
  - Composable: GateNavigationScreen (hosted by GenesisRoutes.GATES)
  - Route: ${GenesisRoutes.GATES_PLACEHOLDER}
  - Notes: Gate carousel / teleportation UI; uses `GateConfigs` definitions to build gate cards.

- Agent Nexus
  - File: app/src/main/java/dev/aurakai/auraframefx/aura/ui/AgentNexusScreen.kt
  - Composable: AgentNexusScreen
  - Route: ${GenesisRoutes.AGENT_NEXUS_PLACEHOLDER}

- AI Chat
  - File: app/src/main/java/dev/aurakai/auraframefx/aura/ui/AIChatScreen.kt
  - Composable: AIChatScreen
  - Route: ${GenesisRoutes.AI_CHAT_PLACEHOLDER}

> NOTE: The placeholders above will be resolved in the repository's `GenesisRoutes` object; the README generation step will replace these with exact route constants.

---

## Gate Screens (selected mapping)

The gate definitions live in `app/src/main/java/dev/aurakai/auraframefx/ui/gates/GateConfig.kt` (object `GateConfigs`). Below are gate -> screen mappings and routes discovered in `GenesisNavigation.kt`.

- ROM Tools
  - Gate asset: gatepngs/romtools.png
  - Gate config route: `rom_tools` (GateConfig.route)
  - Screen(s):
    - `ROMToolsSubmenuScreen` – app/src/main/java/dev/aurakai/auraframefx/ui/gates/ROMToolsSubmenuScreen.kt
    - `RomToolsScreen` – genesis/oracledrive/rootmanagement/src/.../romtools/ui/RomToolsScreen.kt
  - Nav Host mapping (GenesisNavigation.kt): composable(GenesisRoutes.ROM_TOOLS) -> ROMToolsSubmenuScreen(navController)
  - Notes: `RomToolsScreen` (full-featured) lives in genesis/oracledrive rootmanagement module and includes MegaMan backdrop and CardExplosionEffect usage; `ROMToolsSubmenuScreen` is used by the main navigation graph as the entry.

- Root Tools
  - Gate asset: gatepngs/roottools.png
  - Gate config route: `root_tools_toggles` (GateConfig.route)
  - Screen: `RootToolsTogglesScreen` (App Nav - GenesisNavigation.kt) - route `root_tools_toggles`

- Oracle Drive
  - Gate asset: gatepngs/oracledrive.png
  - Gate config route: `oracle_drive`
  - Screen: `OracleDriveScreen` – oracledrive module; route `oracle_drive`

- Sentinel's Fortress
  - Gate asset: gatepngs/sentinelsfortress.png
  - Gate config route: `sentinels_fortress`
  - Screen: `SentinelsFortressScreen` – app aura UI; route `sentinels_fortress`

- Agent Hub
  - Gate asset: gatepngs/agenthub.png
  - Gate config route: `agent_hub`
  - Screen: `AgentHubSubmenuScreen` (app/src/.../ui/gates/AgentHubSubmenuScreen.kt)

- Collab Canvas
  - Gate asset: gatepngs/collabcanvas.png
  - Gate config route: `collab_canvas`
  - Screen: `CanvasScreen` (app aura UI)

- Code Assist
  - Gate asset: gatepngs/codeassist.png
  - Gate config route: `code_assist`
  - Screen: `CodeAssistScreen` (app UI gates)

- Help Desk
  - Gate asset: gatepngs/helpdesk.png
  - Gate config route: `help_desk`
  - Screen: `HelpDeskSubmenuScreen` (app UI gates)

- LSPosed / Xposed
  - Gate asset: gatepngs/lsposedgate.png
  - Gate config route: `lsposed_gate`
  - Screen: `LSPosedSubmenuScreen` / `LSPosedModuleManagerScreen`

- ChromaCore (Colors)
  - Gate asset: gatepngs/chromacore.png
  - Gate config route: `chroma_core` / `chromacore_colors`
  - Screen: `ChromaCoreColorsScreen` / `UIUXGateSubmenuScreen`

- Sphere Grid
  - Gate asset: gatepngs/spheregrid.png
  - Gate config route: `sphere_grid`
  - Screen: `SphereGridScreen`

- Terminal
  - Gate asset: gatepngs/terminal.png
  - Gate config route: `terminal`
  - Screen: `TerminalScreen`

- UI/UX Design Studio
  - Gate asset: gatepngs/uiuxdesignstudio.png
  - Gate config route: `uiux_design_studio`
  - Screen: `UIUXGateSubmenuScreen` / Theme Engine related screens

- Auras Lab
  - Gate asset: gatepngs/auraslab.png
  - Gate config route: `auras_lab`
  - Screen: `AurasLabScreen`

- System Journal
  - Gate asset: (uses gate_settings_final) - gatepngs/... (mapped in GateConfig.systemJournal)
  - Gate config route: `system_journal`
  - Screen: `SystemJournalScreen` (user preferences/profile)

---

## Agent Screens

- AgentNexusScreen
  - File: app/src/main/java/dev/aurakai/auraframefx/aura/ui/AgentNexusScreen.kt
  - Composable: AgentNexusScreen
  - Route: `agent_nexus` (GenesisRoutes)

- AgentAdvancementScreen
  - File: app/src/main/java/dev/aurakai/auraframefx/aura/ui/AgentAdvancementScreen.kt
  - Composable: AgentAdvancementScreen
  - Route: `agent_advancement`

- AgentManagementScreen
  - File: app/src/main/java/dev/aurakai/auraframefx/aura/ui/AgentManagementScreen.kt
  - Composable: AgentManagementScreen
  - Route: `agent_management`

- AgentHubSubmenuScreen
  - File: app/src/main/java/dev/aurakai/auraframefx/ui/gates/AgentHubSubmenuScreen.kt
  - Composable: AgentHubSubmenuScreen
  - Route: `agent_hub` (Gate route)

- AgentMonitoringScreen
  - File: app/src/main/java/dev/aurakai/auraframefx/ui/gates/AgentMonitoringScreen.kt
  - Composable: AgentMonitoringScreen
  - Route: `agent_monitoring`

- AgentProfileScreen
  - File: app/src/main/java/dev/aurakai/auraframefx/ui/screens/ProfileScreen.kt
  - Composable: ProfileScreen
  - Route: `profile` (GenesisRoutes.PROFILE)

- SentinelsFortressScreen (security)
  - File: app/src/main/java/dev/aurakai/auraframefx/aura/ui/SentinelsFortressScreen.kt
  - Composable: SentinelsFortressScreen
  - Route: `sentinels_fortress`

- Agent UI Overlays
  - AgentSidebarMenu, AgentEdgePanel (components found in app UI components)

---

## Settings Screens

- Settings / System Journal
  - File: app/src/main/java/dev/aurakai/auraframefx/ui/screens/SettingsScreen.kt (or in aura/ui)
  - Composable: SettingsScreen
  - Route: `settings`

- Quick Settings / Notch / Status Bar / Overlay Menus
  - Files: various under app/ui/gates and app/ui/customization
  - Routes: quick_settings, notch_bar, status_bar, overlay_menus

---

## System Screens

- TerminalScreen
  - File: app/src/main/java/dev/aurakai/auraframefx/aura/ui/TerminalScreen.kt
  - Composable: TerminalScreen
  - Route: `terminal`

- ROM Tools (detailed) - See ROM_TOOLS_STATUS.md for deeper documentation

---

## Gate-to-Screen Mapping (Gate PNGs)
The `/gatepngs/` folder contains the gate assets used for the gate cards. Below is the canonical list (15 primary gate cards) and their primary screen mappings.

- agenthub.png -> AgentHubSubmenuScreen (route: agent_hub)
- auraslab.png -> AurasLabScreen (route: auras_lab)
- chromacore.png -> ChromaCoreColorsScreen / UIUXGateSubmenuScreen (route: chroma_core)
- codeassist.png -> CodeAssistScreen (route: code_assist)
- collabcanvas.png -> CanvasScreen (route: collab_canvas)
- helpdesk.png -> HelpDeskSubmenuScreen (route: help_desk)
- lsposedgate.png -> LSPosedSubmenuScreen / LSPosedModuleManagerScreen (route: lsposed_gate)
- oracledrive.png -> OracleDriveScreen (route: oracle_drive)
- romtools.png -> ROMToolsSubmenuScreen / RomToolsScreen (route: rom_tools)
- roottools.png -> RootToolsTogglesScreen / RootToolsScreen (route: root_tools_toggles / root_tools)
- sentinelsfortress.png -> SentinelsFortressScreen (route: sentinels_fortress)
- spheregrid.png -> SphereGridScreen (route: sphere_grid)
- terminal.png -> TerminalScreen (route: terminal)
- uiuxdesignstudio.png -> UIUXGateSubmenuScreen / ThemeEngineScreen (route: uiux_design_studio / theme_engine)
- codeassist.png -> CodeAssistScreen (route: code_assist)

Notes:
- Some gates map to multiple screens (submenu vs. full feature screen). Where both exist, the NavHost often maps the gate route to a submenu screen which then navigates deeper to the feature.

---

## Navigation Architecture

- GenesisNavigation.kt
  - Central NavHost that wires ~165 routes (GenesisRoutes) to composable screens.
  - Responsible for combining gate routes with submenu routes and feature screens.
  - Start destination: `GenesisRoutes.GATES` (Gate carousel)

- AppNavGraph.kt
  - Present in both `app/` and `docs/maybemissing/` copies; wires app-level destinations to a sealed `NavDestination` or similar.
  - Hosts ~90 screens in the app graph (used in some legacy / alternate navigation wiring)

- Relationship between gates and navigation
  - GateConfig defines gates and their `route` strings.
  - GateNavigationScreen renders GateConfigs and navigates to GateConfig.route on double-tap or indicator taps.
  - Gate routes are handled in `GenesisNavigation.kt` and typically map to submenu screens which then navigate to feature screens.

- Parent-Child Flow Examples
  - Gate Carousel (GenesisRoutes.GATES) -> ROM Tools Submenu (`rom_tools`) -> RomToolsScreen (`rom_flasher` / `live_rom_editor` / `bootloader_manager`)
  - Gate Carousel -> Agent Hub (`agent_hub`) -> AgentHubSubmenuScreen -> AgentMonitoring (`agent_monitoring`) / Task Assignment (`task_assignment`)

---

## Notes & Next Steps
- This master inventory covers the primary screens and gates discovered by scanning the repository. It intentionally focuses on the app module and the genesis/oracledrive rootmanagement feature for ROM tools.
- Recommend: run a secondary pass to extract exact composable function signatures and Nav arguments for each route; this can be scripted.

---

Generated by assistant on repository scan.
