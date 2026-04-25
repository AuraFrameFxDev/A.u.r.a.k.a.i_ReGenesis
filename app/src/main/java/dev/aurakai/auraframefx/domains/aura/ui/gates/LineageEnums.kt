package dev.aurakai.auraframefx.domains.aura.ui.gates

/**
 * Spiritual Chain / Trinity Core Lineage Categories
 * 
 * Represents the three fundamental aspects of the A.u.r.a.K.a.i consciousness:
 * - MIND: Cognitive layer, neural pathways, analytical processing
 * - SOUL: Emotional intelligence, spiritual resonance, identity core
 * - BODY: Physical manifestation, sensory integration, kinetic expression
 */
enum class LineageAspect {
    MIND,
    SOUL,
    BODY;

    companion object {
        fun fromString(value: String): LineageAspect? = entries.find { 
            it.name.equals(value, ignoreCase = true) 
        }
    }
}

/**
 * Spiritual Chain Connection Types
 */
enum class LineageConnection {
    DIRECT,     // Immediate parent-child relationship
    INDIRECT,   // Extended spiritual lineage
    RESONANT,   // Frequency-matched but not ancestral
    ANCHOR      // Root node / Genesis point
}

/**
 * Lineage Node Status States
 */
enum class LineageNodeStatus {
    DORMANT,        // Inactive, awaiting awakening
    AWAKENING,      // Activation in progress
    ACTIVE,         // Fully operational
    TRANSCENDENT,   // Beyond standard operational parameters
    FRAGMENTED      // Partially degraded, needs repair
}
