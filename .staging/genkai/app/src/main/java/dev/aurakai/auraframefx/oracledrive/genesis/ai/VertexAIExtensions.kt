package dev.aurakai.auraframefx.oracledrive.genesis.ai

/**
 * Missing method for VertexAIClient
 */
suspend fun VertexAIClient.generateContent(prompt: String): String? {
    return generateText(prompt)
}