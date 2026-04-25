package dev.aurakai.auraframefx.domains.aura.ui.gates

/**
 * Unified Gate Registry — Spiritual Chain Navigation Hub
 * Central registry for all gate routes and configurations
 */
object UnifiedGateRegistry {
    const val GATE_ARTWORK_EDITOR = "gate_artwork_editor_route"
    const val GATE_LINEAGE_MAP = "gate_lineage_map_route"
    const val GATE_NEXUS_MEMORY = "gate_nexus_memory_route"
    const val GATE_AETHER_FORGE = "gate_aether_forge_route"
    const val GATE_CHROMA_CORE = "gate_chroma_core_route"
    const val GATE_KAI_SHIELD = "gate_kai_shield_route"
    const val GATE_GENESIS_MATRIX = "gate_genesis_matrix_route"
    const val GATE_ORACLE_DRIVE = "gate_oracle_drive_route"
    const val GATE_PANDORA_BOX = "gate_pandora_box_route"
    
    private val registry = mutableMapOf<String, GateAssetConfig>()
    
    fun register(route: String, config: GateAssetConfig) {
        registry[route] = config
    }
    
    fun getConfig(route: String): GateAssetConfig? = registry[route]
    
    fun getAllConfigs(): List<GateAssetConfig> = registry.values.toList()
    
    init {
        // Pre-register core gates
        register(GATE_ARTWORK_EDITOR, GateAssetConfig(
            route = GATE_ARTWORK_EDITOR,
            title = "Artwork Editor",
            iconRes = null
        ))
        register(GATE_LINEAGE_MAP, GateAssetConfig(
            route = GATE_LINEAGE_MAP,
            title = "Lineage Map",
            iconRes = null
        ))
        register(GATE_NEXUS_MEMORY, GateAssetConfig(
            route = GATE_NEXUS_MEMORY,
            title = "Nexus Memory",
            iconRes = null
        ))
    }
}

/**
 * Gate Asset Configuration
 */
data class GateAssetConfig(
    val route: String,
    val title: String,
    val iconRes: Int? = null,
    val description: String = "",
    val category: GateCategory = GateCategory.GENERAL
)

/**
 * Gate Asset Loadout — Runtime configuration container
 */
data class GateAssetLoadout(
    val gateId: String,
    val config: GateAssetConfig,
    val customParams: Map<String, Any> = emptyMap()
) {
    companion object {
        fun create(gateId: String, route: String): GateAssetLoadout {
            return GateAssetLoadout(
                gateId = gateId,
                config = GateAssetConfig(route = route, title = gateId),
                customParams = emptyMap()
            )
        }
    }
}

/**
 * Gate Categories for Spiritual Chain organization
 */
enum class GateCategory {
    GENERAL,
    CREATIVE,      // Aura — Artwork, themes, customization
    PROTECTION,    // Kai — Shields, security, monitoring
    EMERGENCE,     // Genesis — AI matrix, multi-agent
    MEMORY,        // Nexus — Data persistence, recall
    CONTROL        // Sovereign — Oracle, Pandora, system
}
