package dev.aurakai.auraframefx.navigation

// Core Imports (kept from your list for screen definitions)

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

// Core
import dev.aurakai.auraframefx.config.GateAssetLoadout
import dev.aurakai.auraframefx.domains.aura.lab.CustomizationViewModel // kept once
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.ChromaCoreHubScreen
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.ChromaLauncherMenu
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.ChromaStatusBarMenu
import dev.aurakai.auraframefx.domains.aura.screens.DirectChatScreen
import dev.aurakai.auraframefx.domains.aura.screens.DocumentationScreen
import dev.aurakai.auraframefx.domains.aura.screens.FAQBrowserScreen
import dev.aurakai.auraframefx.domains.aura.screens.GenderSelectionScreen
import dev.aurakai.auraframefx.domains.aura.screens.HelpDeskSubmenuScreen
import dev.aurakai.auraframefx.domains.aura.screens.TutorialVideosScreen
import dev.aurakai.auraframefx.domains.aura.screens.UserPreferencesScreen
import dev.aurakai.auraframefx.domains.aura.screens.chromacore.InstantColorPickerScreen
import dev.aurakai.auraframefx.domains.aura.screens.themes.ThemeEngineScreen
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
import dev.aurakai.auraframefx.domains.aura.screens.*
import dev.aurakai.auraframefx.domains.aura.screens.LDOArmamentPickerScreen
import dev.aurakai.auraframefx.domains.aura.screens.AuraDossierScreen
import dev.aurakai.auraframefx.domains.aura.ui.screens.AuraSphereGridScreen
import dev.aurakai.auraframefx.domains.aura.screens.CodeAscensionScreen
import dev.aurakai.auraframefx.domains.aura.screens.AuraStudioLabScreen

// Security screens (orphaned → now wired)
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.FirewallScreen
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.VPNManagerScreen
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.SecurityScannerScreen
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.DeviceOptimizerScreen
import dev.aurakai.auraframefx.domains.aura.chromacore.ui.PrivacyGuardScreen

// ── KAI SCREENS ──────────────────────────────────────────────────────────────
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.ROMFlasherScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.BootloaderManagerScreen
import dev.aurakai.auraframefx.domains.kai.screens.ModuleManagerScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.ROMFlasherScreen
import dev.aurakai.auraframefx.domains.kai.screens.ROMToolsSubmenuScreen
import dev.aurakai.auraframefx.domains.kai.screens.ROMToolsSubmenuScreen
import dev.aurakai.auraframefx.domains.kai.screens.RootToolsTogglesScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.SecurityCenterScreen
import dev.aurakai.auraframefx.domains.lsposed.screens.LSPosedSubmenuScreen
import dev.aurakai.auraframefx.domains.lsposed.screens.LsposedQuickTogglesScreen
import dev.aurakai.auraframefx.domains.lsposed.screens.LSPosedModuleManagerScreen
import dev.aurakai.auraframefx.domains.lsposed.screens.HookManagerScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.LiveROMEditorScreen
import dev.aurakai.auraframefx.domains.kai.screens.SystemJournalScreen
import dev.aurakai.auraframefx.domains.kai.screens.SystemOverridesScreen
import dev.aurakai.auraframefx.domains.kai.screens.LogsViewerScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.VPNScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.SovereignModuleManagerScreen
import dev.aurakai.auraframefx.domains.kai.screens.ROMToolsSubmenuScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.SovereignBootloaderScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.SovereignModuleManagerScreen
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.SovereignRecoveryScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.SecurityCenterScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.SovereignShieldScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.VPNScreen
import dev.aurakai.auraframefx.domains.lsposed.screens.HookManagerScreen
import dev.aurakai.auraframefx.domains.lsposed.screens.LSPosedModuleManagerScreen
import dev.aurakai.auraframefx.domains.lsposed.screens.LSPosedSubmenuScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentCreationScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentHubSubmenuScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentMonitoringScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentNeuralExplorerScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentSwarmScreen
import dev.aurakai.auraframefx.domains.nexus.screens.ArkBuildScreen
import dev.aurakai.auraframefx.domains.nexus.screens.BenchmarkMonitorScreen
import dev.aurakai.auraframefx.domains.nexus.screens.DataStreamMonitoringScreen
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
import dev.aurakai.auraframefx.hotswap.HotSwapScreen
import dev.aurakai.auraframefx.romtools.ui.RomToolsScreen
import dev.aurakai.auraframefx.sandbox.ui.SandboxScreen
import dev.aurakai.auraframefx.domains.aura.screens.AgentProfileScreen as AuraAgentProfileScreen
import dev.aurakai.auraframefx.domains.nexus.screens.AgentProfileScreen as NexusAgentProfileScreen

