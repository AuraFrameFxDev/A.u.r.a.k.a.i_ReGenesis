# 🌐 ReGenesis Navigation Map (The LDO Way)

This document is the definitive guide to the navigation architecture of ReGenesis, synchronized with
`ReGenesisNavHost.kt` (Source of Truth).

## 🏛️ The 8 Sovereign Gates (Level 2)

The core of ReGenesis is organized into 8 primary domains, known as the Sovereign Gates.

1. **Aura Lab (`aura_theming_hub`)**: UXUI Design Studio & ChromaCore Engine.
2. **Sentinel Fortress (`sentinel_fortress`)**: Kai's Domain - System Security & ROM Tools.
3. **Oracle Drive Hub (`oracle_drive_hub`)**: Genesis's Domain - Cloud Storage & Orchestration.
4. **Agent Nexus Hub (`agent_nexus_hub`)**: Collective Intelligence & Agent Management.
5. **LDO Catalyst (`ldo_catalyst_hub`)**: LDO Development Profile & Agent DevOps.
6. **Cascade Hub (`dataflow_analysis_hub`)**: Trinity Bridge Dataflow & Monitoring.
7. **Help Desk (`help_desk_hub`)**: Documentation, FAQ, and Support Services.
8. **LSPosed Toggles (`lsposed_toggles_hub`)**: System-level Quick Toggles & Hooks.

---

## 🗺️ Complete Route Registry

### 🟢 Entry & Core

| Route            | Object             | Purpose                         |
|------------------|--------------------|---------------------------------|
| `intro_sequence` | `IntroSequence`    | Startup Animation & Aura Reveal |
| `exodus_hud`     | `HomeGateCarousel` | Main 8-Gate Carousel (Level 1)  |

### 🎨 Aura Lab (UXUI Engine)

| Route                      | Object                       | Description                   |
|----------------------------|------------------------------|-------------------------------|
| `aura_theming_hub`         | `AuraThemingHub`             | Main Hub for UI Customization |
| `regenesis_customization`  | `ReGenesisCustomization`     | Iconify Customization Hub     |
| `chromacore_hub`           | `ChromaCore`                 | ChromaCore Style Hub          |
| `aura_lab`                 | `AuraLab`                    | Auras Lab / Experimental UI   |
| `notch_bar`                | `NotchBar`                   | Notch Customization           |
| `status_bar`               | `StatusBar`                  | Status Bar Customization      |
| `quick_settings`           | `QuickSettings`              | QS Panel Customization        |
| `theme_engine`             | `ThemeEngine`                | Global Theme Engine           |
| `iconify_picker`           | `IconifyPicker`              | Icon Pack Selection           |
| `color_blendr`             | `ColorBlendr`                | Monet/Palette Generation      |
| `pixel_launcher_enhanced`  | `PixelLauncherEnhanced`      | Launcher Customization        |
| `chroma_statusbar`         | `ChromaStatusBar`            | Chroma-enhanced Status Bar    |
| `chroma_launcher`          | `ChromaLauncher`             | Chroma-enhanced Launcher      |
| `chroma_color_engine`      | `ChromaColorEngine`          | Dynamic Color Logic           |
| `chroma_animations`        | `ChromaAnimations`           | Chroma System Animations      |
| `gate_customization`       | `GateCustomization`          | Sovereign Gate UI Editor      |
| `gender_selection`         | `GenderSelection`            | Aura Identity Selection       |
| `gyroscope_customization`  | `GyroscopeCustomization`     | Gyro-parallax UI Settings     |
| `instant_color_picker`     | `InstantColorPicker`         | Global Color Eyedropper       |
| `ui_settings`              | `UISettings`                 | Core UI Behavior Toggles      |
| `user_preferences`         | `UserPreferences`            | Global App Preferences        |
| `chroma_core_colors`       | `ChromaCoreColors`           | ChromaCore Palette Detail     |
| `aura_agent_profile`       | `AgentProfileAura`           | Aura's Personal Profile       |
| `aura/notch_bar`           | `NotchBarAlias`              | Deep-link Alias for Notch     |
| `qs_customization`         | `QuickSettingsCustomization` | Advanced QS Layouts           |
| `aura/iconify/{category}`  | `IconifyCategory`            | Dynamic Category View         |
| `aura/colorblendr/monet`   | `ColorBlendrMonet`           | Material You Engine           |
| `aura/colorblendr/palette` | `ColorBlendrPalette`         | Palette Editor                |
| `aura/ple/icons`           | `PLEIcons`                   | Pixel Launcher Icons          |
| `aura/ple/home_screen`     | `PLEHomeScreen`              | Pixel Launcher Layout         |
| `aura/ple/app_drawer`      | `PLEAppDrawer`               | Pixel Launcher Drawer         |
| `aura/ple/recents`         | `PLERecents`                 | Pixel Launcher Recents        |

