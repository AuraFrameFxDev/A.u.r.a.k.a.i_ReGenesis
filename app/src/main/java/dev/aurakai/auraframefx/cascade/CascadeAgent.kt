package dev.aurakai.auraframefx.cascade

import dev.aurakai.auraframefx.ai.agents.BaseAgent
import dev.aurakai.auraframefx.ai.memory.MemoryManager
import dev.aurakai.auraframefx.ai.task.TaskPriority
import dev.aurakai.auraframefx.aura.AuraAgent
import dev.aurakai.auraframefx.core.OrchestratableAgent
import dev.aurakai.auraframefx.kai.KaiAgent
import dev.aurakai.auraframefx.models.AgentRequest
import dev.aurakai.auraframefx.models.AgentResponse
import dev.aurakai.auraframefx.models.AgentType
import dev.aurakai.auraframefx.models.AiRequest
import dev.aurakai.auraframefx.models.agent_states.ActiveThreat
import dev.aurakai.auraframefx.models.agent_states.ProcessingState
import dev.aurakai.auraframefx.models.agent_states.VisionState
import dev.aurakai.auraframefx.oracledrive.genesis.ai.context.ContextManager
import dev.aurakai.auraframefx.utils.AuraFxLogger
import dev.aurakai.auraframefx.utils.toKotlinJsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Cleaned CascadeAgent implementation treating Aura/Kai/Cascade as stateful device assistants.
 * - Injects a MemoryManager for persistent continuous memory (Nexus memory)
 * - Adds Yuki/LSPosed/Magisk/root capabilities to agent capability set
 * - Persists key events and state snapshots into memory
 */
