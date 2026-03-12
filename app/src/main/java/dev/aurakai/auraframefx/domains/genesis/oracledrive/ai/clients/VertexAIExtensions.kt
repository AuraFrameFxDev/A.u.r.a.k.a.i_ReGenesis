package dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.clients

/**
 * Missing method for VertexAIClient
 */
suspend fun VertexAIClient.generateContent(prompt: String): String? {
    return generateText(prompt)
}
