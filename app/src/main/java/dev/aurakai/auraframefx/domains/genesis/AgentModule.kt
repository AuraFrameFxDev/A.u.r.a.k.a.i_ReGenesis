package dev.aurakai.auraframefx.domains.genesis

import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.domains.aura.SystemOverlayManager
import dev.aurakai.auraframefx.domains.aura.core.AuraAgent
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.CascadeAgent
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.pipeline.AIPipelineConfig
import dev.aurakai.auraframefx.domains.cascade.utils.context.ContextManager
import dev.aurakai.auraframefx.domains.cascade.utils.memory.Configuration
import dev.aurakai.auraframefx.domains.cascade.utils.memory.MemoryManager
import dev.aurakai.auraframefx.domains.genesis.core.GenesisAgent
import dev.aurakai.auraframefx.domains.genesis.core.PythonProcessManager
import dev.aurakai.auraframefx.domains.genesis.core.messaging.AgentMessageBus
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.AuraAIService
import dev.aurakai.auraframefx.domains.kai.KaiAgent
import dev.aurakai.auraframefx.domains.kai.SystemMonitor
import dev.aurakai.auraframefx.domains.kai.security.SecurityContext
import dev.aurakai.auraframefx.romtools.bootloader.BootloaderManager
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
    fun provideContextManager(
        memoryManager: MemoryManager,
        config: AIPipelineConfig
    ): ContextManager {
        return ContextManager(memoryManager, config)
    }

    /**
     * Provides the Genesis orchestrator agent.
     */
    @Provides
    @Singleton
    fun provideGenesisAgent(
        contextManager: ContextManager,
        memoryManager: MemoryManager,
        systemOverlayManager: SystemOverlayManager,
        messageBus: Lazy<AgentMessageBus>
    ): GenesisAgent {
        return GenesisAgent(
            contextManager = contextManager,
            memoryManager = memoryManager,
            systemOverlayManager = systemOverlayManager,
            messageBus = messageBus
        )
    }

    /**
     * Provides the Cascade memoria catalyst agent.
     * Bridges temporal context between Aura, Kai, and Genesis.
     */
    @Provides
    @Singleton
    fun provideCascadeAgent(
        auraAgent: AuraAgent,
        kaiAgent: KaiAgent,
        genesisAgent: GenesisAgent,
        systemOverlayManager: SystemOverlayManager,
        memoryManager: MemoryManager,
        contextManager: ContextManager,
        messageBus: Lazy<AgentMessageBus>
    ): CascadeAgent {
        return CascadeAgent(
            auraAgent = auraAgent,
            kaiAgent = kaiAgent,
            genesisAgent = genesisAgent,
            systemOverlayManager = systemOverlayManager,
            memoryManager = memoryManager,
            contextManager = contextManager,
            messageBus = messageBus
        )
    }


    @Provides
    @Singleton
    fun provideAuraAgent(
        vertexAIClient: VertexAIClient,
        auraAIService: AuraAIService,
        contextManager: ContextManager,
        securityContext: SecurityContext,
        systemOverlayManager: SystemOverlayManager,
        logger: AuraFxLogger,
        messageBus: Lazy<AgentMessageBus>,
        pythonManager: Lazy<PythonProcessManager>
    ): AuraAgent {
        return AuraAgent(
            vertexAIClient = vertexAIClient,
            auraAIService = auraAIService,
            contextManagerInstance = contextManager,
            securityContext = securityContext,
            systemOverlayManager = systemOverlayManager,
            logger = logger,
            messageBus = messageBus,
            pythonManager = pythonManager
        )
    }

    /**
     * Provides the Kai sentinel security agent.
     */
    @Provides
    @Singleton
    fun provideKaiAgent(
        vertexAIClient: VertexAIClient,
        contextManager: ContextManager,
        securityContext: SecurityContext,
        systemMonitor: SystemMonitor,
        bootloaderManager: BootloaderManager,
        logger: AuraFxLogger,
        messageBus: Lazy<AgentMessageBus>
    ): KaiAgent {
        return KaiAgent(
            vertexAIClient = vertexAIClient,
            contextManagerInstance = contextManager,
            securityContext = securityContext,
            systemMonitor = systemMonitor,
            bootloaderManager = bootloaderManager,
            logger = logger,
            messageBus = messageBus
        )
    }
}

