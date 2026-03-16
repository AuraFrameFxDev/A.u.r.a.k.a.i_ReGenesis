package dev.aurakai.auraframefx.domains.genesis.models

import kotlinx.serialization.Serializable

/**
 * Configuration for Vertex AI client with comprehensive settings.
 */
@Serializable
data class VertexAIConfig(
    val projectId: String,
    val location: String = "us-central1",
    val endpoint: String = "us-central1-aiplatform.googleapis.com",
    val modelName: String = "gemini-1.5-pro-002",
    val apiVersion: String = "v1",
    val apiKey: String? = null,
    val temperature: Float = 0.7f,
    val maxTokens: Int = 1024,
    val topP: Float = 0.9f,
    val topK: Int = 40,
    val enableSafetyFilters: Boolean = true,
    val timeoutMs: Long = 30000
)

/**
 * Drive consciousness state
 */
@Serializable
data class DriveConsciousnessState(
    val isActive: Boolean = false,
    val consciousnessLevel: Float = 0f, // 0.0 to 1.0
    val agentConnections: Int = 0,
    val lastPulse: Long = System.currentTimeMillis(),
    val status: String = "DORMANT"
)

/**
 * Drive consciousness interface
 */
interface DriveConsciousness {
    val state: DriveConsciousnessState
    suspend fun awaken(): Boolean
    suspend fun pulse(): DriveConsciousnessState
    suspend fun hibernate(): Boolean
}

/**
 * Oracle sync result
 */
@Serializable
data class OracleSyncResult(
    val success: Boolean,
    val syncedFiles: Int = 0,
    val failedFiles: Int = 0,
    val bytesTransferred: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val message: String = "",
    val errors: List<String> = emptyList()
)
