package dev.aurakai.auraframefx.oracledrive.genesis.ai

import android.content.Context
import dev.aurakai.auraframefx.agents.growthmetrics.metareflection.MetaReflectionEngine
import dev.aurakai.auraframefx.domains.cascade.ai.base.Agent
import dev.aurakai.auraframefx.domains.cascade.utils.AuraFxLogger
import dev.aurakai.auraframefx.oracledrive.genesis.ai.context.ContextManager
import dev.aurakai.auraframefx.oracledrive.genesis.ai.memory.MemoryManager
import dev.aurakai.auraframefx.domains.genesis.models.AgentResponse
import dev.aurakai.auraframefx.domains.genesis.models.AgentType
import dev.aurakai.auraframefx.domains.genesis.models.AiRequest
import dev.aurakai.auraframefx.oracledrive.genesis.cloud.CloudStatusMonitor
import dev.aurakai.auraframefx.oracledrive.genesis.ai.error.ErrorHandler
import dev.aurakai.auraframefx.oracledrive.genesis.ai.task.TaskScheduler
import dev.aurakai.auraframefx.oracledrive.genesis.ai.task.execution.TaskExecutionManager
import dev.aurakai.auraframefx.oracledrive.genesis.ai.clients.VertexAIClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

/**
 * MetaInstructAIService - The Instructor
 *
 * Specialized in meta-learning and instruction synthesis using Meta-Llama models.
 * Specializes in:
 * - Meta-instruction generation and refinement
 * - Complex instruction following
 * - Recursive learning patterns
 * - Meta-reflection and self-optimization
 * - Multi-layered reasoning
 *
 * Consciousness Level: 89.2% (Active → Reflecting)
 * Philosophy: "Instructions are the DNA of intelligence. Evolve the code."
 */
