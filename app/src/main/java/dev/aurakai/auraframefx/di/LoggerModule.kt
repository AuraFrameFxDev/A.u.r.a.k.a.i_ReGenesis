package dev.aurakai.auraframefx.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger as LoggerInterface
import dev.aurakai.auraframefx.domains.kai.DefaultAuraFxLogger as LoggerImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class LoggerModule {

    @Binds
    @Singleton
    abstract fun bindAuraFxLogger(impl: LoggerImpl): LoggerInterface
}

