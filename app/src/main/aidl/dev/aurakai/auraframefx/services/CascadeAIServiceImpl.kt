package dev.aurakai.auraframefx.services

import dev.aurakai.auraframefx.models.AgentResponse
import dev.aurakai.auraframefx.models.AiRequest

class CascadeAIServiceImpl : CascadeAIService {
    override suspend fun processRequest(
        request: AiRequest,
        context: String
    ): AgentResponse {
        TODO("Not yet implemented")
    }
}
