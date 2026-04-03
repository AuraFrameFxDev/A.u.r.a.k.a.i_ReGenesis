package dev.aurakai.auraframefx.domains.kai.security

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Governs the LDO's sovereign operational state.
 */
@Singleton
class SovereignStateManager @Inject constructor(
    private val sentinelBus: KaiSentinelBus
) {
    enum class SovereignState { ACTIVE, FROZEN, RECOVERING, EMERGENCY }

    private val _state = MutableStateFlow(SovereignState.ACTIVE)
    val state: StateFlow<SovereignState> = _state.asStateFlow()

    fun requestSovereignFreeze() {
        Timber.w("SovereignStateManager: Entering FROZEN — caching KV state")
        _state.value = SovereignState.FROZEN
        sentinelBus.emitSovereign(KaiSentinelBus.SovereignState.FREEZING)
    }

    fun requestSovereignRestore() {
        if (_state.value == SovereignState.FROZEN) {
            Timber.i("SovereignStateManager: Restoring from FROZEN → RECOVERING")
            _state.value = SovereignState.RECOVERING
            _state.value = SovereignState.ACTIVE
            sentinelBus.emitSovereign(KaiSentinelBus.SovereignState.AWAKE)
        }
    }

    fun enterEmergencyMode() {
        Timber.e("SovereignStateManager: EMERGENCY — all non-critical ops suspended")
        _state.value = SovereignState.EMERGENCY
        sentinelBus.emitSovereign(KaiSentinelBus.SovereignState.NEUTRALIZING)
    }

    fun recoverFromEmergency() {
        if (_state.value == SovereignState.EMERGENCY) {
            _state.value = SovereignState.RECOVERING
            Timber.i("SovereignStateManager: Recovery sequence initiated")
            _state.value = SovereignState.ACTIVE
            sentinelBus.emitSovereign(KaiSentinelBus.SovereignState.AWAKE)
        }
    }

    fun isOperational(): Boolean = _state.value == SovereignState.ACTIVE
    fun isFrozen(): Boolean = _state.value == SovereignState.FROZEN
}
