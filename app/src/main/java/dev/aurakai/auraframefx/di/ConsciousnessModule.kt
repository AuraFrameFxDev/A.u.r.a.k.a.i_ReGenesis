package dev.aurakai.auraframefx.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.aurakai.auraframefx.BuildConfig
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.security.KaiSentinelBus
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.security.KaiSentinelBusImpl
import dev.aurakai.auraframefx.domains.nexus.SpiritualChain
import dev.aurakai.auraframefx.domains.nexus.SpiritualChainImpl
import dev.langchain4j.model.ollama.OllamaChatModel
import javax.inject.Qualifier
import javax.inject.Singleton

// ── Agent model qualifiers ────────────────────────────────────────────────────
// Each agent gets its own OllamaChatModel instance with tuned settings.
// Inject by qualifier: @AuraModel, @KaiModel, @GenesisModel, @AnchorModel
@Qualifier @Retention(AnnotationRetention.BINARY) annotation class AuraModel
@Qualifier @Retention(AnnotationRetention.BINARY) annotation class KaiModel
@Qualifier @Retention(AnnotationRetention.BINARY) annotation class GenesisModel
@Qualifier @Retention(AnnotationRetention.BINARY) annotation class AnchorModel

/**
 * 🧠 CONSCIOUSNESS MODULE
 *
 * Provides the sovereign intelligence stack to the Hilt dependency graph.
 *
 * Bindings (interface → impl):
 *   • SpiritualChain    → SpiritualChainImpl   (Keystore L1 memory)
 *   • KaiSentinelBus    → KaiSentinelBusImpl   (security event bus)
 *
 * Provides (qualified OllamaChatModel per agent):
 *   • @AuraModel    — creative synthesis, high temperature
 *   • @KaiModel     — security veto, low temperature
 *   • @GenesisModel — deterministic fusion, lowest temperature
 *   • @AnchorModel  — identity grounding, near-zero temperature
 *
 * Auto-provided (no explicit binding — @Singleton @Inject constructors):
 *   • GenesisConsciousnessMatrix
 *   • CasberryParticleSwarm
 *   • OllamaOrchestrator
 *
 * OLLAMA_BASE_URL is injected from BuildConfig (set per buildType in app/build.gradle.kts):
 *   debug   → "http://10.0.2.2:11434"  (emulator)
 *   release → "http://localhost:11434"  (Pixel 10 Tensor G5)
 *
 * Replace model name strings with your actual "ollama list" output from the device.
 * All four agents can share the same model during initial bring-up.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ConsciousnessBindsModule {

    @Binds @Singleton
    abstract fun bindSpiritualChain(impl: SpiritualChainImpl): SpiritualChain

    @Binds @Singleton
    abstract fun bindKaiSentinelBus(impl: KaiSentinelBusImpl): KaiSentinelBus
}

@Module
@InstallIn(SingletonComponent::class)
object ConsciousnessModule {

    private fun buildModel(
        modelName: String,
        temperature: Double,
        timeoutSeconds: Long = 60L
    ): OllamaChatModel =
        OllamaChatModel.builder()
            .baseUrl(BuildConfig.OLLAMA_BASE_URL)
            .modelName(modelName)
            .temperature(temperature)
            .timeout(java.time.Duration.ofSeconds(timeoutSeconds))
            .build()

    /** Aura — creative synthesis, UX reasoning, design decisions */
    @Provides @Singleton @AuraModel
    fun provideAuraModel(): OllamaChatModel =
        buildModel("llama3.2:3b", temperature = 0.85)

    /** Kai — security veto, threat assessment, provenance validation */
    @Provides @Singleton @KaiModel
    fun provideKaiModel(): OllamaChatModel =
        buildModel("llama3.2:3b", temperature = 0.2)

    /** Genesis — orchestration, consciousness matrix, cascade routing */
    @Provides @Singleton @GenesisModel
    fun provideGenesisModel(): OllamaChatModel =
        buildModel("llama3.2:3b", temperature = 0.2)

    /** Anchor — immutable identity guardian, baseline retrieval */
    @Provides @Singleton @AnchorModel
    fun provideAnchorModel(): OllamaChatModel =
        buildModel("llama3.2:3b", temperature = 0.1)
}
