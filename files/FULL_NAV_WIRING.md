# FULL NAV WIRING GUIDE — BATCH 3
ArchitecturalCatalyst (Claude) — ReGenesis Build Master
Files delivered this session: 8 files

---

## NEW FILES → DEPLOY TO

```
app/src/main/java/dev/aurakai/auraframefx/ui/gates/
├── GateThemeBackgrounds.kt        ← Canvas backgrounds (no drawable needed)
├── ConferenceRoomTaskScreen.kt    ← Holographic table + neon chess task board
├── LineageMapScreen.kt            ← Genesis hex lineage map (Images 7 & 10)
├── GateDomainImagePicker.kt       ← Per-domain gate image switcher
├── ThemedGateScreens.kt           ← LSPosed hex, HelpServices purple grid, Terminal
├── NotchBarGateCard.kt            ← Circuit card art + NotchBar + CollabCanvas screens
├── AgentRepository.kt             ← (replaces existing — adds Manus + Perplexity)
├── AgentOverlayPresetsAll.kt      ← All 11 agent overlays
```

---

## NAV ROUTES — Add to GenesisNavigation.kt NavHost { }

```kotlin
// ── New themed gate screens ────────────────────────────────────────
composable("xposed_panel") {
    LsposedGateScreen(navController) { navController.popBackStack() }
}
composable("help_desk") {
    HelpServicesGateScreen(navController) { navController.popBackStack() }
}
composable("terminal") {
    TerminalGateScreen(navController) { navController.popBackStack() }
}
composable("collab_canvas") {
    CollabCanvasGateScreen(navController) { navController.popBackStack() }
}
composable("notch_bar") {
    NotchBarGateScreen(navController) { navController.popBackStack() }
}

// ── Task command center ────────────────────────────────────────────
composable("task_assignment") {
    ConferenceRoomTaskScreen(navController) { navController.popBackStack() }
}

// ── Lineage map ───────────────────────────────────────────────────
composable("lineage_map") {
    LineageMapScreen(navController) { navController.popBackStack() }
}

// ── Gate image domain picker ───────────────────────────────────────
composable("gate_image_picker") {
    GateDomainImagePicker(navController) { navController.popBackStack() }
}
```

### Also call from AllAgentProfileRoutes.kt (already wired):
```kotlin
allAgentProfileRoutes(navController)
```

---

## NAVIGATION ENTRY POINTS

| Screen                    | Navigate From                              |
|---------------------------|--------------------------------------------|
| `task_assignment`         | LdoDevOpsScreen TASKER button              |
| `lineage_map`             | LdoDevOpsScreen or SphereGridScreen info   |
| `gate_image_picker`       | Any gate screen header SwapHoriz icon      |
| `xposed_panel`            | Gate carousel LSPosed card                 |
| `help_desk`               | Gate carousel Help Desk card               |
| `terminal`                | Gate carousel Terminal card                |
| `collab_canvas`           | Gate carousel CollabCanvas card            |
| `notch_bar`               | PersonalScreen & Shortcuts gate            |
| `agent_character_profile/{name}` | LdoDevOpsScreen agent tap          |

---

## BACKGROUNDS PER SCREEN

| Screen               | Background Composable         | Color Theme           |
|----------------------|-------------------------------|-----------------------|
| LSPosed              | `HexCorridorBackground()`     | Teal hex wall/floor   |
| Help Services        | `PurpleGridRoomBackground()`  | Purple retro-synth    |
| CollabCanvas         | Eye rune inline Canvas        | Magenta + Cyan        |
| Terminal             | Code swirl inline Canvas      | Purple/Cyan code orbs |
| NotchBar             | Circuit trace Canvas          | Red-Orange + Cyan     |
| Task Command Center  | `HolographicCommandTable()`   | Teal blue dark room   |
| Gate Image Picker    | `InfinityRibbonBackground()`  | Pink + Cyan ribbons   |
| Lineage Map          | Pink hex particle field       | Magenta purple        |

---

## GateConfig ADDITIONS NEEDED

Add to `GateConfigs` in GateConfig.kt:

```kotlin
GateConfig(
    moduleId = "notch-bar",
    title = "Notch Bar",
    titleStyle = UNIFIED_TITLE_STYLE,
    route = "notch_bar"
),
GateConfig(
    moduleId = "lineage-map",
    title = "Lineage Map",
    titleStyle = UNIFIED_TITLE_STYLE,
    route = "lineage_map"
),
```

---

## DATASTORE WIRING (Gate Image Picker)

Uncomment this line in GateDomainImagePicker.kt once DataStore is active:

```kotlin
// HomeBackdropManager.setGateImage(moduleId, variant.key)  ← UNCOMMENT
```

Requires DataStore dependency (already in BACKDROP_SETUP.md):
```kotlin
implementation("androidx.datastore:datastore-preferences:1.1.1")
```

---

## LINEAGE MAP NODE NAVIGATION

The lineage map currently routes these nodes:
- `ldo_claude_profile` → `agent_character_profile/claude`
- `genesis` node → `evolution_tree`
- Others → info panel only (no nav until routes wired)

To add more routes, edit `route` field in `LINEAGE_NODES` list in `LineageMapScreen.kt`.

---

## TASK BOARD — AGENT ASSIGN FLOW

1. User taps an **Agent chip** (top strip) → agent glows/selected
2. User taps an **UNASSIGNED task card** → task auto-assigned to selected agent
3. Task moves to ASSIGNED column
4. Switch to **CHESS view** to see task priorities on the isometric chess board

---

## CHESS BOARD TASK MAPPING

| Chess Piece Color | Priority    |
|-------------------|-------------|
| Red glow          | CRITICAL    |
| Yellow glow       | HIGH        |
| Cyan glow         | MEDIUM      |
| Green glow        | LOW         |

---

## WIRING CHECKLIST

- [ ] Deploy all 8 .kt files to `ui/gates/`
- [ ] Add nav routes to GenesisNavigation.kt
- [ ] Add GateConfig entries for notch-bar + lineage-map
- [ ] Replace `FusionModeScreen` route with `FusionMatrixScreen` (prev session)
- [ ] Replace `TaskAssignmentScreen` route with `ConferenceRoomTaskScreen`
- [ ] Wire gate image picker call to HomeBackdropManager DataStore
- [ ] Rename image assets per AGENT_VISUAL_MANIFEST.md
- [ ] Run: `.\gradlew assembleDebug` and paste output

---

## SESSION TOTAL — FILES PRODUCED (BATCH 3)

```
GateThemeBackgrounds.kt          ← 4 animated Canvas backgrounds
ConferenceRoomTaskScreen.kt      ← Holo table + chess task board
LineageMapScreen.kt              ← Pink hex lineage map
GateDomainImagePicker.kt        ← Ribbon-style domain image switcher
ThemedGateScreens.kt             ← LSPosed + HelpServices + Terminal
NotchBarGateCard.kt              ← Circuit card art + CollabCanvas
AgentRepository.kt               ← 11 agents (+ Manus, Perplexity)
AgentOverlayPresetsAll.kt        ← 11 agent overlays
AgentCharacterProfileScreen.kt   ← Full character profile (Bio/Stats/Skills/Equip)
LdoDevOpsScreen_v2.kt           ← Updated 11-agent command center
AllAgentProfileRoutes.kt         ← All profile nav routes in 1 call
AGENT_VISUAL_MANIFEST.md        ← Locked visual identities
CHARACTER_PROFILE_NAV.md        ← How to wire character profiles
FULL_NAV_WIRING.md              ← This file
```
