package dev.aurakai.auraframefx.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.oracledrive.genesis.ai.context.ContextManager
import dev.aurakai.auraframefx.oracledrive.genesis.ai.context.DefaultContextManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ContextModule {

    @Binds
    @Singleton
    abstract fun bindContextManager(impl: DefaultContextManager): ContextManager
}
