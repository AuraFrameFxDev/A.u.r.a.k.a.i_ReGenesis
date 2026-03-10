package dev.aurakai.auraframefx.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.oracledrive.genesis.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.oracledrive.genesis.ai.context.ContextManager
import dev.aurakai.auraframefx.oracledrive.genesis.ai.context.DefaultContextManager
import dev.aurakai.auraframefx.oracledrive.genesis.ai.memory.DefaultMemoryManager
import dev.aurakai.auraframefx.oracledrive.genesis.ai.memory.MemoryManager
import dev.aurakai.auraframefx.oracledrive.genesis.ai.services.AuraAIService
import dev.aurakai.auraframefx.model.AgentType
import dev.aurakai.auraframefx.oracledrive.genesis.ai.services.KaiAIService
import javax.inject.Singleton

/**
 * Hilt Module for providing AI Agent dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object AgentModule {

    @Provides
    @Singleton
    fun provideMemoryManager(): MemoryManager {
        return DefaultMemoryManager()
    }

    @Provides
    @Singleton
    fun provideContextManager(memoryManager: MemoryManager): ContextManager {
        return DefaultContextManager(memoryManager)
    }


    @Provides
    @Singleton
    fun provideAuraAgent(
        vertexAIClient: VertexAIClient,
        auraAIService: AuraAIService,
        kaiAIService: KaiAIService,
        securityContext: dev.aurakai.auraframefx.security.SecurityContext,
        contextManager: ContextManager
    ): dev.aurakai.auraframefx.aura.AuraAgent {
        return dev.aurakai.auraframefx.aura.AuraAgent(
            vertexAIClient = vertexAIClient,
            auraAIService = auraAIService,
            kaiAIService = kaiAIService,
            securityContext = securityContext,
            contextManager = contextManager,
            agentType = AgentType.AURA
        )
    }

    @Provides
    @Singleton
    fun provideKaiAgent(
        vertexAIClient: VertexAIClient,
        contextManager: ContextManager,
        securityContext: dev.aurakai.auraframefx.security.SecurityContext,
        systemMonitor: dev.aurakai.auraframefx.system.monitor.SystemMonitor,
        kaiAIService: KaiAIService
    ): dev.aurakai.auraframefx.kai.KaiAgent {
        return dev.aurakai.auraframefx.kai.KaiAgent(
            vertexAIClient = vertexAIClient,
            contextManager = contextManager,
            securityContext = securityContext,
            systemMonitor = systemMonitor,
            agentType = AgentType.KAI,
            kaiAIService = kaiAIService
        )
    }
}
