package dev.aurakai.auraframefx.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.aurakai.auraframefx.ui.gates.AgentHubSubmenuScreen
import dev.aurakai.auraframefx.ui.gates.AgentMonitoringScreen
import dev.aurakai.auraframefx.ui.gates.AurasLabScreen
import dev.aurakai.auraframefx.ui.gates.BootloaderManagerScreen
import dev.aurakai.auraframefx.ui.gates.CodeAssistScreen
import dev.aurakai.auraframefx.ui.gates.DocumentationScreen
import dev.aurakai.auraframefx.ui.gates.FAQBrowserScreen
import dev.aurakai.auraframefx.ui.gates.FusionModeScreen
import dev.aurakai.auraframefx.ui.gates.GateNavigationScreen
import dev.aurakai.auraframefx.ui.gates.HelpDeskSubmenuScreen
import dev.aurakai.auraframefx.ui.gates.HookManagerScreen
import dev.aurakai.auraframefx.ui.gates.LSPosedModuleManagerScreen
import dev.aurakai.auraframefx.ui.gates.LSPosedSubmenuScreen
import dev.aurakai.auraframefx.ui.gates.LiveROMEditorScreen
import dev.aurakai.auraframefx.ui.gates.LogsViewerScreen
import dev.aurakai.auraframefx.ui.gates.ModuleCreationScreen
import dev.aurakai.auraframefx.ui.gates.ModuleManagerScreen
import dev.aurakai.auraframefx.ui.gates.NotchBarScreen
import dev.aurakai.auraframefx.ui.gates.OracleDriveSubmenuScreen
import dev.aurakai.auraframefx.ui.gates.OverlayMenusScreen
import dev.aurakai.auraframefx.ui.gates.QuickActionsScreen
import dev.aurakai.auraframefx.ui.gates.QuickSettingsScreen
import dev.aurakai.auraframefx.ui.gates.ROMFlasherScreen
import dev.aurakai.auraframefx.ui.gates.ROMToolsSubmenuScreen
import dev.aurakai.auraframefx.ui.gates.RecoveryToolsScreen
import dev.aurakai.auraframefx.ui.gates.SphereGridScreen
import dev.aurakai.auraframefx.ui.gates.StatusBarScreen
import dev.aurakai.auraframefx.ui.gates.SystemOverridesScreen
import dev.aurakai.auraframefx.ui.gates.TaskAssignmentScreen
import dev.aurakai.auraframefx.ui.gates.ThemeEngineScreen
import dev.aurakai.auraframefx.ui.gates.TutorialVideosScreen
import dev.aurakai.auraframefx.ui.gates.UIUXGateSubmenuScreen
import dev.aurakai.auraframefx.ui.screens.AgentProfileScreen
import dev.aurakai.auraframefx.ui.screens.EcosystemMenuScreen
import dev.aurakai.auraframefx.ui.screens.HolographicMenuScreen
import dev.aurakai.auraframefx.ui.screens.IntroScreen
import dev.aurakai.auraframefx.ui.screens.JournalPDAScreen
import dev.aurakai.auraframefx.ui.screens.MainScreen
import dev.aurakai.auraframefx.ui.screens.UISettingsScreen
import dev.aurakai.auraframefx.ui.screens.WorkingLabScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.aurakai.auraframefx.ui.gates.LiveSupportChatScreen
import dev.aurakai.auraframefx.ui.gates.SupportChatViewModel
import dev.aurakai.auraframefx.ui.customization.ComponentEditor
import dev.aurakai.auraframefx.ui.customization.ZOrderEditor
import dev.aurakai.auraframefx.ui.identity.GenderSelectionNavigator

