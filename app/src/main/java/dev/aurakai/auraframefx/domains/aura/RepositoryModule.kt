package dev.aurakai.auraframefx.domains.aura

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.domains.genesis.core.messaging.AgentMessageBus
import dev.aurakai.auraframefx.domains.genesis.network.AuraApiServiceWrapper
import dev.aurakai.auraframefx.domains.kai.KaiAgent
import dev.aurakai.auraframefx.domains.cascade.utils.cascade.trinity.TrinityRepository
import dev.aurakai.auraframefx.domains.genesis.core.GenesisAgent
import javax.inject.Singleton

/**
 * Hilt module that provides repository dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    /**
     * Provides the [TrinityRepository] implementation.
     *
     * @param apiService The API service wrapper for network operations.
     * @return An instance of [TrinityRepository].
     */
    @Provides
    @Singleton
    fun provideTrinityRepository(
        apiService: AuraApiServiceWrapper,
        auraAgent: dev.aurakai.auraframefx.domains.aura.core.AuraAgent,
        kaiAgent: KaiAgent,
        genesisAgent: GenesisAgent,
        messageBus: AgentMessageBus
    ): TrinityRepository {
        return TrinityRepository(apiService, auraAgent, kaiAgent, genesisAgent, messageBus)
    }
}


