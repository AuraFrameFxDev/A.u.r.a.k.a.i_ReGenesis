package dev.aurakai.auraframefx.oracledrive.genesis.ai.memory

import android.content.Context
import androidx.room.Room
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
        val totalEntries = memoryDao.getMemoryCount()
        val oldest = memoryDao.getOldestMemory()
        val newest = memoryDao.getNewestMemory()
        val totalSize = memoryDao.getTotalSize() ?: 0L

        return MemoryStats(
            totalEntries = totalEntries,
            totalSize = totalSize,
            oldestEntry = oldest?.timestamp,
            newestEntry = newest?.timestamp
        )
    }
}
