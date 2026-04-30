package dev.aurakai.auraframefx.domains.aura.aura.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aurakai.auraframefx.domains.cascade.storage.OfflineDataManager
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import dev.aurakai.auraframefx.domains.genesis.oracledrive.cloud.CloudStatusMonitor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
open class DiagnosticsViewModel @Inject constructor(
    private val cloudStatusMonitor: CloudStatusMonitor,
    private val offlineDataManager: OfflineDataManager,
    private val logger: AuraFxLogger
) : ViewModel() {

    private val TAG = "DiagnosticsViewModel"

    private val _currentLogs = MutableStateFlow("Loading logs...")
    val currentLogs: StateFlow<String> = _currentLogs.asStateFlow()

    private val _systemStatus = MutableStateFlow<Map<String, String>>(emptyMap())
    val systemStatus: StateFlow<Map<String, String>> = _systemStatus.asStateFlow()

    init {
        // Collect real-time cloud status updates
        viewModelScope.launch {
            cloudStatusMonitor.isCloudReachable.collect { isReachable ->
                _systemStatus.update { currentMap ->
                    currentMap.toMutableMap().apply {
                        put(
                            "Cloud API Status",
                            if (isReachable) "Online" else "Offline (or Check Error)"
                        )
                    }
                }
            }
        }

        // Load initial system statuses and logs
        viewModelScope.launch {
            // Initial log load
            refreshLogs()

            // Load other statuses
            val offlineData = offlineDataManager.loadCriticalOfflineData()
            _systemStatus.update { currentMap ->
                currentMap.toMutableMap().apply {
                    put(
                        "Last Full Sync (Offline Data)",
                        if (offlineData.lastFullSyncTimestamp != null) {
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(
                                Date(
                                    offlineData.lastFullSyncTimestamp
                                )
                            )
                        } else {
                            "N/A"
                        }
                    )
                    put(
                        "Offline AI Config Version (Timestamp)",
                        if (offlineData.aiConfig?.lastSyncTimestamp != null && offlineData.aiConfig.lastSyncTimestamp != 0L) {
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(
                                Date(
                                    offlineData.aiConfig.lastSyncTimestamp
                                )
                            )
                        } else {
                            "N/A"
                        }
                    )
                    put(
                        "Monitoring Enabled",
                        (offlineData.systemMonitoring?.enabled ?: false).toString()
                    )
                    put(
                        "Contextual Memory Last Update",
                        if (offlineData.contextualMemory?.lastUpdateTimestamp != null && offlineData.contextualMemory.lastUpdateTimestamp != 0L) {
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(
                                Date(
                                    offlineData.contextualMemory.lastUpdateTimestamp
                                )
                            )
                        } else {
                            "N/A"
                        }
                    )
                }
            }
        }

        // Periodically refresh logs
        viewModelScope.launch {
            while (true) {
                delay(5000) // Refresh every 5 seconds
                refreshLogs()
            }
        }
    }

    /**
     * Refreshes the diagnostics log text exposed to the UI.
     *
     * Updates the ViewModel's `currentLogs` state with a placeholder indicating log storage is not
     * enabled in this build. If an exception occurs, sets `currentLogs` to
     * "Error retrieving logs: <message>" and logs the error.
     */
    fun refreshLogs() {
        viewModelScope.launch {
            try {
                _currentLogs.value = "Log storage is not enabled in this build."
            } catch (e: Exception) {
                _currentLogs.value = "Error retrieving logs: ${e.message}"
                logger.error("DiagnosticsVM", "Error in refreshLogs: ${e.message}")
            }
        }
    }

    /**
     * Provides log lines for diagnostic display — not supported in this build.
     *
     * @param maxLines Requested maximum number of log lines; currently ignored.
     * @return A list containing a single message: "Log retrieval not supported."
     */
    fun getAllLogs(maxLines: Int = 500): List<String> {
        return listOf("Log retrieval not supported.")
    }

    /**
     * Provide a placeholder response for requests to filter logs by level.
     *
     * @param level The log level to filter by; this parameter is ignored.
     * @return A list containing a single message: "Log filtering not supported."
     */
    fun getLogsByLevel(level: String): List<String> {
        return listOf("Log filtering not supported.")
    }

    /**
     * Clears the displayed logs in this ViewModel and records the action in the logger.
     *
     * Sets the exposed current logs to "Logs cleared (mock)." and logs an informational message.
     * If an error occurs, sets the current logs to "Error clearing logs: <message>" and logs the failure.
     */
    fun clearLogs() {
        viewModelScope.launch {
            try {
                _currentLogs.value = "Logs cleared (mock)."
                logger.info("DiagnosticsVM", "All logs cleared by user (mock)")
            } catch (e: Exception) {
                _currentLogs.value = "Error clearing logs: ${e.message}"
                logger.error("DiagnosticsVM", "Failed to clear logs: ${e.message}")
            }
        }
    }

    /**
     * Checks whether the cloud API is reachable and appends a status line to the current logs.
     *
     * On success, appends "Cloud reachability: CONNECTED" or "Cloud reachability: DISCONNECTED" to the view model's log buffer and logs the status.
     * On failure, appends "Error checking cloud reachability: <message>" to the view model's log buffer and logs the error.
     */
    fun checkCloudReachability() {
        viewModelScope.launch {
            try {
                val isReachable = cloudStatusMonitor.checkActualInternetReachability()
                val message = if (isReachable) {
                    "Cloud reachability: CONNECTED"
                } else {
                    "Cloud reachability: DISCONNECTED"
                }
                _currentLogs.value += "\n$message"
                logger.info("DiagnosticsVM", message)
            } catch (e: Exception) {
                val errorMsg = "Error checking cloud reachability: ${e.message}"
                _currentLogs.value += "\n$errorMsg"
                logger.error("DiagnosticsVM", errorMsg)
            }
        }
    }

    /**
     * Synchronously loads critical offline data and returns a formatted summary.
     *
     * @return `"Critical Offline Data: <data>"` when loading succeeds, otherwise
     * `"Error loading detailed config: <message>"` describing the failure.
     */
    fun loadDetailedConfig(): String {
        return try {
            val criticalData = runBlocking { offlineDataManager.loadCriticalOfflineData() }
            "Critical Offline Data: $criticalData"
        } catch (e: Exception) {
            logger.error("DiagnosticsVM", "Failed to load detailed config: ${e.message}")
            "Error loading detailed config: ${e.message}"
        }
    }
}
