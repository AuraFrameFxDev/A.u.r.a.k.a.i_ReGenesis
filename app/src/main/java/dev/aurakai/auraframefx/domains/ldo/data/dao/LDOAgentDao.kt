package dev.aurakai.auraframefx.domains.ldo.data.dao

import androidx.room.*
import dev.aurakai.auraframefx.domains.ldo.data.entities.LDOAgentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LDOAgentDao {
    @Query("SELECT * FROM ldo_agents ORDER BY lastActiveMs DESC")
    fun observeAll(): Flow<List<LDOAgentEntity>>

    @Query("SELECT * FROM ldo_agents WHERE agentId = :id")
    suspend fun getById(id: String): LDOAgentEntity?

    @Query("SELECT * FROM ldo_agents WHERE isActive = 1")
    suspend fun getActiveAgents(): List<LDOAgentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(agent: LDOAgentEntity)

    @Update
    suspend fun update(agent: LDOAgentEntity)

    @Delete
    suspend fun delete(agent: LDOAgentEntity)

    @Query("UPDATE ldo_agents SET lastActiveMs = :ms WHERE agentId = :id")
    suspend fun updateLastActive(id: String, ms: Long = System.currentTimeMillis())
}
