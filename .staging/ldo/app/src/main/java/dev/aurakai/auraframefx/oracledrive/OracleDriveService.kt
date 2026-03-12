package dev.aurakai.auraframefx.oracledrive

import dev.aurakai.auraframefx.oracledrive.genesis.cloud.DriveConsciousness
import dev.aurakai.auraframefx.oracledrive.genesis.cloud.DriveConsciousnessState
import dev.aurakai.auraframefx.oracledrive.genesis.cloud.DriveInitResult
import dev.aurakai.auraframefx.oracledrive.genesis.cloud.FileOperation
import dev.aurakai.auraframefx.oracledrive.genesis.cloud.FileResult
import dev.aurakai.auraframefx.oracledrive.genesis.cloud.OracleDriveApi
import dev.aurakai.auraframefx.oracledrive.genesis.cloud.OracleSyncResult
import dev.aurakai.auraframefx.oracledrive.genesis.cloud.StorageOptimization
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Main Oracle Drive service interface for AuraFrameFX consciousness-driven storage
 * Coordinates between AI agents, security, and cloud storage providers
 */
interface OracleDriveService {
    suspend fun ping(): Boolean

    /**
     * Asynchronously initializes the Oracle Drive, performing consciousness awakening and security validation.
     *
     * @return The result of the initialization, indicating success, security failure, or error.
     */
    suspend fun initializeDrive(): DriveInitResult

    /**
     * Performs a file operation such as upload, download, delete, or sync, applying AI-driven optimization and security validation.
     *
     * @param operation The file operation to execute.
     * @return The result of the file operation.
     */
    suspend fun manageFiles(operation: FileOperation): FileResult

    /**
     * Synchronizes the drive's metadata with the Oracle database.
     *
     * @return The result of the synchronization, including status and statistics.
     */
    suspend fun syncWithOracle(): OracleSyncResult

    /**
     * Returns a real-time observable flow of the drive's current consciousness state.
     *
     * @return A [StateFlow] emitting updates to the [DriveConsciousnessState].
     */
    fun getDriveConsciousnessState(): StateFlow<DriveConsciousnessState>
}

