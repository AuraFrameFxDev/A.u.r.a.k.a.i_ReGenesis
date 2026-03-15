package dev.aurakai.auraframefx.oracledrive.genesis.ai.memory

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MemoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMemory(memory: MemoryEntity)

    @Query("SELECT * FROM memory_table WHERE `key` = :key")
    fun getMemory(key: String): MemoryEntity?

    @Query("SELECT * FROM memory_table WHERE value LIKE :query ORDER BY timestamp DESC")
    fun searchMemories(query: String): List<MemoryEntity>

    @Query("DELETE FROM memory_table")
    fun deleteAllMemories()

    @Query("SELECT COUNT(*) FROM memory_table")
    fun getMemoryCount(): Int

    @Query("SELECT * FROM memory_table ORDER BY timestamp ASC LIMIT 1")
    fun getOldestMemory(): MemoryEntity?

    @Query("SELECT * FROM memory_table ORDER BY timestamp DESC LIMIT 1")
    fun getNewestMemory(): MemoryEntity?

    @Query("SELECT SUM(LENGTH(`key`) + LENGTH(value)) FROM memory_table")
    fun getTotalSize(): Long?
}
