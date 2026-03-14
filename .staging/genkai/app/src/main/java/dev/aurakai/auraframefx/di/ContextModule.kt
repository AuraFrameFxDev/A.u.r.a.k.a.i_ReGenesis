package dev.aurakai.auraframefx.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.ai.context.ContextManager
import dev.aurakai.auraframefx.utils.AuraFxLogger
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ContextModule {

    @Provides
    @Singleton
    fun provideContextManager(logger: AuraFxLogger): ContextManager {
        return ContextManager(logger)
    }
}