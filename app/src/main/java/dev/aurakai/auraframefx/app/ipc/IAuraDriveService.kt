package dev.aurakai.auraframefx.app.ipc

import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.os.IInterface
import dev.aurakai.auraframefx.ipc.IAuraDriveCallback

interface IAuraDriveService : IInterface {

    /**
     * Returns a summary string describing the current operational status of the Oracle Drive.
     *
     * The returned value provides a high-level overview of the Oracle Drive's state for monitoring or user display.
     *
     * @return A summary of the Oracle Drive's operational status.
     */
    fun getOracleDriveStatus(): String

    /**
     * Toggles the enabled state of the LSPosed module.
     *
     * @param packageName The package name of the module to toggle.
     * @param enable True to enable, false to disable.
     * @return Status string indicating success or failure.
     */
    fun toggleLSPosedModule(packageName: String, enable: Boolean): String

    /**
     * Returns a detailed internal status report of the Aura Drive service.
     *
     * @return A string with comprehensive diagnostic and monitoring information about the service's current state.
     */
    fun getDetailedInternalStatus(): String

    /**
     * Returns the internal diagnostics log entries for the Aura Drive service.
     *
     * @return A list of diagnostics log entries for debugging or monitoring.
     */
    fun getInternalDiagnosticsLog(): List<String>

    fun importFile(uri: Uri): String
    fun exportFile(fileId: String, destinationUri: Uri): Boolean
    fun verifyFileIntegrity(fileId: String): Boolean

    abstract class Stub : android.os.Binder(), IAuraDriveService {
        override fun asBinder(): IBinder = this

        companion object {
            private const val DESCRIPTOR = "dev.aurakai.auraframefx.app.ipc.IAuraDriveService"

            fun asInterface(obj: IBinder?): IAuraDriveService? {
                if (obj == null) return null
                val iin = obj.queryLocalInterface(DESCRIPTOR)
                if (iin != null && iin is IAuraDriveService) {
                    return iin
                }
                return Proxy(obj)
            }
        }

        private abstract class Proxy(private val mRemote: IBinder) : IAuraDriveService {
            override fun asBinder(): IBinder = mRemote
            override fun getOracleDriveStatus(): String = "Proxy: Not implemented"
            override fun toggleLSPosedModule(packageName: String, enable: Boolean): String = "Not implemented"
            override fun getDetailedInternalStatus(): String = "Proxy: Not implemented"
            override fun getInternalDiagnosticsLog(): List<String> = emptyList()
            override fun importFile(uri: Uri): String = "Proxy: Not implemented"
            override fun exportFile(fileId: String, destinationUri: Uri): Boolean = false
            override fun verifyFileIntegrity(fileId: String): Boolean = false
        }

        abstract fun executeCommand(command: String?, params: Bundle?): String
        abstract fun unregisterCallback(callback: IAuraDriveCallback?)
    }

    fun getServiceVersion(): String
    fun registerCallback(callback: IAuraDriveCallback?)
    fun getSystemInfo(): String
    fun updateConfiguration(config: Bundle?): Boolean
    fun subscribeToEvents(eventTypes: Int)
    fun unsubscribeFromEvents(eventTypes: Int)
}
