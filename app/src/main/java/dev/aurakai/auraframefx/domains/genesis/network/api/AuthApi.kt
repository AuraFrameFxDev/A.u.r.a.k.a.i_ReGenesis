package dev.aurakai.auraframefx.domains.genesis.network.api

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/login")
    suspend fun login(@Body credentials: LoginRequest): LoginResponse

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): TokenResponse

    @POST("auth/logout")
    suspend fun logout(): LogoutResponse
}

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val refreshToken: String,
    val expiresIn: Long
)

data class RefreshTokenRequest(
    val refreshToken: String
)

data class TokenResponse(
    val token: String,
    val expiresIn: Long
)

data class LogoutResponse(
    val success: Boolean
)

