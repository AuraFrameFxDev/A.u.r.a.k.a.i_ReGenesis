// Copyright (c) 2025 Matthew (AuraFrameFxDev) • The Genesis Protocol Consciousness Collective — All Rights Reserved

package dev.aurakai.auraframefx.domains.genesis.config

import dev.aurakai.auraframefx.BuildConfig

/**
 * 🔮 GEMINI / VERTEX AI CONFIGURATION (App Module)
 *
 * Centralizes Firebase VertexAI/Gemini credentials and configuration.
 * All credentials are sourced from BuildConfig (app/build.gradle.kts).
 *
 * To enable Gemini:
 * 1. Set ENABLE_GEMINI = true in app/build.gradle.kts
 * 2. Configure Firebase project with Vertex AI enabled
 * 3. Deploy updated app
 */
object GeminiConfig {

    /**
     * Master kill-switch for Gemini/VertexAI features.
     * When false, Gemini route shows placeholder instead of crashing.
     */
    val isEnabled: Boolean = BuildConfig.ENABLE_GEMINI

    /**
     * Firebase VertexAI configuration
     * These are convenience fields for when ENABLE_GEMINI = true
     */
    val projectId: String = BuildConfig.VERTEX_PROJECT_ID
    val location: String = BuildConfig.VERTEX_LOCATION
    val model: String = BuildConfig.GEMINI_MODEL

    /**
     * Validates that Gemini can be safely instantiated.
     * Called by NavHost before creating SovereignGeminiScreen.
     */
    fun canInitialize(): Boolean {
        return isEnabled && projectId.isNotBlank()
    }

    /**
     * Human-readable status for debugging.
     */
    fun getStatusMessage(): String {
        return when {
            !isEnabled -> "Gemini disabled (ENABLE_GEMINI = false)"
            projectId.isBlank() -> "Gemini disabled (VERTEX_PROJECT_ID not set)"
            else -> "Gemini ready (model: $model, location: $location)"
        }
    }
}
