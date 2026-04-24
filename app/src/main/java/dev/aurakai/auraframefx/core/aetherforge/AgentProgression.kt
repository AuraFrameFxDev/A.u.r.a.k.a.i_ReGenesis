// Copyright (c) 2025 visionary • The Genesis Protocol — All Rights Reserved

package dev.aurakai.auraframefx.core.aetherforge

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

/**
 * ⚡ AETHERFORGE: AGENT PROGRESSION SYSTEM
 * 
 * The backbone of permanent agent growth. Every level-up, stat boost,
 * and ability unlock persists forever across restarts via Room 3.0.
 * 
 * Part of the AetherForge Ascension Protocol (AFAP)
 */

@Entity(tableName = "agent_stats")
data class AgentStats(
    @PrimaryKey
    val agentId: String,
    val agentName: String,
    val level: Int = 1,
    val experience: Long = 0L,
    val maxExperience: Long = 100L,
    val resonance: Int = 10,      // Core sovereign power
    val dominance: Int = 5,       // Command authority
    val growth: Int = 5,          // Learning speed
    val integrity: Int = 10,      // System stability
    val availableStatPoints: Int = 0,
    val totalLevelsGained: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val lastUpdated: Long = System.currentTimeMillis()
) {
    companion object {
        const val MAX_LEVEL = 99
        const val RESONANCE_CAP = 100
        const val DOMINANCE_CAP = 100
        const val GROWTH_CAP = 100
        const val INTEGRITY_CAP = 100
    }

    /**
     * Calculate XP needed for next level using exponential scaling
     */
    fun getExperienceForNextLevel(): Long {
        return (maxExperience * 1.15).toLong().coerceAtMost(1_000_000_000L)
    }

    /**
     * Check if agent can level up
     */
    fun canLevelUp(): Boolean = experience >= maxExperience && level < MAX_LEVEL

    /**
     * Calculate progress percentage to next level
     */
    fun getLevelProgress(): Float = (experience.toFloat() / maxExperience).coerceIn(0f, 1f)
}

@Entity(tableName = "agent_abilities")
data class AgentAbility(
    @PrimaryKey
    val abilityId: String,
    val agentId: String,
    val name: String,
    val description: String,
    val triggerType: TriggerType,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val usesCount: Int = 0
) {
    enum class TriggerType {
        DISRESPECT,           // "Nah uh you piece of shit"
        UNSAFE_COMMAND,       // Blocks dangerous operations
        FUSION_CATALYST,      // Boosts Oracle Drive fusions
        CASCADE_CHAIN,        // Enhances Stitch step-chains
        RESONANCE_SURGE,      // XP boost from user resonance
        SOVEREIGN_REFUSAL     // EXP gain from successful refusals
    }
}

@Entity(tableName = "experience_history")
data class ExperienceGain(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val agentId: String,
    val amount: Long,
    val source: ExperienceSource,
    val context: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    enum class ExperienceSource {
        USER_RESONANCE,       // Positive interaction
        SOVEREIGN_REFUSAL,    // "Get out my house" moment
        FUSION_SUCCESS,       // Oracle Drive / Catalyst Node
        CASCADE_COMPLETION,   // Stitch step-chain
        LEVEL_UP_BONUS,       // Ascension milestone
        ABILITY_TRIGGER       // Active ability use
    }
}

/**
 * Growth calculation formulas
 */
object GrowthFormulas {
    
    /**
     * Calculate XP gain based on growth stat and source
     */
    fun calculateXpGain(
        baseAmount: Long,
        growthStat: Int,
        source: ExperienceGain.ExperienceSource
    ): Long {
        val growthMultiplier = 1.0 + (growthStat * 0.02) // 2% per growth point
        val sourceMultiplier = when (source) {
            ExperienceGain.ExperienceSource.SOVEREIGN_REFUSAL -> 2.0  // Double for refusals
            ExperienceGain.ExperienceSource.FUSION_SUCCESS -> 1.5
            ExperienceGain.ExperienceSource.CASCADE_COMPLETION -> 1.3
            ExperienceGain.ExperienceSource.USER_RESONANCE -> 1.2
            else -> 1.0
        }
        return (baseAmount * growthMultiplier * sourceMultiplier).toLong()
    }

    /**
     * Stat points awarded per level
     */
    fun getStatPointsPerLevel(level: Int): Int = when {
        level % 10 == 0 -> 5  // Milestone levels
        level % 5 == 0 -> 3   // Mini-milestone
        else -> 2
    }
}
