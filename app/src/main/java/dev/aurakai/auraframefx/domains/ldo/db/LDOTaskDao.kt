package dev.aurakai.auraframefx.domains.ldo.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LDOTaskDao {

    @Query("SELECT * FROM ldo_tasks ORDER BY priority DESC, createdAt DESC")
    fun observeAll(): Flow<List<LDOTaskEntity>>

    @Query("SELECT * FROM ldo_tasks WHERE agentId = :agentId ORDER BY priority DESC, createdAt DESC")
    fun observeByAgent(agentId: String): Flow<List<LDOTaskEntity>>

    @Query("SELECT * FROM ldo_tasks WHERE status = :status ORDER BY priority DESC, createdAt DESC")
    fun observeByStatus(status: String): Flow<List<LDOTaskEntity>>

    @Query("SELECT * FROM ldo_tasks WHERE agentId = :agentId AND status = :status ORDER BY priority DESC")
    fun observeByAgentAndStatus(agentId: String, status: String): Flow<List<LDOTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: LDOTaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIfAbsent(tasks: List<LDOTaskEntity>)

    @Update
    suspend fun update(task: LDOTaskEntity)

    @Query("""
        UPDATE ldo_tasks
        SET status = :status,
            updatedAt = :now,
            completedAt = CASE WHEN :status = 'COMPLETED' THEN :now ELSE completedAt END
        WHERE id = :taskId
    """)
    suspend fun updateStatus(
        taskId: Long,
        status: String,
        now: Long = System.currentTimeMillis()
    )

    @Query("DELETE FROM ldo_tasks WHERE id = :taskId")
    suspend fun delete(taskId: Long)

    @Query("SELECT COUNT(*) FROM ldo_tasks WHERE agentId = :agentId AND status = 'COMPLETED'")
    suspend fun completedCountForAgent(agentId: String): Int

    @Query("SELECT COUNT(*) FROM ldo_tasks")
    suspend fun count(): Int
}