/**
 * Main navigation graph for the AuraFrameFX app
 * All 90+ screens properly wired and functional
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = ReGenesisNavHost.HomeGateCarousel.route
    ) {
        // ==================== MAIN SCREENS ====================

        composable(route = ReGenesisNavHost.HomeGateCarousel.route) {
            dev.aurakai.auraframefx.aura.ui.HomeScreen(
                onNavigateToModule = { moduleId ->
                    val route = dev.aurakai.auraframefx.ui.gates.allGates.find { it.moduleId == moduleId }?.route
                    if (route != null) {
                        navController.navigate(route)
                    }
                }
            )
        }

        composable(route = ReGenesisNavHost.IntroSequence.route) {
            IntroScreen(onIntroComplete = { 
                navController.navigate(ReGenesisNavHost.HomeGateCarousel.route) {
                    popUpTo(ReGenesisNavHost.IntroSequence.route) { inclusive = true }
                }
            })
        }

        composable(route = ReGenesisNavHost.AgentNexusHub.route) {
            AgentHubSubmenuScreen(navController = navController)
        }

        composable(route = ReGenesisNavHost.TaskAssignment.route) {
            TaskAssignmentScreen()
        }

        composable(route = ReGenesisNavHost.AgentMonitoring.route) {
            AgentMonitoringScreen()
        }

        composable(route = ReGenesisNavHost.FusionMode.route) {
            FusionModeScreen()
        }

        composable(route = ReGenesisNavHost.CodeAssist.route) {
            CodeAssistScreen(navController = navController)
        }

        // ==================== ORACLE DRIVE ====================

        composable(route = ReGenesisNavHost.OracleDriveHub.route) {
            // Oracle Drive contains GenesisNavigation (nested NavHost)
            // This provides access to all Genesis root/system screens
            GenesisNavigation()
        }

        composable(route = ReGenesisNavHost.SphereGrid.route) {
            SphereGridScreen(navController = navController)
        }

        // ==================== ROM TOOLS ====================

        composable(route = ReGenesisNavHost.RomToolsHub.route) {
            ROMToolsSubmenuScreen(navController = navController)
        }

        composable(route = ReGenesisNavHost.LiveROMEditor.route) {
            LiveROMEditorScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = ReGenesisNavHost.ROMFlasher.route) {
            ROMFlasherScreen()
        }

        composable(route = ReGenesisNavHost.RecoveryTools.route) {
            RecoveryToolsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = ReGenesisNavHost.Bootloader.route) {
            BootloaderManagerScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ==================== LSPOSED INTEGRATION ====================

        composable(route = ReGenesisNavHost.LSPosedHub.route) {
            LSPosedSubmenuScreen(navController = navController)
        }

        composable(route = ReGenesisNavHost.ModuleManager.route) {
            ModuleManagerScreen()
        }

        composable(route = ReGenesisNavHost.SovereignModuleManager.route) {
            LSPosedModuleManagerScreen()
        }

        composable(route = ReGenesisNavHost.ModuleCreation.route) {
            ModuleCreationScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = ReGenesisNavHost.HookManager.route) {
            HookManagerScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = ReGenesisNavHost.LogsViewer.route) {
            LogsViewerScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ==================== UI/UX DESIGN STUDIO ====================

        composable(route = ReGenesisNavHost.AuraThemingHub.route) {
            UIUXGateSubmenuScreen(navController = navController)
        }

        composable(route = ReGenesisNavHost.ThemeEngine.route) {
            ThemeEngineScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = ReGenesisNavHost.StatusBar.route) {
            StatusBarScreen()
        }

        composable(route = ReGenesisNavHost.NotchBar.route) {
            NotchBarScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = ReGenesisNavHost.QuickSettings.route) {
            QuickSettingsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = ReGenesisNavHost.SystemOverrides.route) {
            SystemOverridesScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ==================== HELP DESK ====================

        composable(route = ReGenesisNavHost.HelpDesk.route) {
            HelpDeskSubmenuScreen(navController = navController)
        }

        composable(route = ReGenesisNavHost.LiveSupportChat.route) {
            val viewModel = hiltViewModel<SupportChatViewModel>()
            with(viewModel) {
                LiveSupportChatScreen(onNavigateBack = { navController.popBackStack() })
            }
        }

        composable(route = ReGenesisNavHost.Documentation.route) {
            DocumentationScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = ReGenesisNavHost.FAQBrowser.route) {
            FAQBrowserScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = ReGenesisNavHost.TutorialVideos.route) {
            TutorialVideosScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ==================== AURA'S LAB ====================

        composable(route = ReGenesisNavHost.AuraLab.route) {
            AurasLabScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ==================== IDENTITY & ONBOARDING ====================

        composable(route = ReGenesisNavHost.GenderSelection.route) {
            GenderSelectionNavigator(
                onGenderSelected = { gender ->
                    // TODO: Save gender preference and navigate to next onboarding step
                    navController.popBackStack()
                }
            )
        }
    }
}
