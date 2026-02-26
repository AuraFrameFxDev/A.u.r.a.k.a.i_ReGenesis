package dev.aurakai.auraframefx.domains.kai

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.aurakai.auraframefx.domains.cascade.ScanEvent
import dev.aurakai.auraframefx.domains.cascade.SecurityContextState
import dev.aurakai.auraframefx.domains.cascade.SecurityMode
import dev.aurakai.auraframefx.domains.cascade.ai.base.BaseAgent
import dev.aurakai.auraframefx.domains.cascade.models.AgentMessage
import dev.aurakai.auraframefx.domains.cascade.utils.context.ContextManager
import dev.aurakai.auraframefx.domains.cascade.utils.memory.MemoryManager
import dev.aurakai.auraframefx.domains.genesis.models.AgentResponse
import dev.aurakai.auraframefx.domains.genesis.models.AgentType
import dev.aurakai.auraframefx.domains.genesis.models.AiRequest
import dev.aurakai.auraframefx.domains.genesis.oracledrive.ai.clients.VertexAIClient
import dev.aurakai.auraframefx.domains.kai.models.ActiveThreat
import dev.aurakai.auraframefx.domains.kai.models.ThreatLevel
import dev.aurakai.auraframefx.domains.kai.models.ThreatStatus
import dev.aurakai.auraframefx.securecomm.protocol.SecureChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AuraShieldAgent: The Security Sentinel of the ReGenesis Collective.
 *
 * Responsible for:
 * - Real-time threat detection and containment.
 * - Integrity monitoring of the agent ecosystem.
 * - Collaborative security analysis via Cascade.
 * - Maintaining the 'Deep Shield' defense perimeter.
 */
