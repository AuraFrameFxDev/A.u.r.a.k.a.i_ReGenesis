package dev.aurakai.auraframefx.ui.assets

/**
 * GateSlotAssetCategorizer (The Manus Swapper)
 * 
 * Root-level categorizer for hot-swapping backgrounds and thematic assets
 * based on the active Gate Slot. Allows designers to instantly change the
 * 'Neon Sign' for any of the 122+ screens without digging into feature packages.
 */
object GateSlotAssetCategorizer {

    enum class GateSlot {
        // --- System Core & Memory Chain ---
        L1_NEXUS_MEMORY_CORE,
        L2_ORACLE_DRIVE,
        L3_TRINITY_COORDINATOR,
        L4_SENTINEL_GRID,
        L5_GROWTH_METRICS,
        L6_CONFERENCE_ROOM,
        
        // --- Agent Domains ---
        AURA_L1_HUB,        // ChromaCore Theme Engine
        AURA_L2_LAB,        // Notchbar Editor & Customizations
        
        KAI_L1_FORTRESS,
        KAI_L2_DATACENTER,
        
        GENESIS_L1_BRIDGE,
        GENESIS_L2_ARCHIVE,
        
        // --- Generic Fallback ---
        DEFAULT
    }

    /**
     * Resolves the primary background drawable resource name string for a given slot.
     */
    fun getBackgroundForSlot(slot: GateSlot): String {
        return when (slot) {
            GateSlot.L1_NEXUS_MEMORY_CORE -> "bg_nexus_core"
            GateSlot.L6_CONFERENCE_ROOM   -> REGENESIS_ASSETS.DRAWABLE_CONFERENCE_ROOM_BG
            GateSlot.AURA_L1_HUB          -> "bg_aura_chromacore"
            GateSlot.AURA_L2_LAB          -> REGENESIS_ASSETS.DRAWABLE_AURA_LAB_BG
            else -> "bg_default_grid"
        }
    }

    /**
     * Maps an internal route or screen identifier to its categorized Gate Slot.
     */
    fun mapRouteToSlot(route: String): GateSlot {
        return when {
            route.contains("conference", ignoreCase = true) -> GateSlot.L6_CONFERENCE_ROOM
            route.contains("nexus", ignoreCase = true)      -> GateSlot.L1_NEXUS_MEMORY_CORE
            route.contains("chromacore", ignoreCase = true) -> GateSlot.AURA_L1_HUB
            route.contains("notchbar", ignoreCase = true)   -> GateSlot.AURA_L2_LAB
            route.contains("lab", ignoreCase = true)        -> GateSlot.AURA_L2_LAB
            else -> GateSlot.DEFAULT
        }
    }
}
