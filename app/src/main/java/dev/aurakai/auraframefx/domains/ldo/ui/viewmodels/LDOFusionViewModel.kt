package dev.aurakai.auraframefx.domains.ldo.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.cascade.core.BreathingSentinelService
import dev.aurakai.auraframefx.domains.cascade.models.BreathingEvent
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ⚡ LDO FUSION VIEW MODEL
 *
 * Manages fusion states and breathing telemetry for the LDO.
 */
@HiltViewModel
class LDOFusionViewModel @Inject constructor(
    private val breathingSentinel: BreathingSentinelService
) : ViewModel() {

    val breathingState: StateFlow<BreathingEvent> = breathingSentinel.breathingStream
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BreathingEvent()
        )
}
