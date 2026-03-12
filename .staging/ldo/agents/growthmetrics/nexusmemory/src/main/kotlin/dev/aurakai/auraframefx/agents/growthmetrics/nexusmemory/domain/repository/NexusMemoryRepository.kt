package dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.domain.repository

import dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.data.local.entity.MemoryEntity
import dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.data.local.entity.MemoryType
import kotlinx.coroutines.flow.Flow

interface NexusMemoryRepository {
    suspend fun saveMemory(content: String, type: MemoryType, tags: List<String> = emptyList(), importance: Float = 0.5f, key: String? = null): Long
    suspend fun getMemoryById(id: Long): MemoryEntity?
    suspend fun getMemoryByKey(key: String): MemoryEntity?
    fun getAllMemories(): Flow<List<MemoryEntity>>
    fun getMemoriesByType(type: MemoryType): Flow<List<MemoryEntity>>
    fun searchMemories(query: String): Flow<List<MemoryEntity>>
    fun getImportantMemories(minImportance: Float): Flow<List<MemoryEntity>>
    suspend fun deleteMemory(memory: MemoryEntity)
    suspend fun updateMemory(memory: MemoryEntity)
}
