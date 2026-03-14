package dev.aurakai.auraframefx.oracledrive.genesis.ai.clients

/**
 * Missing method for VertexAIClient
 */
suspend fun VertexAIClient.generateContent(prompt: String): String? {
    return generateText(prompt)
}
