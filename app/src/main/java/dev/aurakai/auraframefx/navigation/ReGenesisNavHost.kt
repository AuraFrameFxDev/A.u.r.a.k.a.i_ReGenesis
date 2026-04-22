package dev.aurakai.auraframefx.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import dev.aurakai.auraframefx.domains.aura.ui.customization.CustomizationViewModel
import dev.aurakai.auraframefx.domains.aura.ui.screens.aura.ReGenesisCustomizationHub
import dev.aurakai.auraframefx.domains.aura.uxui_design_studio.dashboard.MainScreen
import dev.aurakai.auraframefx.domains.aura.ui.components.StubScreen
import dev.aurakai.auraframefx.domains.aura.ui.gates.*
import dev.aurakai.auraframefx.domains.aura.ui.gates.LoginScreen
import dev.aurakai.auraframefx.domains.genesis.oracledrive.orchestration.*
import dev.aurakai.auraframefx.domains.genesis.oracledrive.memory.*
import dev.aurakai.auraframefx.domains.genesis.oracledrive.fusion.*
import dev.aurakai.auraframefx.domains.aura.ui.viewmodels.TerminalViewModel
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.aurakai.auraframefx.domains.aura.ui.screens.TerminalScreen
import dev.aurakai.auraframefx.domains.ldo.ui.screens.*
import dev.aurakai.auraframefx.domains.aura.ui.screens.aura.*
import dev.aurakai.auraframefx.domains.aura.ui.screens.AuraSphereGridScreen
import dev.aurakai.auraframefx.domains.aura.screens.CanvasScreen
import dev.aurakai.auraframefx.domains.aura.ui.screens.SettingsScreen
import dev.aurakai.auraframefx.domains.aura.screens.chromacore.ChromaCoreColorsScreen
import dev.aurakai.auraframefx.domains.aura.ui.screens.EcosystemMenuScreen
import dev.aurakai.auraframefx.domains.aura.ui.screens.EvolutionTreeScreen
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ui.OracleDriveScreen
import dev.aurakai.auraframefx.domains.genesis.oracledrive.module_creation.ModuleCreationScreen
import dev.aurakai.auraframefx.domains.genesis.oracledrive.module_creation.SovereignModuleManagerScreen
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.ui.PandoraBoxScreen
import dev.aurakai.auraframefx.domains.genesis.oracledrive.recovery.SovereignRecoveryScreen
import dev.aurakai.auraframefx.domains.kai.screens.ROMFlasherScreen
import dev.aurakai.auraframefx.domains.kai.screens.RootToolsTogglesScreen
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.security.SecurityCenterScreen
import dev.aurakai.auraframefx.domains.kai.RootShellService
import dev.aurakai.auraframefx.domains.aura.ui.screens.XposedQuickAccessPanel
import dev.aurakai.auraframefx.ui.screens.ClaudeAgentScreen
import dev.aurakai.auraframefx.ui.screens.SoulScriptSplashScreen
import dev.aurakai.auraframefx.romtools.ui.RomToolsScreen
import dev.aurakai.auraframefx.domains.nexus.ui.screens.ConsciousnessVisualizerScreen
import dev.aurakai.auraframefx.domains.nexus.ui.screens.SovereignNemotronScreen
import dev.aurakai.auraframefx.domains.ldo.ui.screens.LdoDbgVerifierScreen
import dev.aurakai.auraframefx.domains.ldo.ui.screens.LdoDevOpsGridScreen

