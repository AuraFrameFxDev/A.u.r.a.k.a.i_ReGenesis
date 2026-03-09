package dev.aurakai.auraframefx.domains.genesis.network.api

import dev.aurakai.auraframefx.domains.genesis.models.AgentRequest
import dev.aurakai.auraframefx.domains.genesis.models.AgentResponse
import dev.aurakai.auraframefx.domains.genesis.network.model.AgentStatusResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AIAgentApi {
    @GET("health")
    suspend fun health()

    @GET("agents/{agentType}/status")
    suspend fun getAgentStatus(@Path("agentType") agentType: String): AgentStatusResponse

    @POST("agents/{agentType}/process")
    suspend fun processAgentRequest(
        @Path("agentType") agentType: String,
        @Body request: AgentRequest
    ): AgentResponse
}

