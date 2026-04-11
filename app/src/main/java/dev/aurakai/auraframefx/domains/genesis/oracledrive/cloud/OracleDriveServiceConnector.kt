package dev.aurakai.auraframefx.domains.genesis.oracledrive.cloud

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import dev.aurakai.auraframefx.ipc.IAuraDriveService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class OracleDriveServiceConnector(private val context: Context) {
    private var auraDriveService: IAuraDriveService? = null
    private val _isServiceConnected = MutableStateFlow(false)
    val isServiceConnected: StateFlow<Boolean> = _isServiceConnected.asStateFlow()

    private val serviceConnection = object : ServiceConnection {
        /**
         * Handles the event when the AuraDrive service is connected.
         *
         * Obtains the remote `IAuraDriveService` interface from the provided binder and updates the connection state to connected.
         *
         * @param name The component name of the connected service.
         * @param service The binder interface to the connected service.
         */
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            auraDriveService = IAuraDriveService.Stub.asInterface(service)
            _isServiceConnected.value = true
        }

        /**
         * Handles disconnection from the AuraDrive service.
         *
         * Clears the reference to the remote service and updates the connection state to reflect that the service is disconnected.
         */
        override fun onServiceDisconnected(name: ComponentName?) {
            auraDriveService = null
            _isServiceConnected.value = false
        }
    }

    /**
     * Initiates binding to the remote AuraDrive service using an explicit intent.
     *
     * If a security exception occurs during binding, the connection state is set to false.
     */
    fun bindService() {
        val intent = Intent().apply {
            component = ComponentName(
                "com.genesis.ai.app",
                "com.genesis.ai.app.service.AuraDriveServiceImpl"
            )
        }
        try {
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        } catch (e: SecurityException) {
            _isServiceConnected.value = false
        }
    }

    /**
     * Unbinds from the AuraDrive service and marks the connection as disconnected.
     *
     * Any exceptions during unbinding are ignored.
     */
    fun unbindService() {
        try {
            context.unbindService(serviceConnection)
        } catch (_: Exception) {
        }
        auraDriveService = null
        _isServiceConnected.value = false
    }

    /**
     * Returns the current status string from the remote AuraDrive service.
     *
     * @return The status string reported by the remote service, or null if the service is unavailable or a RemoteException occurs.
     */
    suspend fun getStatusFromOracleDrive(): String? = withContext(Dispatchers.IO) {
        try {
            auraDriveService?.getOracleDriveStatus()
        } catch (e: RemoteException) {
            null
        }
    }

    /**
     * Toggles the LSPosed module on the connected Oracle Drive service using the service's internal logic.
     *
     * The `packageName` and `enable` parameters are ignored by the remote service.
     *
     * @return The result string from the service, or null if the service is unavailable or a remote exception occurs.
     */
    suspend fun toggleModuleOnOracleDrive(packageName: String, enable: Boolean): String? =
        withContext(Dispatchers.IO) {
            try {
                auraDriveService?.toggleLSPosedModule(packageName, enable)
            } catch (e: RemoteException) {
                null
            }
        }

    /**
     * Retrieves a detailed internal status report from the remote AuraDrive service.
     *
     * @return The detailed status string, or null if the service is unavailable or a remote exception occurs.
     */
    suspend fun getDetailedInternalStatus(): String? = withContext(Dispatchers.IO) {
        try {
            auraDriveService?.getDetailedInternalStatus()
        } catch (e: RemoteException) {
            null
        }
    }

    /**
     * Retrieves the internal diagnostics log from the remote AuraDrive service.
     *
     * Returns null if the service is unavailable or a remote exception occurs.
     *
     * @return The diagnostics log string, or null if unavailable.
     */
    suspend fun getInternalDiagnosticsLog(): String? = withContext(Dispatchers.IO) {
        try {
            auraDriveService?.getInternalDiagnosticsLog()
        } catch (e: RemoteException) {
            null
        }
    }
}
