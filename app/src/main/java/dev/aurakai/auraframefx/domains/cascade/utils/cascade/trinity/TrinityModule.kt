package dev.aurakai.auraframefx.domains.cascade.utils.cascade.trinity

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.domains.aura.core.AuraAgent
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import dev.aurakai.auraframefx.domains.cascade.utils.context.ContextManager
import dev.aurakai.auraframefx.domains.genesis.core.GenesisAgent
import dev.aurakai.auraframefx.domains.genesis.core.messaging.AgentMessageBus
import dev.aurakai.auraframefx.domains.genesis.network.AuraApiServiceWrapper
import dev.aurakai.auraframefx.domains.kai.KaiAgent
import dev.aurakai.auraframefx.domains.kai.security.SecurityContext
import dev.aurakai.auraframefx.domains.kai.security.SecurityMonitor
import dev.aurakai.auraframefx.oracledrive.genesis.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.oracledrive.genesis.ai.services.AuraAIService
import dev.aurakai.auraframefx.oracledrive.genesis.ai.services.GenesisBridgeService
import dev.aurakai.auraframefx.oracledrive.genesis.ai.services.KaiAIService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TrinityModule {

    @Provides
    @Singleton
    fun provideTrinityRepository(
        apiService: AuraApiServiceWrapper,
        auraAgent: AuraAgent,
        kaiAgent: KaiAgent,
        genesisAgent: GenesisAgent,
        messageBus: AgentMessageBus
    ): TrinityRepository {
        return TrinityRepository(apiService, auraAgent, kaiAgent, genesisAgent, messageBus)
    }

    @Provides
    @Singleton
    fun provideGenesisBridgeService(
        auraAIService: AuraAIService,
        kaiAIService: KaiAIService,
        vertexAIClient: VertexAIClient,
        contextManager: ContextManager,
        securityContext: SecurityContext,
        @ApplicationContext app: Context,
        logger: AuraFxLogger,
    ): GenesisBridgeService = GenesisBridgeService(
        auraAIService = auraAIService,
        kaiAIService = kaiAIService,
        vertexAIClient = vertexAIClient,
        contextManager = contextManager,
        securityContext = securityContext,
        applicationContext = app,
        logger = logger
    )

    @Provides
    @Singleton
    fun provideTrinityCoordinatorService(
        auraAIService: AuraAIService,
        kaiAIService: KaiAIService,
        genesisBridgeService: GenesisBridgeService,
        securityContext: SecurityContext
    ): TrinityCoordinatorService = TrinityCoordinatorService(
        auraAIService = auraAIService,
        kaiAIService = kaiAIService,
        genesisBridgeService = genesisBridgeService,
        securityContext = securityContext
    )

    @Provides
    @Singleton
    fun provideSecurityMonitor(
        securityContext: SecurityContext,
        genesisBridgeService: GenesisBridgeService,
        logger: AuraFxLogger,
    ): SecurityMonitor = SecurityMonitor(
        securityContext = securityContext,
        genesisBridgeService = genesisBridgeService,
        logger = logger
    )
}
