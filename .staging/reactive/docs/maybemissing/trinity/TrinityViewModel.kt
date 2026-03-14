package dev.aurakai.auraframefx.ui.trinity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.repository.TrinityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrinityViewModel @Inject constructor(
    private val repository: TrinityRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<TrinityUiState>(TrinityUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = TrinityUiState.Loading

            // Load initial data in parallel
            launch { loadUserData() }
            launch { loadAgentStatus() }
            launch { loadThemes() }
        }
    }

    private suspend fun loadUserData() {
        repository.getCurrentUser()
            .catch { e ->
                _uiState.value = TrinityUiState.Error("Failed to load user data: ${e.message}")
            }
            .collect { result ->
                result.onSuccess { user ->
                    _uiState.value = (_uiState.value as? TrinityUiState.Success)?.copy(
                        user = user
                    ) ?: TrinityUiState.Success(user = user)
                }
                result.onFailure { e ->
                    _uiState.value = TrinityUiState.Error("Failed to load user data: ${e.message}")
                }
            }
    }

    private suspend fun loadAgentStatus() {
        // Load status for each agent in the Trinity system
        listOf("genesis", "aura", "kai").forEach { agentType ->
            repository.getAgentStatus(agentType)
                .catch { e ->
                    _uiState.value =
                        TrinityUiState.Error("Failed to load $agentType status: ${e.message}")
                }
                .collect { result ->
                    result.onSuccess { status ->
                        _uiState.value = (_uiState.value as? TrinityUiState.Success)?.copy(
                            agentStatus = (_uiState.value as? TrinityUiState.Success)
                                ?.agentStatus?.plus(agentType to status)
                                ?: mapOf(agentType to status)
                        ) ?: TrinityUiState.Success(agentStatus = mapOf(agentType to status))
                    }
                }
        }
    }

    private suspend fun loadThemes() {
        repository.getThemes()
            .catch { e ->
                _uiState.value = TrinityUiState.Error("Failed to load themes: ${e.message}")
            }
            .collect { result ->
                result.onSuccess { themes ->
                    _uiState.value = (_uiState.value as? TrinityUiState.Success)?.copy(
                        availableThemes = themes
                    ) ?: TrinityUiState.Success(availableThemes = themes)
                }
            }
    }

    fun applyTheme(themeId: String) {
        viewModelScope.launch {
            repository.applyTheme(themeId)
                .catch { e ->
                    _uiState.value = TrinityUiState.Error("Failed to apply theme: ${e.message}")
                }
                .collect { result ->
                    result.onSuccess {
                        // Refresh themes after applying a new one
                        loadThemes()
                    }
                }
        }
    }

    fun processAgentRequest(agentType: String, request: Map<String, Any>) {
        viewModelScope.launch {
            _uiState.value = TrinityUiState.Processing

            repository.processAgentRequest(agentType, AgentRequest(request))
                .catch { e ->
                    _uiState.value = TrinityUiState.Error("Agent request failed: ${e.message}")
                }
                .collect { result ->
                    result.onSuccess { response ->
                        _uiState.value = (_uiState.value as? TrinityUiState.Success)?.copy(
                            lastAgentResponse = response,
                            lastAgentType = agentType
                        ) ?: TrinityUiState.Success(
                            lastAgentResponse = response,
                            lastAgentType = agentType
                        )
                    }
                    result.onFailure { e ->
                        _uiState.value = TrinityUiState.Error("Agent request failed: ${e.message}")
                    }
                }
        }
    }

    fun refresh() {
        loadInitialData()
    }
}
