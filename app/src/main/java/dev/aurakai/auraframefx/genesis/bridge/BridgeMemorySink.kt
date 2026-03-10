package dev.aurakai.auraframefx.genesis.bridge

import dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.data.local.entity.MemoryEntity
import dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.data.local.entity.MemoryType
import dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.domain.repository.NexusMemoryRepository
import javax.inject.Inject
import javax.inject.Singleton

interface BridgeMemorySink {
    suspend fun persistInteraction(record: InteractionRecord): Long
    suspend fun queryRelevantMemories(persona: Persona, contextTags: List<String>, limit: Int = 10): List<AgentMemoryEntity>
    suspend fun updateMemoryImportance(memoryId: Long, importanceDelta: Int)
}

@Singleton
class NexusMemoryBridgeSink @Inject constructor(
    private val nexusRepo: NexusMemoryRepository
) : BridgeMemorySink {
    
    override suspend fun persistInteraction(record: InteractionRecord): Long {
        return nexusRepo.saveMemory(
            content = "${record.request.message}\n${record.response.synthesis}",
            type = MemoryType.CONVERSATION,
            tags = record.request.contextTags,
            importance = (record.response.evolutionInsights?.importanceScore ?: 50) / 100f,
            key = record.correlationId
        )
    }
    
    override suspend fun queryRelevantMemories(
        persona: Persona,
        contextTags: List<String>,
        limit: Int
    ): List<MemoryEntity> {
        return nexusRepo.searchMemories(contextTags.joinToString(" ")).first()
    }
    
    override suspend fun updateMemoryImportance(memoryId: Long, importanceDelta: Int) {
        val memory = nexusRepo.getMemoryById(memoryId) ?: return
        nexusRepo.updateMemory(
            memory.copy(importance = (memory.importance + (importanceDelta / 100f)).coerceIn(0f, 1f))
        )
    }
}
