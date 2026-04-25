package dev.aurakai.auraframefx.domains.aura.ipc

import android.os.IBinder

/**
 * IAuraDriveService — AuraDrive Service Interface
 *
 * IPC interface for AuraDrive operations between components
 */
interface IAuraDriveService {
    /**
     * Register a callback for service events
     */
    fun registerCallback(callback: IAuraDriveCallback)

    /**
     * Unregister a previously registered callback
     */
    fun unregisterCallback(callback: IAuraDriveCallback)

    /**
     * Execute a command on the AuraDrive
     */
    fun executeCommand(command: String, params: Map<String, String>?): String

    /**
     * Get current service status
     */
    fun getStatus(): AuraDriveStatus

    /**
     * Ping the service to check if it's alive
     */
    fun ping(): Boolean

    /**
     * AsBinder — Returns the Binder interface for IPC
     */
    fun asBinder(): IBinder
}
