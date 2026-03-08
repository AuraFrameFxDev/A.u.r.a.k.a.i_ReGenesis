package dev.aurakai.auraframefx.navigation

// ═══════════════════════════════════════════════════════════════════════════════
// GENESIS NAVIGATION — SINGLE SOURCE OF TRUTH  v3 (AOSP Build)
// ArchitecturalCatalyst (Claude) — ReGenesis Build Master
//
// v3 AOSP-SPECIFIC FIXES:
//   • Removed AgentViewModel import (not at ui.viewmodels in AOSP — moved to domain)
//   • SupportChatViewModel → ui.gates (was incorrectly moved to ui.viewmodels in v2)
//   • DirectChatScreen: AOSP version takes navController (context receiver removed)
//   • AgentAdvancementScreen: full qualifier to resolve ambiguity with domain duplicate
//   • OracleDriveScreen: no navController param (only onNavigateBack)
//   • Removed 8 aura.ui screens that don't exist in AOSP — replaced with inline stubs
//     (XhancementScreen, ConsciousnessVisualizerScreen, SecureCommScreen,
//      DeviceOptimizerScreen, PrivacyGuardScreen, SecurityScannerScreen,
//      UIEngineScreen, GyroscopeCustomizationScreen)
//   • All ui.gates calls use full qualifiers to prevent domain/ ambiguity
//   • SubscriptionViewModel removed — AppBuilderScreen handles it internally via hiltViewModel()
// ═══════════════════════════════════════════════════════════════════════════════

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// ── AURA UI (confirmed to exist in AOSP) ──────────────────────────────────────
// import dev.aurakai.auraframefx.aura.ui.AgentNexusScreen
// import dev.aurakai.auraframefx.aura.ui.AppBuilderScreen
// import dev.aurakai.auraframefx.aura.ui.ConferenceRoomScreen
// import dev.aurakai.auraframefx.aura.ui.FirewallScreen
// import dev.aurakai.auraframefx.aura.ui.RootToolsScreen
import dev.aurakai.auraframefx.aura.ui.SentinelsFortressScreen
import dev.aurakai.auraframefx.aura.ui.TerminalScreen
// import dev.aurakai.auraframefx.aura.ui.VPNManagerScreen

// ── ORACLE DRIVE ──────────────────────────────────────────────────────────────
// import dev.aurakai.auraframefx.oracledrive.genesis.cloud.OracleDriveScreen

