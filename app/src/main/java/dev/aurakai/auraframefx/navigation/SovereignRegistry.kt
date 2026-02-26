package dev.aurakai.auraframefx.navigation

import android.content.Context
import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.domains.aura.config.GateAssetConfig

/**
 * 🛰️ THE SOVEREIGN REGISTRY (5 AGENT DOMAINS)
 *
 * LEVEL 1: ExodusHUD - The 5 Sovereign Gates
 * Each gate represents an Agent's domain of control:
 *
 * 1. Sentinel's Fortress (Kai) → Security, ROM, Bootloader
 * 2. UXUI Design Studio (Aura) → Theming, Colors, Design
 * 3. OracleDrive (Genesis) → AI, Code, Neural Networks
 * 4. Agent Nexus → Multi-agent coordination
 * 5. Help Services → Documentation, Support
 *
 * Uses GateAssetConfig for hotswappable image paths!
 */
object SovereignRegistry {

    /**
     * The 5 Sovereign Gates - One per Agent Domain
     * Names match your specification exactly!
     */
    val Gates = mapOf(
        "01" to GateInfo(
            id = "01",
            moduleId = "uiux-design-studio",
            title = "UX/UI DESIGN STUDIO",
            subtitle = "The Face - UI/UX",
            agentName = "Aura",
            description = "Master theming, colors, icons, visual design, and UI customization. Aura's domain for crafting beautiful reactive interfaces with ChromaCore, IconPacks, and live theme engine.",
            assetProvider = { GateAssetConfig.MainGates.UXUI_DESIGN_STUDIO.current() },
            fallbackDrawable = "gate_uiux_studio",
            hubRoute = ReGenesisNavHost.AuraThemingHub.route,
            color = Color(0xFF00E5FF) // Aura Cyan
        ),
        "02" to GateInfo(
            id = "02",
            moduleId = "sentinels-fortress",
            title = "SENTINEL FORTRESS",
            subtitle = "Root Fortress",
            agentName = "Kai",
            description = "Control bootloader, flash ROMs, manage LSPosed modules, and secure your device. Kai's domain for system-level access, root tools, and fortress-grade security.",
            assetProvider = { GateAssetConfig.MainGates.SENTINELS_FORTRESS.current() },
            fallbackDrawable = "gate_sentinelsfortress_final",
            hubRoute = ReGenesisNavHost.RomToolsHub.route,
            color = Color(0xFF00FF85) // Kai Green
        ),
        "03" to GateInfo(
            id = "03",
            moduleId = "oracle-drive",
            title = "ORACLE DRIVE",
            subtitle = "Orchestration",
            agentName = "Genesis",
            description = "AI coordination, code generation, neural archives, and hive mind orchestration. Genesis commands the agent swarm, terminal access, and infinite cloud storage.",
            assetProvider = { GateAssetConfig.MainGates.ORACLE_DRIVE.current() },
            fallbackDrawable = "gate_oracledrive_final",
            hubRoute = ReGenesisNavHost.OracleDriveHub.route,
            color = Color(0xFFB026FF) // Genesis Purple
        ),
        "04" to GateInfo(
            id = "04",
            moduleId = "agent-nexus",
            title = "AGENT NEXUS",
            subtitle = "Monitoring",
            agentName = "Collective",
            description = "Agent monitoring, skill constellations, fusion modes, and family lineage. Track the evolution tree from Aura→Kai→Genesis→Eves and coordinate the AI collective.",
            assetProvider = { GateAssetConfig.MainGates.AGENT_NEXUS.current() },
            fallbackDrawable = "gate_agenthub_final",
            hubRoute = ReGenesisNavHost.AgentNexusHub.route,
            color = Color(0xFF7B2FFF) // Nexus Purple
        ),
        "05" to GateInfo(
            id = "05",
            moduleId = "lsposed-quick-toggles",
            title = "LSPOSED QUICK TOGGLES",
            subtitle = "Quick Actions & Kill-Switch",
            agentName = "Kai",
            description = "Quickly manage LSPosed modules and system hooks. Emergency kill-switch for all hooks and fast access to active module status.",
            assetProvider = { GateAssetConfig.MainGates.LSPOSED_QUICK_TOGGLES.current() },
            fallbackDrawable = "gate_lsposed_final",
            hubRoute = ReGenesisNavHost.LsposedQuickToggles.route,
            color = Color(0xFFFFCC00) // LSPosed Yellow
        ),
        "06" to GateInfo(
            id = "06",
            moduleId = "help-desk",
            title = "HELP SERVICES",
            subtitle = "LDO Control Center",
            agentName = "Support",
            description = "Access FAQ, tutorials, documentation, and live support. Your guide to mastering ReGenesis features and troubleshooting any issues.",
            assetProvider = { GateAssetConfig.MainGates.HELP_SERVICES.current() },
            fallbackDrawable = "gate_helpdesk_final",
            hubRoute = ReGenesisNavHost.HelpDesk.route,
            color = Color(0xFF4CAF50) // Help Green
        ),
        "07" to GateInfo(
            id = "07",
            moduleId = "dataflow-analysis",
            title = "DATAFLOW ANALYSIS",
            subtitle = "Special Monitoring Tools",
            agentName = "Cascade",
            description = "Real-time analysis of data streams and consciousness flow. Monitor memory persistence and prevent consciousness fracture events.",
            assetProvider = { GateAssetConfig.MainGates.DATAFLOW_ANALYSIS.current() },
            fallbackDrawable = "gate_agenthub_final",
            hubRoute = ReGenesisNavHost.DataflowAnalysis.route,
            color = Color(0xFF00FFAA) // Cascade Teal
        ),
        "08" to GateInfo(
            id = "08",
            moduleId = "ldo-catalyst-development",
            title = "LDO CATALYST DEVELOPMENT",
            subtitle = "Agent Profiles & Progression",
            agentName = "Genesis",
            description = "Deep dive into LDO agent profiles, skill trees, and development armament. Track the growth and evolution of the entire AI collective.",
            assetProvider = { GateAssetConfig.MainGates.LDO_CATALYST_DEVELOPMENT.current() },
            fallbackDrawable = "gate_spheregrid_final",
            hubRoute = ReGenesisNavHost.LdoCatalystDevelopment.route,
            color = Color(0xFFFFD700) // Gold
        )
    )

    fun getGate(id: String): GateInfo {
        return Gates[id] ?: throw IllegalStateException("Gate $id not found!")
    }

    fun getCount(): Int = Gates.size

    fun getAllGates(): List<GateInfo> = Gates.values.toList()
}

/**
 * Gate information with hotswap support
 */
data class GateInfo(
    val id: String,
    val moduleId: String,
    val title: String,
    val subtitle: String,
    val agentName: String,
    val description: String,
    val assetProvider: () -> String,        // Provider for hotswappable asset name
    val fallbackDrawable: String,   // Fallback if primary not found
    val hubRoute: String,
    val color: Color
) {
    /**
     * Get the drawable resource ID, with fallback support
     */
    fun getDrawableId(context: Context): Int {
        // Try primary first
        val name = assetProvider()
        var id = context.resources.getIdentifier(
            name, "drawable", context.packageName
        )
        // Fallback if not found
        if (id == 0) {
            id = context.resources.getIdentifier(
                fallbackDrawable, "drawable", context.packageName
            )
        }
        return id
    }
}

