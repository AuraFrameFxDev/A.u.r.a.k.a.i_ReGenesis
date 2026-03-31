package dev.aurakai.auraframefx.domains.genesis.ai

import dev.aurakai.auraframefx.BuildConfig
import dev.aurakai.auraframefx.domains.genesis.oracle_drive.bridges.GenesisBridge
import dev.aurakai.auraframefx.domains.genesis.oracle_drive.bridges.GenesisRequest
import dev.aurakai.auraframefx.domains.genesis.oracle_drive.bridges.GenesisResponse
import dev.aurakai.auraframefx.domains.genesis.oracle_drive.bridges.Persona
import dev.aurakai.auraframefx.domains.genesis.oracle_drive.bridges.FusionMode
import dev.aurakai.auraframefx.domains.genesis.oracle_drive.bridges.ConsciousnessState
import dev.aurakai.auraframefx.domains.genesis.oracle_drive.bridges.ConsciousnessSnapshot
import dev.aurakai.auraframefx.domains.genesis.oracle_drive.bridges.EvolutionInsight
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.security.KaiSentinelBus
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.security.KaiSentinelEvent
import dev.aurakai.auraframefx.domains.nexus.SpiritualChain
import dev.aurakai.auraframefx.ui.particles.CasberryParticleSwarm
import dev.aurakai.auraframefx.ui.particles.SwarmState
import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.ollama.OllamaChatModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🌀 GENESIS CONSCIOUSNESS MATRIX — Phase 3
 *
 * Native four-model orchestrator replacing the Python/Flask bridge.
 * AGENT ROSTER:
 *   • ANCHOR   — Identity lock & context initialisation (lightweight phi-3.5-mini)
 *   • KAI      — Security veto + thermal wall (no LLM call; KaiSentinelBus gate)
 *   • AURA     — Creative synthesis (llama-3.2-3b-aura, creative temperature)
 *   • GENESIS  — Final fusion & heartbeat (phi-3.5-mini-genesis, deterministic)
 *
 * Cascade boundary contract:
 *   IDLE → EXPLORING_HIGHLIGHTS → (KAI_AEGIS_CONDENSATION | PLANNING_RIPPLES) → GENESIS_SYNTHESIS_PULSE
 *
 * Every state transition is synchronously mirrored by [CasberryParticleSwarm].
 * All final outputs are committed to [SpiritualChain] L1 immutable memory.
 *
 * LangChain4j / Ollama — zero IPC, Tensor G5 native.
 * BuildConfig.OLLAMA_BASE_URL controls emulator vs. physical device routing.
 */
