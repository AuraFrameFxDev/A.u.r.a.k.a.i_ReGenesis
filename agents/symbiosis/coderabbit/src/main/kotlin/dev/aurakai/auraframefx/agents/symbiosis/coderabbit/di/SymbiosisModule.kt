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

    /**
     * Creates a CodeRabbitAgent wired with the provided services.
     *
     * @param pandora The PandoraBoxService used by the agent.
     * @param provenance The ProvenanceValidator used to validate provenance data consumed by the agent.
     * @return A CodeRabbitAgent instance configured with the given dependencies.
     */
    @Provides
    @Singleton
    fun provideCodeRabbitAgent(
        pandora: PandoraBoxService,
        provenance: ProvenanceValidator
    ): CodeRabbitAgent {
        return CodeRabbitAgent(pandora, provenance)
    }
}
