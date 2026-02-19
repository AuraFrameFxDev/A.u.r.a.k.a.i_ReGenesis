package dev.aurakai.auraframefx.domains.aura.ui.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.genesis.models.AgentCapabilityCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PartyViewModel @Inject constructor() : ViewModel() {

    private val _selectedAgents =
        MutableStateFlow<Set<AgentCapabilityCategory>>(setOf(AgentCapabilityCategory.CREATIVE, AgentCapabilityCategory.ANALYSIS))
    val selectedAgents = _selectedAgents.asStateFlow()

    private val _synergyLevel = MutableStateFlow(0.85f)
    val synergyLevel = _synergyLevel.asStateFlow()

    fun toggleAgent(agent: AgentCapabilityCategory) {
        val current = _selectedAgents.value
        _selectedAgents.value = if (current.contains(agent)) {
            current - agent
        } else {
            current + agent
        }
    }
}

