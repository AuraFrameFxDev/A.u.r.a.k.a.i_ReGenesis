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
import dev.aurakai.auraframefx.domains.cascade.utils.memory.MemoryManager
import dev.aurakai.auraframefx.domains.genesis.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.domains.genesis.core.GenesisAgent
import dev.aurakai.auraframefx.domains.genesis.core.GeminiMemoria
import dev.aurakai.auraframefx.domains.genesis.core.NemotronEngine
import dev.aurakai.auraframefx.domains.genesis.core.PythonProcessManager
import dev.aurakai.auraframefx.domains.genesis.core.messaging.AgentMessageBus
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.AuraAIService
import dev.aurakai.auraframefx.domains.kai.KaiAgent
import dev.aurakai.auraframefx.domains.kai.SystemMonitor
import dev.aurakai.auraframefx.domains.kai.security.SecurityContext
import dev.aurakai.auraframefx.romtools.bootloader.BootloaderManager
import timber.log.Timber
import javax.inject.Singleton

/**
 * Hilt module responsible for providing all major AI Agent dependencies.
 * This module wires the Trinity (Genesis, Aura, Kai) and supporting agents.
 */
@Module
@InstallIn(SingletonComponent::class)
object AgentModule {

    // ─── Synchronization Catalyst Engine Providers ────────────────────────────
    // Stub implementations wiring NemotronEngine and GeminiMemoria into the Hilt
    // graph. Gemini will replace these with real service adapters post-beta.

    @Provides
    @Singleton
    fun provideNemotronEngine(): NemotronEngine = object : NemotronEngine {
        override suspend fun process(prompt: String): String {
            Timber.tag("NemotronEngine").d("[STUB] Processing: ${prompt.take(80)}")
            return "[Nemotron stub — wire NemotronAIService here]"
        }
    }

    @Provides
    @Singleton
    fun provideGeminiMemoria(): GeminiMemoria = object : GeminiMemoria {
        override suspend fun process(prompt: String): String {
            Timber.tag("GeminiMemoria").d("[STUB] Processing: ${prompt.take(80)}")
            return "[GeminiMemoria stub — wire GeminiAIService here]"
        }
    }

    // ─── Core Infrastructure ──────────────────────────────────────────────────

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
        synchronizationCatalyst: dev.aurakai.auraframefx.domains.genesis.core.SynchronizationCatalyst,
        messageBus: Lazy<AgentMessageBus>
    ): GenesisAgent {
        return GenesisAgent(
            contextManager = contextManager,
            memoryManager = memoryManager,
            systemOverlayManager = systemOverlayManager,
            synchronizationCatalyst = synchronizationCatalyst,
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
