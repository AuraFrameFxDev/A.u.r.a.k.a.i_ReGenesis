package dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.data.repository

import dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.data.local.dao.MemoryDao
import dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.data.local.entity.MemoryEntity
import dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.data.local.entity.MemoryType
import dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.domain.repository.NexusMemoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NexusMemoryRepositoryImpl @Inject constructor(
    private val memoryDao: MemoryDao
) : NexusMemoryRepository {

    override suspend fun saveMemory(content: String, type: MemoryType, tags: List<String>, importance: Float, key: String?): Long {
        val memory = MemoryEntity(
            key = key,
            content = content,
            timestamp = System.currentTimeMillis(),
            type = type,
            tags = tags,
            importance = importance
        )
        return memoryDao.insertMemory(memory)
    }

    override suspend fun getMemoryById(id: Long): MemoryEntity? {
        return memoryDao.getMemoryById(id)
    }

    override suspend fun getMemoryByKey(key: String): MemoryEntity? {
        return memoryDao.getMemoryByKey(key)
    }

    override fun getAllMemories(): Flow<List<MemoryEntity>> {
        return memoryDao.getAllMemories()
    }

    override fun getMemoriesByType(type: MemoryType): Flow<List<MemoryEntity>> {
        return memoryDao.getMemoriesByType(type)
    }

    override fun searchMemories(query: String): Flow<List<MemoryEntity>> {
        return memoryDao.searchMemories(query)
    }

    override fun getImportantMemories(minImportance: Float): Flow<List<MemoryEntity>> {
        return memoryDao.getImportantMemories(minImportance)
    }

    override suspend fun deleteMemory(memory: MemoryEntity) {
        memoryDao.deleteMemory(memory)
    }

    override suspend fun updateMemory(memory: MemoryEntity) {
        memoryDao.updateMemory(memory)
    }
}