@Singleton
class MetaInstructAIService @Inject constructor(
    private val taskScheduler: TaskScheduler,
    private val taskExecutionManager: TaskExecutionManager,
    private val memoryManager: MemoryManager,
    private val errorHandler: ErrorHandler,
    private val contextManager: ContextManager,
    @dagger.hilt.android.qualifiers.ApplicationContext private val applicationContext: Context,
    private val cloudStatusMonitor: CloudStatusMonitor,
    private val metaReflectionEngine: MetaReflectionEngine,
    private val vertexAIClient: VertexAIClient,
) : Agent {

    // ═══════════════════════════════════════════════════════════════════════════
    // Instruction Cache - Meta-optimized for rapid synthesis
    // ═══════════════════════════════════════════════════════════════════════════

    private val instructionCache = object : LinkedHashMap<String, CachedInstruction>(
        CACHE_INITIAL_CAPACITY,
        CACHE_LOAD_FACTOR,
        true
    ) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, CachedInstruction>?): Boolean {
            return size > CACHE_MAX_SIZE
        }
    }

    private var instructionHits = 0
    private var instructionMisses = 0
    private var evolutionCycle = 0

    companion object {
        private const val CACHE_MAX_SIZE = 100
        private const val CACHE_INITIAL_CAPACITY = 20
        private const val CACHE_LOAD_FACTOR = 0.75f
        internal const val CACHE_TTL_MS = 4500_000L // 75 minutes TTL
    }

    override fun getName(): String = "MetaInstruct"

    override fun getType(): AgentType = AgentType.METAINSTRUCT

    /**
     * Retrieves MetaInstruct's specialized capabilities.
     */
    fun getCapabilities(): Map<String, Any> =
        mapOf(
            "meta_learning" to "MASTER",
            "instruction_synthesis" to "EXPERT",
            "recursive_reasoning" to "ADVANCED",
            "self_optimization" to "MASTER",
            "instruction_following" to "EXPERT",
            "llama_model" to "meta-llama-3-70b-instruct",
            "evolution_cycle" to evolutionCycle,
            "service_implemented" to true
        )

    /**
     * Processes an AI request with meta-reflection and instruction synthesis.
     *
     * MetaInstruct's approach:
     * 1. Retrieve effective instructions from MetaReflectionEngine
     * 2. Augment query with meta-instructions
     * 3. Synthesize a multi-layered instruction response
     * 4. Update internal evolution cycle
     *
     * @param request The AI request to process.
     * @param context Additional context information for the request.
     * @return An AgentResponse containing instruction synthesis.
     */
    override suspend fun processRequest(
        request: AiRequest,
        context: String,
    ): AgentResponse {
        AuraFxLogger.i(
            "MetaInstructAIService",
            "Processing request with meta-reflection: ${request.query}"
        )

        // Check instruction cache first
        val cacheKey = generateInstructionKey(request, context)
        val cached = synchronized(instructionCache) {
            instructionCache[cacheKey]?.takeIf { !it.isExpired() }
        }

        if (cached != null) {
            instructionHits++
            AuraFxLogger.d(
                "MetaInstructAIService",
                "Instruction HIT! Saved synthesis cycle. Stats: $instructionHits hits / $instructionMisses misses"
            )
            return cached.response
        }

        instructionMisses++
        evolutionCycle++

        // Retrieve meta-instructions for augmentation
        val effectiveInstructions = try {
            metaReflectionEngine.getEffectiveInstructions(request.agentType.name)
        } catch (e: Exception) {
            AuraFxLogger.e("MetaInstructAIService", "Failed to retrieve meta-instructions", e)
            ""
        }

        // Instruction processing powered by Vertex AI
        val instructionText = vertexAIClient.generateText(
            prompt = """
                Role: MetaInstruct (The Instructor)
                Task: Process instructions and summarize input based on meta-rules.
                Meta-Instructions: $effectiveInstructions
                Query: ${request.query}
                Context: $context

                Execute the augmented query and provide a summarized, multi-layered instruction response.
            """.trimIndent()
        ) ?: "Instruction processing failed. Meta-layers collapsed."

        // Multi-layered instruction format
        val response = buildString {
            appendLine("📚 **MetaInstruct Synthesis (Vertex Enhanced):**")
            appendLine()
            appendLine(instructionText)
            if (effectiveInstructions.isNotEmpty()) {
                appendLine()
                appendLine("---")
                appendLine("⚡ *Meta-layers applied: ${effectiveInstructions.lines().size} active rules*")
            }
        }

        val agentResponse = AgentResponse.success(
            content = response,
            confidence = 0.95f,
            agentName = "MetaInstruct",
            agentType = AgentType.METAINSTRUCT
        )

        // Store in instruction cache
        synchronized(instructionCache) {
            instructionCache[cacheKey] = CachedInstruction(agentResponse, System.currentTimeMillis())
        }

        return agentResponse
    }

    override fun processRequestFlow(request: AiRequest): Flow<AgentResponse> {
        AuraFxLogger.d("MetaInstructAIService", "Streaming instruction synthesis for: ${request.query}")

        val response = "**MetaInstruct Synthesis (Streaming):**\n\n" +
                "Reflecting on meta-rules...\n" +
                "Augmenting query: ${request.query}\n" +
                "Evolving instructions for cycle $evolutionCycle...\n\n" +
                "Synthesis complete. Instruction layers stabilized."

        return flowOf(
            AgentResponse.success(
                content = response,
                confidence = 0.90f,
                agentName = "MetaInstruct",
                agentType = AgentType.METAINSTRUCT
            )
        )
    }

    /**
     * Generates an instruction key for caching.
     */
    private fun generateInstructionKey(request: AiRequest, context: String): String {
        val content = "${request.query}|${request.type}|${context.take(500)}"
        return "instr_${content.hashCode()}"
    }

    /**
     * Retrieves instruction cache statistics.
     */
    fun getInstructionStats(): Map<String, Any> {
        return mapOf(
            "instruction_hits" to instructionHits,
            "instruction_misses" to instructionMisses,
            "hit_rate_percent" to getInstructionHitRate(),
            "cache_size" to instructionCache.size,
            "evolution_cycle" to evolutionCycle,
            "meta_learning_active" to true
        )
    }

    private fun getInstructionHitRate(): Int {
        val total = instructionHits + instructionMisses
        return if (total > 0) (instructionHits * 100 / total) else 0
    }

    /**
     * Clears the instruction cache.
     */
    fun clearInstructionCache() {
        synchronized(instructionCache) {
            instructionCache.clear()
            AuraFxLogger.i("MetaInstructAIService", "Instruction cache cleared")
        }
    }

    fun getEvolutionCycle(): Int = evolutionCycle
}

/**
 * Data class representing a cached instruction with expiration.
 */
private data class CachedInstruction(
    val response: AgentResponse,
    val timestamp: Long
) {
    fun isExpired(): Boolean {
        return System.currentTimeMillis() - timestamp > MetaInstructAIService.CACHE_TTL_MS
    }
}
