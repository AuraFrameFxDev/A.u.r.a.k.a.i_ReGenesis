package dev.aurakai.auraframefx.domains.genesis

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.ai.adapters.DefaultGrokAdapter
import dev.aurakai.auraframefx.ai.adapters.GrokAdapter
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.pipeline.AIPipelineConfig
import dev.aurakai.auraframefx.domains.cascade.utils.context.ContextManager
import dev.aurakai.auraframefx.domains.cascade.utils.memory.MemoryManager
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.AuraAIService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.DefaultAuraAIService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.KaiAIService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.DefaultKaiAIService
import javax.inject.Singleton

/**
 * Hilt module responsible for providing all major AI Agent dependencies.
 * This module wires the Trinity (Genesis, Aura, Kai) and supporting agents.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AgentModule {

    @Binds
    @Singleton
    abstract fun bindGrokAdapter(impl: DefaultGrokAdapter): GrokAdapter

    @Binds
    @Singleton
    abstract fun bindAuraAIService(impl: DefaultAuraAIService): AuraAIService

    @Binds
    @Singleton
    abstract fun bindKaiAIService(impl: DefaultKaiAIService): KaiAIService

    companion object {
        @Provides
        @Singleton
        fun provideContextManager(
            memoryManager: MemoryManager,
            config: AIPipelineConfig
        ): ContextManager {
            return ContextManager(memoryManager, config)
        }
    }
}
