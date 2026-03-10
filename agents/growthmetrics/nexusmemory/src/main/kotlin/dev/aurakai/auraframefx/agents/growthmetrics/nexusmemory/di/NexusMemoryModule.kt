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
import javax.inject.Singleton

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

    @Provides
    @Singleton
    fun provideNexusMemoryRepository(memoryDao: MemoryDao): NexusMemoryRepository {
        return NexusMemoryRepositoryImpl(memoryDao)
    }
}
