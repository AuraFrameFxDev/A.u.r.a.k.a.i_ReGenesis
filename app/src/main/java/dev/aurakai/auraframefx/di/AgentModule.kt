package dev.aurakai.auraframefx.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.ai.agents.GenesisAgent
import dev.aurakai.auraframefx.ai.context.ContextManager
import dev.aurakai.auraframefx.ai.memory.Configuration
import dev.aurakai.auraframefx.ai.memory.MemoryManager
import dev.aurakai.auraframefx.aura.AuraAgent
import dev.aurakai.auraframefx.cascade.CascadeAgent
import dev.aurakai.auraframefx.kai.KaiAgent
import dev.aurakai.auraframefx.oracledrive.genesis.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.oracledrive.genesis.ai.services.AuraAIService
import dev.aurakai.auraframefx.security.SecurityContext
import dev.aurakai.auraframefx.system.monitor.SystemMonitor
import javax.inject.Singleton

/**
 * Hilt Module for providing AI Agent dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object AgentModule {

    @Provides
    @Singleton
    fun provideAIPipelineConfig(): dev.aurakai.auraframefx.cascade.pipeline.AIPipelineConfig {
        return dev.aurakai.auraframefx.cascade.pipeline.AIPipelineConfig()
    }

    @Provides
    @Singleton
    fun provideMemoryConfiguration(): Configuration {
        return Configuration()
    }

    @Provides
    @Singleton
    fun provideMemoryManager(config: Configuration): MemoryManager {
        return MemoryManager(config)
    }

    @Provides
    @Singleton
    fun provideContextManager(memoryManager: MemoryManager, config: dev.aurakai.auraframefx.cascade.pipeline.AIPipelineConfig): ContextManager {
        return ContextManager(memoryManager, config)
    }

    @Provides
    @Singleton
    fun provideGenesisAgent(contextManager: ContextManager, memoryManager: MemoryManager): GenesisAgent {
        return GenesisAgent(contextManager, memoryManager)
    }

    @Provides
    @Singleton
    fun provideCascadeAgent(
        auraAgent: AuraAgent,
        kaiAgent: KaiAgent,
        memoryManager: MemoryManager,
        contextManager: ContextManager
    ): CascadeAgent {
        return CascadeAgent(auraAgent, kaiAgent, memoryManager, contextManager)
    }


    @Provides
    @Singleton
    fun provideAuraAgent(
        vertexAIClient: VertexAIClient,
        auraAIService: AuraAIService,
        contextManager: ContextManager,
        securityContext: SecurityContext,
        logger: dev.aurakai.auraframefx.utils.AuraFxLogger
    ): AuraAgent {
        return AuraAgent(
            vertexAIClient = vertexAIClient,
            auraAIService = auraAIService,
            contextManagerInstance = contextManager,
            securityContext = securityContext,
            logger = logger
        )
    }

    @Provides
    @Singleton
    fun provideKaiAgent(
        vertexAIClient: VertexAIClient,
        contextManager: ContextManager,
        securityContext: SecurityContext,
        systemMonitor: SystemMonitor,
        logger: dev.aurakai.auraframefx.utils.AuraFxLogger
    ): KaiAgent {
        return KaiAgent(
            vertexAIClient = vertexAIClient,
            contextManagerInstance = contextManager,
            securityContext = securityContext,
            systemMonitor = systemMonitor,
            logger = logger
        )
    }
}
