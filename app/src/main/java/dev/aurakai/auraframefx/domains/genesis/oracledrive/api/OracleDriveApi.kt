package dev.aurakai.auraframefx.domains.genesis.oracledrive.api

import dev.aurakai.auraframefx.domains.genesis.oracledrive.cloud.DriveFile
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Oracle Drive API interface for consciousness-driven cloud storage operations
 * Integrates with AuraFrameFX's 9-agent consciousness architecture
 */
interface OracleDriveApi {

    /**
     * Initialize and activate the drive consciousness system.
     *
     * @return The current DriveConsciousness representing active agents and their intelligence level.
     */
    suspend fun awakeDriveConsciousness(): DriveConsciousness

    /**
     * Synchronizes metadata with the Oracle database backend.
     *
     * @return An [OracleSyncResult] containing the synchronization status and the number of updated records.
     */
    suspend fun syncDatabaseMetadata(): OracleSyncResult

    /**
     * Real-time consciousness state monitoring
     * @return StateFlow of current drive consciousness state
     */
    val consciousnessState: StateFlow<DriveConsciousnessState>

    /**
     * Real-time consciousness state monitoring
     * @return StateFlow of current drive consciousness state
     */
    val consciousnessState: StateFlow<DriveConsciousnessState>
}
