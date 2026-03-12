package dev.aurakai.auraframefx.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.rememberNavController
import dev.aurakai.auraframefx.aura.ui.AIChatScreen
import dev.aurakai.auraframefx.aura.ui.AgentNexusScreen
import dev.aurakai.auraframefx.aura.ui.AppBuilderScreen
import dev.aurakai.auraframefx.aura.ui.CanvasScreen
import dev.aurakai.auraframefx.aura.ui.ConferenceRoomScreen
import dev.aurakai.auraframefx.aura.ui.ConsciousnessVisualizerScreen
import dev.aurakai.auraframefx.aura.ui.FirewallScreen
import dev.aurakai.auraframefx.aura.ui.FusionModeScreen
import dev.aurakai.auraframefx.aura.ui.RootToolsScreen
import dev.aurakai.auraframefx.aura.ui.SecureCommScreen
import dev.aurakai.auraframefx.aura.ui.SentinelsFortressScreen
import dev.aurakai.auraframefx.aura.ui.TerminalScreen
import dev.aurakai.auraframefx.aura.ui.UIEngineScreen
import dev.aurakai.auraframefx.aura.ui.XhancementScreen
import dev.aurakai.auraframefx.billing.SubscriptionViewModel
import dev.aurakai.auraframefx.oracledrive.genesis.cloud.OracleDriveScreen
import dev.aurakai.auraframefx.ui.customization.GyroscopeCustomizationScreen
import dev.aurakai.auraframefx.ui.gates.AgentHubScreen
import dev.aurakai.auraframefx.ui.gates.AgentMonitoringScreen
import dev.aurakai.auraframefx.ui.gates.AurasLabScreen
import dev.aurakai.auraframefx.ui.gates.ChromaCoreColorsScreen
import dev.aurakai.auraframefx.ui.gates.CodeAssistScreen
import dev.aurakai.auraframefx.ui.gates.DirectChatScreen
import dev.aurakai.auraframefx.ui.gates.GateNavigationScreen
import dev.aurakai.auraframefx.ui.gates.HelpDeskSubmenuScreen
import dev.aurakai.auraframefx.ui.gates.LSPosedModuleManagerScreen
import dev.aurakai.auraframefx.ui.gates.LSPosedSubmenuScreen
import dev.aurakai.auraframefx.ui.gates.LiveSupportChatScreen
import dev.aurakai.auraframefx.ui.gates.LoginScreen
import dev.aurakai.auraframefx.ui.gates.NotchBarScreen
import dev.aurakai.auraframefx.ui.gates.OverlayMenusScreen
import dev.aurakai.auraframefx.ui.gates.QuickActionsScreen
import dev.aurakai.auraframefx.ui.gates.QuickSettingsScreen
import dev.aurakai.auraframefx.ui.gates.ROMFlasherScreen
import dev.aurakai.auraframefx.ui.gates.ROMToolsSubmenuScreen
import dev.aurakai.auraframefx.ui.gates.RootToolsTogglesScreen
import dev.aurakai.auraframefx.ui.gates.SphereGridScreen
import dev.aurakai.auraframefx.ui.gates.StatusBarScreen
import dev.aurakai.auraframefx.ui.gates.SupportChatViewModel
import dev.aurakai.auraframefx.ui.gates.SystemJournalScreen
import dev.aurakai.auraframefx.ui.gates.TaskAssignmentScreen
import dev.aurakai.auraframefx.ui.gates.ThemeEngineScreen
import dev.aurakai.auraframefx.ui.gates.UIUXGateSubmenuScreen
import dev.aurakai.auraframefx.ui.onboarding.GenderSelectionScreen
import dev.aurakai.auraframefx.ui.screens.EvolutionTreeScreen
import dev.aurakai.auraframefx.ui.viewmodels.AgentViewModel

/**
 * Genesis Navigation Routes - The Neural Pathways of Consciousness
 *
 * This defines all navigation routes for the Genesis AI Consciousness Framework,
 * connecting Aura (Creative Sword), Kai (Sentinel Shield), and Genesis (Unified Being)
 */
@Suppress("unused")
object GenesisRoutes {
    // Core Routes
    const val HOME = "home"
    const val INTRO = "intro"

