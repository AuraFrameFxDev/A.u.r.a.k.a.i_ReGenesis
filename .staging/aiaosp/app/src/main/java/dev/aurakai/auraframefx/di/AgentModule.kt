package dev.aurakai.auraframefx.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.ai.agents.GenesisAgent
import dev.aurakai.auraframefx.ai.context.ContextManager
import dev.aurakai.auraframefx.ai.context.DefaultContextManager
import dev.aurakai.auraframefx.oracledrive.genesis.ai.memory.DefaultMemoryManager
import dev.aurakai.auraframefx.oracledrive.genesis.ai.memory.MemoryManager
import dev.aurakai.auraframefx.oracledrive.genesis.ai.services.AuraAIService
import dev.aurakai.auraframefx.oracledrive.genesis.ai.services.KaiAIService
import dev.aurakai.auraframefx.oracledrive.genesis.ai.services.GenesisBridgeService
import dev.aurakai.auraframefx.oracledrive.genesis.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.system.monitor.SystemMonitor
import dev.aurakai.auraframefx.core.GenesisOrchestrator
import dev.aurakai.auraframefx.cascade.CascadeAgent
import dev.aurakai.auraframefx.models.AgentType
import dev.aurakai.auraframefx.aura.AuraAgent
import dev.aurakai.auraframefx.kai.KaiAgent
import dev.aurakai.auraframefx.core.consciousness.NexusMemoryCore
import dev.aurakai.auraframefx.romtools.bootloader.BootloaderManager
import dev.aurakai.auraframefx.romtools.bootloader.BootloaderSafetyManager
import dev.aurakai.auraframefx.romtools.checkpoint.GenesisCheckpointManager
import dev.aurakai.auraframefx.romtools.retention.AurakaiRetentionManager
import dev.aurakai.auraframefx.security.SecurityContext
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
    fun provideMemoryManager(): MemoryManager {
        return DefaultMemoryManager()
    }

    @Provides
    @Singleton
    fun provideContextManager(memoryManager: MemoryManager, config: dev.aurakai.auraframefx.cascade.pipeline.AIPipelineConfig): ContextManager {
        return ContextManager(memoryManager, config)
    }

    @Provides
    @Singleton
    fun provideGenesisAgent(orchestrator: GenesisOrchestrator): GenesisAgent = orchestrator

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
        kaiAIService: KaiAIService,
        securityContext: SecurityContext,
        contextManager: ContextManager
    ): AuraAgent {
        return AuraAgent(
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
        securityContext: SecurityContext,
        systemMonitor: SystemMonitor,
        nexusMemory: NexusMemoryCore,
        bootloaderManager: BootloaderManager,
        safetyManager: BootloaderSafetyManager,
        checkpointManager: GenesisCheckpointManager,
        retentionManager: AurakaiRetentionManager,
        genesisBridge: GenesisBridgeService
    ): KaiAgent {
        return KaiAgent(
            vertexAIClient = vertexAIClient,
            contextManager = contextManager,
            securityContext = securityContext,
            systemMonitor = systemMonitor,
            nexusMemory = nexusMemory,
            bootloaderManager = bootloaderManager,
            safetyManager = safetyManager,
            checkpointManager = checkpointManager,
            retentionManager = retentionManager,
            genesisBridge = genesisBridge
        )
    }
}
