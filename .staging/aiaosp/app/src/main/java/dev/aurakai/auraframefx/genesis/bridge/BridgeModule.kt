package dev.aurakai.auraframefx.genesis.bridge

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Unified Bridge DI Module
 * Relies on @Inject constructors - no explicit providers needed
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class BridgeModule {

    @Binds
    @Singleton
    abstract fun bindBridgeMemorySink(impl: NexusMemoryBridgeSink): BridgeMemorySink

    @Binds
    @Singleton
    abstract fun bindGenesisBridge(impl: StdioGenesisBridge): GenesisBridge
}