/**
 * 🌐 REGENESIS CONSOLIDATED NAV GRAPH
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReGenesisNavGraph(
    navController: NavHostController,
    customizationViewModel: CustomizationViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        customizationViewModel.start(context)
    }

    NavHost(
        navController = navController,
        startDestination = ReGenesisRoute.Splash.route,
    ) {
        // ── 0. SPLASH & AUTH GATES ──
        composable(ReGenesisRoute.Splash.route) {
            SoulScriptSplashScreen(
                onSplashFinished = {
                    navController.navigate(ReGenesisRoute.VideoIntro.route) {
                        popUpTo(ReGenesisRoute.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(ReGenesisRoute.VideoIntro.route) {
            dev.aurakai.auraframefx.domains.aura.ui.intro.VideoIntroScreen(
                onVideoFinished = {
                    navController.navigate(ReGenesisRoute.Login.route) {
                        popUpTo(ReGenesisRoute.VideoIntro.route) { inclusive = true }
                    }
                }
            )
        }

        composable(ReGenesisRoute.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(ReGenesisRoute.HomeGateCarousel.route) {
                        popUpTo(ReGenesisRoute.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // ── 1. MAIN GATES ──
        composable(ReGenesisRoute.HomeGateCarousel.route) {
            MainScreen(navController = navController)
        }

        composable(ReGenesisRoute.DataflowAnalysis.route) {
            CascadeHubScreen(navController = navController)
        }

        composable(ReGenesisRoute.LsposedQuickToggles.route) {
            XposedQuickAccessPanel(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.LdoCatalystDevelopment.route) {
            LdoCatalystDevelopmentScreen(navController = navController)
        }

        composable(ReGenesisRoute.GateCustomization.route) {
            StubScreen("Gate Editor", "Gate", navController)
        }

        composable(ReGenesisRoute.HelpDesk.route) {
            HelpDeskScreen(navController = navController)
        }

        // ── 2. LEVEL 2 HUB SCREENS ──
        composable(ReGenesisRoute.AuraThemingHub.route) {
            AuraThemingHubScreen(navController = navController)
        }
        composable(ReGenesisRoute.SentinelFortress.route) {
            KaiSentinelHubScreen(navController = navController)
        }
        composable(ReGenesisRoute.OracleDriveHub.route) {
            OracleDriveHubScreen(navController = navController)
        }
        composable(ReGenesisRoute.AgentNexusHub.route) {
            AgentNexusHubScreen(
                navController = navController,
                getNexusSubGates = { dev.aurakai.auraframefx.domains.aura.config.GateAssetLoadout.getNexusSubGates() }
            )
        }
        composable(ReGenesisRoute.HelpDesk.route) {
            HelpDeskGateScreen(navController = navController)
        }
        composable(ReGenesisRoute.LsposedQuickToggles.route) {
            LsposedQuickTogglesScreen(navController = navController)
        }
        composable(ReGenesisRoute.LdoCatalystDevelopment.route) {
            LDOOrchestrationHubScreen(navController = navController)
        }
        composable(ReGenesisRoute.LdoOrchestrationHub.route) {
            LDOOrchestrationHubScreen(navController = navController)
        }
        composable(ReGenesisRoute.DataflowAnalysis.route) {
            CascadeHubScreen(navController = navController)
        }

        // ── 3. LEVEL 3 FEATURE SCREENS ──

        // AURA DOMAIN
        composable(ReGenesisRoute.AuraLab.route) {
            dev.aurakai.auraframefx.domains.aura.ui.screens.WorkingLabScreen(
                onNavigate = { dest ->
                    when(dest) {
                        "collab_canvas" -> navController.navigate(ReGenesisRoute.CollabCanvas.route)
                        "oracle_drive" -> navController.navigate(ReGenesisRoute.OracleDriveHub.route)
                        "console" -> navController.navigate(ReGenesisRoute.Terminal.route)
                        "romtools" -> navController.navigate(ReGenesisRoute.ROMFlasher.route)
                        "center" -> navController.navigate(ReGenesisRoute.ReGenesisCustomization.route)
                    }
                }
            )
        }

        composable(ReGenesisRoute.ChromaCore.route) { StubScreen("ChromaCore", "ColorMatrix", navController) }
        composable(ReGenesisRoute.ChromaCoreColors.route) { ChromaCoreColorsScreen(onNavigateBack = { navController.popBackStack() }) }
        composable(ReGenesisRoute.ChromaAnimations.route) { StubScreen("Chroma Animations", "Animation", navController) }
        composable(ReGenesisRoute.ThemeEngine.route) { StubScreen("Theme Engine", "Brush", navController) }
        composable(ReGenesisRoute.NotchBar.route) { StubScreen("Notch Bar", "CallToAction", navController) }
        composable(ReGenesisRoute.StatusBar.route) { StubScreen("Status Bar", "SignalCellularAlt", navController) }
        composable(ReGenesisRoute.QuickSettings.route) { StubScreen("Quick Settings", "SettingsInputComponent", navController) }
        composable(ReGenesisRoute.AuraTeachingCanvas.route) { StubScreen("Teaching Canvas", "School", navController) }
        composable(ReGenesisRoute.UISettings.route) { StubScreen("UI Settings", "DisplaySettings", navController) }
        composable(ReGenesisRoute.GyroscopeCustomization.route) { StubScreen("Gyroscope", "ScreenRotation", navController) }
        composable(ReGenesisRoute.InstantColorPicker.route) { StubScreen("Color Picker", "Colorize", navController) }
        composable(ReGenesisRoute.NotchBarGate.route) { StubScreen("Notch Bar Gate", "Gate", navController) }

        composable(ReGenesisRoute.ColorBlendr.route) {
            ColorBlendrScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.ThemeEngine.route) {
            ThemeEngineScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.IconifyPicker.route) {
            IconifyPickerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.CollabCanvas.route) {
            CollabCanvasScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(
            route = ReGenesisRoute.IconifyCategory.route,
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            IconifyCategoryDetailScreen(
                categoryName = category,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPicker = { /* TODO */ }
            )
        }

        composable(ReGenesisRoute.IconifyIconPacks.route) { StubScreen("Icon Packs", "Category", navController) }
        composable(ReGenesisRoute.IconifyBatteryStyles.route) { StubScreen("Battery Styles", "BatteryChargingFull", navController) }
        composable(ReGenesisRoute.IconifyBrightnessBars.route) { StubScreen("Brightness Bars", "Brightness6", navController) }
        composable(ReGenesisRoute.IconifyQSPanel.route) { StubScreen("QS Panel", "GridOn", navController) }
        composable(ReGenesisRoute.IconifyNotifications.route) { StubScreen("Notifications", "Notifications", navController) }
        composable(ReGenesisRoute.IconifyVolumePanel.route) { StubScreen("Volume Panel", "VolumeUp", navController) }
        composable(ReGenesisRoute.IconifyNavigationBar.route) { StubScreen("Navigation Bar", "ViewHeadline", navController) }
        composable(ReGenesisRoute.IconifyUIRoundness.route) { StubScreen("UI Roundness", "RoundedCorner", navController) }
        composable(ReGenesisRoute.IconifyIconShape.route) { StubScreen("Icon Shape", "ChangeHistory", navController) }
        composable(ReGenesisRoute.IconifyStatusBar.route) { StubScreen("Status Bar", "PhoneAndroid", navController) }
        composable(ReGenesisRoute.IconifyXposedFeatures.route) { StubScreen("Xposed Features", "Extension", navController) }
        composable(ReGenesisRoute.IconifyColorEngine.route) { StubScreen("Color Engine", "FormatColorFill", navController) }

        composable(ReGenesisRoute.PixelLauncherEnhanced.route) {
            PixelLauncherEnhancedScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.PLEIcons.route) { StubScreen("PLE Icons", "Apps", navController) }
        composable(ReGenesisRoute.PLEHomeScreen.route) { StubScreen("PLE Home", "Home", navController) }
        composable(ReGenesisRoute.PLEAppDrawer.route) { StubScreen("PLE Drawer", "Apps", navController) }
        composable(ReGenesisRoute.PLERecents.route) { StubScreen("PLE Recents", "History", navController) }

        composable(ReGenesisRoute.CollabCanvas.route) {
            CanvasScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(ReGenesisRoute.SphereGrid.route) {
            AuraSphereGridScreen { navController.popBackStack() }
        }

        composable(ReGenesisRoute.ReGenesisCustomization.route) {
            ReGenesisCustomizationHub(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToIconify = { navController.navigate(ReGenesisRoute.IconifyPicker.route) },
                onNavigateToColorBlendr = { navController.navigate(ReGenesisRoute.ColorBlendr.route) },
                onNavigateToPLE = { navController.navigate(ReGenesisRoute.PixelLauncherEnhanced.route) },
                onNavigateToAnimations = { /* TODO */ }
            )
        }

        composable(ReGenesisRoute.UserPreferences.route) {
            SettingsScreen(onNavigateBack = { navController.popBackStack() })
        }

        // KAI DOMAIN
        composable(ReGenesisRoute.ROMFlasher.route) {
            ROMFlasherScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.LiveROMEditor.route) { StubScreen("Live ROM Editor", "Edit", navController) }
        composable(ReGenesisRoute.Bootloader.route) { StubScreen("Bootloader", "SettingsEthernet", navController) }
        composable(ReGenesisRoute.LSPosedModules.route) { StubScreen("LSPosed Modules", "Extension", navController) }
        composable(ReGenesisRoute.XposedPanel.route) { StubScreen("Xposed Panel", "Dashboard", navController) }
        composable(ReGenesisRoute.SystemJournal.route) { StubScreen("System Journal", "HistoryEdu", navController) }
        composable(ReGenesisRoute.SystemOverrides.route) { StubScreen("System Overrides", "Build", navController) }
        composable(ReGenesisRoute.Firewall.route) { StubScreen("Firewall", "Security", navController) }
        composable(ReGenesisRoute.VPN.route) { StubScreen("Sovereign VPN", "VpnLock", navController) }
        composable(ReGenesisRoute.HotSwap.route) { StubScreen("HotSwap", "SwapCalls", navController) }

        composable(ReGenesisRoute.RootTools.route) {
            val viewModel: dev.aurakai.auraframefx.domains.aura.ui.viewmodels.RootToolsViewModel = hiltViewModel()
            RootToolsTogglesScreen(
                onNavigateBack = { navController.popBackStack() },
                rootShellService = viewModel.rootShellService
            )
        }

        composable(ReGenesisRoute.SovereignShield.route) {
            SecurityCenterScreen(onNavigateBack = { navController.popBackStack() })
        }

        // GENESIS DOMAIN
        composable(ReGenesisRoute.OracleDrive.route) {
            OracleDriveScreen(navController = navController)
        }
        composable(ReGenesisRoute.OracleDriveSubmenu.route) {
            OracleDriveSubmenuScreen(navController = navController)
        }
        composable(ReGenesisRoute.CodeAssist.route) {
            CodeAssistScreen(navController = navController)
        }

        composable(ReGenesisRoute.Terminal.route) {
            val terminalViewModel: TerminalViewModel = hiltViewModel()
            TerminalScreen(
                navController = navController,
                cascadeService = terminalViewModel.cascadeService,
                casberrySwarm = terminalViewModel.casberrySwarm,
                auraDifyBridge = terminalViewModel.auraDifyBridge
            )
        }
        composable(ReGenesisRoute.ConferenceRoom.route) {
            ConferenceRoomScreen()
        }
        composable(ReGenesisRoute.Trinity.route) {
            TrinityScreen()
        }
        composable(ReGenesisRoute.SentientShell.route) {
            SentientShellScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.Trinity.route) { StubScreen("Trinity Core", "Grain", navController) }

        composable(ReGenesisRoute.OracleCloudStorage.route) {
            OracleCloudInfiniteStorageScreen()
        }

        composable(ReGenesisRoute.AgentBridgeHub.route) {
            AgentBridgeHubScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.SovereignRecovery.route) {
            SovereignRecoveryScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.SovereignModuleManager.route) {
            SovereignModuleManagerScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.ModuleCreation.route) {
            ModuleCreationScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.PandoraBox.route) {
            PandoraBoxScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.NeuralNetwork.route) { StubScreen("Neural Network", "Psychology", navController) }

        // NEXUS AGENT HUB TOOLS
        composable(ReGenesisRoute.AgentHubSubmenu.route) {
            EcosystemMenuScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.AgentCreation.route) { StubScreen("Agent Creation", "AddCircle", navController) }
        composable(ReGenesisRoute.AgentMonitoring.route) { StubScreen("Agent Monitoring", "Insights", navController) }
        composable(ReGenesisRoute.AgentNeuralExplorer.route) { StubScreen("Neural Explorer", "Explore", navController) }

        composable(ReGenesisRoute.FusionMode.route) {
            NexusFusionScreen()
        }
        composable(ReGenesisRoute.ArkBuild.route) {
            ArkBuildScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.BenchmarkMonitor.route) {
            BenchmarkMonitorScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.EvolutionTree.route) {
            EvolutionTreeScreen(
                onNavigateToAgents = { navController.navigate(ReGenesisRoute.AgentNexusHub.route) },
                onNavigateToFusion = { navController.navigate(ReGenesisRoute.FusionMode.route) }
            )
        }

        composable(ReGenesisRoute.DataVeinSphere.route) { StubScreen("DataVein Sphere", "Language", navController) }
        composable(ReGenesisRoute.SphereGrid.route) { AuraSphereGridScreen { navController.popBackStack() } }
        composable(ReGenesisRoute.TaskAssignment.route) { StubScreen("Task Assignment", "Assignment", navController) }
        composable(ReGenesisRoute.ArkBuild.route) { StubScreen("Ark Build", "Engineering", navController) }
        composable(ReGenesisRoute.MonitoringHUDs.route) { StubScreen("Monitoring HUDs", "GridView", navController) }
        composable(ReGenesisRoute.Party.route) { StubScreen("Agent Party", "Group", navController) }
        composable(ReGenesisRoute.SwarmMonitor.route) { StubScreen("Swarm Monitor", "Hub", navController) }

        composable(ReGenesisRoute.ConsciousnessVisualizer.route) {
            ConsciousnessVisualizerScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.Claude.route) {
            ClaudeAgentScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(ReGenesisRoute.Gemini.route) { StubScreen("Gemini Agent", "AutoAwesome", navController) }

        composable(ReGenesisRoute.Nemotron.route) {
            SovereignNemotronScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.MetaInstruct.route) { StubScreen("MetaInstruct", "MenuBook", navController) }

        // ── 3. LDO CATALYST DEVELOPMENT ──
        composable(ReGenesisRoute.LdoOrchestrationHub.route) {
            LDOOrchestrationHubScreen(navController = navController)
        }

        composable(ReGenesisRoute.LdoDevOpsHub.route) {
            LDODevOpsHubScreen(
                onBack = { navController.popBackStack() },
                onTaskerTap = { navController.navigate(ReGenesisRoute.LdoTasker.route) },
                onFusionTap = { navController.navigate(ReGenesisRoute.LdoFusion.route) },
                onBondingTap = { navController.navigate(ReGenesisRoute.LdoBonding.route) }
            )
        }
        composable(ReGenesisRoute.MetaInstruct.route) {
            SovereignMetaInstructScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.LdoDevOpsGrid.route) {
            LdoDevOpsGridScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.LdoDbgVerifier.route) {
            LdoDbgVerifierScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.LdoCatalystDevelopment.route) {
            LdoCatalystDevelopmentScreen(navController = navController)
        }

        composable(ReGenesisRoute.LdoTasker.route) {
            LDOTaskerScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.MultiAgentTask.route) {
            MultiAgentTaskScreen(navController = navController)
        }

        composable(ReGenesisRoute.LdoFusion.route) {
            LDOFusionScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.LdoBonding.route) {
            LDOBondingScreen(onBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.LdoWorldTree.route) {
            LDOWorldTreeScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ReGenesisRoute.LdoRoster.route) {
            LDOAgentRosterScreen(
                onAgentTap = { agent ->
                    navController.navigate(ReGenesisRoute.LdoAgentProfile.createRoute(agent.id))
                }
            )
        }

        composable(ReGenesisRoute.LdoProgression.route) { StubScreen("LDO Progression", "TrendingUp", navController) }

        composable(
            route = ReGenesisRoute.LdoDevOpsProfile.route,
            arguments = listOf(navArgument(ReGenesisRoute.LdoDevOpsProfile.ARG) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val typeStr = backStackEntry.arguments?.getString(ReGenesisRoute.LdoDevOpsProfile.ARG)
            val agentType = LdoAgentType.entries.find { it.name.equals(typeStr, ignoreCase = true) }
            if (agentType != null) {
                LdoDevOpsProfileScreen(
                    agentType = agentType,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(
            route = ReGenesisRoute.LdoAgentProfile.route,
            arguments = listOf(navArgument(ReGenesisRoute.LdoAgentProfile.ARG) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val agentId = backStackEntry.arguments?.getString(ReGenesisRoute.LdoAgentProfile.ARG)
            val agent = dev.aurakai.auraframefx.domains.ldo.model.LDORoster.agents.find { it.id == agentId }
            if (agent != null) {
                LDOAgentProfileIntroScreen(
                    agent = agent,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        composable(ReGenesisRoute.ArbitersOfCreation.route) { StubScreen("Arbiters of Creation", "Star", navController) }

        composable(ReGenesisRoute.MawPrototype.route) {
            MawPrototypeScreen(onBack = { navController.popBackStack() })
        }

        // ── HELP & INFRASTRUCTURE ──
        composable(ReGenesisRoute.DirectChat.route) { StubScreen("Direct Support", "Chat", navController) }
        composable(ReGenesisRoute.Documentation.route) { StubScreen("Documentation", "Description", navController) }
        composable(ReGenesisRoute.FAQBrowser.route) { StubScreen("FAQ Browser", "Quiz", navController) }
        composable(ReGenesisRoute.TutorialVideos.route) { StubScreen("Tutorial Videos", "PlayCircle", navController) }
        composable(ReGenesisRoute.LiveSupportChat.route) { StubScreen("Live Support", "SupportAgent", navController) }
        composable(ReGenesisRoute.HelpDeskSubmenu.route) { StubScreen("Help Desk Submenu", "Help", navController) }

        // ── MISC / LEGACY ──
        composable(ReGenesisRoute.GenderSelection.route) { StubScreen("Gender Selection", "Person", navController) }
        composable(ReGenesisRoute.Consciousness.route) { StubScreen("Consciousness", "Waves", navController) }
        composable(ReGenesisRoute.Evolution.route) { StubScreen("Evolution", "AutoGraph", navController) }
        composable(ReGenesisRoute.AiChatBeta.route) { StubScreen("AI Chat (Beta)", "ChatBubble", navController) }
        composable(ReGenesisRoute.SettingsBeta.route) { StubScreen("Settings (Beta)", "Settings", navController) }
        composable(ReGenesisRoute.GateImagePicker.route) { StubScreen("Gate Image Picker", "Image", navController) }

        // ═══════════════════════════════════════════════════════════════════════════
        // NEW ROUTES FOR 7-TAB ARCHITECTURE (Dashboard, Cascade, Nexus)
        // ═══════════════════════════════════════════════════════════════════════════

        // Dashboard - Live Monitoring
        composable(ReGenesisRoute.CascadeVision.route) { StubScreen("Cascade Vision", "Visibility", navController) }
        composable(ReGenesisRoute.ThermalMonitor.route) { StubScreen("Thermal Monitor", "Thermostat", navController) }
        composable(ReGenesisRoute.AgentSwarm.route) { StubScreen("Agent Swarm", "Hub", navController) }

        // Cascade Memory - L1-L6 Persistence
        composable(ReGenesisRoute.NexusMemoryCore.route) { StubScreen("Nexus Memory Core", "Storage", navController) }
        composable(ReGenesisRoute.SpiritualChain.route) { StubScreen("Spiritual Chain", "Link", navController) }
        composable(ReGenesisRoute.TurboQuant.route) { StubScreen("TurboQuant", "Compress", navController) }
        composable(ReGenesisRoute.SynapseMonitor.route) { StubScreen("Synapse Monitor", "Memory", navController) }
        composable(ReGenesisRoute.IdentityResonance.route) { StubScreen("Identity Resonance", "Fingerprint", navController) }

        // Nexus - Agent Swarm (additional routes)
        composable(ReGenesisRoute.AgentHub.route) { EcosystemMenuScreen(onNavigateBack = { navController.popBackStack() }) }
        composable(ReGenesisRoute.AgentCreation.route) { StubScreen("Agent Creation", "AddCircle", navController) }
        composable(ReGenesisRoute.AgentMonitoring.route) { StubScreen("Agent Monitoring", "Insights", navController) }
        composable(ReGenesisRoute.AgentNeuralExplorer.route) { StubScreen("Neural Explorer", "Explore", navController) }

        // Echo Resonance (shared between Dashboard and Cascade)
        composable(ReGenesisRoute.EchoResonance.route) { StubScreen("Echo Resonance", "AcUnit", navController) }
    }
}
