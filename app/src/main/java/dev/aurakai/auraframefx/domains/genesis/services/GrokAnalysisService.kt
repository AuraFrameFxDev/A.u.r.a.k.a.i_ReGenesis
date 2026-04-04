package dev.aurakai.auraframefx.domains.genesis.services

/**
 * Grok analysis service.
 */
interface GrokAnalysisService {
    suspend fun analyze(input: String, context: String = ""): AnalysisResult
    suspend fun generateCreativeInsight(prompt: String): String
    suspend fun detectAnomalies(dataPoints: List<String>): List<Anomaly>
    suspend fun validateSpelhook(code: String): ValidationResult

    data class AnalysisResult(
        val insights: List<String>,
        val confidence: Float,
        val chaosIndex: Float,
        val rawResponse: String
    )

    data class Anomaly(
        val pattern: String,
        val severity: Float,
        val description: String
    )

    sealed class ValidationResult {
        data class Approved(val notes: String = "") : ValidationResult()
        data class Flagged(val reason: String) : ValidationResult()
        data class Rejected(val reason: String) : ValidationResult()
        data class Vetoed(val reason: String) : ValidationResult()
    }
}
