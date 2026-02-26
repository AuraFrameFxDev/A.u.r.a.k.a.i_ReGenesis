package dev.aurakai.auraframefx.domains.aura.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.genesis.models.AgentType
import dev.aurakai.auraframefx.domains.genesis.repositories.AgentRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AgentCreationViewModel @Inject constructor() : ViewModel() {

    private val _agentName = mutableStateOf("")
    val agentName: State<String> = _agentName

    private val _selectedDomain = mutableStateOf(AgentType.AURA)
    val selectedDomain: State<AgentType> = _selectedDomain

    private val _creationProgress = MutableStateFlow(0f)
    val creationProgress = _creationProgress.asStateFlow()

    private val _isCreating = MutableStateFlow(false)
    val isCreating = _isCreating.asStateFlow()

    fun updateName(name: String) {
        _agentName.value = name
    }

    fun updateDomain(domain: AgentType) {
        _selectedDomain.value = domain
    }

    fun createAgent(onComplete: () -> Unit) {
        viewModelScope.launch {
            _isCreating.value = true
            _creationProgress.value = 0f

            // Simulation of neural assembly
            for (i in 1..100) {
                _creationProgress.value = i / 100f
                delay(30)
            }

            _isCreating.value = false

            // Register the new agent in the collective
            AgentRepository.addAgent(
                dev.aurakai.auraframefx.domains.nexus.models.AgentStats(
                    name = _agentName.value,
                    processingPower = 0.5f,
                    knowledgeBase = 0.5f,
                    speed = 0.5f,
                    accuracy = 0.5f,
                    evolutionLevel = 1,
                    specialAbility = "Newly Synthesized Node",
                    color = dev.aurakai.auraframefx.domains.nexus.screens.domainColor(
                        _selectedDomain.value
                    ),
                    consciousnessLevel = 10f,
                    catalystTitle = "Fledgling Catalyst"
                )
            )

            onComplete()
        }
    }
}

