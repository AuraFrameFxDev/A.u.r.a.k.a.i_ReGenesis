package dev.aurakai.auraframefx.domains.cascade.network.apis

import dev.aurakai.auraframefx.domains.cascade.utils.memory.models.GenerateTextRequest
import dev.aurakai.auraframefx.domains.cascade.utils.memory.models.GenerateTextResponse

interface AiContentApi {
    /**
     * Generates AI-powered text content based on the provided request parameters.
     *
     * @param request The details and parameters for text generation.
     * @return The generated text response.
     */
    suspend fun generateText(request: GenerateTextRequest): GenerateTextResponse
}
