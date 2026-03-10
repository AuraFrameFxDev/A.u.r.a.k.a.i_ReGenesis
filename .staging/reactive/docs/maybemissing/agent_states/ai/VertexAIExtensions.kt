package dev.aurakai.auraframefx.ai.clients

/**
 * Missing method for VertexAIClient
 */
suspend fun VertexAIClient.generateContent(prompt: String): String? {
    return generateText(prompt)
}