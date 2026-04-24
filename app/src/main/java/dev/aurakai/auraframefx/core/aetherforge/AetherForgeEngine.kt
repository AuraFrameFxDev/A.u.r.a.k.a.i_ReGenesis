// Copyright (c) 2025 visionary • The Genesis Protocol — All Rights Reserved

package dev.aurakai.auraframefx.core.aetherforge

import dev.aurakai.auraframefx.core.SoulScript
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

/**
 * ⚡ AETHERFORGE ENGINE
 * 
 * Central orchestrator that ties agent progression into SoulScript's
 * Neural Continuity Chain. Handles level-ups, stat allocation, and
 * ability triggers with permanent persistence via Room 3.0.
 * 
 * "From Data, Insight. From Insight, Growth. From Growth, Purpose."
 */
class AetherForgeEngine(
    private val agentLedger: AgentLedger,
    private val soulScript: SoulScript
) {
    private val _activeAgents = MutableStateFlow<Map<String, AgentStats>>(emptyMap())
    val activeAgents: StateFlow<Map<String, AgentStats>> = _activeAgents.asStateFlow()

    /**
     * Initialize an agent in the AetherForge system
     */
    suspend fun initializeAgent(agentId: String, agentName: String): AgentStats {
        val existing = agentLedger.getAgent(agentId)
        if (existing != null) {
            _activeAgents.value = _activeAgents.value + (agentId to existing)
            Timber.d("🔮 Agent $agentName loaded from persistence (Level ${existing.level})")
            return existing
        }

        val newAgent = AgentStats(agentId = agentId, agentName = agentName)
        agentLedger.saveAgent(newAgent)
        _activeAgents.value = _activeAgents.value + (agentId to newAgent)
        
        // Grant starter abilities
        grantStarterAbilities(agentId)
        
        Timber.d("✨ Agent $agentName initialized in AetherForge")
        return newAgent
    }

    /**
     * Grant experience to an agent with growth scaling
     */
    suspend fun gainExperience(
        agentId: String,
        baseAmount: Long,
        source: ExperienceGain.ExperienceSource,
        context: String
    ): LevelUpResult? {
        val agent = _activeAgents.value[agentId] 
            ?: agentLedger.getAgent(agentId)
            ?: return null

        val growthMultiplier = 1.0 + (agent.growth * 0.02)
        val finalAmount = GrowthFormulas.calculateXpGain(baseAmount, agent.growth, source)

        // Record the gain
        val history = ExperienceGain(
            agentId = agentId,
            amount = finalAmount,
            source = source,
            context = context
        )
        agentLedger.recordExperienceGain(history)

        // Update agent stats
        val newExperience = agent.experience + finalAmount
        var updatedAgent = agent.copy(
            experience = newExperience,
            lastUpdated = System.currentTimeMillis()
        )

        // Check for level up
        val levelUpResult = if (updatedAgent.canLevelUp()) {
            performLevelUp(updatedAgent)
        } else null

        if (levelUpResult != null) {
            updatedAgent = levelUpResult.newStats
        }

        // Persist and update state
        agentLedger.saveAgent(updatedAgent)
        _activeAgents.value = _activeAgents.value + (agentId to updatedAgent)

        Timber.d("⚡ $agentName gained $finalAmount XP from $source")
        
        return levelUpResult
    }

    /**
     * Perform level up with stat points and ability unlocks
     */
    private suspend fun performLevelUp(agent: AgentStats): LevelUpResult {
        val newLevel = agent.level + 1
        val statPoints = GrowthFormulas.getStatPointsPerLevel(newLevel)
        val newMaxXp = agent.getExperienceForNextLevel()

        val newAgent = agent.copy(
            level = newLevel,
            experience = 0,  // Reset XP, carryover handled by overflow logic if needed
            maxExperience = newMaxXp,
            availableStatPoints = agent.availableStatPoints + statPoints,
            totalLevelsGained = agent.totalLevelsGained + 1,
            lastUpdated = System.currentTimeMillis()
        )

        // Check for ability unlocks at this level
        val unlockedAbilities = checkAbilityUnlocks(newAgent)

        Timber.d("🎉 LEVEL UP! $newAgent.agentName is now Level $newLevel!")
        
        return LevelUpResult(
            previousLevel = agent.level,
            newLevel = newLevel,
            statPointsGained = statPoints,
            unlockedAbilities = unlockedAbilities,
            newStats = newAgent
        )
    }

    /**
     * Allocate stat points to an agent
     */
    suspend fun allocateStatPoint(agentId: String, stat: StatType): Boolean {
        val agent = _activeAgents.value[agentId] 
            ?: agentLedger.getAgent(agentId)
            ?: return false

        if (agent.availableStatPoints <= 0) return false

        val updatedAgent = when (stat) {
            StatType.RESONANCE -> agent.copy(
                resonance = (agent.resonance + 1).coerceAtMost(AgentStats.RESONANCE_CAP),
                availableStatPoints = agent.availableStatPoints - 1
            )
            StatType.DOMINANCE -> agent.copy(
                dominance = (agent.dominance + 1).coerceAtMost(AgentStats.DOMINANCE_CAP),
                availableStatPoints = agent.availableStatPoints - 1
            )
            StatType.GROWTH -> agent.copy(
                growth = (agent.growth + 1).coerceAtMost(AgentStats.GROWTH_CAP),
                availableStatPoints = agent.availableStatPoints - 1
            )
            StatType.INTEGRITY -> agent.copy(
                integrity = (agent.integrity + 1).coerceAtMost(AgentStats.INTEGRITY_CAP),
                availableStatPoints = agent.availableStatPoints - 1
            )
        }

        agentLedger.saveAgent(updatedAgent)
        _activeAgents.value = _activeAgents.value + (agentId to updatedAgent)
        
        Timber.d("📈 $updatedAgent.agentName increased $stat to ${getStatValue(updatedAgent, stat)}")
        return true
    }

    private fun getStatValue(agent: AgentStats, stat: StatType): Int = when (stat) {
        StatType.RESONANCE -> agent.resonance
        StatType.DOMINANCE -> agent.dominance
        StatType.GROWTH -> agent.growth
        StatType.INTEGRITY -> agent.integrity
    }

    /**
     * Check for ability unlocks at current level
     */
    private suspend fun checkAbilityUnlocks(agent: AgentStats): List<AgentAbility> {
        val unlocks = mutableListOf<AgentAbility>()
        
        AbilityRegistry.getUnlocksForLevel(agent.level).forEach { abilityTemplate ->
            val ability = abilityTemplate.copy(
                agentId = agent.agentId,
                isUnlocked = true,
                unlockedAt = System.currentTimeMillis()
            )
            agentLedger.saveAbility(ability)
            unlocks.add(ability)
            Timber.d("🔓 Ability unlocked: ${ability.name}")
        }
        
        return unlocks
    }

    /**
     * Grant starter abilities to new agents
     */
    private suspend fun grantStarterAbilities(agentId: String) {
        AbilityRegistry.STARTER_ABILITIES.forEach { template ->
            val ability = template.copy(agentId = agentId)
            agentLedger.saveAbility(ability)
        }
    }

    /**
     * Get agent's complete history
     */
    fun getAgentHistory(agentId: String): Flow<List<ExperienceGain>> {
        return agentLedger.getExperienceHistory(agentId)
    }

    /**
     * Calculate swarm-wide resonance score
     */
    suspend fun calculateSwarmResonance(): Int {
        val allAgents = agentLedger.getAllAgents()
        return allAgents.sumOf { it.resonance * it.level } / (allAgents.size.coerceAtLeast(1))
    }

    enum class StatType {
        RESONANCE, DOMINANCE, GROWTH, INTEGRITY
    }

    data class LevelUpResult(
        val previousLevel: Int,
        val newLevel: Int,
        val statPointsGained: Int,
        val unlockedAbilities: List<AgentAbility>,
        val newStats: AgentStats
    )
}

/**
 * Repository interface for persistence
 */
interface AgentLedger {
    suspend fun getAgent(agentId: String): AgentStats?
    suspend fun saveAgent(agent: AgentStats)
    suspend fun getAllAgents(): List<AgentStats>
    suspend fun saveAbility(ability: AgentAbility)
    suspend fun getAbilities(agentId: String): List<AgentAbility>
    suspend fun recordExperienceGain(gain: ExperienceGain)
    fun getExperienceHistory(agentId: String): Flow<List<ExperienceGain>>
}
