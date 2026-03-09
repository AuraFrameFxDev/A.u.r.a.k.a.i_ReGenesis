package dev.aurakai.auraframefx.ai.pipeline

import dev.aurakai.auraframefx.models.AgentType
import org.junit.Assert.*
import org.junit.jupiter.api.Test

/**
 * Comprehensive unit tests for AIPipelineConfig and related configuration classes.
 * Tests default values, custom configurations, and edge cases.
 */
class AIPipelineConfigTest {

    // AIPipelineConfig Default Values Tests
    @Test
    fun `test default AIPipelineConfig values`() {
        val config = AIPipelineConfig()

        assertEquals(3, config.maxRetries)
        assertEquals(30, config.timeoutSeconds)
        assertEquals(5, config.contextWindowSize)
        assertEquals(0.7f, config.priorityThreshold, 0.001f)
        assertEquals(0.4f, config.priorityWeight, 0.001f)
        assertEquals(0.4f, config.urgencyWeight, 0.001f)
        assertEquals(0.2f, config.importanceWeight, 0.001f)
        assertEquals(10, config.maxActiveTasks)
    }

    @Test
    fun `test default agent priorities`() {
        val config = AIPipelineConfig()

        assertEquals(1.0f, config.agentPriorities[AgentType.GENESIS], 0.001f)
        assertEquals(0.9f, config.agentPriorities[AgentType.KAI], 0.001f)
        assertEquals(0.8f, config.agentPriorities[AgentType.AURA], 0.001f)
        assertEquals(0.7f, config.agentPriorities[AgentType.CASCADE], 0.001f)
    }

    @Test
    fun `test agent priorities ordering`() {
        val config = AIPipelineConfig()
        val genesisPriority = config.agentPriorities[AgentType.GENESIS]!!
        val kaiPriority = config.agentPriorities[AgentType.KAI]!!
        val auraPriority = config.agentPriorities[AgentType.AURA]!!
        val cascadePriority = config.agentPriorities[AgentType.CASCADE]!!

        assertTrue("Genesis should have highest priority", genesisPriority > kaiPriority)
        assertTrue("Kai should have higher priority than Aura", kaiPriority > auraPriority)
        assertTrue("Aura should have higher priority than Cascade", auraPriority > cascadePriority)
    }

    // Custom Configuration Tests
    @Test
    fun `test custom maxRetries configuration`() {
        val config = AIPipelineConfig(maxRetries = 5)
        assertEquals(5, config.maxRetries)
    }

    @Test
    fun `test custom timeout configuration`() {
        val config = AIPipelineConfig(timeoutSeconds = 60)
        assertEquals(60, config.timeoutSeconds)
    }

    @Test
    fun `test custom context window size`() {
        val config = AIPipelineConfig(contextWindowSize = 10)
        assertEquals(10, config.contextWindowSize)
    }

    @Test
    fun `test custom priority threshold`() {
        val config = AIPipelineConfig(priorityThreshold = 0.9f)
        assertEquals(0.9f, config.priorityThreshold, 0.001f)
    }

    @Test
    fun `test custom weight configurations`() {
        val config = AIPipelineConfig(
            priorityWeight = 0.5f,
            urgencyWeight = 0.3f,
            importanceWeight = 0.2f
        )

        assertEquals(0.5f, config.priorityWeight, 0.001f)
        assertEquals(0.3f, config.urgencyWeight, 0.001f)
        assertEquals(0.2f, config.importanceWeight, 0.001f)

        // Verify weights sum to 1.0
        val totalWeight = config.priorityWeight + config.urgencyWeight + config.importanceWeight
        assertEquals(1.0f, totalWeight, 0.001f)
    }

    @Test
    fun `test custom max active tasks`() {
        val config = AIPipelineConfig(maxActiveTasks = 20)
        assertEquals(20, config.maxActiveTasks)
    }

    @Test
    fun `test custom agent priorities`() {
        val customPriorities = mapOf(
            AgentType.GENESIS to 0.5f,
            AgentType.KAI to 0.4f,
            AgentType.AURA to 0.3f,
            AgentType.CASCADE to 0.2f
        )
        val config = AIPipelineConfig(agentPriorities = customPriorities)

        assertEquals(0.5f, config.agentPriorities[AgentType.GENESIS], 0.001f)
        assertEquals(0.4f, config.agentPriorities[AgentType.KAI], 0.001f)
        assertEquals(0.3f, config.agentPriorities[AgentType.AURA], 0.001f)
        assertEquals(0.2f, config.agentPriorities[AgentType.CASCADE], 0.001f)
    }

    // MemoryRetrievalConfig Tests
    @Test
    fun `test default MemoryRetrievalConfig values`() {
        val config = MemoryRetrievalConfig()

        assertEquals(2000, config.maxContextLength)
        assertEquals(0.75f, config.similarityThreshold, 0.001f)
        assertEquals(5, config.maxRetrievedItems)
    }

    @Test
    fun `test custom MemoryRetrievalConfig`() {
        val config = MemoryRetrievalConfig(
            maxContextLength = 5000,
            similarityThreshold = 0.9f,
            maxRetrievedItems = 10
        )

        assertEquals(5000, config.maxContextLength)
        assertEquals(0.9f, config.similarityThreshold, 0.001f)
        assertEquals(10, config.maxRetrievedItems)
    }

    @Test
    fun `test MemoryRetrievalConfig in AIPipelineConfig`() {
        val memoryConfig = MemoryRetrievalConfig(maxContextLength = 3000)
        val pipelineConfig = AIPipelineConfig(memoryRetrievalConfig = memoryConfig)

        assertEquals(3000, pipelineConfig.memoryRetrievalConfig.maxContextLength)
    }

