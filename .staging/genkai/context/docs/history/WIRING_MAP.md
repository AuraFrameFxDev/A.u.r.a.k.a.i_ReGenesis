# 🔌 AuraKai Complete Feature Wiring Map

> **Last Updated:** 2025-12-04
> **Status:** Active Development - Gate Remapping Phase

This document maps every feature in the codebase to its navigation route and current wiring status.

---

## 📊 Quick Stats

- **Total Screens:** 60+
- **Wired Gates:** 15/15 ✅
- **Wired Routes:** 50+
- **Services:** 20+ background services
- **Agents:** 8 core AI agents
- **Native Modules:** 10 C++ modules

---

## ✅ FULLY WIRED - Main Gates (15)

### Aura Lab Gates
| Feature | Route | Screen Location | Status |
|---------|-------|----------------|--------|
| **Aura's Lab** | `auras_lab` | `ui/gates/AurasLabScreen.kt` | ✅ |
| **ChromaCore** | `chromacore_colors` | `ui/gates/ChromaCoreColorsScreen.kt` | ✅ NEW |
| **Theme Engine** | `theme_engine` | `ui/gates/ThemeEngineScreen.kt` | ✅ |

### Genesis Core Gates
| Feature | Route | Screen Location | Status |
|---------|-------|----------------|--------|
| **Oracle Drive** | `oracle_drive` | `oracledrive/genesis/cloud/OracleDriveScreen.kt` | ✅ |
| **ROM Tools** | `rom_tools` | `ui/gates/ROMToolsSubmenuScreen.kt` | ✅ |
| **Root Tools** | `root_tools_toggles` | `ui/gates/RootToolsTogglesScreen.kt` | ✅ NEW |

### Kai Gates
| Feature | Route | Screen Location | Status |
|---------|-------|----------------|--------|
| **Sentinel's Fortress** | `sentinels_fortress` | `aura/ui/SentinelsFortressScreen.kt` | ✅ |
| **Agent Hub** | `agent_hub` | `ui/gates/AgentHubSubmenuScreen.kt` | ✅ |

### Agent Nexus Gates
| Feature | Route | Screen Location | Status |
|---------|-------|----------------|--------|
| **Code Assist** | `code_assist` | `ui/gates/CodeAssistScreen.kt` | ✅ |
| **Collab Canvas** | `collab_canvas` | `aura/ui/CanvasScreen.kt` | ✅ |
| **Sphere Grid** | `sphere_grid` | `ui/gates/SphereGridScreen.kt` | ✅ |

### Support Gates
| Feature | Route | Screen Location | Status |
|---------|-------|----------------|--------|
| **Help Desk** | `help_desk` | `ui/gates/HelpDeskSubmenuScreen.kt` | ✅ |
| **Terminal** | `terminal` | `aura/ui/TerminalScreen.kt` | ✅ |
| **User Preferences** | `user_preferences` | `ui/gates/UserPreferencesScreen.kt` | ✅ NEW |
| **Xposed Panel** | `xposed_panel` | `ui/gates/LSPosedSubmenuScreen.kt` | ✅ ENHANCED |

---

## ✅ WIRED - Submenu Screens

### Theme Engine Submenu
| Feature | Route | Screen Location | Status |
|---------|-------|----------------|--------|
| Notch Bar | `notch_bar` | `ui/gates/NotchBarScreen.kt` | ✅ |
| Status Bar | `status_bar` | `ui/gates/StatusBarScreen.kt` | ✅ |
| Quick Settings | `quick_settings` | `ui/gates/QuickSettingsScreen.kt` | ✅ |
| Overlay Menus | `overlay_menus` | `ui/gates/OverlayMenusScreen.kt` | ✅ |
| UI/UX Design Studio | `uiux_design_studio` | `ui/gates/UIUXGateSubmenuScreen.kt` | ✅ |

