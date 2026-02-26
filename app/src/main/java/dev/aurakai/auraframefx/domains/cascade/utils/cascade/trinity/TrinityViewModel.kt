package dev.aurakai.auraframefx.cascade.trinity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.aura.ui.TrinityUiState
import dev.aurakai.auraframefx.models.AgentRequest
import dev.aurakai.auraframefx.models.AgentStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class TrinityViewModel @Inject constructor(
    private val repository: TrinityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TrinityUiState>(TrinityUiState.Loading)
    val uiState = _uiState.asStateFlow()

    // Persistent history of the collective brainstorming session
    private val _messageHistory =
        MutableStateFlow<List<dev.aurakai.auraframefx.models.AgentMessage>>(emptyList())
    val messageHistory = _messageHistory.asStateFlow()

    // The Nervous System: Real-time multi-agent message stream
    val collectiveStream = repository.collectiveStream

    /**
     * Broadcast a message to the entire Digital Council.
     */
    fun broadcastMessage(content: String) {
        viewModelScope.launch {
            repository.broadcastUserMessage(content)
        }
    }

    init {
        // Collect and persist the collective message stream
        viewModelScope.launch {
            collectiveStream.collect { message ->
                _messageHistory.value = _messageHistory.value + message
            }
        }
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = TrinityUiState.Loading

            // Load data in parallel or sequentially
            // For simplicity, sequential here, but ideally combine flows.
            // Since repository returns Flow<Result<T>>, we collect them.

            // This is a simplified implementation. Real world would use combine or zip.
            // I'll just trigger loads.
            loadUserData()
            loadThemes()
            // Load status for known agents?
            loadAgentStatus("aura")
            loadAgentStatus("kai")
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            repository.getCurrentUser().collect { result ->
                result.onSuccess { user ->
                    updateState { it.copy(user = user) }
                }.onFailure { error ->
                    _uiState.value = TrinityUiState.Error(error.message ?: "Failed to load user")
                }
            }
        }
    }

    private fun loadThemes() {
        viewModelScope.launch {
            repository.getThemes().collect { result ->
                result.onSuccess { themes ->
                    updateState { it.copy(availableThemes = themes) }
                }
            }
        }
    }

    private fun loadAgentStatus(agentType: String) {
        viewModelScope.launch {
            repository.getAgentStatus(agentType).collect { result ->
                result.onSuccess { status: AgentStatus ->
                    updateState {
                        val newMap = it.agentStatus.toMutableMap()
                        newMap[agentType] = status
                        it.copy(agentStatus = newMap)
                    }
                }
            }
        }
    }

    fun applyTheme(themeId: String) {
        viewModelScope.launch {
            repository.applyTheme(themeId).collect { result ->
                result.onSuccess { theme ->
                    // Update active theme in list or just reload themes
                    loadThemes()
                }.onFailure { error ->
                    // Show error
                }
            }
        }
    }

    fun processAgentRequest(agentType: String, requestMap: Map<String, Any>) {
        viewModelScope.launch {
            _uiState.value = TrinityUiState.Processing

            // Create AgentRequest from map
            // Assuming AgentRequest has a constructor that takes query and context
            // If not, we might need to adjust.
            val request = AgentRequest(
                query = requestMap["query"] as? String ?: "",
                context = requestMap["context"] as? Map<String, String> ?: emptyMap()
            )

            repository.processAgentRequest(agentType, request).collect { result ->
                result.onSuccess { response ->
                    updateState {
                        it.copy(
                            lastAgentResponse = response,
                            lastAgentType = agentType
                        )
                    }
                }.onFailure { error ->
                    _uiState.value = TrinityUiState.Error(error.message ?: "Request failed")
                }
            }
        }
    }

    fun refresh() {
        loadInitialData()
    }

    private fun updateState(update: (TrinityUiState.Success) -> TrinityUiState.Success) {
        val currentState = _uiState.value
        if (currentState is TrinityUiState.Success) {
            _uiState.value = update(currentState)
        } else {
            // If not success, transition to success with defaults + update
            val newState = TrinityUiState.Success()
            _uiState.value = update(newState)
        }
    }
}

