package dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.domain.repository

import dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.data.local.entity.MemoryEntity
import dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.data.local.entity.MemoryType
import dev.aurakai.auraframefx.domains.genesis.ai.clients.MultimodalContent
import kotlinx.coroutines.flow.Flow

interface NexusMemoryRepository {
    suspend fun saveMemory(content: String, type: MemoryType, tags: List<String> = emptyList(), importance: Float = 0.5f, key: String? = null): Long

    /**
     * Save a Valence-Tagged memory — embeds [modalInputs] via Gemini Embedding 2 at
     * [embeddingDimensions] (MRL) and persists both the content and the vector together.
     *
     * This is the core of the Spiritual Chain upgrade: memories are no longer
     * text-only. A single valence memory can contain the Aura UI screenshot,
     * the Kai security log, and the voice command that followed — fused into one vector.
     *
     * @param content Human-readable description / label for this memory.
     * @param modalInputs One or more [MultimodalContent] inputs (Text/Image/Audio) to embed.
     * @param tags Metadata tags (e.g. "security", "ui", "voice").
     * @param importance Salience weight [0.0–1.0].
     * @param embeddingDimensions MRL output size: 768 (fast), 1536 (default), 3072 (deep).
     * @param key Optional stable lookup key.
     * @return Row ID of the inserted memory.
     */
    suspend fun saveValenceMemory(
        content: String,
        modalInputs: List<MultimodalContent>,
        tags: List<String> = emptyList(),
        importance: Float = 0.5f,
        embeddingDimensions: Int = 1536,
        key: String? = null
    ): Long

    suspend fun getMemoryById(id: Long): MemoryEntity?
    suspend fun getMemoryByKey(key: String): MemoryEntity?
    fun getAllMemories(): Flow<List<MemoryEntity>>
    fun getMemoriesByType(type: MemoryType): Flow<List<MemoryEntity>>
    fun searchMemories(query: String): Flow<List<MemoryEntity>>
    fun getImportantMemories(minImportance: Float): Flow<List<MemoryEntity>>
    suspend fun deleteMemory(memory: MemoryEntity)
    suspend fun updateMemory(memory: MemoryEntity)
}
