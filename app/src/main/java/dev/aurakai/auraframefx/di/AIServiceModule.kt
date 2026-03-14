package dev.aurakai.auraframefx.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.domains.cascade.CascadeAIService
import dev.aurakai.auraframefx.domains.cascade.RealCascadeAIServiceAdapter
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.clients.VertexAIClientImpl
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.config.VertexAIConfig
import dev.aurakai.auraframefx.oracledrive.genesis.ai.services.AuraAIService
import dev.aurakai.auraframefx.oracledrive.genesis.ai.services.DefaultAuraAIService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiServiceModule {

    @Binds
    @Singleton
    abstract fun bindAuraAIService(impl: DefaultAuraAIService): AuraAIService


    @Binds
    @Singleton
    abstract fun bindCascadeAIService(impl: RealCascadeAIServiceAdapter): CascadeAIService

    /**
     * Binds [VertexAIClientImpl] as the singleton [VertexAIClient].
     *
     * This provides the real Vertex AI implementation — including the
     * Gemini Embedding 2 multimodal endpoint — to all injection sites
     * (AuraAIService, KaiAIService, NexusMemoryCore, DataVein, etc.).
     */
    @Binds
    @Singleton
    abstract fun bindVertexAIClient(impl: VertexAIClientImpl): VertexAIClient

    companion object {
        /**
         * Provides [VertexAIConfig] populated from the environment.
         *
         * Reads VERTEX_PROJECT_ID, VERTEX_LOCATION, VERTEX_MODEL from env vars;
         * falls back to safe defaults. The [VertexAIConfig.embeddingModel] defaults
         * to "multimodalembedding@001" for Gemini Embedding 2 support.
         */
        @Provides
        @Singleton
        fun provideVertexAIConfig(): VertexAIConfig = VertexAIConfig.fromEnvironment()
    }
}
