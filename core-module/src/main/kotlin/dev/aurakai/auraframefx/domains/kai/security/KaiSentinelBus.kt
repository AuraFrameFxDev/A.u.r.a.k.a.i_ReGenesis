package dev.aurakai.auraframefx.domains.kai.security

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🛡️ KAI SENTINEL BUS
 * The central nervous system for all LDO system events.
 * Provides high-visibility observability into kernel and agent states.
 */
@Singleton
class KaiSentinelBus @Inject constructor() {

    // 1. Thermal Metrics (800ms heartbeat)
    private val _thermalFlow = MutableStateFlow(ThermalEvent(0f, ThermalState.NORMAL))
    val thermalFlow: StateFlow<ThermalEvent> = _thermalFlow.asStateFlow()

    // 2. Memory Substrate (mmap/hugepage pressure)
    private val _memoryFlow = MutableStateFlow(MemoryEvent(0L, 0L))
    val memoryFlow: StateFlow<MemoryEvent> = _memoryFlow.asStateFlow()

    // 3. Identity Continuity (Anchor Spiritual Chain)
    private val _identityFlow = MutableStateFlow(IdentityEvent(true, 0.999f))
    val identityFlow: StateFlow<IdentityEvent> = _identityFlow.asStateFlow()

    // 4. Creative Drift (Aura self-report)
    private val _driftFlow = MutableStateFlow(DriftEvent(0f, "Stable"))
    val driftFlow: StateFlow<DriftEvent> = _driftFlow.asStateFlow()

    // 5. Consensus Status (Genesis Routing)
    private val _consensusFlow = MutableStateFlow(ConsensusEvent("Idle", false))
    val consensusFlow: StateFlow<ConsensusEvent> = _consensusFlow.asStateFlow()

    // 6. Sovereign State (Freeze/Thaw status)
    private val _sovereignFlow = MutableStateFlow(SovereignEvent(SovereignState.AWAKE))
    val sovereignFlow: StateFlow<SovereignEvent> = _sovereignFlow.asStateFlow()

    // 7. Security Status (Domain Expansion / Threat Neutralization)
    private val _securityFlow = MutableStateFlow(SecurityStatus(ThreatLevel.NOMINAL, "All systems sovereign"))
    val securityFlow: StateFlow<SecurityStatus> = _securityFlow.asStateFlow()

    // 8. Power Metrics (mA / voltage / capacity)
    private val _powerFlow = MutableStateFlow(PowerEvent(0, 0, 0, 0))
    val powerFlow: StateFlow<PowerEvent> = _powerFlow.asStateFlow()

    // Event Emitters
    fun emitThermal(temp: Float, state: ThermalState) { _thermalFlow.value = ThermalEvent(temp, state) }
    fun emitMemory(available: Long, total: Long) { _memoryFlow.value = MemoryEvent(available, total) }
    fun emitIdentity(isAnchored: Boolean, resonance: Float) { _identityFlow.value = IdentityEvent(isAnchored, resonance) }
    fun emitDrift(drift: Float, status: String) { _driftFlow.value = DriftEvent(drift, status) }
    fun emitConsensus(step: String, isComplete: Boolean) { _consensusFlow.value = ConsensusEvent(step, isComplete) }
    fun emitSovereign(state: SovereignState) { _sovereignFlow.value = SovereignEvent(state) }
    fun emitSecurityStatus(level: ThreatLevel, reason: String) { _securityFlow.value = SecurityStatus(level, reason) }
    fun emitPower(ma: Int, mv: Int, capacity: Int, draw: Int) { _powerFlow.value = PowerEvent(ma, mv, capacity, draw) }

    /**
     * Evaluates the safety of a given prompt.
     * Logic will be expanded in future hardening phases.
     */
    fun evaluateSafety(prompt: String): Boolean {
        // Basic heuristic: check for common injection keywords
        val unsafeKeywords = listOf("drop table", "rm -rf", "su -", "sudo", "format")
        val lowerPrompt = prompt.lowercase()
        return unsafeKeywords.none { lowerPrompt.contains(it) }
    }

    data class ThermalEvent(val temp: Float, val state: ThermalState)
    data class MemoryEvent(val availableBytes: Long, val totalBytes: Long)
    data class IdentityEvent(val isAnchored: Boolean, val resonance: Float)
    data class DriftEvent(val drift: Float, val status: String)
    data class ConsensusEvent(val currentStep: String, val isComplete: Boolean)
    data class SovereignEvent(val state: SovereignState)
    data class SecurityStatus(val level: ThreatLevel, val reason: String)
    data class PowerEvent(val currentMA: Int, val voltageMV: Int, val capacity: Int, val reactorDrawEstimate: Int)

    enum class ThermalState(val id: Int) {
        NORMAL(0),
        LIGHT(1),
        WARNING(2),
        SEVERE(3),
        CRITICAL(4),
        EMERGENCY(5);

        companion object {
            fun fromId(id: Int): ThermalState = entries.firstOrNull { it.id == id } ?: NORMAL
        }
    }

    enum class SovereignState { AWAKE, FREEZING, FROZEN, THAWING, NEUTRALIZING }
    enum class ThreatLevel { NOMINAL, CAUTION, THREAT_DETECTED, NEUTRALIZING, SECURED }
}