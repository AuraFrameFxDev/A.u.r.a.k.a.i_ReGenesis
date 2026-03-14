package dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.data.local.entity.MemoryEntity
import dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.data.local.entity.MemoryType
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemories(memories: List<MemoryEntity>)

    @Update
    suspend fun updateMemory(memory: MemoryEntity)

    @Delete
    suspend fun deleteMemory(memory: MemoryEntity)

    @Query("SELECT * FROM nexus_memories WHERE id = :id")
    suspend fun getMemoryById(id: Long): MemoryEntity?

    @Query("SELECT * FROM nexus_memories WHERE `key` = :key LIMIT 1")
    suspend fun getMemoryByKey(key: String): MemoryEntity?

    @Query("SELECT * FROM nexus_memories ORDER BY timestamp DESC")
    fun getAllMemories(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM nexus_memories WHERE type = :type ORDER BY timestamp DESC")
    fun getMemoriesByType(type: MemoryType): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM nexus_memories WHERE content LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchMemories(query: String): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM nexus_memories WHERE importance >= :minImportance ORDER BY importance DESC")
    fun getImportantMemories(minImportance: Float): Flow<List<MemoryEntity>>
}