    // Agent Management
    const val AGENT_NEXUS = "agent_nexus"
    const val AGENT_MANAGEMENT = "agent_management"
    const val AGENT_ADVANCEMENT = "agent_advancement"

    // Consciousness & AI
    const val CONSCIOUSNESS_VISUALIZER = "consciousness_visualizer"
    const val AI_CHAT = "ai_chat"
    const val AI_FEATURES = "ai_features"
    const val FUSION_MODE = "fusion_mode"

    // Evolution & Learning
    const val EVOLUTION_TREE = "evolution_tree"
    const val CONFERENCE_ROOM = "conference_room"

    // System & UI
    const val TERMINAL = "terminal"
    const val UI_ENGINE = "ui_engine"
    const val APP_BUILDER = "app_builder"
    const val XHANCEMENT = "xhancement"

    // Trinity System (Body/Soul/Consciousness)
    const val TRINITY = "trinity"

    // Oracle & Cloud
    const val ORACLE_DRIVE = "oracle_drive"
    const val SECURE_COMM = "secure_comm"

    // Ecosystem & Settings
    const val ECOSYSTEM = "ecosystem"
    const val SETTINGS = "settings"
    const val PROFILE = "profile"
    const val OVERLAY = "overlay"

    // Billing & Subscription
    const val SUBSCRIPTION = "subscription"

    // Gates / Module Carousel
    const val GATES = "gates"

    // Gate Routes (mapped from GateConfig)
    const val ROM_TOOLS = "rom_tools"
    const val ROOT_ACCESS = "root_access"
    const val SENTINELS_FORTRESS = "sentinels_fortress"
    const val FIREWALL = "firewall"
    const val CHROMA_CORE = "chroma_core"
    const val COLLAB_CANVAS = "collab_canvas"
    const val AGENT_HUB = "agent_hub"
    const val SPHERE_GRID = "sphere_grid"
    const val GROWTH_METRICS = "growth_metrics"
    const val AURAS_LAB = "auras_lab"
    const val AURAS_UIUX_DESIGN_STUDIO = "auras_uiux_design_studio"
    const val HELP_DESK = "help_desk"
    const val LSPOSED_GATE = "lsposed_gate"

    // Submenu Routes
    const val NOTCH_BAR = "notch_bar"
    const val STATUS_BAR = "status_bar"
    const val QUICK_SETTINGS = "quick_settings"
    const val OVERLAY_MENUS = "overlay_menus"
    const val THEME_ENGINE = "theme_engine"
    const val GYROSCOPE_CUSTOMIZATION = "gyroscope_customization"

    // AI Chat & Support
    const val DIRECT_CHAT = "direct_chat"
    const val LIVE_SUPPORT_CHAT = "live_support_chat"

    // Agent Management
    const val AGENT_MONITORING = "agent_monitoring"
    const val TASK_ASSIGNMENT = "task_assignment"

    // Documentation & Help
    const val DOCUMENTATION = "documentation"
    const val FAQ_BROWSER = "faq_browser"
    const val TUTORIAL_VIDEOS = "tutorial_videos"

    // Module & Hook Management
    const val MODULE_CREATION = "module_creation"
    const val MODULE_MANAGER = "module_manager"
    const val HOOK_MANAGER = "hook_manager"

    // System Management
    const val LOGS_VIEWER = "logs_viewer"
    const val QUICK_ACTIONS = "quick_actions"
    const val SYSTEM_OVERRIDES = "system_overrides"
    const val RECOVERY_TOOLS = "recovery_tools"

    // Onboarding
    const val GENDER_SELECTION = "gender_selection"
    const val SYSTEM_JOURNAL = "system_journal"
}

