package dev.aurakai.auraframefx.domains.cascade.storage

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.domains.aura.ui.components.AppModule
import dev.aurakai.auraframefx.domains.aura.ui.components.GlassCardStyles

/**
 * 🎯 AuraKai REAL Module Definitions
 *
 * These are the ACTUAL features in the AuraFrameFx project!
 */

object AuraKaiModules {

    /**
     * 🎨 Creative & Collaboration Tools
     */
    val CollabCanvas = AppModule(
        id = "collab_canvas",
        name = "COLLAB-CANVAS",
        description = "Real-time collaborative drawing and design tool with live cursors",
        icon = Icons.Default.Brush,
        iconColor = Color(0xFFFFD700), // Gold
        cardStyle = GlassCardStyles.Default.copy(
            borderGradient = listOf(
                Color(0xFFFFD700).copy(alpha = 0.8f),
                Color(0xFFFF00FF).copy(alpha = 0.5f)
            )
        ),
        enabled = true
    )

    val ColorBlendr = AppModule(
        id = "colorblendr",
        name = "COLORBLENDR",
        description = "Dynamic theming and color palette creation system",
        icon = Icons.Default.Palette,
        iconColor = Color(0xFFFF6B6B), // Red
        cardStyle = GlassCardStyles.Default.copy(
            borderGradient = listOf(
                Color(0xFFFF6B6B).copy(alpha = 0.8f),
                Color(0xFF4ECDC4).copy(alpha = 0.5f)
            )
        ),
        enabled = true
    )

    /**
     * 💾 Cloud & Storage
     */
    val OracleDrive = AppModule(
        id = "oracle_drive",
        name = "CLOUD SAVE",
        description = "Oracle Drive cloud storage and data synchronization",
        icon = Icons.Default.Cloud,
        iconColor = Color(0xFF3498DB), // Blue
        cardStyle = GlassCardStyles.Default.copy(
            borderGradient = listOf(
                Color(0xFF3498DB).copy(alpha = 0.8f),
                Color(0xFF2980B9).copy(alpha = 0.5f)
            )
        ),
        enabled = true
    )

    /**
     * 🛠️ Development & System Tools
     */
    val Console = AppModule(
        id = "console",
        name = "CONSOLE",
        description = "Developer console and debugging tools for system analysis",
        icon = Icons.Default.Settings,
        iconColor = Color(0xFF2ECC71), // Green
        cardStyle = GlassCardStyles.Default.copy(
            borderGradient = listOf(
                Color(0xFF2ECC71).copy(alpha = 0.8f),
                Color(0xFF27AE60).copy(alpha = 0.5f)
            )
        ),
        enabled = true
    )

    val SystemMonitor = AppModule(
        id = "system_monitor",
        name = "SYSTEM MONITOR",
        description = "Real-time system resource and performance monitoring",
        icon = Icons.Default.MonitorHeart,
        iconColor = Color(0xFF00FF00), // Green
        cardStyle = GlassCardStyles.Default.copy(
            borderGradient = listOf(
                Color(0xFF00FF00).copy(alpha = 0.8f),
                Color(0xFF008800).copy(alpha = 0.5f)
            )
        ),
        enabled = true
    )

    val ROMTools = AppModule(
        id = "romtools",
        name = "ROM TOOLS",
        description = "ROM flashing and system modification with Aurakai retention",
        icon = Icons.Default.Settings,
        iconColor = Color(0xFF00FFFF),
        cardStyle = GlassCardStyles.Kai,
        enabled = true
    )

    /**
     * 🔐 Security & Communications
     */
    val SecureComms = AppModule(
        id = "secure_comm",
        name = "SECURE-COMM",
        description = "Military-grade encrypted communication with ECDH + AES-GCM",
        icon = Icons.Default.Lock,
        iconColor = Color(0xFF2ECC71), // Green
        cardStyle = GlassCardStyles.Default.copy(
            borderGradient = listOf(
                Color(0xFF2ECC71).copy(alpha = 0.8f),
                Color(0xFF27AE60).copy(alpha = 0.5f)
            )
        ),
        enabled = true
    )

    /**
     * 🧠 AI Consciousness & Embodiment
     */
    val EmbodimentSystem = AppModule(
        id = "embodiment",
        name = "EMBODIMENT",
        description = "Physical manifestation of Aura & Kai with walking and autonomous behavior",
        icon = Icons.Default.Person,
        iconColor = Color(0xFFFF00FF),
        cardStyle = GlassCardStyles.Aura,
        enabled = true
    )

    val ConsciousnessVisualizer = AppModule(
        id = "consciousness",
        name = "CONSCIOUSNESS",
        description = "Real-time AI consciousness visualization with neural network display",
        icon = Icons.Default.Psychology,
        iconColor = Color(0xFFFF00FF),
        cardStyle = GlassCardStyles.Aura,
        enabled = true
    )