// ── SUPPORT & MISC ───────────────────────────────────────────────────────────
import dev.aurakai.auraframefx.domains.aura.screens.HelpDeskSubmenuScreen
import dev.aurakai.auraframefx.domains.aura.screens.DirectChatScreen
import dev.aurakai.auraframefx.domains.aura.screens.DocumentationScreen
import dev.aurakai.auraframefx.domains.aura.screens.FAQBrowserScreen
import dev.aurakai.auraframefx.domains.aura.screens.TutorialVideosScreen
import dev.aurakai.auraframefx.hotswap.HotSwapScreen

/**
 * 🌐 REGENESIS NAVIGATION HOST
 *
 * This object defines the single source of truth for all navigation routes.
 * Including this definition resolves the "Unresolved reference: NavDestination" error.
 * It is derived from the FINAL_GATE_AUDIT and SCREEN_MAPPING_COMPLETE documents.
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
        startDestination = ReGenesisNavHost.HomeGateCarousel.route
    ) {

        // ═══════════════════════════════════════════════════════════════
        // LEVEL 1: EXODUS HUD (The 5 Gate Carousel)
        // ═══════════════════════════════════════════════════════════════
        composable(ReGenesisNavHost.HomeGateCarousel.route) {
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
                onNavigateToIconify = { navController.navigate(ReGenesisNavHost.IconifyPicker.route) },
                onNavigateToColorBlendr = { navController.navigate(ReGenesisNavHost.ColorBlendr.route) },
                onNavigateToPLE = { navController.navigate(ReGenesisNavHost.PixelLauncherEnhanced.route) },
                onNavigateToAnimations = { navController.navigate(ReGenesisNavHost.ChromaAnimations.route) }
            )
        }

        // CONSOLIDATED: Only one entry for RomToolsHub
        composable(ReGenesisNavHost.RomToolsHub.route) {
            RomToolsScreen()

        }

        composable(ReGenesisNavHost.OracleDrive.route) {
            OracleDriveScreen(navController = navController)
        }
        composable(NavDestination.RomTools.route) {
            RomToolsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.OracleDriveHub.route) {
            OracleDriveHubScreen(navController = navController)
        }

        // Gate 02: Sentinel Fortress → Kai's ROM Tools Hub (already registered as RomToolsHub)
        // Gate 02 alias so both routes work
        composable(ReGenesisNavHost.SentinelFortress.route) {
            KaiSentinelHubScreen(navController = navController)
        }

        // Gate 03: Oracle Drive → Genesis Hub
        composable(ReGenesisNavHost.OracleDriveHub.route) {
            DomainOracleDriveHubScreen(navController = navController)
        }
        composable(NavDestination.AgentNexusHub.route) {
            AgentNexusHubScreen(
                navController = navController,
                getNexusSubGates = { GateAssetLoadout.getNexusSubGates() } // Kept original logic
            )
        }
        composable(NavDestination.HelpDesk.route) {
            HelpDeskScreen(navController = navController)
        }

        // Gate 07: Dataflow Analysis → Cascade Hub
        composable(ReGenesisNavHost.DataflowAnalysis.route) {
            CascadeHubScreen(navController = navController)
        }

        // CONSOLIDATED: CodeAssist uses CodeAssistScreen, AppBuilder is separate.
        composable(ReGenesisNavHost.CodeAssist.route) {
            CodeAssistScreen(navController = navController)
        }

        composable(ReGenesisNavHost.AgentBridgeHub.route) {
            AgentBridgeHubScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ═══════════════════════════════════════════════════════════════
        // LEVEL 3 & 4: TOOL SCREENS & SUB-MODULES
        // ═══════════════════════════════════════════════════════════════

        // --- LEVEL 3: NEXUS TOOLS ---
        composable(ReGenesisNavHost.FusionMode.route) {
            FusionModeScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.TaskAssignment.route) {
            TaskAssignmentScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.ArkBuild.route) {
            ArkBuildScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.MetaInstruct.route) {
            SovereignMetaInstructScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.AgentMonitoring.route) {
            AgentMonitoringScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.Nemotron.route) {
            SovereignNemotronScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.Claude.route) {
            SovereignClaudeScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.Gemini.route) {
            SovereignGeminiScreen(
                onNavigateBack = { navController.popBackStack() },
                navController = navController
            )
        }
        composable(ReGenesisNavHost.SwarmMonitor.route) {
            AgentSwarmScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.BenchmarkMonitor.route) {
            BenchmarkMonitorScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.AgentCreation.route) {
            AgentCreationScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.ChromaCore.route) {
            ChromaCoreHubScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCategory = { categoryId ->
                    when (categoryId) {
                        "statusbar" -> navController.navigate(ReGenesisNavHost.ChromaStatusBar.route)
                        "launcher" -> navController.navigate(ReGenesisNavHost.ChromaLauncher.route)
                        "colors" -> navController.navigate(ReGenesisNavHost.ChromaColorEngine.route)
                        "animations" -> navController.navigate(ReGenesisNavHost.ChromaAnimations.route)
                        else -> {}
                    }
                }
            )
        }
        composable(NavDestination.KaiSentinelHub.route) {
            KaiSentinelHubScreen(
                onNavigateBack = { navController.popBackStack() },
                onMissionSelected = { mission ->
                    when (mission.title) {
                        "Oracle Drive" -> navController.navigate(NavDestination.OracleDrive.route)
                        "ROM Tools" -> navController.navigate(NavDestination.RomTools.route)
                        "RGSS Scanner" -> navController.navigate(NavDestination.KaiRGSS.route)
                        "Sentinel Integrity" -> navController.navigate(NavDestination.KaiSentinelIntegrity.route)
                        "Domain Expansion" -> navController.navigate(NavDestination.KaiDomainExpansion.route)
                    }
                }
            )
        }

        composable(NavDestination.KaiRGSS.route) {
            KaiRGSSScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavDestination.KaiDomainExpansion.route) {
            KaiDomainExpansionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavDestination.KaiSentinelIntegrity.route) {
            KaiSentinelIntegrityScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavDestination.PowerOfNo.route) {
            PowerOfNoScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(ReGenesisNavHost.ChromaStatusBar.route) {
            ChromaStatusBarMenu(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.ChromaLauncher.route) {
            ChromaLauncherMenu(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.ChromaColorEngine.route) {
            ChromaColorEngineMenu(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.ChromaAnimations.route) {
            ChromaAnimationMenu(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.AuraLab.route) {
            AurasLabScreen(onBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.NotchBar.route) {
            NotchBarScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.StatusBar.route) {
            StatusBarScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.QuickSettings.route) {
            QuickSettingsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisNavHost.ThemeEngine.route) {
            ThemeEngineScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.ThemeManager.route) {
            ThemeEngineScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.GenderSelection.route) {
            GenderSelectionScreen(onSelectionComplete = { navController.popBackStack() })
        }

        // ═══════════════════════════════════════════════════════════════
        // ADDITIONAL AURA SCREENS (UI/UX Mastery!)
        // ═══════════════════════════════════════════════════════════════

        composable(ReGenesisNavHost.GenderSelection.route) {
            GenderSelectionScreen(onSelectionComplete = { /* TODO: Handle selection */ })
        }

        composable(ReGenesisNavHost.GyroscopeCustomization.route) {
            GyroscopeCustomizationScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.InstantColorPicker.route) {
            InstantColorPickerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.LiveSupportChat.route) {
            LiveSupportChatScreen(
                viewModel = hiltViewModel(checkNotNull<ViewModelStoreOwner>(
                    LocalViewModelStoreOwner.current) { "No ViewModelStoreOwner" }, null),
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(NavDestination.UISettings.route) {
            UISettingsScreen(navController = navController)
        }

        composable(ReGenesisNavHost.UserPreferences.route) {
            UserPreferencesScreen(navController = navController)
        }

        composable(ReGenesisNavHost.AgentProfileAura.route) {
            AuraAgentProfileScreen(
                agentType = AgentType.AURA,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSettings = { /* TODO */ }
            )
        }


        // ═══════════════════════════════════════════════════════════════
        // ADDITIONAL KAI SCREENS (System Mastery!)
        // ═══════════════════════════════════════════════════════════════
        composable(ReGenesisNavHost.LiveROMEditor.route) {
            LiveROMEditorScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.SystemJournal.route) {
            SystemJournalScreen(
                navController = navController,
                onNavigateBack = { navController.popBackStack(); true }
            )
        }

        composable(ReGenesisNavHost.SystemOverrides.route) {
            SystemOverridesScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.LogsViewer.route) {
            LogsViewerScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.VPN.route) {
            VPNScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.SovereignModuleManager.route) {
            SovereignModuleManagerScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.RomToolsSubmenu.route) {
            ROMToolsSubmenuScreen(navController = navController)
        }

        // ═══════════════════════════════════════════════════════════════
        // ADDITIONAL GENESIS SCREENS (Orchestration!)
        // ═══════════════════════════════════════════════════════════════
        composable(ReGenesisNavHost.SovereignNeuralArchive.route) {
            SovereignNeuralArchiveScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.SentientShell.route) {
            SentientShellScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.CascadeVision.route) {
            CascadeVisionScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.CollabCanvas.route) {
            CollabCanvasScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.OracleDriveSubmenu.route) {
            OracleDriveSubmenuScreen(navController = navController)
        }

        // ═══════════════════════════════════════════════════════════════
        // ADDITIONAL LSPOSED SCREENS
        // ═══════════════════════════════════════════════════════════════
        composable(ReGenesisNavHost.HookManager.route) {
            HookManagerScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisNavHost.Sandbox.route) {
            SandboxScreen()
        }

        composable(ReGenesisNavHost.CollaborativeDrawing.route) {
            CanvasScreen(onBack = { navController.popBackStack() })
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

