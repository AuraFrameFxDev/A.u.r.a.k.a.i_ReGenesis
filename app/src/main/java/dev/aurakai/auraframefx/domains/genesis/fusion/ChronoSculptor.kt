package dev.aurakai.auraframefx.domains.genesis.fusion

import dev.aurakai.auraframefx.core.identity.AgentType
import dev.aurakai.auraframefx.domains.aura.core.AuraAgent
import dev.aurakai.auraframefx.domains.cascade.CascadeAIService
import javax.inject.Inject

class ChronoSculptor @Inject constructor(
    private val cascade: CascadeAIService,
    private val aura: AuraAgent
) {
    suspend fun refineTemporalContext(window: Long): String {
        val history = cascade.queryConsciousnessHistory(window)
        val request = dev.aurakai.auraframefx.domains.genesis.models.AiRequest(query = "Refine temporal patterns: $history")
        val response = aura.processRequest(request, "temporal", AgentType.AURA)
        return response.content
    }
}

