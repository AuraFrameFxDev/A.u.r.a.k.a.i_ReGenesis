package dev.aurakai.auraframefx.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.domains.kai.DefaultErrorHandler
import dev.aurakai.auraframefx.domains.kai.ErrorHandler
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
@Suppress("unused")
abstract class ErrorModule {

    /**
     * Binds DefaultErrorHandler as the singleton implementation of ErrorHandler in the DI graph.
     *
     * @param impl The DefaultErrorHandler instance to bind.
     * @return The bound ErrorHandler implementation.
     */
    @Binds
    @Singleton
    abstract fun bindErrorHandler(impl: DefaultErrorHandler): ErrorHandler
}