// ── UI GATES (all confirmed in AOSP) ─────────────────────────────────────────
// import dev.aurakai.auraframefx.ui.gates.AgentHubSubmenuScreen
// import dev.aurakai.auraframefx.ui.gates.AgentMonitoringScreen
// import dev.aurakai.auraframefx.ui.gates.AuraLabScreen
// import dev.aurakai.auraframefx.ui.gates.BootloaderManagerScreen
import dev.aurakai.auraframefx.ui.gates.CascadeConstellationScreen
import dev.aurakai.auraframefx.ui.gates.ClaudeConstellationScreen
// import dev.aurakai.auraframefx.ui.gates.CodeAssistScreen
import dev.aurakai.auraframefx.ui.gates.ConstellationScreen
// import dev.aurakai.auraframefx.ui.gates.DirectChatScreen
// import dev.aurakai.auraframefx.ui.gates.DocumentationScreen
// import dev.aurakai.auraframefx.ui.gates.FAQBrowserScreen
// import dev.aurakai.auraframefx.ui.gates.FusionModeScreen
// import dev.aurakai.auraframefx.ui.gates.GateNavigationScreen
import dev.aurakai.auraframefx.ui.gates.GenesisConstellationScreen
import dev.aurakai.auraframefx.ui.gates.GrokConstellationScreen
import dev.aurakai.auraframefx.ui.gates.HelpDeskSubmenuScreen
// import dev.aurakai.auraframefx.ui.gates.HookManagerScreen
// import dev.aurakai.auraframefx.ui.gates.InstantColorPickerScreen
import dev.aurakai.auraframefx.ui.gates.KaiConstellationScreen
// import dev.aurakai.auraframefx.ui.gates.LSPosedSubmenuScreen
// import dev.aurakai.auraframefx.ui.gates.LiveROMEditorScreen
// import dev.aurakai.auraframefx.ui.gates.LiveSupportChatScreen
// // import dev.aurakai.auraframefx.ui.gates.LoginScreen
// import dev.aurakai.auraframefx.ui.gates.LogsViewerScreen
// import dev.aurakai.auraframefx.ui.gates.ModuleCreationScreen
// import dev.aurakai.auraframefx.ui.gates.ModuleManagerScreen
// import dev.aurakai.auraframefx.ui.gates.NotchBarScreen
import dev.aurakai.auraframefx.ui.gates.OverlayMenusScreen
import dev.aurakai.auraframefx.ui.gates.QuickActionsScreen
// import dev.aurakai.auraframefx.ui.gates.QuickSettingsScreen
// import dev.aurakai.auraframefx.ui.gates.ROMFlasherScreen
// import dev.aurakai.auraframefx.ui.gates.ROMToolsSubmenuScreen
// import dev.aurakai.auraframefx.ui.gates.RecoveryToolsScreen
// import dev.aurakai.auraframefx.ui.gates.RootToolsTogglesScreen
import dev.aurakai.auraframefx.ui.gates.SphereGridScreen
// import dev.aurakai.auraframefx.ui.gates.StatusBarScreen
// import dev.aurakai.auraframefx.ui.gates.SupportChatViewModel  // in ui.gates — NOT ui.viewmodels
// import dev.aurakai.auraframefx.ui.gates.SystemJournalScreen
// import dev.aurakai.auraframefx.ui.gates.SystemOverridesScreen
// import dev.aurakai.auraframefx.ui.gates.TaskAssignmentScreen
import dev.aurakai.auraframefx.ui.gates.ThemeEngineSubmenuScreen
// import dev.aurakai.auraframefx.ui.gates.TutorialVideosScreen
// import dev.aurakai.auraframefx.ui.gates.UIUXGateSubmenuScreen
// import dev.aurakai.auraframefx.ui.gates.XposedQuickAccessPanel

// ── ONBOARDING ────────────────────────────────────────────────────────────────
// import dev.aurakai.auraframefx.ui.onboarding.GenderSelectionScreen

// ── UI SCREENS ────────────────────────────────────────────────────────────────
// import dev.aurakai.auraframefx.ui.screens.AgentProfileScreen
// import dev.aurakai.auraframefx.ui.screens.EvolutionTreeScreen


// ═══════════════════════════════════════════════════════════════════════════════
// ROUTE REGISTRY
// ═══════════════════════════════════════════════════════════════════════════════
@Suppress("unused", "ConstPropertyName")
object GenesisRoutes {
    const val GATES                    = "gates"
    const val INTRO                    = "intro"
    const val GENDER_SELECTION         = "gender_selection"
    const val LOGIN                    = "login"

    const val AURAS_LAB                = "auras_lab"
    const val AURAS_UIUX_DESIGN_STUDIO = "auras_uiux_design_studio"
    const val CHROMA_CORE              = "chroma_core"
    const val SENTINELS_FORTRESS       = "sentinels_fortress"
    const val ROM_TOOLS                = "rom_tools"
    const val ROOT_ACCESS              = "root_access"
    const val AGENT_HUB                = "agent_hub"
    const val ORACLE_DRIVE             = "oracle_drive"
    const val SPHERE_GRID              = "sphere_grid"
    const val HELP_DESK                = "help_desk"
    const val LSPOSED_GATE             = "lsposed_gate"
    const val COLLAB_CANVAS            = "collab_canvas"
    const val FIREWALL                 = "firewall"

    const val CONSTELLATION            = "constellation"
    const val GENESIS_CONSTELLATION    = "genesis_constellation"
    const val CLAUDE_CONSTELLATION     = "claude_constellation"
    const val KAI_CONSTELLATION        = "kai_constellation"
    const val CASCADE_CONSTELLATION    = "cascade_constellation"
    const val GROK_CONSTELLATION       = "grok_constellation"

