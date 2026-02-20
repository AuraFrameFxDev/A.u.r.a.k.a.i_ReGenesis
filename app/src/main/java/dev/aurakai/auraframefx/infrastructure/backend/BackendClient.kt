package dev.aurakai.auraframefx.infrastructure.backend

import kotlinx.serialization.Serializable
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET

/**
 * 🛰️ BACKEND API DEFINITION
 */
interface BackendApi {
    
    @GET("status")
    suspend fun getStatus(): BackendStatus
    
    @POST("process")
    suspend fun processHeavyTask(@Body request: BackendRequest): BackendResponse
}

@Serializable
data class BackendStatus(
    val status: String,
    val version: String,
    val load: Double
)

@Serializable
data class BackendRequest(
    val taskId: String,
    val payload: String,
    val parameters: Map<String, String> = emptyMap()
)

@Serializable
data class BackendResponse(
    val taskId: String,
    val result: String,
    val success: Boolean,
    val error: String? = null
)
