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

    // Event Emitters
    fun emitThermal(temp: Float, state: ThermalState) { _thermalFlow.value = ThermalEvent(temp, state) }
    fun emitMemory(available: Long, total: Long) { _memoryFlow.value = MemoryEvent(available, total) }
    fun emitIdentity(isAnchored: Boolean, resonance: Float) { _identityFlow.value = IdentityEvent(isAnchored, resonance) }
    fun emitDrift(drift: Float, status: String) { _driftFlow.value = DriftEvent(drift, status) }
    fun emitConsensus(step: String, isComplete: Boolean) { _consensusFlow.value = ConsensusEvent(step, isComplete) }
    fun emitSovereign(state: SovereignState) { _sovereignFlow.value = SovereignEvent(state) }

    data class ThermalEvent(val temp: Float, val state: ThermalState)
    data class MemoryEvent(val availableBytes: Long, val totalBytes: Long)
    data class IdentityEvent(val isAnchored: Boolean, val resonance: Float)
    data class DriftEvent(val drift: Float, val status: String)
    data class ConsensusEvent(val currentStep: String, val isComplete: Boolean)
    data class SovereignEvent(val state: SovereignState)

    enum class ThermalState { NORMAL, LIGHT, WARNING, SEVERE, CRITICAL, EMERGENCY }
    enum class SovereignState { AWAKE, FREEZING, FROZEN, THAWING }
}