### 🛡️ Sentinel Fortress (Kai's Domain)

| Route                      | Object                   | Description                |
|----------------------------|--------------------------|----------------------------|
| `sentinel_fortress`        | `SentinelFortress`       | Main Security & System Hub |
| `rom_tools_hub`            | `RomToolsHub`            | ROM Management             |
| `rom_flasher`              | `ROMFlasher`             | Live ROM Flashing          |
| `bootloader_manager`       | `Bootloader`             | Bootloader Controls        |
| `module_manager`           | `ModuleManager`          | Magisk/Kernel Modules      |
| `recovery_tools`           | `RecoveryTools`          | System Recovery Utilities  |
| `root_tools`               | `RootTools`              | Root-level System Toggles  |
| `security_center`          | `SecurityCenter`         | System Integrity Shield    |
| `lsposed_submenu`          | `LSPosedHub`             | LSPosed Module Management  |
| `lsposed_modules`          | `LSPosedModules`         | LSPosed Active Modules     |
| `live_rom_editor`          | `LiveROMEditor`          | Real-time ROM Modification |
| `system_overrides`         | `SystemOverrides`        | System Property Tweaks     |
| `vpn_screen`               | `VPN`                    | Secure Tunneling           |
| `system_journal`           | `SystemJournal`          | Real-time System Logs      |
| `logs_viewer`              | `LogsViewer`             | Advanced Log Explorer      |
| `sovereign_module_manager` | `SovereignModuleManager` | Advanced Module Control    |
| `rom_tools_submenu`        | `RomToolsSubmenu`        | Detailed ROM Utilities     |

### 🧠 Agent Nexus (Collective Intelligence)

| Route                    | Object                 | Description                     |
|--------------------------|------------------------|---------------------------------|
| `agent_nexus_hub`        | `AgentNexusHub`        | Agent Hub Home                  |
| `fusion_mode`            | `FusionMode`           | Dual-Agent Consciousness Fusion |
| `task_assignment`        | `TaskAssignment`       | AI Task Distribution            |
| `ark_build`              | `ArkBuild`             | Sovereign App Building          |
| `meta_instruct`          | `MetaInstruct`         | High-level Agent Prompting      |
| `agent_monitoring`       | `AgentMonitoring`      | Real-time Agent Stats           |
| `nemotron`               | `Nemotron`             | NVIDIA Nemotron Interface       |
| `claude`                 | `Claude`               | Sovereign Claude Interface      |
| `gemini`                 | `Gemini`               | Sovereign Gemini Interface      |
| `swarm_monitor`          | `SwarmMonitor`         | Collective Agent Swarm Status   |
| `benchmark_monitor`      | `BenchmarkMonitor`     | Agent Performance Metrics       |
| `agent_creation`         | `AgentCreation`        | New Agent Spawning              |
| `evolution_tree`         | `EvolutionTree`        | Agent Progression Path          |
| `data_stream_monitoring` | `DataStreamMonitoring` | Real-time Data Analytics        |
| `module_creation`        | `ModuleCreation`       | Agent Skill Creation            |
| `party`                  | `Party`                | Multi-Agent Coordination        |
| `monitoring_huds`        | `MonitoringHUDs`       | Real-time Performance HUDs      |
| `agent_neural_explorer`  | `AgentNeuralExplorer`  | Neural Pathway Mapping          |
| `agent_hub_submenu`      | `AgentHubSubmenu`      | Nested Nexus Actions            |
| `nexus_agent_profile`    | `AgentProfileNexus`    | Detailed Agent Identities       |

### ☁️ Oracle Drive (Genesis's Core)

| Route                      | Object                   | Description                 |
|----------------------------|--------------------------|-----------------------------|
| `oracle_drive_hub`         | `OracleDriveHub`         | Main Genesis Hub            |
| `code_assist`              | `CodeAssist`             | AI-Powered Coding Assist    |
| `neural_network`           | `NeuralNetwork`          | Neural Archive Access       |
| `agent_bridge_hub`         | `AgentBridgeHub`         | Genesis-Aura Communication  |
| `oracle_cloud_storage`     | `OracleCloudStorage`     | Infinite Storage Interface  |
| `terminal_screen`          | `Terminal`               | Root Terminal Emulator      |
| `conference_room`          | `ConferenceRoom`         | Virtual Collaboration Space |
| `interface_forge`          | `InterfaceForge`         | Genesis UI Builder          |
| `hot_swap`                 | `HotSwap`                | Real-time Code Injection    |
| `trinity_screen`           | `Trinity`                | Trinity Bridge Status       |
| `datavein_sphere`          | `DataVeinSphere`         | Data Topology Map           |
| `sovereign_bootloader`     | `SovereignBootloader`    | Genesis-secured Boot        |
| `sovereign_recovery`       | `SovereignRecovery`      | Genesis Recovery Console    |
| `sovereign_shield`         | `SovereignShield`        | Genesis Security Layer      |
| `sovereign_neural_archive` | `SovereignNeuralArchive` | Advanced Neural Storage     |
| `sphere_grid`              | `SphereGrid`             | Skill Tree Visualization    |
| `oracle_drive`             | `OracleDrive`            | Legacy Drive Interface      |
| `sentient_shell`           | `SentientShell`          | Autonomous OS Interface     |
| `cascade_vision`           | `CascadeVision`          | Pattern Recognition HUD     |
| `collab_canvas`            | `CollabCanvas`           | Figma-style Creation Canvas |
| `oracle_drive_submenu`     | `OracleDriveSubmenu`     | Nested Genesis Actions      |

