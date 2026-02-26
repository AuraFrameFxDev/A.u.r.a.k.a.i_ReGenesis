package dev.aurakai.auraframefx.domains.ldo.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LDOAgentDao {

    @Query("SELECT * FROM ldo_agents ORDER BY evolutionLevel DESC, displayName ASC")
    fun observeAll(): Flow<List<LDOAgentEntity>>

    @Query("SELECT * FROM ldo_agents WHERE id = :agentId")
    fun observeAgent(agentId: String): Flow<LDOAgentEntity?>

    @Query("SELECT * FROM ldo_agents WHERE id = :agentId")
    suspend fun getAgent(agentId: String): LDOAgentEntity?

    @Query("SELECT * FROM ldo_agents WHERE isActive = 1")
    fun observeActiveAgents(): Flow<List<LDOAgentEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfAbsent(agent: LDOAgentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(agent: LDOAgentEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIfAbsent(agents: List<LDOAgentEntity>)

    @Update
    suspend fun update(agent: LDOAgentEntity)

    @Query("UPDATE ldo_agents SET isActive = :active, updatedAt = :now WHERE id = :agentId")
    suspend fun setActive(agentId: String, active: Boolean, now: Long = System.currentTimeMillis())

    @Query("""
        UPDATE ldo_agents
        SET tasksCompleted = tasksCompleted + 1,
            evolutionLevel = evolutionLevel + CASE WHEN tasksCompleted % 10 = 9 THEN 1 ELSE 0 END,
            updatedAt = :now
        WHERE id = :agentId
    """)
    suspend fun incrementTasksCompleted(agentId: String, now: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM ldo_agents")
    suspend fun count(): Int
}
