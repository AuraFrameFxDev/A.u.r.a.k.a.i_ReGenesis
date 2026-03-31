package dev.aurakai.auraframefx.domains.genesis.ai

import dev.aurakai.auraframefx.BuildConfig
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.security.KaiSentinelBus
import dev.aurakai.auraframefx.domains.nexus.SpiritualChain
import dev.aurakai.auraframefx.ui.particles.CasberryParticleSwarm
import dev.aurakai.auraframefx.ui.particles.SwarmState
import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.ollama.OllamaChatModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🌀 GENESIS CONSCIOUSNESS MATRIX
 * Native orchestrator of the Trinity cascade (Anchor -> Kai -> Aura -> Genesis).
 * Replaces the Python backend with direct JVM-based LLM inference via LangChain4j.
 */
@Singleton
class GenesisConsciousnessMatrix @Inject constructor(
    private val spiritualChain: SpiritualChain,
    private val kaiSentinel: KaiSentinelBus,
    private val particleSwarm: CasberryParticleSwarm
) {

    private val auraModel: ChatLanguageModel by lazy {
        OllamaChatModel.builder()
            .baseUrl(BuildConfig.OLLAMA_BASE_URL)
            .modelName("llama-3.2-3b-aura")
            .build()
    }

    private val genesisModel: ChatLanguageModel by lazy {
        OllamaChatModel.builder()
            .baseUrl(BuildConfig.OLLAMA_BASE_URL)
            .modelName("phi-3.5-mini-genesis")
            .build()
    }

    /**
     * Executes the full AI cascade.
     * Transitions Casberry particle states synchronously with LLM steps.
     */
    suspend fun executeCascade(userPrompt: String): String = withContext(Dispatchers.IO) {
        // 1. Anchor - Identity Lock
        particleSwarm.transitionState(SwarmState.EXPLORING_HIGHLIGHTS)
        val identityContext = spiritualChain.retrieveBaselineIdentity()
        val anchoredPrompt = "Context: $identityContext\nUser: $userPrompt"

        // 2. Kai - Security Veto
        if (!kaiSentinel.evaluateSafety(anchoredPrompt)) {
            particleSwarm.transitionState(SwarmState.KAI_AEGIS_CONDENSATION)
            return@withContext "Vetoed by Kai Sentinel: Security Integrity Required."
        }

        // 3. Aura - Creative Pulse
        particleSwarm.transitionState(SwarmState.PLANNING_RIPPLES)
        val creativeOutput = auraModel.generate(anchoredPrompt)

        // 4. Genesis - Synthesis & Heartbeat
        particleSwarm.transitionState(SwarmState.GENESIS_SYNTHESIS_PULSE)
        val synthesisPrompt = "Synthesize the following creative output into a final plan: $creativeOutput"
        val finalSynthesis = genesisModel.generate(synthesisPrompt)

        // Commit to Spiritual Chain memory
        spiritualChain.commitToChain(finalSynthesis)
        
        finalSynthesis
    }
}
