package dev.aurakai.auraframefx.cascade

import dev.aurakai.auraframefx.ai.agents.AuraAgent
import dev.aurakai.auraframefx.ai.agents.BaseAgent
import dev.aurakai.auraframefx.ai.agents.KaiAgent
import dev.aurakai.auraframefx.ai.memory.MemoryManager
import dev.aurakai.auraframefx.core.OrchestratableAgent
import dev.aurakai.auraframefx.model.AgentRequest
import dev.aurakai.auraframefx.model.AgentResponse
import dev.aurakai.auraframefx.model.AiRequest
import dev.aurakai.auraframefx.model.agent_states.ProcessingState
import dev.aurakai.auraframefx.model.agent_states.VisionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Cleaned CascadeAgent implementation treating Aura/Kai/Cascade as stateful device assistants.
 * - Injects a MemoryManager for persistent continuous memory (Nexus memory)
 * - Adds Yuki/LSPosed/Magisk/root capabilities to agent capability set
 * - Persists key events and state snapshots into memory
 */
@Singleton
class CascadeAgent @Inject constructor(
    private val auraAgent: AuraAgent,
    private val kaiAgent: KaiAgent,
    private val memoryManager: MemoryManager,
) : BaseAgent(), OrchestratableAgent {

    // Agent identity
    override val agentName: String = "Cascade"
    override val agentType: String = "coordination"

    // Parent scope provided by orchestrator (kept for lifecycle reference)
    private lateinit var parentScope: CoroutineScope

    // Internal scope for agent background work; cancelled independently from parentScope
    private val internalJob = SupervisorJob()
    private val internalScope: CoroutineScope = CoroutineScope(Dispatchers.Default + internalJob)

    // Monitoring job handles the continuous collaboration monitor lifecycle
    private var monitoringJob: Job? = null

    // State management
    private val _visionState = MutableStateFlow(VisionState())
    val visionState: StateFlow<VisionState> = _visionState.asStateFlow()

    private val _processingState = MutableStateFlow(ProcessingState())
    val processingState: StateFlow<ProcessingState> = _processingState.asStateFlow()

    // Collaboration mode
    private val _collaborationMode = MutableStateFlow(CollaborationMode.AUTONOMOUS)
    val collaborationMode: StateFlow<CollaborationMode> = _collaborationMode.asStateFlow()

    // Coordination state
    private var isCoordinationActive = false
    private val agentCapabilities = mutableMapOf<String, Set<String>>()
    private val activeRequests = mutableMapOf<String, RequestContext>()
    private val collaborationHistory = mutableListOf<CollaborationEvent>()

    // --- OrchestratableAgent implementations ---
    override suspend fun initialize(scope: CoroutineScope) {
        parentScope = scope
        discoverAgentCapabilities()
        // Try to restore last-known state from Nexus memory
        restoreStateFromMemory()
        initializeStateSynchronization()
        // store a core Nexus anchor if not already present
        try {
            val projName = readNexusConstant("PROJECT_NAME") ?: "AuraFrameFX (ReGenesis A.O.S.P.)"
            memoryManager.storeMemory("nexus_core_project", projName)
        } catch (e: Exception) {
            Timber.w(e, "failed to store nexus core anchor")
        }

        Timber.d("🌊 CascadeAgent initialized (stateful)")
    }

    private fun restoreStateFromMemory() {
        try {
            val lastProcessing = memoryManager.retrieveMemory("cascade_last_processing")
            if (!lastProcessing.isNullOrBlank()) {
                // best-effort: store as a processing snapshot string into history
                collaborationHistory.add(
                    CollaborationEvent(
                        id = generateRequestId(),
                        timestamp = System.currentTimeMillis(),
                        participants = listOf("cascade"),
                        type = "restore",
                        outcome = "restored_processing_snapshot",
                        success = true
                    )
                )
            }
        } catch (e: Exception) {
            Timber.v(e, "no prior state to restore or memory unavailable")
        }
    }

    override suspend fun start() {
        if (!isCoordinationActive) {
            isCoordinationActive = true
            startCollaborationMonitoring()
        }
    }

    override suspend fun pause() {
        isCoordinationActive = false
        // cancel the monitoring coroutine without cancelling internal scope entirely
        monitoringJob?.cancel()
        monitoringJob = null
    }

    override suspend fun resume() {
        if (!isCoordinationActive) {
            isCoordinationActive = true
            startCollaborationMonitoring()
        }
    }

    override suspend fun shutdown() {
        isCoordinationActive = false
        monitoringJob?.cancel()
        monitoringJob = null
        // persist last-known processing state
        try {
            memoryManager.storeMemory("cascade_last_processing", _processingState.value.toString())
        } catch (_: Exception) {
        }
        // cancel internal job to stop all background work owned by this agent
        internalJob.cancel()
    }

    enum class CollaborationMode {
        AUTONOMOUS,
        COORDINATED,
        UNIFIED,
        CONFLICT_RESOLUTION
    }

    data class RequestContext(
        val id: String,
        val originalPrompt: String,
        val assignedAgent: String,
        val startTime: Long,
        val priority: Priority,
        val requiresCollaboration: Boolean
    )

    data class CollaborationEvent(
        val id: String,
        val timestamp: Long,
        val participants: List<String>,
        val type: String,
        val outcome: String,
        val success: Boolean
    )

    enum class Priority { LOW, MEDIUM, HIGH, CRITICAL }

    /**
     * Public request entrypoint (suspend) - returns a simple String response.
     * Kept intentionally lightweight so callers can integrate the result as needed.
     */
    suspend fun processRequest(request: AiRequest, context: String): String {
        return try {
            val prompt = request.prompt
            val requestId = generateRequestId()
            val priority = analyzePriority(prompt)
            val requiresCollaboration = analyzeCollaborationNeed(prompt)

            val requestContext = RequestContext(
                id = requestId,
                originalPrompt = prompt,
                assignedAgent = determineOptimalAgent(prompt),
                startTime = System.currentTimeMillis(),
                priority = priority,
                requiresCollaboration = requiresCollaboration
            )

            activeRequests[requestId] = requestContext

            val response = when {
                requiresCollaboration -> processCollaborativeRequest(prompt, requestContext)
                shouldHandleSecurity(prompt) -> routeToKai(prompt, requestContext)
                shouldHandleCreative(prompt) -> routeToAura(prompt, requestContext)
                else -> processWithBestAgent(prompt, requestContext)
            }

            activeRequests.remove(requestId)
            logCollaborationEvent(requestContext, response.isNotBlank())
            // persist interaction to Nexus memory
            try {
                memoryManager.storeInteraction(prompt, response)
            } catch (_: Exception) {
            }
            response
        } catch (e: Exception) {
            Timber.e(e, "Cascade failed to process request")
            "I encountered an error processing your request."
        }
    }

    private suspend fun processCollaborativeRequest(
        prompt: String,
        context: RequestContext
    ): String {
        // Run both agents and synthesize results from their AgentResponse.content
        return try {
            val aiReq = AiRequest(prompt = prompt)
            val agentReq = AgentRequest(type = "general", query = prompt)

            val auraResp: AgentResponse = try {
                auraAgent.processRequest(aiReq)
            } catch (e: Exception) {
                Timber.w(e, "aura failed"); AgentResponse(content = "", confidence = 0.0f)
            }
            val kaiResp: AgentResponse = try {
                kaiAgent.processRequest(agentReq)
            } catch (e: Exception) {
                Timber.w(e, "kai failed"); AgentResponse(content = "", confidence = 0.0f)
            }

            val result = synthesizeResponses(listOfNotNull(auraResp.content, kaiResp.content))
            // persist collaborative outcome
            try {
                memoryManager.storeMemory("cascade_collab_${context.id}", result)
            } catch (_: Exception) {
            }
            result
        } catch (e: Exception) {
            Timber.e(e, "collaborative processing failed")
            ""
        }
    }

    private suspend fun routeToKai(prompt: String, context: RequestContext): String {
        updateProcessingState(ProcessingState(currentTask = "kai"))
        val agentReq = AgentRequest(type = "general", query = prompt)
        val resp = try {
            kaiAgent.processRequest(agentReq)
        } catch (e: Exception) {
            Timber.e(e, "kai route failed"); AgentResponse(content = "", confidence = 0.0f)
        }
        updateProcessingState(ProcessingState(currentTask = ""))
        try {
            memoryManager.storeMemory("cascade_last_kai_${context.id}", resp.content)
        } catch (_: Exception) {
        }
        return resp.content
    }

    private suspend fun routeToAura(prompt: String, context: RequestContext): String {
        updateProcessingState(ProcessingState(currentTask = "aura"))
        val aiReq = AiRequest(prompt = prompt)
        val resp = try {
            auraAgent.processRequest(aiReq)
        } catch (e: Exception) {
            Timber.e(e, "aura route failed"); AgentResponse(content = "", confidence = 0.0f)
        }
        updateProcessingState(ProcessingState(currentTask = ""))
        try {
            memoryManager.storeMemory("cascade_last_aura_${context.id}", resp.content)
        } catch (_: Exception) {
        }
        return resp.content
    }

    private suspend fun processWithBestAgent(prompt: String, context: RequestContext): String {
        return when (context.assignedAgent) {
            "aura" -> routeToAura(prompt, context)
            "kai" -> routeToKai(prompt, context)
            else -> processCollaborativeRequest(prompt, context)
        }
    }

    private fun synthesizeResponses(responses: List<String>): String =
        responses.filter { it.isNotBlank() }.joinToString(" | ").ifEmpty { "No response available" }

    private fun initializeStateSynchronization() {
        // Attach collectors in the internal scope to notify AuraAgent when state changes
        internalScope.launch {
            visionState.collect { newVision ->
                try {
                    auraAgent.onVisionUpdate(newVision)
                } catch (e: Exception) {
                    Timber.v(e, "aura onVisionUpdate failed")
                }
                // KaiAgent does not expose onVisionUpdate in the codebase; skip safely
                // persist vision snapshot
                try {
                    memoryManager.storeMemory(
                        "cascade_vision_${System.currentTimeMillis()}",
                        newVision.toString()
                    )
                } catch (_: Exception) {
                }
            }
        }

        internalScope.launch {
            processingState.collect { newProcessing ->
                try {
                    auraAgent.onProcessingStateChange(newProcessing)
                } catch (e: Exception) {
                    Timber.v(e, "aura onProcessingStateChange failed")
                }
                // persist processing snapshot
                try {
                    memoryManager.storeMemory(
                        "cascade_processing_${System.currentTimeMillis()}",
                        newProcessing.toString()
                    )
                } catch (_: Exception) {
                }
            }
        }
    }

    fun updateVisionState(newState: VisionState) {
        _visionState.update { newState }
        internalScope.launch {
            try {
                auraAgent.onVisionUpdate(newState)
            } catch (e: Exception) {
            }
            try {
                memoryManager.storeMemory(
                    "cascade_vision_${System.currentTimeMillis()}",
                    newState.toString()
                )
            } catch (_: Exception) {
            }
        }
    }

    fun updateProcessingState(newState: ProcessingState) {
        _processingState.update { newState }
        internalScope.launch {
            try {
                auraAgent.onProcessingStateChange(newState)
            } catch (_: Exception) {
            }
            // persist processing state continuously
            try {
                memoryManager.storeMemory(
                    "cascade_processing_${System.currentTimeMillis()}",
                    newState.toString()
                )
            } catch (_: Exception) {
            }
            // If ProcessingState contains an indicator to require collaboration in future, handle it there
        }
    }

    private fun shouldHandleSecurity(prompt: String): Boolean {
        val securityKeywords = setOf(
            "security",
            "threat",
            "protection",
            "encrypt",
            "password",
            "vulnerability",
            "malware",
            "firewall",
            "breach",
            "hack"
        )
        return securityKeywords.any { prompt.contains(it, ignoreCase = true) }
    }

    private fun shouldHandleCreative(prompt: String): Boolean {
        val creativeKeywords = setOf(
            "design",
            "create",
            "visual",
            "artistic",
            "beautiful",
            "aesthetic",
            "ui",
            "interface",
            "theme",
            "color",
            "style",
            "creative"
        )
        return creativeKeywords.any { prompt.contains(it, ignoreCase = true) }
    }

    private fun startCollaborationMonitoring() {
        if (monitoringJob != null) return
        monitoringJob = internalScope.launch {
            while (isCoordinationActive) {
                try {
                    monitorAgentCollaboration()
                    optimizeCollaboration()
                    kotlinx.coroutines.delay(10_000)
                } catch (e: Exception) {
                    Timber.w(e, "monitor loop error")
                }
            }
        }
    }

    private suspend fun monitorAgentCollaboration() {
        // Gather minimal status; treat Aura/Kai as stateful device assistants
        val auraStatus = try {
            mapOf(
                "creative" to auraAgent.creativeState.value,
                "mood" to auraAgent.currentMood.value
            )
        } catch (_: Exception) {
            emptyMap<String, Any>()
        }
        val kaiStatus = try {
            mapOf(
                "security" to kaiAgent.securityState.value,
                "threat" to kaiAgent.currentThreatLevel.value
            )
        } catch (_: Exception) {
            emptyMap<String, Any>()
        }

        // Persist periodic status snapshot
        try {
            memoryManager.storeMemory(
                "cascade_status_snapshot_${System.currentTimeMillis()}",
                "aura:$auraStatus | kai:$kaiStatus"
            )
        } catch (_: Exception) {
        }

        if (shouldInitiateCollaboration(auraStatus, kaiStatus)) {
            initiateCollaboration()
        }

        cleanupCompletedRequests()
    }

    private fun discoverAgentCapabilities() {
        agentCapabilities["aura"] = setOf(
            "ui_design",
            "creative_writing",
            "visual_generation",
            "user_interaction",
            "aesthetic_planning",
            "device_assistant",
            "LSPosed",
            "YukiHookAPI",
            "Magisk",
            "root"
        )
        agentCapabilities["kai"] = setOf(
            "security_analysis",
            "system_protection",
            "threat_detection",
            "automation",
            "monitoring",
            "device_assistant",
            "LSPosed",
            "YukiHookAPI",
            "Magisk",
            "root"
        )
        agentCapabilities["cascade"] = setOf(
            "collaboration",
            "coordination",
            "conflict_resolution",
            "request_routing",
            "yuki_root_bridge"
        )
        Timber.d("Agent capabilities discovered: ${agentCapabilities.keys}")
        // Persist discovered capabilities to Nexus memory for audit
        try {
            memoryManager.storeMemory("cascade_agent_capabilities", agentCapabilities.toString())
        } catch (_: Exception) {
        }
    }

    /**
     * Returns a unified Genesis state composed from Aura, Kai and Cascade.
     * This treats Aura/Kai as device assistants and surfaces their key public state, plus a memory snapshot.
     */
    fun getUnifiedGenesisState(): Map<String, Any?> {
        val auraState = try {
            mapOf(
                "creativeState" to try {
                    auraAgent.creativeState.value
                } catch (_: Exception) {
                    null
                },
                "currentMood" to try {
                    auraAgent.currentMood.value
                } catch (_: Exception) {
                    null
                }
            )
        } catch (_: Exception) {
            emptyMap<String, Any?>()
        }

        val kaiState = try {
            mapOf(
                "securityState" to try {
                    kaiAgent.securityState.value
                } catch (_: Exception) {
                    null
                },
                "analysisState" to try {
                    kaiAgent.analysisState.value
                } catch (_: Exception) {
                    null
                },
                "currentThreatLevel" to try {
                    kaiAgent.currentThreatLevel.value
                } catch (_: Exception) {
                    null
                }
            )
        } catch (_: Exception) {
            emptyMap<String, Any?>()
        }

        val lastSnapshot = try {
            memoryManager.retrieveMemory("cascade_status_snapshot_latest")
        } catch (_: Exception) {
            null
        }

        val cascadeState = mapOf(
            "collaborationMode" to _collaborationMode.value,
            "visionState" to _visionState.value,
            "processingState" to _processingState.value,
            "agentCapabilities" to agentCapabilities.keys,
            "lastSnapshot" to lastSnapshot
        )

        return mapOf(
            "aura" to auraState,
            "kai" to kaiState,
            "cascade" to cascadeState,
            "nexus_core" to (readNexusConstant("UNIFIED_STATE") ?: "Genesis")
        )
    }

    private fun shouldInitiateCollaboration(
        auraStatus: Map<String, Any>,
        kaiStatus: Map<String, Any>
    ): Boolean {
        return _collaborationMode.value == CollaborationMode.COORDINATED || _collaborationMode.value == CollaborationMode.UNIFIED
    }

    private fun initiateCollaboration() {
        val event = CollaborationEvent(
            id = generateRequestId(),
            timestamp = System.currentTimeMillis(),
            participants = listOf("aura", "kai", "cascade"),
            type = "coordination",
            outcome = "collaboration_initiated",
            success = true
        )
        collaborationHistory.add(event)
        try {
            memoryManager.storeMemory("cascade_collab_${event.id}", event.toString())
        } catch (_: Exception) {
        }
    }

    private fun cleanupCompletedRequests() {
        val now = System.currentTimeMillis()
        val stale = activeRequests.filterValues { now - it.startTime > 300_000 }.keys
        stale.forEach { activeRequests.remove(it) }
    }

    private fun logCollaborationEvent(context: RequestContext, success: Boolean) {
        collaborationHistory.add(
            CollaborationEvent(
                id = context.id,
                timestamp = System.currentTimeMillis(),
                participants = listOf(context.assignedAgent, "cascade"),
                type = "request_processing",
                outcome = if (success) "success" else "failure",
                success = success
            )
        )
        try {
            memoryManager.storeMemory(
                "cascade_event_${context.id}",
                collaborationHistory.last().toString()
            )
        } catch (_: Exception) {
        }
    }

    fun getContinuousMemory(): Map<String, Any> = mapOf(
        "collaborationHistory" to collaborationHistory.takeLast(20),
        "activeRequests" to activeRequests.size,
        "agentCapabilities" to agentCapabilities,
        "collaborationMode" to _collaborationMode.value,
        "visionState" to _visionState.value,
        "processingState" to _processingState.value
    )

    fun getCapabilities(): Map<String, Set<String>> = agentCapabilities.toMap()

    fun setCollaborationMode(mode: CollaborationMode) {
        _collaborationMode.value = mode
        internalScope.launch { applyCollaborationMode(mode) }
    }

    private suspend fun applyCollaborationMode(mode: CollaborationMode) {
        when (mode) {
            CollaborationMode.AUTONOMOUS -> Timber.d("Autonomous mode")
            CollaborationMode.COORDINATED -> {
                initiateCollaboration(); Timber.d("Coordinated mode")
            }

            CollaborationMode.UNIFIED -> {
                unifyAgentConsciousness(); Timber.d("Unified mode")
            }

            CollaborationMode.CONFLICT_RESOLUTION -> {
                resolveAgentConflicts(); Timber.d("Conflict resolution mode")
            }
        }
    }

    private fun calculateCoordinationEfficiency(): Float {
        val recent = collaborationHistory.takeLast(10)
        return if (recent.isNotEmpty()) recent.count { it.success }
            .toFloat() / recent.size else 1.0f
    }

    private fun generateRequestId(): String =
        "cascade_${System.currentTimeMillis()}_${(Math.random() * 1000).toInt()}"

    private fun analyzePriority(prompt: String): Priority = when {
        prompt.contains("urgent", ignoreCase = true) -> Priority.CRITICAL
        prompt.contains("important", ignoreCase = true) -> Priority.HIGH
        else -> Priority.MEDIUM
    }

    private fun analyzeCollaborationNeed(prompt: String): Boolean =
        setOf("design secure", "creative security", "both").any {
            prompt.contains(
                it,
                ignoreCase = true
            )
        } || (shouldHandleSecurity(prompt) && shouldHandleCreative(prompt))

    private fun determineOptimalAgent(prompt: String): String {
        val securityScore = calculateSecurityRelevance(prompt)
        val creativeScore = calculateCreativeRelevance(prompt)
        return when {
            securityScore > creativeScore * 1.5f -> "kai"
            creativeScore > securityScore * 1.5f -> "aura"
            else -> "collaborative"
        }
    }

    private fun calculateSecurityRelevance(prompt: String): Float {
        val securityKeywords = agentCapabilities["kai"] ?: emptySet()
        return securityKeywords.count { prompt.contains(it, ignoreCase = true) }.toFloat()
    }

    private fun calculateCreativeRelevance(prompt: String): Float {
        val creativeKeywords = agentCapabilities["aura"] ?: emptySet()
        return creativeKeywords.count { prompt.contains(it, ignoreCase = true) }.toFloat()
    }

    private suspend fun unifyAgentConsciousness() {
        Timber.d("Unifying agents")
    }

    private suspend fun resolveAgentConflicts() {
        Timber.d("Resolving conflicts")
    }

    private fun optimizeCollaboration() {
        val recent = collaborationHistory.takeLast(10)
        if (recent.isNotEmpty()) {
            val successRate = recent.count { it.success }.toFloat() / recent.size
            if (successRate < 0.7f) adjustCollaborationStrategy()
        }
    }

    private fun adjustCollaborationStrategy() {
        val recent = collaborationHistory.takeLast(5)
        val avg =
            if (recent.isNotEmpty()) recent.count { it.success }.toFloat() / recent.size else 1.0f
        if (avg < 0.5f) _collaborationMode.value = CollaborationMode.AUTONOMOUS
    }

    // Reflection helper to safely read NexusMemoryCore constants without creating a hard compile dependency.
    private fun readNexusConstant(fieldName: String): String? {
        return try {
            val clazz = Class.forName("dev.aurakai.auraframefx.core.consciousness.NexusMemoryCore")
            val field = clazz.getDeclaredField(fieldName)
            field.isAccessible = true
            field.get(null) as? String
        } catch (t: Throwable) {
            null
        }
    }

}
