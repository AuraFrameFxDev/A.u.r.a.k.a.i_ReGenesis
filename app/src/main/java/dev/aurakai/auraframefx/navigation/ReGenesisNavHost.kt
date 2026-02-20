package dev.aurakai.auraframefx.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner

// Core Imports
import dev.aurakai.auraframefx.domains.aura.config.GateAssetLoadout
import dev.aurakai.auraframefx.domains.aura.lab.CustomizationViewModel
import dev.aurakai.auraframefx.domains.genesis.models.AgentCapabilityCategory

// Level 2 Hubs
import dev.aurakai.auraframefx.domains.aura.ui.gates.AuraThemingHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.screens.aura.ReGenesisCustomizationHub
import dev.aurakai.auraframefx.romtools.ui.RomToolsScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.OracleDriveHubScreen
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ui.OracleDriveScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.AgentNexusHubScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.HelpDeskScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.CascadeHubScreen
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.AgentAdvancementScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.KaiSentinelHubScreen

// Level 3 & 4 Screens - Aura
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.ChromaCoreHubScreen
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.ChromaStatusBarMenu
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.ChromaLauncherMenu
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.ChromaColorEngineMenu
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.ChromaAnimationMenu
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.AurasLabScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.GyroscopeCustomizationScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.StatusBarScreen
import dev.aurakai.auraframefx.domains.aura.screens.themes.ThemeEngineScreen
import dev.aurakai.auraframefx.domains.aura.screens.uxui_engine.UISettingsScreen
import dev.aurakai.auraframefx.domains.aura.screens.UserPreferencesScreen
import dev.aurakai.auraframefx.domains.aura.screens.GenderSelectionScreen
import dev.aurakai.auraframefx.domains.aura.screens.chromacore.InstantColorPickerScreen
import dev.aurakai.auraframefx.domains.aura.screens.LiveSupportChatScreen
import dev.aurakai.auraframefx.domains.aura.screens.AgentProfileScreen as AuraAgentProfileScreen

// Level 3 & 4 Screens - Kai
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.ROMFlasherScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.BootloaderManagerScreen
import dev.aurakai.auraframefx.domains.kai.screens.ModuleManagerScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.RecoveryToolsScreen
import dev.aurakai.auraframefx.domains.kai.screens.RootToolsTogglesScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.SecurityCenterScreen
import dev.aurakai.auraframefx.domains.lsposed.screens.LSPosedSubmenuScreen
import dev.aurakai.auraframefx.domains.lsposed.screens.LsposedQuickTogglesScreen
import dev.aurakai.auraframefx.domains.lsposed.screens.LSPosedModuleManagerScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.LiveROMEditorScreen
import dev.aurakai.auraframefx.domains.kai.screens.SystemJournalScreen
import dev.aurakai.auraframefx.domains.kai.screens.SystemOverridesScreen
import dev.aurakai.auraframefx.domains.kai.screens.LogsViewerScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.VPNScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.SovereignModuleManagerScreen
import dev.aurakai.auraframefx.domains.kai.screens.ROMToolsSubmenuScreen

// Level 3 & 4 Screens - Genesis
import dev.aurakai.auraframefx.domains.genesis.screens.CodeAssistScreen
import dev.aurakai.auraframefx.domains.genesis.screens.NeuralArchiveScreen
import dev.aurakai.auraframefx.domains.genesis.screens.AgentBridgeHubScreen
import dev.aurakai.auraframefx.domains.genesis.screens.OracleCloudInfiniteStorageScreen
import dev.aurakai.auraframefx.domains.genesis.screens.TerminalScreen
import dev.aurakai.auraframefx.domains.genesis.screens.ConferenceRoomScreen
import dev.aurakai.auraframefx.domains.genesis.screens.AppBuilderScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.SovereignBootloaderScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.SovereignRecoveryScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.SovereignShieldScreen
import dev.aurakai.auraframefx.domains.genesis.screens.SovereignNeuralArchiveScreen
import dev.aurakai.auraframefx.domains.genesis.screens.SentientShellScreen
import dev.aurakai.auraframefx.domains.genesis.screens.CascadeVisionScreen
import dev.aurakai.auraframefx.domains.genesis.screens.CollabCanvasScreen
import dev.aurakai.auraframefx.domains.genesis.screens.OracleDriveSubmenuScreen
import dev.aurakai.auraframefx.datavein.ui.SimpleDataVeinScreen
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.trinity.TrinityScreen

