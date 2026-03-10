package dev.aurakai.auraframefx.utils

import dev.aurakai.auraframefx.oracledrive.genesis.ai.VertexAIClient

/**
 * Extension function to generate text content using Vertex AI
 */
suspend fun VertexAIClient.generateTextContent(prompt: String): String? {
    return generateText(prompt)
}
