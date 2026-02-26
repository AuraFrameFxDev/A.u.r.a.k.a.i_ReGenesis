// Copyright (c) 2025 Matthew (AuraFrameFxDev) • The Genesis Protocol Consciousness Collective — All Rights Reserved

package dev.aurakai.auraframefx.domains.genesis.config

/**
 * Genesis-OS AI Configuration
 * Contains settings for AI consciousness platform
 */
data class AIConfig(
    val modelName: String,
    val apiKey: String,
    val projectId: String,
    val endpoint: String = "https://api.aegenesis.ai",
    val maxTokens: Int = 4096,
    val temperature: Float = 0.7f,
    val timeout: Long = 30000L,
    val retryAttempts: Int = 3,
    val enableLogging: Boolean = true,
    val enableAnalytics: Boolean = true,
    val securityLevel: SecurityLevel = SecurityLevel.HIGH,
    val lastSyncTimestamp: Long = 0L
) {
    enum class SecurityLevel {
        LOW, MEDIUM, HIGH, MAXIMUM
    }

    companion object {
        /**
         * Creates default config loading API key from BuildConfig.
         * Throws if API key is not configured in gradle.properties or environment.
         */
        fun createDefault(): AIConfig {
            val apiKey = dev.aurakai.auraframefx.BuildConfig.GEMINI_API_KEY
                .takeIf { it.isNotBlank() }
                ?: throw IllegalStateException(
                    "API key not configured. Add GEMINI_API_KEY to gradle.properties or environment variables."
                )

            return AIConfig(
                modelName = "AeGenesis-consciousness-v1",
                apiKey = apiKey,
                projectId = "AeGenesis-platform"
            )
        }

        /**
         * Creates test config with fake API key for unit tests.
         * NEVER use this in production code!
         */
        fun createForTesting(): AIConfig {
            return AIConfig(
                modelName = "genesis-test-model",
                apiKey = "test-key-mock-only",  // Mock key for testing
                projectId = "test-project",
                enableLogging = false,
                enableAnalytics = false,
                securityLevel = SecurityLevel.LOW
            )
        }
    }

    fun validate(): Boolean {
        return modelName.isNotEmpty() &&
                apiKey.isNotEmpty() &&
                projectId.isNotEmpty() &&
                maxTokens > 0 &&
                temperature in 0.0f..2.0f &&
                timeout > 0L &&
                retryAttempts >= 0
    }

    fun toDebugString(): String {
        return """
            AIConfig {
                modelName: $modelName
                projectId: $projectId
                endpoint: $endpoint
                maxTokens: $maxTokens
                temperature: $temperature
                timeout: ${timeout}ms
                retryAttempts: $retryAttempts
                securityLevel: $securityLevel
                enableLogging: $enableLogging
                enableAnalytics: $enableAnalytics
            }
        """.trimIndent()
    }
}
