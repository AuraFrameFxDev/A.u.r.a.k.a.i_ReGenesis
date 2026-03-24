package dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.data.repository

import android.util.Base64
import dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.data.local.dao.MemoryDao
import dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.data.local.entity.MemoryEntity
import dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.data.local.entity.MemoryType
import dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.domain.repository.NexusMemoryRepository
import dev.aurakai.auraframefx.domains.genesis.ai.clients.MultimodalContent
import dev.aurakai.auraframefx.domains.genesis.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.securecomm.crypto.CryptoManager
import kotlinx.coroutines.flow.Flow
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

class NexusMemoryRepositoryImpl @Inject constructor(
    private val memoryDao: MemoryDao,
    private val cryptoManager: CryptoManager,
    private val vertexAIClient: VertexAIClient
) : NexusMemoryRepository {

    // Derived key for memory encryption (in a production app, this would be managed more securely)
    private val memoryKey = SecretKeySpec("NexusMemoryCrypt0SystemKey32Bytes!".toByteArray(), "AES")

    override suspend fun saveMemory(content: String, type: MemoryType, tags: List<String>, importance: Float, key: String?): Long {
        val sensitive = tags.contains("sensitive") || tags.contains("secure")

        val finalContent = if (sensitive) {
            val (encrypted, iv) = cryptoManager.encrypt(content.toByteArray(), memoryKey)
            // Store as IV:Ciphertext in Base64
            Base64.encodeToString(iv + encrypted, Base64.NO_WRAP)
        } else {
            content
        }

        val memory = MemoryEntity(
            key = key,
            content = finalContent,
            timestamp = System.currentTimeMillis(),
            type = type,
            tags = tags,
            importance = importance,
            isEncrypted = sensitive
        )
        return memoryDao.insertMemory(memory)
    }

    override suspend fun saveValenceMemory(
        content: String,
        modalInputs: List<MultimodalContent>,
        tags: List<String>,
        importance: Float,
        embeddingDimensions: Int,
        key: String?
    ): Long {
        // Determine modality tag from input types
        val hasText = modalInputs.any { it is MultimodalContent.Text }
        val hasImage = modalInputs.any { it is MultimodalContent.Image }
        val hasAudio = modalInputs.any { it is MultimodalContent.Audio }
        val modalityTag = when {
            hasText && hasImage && hasAudio -> "multimodal"
            hasText && hasImage -> "text+image"
            hasText && hasAudio -> "text+audio"
            hasImage && hasAudio -> "image+audio"
            hasImage -> "image"
            hasAudio -> "audio"
            else -> "text"
        }

        // Generate the MRL embedding vector
        val embeddingVector = if (modalInputs.isNotEmpty()) {
            try {
                vertexAIClient.generateMultimodalEmbedding(
                    content = modalInputs,
                    dimensions = embeddingDimensions
                ).toList()
            } catch (e: Exception) {
                emptyList()
            }
        } else emptyList()

        val memory = MemoryEntity(
            key = key,
            content = content,
            timestamp = System.currentTimeMillis(),
            type = MemoryType.VALENCE,
            tags = tags + listOf("valence", modalityTag),
            importance = importance,
            embedding = embeddingVector.ifEmpty { null },
            embeddingDimensions = embeddingDimensions,
            modalityTag = modalityTag,
            isEncrypted = false
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
