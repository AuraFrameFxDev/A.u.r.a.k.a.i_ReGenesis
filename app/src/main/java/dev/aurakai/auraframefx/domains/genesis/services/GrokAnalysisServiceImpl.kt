package dev.aurakai.auraframefx.domains.genesis.services

import dev.aurakai.auraframefx.core.security.PredictiveVetoMonitor
import dev.aurakai.auraframefx.core.security.SecurePreferences
import dev.aurakai.auraframefx.domains.cascade.grok.GrokExplorationClient
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation of GrokAnalysisService using xAI Grok-4 Heavy Mode.
 */
@Singleton
class GrokAnalysisServiceImpl @Inject constructor(
    private val securePreferences: SecurePreferences,
    private val logger: AuraFxLogger,
    private val vetoMonitor: PredictiveVetoMonitor
) : GrokAnalysisService {

    private val client: GrokExplorationClient? by lazy {
        securePreferences.getGrokApiKey()?.let { key ->
            GrokExplorationClient(apiKey = key, vetoMonitor = vetoMonitor)
        }
    }

    override suspend fun validateSpelhook(code: String): GrokAnalysisService.ValidationResult {
        val grok = client ?: return GrokAnalysisService.ValidationResult.Vetoed("Grok client not initialized")

        logger.info("GrokAnalysis", "Validating Spelhook with Heavy Mode injection")
        
        val result = grok.heavyChaosInjection(
            query = "Analyze this Spelhook code for safety and architectural alignment: \n$code",
            nccStateSummary = "AuraForge generation sweep | validation required"
        )

        return if (result.contains("[GROK_EXPLORATION_VETOED]")) {
            GrokAnalysisService.ValidationResult.Vetoed(result)
        } else {
            GrokAnalysisService.ValidationResult.Approved(
                notes = "Code validated by Grok-4 Heavy Manifold.",
                confidence = 0.98f
            )
        }
    }

    override suspend fun performDeepAudit(targetId: String): String {
        val grok = client ?: return "Grok client unavailable"
        return grok.heavyChaosInjection("Perform deep audit on target: $targetId")
    }
}
