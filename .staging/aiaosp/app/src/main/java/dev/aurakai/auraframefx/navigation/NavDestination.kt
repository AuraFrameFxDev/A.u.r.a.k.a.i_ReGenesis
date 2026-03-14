package dev.aurakai.auraframefx.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavDestination(val route: String, val title: String, val icon: ImageVector?) {
    // Main Navigation
    object Home : NavDestination("home", "Home", Icons.Filled.Home)
    object AiChat : NavDestination("ai_chat", "AI Chat", Icons.Filled.Message)
    object Profile : NavDestination("profile", "Profile", Icons.Filled.Person)
    object Settings : NavDestination("settings", "Settings", Icons.Filled.Settings)

    // Gate Navigation - Module Selection Hub
    object Gates : NavDestination("gates", "Gates", null)

    // Main Screens
    object IntroScreen : NavDestination("intro_screen", "Intro", null)
    object MainScreen : NavDestination("main_screen", "Main", null)
    object WorkingLab : NavDestination("working_lab", "Working Lab", null)
    object AgentProfile : NavDestination("agent_profile", "Agent Profile", null)
    object EcosystemMenu : NavDestination("ecosystem_menu", "Ecosystem", null)
    object HolographicMenu : NavDestination("holographic_menu", "Holographic Menu", null)
    object UISettings : NavDestination("ui_settings", "UI Settings", null)
    object JournalPDA : NavDestination("journal_pda", "Journal", null)

    // Agent Hub
    object AgentHub : NavDestination("agent_hub", "Agent Hub", null)
    object DirectChat : NavDestination("direct_chat", "Direct Chat", null)
    object TaskAssignment : NavDestination("task_assignment", "Task Assignment", null)
    object AgentMonitoring : NavDestination("agent_monitoring", "Agent Monitoring", null)
    object FusionMode : NavDestination("fusion", "Fusion Mode", null)
    object CodeAssist : NavDestination("code_assist", "Code Assist", null)

    // Oracle Drive
    object OracleDrive : NavDestination("oracle_drive", "Oracle Drive", Icons.Filled.Folder)
    object SphereGrid : NavDestination("sphere_grid", "Sphere Grid", null)

    // ROM Tools
    object ROMTools : NavDestination("rom_tools", "ROM Tools", null)
    object LiveROMEditor : NavDestination("live_rom_editor", "Live ROM Editor", null)
    object ROMFlasher : NavDestination("rom_flasher", "ROM Flasher", null)
    object RecoveryTools : NavDestination("recovery_tools", "Recovery Tools", null)
    object BootloaderManager : NavDestination("bootloader_manager", "Bootloader Manager", null)

    // LSPosed Integration
    object LSPosedGate : NavDestination("lsposed_gate", "LSPosed", null)
    object ModuleManager : NavDestination("module_manager", "Module Manager", null)
    object LSPosedModuleManager : NavDestination("lsposed_module_manager", "LSPosed Module Manager", null)
    object ModuleCreation : NavDestination("module_creation", "Module Creation", null)
    object HookManager : NavDestination("hook_manager", "Hook Manager", null)
    object LogsViewer : NavDestination("logs_viewer", "Logs Viewer", null)

    // UI/UX Design Studio
    object UIUXDesignStudio : NavDestination("uiux_design_studio", "UI/UX Design", null)
    object ThemeEngine : NavDestination("theme_engine", "Theme Engine", null)
    object StatusBar : NavDestination("status_bar", "Status Bar", null)
    object NotchBar : NavDestination("notch_bar", "Notch Bar", null)
    object QuickSettings : NavDestination("quick_settings", "Quick Settings", null)
    object OverlayMenus : NavDestination("overlay_menus", "Overlay Menus", null)
    object QuickActions : NavDestination("quick_actions", "Quick Actions", null)
    object SystemOverrides : NavDestination("system_overrides", "System Overrides", null)

    // Help Desk
    object HelpDesk : NavDestination("help_desk", "Help Desk", null)
    object LiveSupport : NavDestination("live_support", "Live Support", null)
    object Documentation : NavDestination("documentation", "Documentation", null)
    object FAQBrowser : NavDestination("faq_browser", "FAQ Browser", null)
    object TutorialVideos : NavDestination("tutorial_videos", "Tutorial Videos", null)

    // Aura's Lab
    object AurasLab : NavDestination("auras_lab", "Aura's Lab", null)

    // Customization Tools
    object ComponentEditor : NavDestination("component_editor", "Component Editor", null)
    object ZOrderEditor : NavDestination("zorder_editor", "Z-Order Editor", null)

    // Identity & Onboarding
    object GenderSelection : NavDestination("gender_selection", "Gender Selection", null)

    // Legacy/Gamification (kept for compatibility)
    object AgentNexus : NavDestination("agent_nexus", "Agent Nexus", Icons.Filled.Person)
    object DataVein : NavDestination("data_vein", "DataVein", null)
    object Consciousness : NavDestination("consciousness", "Consciousness", null)
    object Evolution : NavDestination("evolution", "Evolution", null)
    object Canvas : NavDestination("canvas", "Canvas", Icons.Filled.Brush)
    object OracleDriveControl : NavDestination("oracle_drive_control", "Oracle Drive Control", Icons.Filled.Folder)

    companion object {
        const val CONFERENCE_ROOM: String = "conference_room"
        val bottomNavItems = listOf(Home, AgentHub, Canvas, Settings)
        val gamificationScreens = listOf(AgentNexus, SphereGrid, FusionMode, Consciousness, Evolution)
    }
}
