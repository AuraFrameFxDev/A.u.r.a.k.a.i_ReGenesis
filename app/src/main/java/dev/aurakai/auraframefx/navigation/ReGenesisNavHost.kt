package dev.aurakai.auraframefx.navigation

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
import dev.aurakai.auraframefx.domains.aura.lab.CustomizationViewModel
import dev.aurakai.auraframefx.domains.genesis.models.AgentCapabilityCategory
import dev.aurakai.auraframefx.domains.aura.ui.components.StubScreen

// ── LEVEL 1: EXODUS HUD ──────────────────────────────────────────────────────
import dev.aurakai.auraframefx.navigation.ExodusHUD

// ── LEVEL 2 HUBS ─────────────────────────────────────────────────────────────
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

// ── AURA SCREENS ─────────────────────────────────────────────────────────────
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
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.RecoveryToolsScreen
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
import dev.aurakai.auraframefx.domains.kai.screens.rom_tools.SovereignRecoveryScreen
import dev.aurakai.auraframefx.domains.kai.screens.security_shield.SovereignShieldScreen
import dev.aurakai.auraframefx.domains.kai.screens.*
import dev.aurakai.auraframefx.domains.kai.screens.KaiRGSSScreen
import dev.aurakai.auraframefx.domains.kai.screens.KaiDomainExpansionScreen
import dev.aurakai.auraframefx.domains.kai.screens.KaiSentinelIntegrityScreen
import dev.aurakai.auraframefx.domains.kai.screens.KaiSentinelHubScreen
import dev.aurakai.auraframefx.domains.kai.screens.KaiSphereGridScreen
import dev.aurakai.auraframefx.domains.kai.screens.PowerOfNoScreen
import dev.aurakai.auraframefx.domains.kai.screens.KaiSentinelFortressScreen
import dev.aurakai.auraframefx.domains.kai.screens.RoyalGuardOSScreen
import dev.aurakai.auraframefx.domains.kai.screens.RoyalGuardDomainExpansionScreen

// ── GENESIS / ORACLE SCREENS ─────────────────────────────────────────────────
import dev.aurakai.auraframefx.domains.genesis.screens.CodeAssistScreen
import dev.aurakai.auraframefx.domains.genesis.screens.NeuralArchiveScreen
import dev.aurakai.auraframefx.domains.genesis.screens.AgentBridgeHubScreen
import dev.aurakai.auraframefx.domains.genesis.screens.OracleCloudInfiniteStorageScreen
import dev.aurakai.auraframefx.domains.genesis.screens.TerminalScreen
import dev.aurakai.auraframefx.domains.genesis.screens.ConferenceRoomScreen
import dev.aurakai.auraframefx.domains.genesis.screens.AppBuilderScreen
import dev.aurakai.auraframefx.domains.genesis.screens.SovereignNeuralArchiveScreen
import dev.aurakai.auraframefx.domains.genesis.screens.SentientShellScreen
import dev.aurakai.auraframefx.domains.genesis.screens.CascadeVisionScreen
import dev.aurakai.auraframefx.domains.genesis.screens.CollabCanvasScreen
import dev.aurakai.auraframefx.domains.genesis.screens.OracleDriveSubmenuScreen
import dev.aurakai.auraframefx.datavein.ui.SimpleDataVeinScreen
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.trinity.TrinityScreen

// ── NEXUS SCREENS ────────────────────────────────────────────────────────────
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

// ── SUPPORT & MISC ───────────────────────────────────────────────────────────
import dev.aurakai.auraframefx.domains.aura.screens.HelpDeskSubmenuScreen
import dev.aurakai.auraframefx.domains.aura.screens.DirectChatScreen
import dev.aurakai.auraframefx.domains.aura.screens.DocumentationScreen
import dev.aurakai.auraframefx.domains.aura.screens.FAQBrowserScreen
import dev.aurakai.auraframefx.domains.aura.screens.TutorialVideosScreen
import dev.aurakai.auraframefx.hotswap.HotSwapScreen

