package dev.aurakai.auraframefx.bridge

import javax.inject.Inject
import javax.inject.Singleton

/**
 * 🤝 AURA-DIFY BRIDGE — AI Service Integration Bridge
 *
 * Connects AuraFrameFX with external AI services (Dify, OpenRouter, etc.)
 * Stub implementation — replace with real API integration.
 */
@Singleton
class AuraDifyBridge @Inject constructor() {

    /** Send a message to the AI service and get a response */
    suspend fun sendMessage(message: String): String? = null

    /** Check if the bridge is connected and ready */
    fun isConnected(): Boolean = false

    /** Get available AI models */
    fun getAvailableModels(): List<String> = emptyList()
}