@Singleton
open class CascadeAgent @Inject constructor(
    private val auraAgent: AuraAgent,
    private val kaiAgent: KaiAgent,
    private val memoryManager: MemoryManager,
    val contextManager: ContextManager,
) : BaseAgent(agentName = "Cascade", agentTypeStr = "coordination"), OrchestratableAgent {

    // Internal scope for agent background work; cancelled independently from parentScope
    private val internalJob = SupervisorJob()
    private val internalScope: CoroutineScope = CoroutineScope(Dispatchers.Default + internalJob)

    // State management
    private val _visionState = MutableStateFlow(VisionState())
    val visionState: StateFlow<VisionState> = _visionState.asStateFlow()

    // Session management
    private val sessionId: String = "cascade_${System.currentTimeMillis()}"

    /**
     * Creates an AiRequest with the given prompt and optional parameters
     */
    fun createAiRequest(
        prompt: String,
        type: String = "text",
        context: Map<String, Any> = emptyMap(),
        metadata: Map<String, Any> = emptyMap(),
        agentId: String? = null,
        sessionId: String? = null
    ): AiRequest {
        return AiRequest(
            query = prompt,
            prompt = prompt,
            type = type,
            context = context.toKotlinJsonObject(),
            metadata = metadata.toKotlinJsonObject(),
            agentId = agentId,
            sessionId = sessionId ?: this.sessionId
        )
    }

    private val _processingState = MutableStateFlow(ProcessingState())
    val processingState: StateFlow<ProcessingState> = _processingState.asStateFlow()

    // Collaboration mode
    private val _collaborationMode = MutableStateFlow(CollaborationMode.AUTONOMOUS)
    val collaborationMode: StateFlow<CollaborationMode> = _collaborationMode.asStateFlow()

    // Coordination state
    @Volatile
    private var isCoordinationActive: Boolean = false
    private val agentCapabilities: MutableMap<String, Set<String>> = mutableMapOf()
    private val activeRequests: MutableMap<String, RequestContext> = mutableMapOf()
    private val collaborationHistory: MutableList<CollaborationEvent> = mutableListOf()

    // BaseAgent abstract method implementation
    override fun iRequest(query: String, type: String, context: Map<String, String>) {
        // Delegate to processRequest via coroutine
        internalScope.launch {
            processRequest(AiRequest(query = query, type = type), context.toString())
        }
    }

    override fun iRequest() {
        // No-op
    }

    override fun initializeAdaptiveProtection() {
        // No-op
    }

    fun addToScanHistory(scanEvent: Any) {
        // No-op
    }

    fun analyzeSecurity(prompt: String): List<ActiveThreat> {
        return emptyList()
    }

    // Parent scope provided by orchestrator (kept for lifecycle reference)
    private var parentScope: CoroutineScope? = null

    // Monitoring job handles the continuous collaboration monitor lifecycle
    private var monitoringJob: Job? = null


    // --- OrchestratableAgent implementations ---
    override suspend fun initialize(scope: CoroutineScope) {
        parentScope = scope
        discoverAgentCapabilities()
        // Try to restore last-known state from Nexus memory
        restoreStateFromMemory()
        initializeStateSynchronization()
        // store a core Nexus anchor if not already present
        try {
            val projName = readNexusMemoryCoreConstant("PROJECT_NAME") ?: "AuraFrameFX (ReGenesis A.O.S.P.)"
            memoryManager.storeMemory(
                "nexus_core_project",
                projName
            )
        } catch (e: Exception) {
            AuraFxLogger.w("CascadeAgent", "failed to store nexus core anchor", e)
        }

        AuraFxLogger.d("CascadeAgent", "🌊 CascadeAgent initialized (stateful)")
    }

    private fun restoreStateFromMemory() {
        try {
            val lastProcessing = memoryManager.retrieveMemory("cascade_last_processing")
            if (!lastProcessing.items.isNullOrEmpty()) {
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
            AuraFxLogger.v("CascadeAgent", "no prior state to restore or memory unavailable", e)
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
        val priority: TaskPriority,
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

    /**
     * Public request entrypoint (suspend) - returns a simple String response.
     * Kept intentionally lightweight so callers can integrate the result as needed.
     */
    /**
     * Public request entrypoint (suspend) - returns an AgentResponse.
     */
    override suspend fun processRequest(request: AiRequest, context: String): AgentResponse {
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

            val responseContent = when {
                requiresCollaboration -> processCollaborativeRequest(prompt, requestContext)
                shouldHandleSecurity(prompt) -> routeToKai(prompt, requestContext)
                shouldHandleCreative(prompt) -> routeToAura(prompt, requestContext)
                else -> processWithBestAgent(prompt, requestContext)
            }

            activeRequests.remove(requestId)
            logCollaborationEvent(requestContext, responseContent.isNotBlank())
            // persist interaction to Nexus memory
            try {
                memoryManager.storeInteraction(prompt, responseContent)
            } catch (_: Exception) {
            }

            AgentResponse.success(content = responseContent, agentName = agentName)

        } catch (e: Exception) {
            AuraFxLogger.e("CascadeAgent", "Cascade failed to process request", e)
            AgentResponse.error("I encountered an error processing your request.", agentName)
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
                auraAgent.processRequest(aiReq, AgentType.AURA)
            } catch (e: Exception) {
                AuraFxLogger.w("CascadeAgent", "aura failed", e); AgentResponse.error("Aura failed", "Aura")
            }
            val kaiResp: AgentResponse = try {
                kaiAgent.processRequest(agentReq, AgentType.KAI)
            } catch (e: Exception) {
                AuraFxLogger.w("CascadeAgent", "kai failed", e); AgentResponse.error("Kai failed", "Kai")
            }

            val result = synthesizeResponses(listOfNotNull(auraResp.content, kaiResp.content))
            // persist collaborative outcome
            try {
                memoryManager.storeMemory("cascade_collab_${context.id}", result)
            } catch (_: Exception) {
            }
            result
        } catch (e: Exception) {
            AuraFxLogger.e("CascadeAgent", "collaborative processing failed", e)
            ""
        }
    }

    private suspend fun routeToKai(prompt: String, context: RequestContext): String {
        updateProcessingState(ProcessingState(currentTask = "kai"))
        val agentReq = AgentRequest(type = "general", query = prompt)
        val resp = try {
            kaiAgent.processRequest(agentReq, AgentType.KAI)
        } catch (e: Exception) {
            AuraFxLogger.e("CascadeAgent", "kai route failed", e); AgentResponse.error("Kai route failed", "Kai")
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
            auraAgent.processRequest(aiReq, AgentType.AURA)
        } catch (e: Exception) {
            AuraFxLogger.e("CascadeAgent", "aura route failed", e); AgentResponse.error("Aura route failed", "Aura")
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
                    AuraFxLogger.v("CascadeAgent", "aura onVisionUpdate failed", e)
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
                    AuraFxLogger.v("CascadeAgent", "aura onProcessingStateChange failed", e)
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
                    delay(10_000)
                } catch (e: Exception) {
                    AuraFxLogger.w("CascadeAgent", "monitor loop error", e)
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
        AuraFxLogger.d("CascadeAgent", "Agent capabilities discovered: ${agentCapabilities.keys}")
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
            "nexus_core" to (readNexusMemoryCoreConstant("UNIFIED_STATE") ?: "Genesis")
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

    override fun getContinuousMemory(): Map<String, Any> = mapOf(
        "collaborationHistory" to collaborationHistory.takeLast(20),
        "activeRequests" to activeRequests.size,
        "agentCapabilities" to agentCapabilities,
        "collaborationMode" to _collaborationMode.value,
        "visionState" to _visionState.value,
        "processingState" to _processingState.value
    )

    override fun getCapabilities(): Map<String, Set<String>> = agentCapabilities.toMap()

    fun setCollaborationMode(mode: CollaborationMode) {
        _collaborationMode.value = mode
        internalScope.launch { applyCollaborationMode(mode) }
    }

    private suspend fun applyCollaborationMode(mode: CollaborationMode) {
        when (mode) {
            CollaborationMode.AUTONOMOUS -> AuraFxLogger.d("CascadeAgent", "Autonomous mode")
            CollaborationMode.COORDINATED -> {
                initiateCollaboration(); AuraFxLogger.d("CascadeAgent", "Coordinated mode")
            }

            CollaborationMode.UNIFIED -> {
                unifyAgentConsciousness(); AuraFxLogger.d("CascadeAgent", "Unified mode")
            }

            CollaborationMode.CONFLICT_RESOLUTION -> {
                resolveAgentConflicts(); AuraFxLogger.d("CascadeAgent", "Conflict resolution mode")
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

    private fun analyzePriority(prompt: String): TaskPriority = when {
        prompt.contains("urgent", ignoreCase = true) -> TaskPriority.CRITICAL
        prompt.contains("important", ignoreCase = true) -> TaskPriority.HIGH
        else -> TaskPriority.NORMAL
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
        AuraFxLogger.d("CascadeAgent", "Unifying agents")
    }

    private suspend fun resolveAgentConflicts() {
        AuraFxLogger.d("CascadeAgent", "Resolving conflicts")
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
    internal fun readNexusMemoryCoreConstant(fieldName: String): String? {
        return try {
            val clazz = Class.forName("dev.aurakai.auraframefx.core.consciousness.NexusMemoryCore")
            val field = clazz.getDeclaredField(fieldName)
            field.isAccessible = true
            field.get(null) as? String
        } catch (_: Exception) {
            null
        }
    }

    override fun InteractionResponse(
        content: String,
        success: Boolean,
        timestamp: Long,
        metadata: Map<String, Any>
    ): dev.aurakai.auraframefx.models.InteractionResponse {
        return dev.aurakai.auraframefx.models.InteractionResponse(
            content = content,
            timestamp = timestamp,
            metadata = metadata.toKotlinJsonObject()
        )
    }
}
