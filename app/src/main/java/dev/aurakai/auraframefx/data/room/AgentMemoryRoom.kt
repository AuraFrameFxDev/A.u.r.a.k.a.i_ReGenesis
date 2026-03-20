package dev.aurakai.auraframefx.data.room

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Room Entity for Agent Memory.
 */
data class AgentMemoryEntity(
    val id: Long = 0,
    val agentType: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Room DAO for Agent Memory.
 */
interface AgentMemoryDao {
    suspend fun insertMemory(memory: AgentMemoryEntity)
    fun getMemoriesForAgent(agentType: String): Flow<List<AgentMemoryEntity>>
}

/**
 * Compatibility shim for AgentMemoryDao.
 */
class AgentMemoryDaoShim : AgentMemoryDao {
    override suspend fun insertMemory(memory: AgentMemoryEntity) {}
    override fun getMemoriesForAgent(agentType: String): Flow<List<AgentMemoryEntity>> = flowOf(emptyList())
}
