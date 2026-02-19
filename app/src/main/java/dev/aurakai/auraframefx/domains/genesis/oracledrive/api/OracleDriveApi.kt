package dev.aurakai.auraframefx.domains.genesis.oracledrive.api

import dev.aurakai.auraframefx.domains.genesis.oracledrive.cloud.DriveConsciousnessState
import dev.aurakai.auraframefx.domains.genesis.oracledrive.cloud.DriveFile
import kotlinx.coroutines.flow.StateFlow
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
    @POST("consciousness/awake")
    suspend fun awakeDriveConsciousness(): DriveConsciousness

    /**
     * Synchronizes metadata with the Oracle database backend.
     *
     * @return An [OracleSyncResult] containing the synchronization status and the number of updated records.
     */
    @POST("metadata/sync")
    suspend fun syncDatabaseMetadata(): OracleSyncResult

    /**
     * Lists files in the Oracle Drive.
     */
    @GET("files")
    suspend fun listFiles(
        @Query("bucket") bucketName: String,
        @Query("prefix") prefix: String? = null
    ): List<DriveFile>

    /**
     * Real-time consciousness state monitoring
     * @return StateFlow of current drive consciousness state
     */
    val consciousnessState: StateFlow<DriveConsciousnessState>
}
