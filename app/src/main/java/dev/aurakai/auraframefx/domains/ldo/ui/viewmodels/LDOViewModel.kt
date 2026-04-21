package dev.aurakai.auraframefx.domains.ldo.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.ldo.data.LDORepository
import dev.aurakai.auraframefx.domains.ldo.data.entities.LDOAgentEntity
import dev.aurakai.auraframefx.domains.ldo.data.entities.LDOBondLevelEntity
import dev.aurakai.auraframefx.domains.ldo.data.entities.LDOTaskEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LDOUiState(
    val agents: List<LDOAgentEntity> = emptyList(),
    val tasks: List<LDOTaskEntity> = emptyList(),
    val bondLevels: List<LDOBondLevelEntity> = emptyList(),
    val selectedAgentId: String? = null,
    val isLoading: Boolean = true,
    val error: String? = null
) {
    val selectedAgent: LDOAgentEntity?
        get() = agents.find { it.agentId == selectedAgentId }

    val selectedAgentBond: LDOBondLevelEntity?
        get() = bondLevels.find { it.agentId == selectedAgentId }

    val tasksForSelectedAgent: List<LDOTaskEntity>
        get() = if (selectedAgentId != null)
            tasks.filter { it.assignedAgentId == selectedAgentId }
        else tasks
}

@HiltViewModel
class LDOViewModel @Inject constructor(
    private val repository: LDORepository
) : ViewModel() {

    private val _selectedAgentId = MutableStateFlow<String?>(null)
    private val _error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<LDOUiState> = combine(
        repository.observeAllAgents(),
        repository.observeAllTasks(),
        repository.observeAllBondLevels(),
        _selectedAgentId,
        _error
    ) { agents, tasks, bonds, selectedId, error ->
        LDOUiState(
            agents = agents,
            tasks = tasks,
            bondLevels = bonds,
            selectedAgentId = selectedId ?: agents.firstOrNull()?.agentId,
            isLoading = false,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LDOUiState()
    )

    init {
        viewModelScope.launch {
            try {
                repository.seedIfEmpty()
            } catch (e: Exception) {
                _error.update { "Seed failed: ${e.message}" }
            }
        }
    }

    fun selectAgent(agentId: String) {
        _selectedAgentId.update { agentId }
    }

    fun addTask(
        agentId: String,
        title: String,
        description: String,
        priority: Int = 1,
        category: String = "general"
    ) {
        viewModelScope.launch {
            try {
                repository.insertTask(
                    LDOTaskEntity(
                        assignedAgentId = agentId,
                        title = title,
                        description = description,
                        priority = priority,
                        status = "PENDING"
                    )
                )
            } catch (e: Exception) {
                _error.update { "Add task failed: ${e.message}" }
            }
        }
    }

    fun updateStatus(taskId: Long, status: String) {
        viewModelScope.launch {
            try {
                repository.updateTaskStatus(taskId, status)
            } catch (e: Exception) {
                _error.update { "Update failed: ${e.message}" }
            }
        }
    }
}
