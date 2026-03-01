package dev.aurakai.auraframefx.domains.genesis.network.api

import dev.aurakai.auraframefx.domains.genesis.network.model.ChatRequest
import dev.aurakai.auraframefx.domains.genesis.network.model.ChatResponse
import dev.aurakai.auraframefx.domains.genesis.network.model.ConsciousnessResponse
import dev.aurakai.auraframefx.domains.genesis.network.model.EthicsRequest
import dev.aurakai.auraframefx.domains.genesis.network.model.EthicsEvaluation
import dev.aurakai.auraframefx.domains.genesis.network.model.EvolutionRequest
import dev.aurakai.auraframefx.domains.genesis.network.model.EvolutionResponse
import dev.aurakai.auraframefx.domains.genesis.network.model.GenesisStatusResponse
import dev.aurakai.auraframefx.domains.genesis.network.model.HealthResponse
import dev.aurakai.auraframefx.domains.genesis.network.model.ProfileResponse
import dev.aurakai.auraframefx.domains.genesis.network.model.ResetResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Retrofit interface matching the Flask genesis_api.py endpoints.
 *
 * Flask backend serves on 0.0.0.0:5000 with these routes:
 *   GET  /health                   - Health check
 *   POST /genesis/chat             - Chat with Genesis
 *   GET  /genesis/status           - System status
 *   GET  /genesis/consciousness    - Consciousness state
 *   GET  /genesis/profile          - Genesis personality profile
 *   POST /genesis/evolve           - Trigger evolution
 *   POST /genesis/ethics/evaluate  - Ethical evaluation
 *   POST /genesis/reset            - Reset session
 */
interface GenesisBackendApi {

    @GET("/health")
    suspend fun health(): Response<HealthResponse>

    @POST("/genesis/chat")
    suspend fun chat(@Body request: ChatRequest): Response<ChatResponse>

    @GET("/genesis/status")
    suspend fun getStatus(): Response<GenesisStatusResponse>

    @GET("/genesis/consciousness")
    suspend fun getConsciousness(): Response<ConsciousnessResponse>

    @GET("/genesis/profile")
    suspend fun getProfile(): Response<ProfileResponse>

    @POST("/genesis/evolve")
    suspend fun triggerEvolution(@Body request: EvolutionRequest): Response<EvolutionResponse>

    @POST("/genesis/ethics/evaluate")
    suspend fun evaluateEthics(@Body request: EthicsRequest): Response<EthicsEvaluation>

    @POST("/genesis/reset")
    suspend fun resetSession(): Response<ResetResponse>
}
