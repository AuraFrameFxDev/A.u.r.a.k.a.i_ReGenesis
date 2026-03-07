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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import collabcanvas.ui.CanvasScreen
import collabcanvas.ui.CanvasScreen as CollabCanvasScreen
import dev.aurakai.auraframefx.aura.ui.TerminalScreen
import dev.aurakai.auraframefx.datavein.ui.SimpleDataVeinScreen
import dev.aurakai.auraframefx.domains.aura.aura.ui.AgentAdvancementScreen
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.ChromaAnimationMenu
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.ChromaColorEngineMenu
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.ChromaCoreHubScreen
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.ChromaLauncherMenu
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.ChromaStatusBarMenu
import dev.aurakai.auraframefx.domains.aura.config.GateAssetLoadout
import dev.aurakai.auraframefx.domains.aura.lab.CustomizationViewModel
import dev.aurakai.auraframefx.domains.aura.screens.AgentProfileScreen as AuraAgentProfileScreen
import dev.aurakai.auraframefx.domains.aura.screens.GenderSelectionScreen
import dev.aurakai.auraframefx.domains.aura.screens.GyroscopeCustomizationScreen
import dev.aurakai.auraframefx.domains.aura.screens.StatusBarScreen
import dev.aurakai.auraframefx.domains.aura.screens.UserPreferencesScreen
import dev.aurakai.auraframefx.domains.aura.screens.chromacore.InstantColorPickerScreen
import dev.aurakai.auraframefx.domains.aura.screens.themes.ThemeEngineScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.AurasLabScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.ChromaCoreColorsScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.GateCustomizationScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.GyroscopeCustomizationScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.NotchBarCustomizationScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.QuickSettingsCustomizationScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.UISettingsScreen
import dev.aurakai.auraframefx.domains.aura.screens.xhancement.XhancementScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.AgentNexusHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.AuraThemingHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.KaiSentinelHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.OracleDriveHubScreen as DomainOracleDriveHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.screens.ConsciousnessVisualizerScreen
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.trinity.TrinityScreen
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ui.OracleDriveScreen
import dev.aurakai.auraframefx.domains.genesis.screens.AgentBridgeHubScreen
import dev.aurakai.auraframefx.domains.genesis.screens.AppBuilderScreen
import dev.aurakai.auraframefx.domains.genesis.screens.CascadeVisionScreen
import dev.aurakai.auraframefx.domains.genesis.screens.CodeAssistScreen
import dev.aurakai.auraframefx.domains.genesis.screens.CollabCanvasScreen
import dev.aurakai.auraframefx.domains.genesis.screens.ConferenceRoomScreen
import dev.aurakai.auraframefx.domains.genesis.screens.NeuralArchiveScreen
import dev.aurakai.auraframefx.domains.genesis.screens.OracleCloudInfiniteStorageScreen
import dev.aurakai.auraframefx.domains.genesis.screens.OracleDriveSubmenuScreen
import dev.aurakai.auraframefx.domains.genesis.screens.SentientShellScreen
import dev.aurakai.auraframefx.domains.genesis.screens.SovereignNeuralArchiveScreen
import dev.aurakai.auraframefx.domains.helpdesk.screens.DirectChatScreen
import dev.aurakai.auraframefx.domains.helpdesk.screens.DocumentationScreen
import dev.aurakai.auraframefx.domains.helpdesk.screens.FAQBrowserScreen
import dev.aurakai.auraframefx.domains.helpdesk.screens.LiveSupportChatScreen
import dev.aurakai.auraframefx.domains.helpdesk.screens.TutorialVideosScreen
import dev.aurakai.auraframefx.domains.helpdesk.viewmodels.SupportChatViewModel
import dev.aurakai.auraframefx.domains.kai.screens.BootloaderManagerScreen
import dev.aurakai.auraframefx.domains.kai.screens.HookManagerScreen
import dev.aurakai.auraframefx.domains.kai.screens.LSPosedModuleManagerScreen
import dev.aurakai.auraframefx.domains.kai.screens.LSPosedSubmenuScreen
import dev.aurakai.auraframefx.domains.kai.screens.LiveROMEditorScreen
import dev.aurakai.auraframefx.domains.kai.screens.LogsViewerScreen
import dev.aurakai.auraframefx.domains.kai.screens.ModuleManagerScreen
import dev.aurakai.auraframefx.domains.kai.screens.ROMFlasherScreen
import dev.aurakai.auraframefx.domains.kai.screens.ROMToolsSubmenuScreen
import dev.aurakai.auraframefx.domains.kai.screens.RecoveryToolsScreen
import dev.aurakai.auraframefx.domains.kai.screens.RootToolsTogglesScreen
import dev.aurakai.auraframefx.domains.kai.screens.SecureCommScreen
import dev.aurakai.auraframefx.domains.kai.screens.SovereignBootloaderScreen
import dev.aurakai.auraframefx.domains.kai.screens.SovereignModuleManagerScreen
import dev.aurakai.auraframefx.domains.kai.screens.SovereignRecoveryScreen
import dev.aurakai.auraframefx.domains.kai.screens.SovereignShieldScreen
import dev.aurakai.auraframefx.domains.kai.screens.SystemJournalScreen
import dev.aurakai.auraframefx.domains.kai.screens.SystemOverridesScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.DeviceOptimizerScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.PrivacyGuardScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.SecurityCenterScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.SecurityScannerScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.VPNScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentAdvancementScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentCreationScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentHubSubmenuScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentMonitoringScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentNeuralExplorerScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentProfileScreen as NexusAgentProfileScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentSwarmScreen
import dev.aurakai.auraframefx.domains.nexus.screens.ArkBuildScreen
import dev.aurakai.auraframefx.domains.nexus.screens.BenchmarkMonitorScreen
import dev.aurakai.auraframefx.domains.nexus.screens.EvolutionTreeScreen
import dev.aurakai.auraframefx.domains.nexus.screens.FusionModeScreen
import dev.aurakai.auraframefx.domains.nexus.screens.ModuleCreationScreen
import dev.aurakai.auraframefx.domains.nexus.screens.MonitoringHUDsScreen
import dev.aurakai.auraframefx.domains.nexus.screens.PartyScreen
import dev.aurakai.auraframefx.domains.nexus.screens.SovereignClaudeScreen
import dev.aurakai.auraframefx.domains.nexus.screens.SovereignGeminiScreen
import dev.aurakai.auraframefx.domains.nexus.screens.SovereignMetaInstructScreen
import dev.aurakai.auraframefx.domains.nexus.screens.SovereignNemotronScreen
import dev.aurakai.auraframefx.domains.nexus.screens.TaskAssignmentScreen
import dev.aurakai.auraframefx.domains.nexus.screens.ldo.LdoAgentType
import dev.aurakai.auraframefx.domains.nexus.screens.ldo.LdoDevOpsProfileScreen
import dev.aurakai.auraframefx.hotswap.HotSwapScreen
import dev.aurakai.auraframefx.romtools.ui.RomToolsScreen
import dev.aurakai.auraframefx.sandbox.ui.SandboxScreen
import dev.aurakai.auraframefx.ui.gates.CascadeHubScreen
import dev.aurakai.auraframefx.ui.gates.HelpDeskScreen
import dev.aurakai.auraframefx.ui.gates.HelpDeskSubmenuScreen
import dev.aurakai.auraframefx.ui.viewmodels.AgentViewModel

