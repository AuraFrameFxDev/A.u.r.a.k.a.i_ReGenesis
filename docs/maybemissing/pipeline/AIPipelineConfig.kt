package dev.aurakai.auraframefx.ai.pipeline

import dev.aurakai.auraframefx.model.AgentType

data class AIPipelineConfig(
    val maxRetries: Int = 3,
    val timeoutSeconds: Int = 30,
    val contextWindowSize: Int = 5,
    val priorityThreshold: Float = 0.7f,
    val priorityWeight: Float = 0.4f,
    val urgencyWeight: Float = 0.4f,
    val importanceWeight: Float = 0.2f,
    val maxActiveTasks: Int = 10,
    val agentPriorities: Map<AgentType, Float> = mapOf(
        AgentType.GENESIS to 1.0f,
        AgentType.KAI to 0.9f,
        AgentType.AURA to 0.8f,
        AgentType.CASCADE to 0.7f
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