    // ContextChainingConfig Tests
    @Test
    fun `test default ContextChainingConfig values`() {
        val config = ContextChainingConfig()

        assertEquals(10, config.maxChainLength)
        assertEquals(0.6f, config.relevanceThreshold, 0.001f)
        assertEquals(0.9f, config.decayRate, 0.001f)
    }

    @Test
    fun `test custom ContextChainingConfig`() {
        val config = ContextChainingConfig(
            maxChainLength = 20,
            relevanceThreshold = 0.8f,
            decayRate = 0.95f
        )

        assertEquals(20, config.maxChainLength)
        assertEquals(0.8f, config.relevanceThreshold, 0.001f)
        assertEquals(0.95f, config.decayRate, 0.001f)
    }

    @Test
    fun `test ContextChainingConfig in AIPipelineConfig`() {
        val chainingConfig = ContextChainingConfig(maxChainLength = 15)
        val pipelineConfig = AIPipelineConfig(contextChainingConfig = chainingConfig)

        assertEquals(15, pipelineConfig.contextChainingConfig.maxChainLength)
    }

    // Edge Cases and Validation Tests
    @Test
    fun `test zero maxRetries`() {
        val config = AIPipelineConfig(maxRetries = 0)
        assertEquals(0, config.maxRetries)
    }

    @Test
    fun `test very large timeout`() {
        val config = AIPipelineConfig(timeoutSeconds = 3600) // 1 hour
        assertEquals(3600, config.timeoutSeconds)
    }

    @Test
    fun `test minimum priority threshold`() {
        val config = AIPipelineConfig(priorityThreshold = 0.0f)
        assertEquals(0.0f, config.priorityThreshold, 0.001f)
    }

    @Test
    fun `test maximum priority threshold`() {
        val config = AIPipelineConfig(priorityThreshold = 1.0f)
        assertEquals(1.0f, config.priorityThreshold, 0.001f)
    }

    @Test
    fun `test empty agent priorities map`() {
        val config = AIPipelineConfig(agentPriorities = emptyMap())
        assertTrue(config.agentPriorities.isEmpty())
    }

    @Test
    fun `test single agent priority`() {
        val config = AIPipelineConfig(
            agentPriorities = mapOf(AgentType.GENESIS to 1.0f)
        )
        assertEquals(1, config.agentPriorities.size)
        assertEquals(1.0f, config.agentPriorities[AgentType.GENESIS], 0.001f)
    }

    // Data Class Behavior Tests
    @Test
    fun `test AIPipelineConfig copy functionality`() {
        val original = AIPipelineConfig(maxRetries = 3, timeoutSeconds = 30)
        val modified = original.copy(maxRetries = 5)

        assertEquals(5, modified.maxRetries)
        assertEquals(30, modified.timeoutSeconds)
        assertEquals(3, original.maxRetries) // Original unchanged
    }

    @Test
    fun `test AIPipelineConfig equality`() {
        val config1 = AIPipelineConfig(maxRetries = 3)
        val config2 = AIPipelineConfig(maxRetries = 3)
        val config3 = AIPipelineConfig(maxRetries = 5)

        assertEquals(config1, config2)
        assertNotEquals(config1, config3)
    }

    @Test
    fun `test MemoryRetrievalConfig copy functionality`() {
        val original = MemoryRetrievalConfig(maxContextLength = 2000)
        val modified = original.copy(maxContextLength = 5000)

        assertEquals(5000, modified.maxContextLength)
        assertEquals(2000, original.maxContextLength)
    }

    @Test
    fun `test ContextChainingConfig copy functionality`() {
        val original = ContextChainingConfig(maxChainLength = 10)
        val modified = original.copy(maxChainLength = 20)

        assertEquals(20, modified.maxChainLength)
        assertEquals(10, original.maxChainLength)
    }

    // Complex Configuration Tests
    @Test
    fun `test fully customized pipeline configuration`() {
        val memoryConfig = MemoryRetrievalConfig(
            maxContextLength = 5000,
            similarityThreshold = 0.85f,
            maxRetrievedItems = 8
        )

        val chainingConfig = ContextChainingConfig(
            maxChainLength = 15,
            relevanceThreshold = 0.7f,
            decayRate = 0.92f
        )

        val agentPriorities = mapOf(
            AgentType.GENESIS to 1.0f,
            AgentType.KAI to 0.95f,
            AgentType.AURA to 0.85f,
            AgentType.CASCADE to 0.75f
        )

        val config = AIPipelineConfig(
            maxRetries = 5,
            timeoutSeconds = 45,
            contextWindowSize = 8,
            priorityThreshold = 0.8f,
            priorityWeight = 0.35f,
            urgencyWeight = 0.35f,
            importanceWeight = 0.3f,
            maxActiveTasks = 15,
            agentPriorities = agentPriorities,
            memoryRetrievalConfig = memoryConfig,
            contextChainingConfig = chainingConfig
        )

        // Verify all custom values
        assertEquals(5, config.maxRetries)
        assertEquals(45, config.timeoutSeconds)
        assertEquals(8, config.contextWindowSize)
        assertEquals(0.8f, config.priorityThreshold, 0.001f)
        assertEquals(15, config.maxActiveTasks)
        assertEquals(5000, config.memoryRetrievalConfig.maxContextLength)
        assertEquals(15, config.contextChainingConfig.maxChainLength)
        assertEquals(1.0f, config.agentPriorities[AgentType.GENESIS], 0.001f)
    }

    @Test
    fun `test configuration immutability through copy`() {
        val config1 = AIPipelineConfig()
        val config2 = config1.copy(maxRetries = 10)

        assertEquals(3, config1.maxRetries) // Original unchanged
        assertEquals(10, config2.maxRetries) // Copy modified
    }
}
