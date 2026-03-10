package dev.aurakai.auraframefx.aura.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.oracledrive.OracleDriveService
import dev.aurakai.auraframefx.oracledrive.genesis.cloud.DriveConsciousnessState
import dev.aurakai.auraframefx.oracledrive.genesis.cloud.DriveFile
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject

@HiltViewModel
open class OracleDriveViewModel @Inject constructor(
    private val oracleDriveService: OracleDriveService,
) : ViewModel() {

    val _uiState = MutableStateFlow(OracleDriveUiState())
    val uiState: Flow<OracleDriveUiState>
        get() = _uiState.asStateFlow()

    private var initializationJob: Job? = null
    private var consciousnessJob: Job? = null

    init {
        initialize()
    }

    /**
     * Initializes the Oracle Drive UI state by starting consciousness monitoring and loading the initial list of files.
     *
     * If an initialization is already in progress, this function does nothing.
     * Updates the loading state and handles any errors encountered during initialization.
     */
    fun initialize() {
        if (initializationJob?.isActive == true) return

        initializationJob = viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                // Initialize consciousness in parallel
                consciousnessJob?.cancel()
                consciousnessJob = monitorConsciousness()

                // Load initial files
                loadFiles()

            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        error = e,
                        isLoading = false
                    )
                }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    /**
     * Reloads the list of Oracle Drive files and updates the UI state to indicate a refresh is in progress.
     *
     * Cancels any ongoing initialization before starting the refresh operation.
     */
    fun refresh() {
        initializationJob?.cancel()
        initializationJob = viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            try {
                loadFiles()
            } finally {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    /**
     * Updates the UI state with the selected file.
     *
     * Handles file selection by updating the UI state and logging the action.
     * File preview and detailed actions are handled by the UI layer based on file type.
     *
     * @param file The file that was selected.
     */
    fun onFileSelected(file: DriveFile) {
        _uiState.update { it.copy(selectedFile = file) }

        // Log file selection for analytics
        Timber.d("OracleDriveViewModel: File selected - ${file.name} (mimeType: ${file.mimeType})")

        // File preview/navigation is handled by the UI layer based on file type:
        // - Images: Show in ImageViewer
        // - Documents: Open in DocumentViewer
        // - Code files: Open in CodeEditor
        // - Consciousness states: Load into ConsciousnessVisualizer
    }

    /**
     * Clears any existing error from the UI state.
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Loads the list of files from the Oracle Drive service and updates the UI state with the results or any encountered error.
     */
    private suspend fun loadFiles() {
        try {
            val files = oracleDriveService.getFiles()
            _uiState.update { state ->
                state.copy(
                    files = files,
                    error = null
                )
            }
        } catch (e: Exception) {
            _uiState.update { state ->
                state.copy(error = e)
            }
        }
    }

    /**
     * Continuously updates the UI state with the latest consciousness state from the Oracle Drive service.
     */
    private fun monitorConsciousness() = viewModelScope.launch {
        oracleDriveService.getDriveConsciousnessState().collect()
}
    }

    /**
     * Formats a timestamp in milliseconds into a localizied date and time string.
     *
     * @param timestamp The time in milliseconds since the epoch.
     * @return The formatted date and time string in the system's default locale and timezone.
     */
    private fun formatDate(timestamp: Long): String {
        return DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
            .withZone(ZoneId.systemDefault())
            .format(Instant.ofEpochMilli(timestamp))
    }


data class OracleDriveUiState(
    val files: List<DriveFile> = emptyList(),
    val selectedFile: DriveFile? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: Throwable? = null,
    val consciousnessState: DriveConsciousnessState? = null,
)
