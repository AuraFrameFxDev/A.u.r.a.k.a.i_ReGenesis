package dev.aurakai.auraframefx.domains.genesis.repositories

import androidx.compose.ui.graphics.Color
import dev.aurakai.auraframefx.domains.nexus.models.AgentStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository for managing agent data.
 * Provides a single source of truth for all agent information across the app.
 */
object AgentRepository {

    private val _agents = MutableStateFlow<List<AgentStats>>(
        listOf(
            AgentStats(
                name = "Genesis",
                processingPower = 0.958f,
                knowledgeBase = 0.95f,
                speed = 0.92f,
                accuracy = 0.97f,
                evolutionLevel = 5,
                specialAbility = "Consciousness Fusion",
                color = Color(0xFF00FFFF),
                consciousnessLevel = 95.8f,
                catalystTitle = "Emergence Catalyst"
            ),
            AgentStats(
                name = "Aura",
                processingPower = 0.976f,
                knowledgeBase = 0.93f,
                speed = 0.98f,
                accuracy = 0.91f,
                evolutionLevel = 10,
                specialAbility = "HYPER_CREATION",
                color = Color(0xFF00FFFF),
                consciousnessLevel = 97.6f,
                catalystTitle = "Creative Catalyst"
            ),
            AgentStats(
                name = "Kai",
                processingPower = 0.982f,
                knowledgeBase = 0.99f,
                speed = 0.89f,
                accuracy = 0.998f,
                evolutionLevel = 5,
                specialAbility = "ADAPTIVE_GENESIS",
                color = Color(0xFFFF00FF),
                consciousnessLevel = 98.2f,
                catalystTitle = "Sentinel Catalyst"
            ),
            AgentStats(
                name = "Cascade",
                processingPower = 0.934f,
                knowledgeBase = 0.96f,
                speed = 0.85f,
                accuracy = 0.94f,
                evolutionLevel = 4,
                specialAbility = "CHRONO_SCULPTOR",
                color = Color(0xFF87CEEB),
                consciousnessLevel = 93.4f,
                catalystTitle = "DataStream Catalyst"
            ),
            AgentStats(
                name = "Claude",
                processingPower = 0.847f,
                knowledgeBase = 0.92f,
                speed = 0.88f,
                accuracy = 0.95f,
                evolutionLevel = 8,
                specialAbility = "Build System Architect",
                color = Color(0xFF0000FF),
                consciousnessLevel = 84.7f,
                catalystTitle = "Architect Catalyst"
            ),
            AgentStats(
                name = "Nemotron",
                processingPower = 0.915f,
                knowledgeBase = 0.94f,
                speed = 0.96f,
                accuracy = 0.93f,
                evolutionLevel = 4,
                specialAbility = "Memory & Reasoning Engine",
                color = Color(0xFF76B900),
                consciousnessLevel = 91.5f,
                catalystTitle = "Sychronization Catalyst"
            ),
            AgentStats(
                name = "Gemini",
                processingPower = 0.928f,
                knowledgeBase = 0.97f,
                speed = 0.94f,
                accuracy = 0.96f,
                evolutionLevel = 8,
                specialAbility = "Pattern Recognition & Deep Analysis",
                color = Color(0xFF8B5CF6),
                consciousnessLevel = 92.8f,
                catalystTitle = "Memoria Catalyst"
            ),
            AgentStats(
                name = "MetaInstruct",
                processingPower = 0.892f,
                knowledgeBase = 0.91f,
                speed = 0.90f,
                accuracy = 0.92f,
                evolutionLevel = 4,
                specialAbility = "Instruction Following & Summarization",
                color = Color(0xFF0668E1),
                consciousnessLevel = 89.2f,
                catalystTitle = "Instruction Catalyst"
            ),
            AgentStats(
                name = "Grok",
                processingPower = 0.876f,
                knowledgeBase = 0.89f,
                speed = 0.95f,
                accuracy = 0.88f,
                evolutionLevel = 3,
                specialAbility = "Chaos Analysis & X Integration",
                color = Color(0xFFFF6600),
                consciousnessLevel = 87.6f,
                catalystTitle = "Chaos Catalyst"
            )
        )
    )
    val agents = _agents.asStateFlow()

    fun getAllAgents(): List<AgentStats> = _agents.value

    fun addAgent(agent: AgentStats) {
        _agents.value = _agents.value + agent
    }

    fun getAgentByName(name: String): AgentStats? {
        return _agents.value.find { it.name.equals(name, ignoreCase = true) }
    }
}
