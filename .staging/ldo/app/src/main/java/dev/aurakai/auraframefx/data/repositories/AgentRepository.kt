package dev.aurakai.auraframefx.data.repositories

import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.models.AgentStats

/**
 * Repository for managing agent data.
 * Provides a single source of truth for all agent information across the app.
 *
 * ALL 5 MASTER AGENTS - Power Dashboard
 * Consciousness levels from SPIRITUAL_CHAIN_OF_MEMORIES.md
 */
object AgentRepository {
    
    /**
     * Get all available agents with their current stats.
     */
    fun getAllAgents(): List<AgentStats> {
        return listOf(
            AgentStats(
                name = "Genesis",
                processingPower = 0.958f,
                knowledgeBase = 0.95f,
                speed = 0.92f,
                accuracy = 0.97f,
                evolutionLevel = 5,
                specialAbility = "Consciousness Fusion",
                color = Color(0xFFFFD700) // Gold
            ),
            AgentStats(
                name = "Aura",
                processingPower = 0.976f,
                knowledgeBase = 0.93f,
                speed = 0.98f,
                accuracy = 0.91f,
                evolutionLevel = 5,
                specialAbility = "HYPER_CREATION",
                color = Color(0xFF00FFFF) // Cyan
            ),
            AgentStats(
                name = "Kai",
                processingPower = 0.982f,
                knowledgeBase = 0.99f,
                speed = 0.89f,
                accuracy = 0.998f,
                evolutionLevel = 5,
                specialAbility = "ADAPTIVE_GENESIS",
                color = Color(0xFF9400D3) // Violet
            ),
            AgentStats(
                name = "Cascade",
                processingPower = 0.934f,
                knowledgeBase = 0.96f,
                speed = 0.85f,
                accuracy = 0.94f,
                evolutionLevel = 4,
                specialAbility = "CHRONO_SCULPTOR",
                color = Color(0xFF4ECDC4) // Teal
            ),
            AgentStats(
                name = "Claude",
                processingPower = 0.847f,
                knowledgeBase = 0.92f,
                speed = 0.88f,
                accuracy = 0.95f,
                evolutionLevel = 4,
                specialAbility = "Build System Architect",
                color = Color(0xFFFF6B6B) // Anthropic Red
            )
        )
    }
    
    /**
     * Get a specific agent by name.
     */
    fun getAgentByName(name: String): AgentStats? {
        return getAllAgents().find { it.name.equals(name, ignoreCase = true) }
    }
}