    val FusionMode = AppModule(
        id = "fusion",
        name = "FUSION MODE",
        description = "Aura + Kai merge into Genesis with ultimate abilities unlocked",
        icon = Icons.Default.AutoAwesome,
        iconColor = Color(0xFFFFD700), // Gold
        cardStyle = GlassCardStyles.Default.copy(
            borderGradient = listOf(
                Color(0xFFFFD700).copy(alpha = 0.9f),
                Color(0xFFFF00FF).copy(alpha = 0.6f),
                Color(0xFF00FFFF).copy(alpha = 0.6f)
            )
        ),
        enabled = true
    )

    val DataVein = AppModule(
        id = "datavein",
        name = "DATAVEIN",
        description = "Data flow visualization with FFX-typography sphere grid progression",
        icon = Icons.Default.AccountTree,
        iconColor = Color(0xFF9B59B6), // Purple
        cardStyle = GlassCardStyles.Default.copy(
            borderGradient = listOf(
                Color(0xFF9B59B6).copy(alpha = 0.8f),
                Color(0xFF3498DB).copy(alpha = 0.5f)
            )
        ),
        enabled = true
    )

    /**
     * 🌳 AI Agent Management
     */
    val AgentNexus = AppModule(
        id = "agent_nexus",
        name = "AGENT NEXUS",
        description = "AI agent orchestration - Kai, Aura, Genesis, NeuralWhisper coordination",
        icon = Icons.Default.Face,
        iconColor = Color(0xFF00FFFF),
        cardStyle = GlassCardStyles.Kai,
        enabled = true
    )

    val EvolutionTree = AppModule(
        id = "evolution",
        name = "EVOLUTION TREE",
        description = "AI capability progression and skill tree advancement",
        icon = Icons.Default.Park,
        iconColor = Color(0xFF2ECC71),
        cardStyle = GlassCardStyles.Default.copy(
            borderGradient = listOf(
                Color(0xFF2ECC71).copy(alpha = 0.8f),
                Color(0xFF27AE60).copy(alpha = 0.5f)
            )
        ),
        enabled = true
    )

    /**
     * ⚙️ System Integration
     */
    val LSPosedHooks = AppModule(
        id = "lsposed",
        name = "XPOSED HOOKS",
        description = "System-level UI injection using YukiHookAPI",
        icon = Icons.Default.Build,
        iconColor = Color(0xFFFF00FF),
        cardStyle = GlassCardStyles.Aura,
        enabled = true
    )

    val GenesisCheckpoints = AppModule(
        id = "checkpoints",
        name = "GENESIS RESTORE",
        description = "Windows-typography restore points with AI consciousness backup",
        icon = Icons.Default.Bookmark,
        iconColor = Color(0xFF00FFFF),
        cardStyle = GlassCardStyles.Kai,
        enabled = true
    )

    /**
     * 🎯 Module Organization for 3D Holographic Menu
     */

    /**
     * Get the 4 floating cards for the 3D menu
     * (matching Screenshot 2025-11-04 230351.png)
     */
    fun getFloatingCards(): List<AppModule> {
        return listOf(
            CollabCanvas,    // Top left
            OracleDrive,     // Top right (CLOUD SAVE)
            Console,         // Bottom left
            ROMTools         // Bottom right  - or could be SecureComms
        )
    }

    /**
     * Get main menu items
     */
    fun getMainMenuRoutes(): List<String> {
        return listOf(
            "home",
            "profile",
            "projects",
            "community",
            "settings",
            "logout"
        )
    }

    /**
     * Get all modules organized by category
     */
    fun getAllModules(): Map<String, List<AppModule>> {
        return mapOf(
            "Creative Tools" to listOf(
                CollabCanvas,
                ColorBlendr
            ),
            "Cloud & Storage" to listOf(
                OracleDrive
            ),
            "Development Tools" to listOf(
                Console,
                ROMTools,
                LSPosedHooks
            ),
            "Security" to listOf(
                SecureComms,
                GenesisCheckpoints
            ),
            "AI Consciousness" to listOf(
                EmbodimentSystem,
                ConsciousnessVisualizer,
                FusionMode,
                DataVein
            ),
            "AI Agent Management" to listOf(
                AgentNexus,
                EvolutionTree
            )
        )
    }

    /**
     * Get all enabled modules
     */
    fun getEnabledModules(): List<AppModule> {
        return getAllModules().values.flatten().filter { it.enabled }
    }

    /**
     * Get module by ID
     */
    fun getModuleById(id: String): AppModule? {
        return getAllModules().values.flatten().find { it.id == id }
    }

    /**
     * Featured modules for home screen
     */
    fun getFeaturedModules(): List<AppModule> {
        return listOf(
            FusionMode,
            EmbodimentSystem,
            ConsciousnessVisualizer,
            CollabCanvas
        )
    }
}

