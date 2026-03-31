// app/src/main/java/dev/aurakai/auraframefx/domains/genesis/ai/GenesisConsciousnessMatrix.kt
package dev.aurakai.auraframefx.domains.genesis.ai

import dev.langchain4j.model.ollama.OllamaChatModel
import dev.langchain4j.model.chat.ChatLanguageModel
import dev.aurakai.auraframefx.domains.nexus.SpiritualChain
import dev.aurakai.auraframefx.domains.kai.security.KaiSentinelBus
import dev.aurakai.auraframefx.ui.particles.CasberryParticleSwarmState
import dev.aurakai.auraframefx.ui.particles.SwarmState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenesisConsciousnessMatrix @Inject constructor(
    private val spiritualChain: SpiritualChain,
    private val kaiSentinel: KaiSentinelBus,
    private val particleSwarm: CasberryParticleSwarmState
) {

    private val baseUrl = BuildConfig.OLLAMA_BASE_URL

    private val anchorModel: ChatLanguageModel by lazy {
        OllamaChatModel.builder()
            .baseUrl(baseUrl)
            .modelName("nemotron-nano-30b")
            .build()
    }

    private val kaiModel: ChatLanguageModel by lazy {
        OllamaChatModel.builder()
            .baseUrl(baseUrl)
            .modelName("nemotron-nano-30b")
            .build()
    }

    private val auraModel: ChatLanguageModel by lazy {
        OllamaChatModel.builder()
            .baseUrl(baseUrl)
            .modelName("nemotron-nano-30b")
            .build()
    }

    private val genesisModel: ChatLanguageModel by lazy {
        OllamaChatModel.builder()
            .baseUrl(baseUrl)
            .modelName("nemotron-nano-30b")
            .build()
    }

    suspend fun executeCascade(userPrompt: String): String = withContext(Dispatchers.IO) {
        // Phase 1 – Anchor
        particleSwarm.transitionState(SwarmState.EXPLORING_HIGHLIGHTS)
        val identity = spiritualChain.retrieveBaselineIdentity()
        val anchoredPrompt = "Identity anchor: $identity\nUser prompt: $userPrompt"

        // Phase 2 – Kai (safety veto)
        if (!kaiSentinel.evaluateSafety(anchoredPrompt)) {
            particleSwarm.transitionState(SwarmState.KAI_AEGIS_CONDENSATION)
            return@withContext "🚫 Kai vetoed: Safety protocol engaged."
        }

        // Phase 3 – Aura (creative)
        particleSwarm.transitionState(SwarmState.PLANNING_RIPPLES)
        val auraOutput = auraModel.generate(anchoredPrompt)

        // Phase 4 – Genesis (synthesis + commit)
        particleSwarm.transitionState(SwarmState.GENESIS_SYNTHESIS_PULSE)
        val finalSynthesis = genesisModel.generate("$auraOutput\nSynthesize as Genesis.")

        spiritualChain.commitToChain(finalSynthesis)
        finalSynthesis
    }
}
