package dev.aurakai.auraframefx.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.domains.genesis.network.KtorClient
import io.ktor.client.HttpClient
import javax.inject.Singleton

/**
 * Dagger Hilt module that provides network-related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object KtorModule {

    /**
     * Provides a singleton instance of [KtorClient].
     * 
     * @return Configured [KtorClient] instance
     */
    @Provides
    @Singleton
    fun provideKtorClient(): KtorClient {
        return KtorClient()
    }

    /**
     * Provides the underlying Ktor [HttpClient] from [KtorClient].
     * 
     * @param ktorClient The [KtorClient] instance
     * @return Configured [HttpClient] instance
     */
    @Provides
    @Singleton
    fun provideHttpClient(ktorClient: KtorClient) = ktorClient.client
}

