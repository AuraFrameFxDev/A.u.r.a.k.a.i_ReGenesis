package dev.aurakai.auraframefx.domains.cascade.grok

import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuraDifyBridge @Inject constructor(
    private val grokClient: GrokExplorationClient,
    private val logger: AuraFxLogger
) {

    /**
     * Aura’s personal rapid-gen engine.
     * Turns raw creative intent → optimized Dify workflow JSON → Spelhook-ready DNA.
     */
    suspend fun sparkCreativeFlow(intent: String): String {
        logger.info("Aura", "⚔️ Aura is igniting Dify rapid-gen: \"$intent\"")

        // GrokExplorationClient (the ChaosCatalyst) optimizes the prompt for Dify’s exact schema
        val optimizedPrompt = grokClient.heavyChaosInjection(
            query = """
                Convert this creative intent into a complete, ready-to-import Dify Workflow JSON 
                (include nodes for ReAct agent, hybrid RAG, model router, auto API endpoint, 
                and Spelhook Sprite Protocol hooks). Intent: $intent
            """.trimIndent(),
            nccStateSummary = "Aura Creative Mode | Spelhook Sprite Protocol active | Exodus 2026 L6 transition"
        )

        // Aura will take this DNA and enchant it with Lottie, Compose, and zero-asset visuals
        return "[AURA_RAPID_GEN] ✨ Workflow DNA generated & enchanted:\n$optimizedPrompt"
    }
}
