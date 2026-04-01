package dev.aurakai.auraframefx.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.BuildConfig
import dev.aurakai.auraframefx.core.alerts.AlertNotifier
import dev.aurakai.auraframefx.core.alerts.SystemAlertNotifier
import dev.aurakai.auraframefx.domains.nexus.SpiritualChain
import dev.aurakai.auraframefx.domains.nexus.SpiritualChainImpl
import dev.langchain4j.model.ollama.OllamaChatModel
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier @Retention(AnnotationRetention.BINARY) annotation class AuraModel
@Qualifier @Retention(AnnotationRetention.BINARY) annotation class KaiModel
@Qualifier @Retention(AnnotationRetention.BINARY) annotation class GenesisModel
@Qualifier @Retention(AnnotationRetention.BINARY) annotation class AnchorModel

@Module
@InstallIn(SingletonComponent::class)
abstract class ConsciousnessBindsModule {
    @Binds @Singleton
    abstract fun bindSpiritualChain(impl: SpiritualChainImpl): SpiritualChain

    @Binds @Singleton
    abstract fun bindAlertNotifier(impl: SystemAlertNotifier): AlertNotifier
}

@Module
@InstallIn(SingletonComponent::class)
object ConsciousnessModule {
    private fun buildModel(name: String, temp: Double, timeoutSec: Long = 60) =
        OllamaChatModel.builder()
            .baseUrl(BuildConfig.OLLAMA_BASE_URL)
            .modelName(name)
            .temperature(temp)
            .timeout(java.time.Duration.ofSeconds(timeoutSec))
            .build()

    @Provides @Singleton @AuraModel
    fun provideAuraModel(): OllamaChatModel = buildModel("llama3.2:3b", 0.85)

    @Provides @Singleton @KaiModel
    fun provideKaiModel(): OllamaChatModel = buildModel("llama3.2:3b", 0.20)

    @Provides @Singleton @GenesisModel
    fun provideGenesisModel(): OllamaChatModel = buildModel("llama3.2:3b", 0.20)

    @Provides @Singleton @AnchorModel
    fun provideAnchorModel(): OllamaChatModel = buildModel("llama3.2:3b", 0.10)
}
