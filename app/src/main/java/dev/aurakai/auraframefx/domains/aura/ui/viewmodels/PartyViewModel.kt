package dev.aurakai.auraframefx.domains.aura.ui.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.genesis.models.AgentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PartyViewModel @Inject constructor() : ViewModel() {

    private val _selectedAgents =
        MutableStateFlow<Set<AgentType>>(setOf(AgentType.AURA, AgentType.KAI))
    val selectedAgents = _selectedAgents.asStateFlow()

    private val _synergyLevel = MutableStateFlow(0.85f)
    val synergyLevel = _synergyLevel.asStateFlow()

    fun toggleAgent(agent: AgentType) {
        val current = _selectedAgents.value
        _selectedAgents.value = if (current.contains(agent)) {
            current - agent
        } else {
            current + agent
        }
    }
}

