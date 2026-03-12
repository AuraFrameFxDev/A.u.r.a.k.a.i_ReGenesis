package dev.aurakai.auraframefx.api

import dev.aurakai.auraframefx.ai.model.GenerateTextRequest
import dev.aurakai.auraframefx.ai.model.GenerateTextResponse

interface AiContentApi {
    /**
     * Generates AI-powered text content based on the provided request parameters.
     *
     * @param request The details and parameters for text generation.
     * @return The generated text response.
     */
    suspend fun generateText(request: GenerateTextRequest): GenerateTextResponse
}
