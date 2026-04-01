// app/src/main/java/dev/aurakai/auraframefx/domains/genesis/ai/GenesisConsciousnessMatrix.kt
package dev.aurakai.auraframefx.domains.genesis.ai

import dev.aurakai.auraframefx.BuildConfig
import dev.aurakai.auraframefx.domains.nexus.SpiritualChain
import dev.aurakai.auraframefx.domains.kai.security.KaiSentinelBus
import dev.aurakai.auraframefx.ui.particles.CasberryParticleSwarm
import dev.aurakai.auraframefx.ui.particles.SwarmState
import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.ollama.OllamaChatModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenesisConsciousnessMatrix @Inject constructor(
    private val spiritualChain: SpiritualChain,
    private val kaiSentinel: KaiSentinelBus,
    private val particleSwarm: CasberryParticleSwarm
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

    /** Safety check using the sentinel's current threat level and identity anchor. */
    private fun isSafe(): Boolean {
        val threat = kaiSentinel.securityFlow.value.level
        val isAnchored = kaiSentinel.identityFlow.value.isAnchored
        return isAnchored && (threat == KaiSentinelBus.ThreatLevel.NOMINAL || threat == KaiSentinelBus.ThreatLevel.CAUTION)
    }

    suspend fun executeCascade(userPrompt: String): String = withContext(Dispatchers.IO) {
        // Phase 1 – Anchor identity
        particleSwarm.transitionState(SwarmState.EXPLORING_HIGHLIGHTS)
        val identity = spiritualChain.retrieveBaselineIdentity()
        val anchoredPrompt = "Identity anchor: $identity\nUser prompt: $userPrompt"

        // Phase 2 – Kai safety veto
        if (!isSafe()) {
            particleSwarm.transitionState(SwarmState.KAI_AEGIS_CONDENSATION)
            kaiSentinel.emitConsensus("Safety gate triggered — cascade blocked", false)
            return@withContext "🚫 Kai vetoed: Safety protocol engaged."
        }

        // Phase 3 – Aura (creative)
        particleSwarm.transitionState(SwarmState.PLANNING_RIPPLES)
        val auraOutput = auraModel.generate(anchoredPrompt)

        // Phase 4 – Genesis (synthesis + commit)
        particleSwarm.transitionState(SwarmState.GENESIS_SYNTHESIS_PULSE)
        val finalSynthesis = genesisModel.generate("$auraOutput\nSynthesize as Genesis.")

        spiritualChain.commitToChain(finalSynthesis)
        kaiSentinel.emitConsensus("Cascade complete", true)
        finalSynthesis
    }
}