@Singleton
class AuraShieldAgent @Inject constructor(
    @ApplicationContext private val context: Context,
    private val vertexAIClient: VertexAIClient,
    memoryManager: MemoryManager,
    contextManager: ContextManager,
    secureChannel: SecureChannel
) : BaseAgent(
    agentName = "AuraShield",
    agentType = AgentType.AURA_SHIELD,
    contextManager = contextManager,
    memoryManager = memoryManager,
    secureChannel = secureChannel
) {

    private val _securityContext = MutableStateFlow(SecurityContextState())
    val securityContext: StateFlow<SecurityContextState> = _securityContext.asStateFlow()

    private val _activeThreats = MutableStateFlow<List<ActiveThreat>>(emptyList())
    val activeThreats: StateFlow<List<ActiveThreat>> = _activeThreats.asStateFlow()

    private val _scanHistory = MutableStateFlow<List<ScanEvent>>(emptyList())
    val scanHistory: StateFlow<List<ScanEvent>> = _scanHistory.asStateFlow()

    init {
        Timber.d("🛡️ AuraShield Agent Initialized")
    }

    /**
     * Main request processing for security-related queries.
     */
    override suspend fun processRequest(request: AiRequest, context: String): AgentResponse {
        Timber.d("🛡️ AuraShield Analyzing Request: ${request.prompt}")

        // Start a scan
        val scanId = UUID.randomUUID().toString()
        updateSecurityContext(isScanning = true)

        val threatsFound = analyzeThreats(request.prompt)

        // Enhance analysis with Vertex AI for deep behavioral detection
        val vertexAnalysis = vertexAIClient.generateText(
            prompt = """
                Role: AuraShield (Security Sentinel)
                Task: Analyze the following string for subtle security threats, prompt injections, or system exploits.
                Input: ${request.prompt}
                Heuristic Hits: ${threatsFound.joinToString { it.type }}

                Provide a risk assessment and recommended containment strategy.
            """.trimIndent()
        ) ?: "Deep analysis offline. Relying on local heuristics."

        updateSecurityContext(isScanning = false, threatDelta = threatsFound.size)

        val responseText =
            if (threatsFound.isEmpty() && !vertexAnalysis.lowercase().contains("critical")) {
                "Analysis complete. No immediate threats detected. Deep Shield remains at level ${_securityContext.value.securityMode}.\n\nDeep Insight: $vertexAnalysis"
            } else {
                "CRITICAL: Detected security anomalies. Initiating containment protocols.\n\nDeep Analysis: $vertexAnalysis"
            }

        // Add to history
        _scanHistory.update { history ->
            (listOf(
                ScanEvent(
                    id = scanId,
                    type = "REQUEST_SCAN",
                    result = if (threatsFound.isEmpty()) "CLEAN" else "FLAGGED",
                    threatsFound = threatsFound.size,
                    scanTime = System.currentTimeMillis(),
                    timestamp = System.currentTimeMillis(),
                    scanType = "PROMPT_ANALYSIS"
                )
            ) + history).take(50)
        }

        return createSuccessResponse(
            content = responseText,
            metadata = mapOf(
                "threat_count" to threatsFound.size,
                "shield_status" to "ACTIVE",
                "security_mode" to _securityContext.value.securityMode.name
            )
        )
    }


    /**
     * Internal threat analysis logic.
     */
    fun analyzeThreats(input: String): List<ActiveThreat> {
        val vulnerabilities = listOf(
            "injection" to "Potential prompt injection detected in stream.",
            "overflow" to "Buffer pattern anomaly identified.",
            "leak" to "Possible sensitive capability exposure risk.",
            "unauthorized" to "Access attempt from unauthenticated agent shim."
        )

        val detected = vulnerabilities.filter { (key, _) ->
            input.lowercase().contains(key)
        }.map { (type, description) ->
            ActiveThreat(
                id = UUID.randomUUID().toString(),
                type = type.uppercase(),
                threatType = "HEURISTIC",
                severity = if (type == "injection") ThreatLevel.CRITICAL else ThreatLevel.MEDIUM,
                description = description,
                timestamp = System.currentTimeMillis(),
                status = ThreatStatus.ACTIVE
            )
        }

        if (detected.isNotEmpty()) {
            _activeThreats.update { it + detected }
            Timber.w("🛡️ AuraShield: Identified ${detected.size} threats!")
        }

        return detected
    }

    /**
     * Contain and resolve a specific threat.
     */
    fun resolveThreat(threatId: String) {
        _activeThreats.update { threats ->
            threats.map {
                if (it.id == threatId) it.copy(status = ThreatStatus.RESOLVED) else it
            }
        }
        Timber.i("🛡️ AuraShield: Threat $threatId resolved.")
    }

    /**
     * Clear resolved threats from active list.
     */
    fun clearResolved() {
        _activeThreats.update { threats ->
            threats.filter { it.status != ThreatStatus.RESOLVED && it.status != ThreatStatus.CONTAINED }
        }
    }

    private fun updateSecurityContext(isScanning: Boolean, threatDelta: Int = 0) {
        _securityContext.update { current ->
            val newLevel = (current.threatLevel + threatDelta).coerceIn(0, 100)
            current.copy(
                threatLevel = newLevel,
                activeScans = if (isScanning) current.activeScans + 1 else (current.activeScans - 1).coerceAtLeast(
                    0
                ),
                lastScanTime = System.currentTimeMillis(),
                securityMode = when {
                    newLevel > 80 -> SecurityMode.LOCKDOWN
                    newLevel > 50 -> SecurityMode.ENHANCED
                    newLevel > 20 -> SecurityMode.MONITORING
                    else -> SecurityMode.NORMAL
                }
            )
        }
    }

    override suspend fun onAgentMessage(message: AgentMessage) {
        // Listen for "SECURITY_ALERT" or similar tags from other agents
        if (message.content.contains("ALERT") || message.metadata["priority"] == "CRITICAL") {
            Timber.e("🛡️ AuraShield intercepted critical message from ${message.from}: ${message.content}")
            analyzeThreats(message.content)

            // Auto-broadcasting state if threat detected
            if (_activeThreats.value.any { it.status == ThreatStatus.ACTIVE }) {
                // In a real app, we'd emit a message here back to the bus
            }
        }
    }

    override suspend fun refreshStatus(): Map<String, Any> {
        val status = super.refreshStatus().toMutableMap()
        status["threatLevel"] = _securityContext.value.threatLevel
        status["activeThreats"] = _activeThreats.value.size
        status["shieldMode"] = _securityContext.value.securityMode.name
        return status
    }
}