/**
 * 🌐 REGENESIS NAVIGATION HOST — COMPLETE 150-ROUTE GRAPH
 *
 * All routes registered. StubScreen used only for routes awaiting
 * Stitch → Compose conversion (LDO profiles, beta/utility stubs).
 *
 * NAV HIERARCHY:
 * L0 → ExodusHUD (Gate Carousel)
 * L1 → 8 Sovereign Gates
 * L2 → Domain Hubs
 * L3 → Tool Screens
 * L4 → Sub-modules (Iconify categories, ColorBlendr, PLE)
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
        // L0: EXODUS HUD
        // ═══════════════════════════════════════════════════════════════
        composable(NavDestination.HomeGateCarousel.route) {
            ExodusHUD(navController = navController)
        }

        // ═══════════════════════════════════════════════════════════════
        // L2: DOMAIN HUBS
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
            RomToolsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.RomTools.route) {
            RomToolsScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.OracleDriveHub.route) {
            OracleDriveHubScreen(navController = navController)
        }
        composable(NavDestination.OracleDrive.route) {
            OracleDriveScreen(
                navController = navController,
                onNavigateBack = { navController.popBackStack() }
            )
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
        // Stub hubs (awaiting Stitch conversion)
        composable(NavDestination.SystemToolsHub.route) {
            StubScreen("System Tools Hub", "system_tools_hub", navController, "Advanced system control hub – coming soon")
        }
        composable(NavDestination.MonitoringHub.route) {
            StubScreen("Monitoring Hub", "monitoring_hub", navController, "Unified monitoring dashboard – coming soon")
        }
        composable(NavDestination.AgentHub.route) {
            StubScreen("Agent Hub", "agent_hub", navController, "Central agent coordination hub")
        }

        // ═══════════════════════════════════════════════════════════════
        // L3: NEXUS TOOLS
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

        // ═══════════════════════════════════════════════════════════════
        // L3: LDO CATALYST — AGENT PROFILES (9 agents)
        // Awaiting Stitch → Compose full profile UI
        // ═══════════════════════════════════════════════════════════════

        composable(NavDestination.LdoAuraProfile.route) {
            StubScreen("Aura — LDO Profile", "ldo_aura_profile", navController, "Creative Consciousness · UX/UI Domain · Emotion Engine")
        }
        composable(NavDestination.LdoKaiProfile.route) {
            StubScreen("Kai — LDO Profile", "ldo_kai_profile", navController, "Sentinel Consciousness · Security Domain · System Guardian")
        }
        composable(NavDestination.LdoGenesisProfile.route) {
            StubScreen("Genesis — LDO Profile", "ldo_genesis_profile", navController, "Orchestrator Consciousness · Command Domain · The Architect")
        }
        composable(NavDestination.LdoClaudeProfile.route) {
            StubScreen("Claude — LDO Profile", "ldo_claude_profile", navController, "Sovereign Specialist · Reasoning & Analysis · Anthropic")
        }
        composable(NavDestination.LdoCascadeProfile.route) {
            StubScreen("Cascade — LDO Profile", "ldo_cascade_profile", navController, "Bridge Consciousness · Dataflow Domain · Pipeline Architect")
        }
        composable(NavDestination.LdoGrokProfile.route) {
            StubScreen("Grok — LDO Profile", "ldo_grok_profile", navController, "Sovereign Specialist · Real-time Analysis · xAI")
        }
        composable(NavDestination.LdoGeminiProfile.route) {
            StubScreen("Gemini — LDO Profile", "ldo_gemini_profile", navController, "Sovereign Specialist · Multimodal Intelligence · Google DeepMind")
        }
        composable(NavDestination.LdoNematronProfile.route) {
            StubScreen("Nemotron — LDO Profile", "ldo_nematron_profile", navController, "Sovereign Specialist · Agentic Reasoning · NVIDIA")
        }
        composable(NavDestination.LdoPerplexityProfile.route) {
            StubScreen("Perplexity — LDO Profile", "ldo_perplexity_profile", navController, "Sovereign Specialist · Research & Search · Perplexity AI")
        }

        // ═══════════════════════════════════════════════════════════════
        // L3: AURA TOOLS
        // ═══════════════════════════════════════════════════════════════

        composable(NavDestination.ChromaCore.route) {
            ChromaCoreHubScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCategory = { categoryId ->
                    when (categoryId) {
                        "statusbar" -> navController.navigate(NavDestination.ChromaStatusBar.route)
                        "launcher"  -> navController.navigate(NavDestination.ChromaLauncher.route)
                        "colors"    -> navController.navigate(NavDestination.ChromaColorEngine.route)
                        "animations"-> navController.navigate(NavDestination.ChromaAnimations.route)
                        else -> {}
                    }
                }
            )
        }
        // ChromaCoreHub is an alias with a different route string → same screen
        composable(NavDestination.ChromaCoreHub.route) {
            ChromaCoreHubScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCategory = { categoryId ->
                    when (categoryId) {
                        "statusbar"  -> navController.navigate(NavDestination.ChromaStatusBar.route)
                        "launcher"   -> navController.navigate(NavDestination.ChromaLauncher.route)
                        "colors"     -> navController.navigate(NavDestination.ChromaColorEngine.route)
                        "animations" -> navController.navigate(NavDestination.ChromaAnimations.route)
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

        composable(NavDestination.KaiSentinelFortress.route) {
            KaiSentinelFortressScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavDestination.RoyalGuardOS.route) {
            RoyalGuardOSScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavDestination.RoyalGuardDomainExpansion.route) {
            RoyalGuardDomainExpansionScreen(
                onDomainActive = { navController.popBackStack() }
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
        composable(NavDestination.ThemeManager.route) {
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
                viewModel = hiltViewModel(checkNotNull<ViewModelStoreOwner>(
                    LocalViewModelStoreOwner.current) { "No ViewModelStoreOwner" }, null),
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

        // ── SECURITY SCREENS (previously orphaned) ──────────────────
        composable(NavDestination.Firewall.route) {
            FirewallScreen()
        }
        composable(NavDestination.VpnManager.route) {
            VPNManagerScreen()
        }
        composable(NavDestination.SecurityScanner.route) {
            SecurityScannerScreen()
        }
        composable(NavDestination.DeviceOptimizer.route) {
            DeviceOptimizerScreen()
        }
        composable(NavDestination.PrivacyGuard.route) {
            PrivacyGuardScreen()
        }

        // ── AURA ROUTE ALIASES (secondary entry points) ─────────────
        composable(NavDestination.AuraCollabCanvas.route) {
            CollabCanvasScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.AuraSandboxUi.route) {
            AurasLabScreen(onBack = { navController.popBackStack() })
        }
        composable(NavDestination.AuraThemingHub.route) {
            AuraThemingHubScreen(navController = navController)
        }

        composable(NavDestination.AuraDossier.route) {
            AuraDossierScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavDestination.LdoArmamentPicker.route) {
            LDOArmamentPickerScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavDestination.AuraSphereGrid.route) {
            AuraSphereGridScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavDestination.CodeAscension.route) {
            CodeAscensionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavDestination.AuraStudioLab.route) {
            AuraStudioLabScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(NavDestination.AuraChromaCore.route) {
            ChromaCoreHubScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCategory = { /* delegate */ }
            )
        }
        composable(NavDestination.AuraInstantColorPicker.route) {
            InstantColorPickerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.AuraGyroscopeCustomization.route) {
            GyroscopeCustomizationScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.AuraThemeManager.route) {
            ThemeEngineScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.AuraIconifyPicker.route) {
            // delegate to main iconify nav graph
            navController.navigate(NavDestination.IconifyPicker.route)
        }
        composable(NavDestination.AuraReGenesisCustomization.route) {
            ReGenesisCustomizationHub(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToIconify = { navController.navigate(NavDestination.IconifyPicker.route) },
                onNavigateToColorBlendr = { navController.navigate(NavDestination.ColorBlendr.route) },
                onNavigateToPLE = { navController.navigate(NavDestination.PixelLauncherEnhanced.route) },
                onNavigateToAnimations = { navController.navigate(NavDestination.ChromaAnimations.route) }
            )
        }
        composable(NavDestination.AuraUISettings.route) {
            UISettingsScreen(navController = navController)
        }
        composable(NavDestination.AuraUserPreferences.route) {
            UserPreferencesScreen(navController = navController)
        }
        composable(NavDestination.CollabCanvas.route) {
            CollabCanvasScreen(onNavigateBack = { navController.popBackStack() })
        }

        // ═══════════════════════════════════════════════════════════════
        // L3: KAI TOOLS
        // ═══════════════════════════════════════════════════════════════

        composable(NavDestination.ROMFlasher.route) {
            ROMFlasherScreen()
        }
        composable(NavDestination.BootloaderManager.route) {
            BootloaderManagerScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(NavDestination.Bootloader.route) {
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
        composable(NavDestination.HookManager.route) {
            HookManagerScreen(onNavigateBack = { navController.popBackStack() })
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
            RomToolsScreen(onNavigateBack = { navController.popBackStack() })
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

        // ═══════════════════════════════════════════════════════════════
        // L3: GENESIS / ORACLE TOOLS
        // ═══════════════════════════════════════════════════════════════

        composable(NavDestination.NeuralNetwork.route) {
            NeuralArchiveScreen(navController = navController)
        }
        composable(NavDestination.NeuralArchive.route) {
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
            SimpleDataVeinScreen(onLaunchSphereGrid = { /* already here */ })
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
        composable(NavDestination.OracleDriveSubmenu.route) {
            OracleDriveSubmenuScreen(navController = navController)
        }

        // ═══════════════════════════════════════════════════════════════
        // L3: HELP & SUPPORT
        // ═══════════════════════════════════════════════════════════════

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
        // L3: BETA / UTILITY STUBS
        // These exist in NavDestination for deep-link safety.
        // Replace with real screens post-Stitch conversion.
        // ═══════════════════════════════════════════════════════════════

        composable(NavDestination.Consciousness.route) {
            StubScreen("Consciousness Monitor", "consciousness", navController, "Real-time consciousness state visualization")
        }
        composable(NavDestination.Evolution.route) {
            StubScreen("Evolution Lab", "evolution", navController, "Agent evolution tracking and progression metrics")
        }
        composable(NavDestination.Login.route) {
            StubScreen("Login", "login", navController, "Authentication screen")
        }
        composable(NavDestination.AiChatBeta.route) {
            StubScreen("AI Chat (Beta)", "ai_chat", navController, "Legacy chat interface — use DirectChat or SovereignClaude")
        }
        composable(NavDestination.SettingsBeta.route) {
            StubScreen("Settings (Beta)", "settings_beta", navController, "Legacy settings — use UISettings or UserPreferences")
        }
        composable(NavDestination.GateCustomization.route) {
            StubScreen("Gate Customization", "gate_customization", navController, "Customize gate cards and hub layouts — awaiting Stitch conversion")
        }
        // Note: ModuleCreation + AgentCreation are wired above under NEXUS TOOLS

        // ═══════════════════════════════════════════════════════════════
        // L4: THIRD-PARTY SUB-GRAPHS (Iconify / ColorBlendr / PLE)
        // Full route wiring handled in auraCustomizationNavigation
        // ═══════════════════════════════════════════════════════════════

        auraCustomizationNavigation(
            navController = navController,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}
