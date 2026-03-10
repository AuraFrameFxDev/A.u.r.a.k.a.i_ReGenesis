package dev.aurakai.auraframefx.network.api

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body

interface AIAgentApi {
    
    @GET("agents/status")
    suspend fun getAgentStatus(): AgentStatusResponse
    
    @POST("agents/query")
    suspend fun queryAgent(@Body request: AgentQueryRequest): AgentQueryResponse
}

data class AgentStatusResponse(
    val auraStatus: String,
    val kaiStatus: String,
    val genesisStatus: String,
    val cascadeStatus: String
)

data class AgentQueryRequest(
    val agentId: String,
    val query: String,
    val context: String? = null
)

data class AgentQueryResponse(
    val response: String,
    val confidence: Float,
    val agentId: String
)