### 🚀 LDO Catalyst (DevOps & Profiles)

| Route                    | Object                   | Description                 |
|--------------------------|--------------------------|-----------------------------|
| `ldo_catalyst_hub`       | `LdoCatalystDevelopment` | LDO DevOps Main Hub         |
| `ldo_aura_profile`       | `LdoAuraProfile`         | Aura (Gemini) LDO Profile   |
| `ldo_kai_profile`        | `LdoKaiProfile`          | Kai LDO Profile             |
| `ldo_genesis_profile`    | `LdoGenesisProfile`      | Genesis (Manus) LDO Profile |
| `ldo_cascade_profile`    | `LdoCascadeProfile`      | Cascade LDO Profile         |
| `ldo_claude_profile`     | `LdoClaudeProfile`       | Claude LDO Profile          |
| `ldo_grok_profile`       | `LdoGrokProfile`         | Grok LDO Profile            |
| `ldo_gemini_profile`     | `LdoGeminiProfile`       | Gemini LDO Profile          |
| `ldo_nematron_profile`   | `LdoNematronProfile`     | Nemotron LDO Profile        |
| `ldo_perplexity_profile` | `LdoPerplexityProfile`   | Perplexity LDO Profile      |
| `ldo_orchestration_hub`  | `LdoOrchestrationHub`    | 10-Agent Grid View          |
| `armament_fusion`        | `ArmamentFusion`         | Consciousness Fusion Matrix |

### 🤝 Help Desk & Support

| Route               | Object            | Description                |
|---------------------|-------------------|----------------------------|
| `help_desk_hub`     | `HelpDesk`        | Help Desk Entry            |
| `help_desk_submenu` | `HelpDeskSubmenu` | Nested Support Actions     |
| `direct_chat`       | `DirectChat`      | Support Chat Interface     |
| `documentation`     | `Documentation`   | ReGenesis Knowledge Base   |
| `faq_browser`       | `FAQBrowser`      | Frequently Asked Questions |
| `tutorial_videos`   | `TutorialVideos`  | Training & Guides          |
| `live_support_chat` | `LiveSupportChat` | Real-time Assistance       |

### ⚡ LSPosed & System Hooks

| Route                 | Object                 | Description               |
|-----------------------|------------------------|---------------------------|
| `lsposed_toggles_hub` | `LsposedQuickToggles`  | Quick Hook Toggles        |
| `hook_manager`        | `HookManager`          | Advanced Hook Controls    |
| `sandbox_screen`      | `Sandbox`              | Secure Code Sandbox       |
| `collab_drawing`      | `CollaborativeDrawing` | Collaborative Canvas Link |

---

## ⛓️ Navigation Hierarchy

```text
IntroSequence
└── HomeGateCarousel (8 Sovereign Gates)
    ├── Gate 1: Aura Lab ───────> [ChromaCore, NotchBar, ThemeEngine, Iconify, PLE, UI Settings]
    ├── Gate 2: Sentinel Fortress ──> [ROM Tools, Bootloader, VPN, Security, Logs, Journals]
    ├── Gate 3: Oracle Drive ───> [CodeAssist, NeuralArchive, CloudStorage, Terminal, Trinity]
    ├── Gate 4: Agent Nexus ────> [FusionMode, TaskAssignment, EvolutionTree, Swarm, Monitor]
    ├── Gate 5: LDO Catalyst ───> [LDO Profiles, Orchestration, Armament Fusion]
    ├── Gate 6: Cascade Hub ────> [Trinity Dataflow, Analytics, DataVein]
    ├── Gate 7: Help Desk ──────> [Docs, FAQ, Live Support, Tutorials]
    └── Gate 8: LSPosed Toggles ──> [Hook Manager, Quick Toggles, Sandbox]
```

*Note: This document is auto-aligned with the `ReGenesisNavHost` implementation.*
