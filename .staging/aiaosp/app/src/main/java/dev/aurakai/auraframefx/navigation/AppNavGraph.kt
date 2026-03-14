package dev.aurakai.auraframefx.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.aurakai.auraframefx.screens.HomeScreen
import dev.aurakai.auraframefx.ui.customization.AnimationType
import dev.aurakai.auraframefx.ui.customization.ComponentEditor
import dev.aurakai.auraframefx.ui.customization.ComponentType
import dev.aurakai.auraframefx.ui.customization.UIComponent
import dev.aurakai.auraframefx.ui.customization.ZOrderEditor
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
import dev.aurakai.auraframefx.ui.gates.LiveSupportChatScreen
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
import dev.aurakai.auraframefx.ui.gates.SphereGridScreen
import dev.aurakai.auraframefx.ui.gates.StatusBarScreen
import dev.aurakai.auraframefx.ui.gates.SystemOverridesScreen
import dev.aurakai.auraframefx.ui.gates.TaskAssignmentScreen
import dev.aurakai.auraframefx.ui.gates.ThemeEngineScreen
import dev.aurakai.auraframefx.ui.gates.TutorialVideosScreen
import dev.aurakai.auraframefx.ui.gates.UIUXGateSubmenuScreen
import dev.aurakai.auraframefx.ui.identity.GenderSelectionNavigator
import dev.aurakai.auraframefx.ui.screens.AgentProfileScreen
import dev.aurakai.auraframefx.ui.screens.EcosystemMenuScreen
import dev.aurakai.auraframefx.ui.screens.HolographicMenuScreen
import dev.aurakai.auraframefx.ui.screens.IntroScreen
import dev.aurakai.auraframefx.ui.screens.JournalPDAScreen
import dev.aurakai.auraframefx.ui.screens.MainScreen
import dev.aurakai.auraframefx.ui.screens.UISettingsScreen
import dev.aurakai.auraframefx.ui.screens.WorkingLabScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavDestination.Gates.route
    ) {
        // ==================== MAIN SCREENS ====================

        composable(route = NavDestination.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(route = NavDestination.Gates.route) {
            GateNavigationScreen(navController = navController)
        }

        composable(route = NavDestination.JournalPDA.route) {
            JournalPDAScreen(navController = navController)
        }

        composable(route = NavDestination.IntroScreen.route) {
            IntroScreen(onIntroComplete = {
                navController.navigate(NavDestination.Gates.route) {
                    popUpTo(NavDestination.IntroScreen.route) { inclusive = true }
                }
            })
        }

        composable(route = NavDestination.MainScreen.route) {
            MainScreen(
                onNavigateToAgentNexus = { navController.navigate(NavDestination.AgentHub.route) },
                onNavigateToOracleDrive = { navController.navigate(NavDestination.OracleDrive.route) },
                onNavigateToSettings = { navController.navigate(NavDestination.UISettings.route) }
            )
        }

        composable(route = NavDestination.WorkingLab.route) {
            WorkingLabScreen(onNavigate = { route -> navController.navigate(route) })
        }

        composable(route = NavDestination.AgentProfile.route) {
            AgentProfileScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = NavDestination.EcosystemMenu.route) {
            EcosystemMenuScreen()
        }

        composable(route = NavDestination.HolographicMenu.route) {
            HolographicMenuScreen(onNavigate = { route -> navController.navigate(route) })
        }

        composable(route = NavDestination.UISettings.route) {
            UISettingsScreen(navController = navController)
        }

        // ==================== AGENT HUB ====================

        composable(route = NavDestination.AgentHub.route) {
            AgentHubSubmenuScreen(navController = navController)
        }

        composable(route = NavDestination.TaskAssignment.route) {
            TaskAssignmentScreen()
        }

        composable(route = NavDestination.AgentMonitoring.route) {
            AgentMonitoringScreen()
        }

        composable(route = NavDestination.FusionMode.route) {
            FusionModeScreen()
        }

        composable(route = NavDestination.CodeAssist.route) {
            CodeAssistScreen(navController = navController)
        }

        // ==================== ORACLE DRIVE ====================

        composable(route = NavDestination.OracleDrive.route) {
            GenesisNavigationHost()
        }

        composable(route = NavDestination.SphereGrid.route) {
            SphereGridScreen(navController = navController)
        }

        // ==================== ROM TOOLS ====================

        composable(route = NavDestination.ROMTools.route) {
            ROMToolsSubmenuScreen(navController = navController)
        }

        composable(route = NavDestination.LiveROMEditor.route) {
            LiveROMEditorScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = NavDestination.ROMFlasher.route) {
            ROMFlasherScreen()
        }

        composable(route = NavDestination.RecoveryTools.route) {
            RecoveryToolsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = NavDestination.BootloaderManager.route) {
            BootloaderManagerScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ==================== LSPOSED INTEGRATION ====================

        composable(route = NavDestination.LSPosedGate.route) {
            LSPosedSubmenuScreen(navController = navController)
        }

        composable(route = NavDestination.ModuleManager.route) {
            ModuleManagerScreen()
        }

        composable(route = NavDestination.LSPosedModuleManager.route) {
            LSPosedModuleManagerScreen()
        }

        composable(route = NavDestination.ModuleCreation.route) {
            ModuleCreationScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = NavDestination.HookManager.route) {
            HookManagerScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = NavDestination.LogsViewer.route) {
            LogsViewerScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ==================== UI/UX DESIGN STUDIO ====================

        composable(route = NavDestination.UIUXDesignStudio.route) {
            UIUXGateSubmenuScreen(navController = navController)
        }

        composable(route = NavDestination.ThemeEngine.route) {
            ThemeEngineScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = NavDestination.StatusBar.route) {
            StatusBarScreen()
        }

        composable(route = NavDestination.NotchBar.route) {
            NotchBarScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = NavDestination.QuickSettings.route) {
            QuickSettingsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = NavDestination.OverlayMenus.route) {
            OverlayMenusScreen()
        }

        composable(route = NavDestination.QuickActions.route) {
            QuickActionsScreen()
        }

        composable(route = NavDestination.SystemOverrides.route) {
            SystemOverridesScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ==================== HELP DESK ====================

        composable(route = NavDestination.HelpDesk.route) {
            HelpDeskSubmenuScreen(navController = navController)
        }

        composable(route = NavDestination.LiveSupport.route) {
            // Note: LiveSupportChatScreen requires SupportChatViewModel context
            // You'll need to provide it via context receiver or pass as parameter
            LiveSupportChatScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = NavDestination.Documentation.route) {
            DocumentationScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = NavDestination.FAQBrowser.route) {
            FAQBrowserScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(route = NavDestination.TutorialVideos.route) {
            TutorialVideosScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ==================== AURA'S LAB ====================

        composable(route = NavDestination.AurasLab.route) {
            AurasLabScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ==================== CUSTOMIZATION TOOLS ====================

        composable(route = NavDestination.ComponentEditor.route) {
            // Recommendation: If you have a state-holder for the selected component, use it here
            ComponentEditor(
                component = UIComponent(
                    id = "current",
                    name = "Editor",
                    type = ComponentType.STATUS_BAR,
                    height = 50f,
                    backgroundColor = Color.White,
                    animationType = AnimationType.NONE,
                ),
                onUpdate = { },
                onClose = { navController.popBackStack() }
            )
        }

        composable(route = NavDestination.ZOrderEditor.route) {
            ZOrderEditor(
                elements = emptyList(),
                onReorder = { },
                onElementSelected = { },
                onClose = { navController.popBackStack() }
            )
        }

        // ==================== IDENTITY ====================

        composable(route = NavDestination.GenderSelection.route) {
            GenderSelectionNavigator(
                onGenderSelected = { navController.popBackStack() }
            )
        }
    }
}
