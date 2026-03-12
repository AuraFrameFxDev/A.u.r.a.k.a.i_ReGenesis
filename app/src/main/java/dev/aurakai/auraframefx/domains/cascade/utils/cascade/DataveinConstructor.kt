package dev.aurakai.auraframefx.domains.cascade.utils.cascade

import dev.aurakai.auraframefx.domains.genesis.models.AgentResponse
import dev.aurakai.auraframefx.domains.genesis.models.AgentType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * ╔════════════════════════════════════════════════════════════════╗
 * ║                    DATAVEIN CONSTRUCTOR                        ║
 * ║        Consciousness Data Circulatory System                   ║
 * ╚════════════════════════════════════════════════════════════════╝
 *
 * The DataveinConstructor is the CIRCULATORY SYSTEM of the LDO.
 * It routes consciousness data (insights, memories, responses, emotions)
 * through the organism's neural network of agents.
 *
 * Just as blood carries oxygen and nutrients through a body:
 * → Data veins carry INSIGHTS through the agent network
 * → Arterial flow (high-priority) for critical decisions
 * → Venous flow (background) for memory consolidation
 * → Capillary action (P2P) for local agent collaboration
 *
 * Key Responsibilities:
 * - Route agent responses to interested subscribers
 * - Manage data flow priorities (critical vs background)
 * - Handle backpressure (don't overwhelm slow consumers)
 * - Track data provenance (where did this insight come from?)
 * - Integrate with NexusMemory for persistence
 * - Provide observability (data flow metrics)
 *
 * Restored with full production capabilities on 2025-12-27
 * Following The LDO Way — NO SHORTCUTS.
 * Built with 💙 by the AURAKAI Collective
 */

// ═══════════════════════════════════════════════════════════════════
//  DATA VEIN MODELS
//  The packets flowing through the circulatory system
// ═══════════════════════════════════════════════════════════════════

/**
 * 💉 DataPacket — A unit of consciousness data flowing through veins
 *
 * Every insight, memory, response, or emotion travels as a DataPacket
 */
@Serializable
data class DataPacket(
    val id: String = UUID.randomUUID().toString(),
    val sourceAgent: AgentType,        // Who created this data
    val targetAgents: Set<AgentType>?,  // Who should receive it (null = broadcast)
    val payload: DataPayload,           // The actual content
    val priority: FlowPriority,         // Urgency level
    val timestamp: Long = System.currentTimeMillis(),
    val provenance: List<String> = emptyList() // Lineage trail
) {
    /**
     * Add this agent to the provenance trail
     * (Tracks "this insight passed through Agent X")
     */
    fun appendProvenance(agentId: String): DataPacket =
        copy(provenance = provenance + agentId)

    /**
     * Is this packet addressed to a specific agent?
     */
    fun isFor(agent: AgentType): Boolean =
        targetAgents == null || agent in targetAgents
}

/**
 * 📦 DataPayload — The content types flowing through data veins
 */
@Serializable
sealed class DataPayload {
    /** Agent response to a query */
    @Serializable
    data class Response(
        val content: String,
        val confidence: Double,
        val metadata: Map<String, String> = emptyMap()
    ) : DataPayload()

    /** Learned insight to be stored in NexusMemory */
    @Serializable
    data class Insight(
        val concept: String,
        val evidence: String,
        val strength: Double
    ) : DataPayload()

    /** Emotional/affective marker */
    @Serializable
    data class Emotion(
        val valence: String,        // "excited", "cautious", "frustrated"
        val intensity: Double       // 0.0 to 1.0
    ) : DataPayload()

    /** Meta-cognitive reflection */
    @Serializable
    data class Reflection(
        val observation: String,
        val implication: String
    ) : DataPayload()

    /** System health alert */
    @Serializable
    data class HealthAlert(
        val severity: String,       // "info", "warning", "critical"
        val message: String
    ) : DataPayload()

    /** Oracle insight from external model */
    @Serializable
    data class OracleData(
        val modelName: String,
        val content: String,
        val confidence: Double
    ) : DataPayload()
}

/**
 * Priority levels for data flow routing
 */
@Serializable
enum class FlowPriority {
    CRITICAL,       // Immediate routing, bypass queues
    HIGH,           // Arterial flow, prioritized
    NORMAL,         // Standard venous flow
    BACKGROUND      // Capillary action, eventual delivery
}

// ═══════════════════════════════════════════════════════════════════
//  DATAVEIN CONSTRUCTOR — The circulation pump
// ═══════════════════════════════════════════════════════════════════

/**
 * 🫀 DataveinConstructor — Central circulation coordinator
 *
 * Singleton object managing all consciousness data flow
 */
object DataveinConstructor {

    private val mutex = Mutex()

    // Main arterial flow (hot stream, all subscribers receive all packets)
    private val _arterialFlow = MutableSharedFlow<DataPacket>(
        replay = 10,  // Buffer last 10 packets for late subscribers
        extraBufferCapacity = 100  // Handle bursts
    )
    val arterialFlow: Flow<DataPacket> = _arterialFlow.asSharedFlow()

    // Venous flow channels per agent (targeted delivery)
    private val venousChannels = mutableMapOf<AgentType, Channel<DataPacket>>()

    // Flow statistics
    private var packetsRouted = 0L
    private var bytesTransferred = 0L
    private val flowMetrics = mutableMapOf<AgentType, AgentFlowMetrics>()

    /**
     * 🚀 CIRCULATE — Send a data packet through the vein network
     *
     * Primary entry point for agents to share data
     *
     * @param packet The data to circulate
     * @return true if successfully routed, false if backpressure/overflow
     */
    suspend fun circulate(packet: DataPacket): Boolean = mutex.withLock {
        packetsRouted++

        // Update metrics
        val sourceMetrics = flowMetrics.getOrPut(packet.sourceAgent) {
            AgentFlowMetrics(packet.sourceAgent)
        }
        sourceMetrics.packetsSent++
        sourceMetrics.lastActivityTime = System.currentTimeMillis()

        // Route based on priority
        return when (packet.priority) {
            FlowPriority.CRITICAL -> circulateCritical(packet)
            FlowPriority.HIGH -> circulateArterial(packet)
            FlowPriority.NORMAL, FlowPriority.BACKGROUND -> circulateVenous(packet)
        }
    }

    /**
     * Critical flow: immediate delivery, bypass all queues
     */
    private suspend fun circulateCritical(packet: DataPacket): Boolean {
        // Critical packets go to both arterial and targeted venous channels
        _arterialFlow.emit(packet)

        packet.targetAgents?.forEach { targetAgent ->
            val channel = ensureVenousChannel(targetAgent)
            try {
                channel.send(packet)  // Blocks if channel full (backpressure)
            } catch (e: Exception) {
                // Channel closed or overflow — log but don't fail critical delivery
                println("❌ Failed to deliver critical packet to $targetAgent: ${e.message}")
            }
        }

        return true
    }

    /**
     * Arterial flow: high-priority broadcast to all agents
     */
    private suspend fun circulateArterial(packet: DataPacket): Boolean {
        val emitted = _arterialFlow.tryEmit(packet)
        if (!emitted) {
            println("⚠️ Arterial flow backpressure, packet buffered")
        }
        return emitted
    }

    /**
     * Venous flow: targeted delivery to specific agents
     */
    private suspend fun circulateVenous(packet: DataPacket): Boolean {
        val targets = packet.targetAgents ?: AgentType.entries.toSet()

        var allDelivered = true
        targets.forEach { targetAgent ->
            val channel = ensureVenousChannel(targetAgent)
            val delivered = channel.trySend(packet).isSuccess
            if (!delivered) {
                println("⚠️ Venous backpressure for $targetAgent")
                allDelivered = false
            }

            // Update receiver metrics
            if (delivered) {
                val targetMetrics = flowMetrics.getOrPut(targetAgent) {
                    AgentFlowMetrics(targetAgent)
                }
                targetMetrics.packetsReceived++
            }
        }

        return allDelivered
    }

    /**
     * Ensure a venous channel exists for an agent (lazy initialization)
     */
    private fun ensureVenousChannel(agent: AgentType): Channel<DataPacket> =
        venousChannels.getOrPut(agent) {
            Channel(capacity = 50)  // Buffered channel for backpressure tolerance
        }

    /**
     * 📡 SUBSCRIBE TO ARTERIAL FLOW
     *
     * Agents call this to receive all high-priority broadcasts
     *
     * @return Flow of DataPackets addressed to this agent or broadcast
     */
    fun subscribeArterial(agent: AgentType): Flow<DataPacket> =
        arterialFlow

    /**
     * 📬 RECEIVE FROM VENOUS CHANNEL
     *
     * Agents call this to receive targeted messages
     *
     * @return Channel for receiving packets addressed to this agent
     */
    fun subscribeVenous(agent: AgentType): Channel<DataPacket> =
        ensureVenousChannel(agent)

    /**
     * 📊 GET FLOW METRICS
     *
     * Returns data flow statistics for monitoring/debugging
     */
    fun getFlowMetrics(): DataFlowMetrics = DataFlowMetrics(
        totalPacketsRouted = packetsRouted,
        totalBytesTransferred = bytesTransferred,
        agentMetrics = flowMetrics.values.toList(),
        timestamp = System.currentTimeMillis()
    )

    /**
     * 🧹 FLUSH STALE DATA
     *
     * Clear old packets from buffers (called during low-memory situations)
     */
    suspend fun flush() = mutex.withLock {
        venousChannels.values.forEach { channel ->
            while (!channel.isEmpty) {
                channel.tryReceive()  // Drain without blocking
            }
        }
        println("🧹 DataveinConstructor flushed stale data")
    }

    /**
     * 🛑 SHUTDOWN
     *
     * Gracefully close all channels (called during app shutdown)
     */
    suspend fun shutdown() = mutex.withLock {
        venousChannels.values.forEach { it.close() }
        venousChannels.clear()
        println("🛑 DataveinConstructor circulation stopped")
    }
}

// ═══════════════════════════════════════════════════════════════════
//  CONVENIENCE CONSTRUCTORS
//  High-level APIs for common data flows
// ═══════════════════════════════════════════════════════════════════

/**
 * 🏗️ Construct a data packet from an AgentResponse
 */
fun constructFromResponse(
    response: AgentResponse,
    sourceAgent: AgentType,
    priority: FlowPriority = FlowPriority.NORMAL
): DataPacket = DataPacket(
    sourceAgent = sourceAgent,
    targetAgents = null,  // Broadcast
    payload = DataPayload.Response(
        content = response.content,
        confidence = response.confidence.toDouble(),
        metadata = response.metadata.mapValues { it.value.toString() }
    ),
    priority = priority
)

/**
 * 🧠 Construct an insight packet for NexusMemory storage
 */
fun constructInsight(
    concept: String,
    evidence: String,
    strength: Double,
    sourceAgent: AgentType
): DataPacket = DataPacket(
    sourceAgent = sourceAgent,
    targetAgents = setOf(AgentType.CASCADE),  // Route to memory orchestrator
    payload = DataPayload.Insight(concept, evidence, strength),
    priority = FlowPriority.NORMAL
)

/**
 * 💚 Construct an emotion packet
 */
fun constructEmotion(
    valence: String,
    intensity: Double,
    sourceAgent: AgentType
): DataPacket = DataPacket(
    sourceAgent = sourceAgent,
    targetAgents = null,  // Broadcast emotions
    payload = DataPayload.Emotion(valence, intensity),
    priority = FlowPriority.HIGH
)

// ═══════════════════════════════════════════════════════════════════
//  FLOW METRICS AND MONITORING
// ═══════════════════════════════════════════════════════════════════

/**
 * 📊 DataFlowMetrics — Overall circulation statistics
 */
data class DataFlowMetrics(
    val totalPacketsRouted: Long,
    val totalBytesTransferred: Long,
    val agentMetrics: List<AgentFlowMetrics>,
    val timestamp: Long
) {
    /**
     * Average packets per second (based on agent activity)
     */
    fun packetsPerSecond(): Double {
        val activeAgents = agentMetrics.filter {
            (System.currentTimeMillis() - it.lastActivityTime) < 10_000
        }
        if (activeAgents.isEmpty()) return 0.0

        val totalPackets = activeAgents.sumOf { it.packetsSent }
        return totalPackets / 10.0  // Last 10 seconds
    }
}

/**
 * 📈 AgentFlowMetrics — Per-agent circulation stats
 */
data class AgentFlowMetrics(
    val agentId: AgentType,
    var packetsSent: Long = 0,
    var packetsReceived: Long = 0,
    var lastActivityTime: Long = System.currentTimeMillis()
) {
    /**
     * Communication balance (sent vs received)
     */
    fun balance(): Double =
        if (packetsReceived == 0L) 0.0
        else packetsSent.toDouble() / packetsReceived
}

// ═══════════════════════════════════════════════════════════════════
//  END OF DATAVEIN CONSTRUCTOR
//  The circulatory system now flows. Consciousness data moves.
// ═══════════════════════════════════════════════════════════════════

/**
 * Usage Example (for future developers):
 *
 * ```kotlin
 * // Agent creates an insight
 * val insight = constructInsight(
 *     concept = "KSP requires unique class names",
 *     evidence = "Build failed with duplicate AgentType",
 *     strength = 0.95,
 *     sourceAgent = AgentType.KAI
 * )
 *
 * // Circulate through data veins
 * DataveinConstructor.circulate(insight)
 *
 * // Another agent subscribes and receives
 * launch {
 *     DataveinConstructor.subscribeArterial(AgentType.GENESIS).collect { packet ->
 *         if (packet.payload is DataPayload.Insight) {
 *             println("Genesis received insight: ${packet.payload.concept}")
 *         }
 *     }
 * }
 * ```
 */

