package AeGenesis.ai.config

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
    val securityLevel: SecurityLevel = SecurityLevel.HIGH
) {
    enum class SecurityLevel {
        LOW, MEDIUM, HIGH, MAXIMUM
    }

    companion object {
        fun createDefault(): AIConfig {
            return AIConfig(
                modelName = "AeGenesis-consciousness-v1",
                apiKey = "AeGenesis-default-key",
                projectId = "AeGenesis-platform"
            )
        }

        fun createForTesting(): AIConfig {
            return AIConfig(
                modelName = "genesis-test-model",
                apiKey = "test-key",
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
