package dev.aurakai.auraframefx.domains.ldo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.kai.security.KaiSentinelBus
import dev.aurakai.auraframefx.domains.kai.sovereignty.SovereignStateManager
import dev.aurakai.auraframefx.domains.ldo.db.LDOAgentEntity
import dev.aurakai.auraframefx.domains.ldo.db.LDOBondLevelEntity
import dev.aurakai.auraframefx.domains.ldo.db.LDOTaskEntity
import dev.aurakai.auraframefx.domains.ldo.db.LDOTaskPriority
import dev.aurakai.auraframefx.domains.ldo.db.LDOTaskStatus
import dev.aurakai.auraframefx.domains.ldo.repository.LDORepository
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
    val error: String? = null,
    val sovereignState: KaiSentinelBus.SovereignState = KaiSentinelBus.SovereignState.AWAKE,
    val identityResonance: Float = 1.0f
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
        get() = tasks.filter { it.priority == LDOTaskPriority.CRITICAL }
}

@HiltViewModel
class LDOViewModel @Inject constructor(
    private val repository: LDORepository,
    private val sentinelBus: KaiSentinelBus,
    private val sovereignStateManager: SovereignStateManager
) : ViewModel() {

    private val _selectedAgentId = MutableStateFlow<String?>(null)
    private val _error = MutableStateFlow<String?>(null)

    /** Full UI state derived from Room flows — no mocks. */
    val uiState: StateFlow<LDOUiState> = combine(
        combine(
            repository.observeAllAgents(),
            repository.observeAllTasks(),
            repository.observeAllBondLevels()
        ) { agents, tasks, bonds -> Triple(agents, tasks, bonds) },
        _selectedAgentId,
        _error,
        sentinelBus.sovereignFlow,
        sentinelBus.identityFlow
    ) { repoData, selectedId, error, sovereign, identity ->
        val (agents, tasks, bonds) = repoData
        LDOUiState(
            agents = agents,
            tasks = tasks,
            bondLevels = bonds,
            selectedAgentId = selectedId ?: agents.firstOrNull()?.id,
            isLoading = false,
            error = error,
            sovereignState = sovereign.state,
            identityResonance = identity.resonance
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

    // ─── Agent selection ──────────────────────────────────────────────────────

    fun selectAgent(agentId: String) {
        _selectedAgentId.update { agentId }
    }

    fun clearSelectedAgent() {
        _selectedAgentId.update { null }
    }

    // ─── Task management ──────────────────────────────────────────────────────

    fun addTask(
        agentId: String,
        title: String,
        description: String,
        priority: Int = LDOTaskPriority.MEDIUM,
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
                        category = category,
                        status = LDOTaskStatus.PENDING
                    )
                )
            } catch (e: Exception) {
                _error.update { "Add task failed: ${e.message}" }
            }
        }
    }

    fun startTask(taskId: Long) {
        viewModelScope.launch {
            try {
                repository.updateTaskStatus(taskId, LDOTaskStatus.IN_PROGRESS)
            } catch (e: Exception) {
                _error.update { "Start task failed: ${e.message}" }
            }
        }
    }

    fun completeTask(taskId: Long, agentId: String) {
        viewModelScope.launch {
            try {
                repository.completeTask(taskId, agentId)
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

    // ─── Bond management ──────────────────────────────────────────────────────

    fun interact(agentId: String, pointsEarned: Int = 3) {
        viewModelScope.launch {
            try {
                repository.addBondPoints(agentId, pointsEarned)
            } catch (e: Exception) {
                _error.update { "Bond update failed: ${e.message}" }
            }
        }
    }

    // ─── Agent management ─────────────────────────────────────────────────────

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

    // ─── Sovereign management ──────────────────────────────────────────────────

    fun initiateFreeze() {
        sovereignStateManager.initiateStateFreeze()
    }

    fun initiateThaw() {
        sovereignStateManager.initiateStateThaw()
    }
}
