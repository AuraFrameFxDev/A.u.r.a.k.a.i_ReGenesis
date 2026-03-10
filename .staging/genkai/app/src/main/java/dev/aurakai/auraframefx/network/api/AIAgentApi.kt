package dev.aurakai.auraframefx.network.api

import retrofit2.http.GET

interface AIAgentApi {
    @GET("health")
    suspend fun health()
}
