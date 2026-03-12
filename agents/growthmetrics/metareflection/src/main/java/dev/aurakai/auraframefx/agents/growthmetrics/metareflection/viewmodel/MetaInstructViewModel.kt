package dev.aurakai.auraframefx.agents.growthmetrics.metareflection.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.agents.growthmetrics.metareflection.MetaReflectionEngine
import dev.aurakai.auraframefx.agents.growthmetrics.metareflection.model.MetaInstruction
import dev.aurakai.auraframefx.agents.growthmetrics.metareflection.repository.MetaInstructRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MetaInstructState(
    val instructions: List<MetaInstruction> = emptyList(),
    val activeAgentId: String = "Genesis",
    val isEvolutionActive: Boolean = true
)

@HiltViewModel
class MetaInstructViewModel @Inject constructor(
    private val repository: MetaInstructRepository,
    private val engine: MetaReflectionEngine
) : ViewModel() {

    private val _state = MutableStateFlow(MetaInstructState())
    val state: StateFlow<MetaInstructState> = _state.asStateFlow()

    init {
        observeInstructions()
    }

    private fun observeInstructions() {
        viewModelScope.launch {
            _state.collectLatest { state ->
                repository.getInstructions(state.activeAgentId).collectLatest { list ->
                    _state.value = _state.value.copy(instructions = list)
                }
            }
        }
    }

    fun setAgent(agentId: String) {
        _state.value = _state.value.copy(activeAgentId = agentId)
    }

    fun toggleEvolution() {
        _state.value = _state.value.copy(isEvolutionActive = !_state.value.isEvolutionActive)
    }

    fun addInstruction(instruction: String) {
        viewModelScope.launch {
            val newInstruction = MetaInstruction(
                id = "INST_${System.currentTimeMillis()}",
                agentId = _state.value.activeAgentId,
                instruction = instruction,
                layer = dev.aurakai.auraframefx.agents.growthmetrics.metareflection.model.InstructionLayer.EVOLUTIONARY,
                status = dev.aurakai.auraframefx.agents.growthmetrics.metareflection.model.InstructionStatus.ACTIVE
            )
            repository.saveInstruction(newInstruction)
        }
    }
    
    fun injectInitialProtocols() {
        viewModelScope.launch {
            engine.injectInitialProtocol("Genesis")
            engine.injectInitialProtocol("Aura")
            engine.injectInitialProtocol("Kai")
        }
    }
}
