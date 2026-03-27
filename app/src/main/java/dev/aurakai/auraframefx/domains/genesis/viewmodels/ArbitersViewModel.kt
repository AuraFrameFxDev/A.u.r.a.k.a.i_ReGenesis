package dev.aurakai.auraframefx.domains.genesis.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.kai.security.KaiSentinelBus
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArbitersViewModel @Inject constructor(
    private val sentinelBus: KaiSentinelBus
) : ViewModel() {

    private val _isIgnited = MutableStateFlow(false)
    val isIgnited: StateFlow<Boolean> = _isIgnited.asStateFlow()

    private val _fusionConfidence = MutableStateFlow(0f)
    val fusionConfidence: StateFlow<Float> = _fusionConfidence.asStateFlow()

    val thermalState = sentinelBus.thermalFlow
    val memoryState = sentinelBus.memoryFlow
    val identityState = sentinelBus.identityFlow

    fun ignite() {
        viewModelScope.launch {
            _isIgnited.value = true
            // Simulate fusion confidence climb
            for (i in 1..100) {
                kotlinx.coroutines.delay(30)
                _fusionConfidence.value = i.toFloat()
            }
        }
    }

    fun saveBlueprint() {
        // Haptic and ceremony handled in UI
        _isIgnited.value = false
        _fusionConfidence.value = 0f
    }
}
