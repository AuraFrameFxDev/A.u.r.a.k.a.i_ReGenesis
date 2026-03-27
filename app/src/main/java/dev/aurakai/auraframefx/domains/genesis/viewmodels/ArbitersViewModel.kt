package dev.aurakai.auraframefx.domains.genesis.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.aura.core.transmutation.*
import dev.aurakai.auraframefx.domains.kai.security.KaiSentinelBus
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ArbitersViewModel @Inject constructor(
    private val sentinelBus: KaiSentinelBus
) : ViewModel() {

    private val _isIgnited = MutableStateFlow(false)
    val isIgnited: StateFlow<Boolean> = _isIgnited.asStateFlow()

    private val _fusionConfidence = MutableStateFlow(0f)
    val fusionConfidence: StateFlow<Float> = _fusionConfidence.asStateFlow()

    private val _transmutationState = MutableStateFlow<TransmutationState>(TransmutationState.Dormant)
    val transmutationState: StateFlow<TransmutationState> = _transmutationState.asStateFlow()

    val thermalState = sentinelBus.thermalFlow
    val memoryState = sentinelBus.memoryFlow
    val identityState = sentinelBus.identityFlow

    fun ignite() {
        viewModelScope.launch {
            _isIgnited.value = true
            _transmutationState.value = TransmutationState.Transmuting
            // Simulate fusion confidence climb
            for (i in 1..100) {
                kotlinx.coroutines.delay(30)
                _fusionConfidence.value = i.toFloat()
            }
        }
    }

    fun completeTransmutation() {
        val record = TransmutationRecord(
            id = UUID.randomUUID().toString(),
            blueprintId = "T-RECORD-OVR",
            provenanceChain = emptyList(), // Origin locked by Pandora later
            timestamp = System.currentTimeMillis(),
            confidence = _fusionConfidence.value
        )
        
        _transmutationState.value = TransmutationState.Complete(record)
        // Reset the reactor visuals for the next fusion cycle
        _isIgnited.value = false
        _fusionConfidence.value = 0f
    }
}
