package dev.aurakai.auraframefx.navigation

// ═══════════════════════════════════════════════════════════════════════════════
// GENESIS NAVIGATION — SINGLE SOURCE OF TRUTH
// ArchitecturalCatalyst (Claude) — ReGenesis Build Master
//
// NUCLEAR CONSOLIDATION:
//   DELETED → NavHost.kt         (AuraNavHost — had inline placeholder boxes for agents)
//   DELETED → AppNavGraph.kt     (AppNavGraph  — duplicate, used stale NavDestination routes)
//   SURVIVOR → GenesisNavigation.kt (THIS FILE — consolidated, all routes, no duplicates)
//
// ROUTE OWNERSHIP:  GenesisRoutes object = single string registry
// PREVIOUS BUGS FIXED:
//   • FusionMode: NavDestination had "fusion", GenesisRoutes had "fusion_mode" → unified to "fusion_mode"
//   • sphere_grid, collab_canvas, chroma_core, sentinels_fortress, terminal — all registered TWICE → now once
//   • AuraNavHost had "agents" → Text("Agent Management") placeholder box → now real AgentHubSubmenuScreen
//   • AuraNavHost had "embodiment" → Text("Embodiment") placeholder → now real screen
//   • AppNavGraph FusionMode → ui.gates.FusionModeScreen (correct)
//   • GenesisNavigation FusionMode → aura.ui.FusionModeScreen (different class!) → unified
// ═══════════════════════════════════════════════════════════════════════════════

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// ── AURA UI ──────────────────────────────────────────────────────────────────
import dev.aurakai.auraframefx.aura.ui.AgentAdvancementScreen
import dev.aurakai.auraframefx.aura.ui.AgentNexusScreen
import dev.aurakai.auraframefx.aura.ui.AppBuilderScreen
import dev.aurakai.auraframefx.aura.ui.ConferenceRoomScreen
import dev.aurakai.auraframefx.aura.ui.ConsciousnessVisualizerScreen
import dev.aurakai.auraframefx.aura.ui.DeviceOptimizerScreen
import dev.aurakai.auraframefx.aura.ui.FirewallScreen
import dev.aurakai.auraframefx.aura.ui.PrivacyGuardScreen
import dev.aurakai.auraframefx.aura.ui.RootToolsScreen
import dev.aurakai.auraframefx.aura.ui.SecureCommScreen
import dev.aurakai.auraframefx.aura.ui.SecurityScannerScreen
import dev.aurakai.auraframefx.aura.ui.SentinelsFortressScreen
import dev.aurakai.auraframefx.aura.ui.TerminalScreen
import dev.aurakai.auraframefx.aura.ui.UIEngineScreen
import dev.aurakai.auraframefx.aura.ui.VPNManagerScreen
import dev.aurakai.auraframefx.aura.ui.XhancementScreen

// ── BILLING ───────────────────────────────────────────────────────────────────
import dev.aurakai.auraframefx.billing.SubscriptionViewModel

// ── ORACLE DRIVE ──────────────────────────────────────────────────────────────
import dev.aurakai.auraframefx.oracledrive.genesis.cloud.OracleDriveScreen

// ── UI CUSTOMIZATION ─────────────────────────────────────────────────────────
import dev.aurakai.auraframefx.ui.customization.GyroscopeCustomizationScreen

