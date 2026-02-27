package dev.aurakai.auraframefx.domains.genesis.oracledrive.cloud

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

/**
 * 🌐 Oracle Cloud API — Retrofit interface
 * Matches the methods called by OracleDriveRepositoryImpl.
 */
interface OracleCloudApi {

    /**
     * List objects in a bucket, optionally filtered by prefix.
     */
    @GET("n/{namespace}/b/{bucket}/o")
    suspend fun listFiles(
        @Path("bucket") bucketName: String,
        @Query("prefix") prefix: String? = null,
        @Path("namespace") namespace: String = "aurakai"
    ): Response<ListObjectsResponse>

    /**
     * Upload a file (PUT object) to the bucket.
     */
    @PUT("n/{namespace}/b/{bucket}/o/{objectName}")suspend fun uploadFile(
        @Path("bucket") bucketName: String,
        @Path("objectName") objectName: String,
        @Body body: RequestBody
    ,
        @Path("namespace") namespace: String = "aurakai"
    ): Response<Unit>

    /**
     * Download a file from the bucket.
     */
    @GET("n/{namespace}/b/{bucket}/o/{objectName}")
    @Streaming
    suspend fun downloadFile(
        @Path("bucket") bucketName: String,
        @Path("objectName") objectName: String,
        @Path("namespace") namespace: String = "aurakai"
    ): Response<ResponseBody>

    /**
     * Delete an object from the bucket.
     */
    @DELETE("n/{namespace}/b/{bucket}/o/{objectName}")
    suspend fun deleteFile(
        @Path("bucket") bucketName: String,
        @Path("objectName") objectName: String,
        @Path("namespace") namespace: String = "aurakai"
    ): Response<Unit>
}

// ─────────────────────────────────────────────────────────────────────────
// Response models
// ─────────────────────────────────────────────────────────────────────────

data class ListObjectsResponse(
    val objects: List<ObjectSummary> = emptyList()
)

data class ObjectSummary(
    val name: String,
    val size: Long,
    val timeCreated: String
)
