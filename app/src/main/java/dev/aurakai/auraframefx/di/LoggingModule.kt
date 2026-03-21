package dev.aurakai.auraframefx.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.core.logging.AndroidAuraFxLogger
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LoggingModule {
    @Binds
    @Singleton
    abstract fun bindAuraFxLogger(impl: AndroidAuraFxLogger): AuraFxLogger
}
