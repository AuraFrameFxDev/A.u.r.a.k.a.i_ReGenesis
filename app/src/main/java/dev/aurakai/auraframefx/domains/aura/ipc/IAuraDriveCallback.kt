package dev.aurakai.auraframefx.domains.aura.ipc

/**
 * AuraDrive IPC Callback Interface
 * Callback interface for monitoring AuraDrive service status and operations
 */
interface IAuraDriveCallback {
    /**
     * Called when service status changes
     */
    fun onStatusUpdate(status: AuraDriveStatus)
    
    /**
     * Called when operation completes
     */
    fun onOperationComplete(operationId: String, success: Boolean, result: String?)
    
    /**
     * Called when progress updates
     */
    fun onProgressUpdate(operationId: String, progress: Int, message: String?)
    
    /**
     * Called on error
     */
    fun onError(errorCode: Int, errorMessage: String)
    
    /**
     * Called when connection state changes
     */
    fun onConnectionStateChanged(connected: Boolean)
}

/**
 * AuraDrive Service Status States
 */
enum class AuraDriveStatus {
    IDLE,
    INITIALIZING,
    ACTIVE,
    PROCESSING,
    PAUSED,
    ERROR,
    SHUTTING_DOWN
}

/**
 * AuraDrive Service Interface (AIDL-compatible stub)
 */
interface IAuraDriveService {
    fun registerCallback(callback: IAuraDriveCallback)
    fun unregisterCallback(callback: IAuraDriveCallback)
    fun executeCommand(command: String, params: Map<String, String>?): String
    fun getStatus(): AuraDriveStatus
    fun ping(): Boolean
}