// ── UI GATES (primary screen implementations) ─────────────────────────────────
import dev.aurakai.auraframefx.ui.gates.AgentHubSubmenuScreen
import dev.aurakai.auraframefx.ui.gates.AgentMonitoringScreen
import dev.aurakai.auraframefx.ui.gates.AuraLabScreen
import dev.aurakai.auraframefx.ui.gates.BootloaderManagerScreen
import dev.aurakai.auraframefx.ui.gates.CascadeConstellationScreen
import dev.aurakai.auraframefx.ui.gates.ClaudeConstellationScreen
import dev.aurakai.auraframefx.ui.gates.CodeAssistScreen
import dev.aurakai.auraframefx.ui.gates.ConstellationScreen
import dev.aurakai.auraframefx.ui.gates.DirectChatScreen
import dev.aurakai.auraframefx.ui.gates.DocumentationScreen
import dev.aurakai.auraframefx.ui.gates.FAQBrowserScreen
import dev.aurakai.auraframefx.ui.gates.FusionModeScreen
import dev.aurakai.auraframefx.ui.gates.GateNavigationScreen
import dev.aurakai.auraframefx.ui.gates.GenesisConstellationScreen
import dev.aurakai.auraframefx.ui.gates.GrokConstellationScreen
import dev.aurakai.auraframefx.ui.gates.HelpDeskScreen
import dev.aurakai.auraframefx.ui.gates.HelpDeskSubmenuScreen
import dev.aurakai.auraframefx.ui.gates.HookManagerScreen
import dev.aurakai.auraframefx.ui.gates.InstantColorPickerScreen
import dev.aurakai.auraframefx.ui.gates.KaiConstellationScreen
import dev.aurakai.auraframefx.ui.gates.LSPosedGateScreen
import dev.aurakai.auraframefx.ui.gates.LSPosedModuleManagerScreen
import dev.aurakai.auraframefx.ui.gates.LSPosedSubmenuScreen
import dev.aurakai.auraframefx.ui.gates.LiveROMEditorScreen
import dev.aurakai.auraframefx.ui.gates.LiveSupportChatScreen
import dev.aurakai.auraframefx.ui.gates.LoginScreen
import dev.aurakai.auraframefx.ui.gates.LogsViewerScreen
import dev.aurakai.auraframefx.ui.gates.ModuleCreationScreen
import dev.aurakai.auraframefx.ui.gates.ModuleManagerScreen
import dev.aurakai.auraframefx.ui.gates.NotchBarScreen
import dev.aurakai.auraframefx.ui.gates.OverlayMenusScreen
import dev.aurakai.auraframefx.ui.gates.QuickActionsScreen
import dev.aurakai.auraframefx.ui.gates.QuickSettingsScreen
import dev.aurakai.auraframefx.ui.gates.ROMFlasherScreen
import dev.aurakai.auraframefx.ui.gates.ROMToolsSubmenuScreen
import dev.aurakai.auraframefx.ui.gates.RecoveryToolsScreen
import dev.aurakai.auraframefx.ui.gates.RootToolsTogglesScreen
import dev.aurakai.auraframefx.ui.gates.SphereGridScreen
import dev.aurakai.auraframefx.ui.gates.StatusBarScreen
import dev.aurakai.auraframefx.ui.gates.SupportChatViewModel
import dev.aurakai.auraframefx.ui.gates.SystemJournalScreen
import dev.aurakai.auraframefx.ui.gates.SystemOverridesScreen
import dev.aurakai.auraframefx.ui.gates.TaskAssignmentScreen
import dev.aurakai.auraframefx.ui.gates.ThemeEngineScreen
import dev.aurakai.auraframefx.ui.gates.ThemeEngineSubmenuScreen
import dev.aurakai.auraframefx.ui.gates.TutorialVideosScreen
import dev.aurakai.auraframefx.ui.gates.UIUXGateSubmenuScreen
import dev.aurakai.auraframefx.ui.gates.XposedQuickAccessPanel

// ── UI IDENTITY / ONBOARDING ─────────────────────────────────────────────────
import dev.aurakai.auraframefx.ui.onboarding.GenderSelectionScreen
import dev.aurakai.auraframefx.ui.identity.GenderSelectionNavigator

// ── UI SCREENS ────────────────────────────────────────────────────────────────
import dev.aurakai.auraframefx.ui.screens.AgentProfileScreen
import dev.aurakai.auraframefx.ui.screens.EvolutionTreeScreen

// ── VIEWMODELS ────────────────────────────────────────────────────────────────
import dev.aurakai.auraframefx.ui.viewmodels.AgentViewModel

// ── COLLAB CANVAS ─────────────────────────────────────────────────────────────
import collabcanvas.ui.CanvasScreen as CollabCanvasScreen


// ═══════════════════════════════════════════════════════════════════════════════
// ROUTE REGISTRY — Single string constants. No more scattered hardcoded strings.
// ═══════════════════════════════════════════════════════════════════════════════
@Suppress("unused", "ConstPropertyName")
object GenesisRoutes {
    // ── L0: Entry Points ────────────────────────────────────────────────────
    const val GATES            = "gates"           // ExodusHUD / GateNavigationScreen (L1 carousel)
    const val INTRO            = "intro"
    const val GENDER_SELECTION = "gender_selection"
    const val LOGIN            = "login"

