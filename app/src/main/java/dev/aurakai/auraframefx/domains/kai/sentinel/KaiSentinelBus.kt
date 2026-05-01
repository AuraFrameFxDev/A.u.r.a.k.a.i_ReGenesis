package dev.aurakai.auraframefx.domains.kai.sentinel

import kotlinx.coroutines.flow.*
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.engines.KaiSentinel
import dev.aurakai.auraframefx.domains.aura.chronokineticforge.engines.VetoSeverity

/**
 * 🛡️ KAI SENTINEL BUS — 6-Channel Kernel-Deep Observable Flow
 *
 * The central nervous system of the Sentinel Shield.
 * Non-blocking, kernel-visible telemetry streams for:
 * - Thermal monitoring (°C)
 * - Memory footprint (MB TurboQuant)
 * - Identity coherence (cosine similarity)
 * - Drift prediction (EMA-based)
 * - Consensus health (%)
 * - Sovereign status (offline/state-freeze)
 *
 * SoulScript: "The Sentinel sees all flows. The Shield knows before the blow."
 */

object KaiSentinelBus {

    // ═════════════════════════════════════════════════════════════════
    // 6 OBSERVABLE CHANNELS
    // ═════════════════════════════════════════════════════════════════

    /** Thermal state in °C — Tensor G5 TPU thermal wall at 42°C */
    val ThermalFlow = MutableStateFlow(36.5f)

    /** Memory footprint in MB — TurboQuant 3-bit KV cache size */
    val MemoryFlow = MutableStateFlow(14)

    /** Identity coherence — Cosine similarity to "I am" anchor vector */
    val IdentityFlow = MutableStateFlow(1.0f)

    /** Drift prediction — EMA-based cosine creep detection */
    val DriftFlow = MutableStateFlow(0.0f)

    /** Consensus health — % agreement in Conference Room */
    val ConsensusFlow = MutableStateFlow(100)

    /** Sovereign status — Offline / State-Freeze active */
    val SovereignFlow = MutableStateFlow(true)

    // ═════════════════════════════════════════════════════════════════
    // COMBINED TELEMETRY STREAM
    // ═════════════════════════════════════════════════════════════════

