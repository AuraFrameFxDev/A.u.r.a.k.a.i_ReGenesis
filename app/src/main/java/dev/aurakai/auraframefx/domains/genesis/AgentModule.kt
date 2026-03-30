package dev.aurakai.auraframefx.domains.genesis

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.pipeline.AIPipelineConfig
import dev.aurakai.auraframefx.domains.cascade.utils.context.ContextManager
import dev.aurakai.auraframefx.domains.cascade.utils.memory.MemoryManager
import javax.inject.Singleton

/**
 * Hilt module responsible for providing all major AI Agent dependencies.
 * This module wires the Trinity (Genesis, Aura, Kai) and supporting agents.
 */
@Module
@InstallIn(SingletonComponent::class)
object AgentModule {

    @Provides
    @Singleton
    fun provideContextManager(
        memoryManager: MemoryManager,
        config: AIPipelineConfig
    ): ContextManager {
        return ContextManager(memoryManager, config)
    }
}
