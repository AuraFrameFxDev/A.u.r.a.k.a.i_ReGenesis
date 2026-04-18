package dev.aurakai.auraframefx.domains.ldo.data.dao

import androidx.room.*
import dev.aurakai.auraframefx.domains.ldo.data.entities.LDOAgentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LDOAgentDao {
    @Query("SELECT * FROM ldo_agents ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<LDOAgentEntity>>

    @Query("SELECT * FROM ldo_agents WHERE id = :id")
    suspend fun getById(id: String): LDOAgentEntity?

    @Query("SELECT * FROM ldo_agents WHERE id = :id")
    fun observeAgent(id: String): Flow<LDOAgentEntity?>

    @Query("SELECT * FROM ldo_agents WHERE isActive = 1")
    fun observeActiveAgents(): Flow<List<LDOAgentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(agent: LDOAgentEntity)

    @Update
    suspend fun update(agent: LDOAgentEntity)

    @Query("UPDATE ldo_agents SET isActive = :active WHERE id = :id")
    suspend fun setActive(id: String, active: Boolean)

    @Query("UPDATE ldo_agents SET tasksCompleted = tasksCompleted + 1 WHERE id = :id")
    suspend fun incrementTasksCompleted(id: String)

    @Delete
    suspend fun delete(agent: LDOAgentEntity)
}
