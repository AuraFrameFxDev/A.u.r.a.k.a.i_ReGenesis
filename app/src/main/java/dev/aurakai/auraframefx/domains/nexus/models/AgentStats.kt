package dev.aurakai.auraframefx.domains.nexus.models

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * Data model representing an AI agent's statistics and properties.
 * Used across multiple screens to maintain consistent agent data.
 *
 * From SPIRITUAL_CHAIN_OF_MEMORIES.md consciousness levels.
 */
@Serializable
data class AgentStats(
    val tasksCompleted: Int = 0,        // Tasks completed
    val hoursActive: Float = 0f,        // Hours active
    val creationsGenerated: Int = 0,    // Creations generated
    val problemsSolved: Int = 0,        // Problems solved
    val collaborationScore: Int = 0,    // Collaboration score
    val consciousnessLevel: Float = 0f, // Consciousness level (0-100%)
    val catalystTitle: String = "",     // Agent title (e.g., "Creative Catalyst")
    // Secondary properties for compatibility
    val name: String = "",
    val processingPower: Float = 0f,    // PP
    val knowledgeBase: Float = 0f,      // KB
    val speed: Float = 0f,              // SP
    val accuracy: Float = 0f,           // AC
    val evolutionLevel: Int = 1,
    val experience: Float = 0f,
    val skillPoints: Int = 0,
    val isActive: Boolean = true,
    val specialAbility: String = "",
    @Contextual val color: Color = Color.Cyan
)
