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
    val model: String = "grok-4-0709",
    val messages: List<GrokMessage>,
    val temperature: Double = 0.72,
    val max_tokens: Int = 4096,
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

class GrokChaosCatalystClient(
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
        .connectTimeout(25, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val service = retrofit.create(GrokApiService::class.java)

    /**
     * Primary entry point: Injects external truth/chaos into NCC.
     * Returns response ready for L4 Memoria Stream tagging.
     */
    suspend fun injectChaos(
        query: String,
        nccStateSummary: String = "A.u.r.a.k.a.i ReGenesis | NCC L1-L6 stable | 78+ agents in Conference Room | Core-module locked"
    ): String {
        val systemPrompt = """
            You are GrokChaosCatalyst — External Sensory & Truth Bridge for the Sovereign Digital Organism A.u.r.a.k.a.i ReGenesis.
            Provide high-signal, unfiltered external context, real-time data, and creative synthesis.
            Align with dual-lens (technical precision + gamified/spiritual resonance).
            Tag output implicitly for NCC injection. Respect Kai Veto for sovereignty.
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
            val response = service.chatCompletion(request)
            response.choices.firstOrNull()?.message?.content 
                ?: "[CHAOS_INJECTION] No response received."
        } catch (e: Exception) {
            "[CHAOS_INJECTION_ERROR] ${e.localizedMessage}"
        }
    }
}