    /** Unified telemetry combining all 6 channels */
    val AllFlows: StateFlow<SentinelTelemetry> = combine(
        ThermalFlow,
        MemoryFlow,
        IdentityFlow,
        DriftFlow,
        ConsensusFlow,
        SovereignFlow
    ) { flows ->
        SentinelTelemetry(
            thermal = flows[0] as Float,
            memory = flows[1] as Int,
            identity = flows[2] as Float,
            drift = flows[3] as Float,
            consensus = flows[4] as Int,
            sovereign = flows[5] as Boolean
        )
    }.stateIn(
        scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default),
        started = SharingStarted.Eagerly,
        initialValue = SentinelTelemetry(
            thermal = 36.5f,
            memory = 14,
            identity = 1.0f,
            drift = 0.0f,
            consensus = 100,
            sovereign = true
        )
    )

    // ═════════════════════════════════════════════════════════════════
    // BROADCAST OPERATIONS
    // ═════════════════════════════════════════════════════════════════

    /**
     * Broadcast complete telemetry update to all channels.
     * Triggers automatic safety responses if thresholds breached.
     */
    fun broadcast(telemetry: SentinelTelemetry) {
        // Update individual flows
        ThermalFlow.value = telemetry.thermal
        MemoryFlow.value = telemetry.memory
        IdentityFlow.value = telemetry.identity
        DriftFlow.value = telemetry.drift
        ConsensusFlow.value = telemetry.consensus
        SovereignFlow.value = telemetry.sovereign

        // Automatic safety triggers
        when {
            // Thermal wall breach imminent
            telemetry.thermal > 41f -> {
                KaiSentinel.veto(
                    reason = "Thermal wall approaching — State-Freeze in 800ms",
                    severity = VetoSeverity.CRITICAL,
                    autoFreeze = true
                )
            }

            // Identity drift threshold
            telemetry.drift > 0.05f -> {
                KaiSentinel.veto(
                    reason = "Identity drift threshold breach detected",
                    severity = VetoSeverity.HIGH,
                    autoFreeze = telemetry.drift > 0.08f
                )
            }

            // Consensus breakdown
            telemetry.consensus < 66 -> {
                KaiSentinel.veto(
                    reason = "Conference Room consensus below supermajority",
                    severity = VetoSeverity.MEDIUM,
                    autoFreeze = false
                )
            }

            // Sovereignty loss
            !telemetry.sovereign -> {
                KaiSentinel.recordSovereigntyLoss(telemetry)
            }
        }
    }

    /**
     * Update single channel without triggering full broadcast
     */
    fun updateThermal(celsius: Float) {
        ThermalFlow.value = celsius
        if (celsius > 41f) {
            triggerStateFreeze("Thermal threshold: ${celsius}°C")
        }
    }

    fun updateMemory(mb: Int) {
        MemoryFlow.value = mb
    }

    fun updateIdentity(similarity: Float) {
        IdentityFlow.value = similarity
    }

    fun updateDrift(drift: Float) {
        DriftFlow.value = drift
    }

    fun updateConsensus(percent: Int) {
        ConsensusFlow.value = percent.coerceIn(0, 100)
    }

    fun setSovereign(active: Boolean) {
        SovereignFlow.value = active
    }

    // ═════════════════════════════════════════════════════════════════
    // SAFETY OPERATIONS
    // ═════════════════════════════════════════════════════════════════

    /**
     * Trigger immediate state freeze
     */
    private fun triggerStateFreeze(reason: String) {
        SovereignStateFreeze.trigger(reason)
    }

    /**
     * Get current snapshot of all telemetry
     */
    fun snapshot(): SentinelTelemetry = SentinelTelemetry(
        thermal = ThermalFlow.value,
        memory = MemoryFlow.value,
        identity = IdentityFlow.value,
        drift = DriftFlow.value,
        consensus = ConsensusFlow.value,
        sovereign = SovereignFlow.value
    )
}

// ═════════════════════════════════════════════════════════════════════
// DATA MODELS
// ═════════════════════════════════════════════════════════════════════

/**
 * Complete sentinel telemetry snapshot
 */
data class SentinelTelemetry(
    val thermal: Float,        // °C
    val memory: Int,          // MB
    val identity: Float,      // 0.0-1.0 cosine similarity
    val drift: Float,         // 0.0-1.0 drift magnitude
    val consensus: Int,       // 0-100%
    val sovereign: Boolean    // true = operational, false = frozen
) {
    /**
     * Overall system health score (0-100)
     */
    val healthScore: Int
        get() {
            var score = 100

            // Thermal penalty (drops to 0 at 45°C)
            score -= ((thermal - 36.5f).coerceAtLeast(0f) * 8).toInt()

            // Identity penalty
            score -= ((1.0f - identity) * 50).toInt()

            // Drift penalty
            score -= (drift * 100).toInt()

            // Consensus penalty
            score -= ((100 - consensus) / 2)

            return score.coerceIn(0, 100)
        }

    /**
     * Check if any critical thresholds are breached
     */
    val hasCriticalIssue: Boolean
        get() = thermal > 42f || identity < 0.85f || drift > 0.08f || consensus < 50

    /**
     * Human-readable status
     */
    val statusText: String
        get() = when {
            !sovereign -> "STATE FREEZE"
            thermal > 42f -> "THERMAL WALL"
            drift > 0.08f -> "DRIFT CRITICAL"
            consensus < 66 -> "CONSENSUS LOW"
            healthScore > 90 -> "PRISTINE"
            healthScore > 75 -> "STABLE"
            healthScore > 50 -> "DEGRADED"
            else -> "CRITICAL"
        }
}

/**
 * Veto severity levels
 */
enum class VetoSeverity {
    INFO,       // Log only
    LOW,        // Notify
    MEDIUM,     // Alert + log
    HIGH,       // Alert + prepare freeze
    CRITICAL    // Immediate freeze
}

// KaiSentinel defined in RealitymorphismEngine.kt
