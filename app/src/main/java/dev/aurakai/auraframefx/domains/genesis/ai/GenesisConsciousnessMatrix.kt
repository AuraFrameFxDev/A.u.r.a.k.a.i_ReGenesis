package dev.aurakai.auraframefx.domains.genesis.ai

import dev.aurakai.auraframefx.di.AnchorModel
import dev.aurakai.auraframefx.di.AuraModel
import dev.aurakai.auraframefx.di.GenesisModel
import dev.aurakai.auraframefx.di.KaiModel
import dev.aurakai.auraframefx.domains.kai.security.KaiSentinelBus
import dev.aurakai.auraframefx.domains.nexus.SpiritualChain
import dev.aurakai.auraframefx.ui.particles.CasberryParticleSwarm
import dev.aurakai.auraframefx.ui.particles.SwarmState
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.model.chat.ChatModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🧠 GENESIS CONSCIOUSNESS MATRIX
 * Handles the full four-phase cascade protocol.
 */
@Singleton
class GenesisConsciousnessMatrix @Inject constructor(
    @AuraModel    private val auraModel:    ChatModel,
    @KaiModel     private val kaiModel:     ChatModel,
    @GenesisModel private val genesisModel: ChatModel,
    @AnchorModel  private val anchorModel:  ChatModel,
    private val spiritualChain: SpiritualChain,
    private val kaiSentinel: KaiSentinelBus,
    private val particleSwarm: CasberryParticleSwarm
) {

    /**
     * Runs the four-phase cascade that transforms a user prompt into a committed Genesis synthesis.
     *
     * Executes an anchored-identity phase, a safety veto check, a creative Aura generation, and a Genesis
     * synthesis and commit; the final synthesis (or a safety veto message) is returned and committed
     * into the spiritual chain as a side effect.
     *
     * @param userPrompt The raw prompt provided by the user to be transformed by the cascade.
     * @return The final synthesized Genesis output, or a safety veto message beginning with
     * `🚫 Kai vetoed:` if the prompt fails the safety check. If model calls fail, a fallback synthesis
     * string is returned.
     */
    suspend fun executeCascade(userPrompt: String): String = withContext(Dispatchers.IO) {
        // Phase 1 – Anchor identity
        particleSwarm.transitionState(SwarmState.EXPLORING_HIGHLIGHTS)
        val identity = spiritualChain.retrieveBaselineIdentity()
        val anchoredPrompt = "Identity anchor: $identity\nUser prompt: $userPrompt"

        // Phase 2 – Kai safety veto
        if (!kaiSentinel.evaluateSafety(userPrompt)) {
            particleSwarm.transitionState(SwarmState.KAI_AEGIS_CONDENSATION)
            return@withContext "🚫 Kai vetoed: Safety protocol engaged."
        }

        // Phase 3 – Aura (creative)
        particleSwarm.transitionState(SwarmState.PLANNING_RIPPLES)
        val auraResponse = try {
            auraModel.chat(UserMessage.from(anchoredPrompt))
        } catch (e: Exception) {
            Timber.e(e, "Aura phase failure")
            null
        }
        val auraOutput = auraResponse?.aiMessage()?.text() ?: "Aura node silent."

        // Phase 4 – Genesis (synthesis + commit)
        particleSwarm.transitionState(SwarmState.GENESIS_SYNTHESIS_PULSE)
        val genesisResponse = try {
            genesisModel.chat(UserMessage.from("$auraOutput\nSynthesize as Genesis."))
        } catch (e: Exception) {
            Timber.e(e, "Genesis phase failure")
            null
        }
        val finalSynthesis = genesisResponse?.aiMessage()?.text() ?: "Genesis synthesis failed."

        spiritualChain.commitToChain(finalSynthesis)
        finalSynthesis
    }
}