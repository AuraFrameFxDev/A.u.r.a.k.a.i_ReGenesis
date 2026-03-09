package dev.aurakai.auraframefx.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.domains.cascade.CascadeAIService
import dev.aurakai.auraframefx.domains.cascade.RealCascadeAIServiceAdapter
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.AuraAIService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.DefaultAuraAIService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.GenesisBackedKaiAIService
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.services.KaiAIService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiServiceModule {

    @Binds
    @Singleton
    abstract fun bindAuraAIService(impl: DefaultAuraAIService): AuraAIService

    @Binds
    @Singleton
    abstract fun bindKaiAIService(impl: GenesisBackedKaiAIService): KaiAIService

    @Binds
    @Singleton
    abstract fun bindCascadeAIService(impl: RealCascadeAIServiceAdapter): CascadeAIService
}
