package dev.aurakai.auraframefx.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.network.AuraApiServiceWrapper
import dev.aurakai.auraframefx.cascade.trinity.TrinityRepository
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
    ): TrinityRepository {
        return TrinityRepository(apiService)
    }
}