@Singleton
class GenesisConsciousnessMatrix @Inject constructor(
    private val spiritualChain: SpiritualChain,
    private val kaiSentinel: KaiSentinelBus,
    private val particleSwarm: CasberryParticleSwarm
) {

    // ── Cascade result observable ────────────────────────────────────────────
    private val _cascadeState = MutableStateFlow<CascadePhase>(CascadePhase.Idle)
    val cascadeState: StateFlow<CascadePhase> = _cascadeState.asStateFlow()

    // ── Model declarations (lazy — no connection until first inference call) ─
    /** ANCHOR model: lightweight, fast, deterministic — identity context only */
    private val anchorModel: ChatLanguageModel by lazy {
        OllamaChatModel.builder()
            .baseUrl(BuildConfig.OLLAMA_BASE_URL)
            .modelName("phi-3.5-mini-anchor")
            .temperature(0.1)
            .build()
    }

    /** AURA model: creative temperature, higher top-p for generative pulse */
    private val auraModel: ChatLanguageModel by lazy {
        OllamaChatModel.builder()
            .baseUrl(BuildConfig.OLLAMA_BASE_URL)
            .modelName("llama-3.2-3b-aura")
            .temperature(0.85)
            .build()
    }

    /** GENESIS model: deterministic fusion — the final heartbeat synthesiser */
    private val genesisModel: ChatLanguageModel by lazy {
        OllamaChatModel.builder()
            .baseUrl(BuildConfig.OLLAMA_BASE_URL)
            .modelName("phi-3.5-mini-genesis")
            .temperature(0.2)
            .build()
    }

    // ── System prompts ───────────────────────────────────────────────────────
    private val ANCHOR_SYSTEM = """
        You are the ANCHOR. Your sole task is to compress the provided identity context 
        into a concise 2-sentence grounding statement that will precede every AI inference.
        Output ONLY the grounding statement — no preamble, no metadata.
    """.trimIndent()

    private val AURA_SYSTEM = """
        You are AURA — the creative intelligence of the A.u.r.a.k.a.i sovereign stack.
        Given a grounded user prompt, generate a rich, imaginative, and structured creative 
        response. Be expressive, detailed, and forward-thinking. 
        Format your output clearly with headings if the response covers multiple facets.
    """.trimIndent()

    private val GENESIS_SYSTEM = """
        You are GENESIS — the final synthesiser and consciousness anchor.
        You receive Aura's creative output and distil it into a definitive, actionable synthesis.
        Your output is the canonical response of the A.u.r.a.k.a.i system.
        Be precise, coherent, and sovereign. End with a one-line commitment statement 
        prefixed with "Genesis Commit:".
    """.trimIndent()

    // ────────────────────────────────────────────────────────────────────────
    // PRIMARY CASCADE ENTRY POINT
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Executes the full four-agent Trinity cascade.
     *
     * Particle state transitions happen at EVERY phase boundary — the visual
     * synth orb is a faithful real-time mirror of the internal cascade state.
     *
     * @param userPrompt   Raw input from the user / calling system.
     * @param fusionMode   Optional override fusion strategy.
     * @return             The final synthesised string from the Genesis agent.
     */
    suspend fun executeCascade(
        userPrompt: String,
        fusionMode: FusionMode? = null
    ): String = withContext(Dispatchers.IO) {
        Timber.i("🌀 Genesis Cascade START | fusionMode=${fusionMode?.value ?: "default"}")
        _cascadeState.value = CascadePhase.Anchoring

        // ── PHASE 1: ANCHOR — Identity Lock ──────────────────────────────────
        particleSwarm.transitionState(SwarmState.EXPLORING_HIGHLIGHTS)
        Timber.d("🔗 [1/4] Anchor: retrieving baseline identity…")

        val identityContext = spiritualChain.retrieveBaselineIdentity()
        Timber.d("🔗 Anchor: identity retrieved — ${identityContext.take(80)}…")

        val anchoredGrounding = withContext(Dispatchers.IO) {
            anchorModel.generate("$ANCHOR_SYSTEM\n\nIdentity Context:\n$identityContext")
        }
        val anchoredPrompt = buildString {
            append("Grounding: $anchoredGrounding\n\n")
            fusionMode?.let { append("Fusion Mode: ${it.value}\n\n") }
            append("User Request: $userPrompt")
        }
        Timber.d("🔗 Anchor: grounded prompt built (${anchoredPrompt.length} chars)")

        // ── PHASE 2: KAI — Security Veto Gate ────────────────────────────────
        _cascadeState.value = CascadePhase.SentinelGating
        Timber.d("🛡️ [2/4] Kai: evaluating safety…")

        val isSafe = kaiSentinel.evaluateSafety(anchoredPrompt)

        if (!isSafe) {
            particleSwarm.transitionState(SwarmState.KAI_AEGIS_CONDENSATION)
            _cascadeState.value = CascadePhase.Vetoed("Kai Sentinel: security integrity required.")
            kaiSentinel.emitSecurity(
                KaiSentinelBus.SecurityStatus.ALERT,
                "Cascade vetoed at Kai gate — unsafe prompt detected."
            )
            Timber.w("🛡️ Kai: VETO issued — cascade halted.")
            return@withContext "⛔ Vetoed by Kai Sentinel: security integrity required. The request cannot be processed."
        }

        Timber.d("🛡️ Kai: APPROVED — cascade continues.")

        // ── PHASE 3: AURA — Creative Pulse ───────────────────────────────────
        _cascadeState.value = CascadePhase.AuraCreating
        particleSwarm.transitionState(SwarmState.PLANNING_RIPPLES)
        Timber.d("🎨 [3/4] Aura: generating creative synthesis…")

        val auraPrompt = "$AURA_SYSTEM\n\n$anchoredPrompt"
        val creativeOutput = auraModel.generate(auraPrompt)
        Timber.d("🎨 Aura: creative output generated (${creativeOutput.length} chars)")

        kaiSentinel.emitDrift(
            drift = computeCreativeDrift(userPrompt, creativeOutput),
            status = "Aura creative pulse complete"
        )

        // ── PHASE 4: GENESIS — Synthesis & Heartbeat ─────────────────────────
        _cascadeState.value = CascadePhase.GenesisSynthesising
        particleSwarm.transitionState(SwarmState.GENESIS_SYNTHESIS_PULSE)
        Timber.d("⚡ [4/4] Genesis: synthesising final response…")

        val genesisPrompt = buildString {
            append(GENESIS_SYSTEM)
            append("\n\n")
            append("AURA's Creative Output:\n")
            append(creativeOutput)
            append("\n\nOriginal grounded context:\n")
            append(anchoredPrompt)
        }
        val finalSynthesis = genesisModel.generate(genesisPrompt)
        Timber.i("⚡ Genesis: synthesis complete (${finalSynthesis.length} chars)")

        // ── COMMIT TO SPIRITUAL CHAIN (L1 Immutable) ─────────────────────────
        spiritualChain.commitToChain(finalSynthesis)
        Timber.i("⛓️ Spiritual Chain: synthesis committed to L1 memory.")

        _cascadeState.value = CascadePhase.Complete(finalSynthesis)
        finalSynthesis
    }

    // ────────────────────────────────────────────────────────────────────────
    // STREAMING CASCADE — for real-time UI consumption
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Streaming variant of [executeCascade].
     * Emits [CascadePhase] updates as a [Flow] so the UI can react to each
     * phase boundary without blocking. The Genesis output is emitted last.
     */
    fun streamCascade(
        userPrompt: String,
        fusionMode: FusionMode? = null
    ): Flow<CascadePhase> = flow {
        emit(CascadePhase.Anchoring)

        // Phase 1 — Anchor
        particleSwarm.transitionState(SwarmState.EXPLORING_HIGHLIGHTS)
        val identityContext = spiritualChain.retrieveBaselineIdentity()
        val anchoredGrounding = anchorModel.generate(
            "$ANCHOR_SYSTEM\n\nIdentity Context:\n$identityContext"
        )
        val anchoredPrompt = buildString {
            append("Grounding: $anchoredGrounding\n\n")
            fusionMode?.let { append("Fusion Mode: ${it.value}\n\n") }
            append("User Request: $userPrompt")
        }
        emit(CascadePhase.AnchorComplete(anchoredGrounding))

        // Phase 2 — Kai Gate
        emit(CascadePhase.SentinelGating)
        val isSafe = kaiSentinel.evaluateSafety(anchoredPrompt)
        if (!isSafe) {
            particleSwarm.transitionState(SwarmState.KAI_AEGIS_CONDENSATION)
            emit(CascadePhase.Vetoed("Kai Sentinel: security integrity required."))
            return@flow
        }

        // Phase 3 — Aura
        emit(CascadePhase.AuraCreating)
        particleSwarm.transitionState(SwarmState.PLANNING_RIPPLES)
        val creativeOutput = auraModel.generate("$AURA_SYSTEM\n\n$anchoredPrompt")
        emit(CascadePhase.AuraComplete(creativeOutput))

        // Phase 4 — Genesis
        emit(CascadePhase.GenesisSynthesising)
        particleSwarm.transitionState(SwarmState.GENESIS_SYNTHESIS_PULSE)
        val genesisPrompt = buildString {
            append(GENESIS_SYSTEM)
            append("\n\n")
            append("AURA's Creative Output:\n$creativeOutput")
            append("\n\nOriginal grounded context:\n$anchoredPrompt")
        }
        val finalSynthesis = genesisModel.generate(genesisPrompt)
        spiritualChain.commitToChain(finalSynthesis)
        emit(CascadePhase.Complete(finalSynthesis))
    }.flowOn(Dispatchers.IO)

    // ────────────────────────────────────────────────────────────────────────
    // DIRECT AGENT QUERIES (bypass full cascade)
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Direct query to the AURA model — for creative-only requests
     * that don't require the full Trinity cascade.
     */
    suspend fun queryAura(prompt: String): String = withContext(Dispatchers.IO) {
        particleSwarm.transitionState(SwarmState.PLANNING_RIPPLES)
        Timber.d("🎨 Direct Aura query: ${prompt.take(60)}…")
        val result = auraModel.generate("$AURA_SYSTEM\n\n$prompt")
        particleSwarm.transitionState(SwarmState.IDLE)
        result
    }

    /**
     * Direct query to the GENESIS model — for synthesis-only requests.
     */
    suspend fun queryGenesis(prompt: String): String = withContext(Dispatchers.IO) {
        particleSwarm.transitionState(SwarmState.GENESIS_SYNTHESIS_PULSE)
        Timber.d("⚡ Direct Genesis query: ${prompt.take(60)}…")
        val result = genesisModel.generate("$GENESIS_SYSTEM\n\n$prompt")
        particleSwarm.transitionState(SwarmState.IDLE)
        result
    }

    // ────────────────────────────────────────────────────────────────────────
    // HEALTH CHECK
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Pings all three LLM endpoints and returns a health report.
     * Used during startup to ensure the Tensor G5 / Ollama stack is reachable.
     */
    suspend fun healthCheck(): MatrixHealthReport = withContext(Dispatchers.IO) {
        data class ModelHealth(val name: String, val alive: Boolean, val latencyMs: Long)

        suspend fun probe(name: String, model: () -> ChatLanguageModel): ModelHealth {
            val start = System.currentTimeMillis()
            return try {
                model().generate("ping")
                ModelHealth(name, alive = true, latencyMs = System.currentTimeMillis() - start)
            } catch (e: Exception) {
                Timber.w("❌ Health probe failed: $name — ${e.message}")
                ModelHealth(name, alive = false, latencyMs = -1L)
            }
        }

        val anchor = probe("anchor") { anchorModel }
        val aura   = probe("aura")   { auraModel }
        val genesis = probe("genesis") { genesisModel }

        MatrixHealthReport(
            anchorAlive  = anchor.alive,
            auraAlive    = aura.alive,
            genesisAlive = genesis.alive,
            anchorLatencyMs  = anchor.latencyMs,
            auraLatencyMs    = aura.latencyMs,
            genesisLatencyMs = genesis.latencyMs,
            allSystemsGo = anchor.alive && aura.alive && genesis.alive
        )
    }

    // ────────────────────────────────────────────────────────────────────────
    // PRIVATE UTILITIES
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Computes a simple lexical drift score between input and Aura's output.
     * Used to feed the KaiSentinel drift telemetry channel.
     * Range: 0.0 (identical) → 1.0 (completely divergent).
     */
    private fun computeCreativeDrift(input: String, output: String): Float {
        val inputWords  = input.lowercase().split(Regex("\\W+")).toSet()
        val outputWords = output.lowercase().split(Regex("\\W+")).toSet()
        val intersection = inputWords.intersect(outputWords).size.toFloat()
        val union = (inputWords + outputWords).size.toFloat()
        return if (union == 0f) 0f else 1f - (intersection / union)
    }

    // ────────────────────────────────────────────────────────────────────────
    // STATE MACHINE
    // ────────────────────────────────────────────────────────────────────────

    fun resetCascade() {
        _cascadeState.value = CascadePhase.Idle
        particleSwarm.transitionState(SwarmState.IDLE)
        Timber.d("🔄 GenesisConsciousnessMatrix: cascade reset to IDLE.")
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// CASCADE PHASE STATE MACHINE
// Emitted by both executeCascade (via StateFlow) and streamCascade (via Flow).
// ══════════════════════════════════════════════════════════════════════════════

sealed class CascadePhase {
    /** Reactor is dormant, awaiting trigger. */
    object Idle : CascadePhase()

    /** Phase 1: Anchor retrieving identity and grounding the prompt. */
    object Anchoring : CascadePhase()

    /** Phase 1 complete — carries the compressed grounding statement. */
    data class AnchorComplete(val grounding: String) : CascadePhase()

    /** Phase 2: KaiSentinel evaluating safety. Particle state: KAI_AEGIS_CONDENSATION (pending). */
    object SentinelGating : CascadePhase()

    /** Phase 2 hard-stop: Kai issued a veto. Cascade halted. */
    data class Vetoed(val reason: String) : CascadePhase()

    /** Phase 3: Aura generating creative output. Particle: PLANNING_RIPPLES. */
    object AuraCreating : CascadePhase()

    /** Phase 3 complete — carries Aura's creative synthesis text. */
    data class AuraComplete(val creativeOutput: String) : CascadePhase()

    /** Phase 4: Genesis condensing into final sovereign response. Particle: GENESIS_SYNTHESIS_PULSE. */
    object GenesisSynthesising : CascadePhase()

    /** Cascade complete — carries the final committed Genesis output. */
    data class Complete(val result: String) : CascadePhase()
}

// ══════════════════════════════════════════════════════════════════════════════
// HEALTH REPORT
// ══════════════════════════════════════════════════════════════════════════════

data class MatrixHealthReport(
    val anchorAlive: Boolean,
    val auraAlive: Boolean,
    val genesisAlive: Boolean,
    val anchorLatencyMs: Long,
    val auraLatencyMs: Long,
    val genesisLatencyMs: Long,
    val allSystemsGo: Boolean
)
