package dev.aurakai.auraframefx.domains.ldo.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.aurakai.auraframefx.domains.ldo.data.entities.LDOTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LDOTaskDao {
    /**
     * Observe all tasks ordered by priority (highest first) then by creation time (newest first).
     *
     * @return A Flow that emits lists of LDOTaskEntity ordered by priority descending, then createdAt descending.
     */
    @Query("SELECT * FROM ldo_tasks ORDER BY priority DESC, createdAt DESC")
    fun observeAll(): Flow<List<LDOTaskEntity>>

    /**
     * Retrieves tasks assigned to the specified agent.
     *
     * @param agentId The agent identifier to match against the `assignedAgentId` column.
     * @return Lists of `LDOTaskEntity` rows whose `assignedAgentId` equals `agentId`.
     */
    @Query("SELECT * FROM ldo_tasks WHERE assignedAgentId = :agentId")
    fun observeByAgent(agentId: String): Flow<List<LDOTaskEntity>>

    /**
     * Retrieve tasks that have the specified status.
     *
     * @param status The task status to filter by.
     * @return A Flow emitting lists of `LDOTaskEntity` that match the specified status.
     */
    @Query("SELECT * FROM ldo_tasks WHERE status = :status")
    fun observeByStatus(status: String): Flow<List<LDOTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: LDOTaskEntity): Long

    @Update
    suspend fun update(task: LDOTaskEntity)

    @Query("UPDATE ldo_tasks SET status = :status, completedAt = :completedAt WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, completedAt: Long? = null)

    @Query("DELETE FROM ldo_tasks WHERE id = :id")
    suspend fun delete(id: Long)

    @Delete
    suspend fun delete(task: LDOTaskEntity)
}
