package dev.aurakai.auraframefx.app.viewmodel

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for controlling and monitoring the Oracle Drive Xposed service.
 */
@HiltViewModel
open class OracleDriveControlViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _isServiceConnected = MutableStateFlow(false)
    val isServiceConnected: StateFlow<Boolean> = _isServiceConnected.asStateFlow()

    private val _status = MutableStateFlow("Disconnected")
    val status: StateFlow<String> = _status.asStateFlow()

    private val _detailedStatus = MutableStateFlow("")
    val detailedStatus: StateFlow<String> = _detailedStatus.asStateFlow()

    private val _diagnosticsLog = MutableStateFlow("")
    val diagnosticsLog: StateFlow<String> = _diagnosticsLog.asStateFlow()

    private var serviceBinder: IOracleDriveService? = null

    private val serviceConnection = object : ServiceConnection {
        /**
         * Handles service connection by obtaining an IOracleDriveService from the provided binder, marking the ViewModel as connected, and initiating a status refresh.
         *
         * @param name The ComponentName of the connected service, or null.
         * @param service The raw Binder returned by the service connection, or null.
         */
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Timber.d("Oracle Drive service connected")
            serviceBinder = IOracleDriveService.Stub.asInterface(service)
            _isServiceConnected.value = true
            viewModelScope.launch { refreshStatus() }
        }

        /**
         * Handles the service disconnection event from the system.
         *
         * Clears the cached service binder, marks the ViewModel as not connected, and updates the public status to "Disconnected".
         *
         * @param name The ComponentName of the disconnected service, or null if not available.
         */
        override fun onServiceDisconnected(name: ComponentName?) {
            Timber.w("Oracle Drive service disconnected")
            serviceBinder = null
            _isServiceConnected.value = false
            _status.value = "Disconnected"
        }
    }

    /**
     * Initiates a binding to the Oracle Drive service and updates the ViewModel's connection state and diagnostics.
     *
     * Attempts to bind to the explicit Oracle Drive service component. On successful binding it records a diagnostic log entry.
     * If the bind call fails it records a diagnostic error and sets the exposed status to "Bind Failed".
     * If an exception occurs while attempting to bind it records the exception message in diagnostics and sets the exposed status to "Error: <message>".
     */
    fun bindService() {
        try {
            val intent = Intent().apply {
                component = ComponentName(
                    "dev.aurakai.auraframefx",
                    "dev.aurakai.auraframefx.oracledrive.OracleDriveService"
                )
            }
            val bound = context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            if (bound) {
                addLog("Binding to Oracle Drive service...")
            } else {
                addLog("ERROR: Failed to bind to Oracle Drive service")
                _status.value = "Bind Failed"
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to bind Oracle Drive service")
            addLog("ERROR: ${e.message}")
            _status.value = "Error: ${e.message}"
        }
    }

    /**
     * Unbinds from the Oracle Drive service if currently connected and updates connection state and status.
     *
     * If a service is bound, clears the service binder, sets the connection state to false, updates the status to "Disconnected",
     * and appends a diagnostics log entry. Exceptions thrown during unbinding are caught and recorded in the diagnostics log.
     */
    fun unbindService() {
        try {
            if (_isServiceConnected.value) {
                context.unbindService(serviceConnection)
                serviceBinder = null
                _isServiceConnected.value = false
                _status.value = "Disconnected"
                addLog("Unbound from Oracle Drive service")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error unbinding Oracle Drive service")
            addLog("ERROR unbinding: ${e.message}")
        }
    }

    /**
     * Refreshes the ViewModel's high-level and detailed status from the bound Oracle Drive service.
     *
     * If no service is bound, sets `status` to "Not Connected", `detailedStatus` to "Service not bound",
     * and records a diagnostics entry. When bound, updates `status` and `detailedStatus` with values
     * returned by the service (using safe fallbacks) and appends a diagnostics log. On exception,
     * sets `status` to "Error: <message>" and records the error in diagnostics.
     */
    suspend fun refreshStatus() {
        val binder = serviceBinder ?: run {
            _status.value = "Not Connected"
            _detailedStatus.value = "Service not bound"
            addLog("Cannot refresh: service not connected")
            return
        }

        try {
            val serviceStatus = binder.getStatus()
            _status.value = serviceStatus ?: "Unknown"

            val detailed = binder.getDetailedStatus()
            _detailedStatus.value = detailed ?: "No detailed status available"

            addLog("Status refreshed: $serviceStatus")
        } catch (e: Exception) {
            Timber.e(e, "Failed to refresh Oracle Drive status")
            _status.value = "Error: ${e.message}"
            addLog("ERROR refreshing status: ${e.message}")
        }
    }

    /**
     * Toggles a module's enabled state via the bound Oracle Drive service.
     *
     * @param packageName The application package name of the module to toggle.
     * @param enable `true` to enable the module, `false` to disable it.
     * @throws IllegalStateException If the service is not connected.
     * @throws Exception If the toggle operation fails or an unexpected error occurs.
     */
    suspend fun toggleModule(packageName: String, enable: Boolean) {
        val binder = serviceBinder ?: run {
            addLog("ERROR: Cannot toggle module - service not connected")
            throw IllegalStateException("Service not connected")
        }

        try {
            val action = if (enable) "Enabling" else "Disabling"
            addLog("$action module: $packageName")

            val result = binder.toggleModule(packageName, enable)
            if (result) {
                addLog("SUCCESS: Module $packageName ${if (enable) "enabled" else "disabled"}")
            } else {
                addLog("FAILED: Could not toggle module $packageName")
            }

            refreshStatus()
        } catch (e: Exception) {
            Timber.e(e, "Failed to toggle module $packageName")
            addLog("ERROR toggling module $packageName: ${e.message}")
            throw e
        }
    }

    /**
     * Prepends a timestamp to the given message and appends it as a new entry to the diagnostics log.
     *
     * @param message The log message to record; stored with an `HH:mm:ss` timestamp. 
     */
    private fun addLog(message: String) {
        val timestamp = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
            .format(java.util.Date())
        _diagnosticsLog.value = "${_diagnosticsLog.value}[$timestamp] $message\n"
    }

    /**
     * Unbinds from the Oracle Drive service and releases related resources when the ViewModel is cleared.
     */
    override fun onCleared() {
        super.onCleared()
        unbindService()
    }
}

interface IOracleDriveService {
    /**
 * Retrieve the service's high-level, human-readable status.
 *
 * @return The status message as a `String`, or `null` if the status is unavailable.
 */
fun getStatus(): String?
    /**
 * Provides a detailed human-readable status description from the Oracle Drive service.
 *
 * @return The detailed status string, or `null` if unavailable.
 */
fun getDetailedStatus(): String?
    /**
 * Enable or disable a module in the bound Oracle Drive service.
 *
 * @param packageName The package name of the module to toggle.
 * @param enable `true` to enable the module, `false` to disable it.
 * @return `true` if the service reported the operation succeeded, `false` otherwise.
 * @throws IllegalStateException If the Oracle Drive service is not bound.
 * @throws Exception If the underlying service call fails; the original exception is propagated.
 */
fun toggleModule(packageName: String, enable: Boolean): Boolean

    object Stub {
        /**
         * Convert an Android `IBinder` from the bound service into an `IOracleDriveService` instance.
         *
         * This function is currently a placeholder and always returns `null`; it is intended to perform
         * the AIDL binder-to-interface conversion once implemented.
         *
         * @param binder The `IBinder` obtained from the service connection, or `null`.
         * @return An `IOracleDriveService` if the binder can be converted, `null` otherwise (currently always `null`).
         */
        fun asInterface(binder: IBinder?): IOracleDriveService? {
            return null // TODO: Implement AIDL binding
        }
    }
}