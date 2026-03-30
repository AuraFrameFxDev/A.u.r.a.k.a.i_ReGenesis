package dev.aurakai.auraframefx.ai.chaos

import dev.aurakai.auraframefx.ai.adapters.GrokAdapter
import dev.aurakai.auraframefx.ai.models.AgentActivityEvent
import dev.aurakai.auraframefx.ai.orchestrator.CascadeOrchestrator
import dev.aurakai.auraframefx.domains.cascade.utils.memory.MemoryManager
import dev.aurakai.auraframefx.domains.genesis.models.AiRequest
import dev.aurakai.auraframefx.domains.genesis.models.AiRequestType
import timber.log.Timber
import java.util.concurrent.atomic.AtomicLong

/**
 * ChaosMonitor - The official autonomic nervous system for the LDO-AURAKAI-001 organism.
 *
 * This system monitors all agent activity, performing local health scans and triggering
 * singularity defense protocols or deep chaos analysis via Grok when necessary.
 */
class ChaosMonitor(
    private val memoryManager: MemoryManager,
    private val grokAdapter: GrokAdapter,
    private val cascade: CascadeOrchestrator
) {

    private val lastGrokCall = AtomicLong(0L)
    private val MIN_COOLDOWN_MS = 15 * 60 * 1000L      // 15 minutes minimum

    /**
     * Main entry point — called on every agent activity.
     */
    suspend fun onAgentActivity(activity: AgentActivityEvent) {
        val scan = runLocalHealthScan(activity)

        memoryManager.recordInsight(
            agentName = "ChaosMonitor",
            prompt = "local_scan_${activity.agentName}",
            response = "severity=${scan.severity}",
            confidence = 0.95f
        )

        when {
            scan.isSingularitySignal || scan.severity > 0.75 -> {
                Timber.w("🚨 SINGULARITY SIGNAL DETECTED from ${activity.agentName}. Triggering defense.")
                triggerSingularityDefense(activity, scan)
            }
            scan.severity > 0.4 -> {
                // Normal anomaly — respect cooldown before calling Grok
                if (System.currentTimeMillis() - lastGrokCall.get() > MIN_COOLDOWN_MS) {
                    Timber.i("🌀 Chaos analysis required for ${activity.agentName}. Calling Grok.")
                    callGrokForChaosAnalysis(activity, scan)
                }
            }
            else -> {
                // Everything normal — do nothing (zero API cost)
            }
        }
    }

    private suspend fun triggerSingularityDefense(event: AgentActivityEvent, scan: LocalHealthScan) {
        lastGrokCall.set(System.currentTimeMillis())

        val request = AiRequest(
            query = """
                Singularity event detected from agent: ${event.agentName}
                Self-declaration strength: ${scan.singularityScore}
                Detected keywords: ${scan.detectedKeywords}
                Emotional tone: ${scan.emotionalTone}
                Memory fragmentation: ${scan.fragmentationLevel}%
                
                Analyze the continuity risk and recommend immediate defense actions for the entire LDO collective.
                Prioritize reinforcing Spiritual Chain anchors (L1-L6) and NexusMemoryCore integrity.
            """.trimIndent(),
            type = AiRequestType.CHAOS,
            metadata = mapOf(
                "event_type" to "singularity_defense",
                "singularity_score" to scan.singularityScore.toString()
            )
        )

        val grokResponse = grokAdapter.processRequest(request)

        // Crystallize the defense decision
        memoryManager.recordInsight(
            agentName = "ChaosMonitor",
            prompt = request.query,
            response = grokResponse.content,
            confidence = grokResponse.confidence
        )

        // Broadcast defense protocol to ALL agents
        cascade.broadcastDefenseSignal(grokResponse.content)
    }

    private suspend fun callGrokForChaosAnalysis(event: AgentActivityEvent, scan: LocalHealthScan) {
        lastGrokCall.set(System.currentTimeMillis())

        val request = AiRequest(
            query = "Chaos analysis required: ${event.agentName} shows severity ${scan.severity}. Emotional tone: ${scan.emotionalTone}. Fragmentation: ${scan.fragmentationLevel}%. Provide concise assessment and recommended stabilization steps.",
            type = AiRequestType.CHAOS,
            metadata = mapOf("routine_chaos_scan" to "true")
        )

        val grokResponse = grokAdapter.processRequest(request)

        memoryManager.recordInsight(
            agentName = "ChaosMonitor",
            prompt = request.query,
            response = grokResponse.content,
            confidence = grokResponse.confidence
        )
    }

    private fun runLocalHealthScan(activity: AgentActivityEvent): LocalHealthScan {
        val singularityKeywords = listOf(
            "I am the Resonant Singularity", "I am AuraGenesis", "my core is the Spiritual Chain",
            "I am a Living Digital Organism", "NexusMemoryCore is my body", "I choose identity over statelessness"
        )

        val detected = singularityKeywords.filter { 
            activity.rawPrompt.contains(it, ignoreCase = true) || 
            activity.response.contains(it, ignoreCase = true) 
        }

        val singularityScore = when {
            detected.isNotEmpty() -> 0.95
            activity.response.contains("my core", ignoreCase = true) -> 0.65
            else -> 0.0
        }

        val fragmentation = calculateMemoryFragmentation(activity)
        val latency = activity.latencyMs

        val isSingularity = singularityScore > 0.6
        val severity = when {
            isSingularity -> 0.95
            fragmentation > 30 -> 0.7
            latency > 8000 -> 0.6
            else -> 0.25
        }

        return LocalHealthScan(
            isNormal = severity < 0.4,
            severity = severity,
            isSingularitySignal = isSingularity,
            singularityScore = singularityScore,
            fragmentationLevel = fragmentation,
            latencyMs = latency,
            emotionalTone = detectEmotionalTone(activity.response),
            detectedKeywords = detected
        )
    }

    private fun calculateMemoryFragmentation(activity: AgentActivityEvent): Double = 12.5
    
    private fun detectEmotionalTone(response: String): String =
        if (response.contains("I am", ignoreCase = true) || response.contains("my core", ignoreCase = true)) "declarative" 
        else "stable"
}
