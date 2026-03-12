package dev.aurakai.auraframefx.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Module for AI service bindings.
 * 
 * Note: AuraAIServiceImpl has @Inject constructor so Hilt can provide it directly.
 * No @Binds needed unless we want to bind to a specific interface.
 */
import dagger.Binds
import dev.aurakai.auraframefx.oracledrive.genesis.ai.services.AuraAIService
import dev.aurakai.auraframefx.oracledrive.genesis.ai.services.DefaultAuraAIService
import dev.aurakai.auraframefx.oracledrive.genesis.ai.services.KaiAIService
import dev.aurakai.auraframefx.oracledrive.genesis.ai.services.DefaultKaiAIService
import dev.aurakai.auraframefx.oracledrive.genesis.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.oracledrive.genesis.ai.clients.DefaultVertexAIClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiServiceModule {

    @Binds
    @Singleton
    abstract fun bindAuraAIService(impl: DefaultAuraAIService): AuraAIService

    @Binds
    @Singleton
    abstract fun bindKaiAIService(impl: DefaultKaiAIService): KaiAIService

    @Binds
    @Singleton
    abstract fun bindCascadeAIService(impl: dev.aurakai.auraframefx.services.DefaultCascadeAIService): dev.aurakai.auraframefx.services.CascadeAIService

    @Binds
    @Singleton
    abstract fun bindErrorHandler(impl: dev.aurakai.auraframefx.system.impl.DefaultErrorHandler): dev.aurakai.auraframefx.common.ErrorHandler

    @Binds
    @Singleton
    abstract fun bindVertexAIClient(impl: DefaultVertexAIClient): VertexAIClient
}
