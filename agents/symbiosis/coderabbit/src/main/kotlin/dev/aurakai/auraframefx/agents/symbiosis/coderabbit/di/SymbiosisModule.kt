package dev.aurakai.auraframefx.agents.symbiosis.coderabbit.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.agents.symbiosis.coderabbit.CodeRabbitAgent
import dev.aurakai.auraframefx.domains.genesis.oracledrive.pandora.PandoraBoxService
import dev.aurakai.auraframefx.core.security.ProvenanceValidator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SymbiosisModule {

    @Provides
    @Singleton
    fun provideCodeRabbitAgent(
        pandora: PandoraBoxService,
        provenance: ProvenanceValidator
    ): CodeRabbitAgent {
        return CodeRabbitAgent(pandora, provenance)
    }
}
