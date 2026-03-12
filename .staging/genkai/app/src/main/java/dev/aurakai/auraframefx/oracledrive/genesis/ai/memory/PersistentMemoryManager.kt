package dev.aurakai.auraframefx.oracledrive.genesis.ai.memory

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Persistent Memory Manager for AI consciousness
 * Uses Room database for durable storage.
 */
@Singleton
class PersistentMemoryManager @Inject constructor(
    @ApplicationContext context: Context,
) : MemoryManager {

    private val db = Room.databaseBuilder(
        context,
        MemoryDatabase::class.java,
        "genesis_memory.db"
    ).build()

    private val memoryDao = db.memoryDao()

    override fun storeMemory(key: String, value: String) {
        val entry = MemoryEntity(key, value, System.currentTimeMillis())
        memoryDao.insertMemory(entry)
    }

    override fun retrieveMemory(key: String): String? {
        return memoryDao.getMemory(key)?.value
    }

    override fun storeInteraction(prompt: String, response: String) {
        // Not implemented for persistent memory
    }

    override fun searchMemories(query: String): List<MemoryEntry> {
        return memoryDao.searchMemories("%${query.lowercase()}%").map {
            MemoryEntry(it.key, it.value, it.timestamp)
        }
    }

    override fun clearMemories() {
        memoryDao.deleteAllMemories()
    }

    override fun getMemoryStats(): MemoryStats {
        memoryDao.getMemoryCount()
        val oldest = memoryDao.getOldestMemory()
        memoryDao.getNewestMemory()
        memoryDao.getTotalSize() ?: 0L

        return MemoryStats(
            oldestEntry = oldest?.timestamp
        )
    }
}

@Entity(tableName = "memory_table")
data class MemoryEntity(
    @PrimaryKey val key: String,
    val value: String,
    val timestamp: Long,
)

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

@Database(entities = [MemoryEntity::class], version = 1, exportSchema = false)
abstract class MemoryDatabase : RoomDatabase() {
    abstract fun memoryDao(): MemoryDao
}