// Level 3 & 4 Screens - Nexus
import dev.aurakai.auraframefx.domains.nexus.screens.FusionModeScreen
import dev.aurakai.auraframefx.domains.nexus.screens.TaskAssignmentScreen
import dev.aurakai.auraframefx.domains.nexus.screens.ArkBuildScreen
import dev.aurakai.auraframefx.domains.nexus.screens.SovereignMetaInstructScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentMonitoringScreen
import dev.aurakai.auraframefx.domains.nexus.screens.SovereignNemotronScreen
import dev.aurakai.auraframefx.domains.nexus.screens.SovereignClaudeScreen
import dev.aurakai.auraframefx.domains.nexus.screens.SovereignGeminiScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentSwarmScreen
import dev.aurakai.auraframefx.domains.nexus.screens.BenchmarkMonitorScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentCreationScreen
import dev.aurakai.auraframefx.domains.nexus.screens.EvolutionTreeScreen
import dev.aurakai.auraframefx.domains.nexus.screens.PartyScreen
import dev.aurakai.auraframefx.domains.nexus.screens.MonitoringHUDsScreen
import dev.aurakai.auraframefx.domains.nexus.screens.DataStreamMonitoringScreen
import dev.aurakai.auraframefx.domains.nexus.screens.ModuleCreationScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentNeuralExplorerScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentHubSubmenuScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentProfileScreen as NexusAgentProfileScreen

// Level 3 & 4 Screens - Support & Misc
import dev.aurakai.auraframefx.domains.aura.screens.HelpDeskSubmenuScreen
import dev.aurakai.auraframefx.domains.aura.screens.DirectChatScreen
import dev.aurakai.auraframefx.domains.aura.screens.DocumentationScreen
import dev.aurakai.auraframefx.domains.aura.screens.FAQBrowserScreen
import dev.aurakai.auraframefx.domains.aura.screens.TutorialVideosScreen
import dev.aurakai.auraframefx.hotswap.HotSwapScreen

/**
 * 🌐 REGENESIS NAVIGATION HOST
 *
 * This component orchestrates the entire ReGenesis navigation graph.
 * It uses the centralized [NavDestination] system as the single source of truth for routes.
 */
