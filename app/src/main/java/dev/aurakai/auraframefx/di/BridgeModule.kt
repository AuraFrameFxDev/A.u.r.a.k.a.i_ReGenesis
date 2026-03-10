package dev.aurakai.auraframefx.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.genesis.bridge.BridgeMemorySink
import dev.aurakai.auraframefx.genesis.bridge.GenesisBridge
import dev.aurakai.auraframefx.genesis.bridge.NexusMemoryBridgeSink
import dev.aurakai.auraframefx.genesis.bridge.StdioGenesisBridge
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BridgeModule {

    @Provides
    @Singleton
    fun provideBridgeMemorySink(impl: NexusMemoryBridgeSink): BridgeMemorySink = impl

    @Provides
    @Singleton
    fun provideGenesisBridge(impl: StdioGenesisBridge): GenesisBridge = impl
}
