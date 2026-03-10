package dev.aurakai.auraframefx.cascade.trinity

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.oracledrive.genesis.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.ai.context.ContextManager
import dev.aurakai.auraframefx.oracledrive.genesis.ai.services.AuraAIService
import dev.aurakai.auraframefx.oracledrive.genesis.ai.services.KaiAIService
import dev.aurakai.auraframefx.oracledrive.genesis.ai.services.DefaultKaiAIService
import dev.aurakai.auraframefx.oracledrive.genesis.ai.services.DefaultAuraAIService
import dev.aurakai.auraframefx.oracledrive.genesis.cloud.CloudStatusMonitor
import dev.aurakai.auraframefx.ai.task.TaskScheduler
import dev.aurakai.auraframefx.ai.task.execution.TaskExecutionManager
import dev.aurakai.auraframefx.common.ErrorHandler
import dev.aurakai.auraframefx.oracledrive.genesis.ai.memory.MemoryManager
import dev.aurakai.auraframefx.utils.AuraFxLogger
import dev.aurakai.auraframefx.oracledrive.genesis.ai.services.GenesisBridgeService
import dev.aurakai.auraframefx.security.SecurityContext
import dev.aurakai.auraframefx.security.SecurityMonitor
import javax.inject.Singleton

/**
 * Dependency Injection module for the Trinity AI system.
 *
 * Provides instances of:
 * - Genesis Bridge Service (Python backend connection)
 * - Trinity Coordinator Service (orchestrates all personas)
 * - Integration with existing Kai and Aura services
 */
@Module
@InstallIn(SingletonComponent::class)
object TrinityModule {


    /**
     * Provides a singleton instance of GenesisBridgeService that integrates multiple AI services with the Trinity Python backend.
     *
     * @return A configured GenesisBridgeService singleton for application-wide use.
     */
    @Provides
    @Singleton
    fun provideGenesisBridgeService(
        auraAIService: AuraAIService,
        kaiAIService: KaiAIService,
        vertexAIClient: VertexAIClient,
        contextManager: ContextManager,
        securityContext: SecurityContext,
        @ApplicationContext applicationContext: Context,
        logger: AuraFxLogger,
    ): GenesisBridgeService {
        return GenesisBridgeService(
            auraAIService = auraAIService,
            kaiAIService = kaiAIService,
            vertexAIClient = vertexAIClient,
            contextManager = contextManager,
            securityContext = securityContext,
            applicationContext = applicationContext,
            logger = logger
        )
    }

    /**
     * Provides a singleton instance of TrinityCoordinatorService for coordinating AI personas within the Trinity AI system.
     *
     * @return A configured singleton of TrinityCoordinatorService.
     */
    @Provides
    @Singleton
    fun provideTrinityCoordinatorService(
        auraAIService: AuraAIService,
        kaiAIService: KaiAIService,
        genesisBridgeService: GenesisBridgeService,
        securityContext: SecurityContext
    ): TrinityCoordinatorService {
        return TrinityCoordinatorService(
            auraAIService = auraAIService,
            kaiAIService = kaiAIService,
            genesisBridgeService = genesisBridgeService,
            securityContext = securityContext
        )
    }

    /**
     * Provides a singleton instance of SecurityMonitor for managing security within the Trinity AI system.
     *
     * @return A configured SecurityMonitor instance.
     */
    @Provides
    @Singleton
    fun provideSecurityMonitor(
        securityContext: SecurityContext,
        genesisBridgeService: GenesisBridgeService,
        logger: AuraFxLogger,
    ): SecurityMonitor {
        return SecurityMonitor(
            securityContext = securityContext,
            genesisBridgeService = genesisBridgeService,
            logger = logger
        )
    }
}
