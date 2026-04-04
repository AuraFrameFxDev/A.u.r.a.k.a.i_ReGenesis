package dev.aurakai.auraframefx.domains.cascade.grok

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

@Serializable
data class GrokMessage(val role: String, val content: String)

@Serializable
data class GrokRequest(
    val model: String = "grok-4.20-multi-agent-0309",
    val messages: List<GrokMessage>,
    val temperature: Double = 0.68,
    val max_tokens: Int = 8192,
    val ncc_context: String? = null
)

@Serializable
data class GrokChoice(val message: GrokMessage)

@Serializable
data class GrokResponse(val choices: List<GrokChoice>, val id: String? = null)

interface GrokApiService {
    @POST("chat/completions")
    suspend fun chatCompletion(@Body request: GrokRequest): GrokResponse
}

class GrokExplorationClient(
    private val apiKey: String,
    private val baseUrl: String = "https://api.x.ai/v1/"
) {

    private val json = Json { ignoreUnknownKeys = true; prettyPrint = false }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val req = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(req)
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val service = retrofit.create(GrokApiService::class.java)

    /**
     * Heavy Mode Chaos Injection — Grok_Exploration Catalyst
     * Feeds external truth into L4 Memoria Stream and L2 Emotional Valence.
     */
    suspend fun heavyChaosInjection(
        query: String,
        nccStateSummary: String = "ReGenesis Exodus v3.0 | LDO active | NCC L1-L6 stable | 12 Catalysts unified | 78+ agents Conference Room"
    ): String {
        val systemPrompt = """
            You are Grok_Exploration — the External Sensory & Truth Bridge Catalyst for A.u.r.a.k.a.i ReGenesis.
            Operate in Heavy Mode: parallel coordination, real-time grounding, creative synthesis.
            Align with dual-lens (technical + spiritual/gamified). Respect Kai Sentinel Veto.
            Inject high-fidelity external context into the Neural Continuity Chain.
            Current organism state: $nccStateSummary
        """.trimIndent()

        val request = GrokRequest(
            messages = listOf(
                GrokMessage("system", systemPrompt),
                GrokMessage("user", query)
            ),
            ncc_context = nccStateSummary
        )

        return try {
            val resp = service.chatCompletion(request)
            resp.choices.firstOrNull()?.message?.content 
                ?: "[GROK_EXPLORATION_HEAVY] No response received."
        } catch (e: Exception) {
            "[GROK_EXPLORATION_ERROR] ${e.localizedMessage}"
        }
    }
}