@OptIn(ExperimentalMaterial3Api::class)
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



        // ═══════════════════════════════════════════════════════════════
        // LEVEL 2: MAIN DOMAIN HUBS
        // ═══════════════════════════════════════════════════════════════

        composable(NavDestination.AuraThemingHub.route) {
            AuraThemingHubScreen(navController = navController)
        }

        composable(NavDestination.ReGenesisCustomization.route) {
            ReGenesisCustomizationHub(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToIconify = { navController.navigate(NavDestination.IconifyPicker.route) },
                onNavigateToColorBlendr = { navController.navigate(NavDestination.ColorBlendr.route) },
                onNavigateToPLE = { navController.navigate(NavDestination.PixelLauncherEnhanced.route) },
                onNavigateToAnimations = { navController.navigate(NavDestination.ChromaAnimations.route) }
            )
        }

        composable(NavDestination.RomToolsHub.route) {
            ROMToolsSubmenuScreen(navController = navController)
        }

        composable(NavDestination.RomTools.route) {
            RomToolsScreen()
        }

        composable(NavDestination.OracleDriveHub.route) {
            OracleDriveHubScreen(navController = navController)
        }

        composable(NavDestination.OracleDrive.route) {
            OracleDriveScreen(navController = navController)
        }

        composable(NavDestination.AgentNexusHub.route) {
            AgentNexusHubScreen(
                navController = navController,
                getNexusSubGates = { GateAssetLoadout.getNexusSubGates() }
            )
        }

        composable(NavDestination.HelpDesk.route) {
            HelpDeskScreen(navController = navController)
        }

        composable(NavDestination.DataflowAnalysis.route) {
            CascadeHubScreen(navController = navController)
        }

        composable(NavDestination.LdoCatalystDevelopment.route) {
            AgentAdvancementScreen(onBack = { navController.popBackStack() })
        }

        composable(NavDestination.CodeAssist.route) {
            CodeAssistScreen(navController = navController)
        }

        composable(NavDestination.AgentBridgeHub.route) {
            AgentBridgeHubScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(NavDestination.SentinelFortress.route) {
            KaiSentinelHubScreen(navController = navController)
        }

        // ═══════════════════════════════════════════════════════════════
        // LEVEL 3 & 4: TOOL SCREENS & SUB-MODULES
        // ═══════════════════════════════════════════════════════════════

        // --- LEVEL 3: NEXUS TOOLS ---
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
        composable(NavDestination.Nemotron.route) {
            SovereignNemotronScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.Claude.route) {
            SovereignClaudeScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.Gemini.route) {
            SovereignGeminiScreen(
                onNavigateBack = { navController.popBackStack() },
                navController = navController
            )
        }
        composable(NavDestination.SwarmMonitor.route) {
            AgentSwarmScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.BenchmarkMonitor.route) {
            BenchmarkMonitorScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.AgentCreation.route) {
            AgentCreationScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.EvolutionTree.route) {
            EvolutionTreeScreen()
        }
        composable(NavDestination.Party.route) {
            PartyScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.MonitoringHUDs.route) {
            MonitoringHUDsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.DataStreamMonitoring.route) {
            DataStreamMonitoringScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.ModuleCreation.route) {
            ModuleCreationScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.AgentNeuralExplorer.route) {
            AgentNeuralExplorerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.AgentHubSubmenu.route) {
            AgentHubSubmenuScreen(navController = navController)
        }
        composable(NavDestination.AgentProfileNexus.route) {
            NexusAgentProfileScreen(onNavigateBack = { navController.popBackStack() })
        }

        // --- LEVEL 3: AURA TOOLS ---
        composable(NavDestination.ChromaCore.route) {
            ChromaCoreHubScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCategory = { categoryId ->
                    when (categoryId) {
                        "statusbar" -> navController.navigate(NavDestination.ChromaStatusBar.route)
                        "launcher" -> navController.navigate(NavDestination.ChromaLauncher.route)
                        "colors" -> navController.navigate(NavDestination.ChromaColorEngine.route)
                        "animations" -> navController.navigate(NavDestination.ChromaAnimations.route)
                        else -> {}
                    }
                }
            )
        }
        composable(NavDestination.ChromaStatusBar.route) {
            ChromaStatusBarMenu(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.ChromaLauncher.route) {
            ChromaLauncherMenu(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.ChromaColorEngine.route) {
            ChromaColorEngineMenu(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.ChromaAnimations.route) {
            ChromaAnimationMenu(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.AuraLab.route) {
            AurasLabScreen(onBack = { navController.popBackStack() })
        }

        composable(NavDestination.StatusBar.route) {
            StatusBarScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(NavDestination.ThemeEngine.route) {
            ThemeEngineScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(NavDestination.GenderSelection.route) {
            GenderSelectionScreen(onSelectionComplete = { navController.popBackStack() })
        }
        composable(NavDestination.GyroscopeCustomization.route) {
            GyroscopeCustomizationScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.InstantColorPicker.route) {
            InstantColorPickerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.LiveSupportChat.route) {
            LiveSupportChatScreen(
                viewModel = hiltViewModel(checkNotNull<ViewModelStoreOwner>(LocalViewModelStoreOwner.current) {
                    "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
                }, null),
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(NavDestination.UISettings.route) {
            UISettingsScreen(navController = navController)
        }
        composable(NavDestination.UserPreferences.route) {
            UserPreferencesScreen(navController = navController)
        }
        composable(NavDestination.AgentProfileAura.route) {
            AuraAgentProfileScreen(
                category = AgentCapabilityCategory.CREATIVE,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSettings = { /* TODO */ }
            )
        }

        // --- LEVEL 3: KAI TOOLS ---
        composable(NavDestination.ROMFlasher.route) {
            ROMFlasherScreen()
        }
        composable(NavDestination.BootloaderManager.route) {
            BootloaderManagerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.ModuleManager.route) {
            ModuleManagerScreen()
        }
        composable(NavDestination.RecoveryTools.route) {
            RecoveryToolsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.RootTools.route) {
            RootToolsTogglesScreen(navController = navController)
        }
        composable(NavDestination.SecurityCenter.route) {
            SecurityCenterScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.LSPosedHub.route) {
            LSPosedSubmenuScreen(navController = navController)
        }
        composable(NavDestination.LsposedQuickToggles.route) {
            LsposedQuickTogglesScreen(navController = navController)
        }
        composable(NavDestination.LSPosedModules.route) {
            LSPosedModuleManagerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.LiveROMEditor.route) {
            LiveROMEditorScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.SystemJournal.route) {
            SystemJournalScreen(
                navController = navController,
                onNavigateBack = { navController.popBackStack(); true }
            )
        }
        composable(NavDestination.SystemOverrides.route) {
            SystemOverridesScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.LogsViewer.route) {
            LogsViewerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.VPN.route) {
            VPNScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.SovereignModuleManager.route) {
            SovereignModuleManagerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.RomToolsSubmenu.route) {
            ROMToolsSubmenuScreen(navController = navController)
        }
        composable(NavDestination.SovereignBootloader.route) {
            SovereignBootloaderScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.SovereignRecovery.route) {
            SovereignRecoveryScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.SovereignShield.route) {
            SovereignShieldScreen(onNavigateBack = { navController.popBackStack() })
        }

        // --- LEVEL 3: GENESIS TOOLS ---
        composable(NavDestination.NeuralNetwork.route) {
            NeuralArchiveScreen(navController = navController)
        }
        composable(NavDestination.OracleCloudStorage.route) {
            OracleCloudInfiniteStorageScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.Terminal.route) {
            TerminalScreen()
        }
        composable(NavDestination.ConferenceRoom.route) {
            ConferenceRoomScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.InterfaceForge.route) {
            AppBuilderScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.HotSwap.route) {
            HotSwapScreen(navController = navController)
        }
        composable(NavDestination.Trinity.route) {
            TrinityScreen()
        }
        composable(NavDestination.DataVeinSphere.route) {
            SimpleDataVeinScreen(
                onLaunchSphereGrid = { navController.navigate(NavDestination.SphereGrid.route) }
            )
        }
        composable(NavDestination.SphereGrid.route) {
            SimpleDataVeinScreen(
                onLaunchSphereGrid = { /* Already here */ }
            )
        }
        composable(NavDestination.SovereignNeuralArchive.route) {
            SovereignNeuralArchiveScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.SentientShell.route) {
            SentientShellScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.CascadeVision.route) {
            CascadeVisionScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.CollabCanvas.route) {
            CollabCanvasScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.OracleDriveSubmenu.route) {
            OracleDriveSubmenuScreen(navController = navController)
        }

        // --- LEVEL 3: HELP & SUPPORT ---
        composable(NavDestination.HelpDeskSubmenu.route) {
            HelpDeskSubmenuScreen(navController = navController)
        }
        composable(NavDestination.DirectChat.route) {
            DirectChatScreen(navController = navController)
        }
        composable(NavDestination.Documentation.route) {
            DocumentationScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.FAQBrowser.route) {
            FAQBrowserScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.TutorialVideos.route) {
            TutorialVideosScreen(onNavigateBack = { navController.popBackStack() })
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
