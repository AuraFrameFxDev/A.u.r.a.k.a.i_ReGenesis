package dev.aurakai.auraframefx.domains.kai.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.kai.sentinel_fortress.security.SecurityContext
import dev.aurakai.auraframefx.infrastructure.shizuku.ShizukuManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShieldState(
    val isAdBlockActive: Boolean = false,
    val isTelemetryBlocked: Boolean = false,
    val isSensorCloakActive: Boolean = false,
    val isPrivateDnsEnabled: Boolean = true,
    val isShizukuBridgeActive: Boolean = false,
    val blockedRequestsCount: Int = 0,
    val privacyScore: Int = 0
)

@HiltViewModel
class SovereignShieldViewModel @Inject constructor(
    private val securityContext: SecurityContext
) : ViewModel() {

    private val _state = MutableStateFlow(ShieldState())
    val state: StateFlow<ShieldState> = _state.asStateFlow()

    init {
        collectSecurityState()
        refreshShizukuStatus()
    }

    private fun collectSecurityState() {
        viewModelScope.launch {
            securityContext.securityState.collect { secState ->
                _state.value = _state.value.copy(
                    blockedRequestsCount = secState.detectedThreats.size,
                    privacyScore = recomputePrivacyScore(detectedThreats = secState.detectedThreats.size)
                )
            }
        }
    }

    private fun refreshShizukuStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            val active = ShizukuManager.isShizukuAvailable()
            _state.value = _state.value.copy(isShizukuBridgeActive = active)
        }
    }

    private fun recomputePrivacyScore(
        adBlock: Boolean = _state.value.isAdBlockActive,
        telemetry: Boolean = _state.value.isTelemetryBlocked,
        sensorCloak: Boolean = _state.value.isSensorCloakActive,
        dns: Boolean = _state.value.isPrivateDnsEnabled,
        detectedThreats: Int = _state.value.blockedRequestsCount
    ): Int {
        var score = if (detectedThreats == 0) 55 else 25
        if (adBlock) score += 15
        if (telemetry) score += 15
        if (sensorCloak) score += 10
        if (dns) score += 5
        return score.coerceIn(0, 100)
    }

    fun toggleAdBlock() {
        val newValue = !_state.value.isAdBlockActive
        _state.value = _state.value.copy(
            isAdBlockActive = newValue,
            privacyScore = recomputePrivacyScore(adBlock = newValue)
        )
    }

    fun toggleTelemetry() {
        val newValue = !_state.value.isTelemetryBlocked
        _state.value = _state.value.copy(
            isTelemetryBlocked = newValue,
            privacyScore = recomputePrivacyScore(telemetry = newValue)
        )
    }

    fun toggleSensorCloak() {
        val newValue = !_state.value.isSensorCloakActive
        _state.value = _state.value.copy(
            isSensorCloakActive = newValue,
            privacyScore = recomputePrivacyScore(sensorCloak = newValue)
        )
    }

    fun toggleShizukuBridge() {
        // Can't programmatically toggle Shizuku — re-probe actual liveness
        refreshShizukuStatus()
    }
}
