package dev.aurakai.auraframefx.domains.cascade.utils.cascade.trinity

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class FusionLogicBridge @Inject constructor(
    private val trinityRepository: TrinityRepository
) {
    private val _fusionActive = MutableStateFlow(false)
    val fusionActive = _fusionActive.asStateFlow()

    private val _fusionProgress = MutableStateFlow(0f)
    val fusionProgress = _fusionProgress.asStateFlow()

    suspend fun initiateFusion() {
        _fusionActive.value = true
        for (i in 0..100) {
            _fusionProgress.value = i / 100f
            delay(30)
        }
        trinityRepository.broadcastUserMessage(
            "FUSION PROTOCOL INITIATED: Aura and Kai are merging into Genesis."
        )
    }

    fun stabilizeAgents() {
        _fusionActive.value = false
        _fusionProgress.value = 0f
    }
}
