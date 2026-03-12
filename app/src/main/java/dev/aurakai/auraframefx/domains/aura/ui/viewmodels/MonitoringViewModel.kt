package dev.aurakai.auraframefx.domains.aura.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.kai.AuraShieldAgent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MonitoringViewModel @Inject constructor(
    private val auraShieldAgent: AuraShieldAgent
) : ViewModel() {

    val securityState = auraShieldAgent.securityContext
    val activeThreats = auraShieldAgent.activeThreats
    val scanHistory = auraShieldAgent.scanHistory

    private val _cpuUsage = MutableStateFlow(45f)
    val cpuUsage = _cpuUsage.asStateFlow()

    private val _ramUsage = MutableStateFlow(1.2f) // in GB
    val ramUsage = _ramUsage.asStateFlow()

    private val _latency = MutableStateFlow(120) // in ms
    val latency = _latency.asStateFlow()

    private val _integrity = MutableStateFlow(0.99f)
    val integrity = _integrity.asStateFlow()

    init {
        // Mock real-time data flow
        viewModelScope.launch {
            while (true) {
                _cpuUsage.value = (40f + Random.nextFloat() * 20f).coerceIn(0f, 100f)
                _ramUsage.value = (1.1f + Random.nextFloat() * 0.4f)
                _latency.value = (100 + Random.nextInt(50))
                _integrity.value = (0.98f + Random.nextFloat() * 0.02f).coerceIn(0f, 1f)
                delay(2000)
            }
        }
    }
}