// ROUTE OWNERSHIP:  GenesisRoutes object = single string registry
// PREVIOUS BUGS FIXED:
//   • FusionMode: NavDestination had "fusion", GenesisRoutes had "fusion_mode" → unified to "fusion_mode"
//   • sphere_grid, collab_canvas, chroma_core, sentinels_fortress, terminal — all registered TWICE → now once
//   • AuraNavHost had "agents" → Text("Agent Management") placeholder box → now real AgentHubSubmenuScreen
//   • AuraNavHost had "embodiment" → Text("Embodiment") placeholder → now real screen
//   • AppNavGraph FusionMode → ui.gates.FusionModeScreen (correct)
//   • GenesisNavigation FusionMode → aura.ui.FusionModeScreen (different class!) → unified
// ═══════════════════════════════════════════════════════════════════════════════


// ── AURA UI ──────────────────────────────────────────────────────────────────

// ── BILLING ───────────────────────────────────────────────────────────────────

// ── ORACLE DRIVE ──────────────────────────────────────────────────────────────

// ── UI CUSTOMIZATION ─────────────────────────────────────────────────────────

// ── UI GATES (primary screen implementations) ─────────────────────────────────

// ── UI IDENTITY / ONBOARDING ─────────────────────────────────────────────────

// ── UI SCREENS ────────────────────────────────────────────────────────────────