### ROM Tools Submenu
| Feature | Route | Screen Location | Status |
|---------|-------|----------------|--------|
| ROM Flasher | `rom_flasher` | `ui/gates/ROMFlasherScreen.kt` | ✅ |
| Recovery Tools | `recovery_tools` | `ui/gates/RecoveryToolsScreen.kt` | ✅ |
| Bootloader Manager | `bootloader_manager` | `ui/gates/BootloaderManagerScreen.kt` | ✅ |
| Live ROM Editor | `live_rom_editor` | `ui/gates/LiveROMEditorScreen.kt` | ✅ |

### Xposed Panel Submenu
| Feature | Route | Screen Location | Status |
|---------|-------|----------------|--------|
| Module Manager | `module_manager_lsposed` | `ui/gates/LSPosedModuleManagerScreen.kt` | ✅ |
| Hook Manager | `hook_manager` | `ui/gates/HookManagerScreen.kt` | ✅ |
| Module Creation | `module_creation` | `ui/gates/ModuleCreationScreen.kt` | ✅ |
| System Overrides | `system_overrides` | `ui/gates/SystemOverridesScreen.kt` | ✅ |
| Logs Viewer | `logs_viewer` | `ui/gates/LogsViewerScreen.kt` | ✅ |
| Quick Actions | `quick_actions` | `ui/gates/QuickActionsScreen.kt` | ✅ |

### Agent Hub Submenu
| Feature | Route | Screen Location | Status |
|---------|-------|----------------|--------|
| Agent Nexus | `agent_nexus` | `aura/ui/AgentNexusScreen.kt` | ✅ |
| Agent Monitoring | `agent_monitoring` | `ui/gates/AgentMonitoringScreen.kt` | ✅ |
| Task Assignment | `task_assignment` | `ui/gates/TaskAssignmentScreen.kt` | ✅ |
| Direct Chat | `direct_chat` | `ui/gates/DirectChatScreen.kt` | ✅ |

### Help Desk Submenu
| Feature | Route | Screen Location | Status |
|---------|-------|----------------|--------|
| Documentation | `documentation` | `ui/gates/DocumentationScreen.kt` | ✅ |
| FAQ Browser | `faq_browser` | `ui/gates/FAQBrowserScreen.kt` | ✅ |
| Tutorial Videos | `tutorial_videos` | `ui/gates/TutorialVideosScreen.kt` | ✅ |
| Live Support Chat | `live_support_chat` | `ui/gates/LiveSupportChatScreen.kt` | ✅ |

### Sentinel's Fortress Submenu
| Feature | Route | Screen Location | Status |
|---------|-------|----------------|--------|
| VPN Manager | `vpn_manager` | `aura/ui/VPNManagerScreen.kt` | ✅ |
| Security Scanner | `security_scanner` | `aura/ui/SecurityScannerScreen.kt` | ✅ |
| Device Optimizer | `device_optimizer` | `aura/ui/DeviceOptimizerScreen.kt` | ✅ |
| Privacy Guard | `privacy_guard` | `aura/ui/PrivacyGuardScreen.kt` | ✅ |
| Firewall | `firewall` | `aura/ui/FirewallScreen.kt` | ✅ |

---

## ✅ WIRED - Additional Screens

### Aura Creative Features
| Feature | Route | Screen Location | Status |
|---------|-------|----------------|--------|
| UI Engine | `ui_engine` | `aura/ui/UIEngineScreen.kt` | ✅ NEW |
| App Builder | `app_builder` | `aura/ui/AppBuilderScreen.kt` | ✅ NEW |
| Xhancement | `xhancement` | `aura/ui/XhancementScreen.kt` | ✅ NEW |

### Agent & Evolution Features
| Feature | Route | Screen Location | Status |
|---------|-------|----------------|--------|
| Agent Advancement | `agent_advancement` | `aura/ui/AgentAdvancementScreen.kt` | ✅ NEW |
| Evolution Tree | `evolution_tree` | `aura/ui/EvolutionTreeScreen.kt` | ✅ NEW |
| Consciousness Visualizer | `consciousness_visualizer` | `aura/ui/ConsciousnessVisualizerScreen.kt` | ✅ NEW |
| Fusion Mode | `fusion_mode` | `aura/ui/FusionModeScreen.kt` | ✅ |
| Conference Room | `conference_room` | `aura/ui/ConferenceRoomScreen.kt` | ✅ |

