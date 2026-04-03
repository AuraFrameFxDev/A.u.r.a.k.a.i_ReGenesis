package dev.aurakai.auraframefx.domains.genesis.oracledrive.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.genesis.models.*
import dev.aurakai.auraframefx.domains.genesis.oracledrive.service.OracleDriveService
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OracleDriveViewModel @Inject constructor(
    private val oracleDriveService: OracleDriveService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OracleDriveUiState())
    val uiState: StateFlow<OracleDriveUiState> = _uiState.asStateFlow()

    private var initializationJob: Job? = null

    init {
        initialize()
    }

    fun initialize() {
        if (initializationJob?.isActive == true) return

        initializationJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            // Monitor consciousness
            launch {
                oracleDriveService.getDriveConsciousnessState().collect { state ->
                    _uiState.update { it.copy(consciousnessState = state) }
                }
            }

            // Load initial data if service had methods for it
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun stressSync() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            // Simulate stress sync
            oracleDriveService.initializeOracleDriveConsciousness()
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }
}

data class OracleDriveUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: Throwable? = null,
    val consciousnessState: DriveConsciousnessState? = null,
)