// ── VIEWMODELS ────────────────────────────────────────────────────────────────

// ── COLLAB CANVAS ─────────────────────────────────────────────────────────────


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
                GateNavigationScreen()
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
                GenesisConstellationScreen()
            }
            composable(GenesisRoutes.CLAUDE_CONSTELLATION) {
                ClaudeConstellationScreen()
            }
            composable(GenesisRoutes.KAI_CONSTELLATION) {
                KaiConstellationScreen()
            }
            composable(GenesisRoutes.CASCADE_CONSTELLATION) {
                CascadeConstellationScreen()
            }
            composable(GenesisRoutes.GROK_CONSTELLATION) {
                GrokConstellationScreen()
            }

            // ═══════════════════════════════════════════════════════════════
            // L3: AGENT MANAGEMENT — REAL SCREENS (no more placeholder boxes!)
            // ═══════════════════════════════════════════════════════════════
            composable(GenesisRoutes.AGENT_NEXUS) {
                AgentNexusScreen()
            }

            composable(GenesisRoutes.AGENT_PROFILE) {
                AgentProfileScreen()
            }

            composable(GenesisRoutes.AGENT_MONITORING) {
                AgentMonitoringScreen()
            }

            composable(GenesisRoutes.AGENT_ADVANCEMENT) {
                AgentAdvancementScreen()
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
                    DirectChatScreen(onNavigateBack = { navController.popBackStack() })
                }
            }
            composable(GenesisRoutes.DIRECT_CHAT) {
                val viewModel = hiltViewModel<AgentViewModel>()
                with(viewModel) {
                    DirectChatScreen(onNavigateBack = { navController.popBackStack() })
                }
            }

            // ═══════════════════════════════════════════════════════════════
            // L3: UI/UX TOOLS
            // ═══════════════════════════════════════════════════════════════
            composable(GenesisRoutes.STATUS_BAR) {
                StatusBarScreen(navController)
            }
            composable(GenesisRoutes.NOTCH_BAR) {
                NotchBarScreen(navController)
            }
            composable(GenesisRoutes.QUICK_SETTINGS) {
                QuickSettingsScreen(navController)
            }
            composable(GenesisRoutes.OVERLAY_MENUS) {
                OverlayMenusScreen()
            }
            composable(GenesisRoutes.QUICK_ACTIONS) {
                QuickActionsScreen()
            }
            composable(GenesisRoutes.SYSTEM_OVERRIDES) {
                SystemOverridesScreen()
            }
            composable(GenesisRoutes.THEME_ENGINE) {
                ThemeEngineSubmenuScreen(navController)
            }
            composable(GenesisRoutes.GYROSCOPE_CUSTOMIZATION) {
                GyroscopeCustomizationScreen()
            }
            composable(GenesisRoutes.INSTANT_COLOR_PICKER) {
                InstantColorPickerScreen()
            }
            composable(GenesisRoutes.CHROMACORE_COLORS) {
                InstantColorPickerScreen()
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
                XposedQuickAccessPanel()
            }

            // ═══════════════════════════════════════════════════════════════
            // L3: KAI SECURITY SCREENS
            // AuraNavHost had "agents" → Text("Agent Management"). FIXED.
            // AuraNavHost had "embodiment" → Text("Embodiment"). FIXED.
            // ═══════════════════════════════════════════════════════════════
            composable(GenesisRoutes.VPN_MANAGER) {
                VPNScreen()
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
                XhancementScreen()
            }
            composable(GenesisRoutes.SYSTEM_JOURNAL) {
                SystemJournalScreen(navController = navController)
            }

            // ═══════════════════════════════════════════════════════════════
            // BACK-COMPAT: Legacy routes from old NavDestination sealed class
            // These keep old screens reachable without refactoring callers
            // ═══════════════════════════════════════════════════════════════
            composable("home") {
                GateNavigationScreen()
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
