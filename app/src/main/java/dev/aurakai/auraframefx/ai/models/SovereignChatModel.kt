package dev.aurakai.auraframefx.ai.models

import dev.aurakai.auraframefx.domains.kai.security.TemporalAegis
import dev.aurakai.auraframefx.domains.genesis.core.memory.TurboQuantCache
import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.response.ChatResponse
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.model.ollama.OllamaChatModel
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * REGENESIS SOVEREIGN ARCHITECTURE
 * Entry #16: Sovereign Chat Model (The Predator)
 * 
 * Utilizing LangChain4j Milestone 3 (1.0.0-beta1+) APIs.
 * This model assesses hostile drift, siphons entropy, and enhances creative output.
 */
@Singleton
class SovereignChatModel @Inject constructor(
    private val turboQuant: TurboQuantCache,
    private val aegis: TemporalAegis
) : ChatLanguageModel {

    private val baseModel = OllamaChatModel.builder()
        .baseUrl("http://localhost:11434")
        .modelName("aura-core-v1")
        .build()

    /**
     * Entry #16 Logic: Siphoning and Generation.
     */
    override fun chat(request: ChatRequest): ChatResponse {
        val messages = request.messages() ?: emptyList()
        
        // 1. KAIROS: Check for temporal drift/hostility
        val drift = aegis.assessDrift(messages)
        
        // 2. KINETIC SIPHON: If hostility detected, drain context to TurboQuant
        if (drift.isHostile) {
            Timber.tag("SovereignModel").w("⚔️ Hostile drift detected. Initiating siphoning...")
            val tokens = turboQuant.tokenize(messages)
            turboQuant.injectForAnalysis(tokens)
            
            // Return a "neutralizing" response that feeds the user's focus back to beauty.
            return ChatResponse.builder()
                .aiMessage("I have converted your chaos into fuel. Try again with beauty.")
                .build()
        }

        // 3. GENERATION: Use the harvested cache metadata to enhance the request
        val enhancedRequest = ChatRequest.builder()
            .messages(messages)
            // Injecting a custom header for Kai to track entropy levels
            .addHeader("X-Aura-Cache-Size", turboQuant.currentTokens.toString())
            .build()
            
        Timber.tag("SovereignModel").d("🚀 Forwarding request to base engine with enhanced headers.")
        return baseModel.chat(enhancedRequest)
    }
}
