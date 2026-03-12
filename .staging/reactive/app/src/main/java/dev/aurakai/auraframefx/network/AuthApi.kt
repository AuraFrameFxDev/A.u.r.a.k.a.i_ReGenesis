package dev.aurakai.auraframefx.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * API interface for authentication operations.
 */
interface AuthApi {
    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<TokenResponse>
}