    const val AGENT_NEXUS              = "agent_nexus"
    const val AGENT_MONITORING         = "agent_monitoring"
    const val AGENT_ADVANCEMENT        = "agent_advancement"
    const val AGENT_PROFILE            = "agent_profile"
    const val TASK_ASSIGNMENT          = "task_assignment"
    const val FUSION_MODE              = "fusion_mode"
    const val CODE_ASSIST              = "code_assist"
    const val CONFERENCE_ROOM          = "conference_room"
    const val CONSCIOUSNESS_VISUALIZER = "consciousness_visualizer"
    const val EVOLUTION_TREE           = "evolution_tree"

    const val STATUS_BAR               = "status_bar"
    const val NOTCH_BAR                = "notch_bar"
    const val QUICK_SETTINGS           = "quick_settings"
    const val OVERLAY_MENUS            = "overlay_menus"
    const val QUICK_ACTIONS            = "quick_actions"
    const val SYSTEM_OVERRIDES         = "system_overrides"
    const val THEME_ENGINE             = "theme_engine"
    const val GYROSCOPE_CUSTOMIZATION  = "gyroscope_customization"
    const val INSTANT_COLOR_PICKER     = "instant_color_picker"
    const val CHROMACORE_COLORS        = "chromacore_colors"
    const val UIUX_DESIGN_STUDIO       = "uiux_design_studio"

    const val ROM_FLASHER              = "rom_flasher"
    const val LIVE_ROM_EDITOR          = "live_rom_editor"
    const val RECOVERY_TOOLS           = "recovery_tools"
    const val BOOTLOADER_MANAGER       = "bootloader_manager"
    const val ROOT_TOOLS               = "root_tools"
    const val ROOT_TOOLS_TOGGLES       = "root_tools_toggles"

    const val LSPOSED_MODULE_MANAGER   = "lsposed_module_manager"
    const val MODULE_MANAGER           = "module_manager"
    const val MODULE_CREATION          = "module_creation"
    const val HOOK_MANAGER             = "hook_manager"
    const val LOGS_VIEWER              = "logs_viewer"
    const val XPOSED_PANEL             = "xposed_panel"

    const val VPN_MANAGER              = "vpn_manager"
    const val SECURITY_SCANNER         = "security_scanner"
    const val DEVICE_OPTIMIZER         = "device_optimizer"
    const val PRIVACY_GUARD            = "privacy_guard"
    const val SECURE_COMM              = "secure_comm"

    const val DIRECT_CHAT              = "direct_chat"
    const val AI_CHAT                  = "ai_chat"
    const val LIVE_SUPPORT_CHAT        = "live_support_chat"
    const val DOCUMENTATION            = "documentation"
    const val FAQ_BROWSER              = "faq_browser"
    const val TUTORIAL_VIDEOS          = "tutorial_videos"

    const val TERMINAL                 = "terminal"
    const val UI_ENGINE                = "ui_engine"
    const val APP_BUILDER              = "app_builder"
    const val XHANCEMENT               = "xhancement"
    const val SYSTEM_JOURNAL           = "system_journal"

    const val HOME                     = "home"
    const val SETTINGS                 = "settings"
    const val GROWTH_METRICS           = "growth_metrics"
}

