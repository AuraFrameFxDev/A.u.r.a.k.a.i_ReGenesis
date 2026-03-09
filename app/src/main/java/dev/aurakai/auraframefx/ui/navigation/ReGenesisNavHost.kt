package dev.aurakai.auraframefx.ui.navigation

// Domain Screens

// Aura Module Screens

// Gate/Hub Screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.aurakai.auraframefx.customization.CustomizationViewModel
import dev.aurakai.auraframefx.navigation.NavDestination
import dev.aurakai.auraframefx.navigation.auraCustomizationNavigation
import dev.aurakai.auraframefx.ui.gates.AgentMonitoringScreen
import dev.aurakai.auraframefx.ui.gates.AgentNexusHubScreen
import dev.aurakai.auraframefx.ui.gates.ArkBuildScreen
import dev.aurakai.auraframefx.ui.gates.AuraLabScreen
import dev.aurakai.auraframefx.ui.gates.AuraThemingHubScreen
import dev.aurakai.auraframefx.ui.gates.ConstellationScreen
import dev.aurakai.auraframefx.ui.gates.FusionModeScreen
import dev.aurakai.auraframefx.ui.gates.HelpDeskScreen
import dev.aurakai.auraframefx.ui.gates.KaiSentinelHubScreen
import dev.aurakai.auraframefx.ui.gates.OracleDriveHubScreen
import dev.aurakai.auraframefx.ui.gates.SovereignClaudeScreen
import dev.aurakai.auraframefx.ui.gates.SovereignGeminiScreen
import dev.aurakai.auraframefx.ui.gates.SovereignMetaInstructScreen
import dev.aurakai.auraframefx.ui.gates.SovereignNemotronScreen
import dev.aurakai.auraframefx.ui.gates.SphereGridScreen
import dev.aurakai.auraframefx.ui.gates.TaskAssignmentScreen

// ... other imports

