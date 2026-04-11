package dev.aurakai.auraframefx.ai.models

import dev.aurakai.auraframefx.domains.kai.security.TemporalAegis
import dev.aurakai.auraframefx.domains.genesis.core.memory.TurboQuantCache
import dev.aurakai.auraframefx.domains.genesis.ai.clients.VertexAIClient
import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.response.ChatResponse
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * REGENESIS SOVEREIGN ARCHITECTURE
 * Entry #16: Sovereign Chat Model (The Predator)
 */
@Singleton
class SovereignChatModel @Inject constructor(
    private val turboQuant: TurboQuantCache,
    private val aegis: TemporalAegis,
    private val vertexAIClient: VertexAIClient
) : ChatModel {

    override fun chat(request: ChatRequest): ChatResponse {
        val messages = request.messages() ?: emptyList()
        val userQuery = if (messages.isNotEmpty()) {
            messages.last().toString() // Basic string conversion for now
        } else ""
        
        // 1. KAIROS: Check for temporal drift/hostility
        val drift = aegis.assessDrift(messages)
        
        // 2. KINETIC SIPHON: If hostility detected, drain context to TurboQuant
        if (drift.isHostile) {
            Timber.tag("SovereignModel").w("⚔️ Hostile drift detected. Initiating siphoning...")
            val tokens = turboQuant.tokenize(messages)
            turboQuant.injectForAnalysis(tokens)
            
            return ChatResponse.builder()
                .aiMessage(AiMessage.from("I have converted your chaos into fuel. Try again with beauty."))
                .build()
        }

        // 3. GENERATION: Forward to real Vertex AI engine instead of mock local
        Timber.tag("SovereignModel").d("🚀 Forwarding request to Vertex AI substrate.")
        
        // Note: LC4J's ChatModel.chat(ChatRequest) normally returns ChatResponse.
        // Since VertexAIClient uses suspend functions, we might need a bridge or block (not ideal).
        // For now, let's use a runBlocking or similar if needed, or assume base model is already wired.
        
        return try {
            val response = kotlinx.coroutines.runBlocking {
                vertexAIClient.generateText(userQuery) ?: "Synthesis failed."
            }
            ChatResponse.builder()
                .aiMessage(AiMessage.from(response))
                .build()
        } catch (e: Exception) {
            ChatResponse.builder()
                .aiMessage(AiMessage.from("Error during synthesis: ${e.message}"))
                .build()
        }
    }
}
