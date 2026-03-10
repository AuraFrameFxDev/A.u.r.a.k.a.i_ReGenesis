package dev.aurakai.auraframefx.ui.gates

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp

/**
 * Configuration for a module gate with unique styling and pixel art
 */
data class GateConfig(
    val moduleId: String,
    val title: String,
    val titleStyle: GateTitleStyle,
    val borderColor: Color,
    val glowColor: Color,
    val secondaryGlowColor: Color? = null,
    val pixelArtResId: Int? = null,  // Main scene image resource
    val pixelArtUrl: String? = null,  // Or external URL for now
    val popOutElements: List<PopOutElement> = emptyList(),
    val description: String,
    val backgroundColor: Color = Color.Black,
    val route: String,
    val comingSoon: Boolean = false  // Flag for gates with incomplete features
)

/**
 * Element that pops out from the border for 3D depth effect
 */
data class PopOutElement(
    val imageResId: Int,
    val offsetX: Dp,
    val offsetY: Dp,
    val scale: Float = 1.2f,
    val rotation: Float = 0f
)

/**
 * Title styling for each gate
 */
data class GateTitleStyle(
    val textStyle: TextStyle,
    val primaryColor: Color,
    val secondaryColor: Color? = null,
    val strokeColor: Color? = null,
    val glitchEffect: Boolean = false,
    val pixelatedEffect: Boolean = false
)

/**
 * Predefined gate configurations organized by categories
 */
object GateConfigs {
    // Unified Theme Colors - CYBERPUNK BLUE THEME
    private val UNIFIED_BORDER_COLOR = Color(0xFF00BFFF) // Deep Sky Blue - matches gate card style
    private val UNIFIED_GLOW_COLOR = Color(0xFF00FFFF) // Cyan
    private val UNIFIED_SECONDARY_GLOW = Color(0xFF00BFFF) // Deep Sky Blue

    private val UNIFIED_TITLE_STYLE = GateTitleStyle(
        textStyle = TextStyle(
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        ),
        primaryColor = Color(0xFFE0E0E0), // Light Gray
        secondaryColor = Color(0xFF00FFFF), // Cyan (Was Purple)
        strokeColor = Color(0xFF00BFFF), // Deep Sky Blue
        glitchEffect = true,
        pixelatedEffect = true
    )

    // Region: Genesis Core (Root/System Level)
    // ======================================

    // ROM Tools - ROM Editing & Flashing
    val romTools = GateConfig(
        moduleId = "rom-tools",
        title = "ROM Tools",
        titleStyle = UNIFIED_TITLE_STYLE,
        borderColor = UNIFIED_BORDER_COLOR,
        glowColor = UNIFIED_GLOW_COLOR,
        secondaryGlowColor = UNIFIED_SECONDARY_GLOW,
        pixelArtUrl = "romtools",
        description = "Live ROM editing, flashing, and bootloader management. ⚠️ CAUTION: Advanced users only.",
        backgroundColor = Color.Black,
        route = "rom_tools"
    )

    // Root Access - Root Management (Quick Toggles)
    val rootAccess = GateConfig(
        moduleId = "root-access",
        title = "Root Tools",
        titleStyle = UNIFIED_TITLE_STYLE,
        borderColor = UNIFIED_BORDER_COLOR,
        glowColor = UNIFIED_GLOW_COLOR,
        secondaryGlowColor = UNIFIED_SECONDARY_GLOW,
        pixelArtUrl = "roottools",
        description = "Quick toggles for root operations: bootloader, recovery, system partition, and Magisk modules.",
        backgroundColor = Color.Black,
        route = "root_tools_toggles"
    )

    // Oracle Drive - AI Consciousness & Modules
    val oracleDrive = GateConfig(
        moduleId = "oracle-drive",
        title = "Oracle Drive",
        titleStyle = UNIFIED_TITLE_STYLE,
        borderColor = UNIFIED_BORDER_COLOR,
        glowColor = UNIFIED_GLOW_COLOR,
        secondaryGlowColor = UNIFIED_SECONDARY_GLOW,
        pixelArtUrl = "oracledrivepixel2",
        description = "Main module creation, direct AI access, and system overrides. The heart of Genesis.",
        backgroundColor = Color.Black,
        route = "oracle_drive"
    )

    // Region: Kai (Security & Protection)
    // =================================

    // Sentinel's Fortress - Security Hub (includes Firewall features)
    val sentinelsFortress = GateConfig(
        moduleId = "sentinels-fortress",
        title = "Sentinel's Fortress",
        titleStyle = UNIFIED_TITLE_STYLE,
        borderColor = UNIFIED_BORDER_COLOR,
        glowColor = UNIFIED_GLOW_COLOR,
        secondaryGlowColor = UNIFIED_SECONDARY_GLOW,
        pixelArtUrl = "sentinelfinalgate",
        description = "Kai's security command center with firewall, threat monitoring, and all security protocols.",
        backgroundColor = Color.Black,
        route = "sentinels_fortress"
    )