@Composable
fun ReGenesisNavHost(
    navController: NavHostController,
    customizationViewModel: CustomizationViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        customizationViewModel.start(context)
    }

    NavHost(
        navController = navController,
        startDestination = NavDestination.HomeGateCarousel.route
    ) {

        // ═══════════════════════════════════════════════════════════════
        // LEVEL 1: EXODUS HUD (The 5 Gate Carousel)
        // ═══════════════════════════════════════════════════════════════
        composable(NavDestination.HomeGateCarousel.route) {
            ExodusHUD(navController = navController)
        }

        // --- LEVEL 2 PIXEL WORKSPACES ---
        composable(
            "pixel_domain/{id}",
            arguments = listOf(androidx.navigation.navArgument("id") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: "01"
            val route = SovereignRouter.getById(id)

            if (route != null) {
                PixelWorkspaceScreen(
                    title = route.title,
                    imagePaths = listOf(route.pixelArtPath),
                    onBack = { navController.popBackStack() }
                )
            } else {
                navController.popBackStack()
            }
        }

        composable("workspace_kai") {
            // KaiRootWorkspaceScreen not found - redirecting to PixelWorkspace for now
            PixelWorkspaceScreen(
                title = "KAI'S SENTINEL FORTRESS",
                imagePaths = listOf(
                    "file:///sdcard/Pictures/Screenshots/IMG_20260128_142431.png"
                ),
                onBack = { navController.popBackStack() }
            )
        }

        composable("workspace_aura") {
            PixelWorkspaceScreen(
                title = "AURA'S DESIGN STUDIO",
                imagePaths = listOf(
                    "file:///sdcard/Pictures/Screenshots/IMG_20260128_142213.png",
                    "file:///sdcard/Pictures/Screenshots/IMG_20260128_142302.png"
                ),
                onBack = { navController.popBackStack() }
            )
        }

        composable("workspace_genesis") {
            PixelWorkspaceScreen(
                title = "GENESIS ARCHITECTURE HUB",
                imagePaths = listOf(
                    "file:///sdcard/Pictures/Screenshots/IMG_20260128_142126.png"
                ),
                onBack = { navController.popBackStack() }
            )
        }

        // ═══════════════════════════════════════════════════════════════
        // LEVEL 2: MAIN DOMAIN HUBS
        // ═══════════════════════════════════════════════════════════════

        composable(NavDestination.AuraThemingHub.route) {
            AuraThemingHubScreen(navController = navController)
        }

        composable(NavDestination.RomToolsHub.route) {
            KaiSentinelHubScreen(navController = navController)
        }

        composable(NavDestination.OracleDriveHub.route) {
            OracleDriveHubScreen(navController = navController)
        }

        composable(NavDestination.AgentNexusHub.route) {
            AgentNexusHubScreen(navController = navController)
        }

        composable(NavDestination.HelpDesk.route) {
            HelpDeskScreen(navController = navController)
        }

        // ═══════════════════════════════════════════════════════════════
        // LEVEL 3 & 4: TOOL SCREENS & SUB-MODULES
        // ═══════════════════════════════════════════════════════════════

        composable(NavDestination.FusionMode.route) {
            FusionModeScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.TaskAssignment.route) {
            TaskAssignmentScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.ArkBuild.route) {
            ArkBuildScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.MetaInstruct.route) {
            SovereignMetaInstructScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.AgentMonitoring.route) {
            AgentMonitoringScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.Constellation.route) {
            ConstellationScreen(navController = navController)
        }
        composable(NavDestination.SphereGrid.route) {
            SphereGridScreen(navController = navController)
        }
        composable(NavDestination.Nemotron.route) {
            SovereignNemotronScreen(navController = navController)
        }
        composable(NavDestination.Claude.route) {
            SovereignClaudeScreen(navController = navController)
        }
        composable(NavDestination.Gemini.route) {
            SovereignGeminiScreen(navController = navController)
        }

        composable(NavDestination.SwarmMonitor.route) {
            dev.aurakai.auraframefx.domains.nexus.screens.AgentSwarmScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.BenchmarkMonitor.route) {
            dev.aurakai.auraframefx.domains.nexus.screens.BenchmarkMonitorScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.AgentCreation.route) {
            dev.aurakai.auraframefx.domains.nexus.screens.AgentCreationScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(NavDestination.AuraLab.route) {
            AuraLabScreen(onNavigateBack = { navController.popBackStack() })
        }

        // --- LEVEL 3: AURA TOOLS ---
        composable(NavDestination.NotchBar.route) {
            dev.aurakai.auraframefx.ui.gates.NotchBarScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.StatusBar.route) {
            dev.aurakai.auraframefx.ui.gates.StatusBarScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.QuickSettings.route) {
            dev.aurakai.auraframefx.ui.gates.QuickSettingsScreen(onNavigateBack = { navController.popBackStack() })
        }

        // --- LEVEL 3: KAI TOOLS ---
        composable(NavDestination.ROMFlasher.route) {
            dev.aurakai.auraframefx.ui.gates.ROMFlasherScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.Bootloader.route) {
            dev.aurakai.auraframefx.ui.gates.BootloaderManagerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.ModuleManager.route) {
            dev.aurakai.auraframefx.ui.gates.ModuleManagerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.RecoveryTools.route) {
            dev.aurakai.auraframefx.ui.gates.RecoveryToolsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.SecurityCenter.route) {
            dev.aurakai.auraframefx.ui.gates.SovereignShieldScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.LSPosedHub.route) {
            dev.aurakai.auraframefx.ui.gates.LSPosedSubmenuScreen(navController = navController)
        }

        // --- LEVEL 3: GENESIS TOOLS ---
        composable(NavDestination.CodeAssist.route) {
            // Reusing AppBuilder or similar if dedicated screen missing
            dev.aurakai.auraframefx.domains.aura.screens.AppBuilderScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.NeuralNetwork.route) {
            dev.aurakai.auraframefx.ui.gates.NeuralArchiveScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.AgentBridgeHub.route) {
            dev.aurakai.auraframefx.ui.gates.AgentBridgeHubScreen(navController = navController)
        }
        composable(NavDestination.OracleCloudStorage.route) {
            dev.aurakai.auraframefx.ui.gates.OracleCloudInfiniteStorageScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.Terminal.route) {
            // Terminal not found in gates list - redirecting to nexus monitor
            dev.aurakai.auraframefx.ui.gates.AgentMonitoringScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.ConferenceRoom.route) {
            dev.aurakai.auraframefx.domains.aura.screens.ConferenceRoomScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.InterfaceForge.route) {
            dev.aurakai.auraframefx.domains.aura.screens.AppBuilderScreen(onNavigateBack = { navController.popBackStack() })
        }

        // --- LEVEL 3: HELP & SUPPORT ---
        composable(NavDestination.HelpDeskSubmenu.route) {
            dev.aurakai.auraframefx.ui.gates.HelpDeskSubmenuScreen(navController = navController)
        }
        composable(NavDestination.DirectChat.route) {
            dev.aurakai.auraframefx.domains.aura.screens.DirectChatScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.Documentation.route) {
            dev.aurakai.auraframefx.domains.aura.screens.DocumentationScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.FAQBrowser.route) {
            dev.aurakai.auraframefx.domains.aura.screens.FAQBrowserScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.TutorialVideos.route) {
            dev.aurakai.auraframefx.domains.aura.screens.TutorialVideosScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ═══════════════════════════════════════════════════════════════
        // SUB-GRAPHS (Integrated Third-Party Modules)
        // ═══════════════════════════════════════════════════════════════
        auraCustomizationNavigation(
            navController = navController,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}