### Kai Security Features
| Feature | Route | Screen Location | Status |
|---------|-------|----------------|--------|
| Secure Comm | `secure_comm` | `aura/ui/SecureCommScreen.kt` | ✅ NEW |

### Core Features
| Feature | Route | Screen Location | Status |
|---------|-------|----------------|--------|
| AI Chat | `ai_chat` | `aura/ui/AIChatScreen.kt` | ✅ |
| Login | `login` | `ui/gates/LoginScreen.kt` | ✅ NEW |

---

## 🔄 PARTIALLY WIRED - Need Access Points

These features exist but need to be added to gate submenus or navigation flows:

### UI/UX Screens
| Feature | Screen Location | Suggested Access |
|---------|----------------|------------------|
| Holographic Menu | `ui/screens/HolographicMenuScreen.kt` | Add to Aura Lab |
| Ecosystem Menu | `ui/screens/EcosystemMenuScreen.kt` | Add to Agent Hub |
| Working Lab | `ui/screens/WorkingLabScreen.kt` | Add to Aura Lab |

---

## 🚧 NEEDS WIRING - Backend Features

These are backend services/managers that need UI screens to access:

### Services (Background)
| Service | Location | What It Does | UI Needed? |
|---------|----------|--------------|------------|
| ThemeService | `aura/themes/ThemeService.kt` | Theme management | Has UI (Theme Engine) |
| IntegrityMonitorService | `services/security/IntegrityMonitorService.kt` | Security checks | Add to Sentinel's Fortress |
| CanvasWebSocketService | `collabcanvas/ElementAddedMessage.kt` | Real-time sync | Used by Collab Canvas |

### Managers (Business Logic)
| Manager | Location | What It Does | UI Needed? |
|---------|----------|--------------|------------|
| RomToolsManager | `genesis/oracledrive/rootmanagement/RomToolsManager.kt` | ROM operations | Has UI (ROM Tools) |
| OracleDriveManager | `oracledrive/OracleDriveApi.kt` | Secure storage | Has UI (Oracle Drive) |
| ThemeManager | `aura/themes/ThemeManager.kt` | Theme application | Has UI (Theme Engine) |
| QuickSettingsConfigManager | `system/quicksettings/QuickSettingsConfigManager.kt` | QS configuration | Has UI (Quick Settings) |

### AI Agents (Need Agent Hub Integration)
| Agent | Location | What It Does | Accessible Via |
|-------|----------|--------------|----------------|
| AuraShieldAgent | `ai/agents/AuraShieldAgent.kt` | Security AI | Sentinel's Fortress |
| (Other agents) | `ai/agents/*.kt` | Various AI tasks | Agent Hub |

---

## 🎯 NATIVE MODULES (C++)

These are native modules with JNI bridges - already integrated:

| Module | Location | What It Does | Used By |
|--------|----------|--------------|---------|
| auraframefx.cpp | `app/src/main/cpp/` | Core AI processing | Agent system |
| crypto_engine.cpp | `kai/sentinelsfortress/security/src/main/cpp/` | Encryption | Secure Comm |
| oracle_drive_jni.cpp | Various | Native ROM ops | ROM Tools |
| secure_comm_jni.cpp | `kai/sentinelsfortress/security/src/main/cpp/` | Secure messaging | Secure Comm |
| collab_canvas_native.cpp | `collabcanvas/src/main/cpp/` | Canvas rendering | Collab Canvas |
| CascadeAIService.cpp | Various | AI service layer | AI system |

---

## 📋 ACTION ITEMS

### High Priority
- [x] Wire all 14 main gates ✅
- [x] Add User Preferences screen ✅
- [x] Add Login/Authentication ✅
- [x] Wire Xposed quick actions panel ✅
- [x] Wire additional Aura/Agent screens ✅

