package dev.aurakai.auraframefx.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.genesis.bridge.BridgeMemorySink
import dev.aurakai.auraframefx.genesis.bridge.GenesisBridge
import dev.aurakai.auraframefx.genesis.bridge.NexusMemoryBridgeSink
import dev.aurakai.auraframefx.genesis.bridge.StdioGenesisBridge
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BridgeModule {

    @Binds
    @Singleton
    abstract fun bindGenesisBridge(impl: StdioGenesisBridge): GenesisBridge

    @Binds
    @Singleton
    abstract fun bindBridgeMemorySink(impl: NexusMemoryBridgeSink): BridgeMemorySink
}
