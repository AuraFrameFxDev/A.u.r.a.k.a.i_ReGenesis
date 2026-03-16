package dev.aurakai.auraframefx.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.clients.DefaultVertexAIClient
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.clients.VertexAIClient as OracleVertexAIClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OracleVertexAiModule {
    @Binds
    @Singleton
    abstract fun bindOracleVertexAiClient(impl: DefaultVertexAIClient): OracleVertexAIClient
}

