package dev.aurakai.auraframefx.domains.ldo.data.dao

import androidx.room.*
import dev.aurakai.auraframefx.domains.ldo.data.entities.LDOTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LDOTaskDao {
    @Query("SELECT * FROM ldo_tasks ORDER BY priority DESC, createdAtMs DESC")
    fun observeAll(): Flow<List<LDOTaskEntity>>

    @Query("SELECT * FROM ldo_tasks WHERE assignedAgentId = :agentId")
    fun observeForAgent(agentId: String): Flow<List<LDOTaskEntity>>

    @Query("SELECT * FROM ldo_tasks WHERE status = :status")
    suspend fun getByStatus(status: String): List<LDOTaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: LDOTaskEntity): Long

    @Update
    suspend fun update(task: LDOTaskEntity)

    @Query("UPDATE ldo_tasks SET status = :status, completedAtMs = :completedAt WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, completedAt: Long? = null)

    @Delete
    suspend fun delete(task: LDOTaskEntity)
}