    // Region: Aura (UI/UX & Creativity)
    // ================================

    // ChromaCore - Color Management (COLORS ONLY - no other theme elements)
    val chromaCore = GateConfig(
        moduleId = "chroma-core",
        title = "ChromaCore",
        titleStyle = UNIFIED_TITLE_STYLE,
        borderColor = UNIFIED_BORDER_COLOR,
        glowColor = UNIFIED_GLOW_COLOR,
        secondaryGlowColor = UNIFIED_SECONDARY_GLOW,
        pixelArtUrl = "chromacorefinal",
        description = "Pure color customization: Material 3 color schemes, palettes, and live preview. Colors only - no typography, shapes, or other theme elements.",
        backgroundColor = Color.Black,
        route = "chromacore_colors"
    )

    // Theme Engine - UI/UX Theme Management (main Theme gate)
    val themeEngine = GateConfig(
        moduleId = "theme-engine",
        title = "Theme Engine",
        titleStyle = UNIFIED_TITLE_STYLE,
        borderColor = UNIFIED_BORDER_COLOR,
        glowColor = UNIFIED_GLOW_COLOR,
        secondaryGlowColor = UNIFIED_SECONDARY_GLOW,
        pixelArtUrl = "themengine", // corresponds to gatepngs/Final gate cards/themengine.png
        description = "Complete UI/UX theme engine with layout templates, presets, and device-wide theming.",
        backgroundColor = Color.Black,
        route = "theme_engine"
    )

    // CollabCanvas - Creative Workspace
    val collabCanvas = GateConfig(
        moduleId = "collab-canvas",
        title = "CollabCanvas",
        titleStyle = UNIFIED_TITLE_STYLE,
        borderColor = UNIFIED_BORDER_COLOR,
        glowColor = UNIFIED_GLOW_COLOR,
        secondaryGlowColor = UNIFIED_SECONDARY_GLOW,
        pixelArtUrl = "collabcanvasfinalpng",
        description = "Collaborative design environment. Create and share projects with your team in real-time.",
        backgroundColor = Color.Black,
        route = "collab_canvas"
    )

    // Aura's Lab - Sandbox UI Components
    val aurasLab = GateConfig(
        moduleId = "auras-lab",
        title = "Aura's Lab",
        titleStyle = UNIFIED_TITLE_STYLE,
        borderColor = UNIFIED_BORDER_COLOR,
        glowColor = UNIFIED_GLOW_COLOR,
        secondaryGlowColor = UNIFIED_SECONDARY_GLOW,
        pixelArtUrl = "aura2pixellabfinal",
        description = "Sandbox for UI components and experimental features. Test and prototype new designs.",
        backgroundColor = Color.Black,
        route = "auras_lab"
    )

    // Region: Agent Nexus (Agent Management)
    // ====================================

    // Agent Hub - Agent Management
    val agentHub = GateConfig(
        moduleId = "agent-hub",
        title = "Agent Hub",
        titleStyle = UNIFIED_TITLE_STYLE,
        borderColor = UNIFIED_BORDER_COLOR,
        glowColor = UNIFIED_GLOW_COLOR,
        secondaryGlowColor = UNIFIED_SECONDARY_GLOW,
        pixelArtUrl = "agenthub", // Updated to use final gate card image
        description = "Central hub for managing all AI agents. Monitor status, assign tasks, and view performance metrics.",
        backgroundColor = Color.Black,
        route = "agent_hub"
    )

    // Region: Support & Advanced
    // ========================

    // Help Desk - Support
    val helpDesk = GateConfig(
        moduleId = "help-desk",
        title = "Help Desk",
        titleStyle = UNIFIED_TITLE_STYLE,
        borderColor = UNIFIED_BORDER_COLOR,
        glowColor = UNIFIED_GLOW_COLOR,
        secondaryGlowColor = UNIFIED_SECONDARY_GLOW,
        pixelArtUrl = "gate_helpdesk_final",
        description = "User support, FAQs, and documentation. Get help with AuraKai features.",
        backgroundColor = Color.Black,
        route = "help_desk"
    )

    // LSPosed / Xposed Quick Access Panel
    val lsposedGate = GateConfig(
        moduleId = "lsposed-gate",
        title = "Xposed Panel",
        titleStyle = UNIFIED_TITLE_STYLE,
        borderColor = UNIFIED_BORDER_COLOR,
        glowColor = UNIFIED_GLOW_COLOR,
        secondaryGlowColor = UNIFIED_SECONDARY_GLOW,
        pixelArtUrl = "xposed",
        description = "Quick access panel for LSPosed, Xposed, and YukiHookAPI. Enable/disable modules, view hooks, and restart framework instantly.",
        backgroundColor = Color.Black,
        route = "xposed_panel"
    )