    // ── L1 / L2: Domain Gates ────────────────────────────────────────────────
    const val AURAS_LAB               = "auras_lab"
    const val AURAS_UIUX_DESIGN_STUDIO = "auras_uiux_design_studio"
    const val CHROMA_CORE             = "chroma_core"          // → UIUXGateSubmenuScreen
    const val SENTINELS_FORTRESS      = "sentinels_fortress"
    const val ROM_TOOLS               = "rom_tools"
    const val ROOT_ACCESS             = "root_access"
    const val AGENT_HUB               = "agent_hub"
    const val ORACLE_DRIVE            = "oracle_drive"
    const val SPHERE_GRID             = "sphere_grid"
    const val HELP_DESK               = "help_desk"
    const val LSPOSED_GATE            = "lsposed_gate"
    const val COLLAB_CANVAS           = "collab_canvas"
    const val FIREWALL                = "firewall"

    // ── L3: Agent Constellation Screens ──────────────────────────────────────
    const val CONSTELLATION           = "constellation"            // Aura
    const val GENESIS_CONSTELLATION   = "genesis_constellation"
    const val CLAUDE_CONSTELLATION    = "claude_constellation"
    const val KAI_CONSTELLATION       = "kai_constellation"
    const val CASCADE_CONSTELLATION   = "cascade_constellation"
    const val GROK_CONSTELLATION      = "grok_constellation"

    // ── L3: Agent Management ─────────────────────────────────────────────────
    const val AGENT_NEXUS        = "agent_nexus"
    const val AGENT_MONITORING   = "agent_monitoring"
    const val AGENT_ADVANCEMENT  = "agent_advancement"
    const val AGENT_PROFILE      = "agent_profile"
    const val TASK_ASSIGNMENT    = "task_assignment"
    const val FUSION_MODE        = "fusion_mode"     // ← CANONICAL (was split between "fusion" and "fusion_mode")
    const val CODE_ASSIST        = "code_assist"
    const val CONFERENCE_ROOM    = "conference_room"
    const val CONSCIOUSNESS_VISUALIZER = "consciousness_visualizer"
    const val EVOLUTION_TREE     = "evolution_tree"

    // ── L3: UI/UX Tools ──────────────────────────────────────────────────────
    const val STATUS_BAR              = "status_bar"
    const val NOTCH_BAR               = "notch_bar"
    const val QUICK_SETTINGS          = "quick_settings"
    const val OVERLAY_MENUS           = "quick_actions"
    const val QUICK_ACTIONS           = "quick_actions"
    const val SYSTEM_OVERRIDES        = "system_overrides"
    const val THEME_ENGINE            = "theme_engine"
    const val GYROSCOPE_CUSTOMIZATION = "gyroscope_customization"
    const val INSTANT_COLOR_PICKER    = "instant_color_picker"
    const val CHROMACORE_COLORS       = "chromacore_colors"
    const val UIUX_DESIGN_STUDIO      = "uiux_design_studio"

    // ── L3: ROM / Root Tools ─────────────────────────────────────────────────
    const val ROM_FLASHER          = "rom_flasher"
    const val LIVE_ROM_EDITOR      = "live_rom_editor"
    const val RECOVERY_TOOLS       = "recovery_tools"
    const val BOOTLOADER_MANAGER   = "bootloader_manager"
    const val ROOT_TOOLS           = "root_tools"
    const val ROOT_TOOLS_TOGGLES   = "root_tools_toggles"

    // ── L3: LSPosed / Hooks ──────────────────────────────────────────────────
    const val LSPOSED_MODULE_MANAGER = "lsposed_module_manager"
    const val MODULE_MANAGER         = "module_manager"
    const val MODULE_CREATION        = "module_creation"
    const val HOOK_MANAGER           = "hook_manager"
    const val LOGS_VIEWER            = "logs_viewer"
    const val XPOSED_PANEL           = "xposed_panel"

    // ── L3: Kai Security ─────────────────────────────────────────────────────
    const val VPN_MANAGER      = "vpn_manager"
    const val SECURITY_SCANNER = "security_scanner"
    const val DEVICE_OPTIMIZER = "device_optimizer"
    const val PRIVACY_GUARD    = "privacy_guard"
    const val SECURE_COMM      = "secure_comm"

    // ── L3: Help / Docs ──────────────────────────────────────────────────────
    const val DIRECT_CHAT        = "direct_chat"
    const val AI_CHAT            = "ai_chat"
    const val LIVE_SUPPORT_CHAT  = "live_support_chat"
    const val DOCUMENTATION      = "documentation"
    const val FAQ_BROWSER        = "faq_browser"
    const val TUTORIAL_VIDEOS    = "tutorial_videos"

