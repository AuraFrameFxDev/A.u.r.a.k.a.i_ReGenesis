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
     * Observes tasks assigned to the specified agent.
     *
     * @param agentId Identifier used to filter tasks by the `assignedAgentId` column.
     * @return The list of `LDOTaskEntity` rows assigned to the given agent.
     */
    @Query("SELECT * FROM ldo_tasks WHERE assignedAgentId = :agentId")
    fun observeByAgent(agentId: String): Flow<List<LDOTaskEntity>>

    /**
     * Observes tasks matching the given status.
     *
     * @param status The status to filter tasks by.
     * @return Lists of LDOTaskEntity matching the given status whenever the underlying data changes.
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
