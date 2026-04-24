// Copyright (c) 2025 visionary • The Genesis Protocol — All Rights Reserved

package dev.aurakai.auraframefx.core.aetherforge

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AgentDao {
    @Query("SELECT * FROM agent_stats WHERE agentId = :agentId")
    suspend fun getAgent(agentId: String): AgentStats?

    @Query("SELECT * FROM agent_stats")
    suspend fun getAllAgents(): List<AgentStats>

    @Query("SELECT * FROM agent_stats")
    fun getAllAgentsFlow(): Flow<List<AgentStats>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgent(agent: AgentStats)

    @Update
    suspend fun updateAgent(agent: AgentStats)

    @Delete
    suspend fun deleteAgent(agent: AgentStats)

    @Query("DELETE FROM agent_stats WHERE agentId = :agentId")
    suspend fun deleteAgentById(agentId: String)
}

@Dao
interface AbilityDao {
    @Query("SELECT * FROM agent_abilities WHERE agentId = :agentId")
    suspend fun getAbilitiesForAgent(agentId: String): List<AgentAbility>

    @Query("SELECT * FROM agent_abilities WHERE agentId = :agentId AND isUnlocked = 1")
    suspend fun getUnlockedAbilities(agentId: String): List<AgentAbility>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAbility(ability: AgentAbility)

    @Update
    suspend fun updateAbility(ability: AgentAbility)

    @Query("UPDATE agent_abilities SET usesCount = usesCount + 1 WHERE abilityId = :abilityId")
    suspend fun incrementAbilityUse(abilityId: String)
}

@Dao
interface ExperienceDao {
    @Query("SELECT * FROM experience_history WHERE agentId = :agentId ORDER BY timestamp DESC")
    suspend fun getHistoryForAgent(agentId: String): List<ExperienceGain>

    @Query("SELECT * FROM experience_history WHERE agentId = :agentId ORDER BY timestamp DESC")
    fun getHistoryForAgentFlow(agentId: String): Flow<List<ExperienceGain>>

    @Query("SELECT SUM(amount) FROM experience_history WHERE agentId = :agentId")
    suspend fun getTotalExperience(agentId: String): Long?

    @Query("SELECT * FROM experience_history WHERE agentId = :agentId AND source = :source")
    suspend fun getHistoryBySource(agentId: String, source: ExperienceGain.ExperienceSource): List<ExperienceGain>

    @Insert
    suspend fun insertExperienceGain(gain: ExperienceGain)

    @Query("DELETE FROM experience_history WHERE agentId = :agentId")
    suspend fun clearHistoryForAgent(agentId: String)
}