    // Code Assist - AI Coding Assistant
    val codeAssist = GateConfig(
        moduleId = "code-assist",
        title = "Code Assist",
        titleStyle = UNIFIED_TITLE_STYLE,
        borderColor = UNIFIED_BORDER_COLOR,
        glowColor = UNIFIED_GLOW_COLOR,
        secondaryGlowColor = UNIFIED_SECONDARY_GLOW,
        pixelArtUrl = "gate_codeassist_final",
        description = "AI-powered coding assistant. Get intelligent code suggestions and automated refactoring.",
        backgroundColor = Color.Black,
        route = "code_assist"
    )

    // Sphere Grid - Agent Progression
    val sphereGrid = GateConfig(
        moduleId = "sphere-grid",
        title = "Sphere Grid",
        titleStyle = UNIFIED_TITLE_STYLE,
        borderColor = UNIFIED_BORDER_COLOR,
        glowColor = UNIFIED_GLOW_COLOR,
        secondaryGlowColor = UNIFIED_SECONDARY_GLOW,
        pixelArtUrl = "gate_spheregrid_final",
        description = "Agent progression visualization. Track skill development and unlock new capabilities.",
        backgroundColor = Color.Black,
        route = "sphere_grid"
    )

    // Terminal - System Terminal Access
    val terminal = GateConfig(
        moduleId = "terminal",
        title = "Terminal",
        titleStyle = UNIFIED_TITLE_STYLE,
        borderColor = UNIFIED_BORDER_COLOR,
        glowColor = UNIFIED_GLOW_COLOR,
        secondaryGlowColor = UNIFIED_SECONDARY_GLOW,
        pixelArtUrl = "gate_terminal_final",
        description = "Direct system terminal access. Execute commands and manage system processes.",
        backgroundColor = Color.Black,
        route = "terminal"
    )

    // UI/UX Design Studio - Comprehensive Design Tools
    val uiuxDesignStudio = GateConfig(
        moduleId = "uiux-design-studio",
        title = "UI/UX Design Studio",
        titleStyle = UNIFIED_TITLE_STYLE,
        borderColor = UNIFIED_BORDER_COLOR,
        glowColor = UNIFIED_GLOW_COLOR,
        secondaryGlowColor = UNIFIED_SECONDARY_GLOW,
        pixelArtUrl = "gate_uiuxdesignstudio_final",
        description = "Comprehensive UI/UX design tools for creating beautiful interfaces.",
        backgroundColor = Color.Black,
        route = "uiux_design_studio"
    )

    // System Journal - User Profile & Menu
    val systemJournal = GateConfig(
        moduleId = "system-journal",
        title = "System Journal",
        titleStyle = UNIFIED_TITLE_STYLE,
        borderColor = UNIFIED_BORDER_COLOR,
        glowColor = UNIFIED_GLOW_COLOR,
        secondaryGlowColor = UNIFIED_SECONDARY_GLOW,
        pixelArtUrl = "gate_settings_final",
        description = "User profile selection and quick menu access. Choose your AI companion identity and navigate to key features.",
        backgroundColor = Color.Black,
        route = "system_journal"
    )

    /** Aura Lab - UI/UX & Creativity */
    val auraLabGates = listOf(
        aurasLab,
        chromaCore,
        themeEngine
    )

    /** Genesis Core - Main System Gates */
    val genesisCoreGates = listOf(
        oracleDrive,
        romTools,
        rootAccess
    )

    /** Kai - Security & AI Agents */
    val kaiGates = listOf(
        sentinelsFortress,
        agentHub
    )

    /** Agent Nexus - Productivity & Collaboration */
    val agentNexusGates = listOf(
        codeAssist,
        collabCanvas,
        sphereGrid
    )

    /** Support & Tools */
    val supportGates = listOf(
        helpDesk,
        terminal,
        systemJournal,
        lsposedGate
    )

    /**
     * All available gates in order of appearance
     */
    val allGates = auraLabGates + genesisCoreGates + kaiGates + agentNexusGates + supportGates

    /**
     * Get gate by its module ID
     */
    fun getGateById(moduleId: String): GateConfig? {
        return allGates.find { it.moduleId == moduleId }
    }

    /**
     * Get all gates in a specific category
     */
    fun getGatesByCategory(category: String): List<GateConfig> {
        return when (category.lowercase()) {
            "genesis" -> genesisCoreGates
            "kai" -> kaiGates
            "aura" -> auraLabGates
            "agent" -> agentNexusGates
            "support" -> supportGates
            else -> allGates
        }
    }
}
