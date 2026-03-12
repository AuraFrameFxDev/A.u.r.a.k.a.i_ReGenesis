package dev.aurakai.auraframefx.domains.cascade.utils.cascade.pipeline

import dev.aurakai.auraframefx.domains.genesis.models.AgentCapabilityCategory

data class AIPipelineConfig(
    val maxRetries: Int = 3,
    val timeoutSeconds: Int = 30,
    val contextWindowSize: Int = 5,
    val priorityThreshold: Float = 0.7f,
    val priorityWeight: Float = 0.4f,
    val urgencyWeight: Float = 0.4f,
    val importanceWeight: Float = 0.2f,
    val maxActiveTasks: Int = 10,
    val agentPriorities: Map<AgentCapabilityCategory, Float> = mapOf(
        AgentCapabilityCategory.COORDINATION to 1.0f,
        AgentCapabilityCategory.ANALYSIS to 0.9f,
        AgentCapabilityCategory.CREATIVE to 0.8f,
        AgentCapabilityCategory.SPECIALIZED to 0.7f
    ),
    val memoryRetrievalConfig: MemoryRetrievalConfig = MemoryRetrievalConfig(),
    val contextChainingConfig: ContextChainingConfig = ContextChainingConfig(),
)

data class MemoryRetrievalConfig(
    val maxContextLength: Int = 2000,
    val similarityThreshold: Float = 0.75f,
    val maxRetrievedItems: Int = 5,
)

data class ContextChainingConfig(
    val maxChainLength: Int = 10,
    val relevanceThreshold: Float = 0.6f,
    val decayRate: Float = 0.9f,
)