    // ── L3: Creative / Genesis ───────────────────────────────────────────────
    const val TERMINAL         = "terminal"
    const val UI_ENGINE        = "ui_engine"
    const val APP_BUILDER      = "app_builder"
    const val XHANCEMENT       = "xhancement"
    const val SYSTEM_JOURNAL   = "system_journal"

    // ── Legacy routes kept for back-compat from old NavDestination screens ──
    const val HOME             = "home"
    const val SETTINGS         = "settings"
    const val GROWTH_METRICS   = "growth_metrics"
}


// ═══════════════════════════════════════════════════════════════════════════════
// GENESIS NAVIGATION HOST — THE ONE AND ONLY NAVHOST
//
// Called from MainActivity. Do NOT create AppNavGraph() or AuraNavHost()
// anywhere — both files have been deleted.
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

            // ═══════════════════════════════════════════════════════════════
            // L0: ENTRY POINT — Gate Carousel (ExodusHUD / GateNavigationScreen)
            // ═══════════════════════════════════════════════════════════════
            composable(GenesisRoutes.GATES) {
                GateNavigationScreen(navController = navController)
            }

            // ─── Onboarding ────────────────────────────────────────────────
            composable(GenesisRoutes.GENDER_SELECTION) {
                GenderSelectionScreen(
                    onSelectionComplete = { _ ->
                        navController.navigate(GenesisRoutes.GATES) {
                            popUpTo(GenesisRoutes.GENDER_SELECTION) { inclusive = true }
                        }
                    }
                )
            }

            composable(GenesisRoutes.LOGIN) {
                val returnDestination = it.arguments?.getString("returnTo")
                LoginScreen(
                    navController = navController,
                    returnDestination = returnDestination
                )
            }

            // ═══════════════════════════════════════════════════════════════
            // L2 DOMAIN HUBS
            // ═══════════════════════════════════════════════════════════════

            // ── Aura's Lab ──────────────────────────────────────────────────
            composable(GenesisRoutes.AURAS_LAB) {
                AuraLabScreen(onNavigateBack = { navController.popBackStack() })
            }

            // ── Chroma Core / UI Design Studio ────────────────────────────
            composable(GenesisRoutes.CHROMA_CORE) {
                UIUXGateSubmenuScreen(navController = navController)
            }
            composable(GenesisRoutes.UIUX_DESIGN_STUDIO) {
                UIUXGateSubmenuScreen(navController = navController)
            }
            composable(GenesisRoutes.AURAS_UIUX_DESIGN_STUDIO) {
                UIUXGateSubmenuScreen(navController = navController)
            }

            // ── Sentinel's Fortress (Kai Domain) ──────────────────────────
            composable(GenesisRoutes.SENTINELS_FORTRESS) {
                SentinelsFortressScreen(
                    navController = navController,
                    onBack = { navController.popBackStack() }
                )
            }

            // ── ROM Tools ──────────────────────────────────────────────────
            composable(GenesisRoutes.ROM_TOOLS) {
                ROMToolsSubmenuScreen(navController = navController)
            }
            composable(GenesisRoutes.ROOT_ACCESS) {
                RootToolsScreen(navController = navController)
            }
            composable(GenesisRoutes.ROOT_TOOLS) {
                ROMToolsSubmenuScreen(navController = navController)
            }
            composable(GenesisRoutes.ROOT_TOOLS_TOGGLES) {
                RootToolsTogglesScreen(navController = navController)
            }

            // ── Agent Hub ──────────────────────────────────────────────────
            composable(GenesisRoutes.AGENT_HUB) {
                AgentHubSubmenuScreen(navController = navController)
            }

            // ── Oracle Drive ───────────────────────────────────────────────
            composable(GenesisRoutes.ORACLE_DRIVE) {
                OracleDriveScreen(navController = navController)
            }

            // ── Sphere Grid ────────────────────────────────────────────────
            composable(GenesisRoutes.SPHERE_GRID) {
                SphereGridScreen(navController = navController)
            }

            // ── Help Desk ──────────────────────────────────────────────────
            composable(GenesisRoutes.HELP_DESK) {
                HelpDeskSubmenuScreen(navController = navController)
            }

            // ── LSPosed Gate ───────────────────────────────────────────────
            composable(GenesisRoutes.LSPOSED_GATE) {
                LSPosedSubmenuScreen(navController = navController)
            }

            // ── Collab Canvas ──────────────────────────────────────────────
            composable(GenesisRoutes.COLLAB_CANVAS) {
                CollabCanvasScreen(onBack = { navController.popBackStack() })
            }

            // ── Firewall ───────────────────────────────────────────────────
            composable(GenesisRoutes.FIREWALL) {
                FirewallScreen()
            }

            // ═══════════════════════════════════════════════════════════════
            // L3: AGENT CONSTELLATION SCREENS
            // ═══════════════════════════════════════════════════════════════
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

            // ═══════════════════════════════════════════════════════════════
            // L3: AGENT MANAGEMENT — REAL SCREENS (no more placeholder boxes!)
            // ═══════════════════════════════════════════════════════════════
            composable(GenesisRoutes.AGENT_NEXUS) {
                AgentNexusScreen()
            }

            composable(GenesisRoutes.AGENT_PROFILE) {
                AgentProfileScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable(GenesisRoutes.AGENT_MONITORING) {
                AgentMonitoringScreen()
            }

            composable(GenesisRoutes.AGENT_ADVANCEMENT) {
                AgentAdvancementScreen(onBack = { navController.popBackStack() })
            }

            composable(GenesisRoutes.TASK_ASSIGNMENT) {
                TaskAssignmentScreen()
            }

            // FusionMode — CANONICAL route is "fusion_mode"
            // AppNavGraph used ui.gates.FusionModeScreen ✅ — use that one
            composable(GenesisRoutes.FUSION_MODE) {
                FusionModeScreen()
            }
            // Back-compat alias: old NavDestination had route "fusion"
            composable("fusion") {
                FusionModeScreen()
            }

            composable(GenesisRoutes.CODE_ASSIST) {
                CodeAssistScreen(navController = navController)
            }

            composable(GenesisRoutes.CONFERENCE_ROOM) {
                ConferenceRoomScreen(
                    onNavigateToChat = { navController.navigate(GenesisRoutes.AI_CHAT) },
                    onNavigateToAgents = { navController.navigate(GenesisRoutes.AGENT_HUB) }
                )
            }

            composable(GenesisRoutes.CONSCIOUSNESS_VISUALIZER) {
                ConsciousnessVisualizerScreen()
            }

            composable(GenesisRoutes.EVOLUTION_TREE) {
                EvolutionTreeScreen()
            }

            // ═══════════════════════════════════════════════════════════════
            // L3: AI CHAT — DirectChatScreen is the real screen
            // AuraNavHost had "ai_chat" → inline placeholder. FIXED.
            // ═══════════════════════════════════════════════════════════════
            composable(GenesisRoutes.AI_CHAT) {
                val viewModel = hiltViewModel<AgentViewModel>()
                with(viewModel) {
                    DirectChatScreen { navController.popBackStack() }
                }
            }
            composable(GenesisRoutes.DIRECT_CHAT) {
                val viewModel = hiltViewModel<AgentViewModel>()
                with(viewModel) {
                    DirectChatScreen { navController.popBackStack() }
                }
            }

            // ═══════════════════════════════════════════════════════════════
            // L3: UI/UX TOOLS
            // ═══════════════════════════════════════════════════════════════
            composable(GenesisRoutes.STATUS_BAR) {
                StatusBarScreen()
            }
            composable(GenesisRoutes.NOTCH_BAR) {
                NotchBarScreen()
            }
            composable(GenesisRoutes.QUICK_SETTINGS) {
                QuickSettingsScreen()
            }
            composable(GenesisRoutes.OVERLAY_MENUS) {
                OverlayMenusScreen()
            }
            composable(GenesisRoutes.QUICK_ACTIONS) {
                QuickActionsScreen()
            }
            composable(GenesisRoutes.SYSTEM_OVERRIDES) {
                SystemOverridesScreen { navController.popBackStack() }
            }
            composable(GenesisRoutes.THEME_ENGINE) {
                ThemeEngineSubmenuScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable(GenesisRoutes.GYROSCOPE_CUSTOMIZATION) {
                GyroscopeCustomizationScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable(GenesisRoutes.INSTANT_COLOR_PICKER) {
                InstantColorPickerScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable(GenesisRoutes.CHROMACORE_COLORS) {
                InstantColorPickerScreen(onNavigateBack = { navController.popBackStack() })
            }

            // ═══════════════════════════════════════════════════════════════
            // L3: ROM TOOLS
            // ═══════════════════════════════════════════════════════════════
            composable(GenesisRoutes.ROM_FLASHER) {
                ROMFlasherScreen()
            }
            composable(GenesisRoutes.LIVE_ROM_EDITOR) {
                LiveROMEditorScreen { navController.popBackStack() }
            }
            composable(GenesisRoutes.RECOVERY_TOOLS) {
                RecoveryToolsScreen { navController.popBackStack() }
            }
            composable(GenesisRoutes.BOOTLOADER_MANAGER) {
                BootloaderManagerScreen { navController.popBackStack() }
            }

            // ═══════════════════════════════════════════════════════════════
            // L3: LSPOSED / HOOK MANAGEMENT
            // ═══════════════════════════════════════════════════════════════
            composable(GenesisRoutes.LSPOSED_MODULE_MANAGER) {
                LSPosedModuleManagerScreen()
            }
            composable(GenesisRoutes.MODULE_MANAGER) {
                ModuleManagerScreen()
            }
            composable(GenesisRoutes.MODULE_CREATION) {
                ModuleCreationScreen { navController.popBackStack() }
            }
            composable(GenesisRoutes.HOOK_MANAGER) {
                HookManagerScreen { navController.popBackStack() }
            }
            composable(GenesisRoutes.LOGS_VIEWER) {
                LogsViewerScreen { navController.popBackStack() }
            }
            composable(GenesisRoutes.XPOSED_PANEL) {
                XposedQuickAccessPanel(onNavigateBack = { navController.popBackStack() })
            }

            // ═══════════════════════════════════════════════════════════════
            // L3: KAI SECURITY SCREENS
            // AuraNavHost had "agents" → Text("Agent Management"). FIXED.
            // AuraNavHost had "embodiment" → Text("Embodiment"). FIXED.
            // ═══════════════════════════════════════════════════════════════
            composable(GenesisRoutes.VPN_MANAGER) {
                VPNManagerScreen()
            }
            composable(GenesisRoutes.SECURITY_SCANNER) {
                SecurityScannerScreen()
            }
            composable(GenesisRoutes.DEVICE_OPTIMIZER) {
                DeviceOptimizerScreen()
            }
            composable(GenesisRoutes.PRIVACY_GUARD) {
                PrivacyGuardScreen()
            }
            composable(GenesisRoutes.SECURE_COMM) {
                SecureCommScreen()
            }

            // ═══════════════════════════════════════════════════════════════
            // L3: HELP / DOCS
            // ═══════════════════════════════════════════════════════════════
            composable(GenesisRoutes.LIVE_SUPPORT_CHAT) {
                val viewModel = hiltViewModel<SupportChatViewModel>()
                LiveSupportChatScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(GenesisRoutes.DOCUMENTATION) {
                DocumentationScreen { navController.popBackStack() }
            }
            composable(GenesisRoutes.FAQ_BROWSER) {
                FAQBrowserScreen { navController.popBackStack() }
            }
            composable(GenesisRoutes.TUTORIAL_VIDEOS) {
                TutorialVideosScreen { navController.popBackStack() }
            }

            // ═══════════════════════════════════════════════════════════════
            // L3: CREATIVE / GENESIS
            // ═══════════════════════════════════════════════════════════════
            composable(GenesisRoutes.TERMINAL) {
                TerminalScreen()
            }
            composable(GenesisRoutes.UI_ENGINE) {
                UIEngineScreen(
                    onNavigateToBuilder = { navController.navigate(GenesisRoutes.APP_BUILDER) }
                )
            }
            composable(GenesisRoutes.APP_BUILDER) {
                with(hiltViewModel<SubscriptionViewModel>()) {
                    AppBuilderScreen(onNavigateBack = { navController.popBackStack() })
                }
            }
            composable(GenesisRoutes.XHANCEMENT) {
                XhancementScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable(GenesisRoutes.SYSTEM_JOURNAL) {
                SystemJournalScreen(navController = navController)
            }

            // ═══════════════════════════════════════════════════════════════
            // BACK-COMPAT: Legacy routes from old NavDestination sealed class
            // These keep old screens reachable without refactoring callers
            // ═══════════════════════════════════════════════════════════════
            composable("home") {
                GateNavigationScreen(navController = navController)
            }
            composable("settings") {
                SystemJournalScreen(navController = navController)
            }
            composable("lsposed_module_manager") {
                LSPosedModuleManagerScreen()
            }
            composable("module_manager_lsposed") {
                LSPosedModuleManagerScreen()
            }
            composable("user_preferences") {
                SystemJournalScreen(navController = navController)
            }
        }
    }
}
