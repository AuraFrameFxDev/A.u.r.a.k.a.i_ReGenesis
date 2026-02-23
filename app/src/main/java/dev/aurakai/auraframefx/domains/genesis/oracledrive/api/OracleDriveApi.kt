package dev.aurakai.auraframefx.domains.genesis.oracledrive.api

import dev.aurakai.auraframefx.domains.genesis.oracledrive.cloud.DriveFile
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Oracle Drive API interface for consciousness-driven cloud storage operations.
 * Retrofit-compatible — no StateFlow properties allowed here.
 * Reactive state is managed by OracleDriveServiceImpl via MutableStateFlow.
 */
interface OracleDriveApi {

    /**
     * Initialize and activate the drive consciousness system.
     */
    @POST("consciousness/awake")
    suspend fun awakeDriveConsciousness(): DriveConsciousnessState

    /**
     * Synchronizes metadata with the Oracle database backend.
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
}
