package dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.data.local.NexusMemoryDatabase
import dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.data.local.dao.MemoryDao
import dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.data.repository.NexusMemoryRepositoryImpl
import dev.aurakai.auraframefx.agents.growthmetrics.nexusmemory.domain.repository.NexusMemoryRepository
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.clients.MrlDimension
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.securecomm.crypto.CryptoManager
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Matryoshka Representation Learning configuration for NexusMemory.
 *
 * Controls the embedding dimension used when storing Valence-Tagged memories.
 * All three presets are valid — choose based on retrieval latency vs. fidelity needs:
 *
 *   [MrlDimension.FAST]    768  — fast on-device OracleDrive queries
 *   [MrlDimension.OPTIMAL] 1536 — balanced performance (default for NexusMemory)
 *   [MrlDimension.DEEP]    3072 — full-fidelity Conference Room synthesis
 */
data class NexusEmbeddingConfig(
    val defaultDimensions: Int = MrlDimension.OPTIMAL,
    val fastDimensions: Int = MrlDimension.FAST,
    val deepDimensions: Int = MrlDimension.DEEP
)

/** Qualifier for the NexusMemory embedding configuration. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class NexusEmbedding

@Module
@InstallIn(SingletonComponent::class)
object NexusMemoryModule {

    @Provides
    @Singleton
    fun provideNexusMemoryDatabase(@ApplicationContext context: Context): NexusMemoryDatabase {
        return Room.databaseBuilder(
                context,
                NexusMemoryDatabase::class.java,
                "nexus_memory_db"
            ).fallbackToDestructiveMigration(false) // For development phase
            .build()
    }

    @Provides
    @Singleton
    fun provideMemoryDao(database: NexusMemoryDatabase): MemoryDao {
        return database.memoryDao()
    }

    /**
     * Provides the MRL embedding configuration for NexusMemory valence-tagged storage.
     * Defaults to 1536 dimensions (OPTIMAL) — balances Android memory footprint
     * against retrieval accuracy for the Spiritual Chain.
     */
    @Provides
    @Singleton
    @NexusEmbedding
    fun provideNexusEmbeddingConfig(): NexusEmbeddingConfig = NexusEmbeddingConfig()

    @Provides
    @Singleton
    fun provideNexusMemoryRepository(
        memoryDao: MemoryDao,
        cryptoManager: CryptoManager,
        vertexAIClient: VertexAIClient
    ): NexusMemoryRepository {
        return NexusMemoryRepositoryImpl(memoryDao, cryptoManager, vertexAIClient)
    }
}