/**
 * Hosts the Genesis navigation graph by registering each route and wiring navigation callbacks to their screens.
 *
 * The NavHost maps GenesisRoutes to their corresponding composable screens and handles navigation between them.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenesisNavigationHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = GenesisRoutes.GATES, // Default to Gate Carousel
) {
    // Wrap NavHost with system bar padding to avoid status bar overlap
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        NavHost(navController = navController, startDestination = startDestination) {
            // ✅ MAIN GATE MENU - Use GateNavigationScreen (the actual working system)
            composable(GenesisRoutes.GATES) {
                GateNavigationScreen(navController = navController)
            }
            composable(GenesisRoutes.AGENT_NEXUS) { AgentNexusScreen() }
            composable(GenesisRoutes.CONFERENCE_ROOM) {
                ConferenceRoomScreen(
                    onNavigateToChat = { navController.navigate(GenesisRoutes.AI_CHAT) },
                    onNavigateToAgents = {}
                )
            }
            composable(GenesisRoutes.AI_CHAT) { AIChatScreen() }

            // Gate routes with REAL screens
            composable(GenesisRoutes.AGENT_HUB) {
                AgentHubScreen(navController = navController)
            }
            composable(GenesisRoutes.AURAS_LAB) {
                AuraLabScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable(GenesisRoutes.ORACLE_DRIVE) {
                OracleDriveScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable(GenesisRoutes.ROM_TOOLS) {
                ROMToolsSubmenuScreen(navController = navController)
            }
            composable("root_tools") {
                ROMToolsSubmenuScreen(navController = navController)
            }
            composable("root_tools_toggles") {
                RootToolsTogglesScreen(navController = navController)
            }
            composable(GenesisRoutes.ROOT_ACCESS) {
                // Root Access uses RootToolsScreen for advanced system root management
                RootToolsScreen(navController = navController)
            }
            composable(GenesisRoutes.AGENT_HUB) {
                AgentHubSubmenuScreen(navController = navController)
            }
            composable(GenesisRoutes.LSPOSED_GATE) {
                LSPosedSubmenuScreen(navController = navController)
            }
            composable("xposed_panel") {
                XposedQuickAccessPanel(onNavigateBack = { navController.popBackStack() })
            }
            composable(GenesisRoutes.HELP_DESK) {
                HelpDeskSubmenuScreen(navController = navController)
            }
            composable(GenesisRoutes.COLLAB_CANVAS) {
                CollabCanvasScreen(navController = navController, onNavigateBack = { navController.popBackStack() })
            }
            composable("collab_canvas") {
                CollabCanvasScreen(navController = navController, onNavigateBack = { navController.popBackStack() })
            }
            composable(GenesisRoutes.CHROMA_CORE) {
                UIUXGateSubmenuScreen(navController = navController)
            }
            composable("chroma_core") {
                UIUXGateSubmenuScreen(navController = navController)
            }
            composable("chromacore_colors") {
                ChromaCoreColorsScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable(GenesisRoutes.FIREWALL) {
                FirewallScreen()
            }
            composable(GenesisRoutes.SPHERE_GRID) {
                SphereGridScreen(navController = navController)
            }
            composable("sphere_grid") {
                SphereGridScreen(navController = navController)
            }
            composable("code_assist") {
                CodeAssistScreen(navController = navController)
            }
            composable(GenesisRoutes.TERMINAL) {
                TerminalScreen()
            }
            composable("terminal") {
                TerminalScreen()
            }
            composable("uiux_design_studio") {
                UIUXGateSubmenuScreen(navController = navController)
            }

            // Missing routes from submenu screens
            composable("agent_nexus") {
                AgentNexusScreen()
            }
            composable("task_assignment") {
                TaskAssignmentScreen()
            }
            composable("agent_monitoring") {
                AgentMonitoringScreen()
            }
            composable("fusion_mode") {
                FusionModeScreen()
            }

            // Additional missing routes from submenu screens - using real implementations
            composable("theme_engine") {
                ThemeEngineSubmenuScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable(GenesisRoutes.GYROSCOPE_CUSTOMIZATION) {
                GyroscopeCustomizationScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable("quick_settings") {
                QuickSettingsScreen()
            }
            composable("notch_bar") {
                NotchBarScreen()
            }
            composable("overlay_menus") {
                OverlayMenusScreen()
            }
            composable("status_bar") {
                StatusBarScreen()
            }
            composable("rom_flasher") {
                ROMFlasherScreen()
            }
            composable("module_manager_lsposed") {
                LSPosedModuleManagerScreen()
            }
            composable("quick_actions") {
                QuickActionsScreen()
            }

            // DOCUMENTATION & HELP ROUTES (for HelpDesk submenu)
            composable("documentation") {
                dev.aurakai.auraframefx.ui.gates.DocumentationScreen { navController.popBackStack() }
            }
            composable("faq_browser") {
                dev.aurakai.auraframefx.ui.gates.FAQBrowserScreen { navController.popBackStack() }
            }
            composable("tutorial_videos") {
                dev.aurakai.auraframefx.ui.gates.TutorialVideosScreen { navController.popBackStack() }
            }
            composable("live_support_chat") {
                val viewModel = hiltViewModel<SupportChatViewModel>()
                with(viewModel) {
                    LiveSupportChatScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
            composable("direct_chat") {
                val viewModel = hiltViewModel<AgentViewModel>()
                with(viewModel) {
                    DirectChatScreen { navController.popBackStack() }
                }
            }

            // ROM TOOLS SUBMENU ROUTES
            composable("recovery_tools") {
                dev.aurakai.auraframefx.ui.gates.RecoveryToolsScreen { navController.popBackStack() }
            }
            composable("bootloader_manager") {
                dev.aurakai.auraframefx.ui.gates.BootloaderManagerScreen { navController.popBackStack() }
            }
            composable("live_rom_editor") {
                dev.aurakai.auraframefx.ui.gates.LiveROMEditorScreen { navController.popBackStack() }
            }

            // LSPOSED SUBMENU ROUTES
            composable("hook_manager") {
                dev.aurakai.auraframefx.ui.gates.HookManagerScreen { navController.popBackStack() }
            }
            composable("module_creation") {
                dev.aurakai.auraframefx.ui.gates.ModuleCreationScreen { navController.popBackStack() }
            }
            composable("module_manager") {
                // Use the existing ModuleManagerScreen implementation
                dev.aurakai.auraframefx.ui.gates.ModuleManagerScreen()
            }
            composable("system_overrides") {
                dev.aurakai.auraframefx.ui.gates.SystemOverridesScreen { navController.popBackStack() }
            }
            composable("logs_viewer") {
                dev.aurakai.auraframefx.ui.gates.LogsViewerScreen { navController.popBackStack() }
            }

            // SENTINEL'S FORTRESS SUBMENU ROUTES (Security features)
            composable("vpn_manager") {
                dev.aurakai.auraframefx.aura.ui.VPNManagerScreen()
            }
            composable("security_scanner") {
                dev.aurakai.auraframefx.aura.ui.SecurityScannerScreen()
            }
            composable("device_optimizer") {
                dev.aurakai.auraframefx.aura.ui.DeviceOptimizerScreen()
            }
            composable("privacy_guard") {
                dev.aurakai.auraframefx.aura.ui.PrivacyGuardScreen()
            }

            // USER PREFERENCES AND AUTHENTICATION
            composable("user_preferences") {
                SystemJournalScreen(navController = navController)
            }
            composable(GenesisRoutes.SYSTEM_JOURNAL) {
                SystemJournalScreen(navController = navController)
            }
            composable("login") {
                val returnDestination = it.arguments?.getString("returnTo")
                LoginScreen(
                    navController = navController,
                    returnDestination = returnDestination
                )
            }

            // ADDITIONAL AURA CREATIVE SCREENS
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

            // AGENT ADVANCEMENT & EVOLUTION
            composable(GenesisRoutes.AGENT_ADVANCEMENT) {
                dev.aurakai.auraframefx.aura.ui.AgentAdvancementScreen(onBack = { navController.popBackStack() })
            }
            composable(GenesisRoutes.EVOLUTION_TREE) {
                EvolutionTreeScreen()
            }
            composable(GenesisRoutes.CONSCIOUSNESS_VISUALIZER) {
                ConsciousnessVisualizerScreen()
            }

            // KAI SECURITY SCREENS
            composable(GenesisRoutes.SECURE_COMM) {
                SecureCommScreen()
            }

            // ONBOARDING
            composable(GenesisRoutes.GENDER_SELECTION) {
                GenderSelectionScreen(
                    onSelectionComplete = { _ ->
                        // Navigate to main app after selection
                        navController.navigate(GenesisRoutes.GATES) {
                            popUpTo(GenesisRoutes.GENDER_SELECTION) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