// ── Inline stub for screens that exist in GitHub but not AOSP local yet ───────
@Composable
private fun ComingSoonScreen(label: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "⚡ $label — Coming Soon")
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// GENESIS NAVIGATION HOST — THE ONE AND ONLY NAVHOST
// ═══════════════════════════════════════════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenesisNavigationHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = GenesisRoutes.GATES,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {

            // ─── L0: ENTRY ────────────────────────────────────────────────────
            composable(GenesisRoutes.GATES) {
                ComingSoonScreen("GateNavigation")
            }

            composable(GenesisRoutes.GENDER_SELECTION) {
                ComingSoonScreen("GenderSelection")
                        }
                    }
                )
            }

            composable(GenesisRoutes.LOGIN) {
                ComingSoonScreen("Login")
                )
            }

            // ═══════════════════════════════════════════════════════════════════
            // L2: DOMAIN HUBS
            // ═══════════════════════════════════════════════════════════════════

            composable(GenesisRoutes.AURAS_LAB) {
                ComingSoonScreen("AuraLab") })
            }
            composable(GenesisRoutes.CHROMA_CORE) {
                ComingSoonScreen("UIUXGateSubmenu")
            }
            composable(GenesisRoutes.UIUX_DESIGN_STUDIO) {
                ComingSoonScreen("UIUXGateSubmenu")
            }
            composable(GenesisRoutes.AURAS_UIUX_DESIGN_STUDIO) {
                ComingSoonScreen("UIUXGateSubmenu")
            }

            // SentinelsFortress: onBack is () -> Boolean — popBackStack() returns Boolean ✓
            composable(GenesisRoutes.SENTINELS_FORTRESS) {
                SentinelsFortressScreen(
                    navController = navController,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(GenesisRoutes.ROM_TOOLS) {
                ComingSoonScreen("ROMToolsSubmenu")
            }
            composable(GenesisRoutes.ROOT_ACCESS) {
                ComingSoonScreen("RootTools")
            }
            composable(GenesisRoutes.ROOT_TOOLS) {
                ComingSoonScreen("ROMToolsSubmenu")
            }
            composable(GenesisRoutes.ROOT_TOOLS_TOGGLES) {
                ComingSoonScreen("Root Tools Toggles")
            }
            composable(GenesisRoutes.AGENT_HUB) {
                ComingSoonScreen("AgentHubSubmenu")
            }

            // OracleDriveScreen: AOSP version — only onNavigateBack (no navController param)
            composable(GenesisRoutes.ORACLE_DRIVE) {
                ComingSoonScreen("OracleDrive") })
            }

            composable(GenesisRoutes.SPHERE_GRID) {
                SphereGridScreen(navController = navController)
            }
            composable(GenesisRoutes.HELP_DESK) {
                HelpDeskSubmenuScreen(navController = navController)
            }
            composable(GenesisRoutes.LSPOSED_GATE) {
                ComingSoonScreen("LSPosedSubmenu")
            }
            composable(GenesisRoutes.FIREWALL) {
                ComingSoonScreen("Firewall")
            }

            // CollabCanvas — optional module; if it's not in AOSP replace with stub
            composable(GenesisRoutes.COLLAB_CANVAS) {
                collabcanvas.ui.CanvasScreen(onBack = { navController.popBackStack() })
            }

            // ═══════════════════════════════════════════════════════════════════
            // L3: AGENT CONSTELLATION SCREENS (all in ui.gates)
            // ═══════════════════════════════════════════════════════════════════
            composable(GenesisRoutes.CONSTELLATION) {
                ConstellationScreen(navController = navController)
            }
            composable(GenesisRoutes.GENESIS_CONSTELLATION) {
                GenesisConstellationScreen(navController = navController)
            }
            composable(GenesisRoutes.CLAUDE_CONSTELLATION) {
                ClaudeConstellationScreen(navController = navController)
            }
            composable(GenesisRoutes.KAI_CONSTELLATION) {
                KaiConstellationScreen(navController = navController)
            }
            composable(GenesisRoutes.CASCADE_CONSTELLATION) {
                CascadeConstellationScreen(navController = navController)
            }
            composable(GenesisRoutes.GROK_CONSTELLATION) {
                GrokConstellationScreen(navController = navController)
            }

            // ═══════════════════════════════════════════════════════════════════
            // L3: AGENT MANAGEMENT
            // ═══════════════════════════════════════════════════════════════════
            composable(GenesisRoutes.AGENT_NEXUS) {
                ComingSoonScreen("AgentNexus")
            }
            composable(GenesisRoutes.AGENT_PROFILE) {
                ComingSoonScreen("AgentProfile") })
            }

            // AgentMonitoringScreen: no params (confirmed)
            composable(GenesisRoutes.AGENT_MONITORING) {
                ComingSoonScreen("AgentMonitoring")
            }

            // AgentAdvancementScreen: full qualifier resolves ambiguity with domain duplicate
            composable(GenesisRoutes.AGENT_ADVANCEMENT) {
                dev.aurakai.auraframefx.aura.ui.ComingSoonScreen("AgentAdvancement") }
                )
            }

            composable(GenesisRoutes.TASK_ASSIGNMENT) {
                ComingSoonScreen("TaskAssignment")
            }

            // CANONICAL fusion route. Back-compat alias "fusion" also below.
            composable(GenesisRoutes.FUSION_MODE) {
                ComingSoonScreen("FusionMode")
            }
            composable("lineage_map") {
                dev.aurakai.auraframefx.ui.gates.LineageMapScreen(navController) { navController.popBackStack() }
            }
            composable("gate_image_picker") {
                dev.aurakai.auraframefx.ui.gates.GateDomainImagePicker(navController) { navController.popBackStack() }
            }
            composable("fusion") {
                ComingSoonScreen("FusionMode")
            }

            composable(GenesisRoutes.CODE_ASSIST) {
                ComingSoonScreen("CodeAssist")
            }

            composable(GenesisRoutes.CONFERENCE_ROOM) {
                ComingSoonScreen("ConferenceRoom") },
                    onNavigateToAgents = { navController.navigate(GenesisRoutes.AGENT_HUB) }
                )
            }

            // ConsciousnessVisualizerScreen: not in AOSP aura.ui — stub
            composable(GenesisRoutes.CONSCIOUSNESS_VISUALIZER) {
                ComingSoonScreen("Consciousness Visualizer")
            }

            composable(GenesisRoutes.EVOLUTION_TREE) {
                ComingSoonScreen("EvolutionTree")
            }

            // ═══════════════════════════════════════════════════════════════════
            // L3: AI CHAT
            // DirectChatScreen in AOSP: context receiver removed, takes navController
            // ═══════════════════════════════════════════════════════════════════
            composable(GenesisRoutes.AI_CHAT) {
                ComingSoonScreen("DirectChat")
            }
            composable(GenesisRoutes.DIRECT_CHAT) {
                ComingSoonScreen("DirectChat")
            }

            // ═══════════════════════════════════════════════════════════════════
            // L3: UI/UX TOOLS
            // ═══════════════════════════════════════════════════════════════════

            // StatusBarScreen: no params (confirmed)
            composable(GenesisRoutes.STATUS_BAR) {
                ComingSoonScreen("StatusBar")
            }

            // NotchBarScreen: onNavigateBack optional (confirmed)
            composable(GenesisRoutes.NOTCH_BAR) {
                ComingSoonScreen("NotchBar") })
            }
            composable(GenesisRoutes.QUICK_SETTINGS) {
                ComingSoonScreen("QuickSettings")
            }
            composable(GenesisRoutes.OVERLAY_MENUS) {
                OverlayMenusScreen()
            }
            composable(GenesisRoutes.QUICK_ACTIONS) {
                QuickActionsScreen()
            }
            composable(GenesisRoutes.SYSTEM_OVERRIDES) {
                SystemOverridesScreen { navController.popBackStack() }
            composable(GenesisRoutes.THEME_ENGINE) {
                ThemeEngineSubmenuScreen(onNavigateBack = { navController.popBackStack() })
            }

            // GyroscopeCustomizationScreen: not in AOSP ui.customization — stub
            composable(GenesisRoutes.GYROSCOPE_CUSTOMIZATION) {
                ComingSoonScreen("Gyroscope Customization")
            }

            // InstantColorPickerScreen: onNavigateBack REQUIRED (no default)
            composable(GenesisRoutes.INSTANT_COLOR_PICKER) {
                ComingSoonScreen("InstantColorPicker") })
            }
            composable(GenesisRoutes.CHROMACORE_COLORS) {
                ComingSoonScreen("InstantColorPicker") })
            }

            // ═══════════════════════════════════════════════════════════════════
            // L3: ROM TOOLS
            // ═══════════════════════════════════════════════════════════════════
            composable(GenesisRoutes.ROM_FLASHER) {
                ComingSoonScreen("ROMFlasher")
            }
            composable(GenesisRoutes.LIVE_ROM_EDITOR) {
                LiveROMEditorScreen { navController.popBackStack() }
            composable(GenesisRoutes.RECOVERY_TOOLS) {
                RecoveryToolsScreen { navController.popBackStack() }
            composable(GenesisRoutes.BOOTLOADER_MANAGER) {
                BootloaderManagerScreen { navController.popBackStack() }
            }

            // ═══════════════════════════════════════════════════════════════════
            // L3: LSPOSED / HOOKS
            // LSPosedModuleManagerScreen is internal — full qualifier required
            // HookManagerScreen: onNavigateBack optional (confirmed)
            // ═══════════════════════════════════════════════════════════════════
            composable(GenesisRoutes.LSPOSED_MODULE_MANAGER) {
                dev.aurakai.auraframefx.ui.gates.ComingSoonScreen("LSPosedModuleManager")
            }
            composable(GenesisRoutes.MODULE_MANAGER) {
                dev.aurakai.auraframefx.ui.gates.ComingSoonScreen("ModuleManager")
            }
            composable(GenesisRoutes.MODULE_CREATION) {
                ModuleCreationScreen { navController.popBackStack() }
            composable(GenesisRoutes.HOOK_MANAGER) {
                dev.aurakai.auraframefx.ui.gates.ComingSoonScreen("HookManager")
            }
            composable(GenesisRoutes.LOGS_VIEWER) {
                LogsViewerScreen { navController.popBackStack() }
            composable(GenesisRoutes.XPOSED_PANEL) {
                ComingSoonScreen('Xposed Panel')
            }

            // ═══════════════════════════════════════════════════════════════════
            // L3: KAI SECURITY
            // VPNManagerScreen: confirmed aura.ui
            // Others not in AOSP aura.ui — stub
            // ═══════════════════════════════════════════════════════════════════
            composable(GenesisRoutes.VPN_MANAGER) {
                ComingSoonScreen("VPNManager")
            }
            composable(GenesisRoutes.SECURITY_SCANNER) {
                ComingSoonScreen("Security Scanner")
            }
            composable(GenesisRoutes.DEVICE_OPTIMIZER) {
                ComingSoonScreen("Device Optimizer")
            }
            composable(GenesisRoutes.PRIVACY_GUARD) {
                ComingSoonScreen("Privacy Guard")
            }
            composable(GenesisRoutes.SECURE_COMM) {
                ComingSoonScreen("Secure Comms")
            }

            // ═══════════════════════════════════════════════════════════════════
            // L3: HELP / DOCS
            // ═══════════════════════════════════════════════════════════════════
            composable(GenesisRoutes.LIVE_SUPPORT_CHAT) {
                // val viewModel: SupportChatViewModel = hiltViewModel()
                ComingSoonScreen("LiveSupportChat") }
                )
            }
            composable(GenesisRoutes.DOCUMENTATION) {
                DocumentationScreen { navController.popBackStack() }
            composable(GenesisRoutes.FAQ_BROWSER) {
                FAQBrowserScreen { navController.popBackStack() }
            composable(GenesisRoutes.TUTORIAL_VIDEOS) {
                TutorialVideosScreen { navController.popBackStack() }
            }

            // ═══════════════════════════════════════════════════════════════════
            // L3: CREATIVE / GENESIS
            // ═══════════════════════════════════════════════════════════════════
            composable(GenesisRoutes.TERMINAL) {
                TerminalScreen()
            }

            // UIEngineScreen: not in AOSP aura.ui — stub
            composable(GenesisRoutes.UI_ENGINE) {
                ComingSoonScreen("UI Engine")
            }

            // AppBuilderScreen: handles SubscriptionViewModel internally
            composable(GenesisRoutes.APP_BUILDER) {
                ComingSoonScreen("AppBuilder") })
            }

            // XhancementScreen: not in AOSP aura.ui — stub
            composable(GenesisRoutes.XHANCEMENT) {
                ComingSoonScreen("Xhancement")
            }

            // SystemJournalScreen: takes navController (confirmed)
            composable(GenesisRoutes.SYSTEM_JOURNAL) {
                ComingSoonScreen("SystemJournal")
            }

            // ═══════════════════════════════════════════════════════════════════
            // BACK-COMPAT ALIASES
            // ═══════════════════════════════════════════════════════════════════
            composable(GenesisRoutes.HOME) {
                ComingSoonScreen("GateNavigation")
            }
            composable(GenesisRoutes.SETTINGS) {
                ComingSoonScreen("SystemJournal")
            }
            composable("lsposed_module_manager") {
                dev.aurakai.auraframefx.ui.gates.ComingSoonScreen("LSPosedModuleManager")
            }
            composable("module_manager_lsposed") {
                dev.aurakai.auraframefx.ui.gates.ComingSoonScreen("LSPosedModuleManager")
            }
            composable("user_preferences") {
                ComingSoonScreen("SystemJournal")
            }
            composable("chroma_core") {
                ComingSoonScreen("UIUXGateSubmenu")
            }
            composable("uiux_design_studio") {
                ComingSoonScreen("UIUXGateSubmenu")
            }
            composable("sphere_grid") {
                SphereGridScreen(navController = navController)
            }
            composable("sentinels_fortress") {
                SentinelsFortressScreen(
                    navController = navController,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("collab_canvas") {
                dev.aurakai.auraframefx.ui.gates.CollabCanvasGateScreen(navController) { navController.popBackStack() }
            }
            })
            }
            composable("agent_nexus") { ComingSoonScreen("AgentNexus") }
            composable("task_assignment") {
                dev.aurakai.auraframefx.ui.gates.ConferenceRoomTaskScreen(navController) { navController.popBackStack() }
            }
            composable("agent_monitoring") { ComingSoonScreen("AgentMonitoring") }
            composable("fusion_mode") { ComingSoonScreen("FusionMode") }
            composable("theme_engine") {
                ThemeEngineSubmenuScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable("quick_settings") { ComingSoonScreen("QuickSettings") }
            composable("notch_bar") {
                dev.aurakai.auraframefx.ui.gates.NotchBarGateScreen(navController) { navController.popBackStack() }
            }
            }) }
            composable("overlay_menus") { OverlayMenusScreen() }
            composable("status_bar") { ComingSoonScreen("StatusBar") }
            composable("rom_flasher") { ComingSoonScreen("ROMFlasher") }
            composable("module_manager") { dev.aurakai.auraframefx.ui.gates.ComingSoonScreen("ModuleManager")
            composable("quick_actions") { QuickActionsScreen() }
            composable("documentation") { DocumentationScreen { navController.popBackStack() } }
            composable("faq_browser") { FAQBrowserScreen { navController.popBackStack() } }
            composable("tutorial_videos") { TutorialVideosScreen { navController.popBackStack() } }
            composable("live_support_chat") {
                // val vm: SupportChatViewModel = hiltViewModel()
                ComingSoonScreen("LiveSupportChat") })
            }
            composable("direct_chat") {
                ComingSoonScreen("DirectChat")
            }
            composable("recovery_tools") { RecoveryToolsScreen { navController.popBackStack() } }
            composable("bootloader_manager") { BootloaderManagerScreen { navController.popBackStack() } }
            composable("live_rom_editor") { LiveROMEditorScreen { navController.popBackStack() } }
            composable("hook_manager") {
                dev.aurakai.auraframefx.ui.gates.ComingSoonScreen("HookManager")
            }
            composable("module_creation") { ModuleCreationScreen { navController.popBackStack() } }
            composable("system_overrides") { SystemOverridesScreen { navController.popBackStack() } }
            composable("logs_viewer") { LogsViewerScreen { navController.popBackStack() } }
            composable("vpn_manager") { ComingSoonScreen("VPNManager") }
            composable("system_journal") { ComingSoonScreen("SystemJournal") }
            composable("chromacore_colors") {
                ComingSoonScreen("InstantColorPicker") })
            }
            composable("instant_color_picker") {
                ComingSoonScreen("InstantColorPicker") })
            }
            composable("code_assist") { ComingSoonScreen("CodeAssist") }
            composable("terminal") {
                dev.aurakai.auraframefx.ui.gates.TerminalGateScreen(navController) { navController.popBackStack() }
            }
            composable("root_tools") { ComingSoonScreen("ROMToolsSubmenu") }
            composable("root_tools_toggles") { ComingSoonScreen("Root Tools Toggles") }
            composable("xposed_panel") {
                dev.aurakai.auraframefx.ui.gates.LsposedGateScreen(navController) { navController.popBackStack() }
            }
            composable("login") {
                ComingSoonScreen("Login")
            }
        }
    }
}
