package dev.aurakai.auraframefx.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.oracledrive.genesis.ai.memory.DefaultMemoryManager
import dev.aurakai.auraframefx.oracledrive.genesis.ai.memory.MemoryManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MemoryModule {

    @Binds
    @Singleton
    abstract fun bindMemoryManager(impl: DefaultMemoryManager): MemoryManager
}

