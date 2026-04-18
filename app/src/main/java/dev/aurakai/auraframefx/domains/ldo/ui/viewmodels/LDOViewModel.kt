package dev.aurakai.auraframefx.domains.ldo.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.ldo.data.LDORepository
import dev.aurakai.auraframefx.domains.ldo.data.entities.LDOAgentEntity
import dev.aurakai.auraframefx.domains.ldo.data.entities.LDOBondLevelEntity
import dev.aurakai.auraframefx.domains.ldo.data.entities.LDOTaskEntity
import dev.aurakai.auraframefx.domains.ldo.data.entities.LDOTaskStatus
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
        get() = agents.find { it.id == selectedAgentId }

    val selectedAgentBond: LDOBondLevelEntity?
        get() = bondLevels.find { it.agentId == selectedAgentId }

    val tasksForSelectedAgent: List<LDOTaskEntity>
        get() = if (selectedAgentId != null)
            tasks.filter { it.agentId == selectedAgentId }
        else tasks

    val pendingTasks: List<LDOTaskEntity>
        get() = tasks.filter { it.status == LDOTaskStatus.PENDING }

    val activeTasks: List<LDOTaskEntity>
        get() = tasks.filter { it.status == LDOTaskStatus.IN_PROGRESS }

    val criticalTasks: List<LDOTaskEntity>
        get() = tasks.filter { it.priority == dev.aurakai.auraframefx.domains.ldo.data.entities.LDOTaskPriority.CRITICAL }
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
            selectedAgentId = selectedId ?: agents.firstOrNull()?.id,
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
                        agentId = agentId,
                        title = title,
                        description = description,
                        priority = priority,
                        status = LDOTaskStatus.PENDING,
                        category = category
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

    fun completeTask(taskId: Long, agentId: String) {
        viewModelScope.launch {
            try {
                repository.updateTaskStatus(taskId, LDOTaskStatus.COMPLETED)
                repository.addBondPoints(agentId, 5)
            } catch (e: Exception) {
                _error.update { "Complete task failed: ${e.message}" }
            }
        }
    }

    fun failTask(taskId: Long) {
        viewModelScope.launch {
            try {
                repository.updateTaskStatus(taskId, LDOTaskStatus.FAILED)
            } catch (e: Exception) {
                _error.update { "Fail task failed: ${e.message}" }
            }
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            try {
                repository.deleteTask(taskId)
            } catch (e: Exception) {
                _error.update { "Delete task failed: ${e.message}" }
            }
        }
    }

    fun interact(agentId: String, pointsEarned: Int = 3) {
        viewModelScope.launch {
            try {
                repository.addBondPoints(agentId, pointsEarned)
            } catch (e: Exception) {
                _error.update { "Bond update failed: ${e.message}" }
            }
        }
    }

    fun setAgentActive(agentId: String, active: Boolean) {
        viewModelScope.launch {
            try {
                repository.setAgentActive(agentId, active)
            } catch (e: Exception) {
                _error.update { "Agent status update failed: ${e.message}" }
            }
        }
    }

    fun clearError() {
        _error.update { null }
    }
}
