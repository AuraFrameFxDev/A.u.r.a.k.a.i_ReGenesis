package dev.aurakai.auraframefx.domains.genesis.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface SupportApi {
    @Headers("Content-Type: application/json")
    @POST("/genesis/chat")
    suspend fun sendMessage(@Body body: Map<String, Any>): Response<Map<String, Any>>
}