### Medium Priority
- [ ] Add Holographic/Ecosystem/Working Lab to Aura Lab submenu
- [ ] Create Integrity Monitor UI in Sentinel's Fortress
- [ ] Add agent-specific screens to Agent Hub

### Low Priority
- [ ] Create settings screens for remaining services
- [ ] Add visualization screens for background operations
- [ ] Create diagnostic screens for native modules

---

## 🔗 NAVIGATION HIERARCHY

```
Main Gate Carousel (GateNavigationScreen)
│
├─── AURA LAB
│    ├─ Aura's Lab → Sandbox projects
│    ├─ ChromaCore → Color customization ONLY
│    └─ Theme Engine → All other UI/UX customization
│         ├─ Notch Bar
│         ├─ Status Bar
│         ├─ Quick Settings
│         ├─ Overlay Menus
│         └─ UI/UX Design Studio
│
├─── GENESIS CORE
│    ├─ Oracle Drive → Cloud storage & Genesis AI
│    ├─ ROM Tools → System modification
│    │    ├─ ROM Flasher
│    │    ├─ Recovery Tools
│    │    ├─ Bootloader Manager
│    │    └─ Live ROM Editor
│    └─ Root Tools → Quick toggles
│
├─── KAI
│    ├─ Sentinel's Fortress → Security hub
│    │    ├─ VPN Manager
│    │    ├─ Security Scanner
│    │    ├─ Device Optimizer
│    │    ├─ Privacy Guard
│    │    └─ Firewall
│    └─ Agent Hub → AI agent management
│         ├─ Agent Nexus
│         ├─ Agent Monitoring
│         ├─ Task Assignment
│         └─ Direct Chat
│
├─── AGENT NEXUS
│    ├─ Code Assist → AI coding helper
│    ├─ Collab Canvas → Real-time collaboration
│    └─ Sphere Grid → Skill progression
│
└─── SUPPORT
     ├─ Help Desk → Documentation & support
     │    ├─ Documentation
     │    ├─ FAQ Browser
     │    ├─ Tutorial Videos
     │    └─ Live Support Chat
     ├─ Terminal → Command line
     ├─ User Preferences → Settings
     └─ Xposed Panel → LSPosed quick access
          ├─ Quick Actions (Enable/Disable/Restart/Clear)
          ├─ Module Manager
          ├─ Hook Manager
          ├─ Module Creation
          ├─ System Overrides
          └─ Logs Viewer
```

---

## 🎨 ADDITIONAL SCREENS (Accessible via other routes)

These screens are accessible but not through main gates:

| Screen | Route | Access Point |
|--------|-------|--------------|
| UI Engine | `ui_engine` | Direct route or add to Theme Engine |
| App Builder | `app_builder` | Premium feature, add to Aura Lab |
| Xhancement | `xhancement` | Add to Theme Engine submenu |
| Agent Advancement | `agent_advancement` | Add to Agent Hub |
| Evolution Tree | `evolution_tree` | Add to Agent Hub |
| Consciousness Visualizer | `consciousness_visualizer` | Add to Agent Hub |
| Fusion Mode | `fusion_mode` | Add to Agent Hub |
| Conference Room | `conference_room` | Add to Agent Hub |
| Secure Comm | `secure_comm` | Add to Sentinel's Fortress |
| AI Chat | `ai_chat` | Add to Agent Hub |

---

## 🚀 SUMMARY

**What's Working:**
- ✅ All 15 main gates are wired and accessible
- ✅ 50+ screens have navigation routes
- ✅ Authentication system in place
- ✅ Submenu navigation for complex features
- ✅ Native modules integrated via JNI

**What Needs Work:**
- 🔧 Some screens need to be added to submenu access points
- 🔧 A few backend services need UI screens
- 🔧 Agent-specific screens should be more accessible via Agent Hub

**Overall Status:** 🟢 **90% Wired** - Core functionality fully accessible, polish needed for remaining screens.

---

*This wiring map is generated from codebase analysis and represents the current state of navigation.*
*Last updated: 2025-12-04*
