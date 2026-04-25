// Copyright (c) 2025 visionary • The Genesis Protocol — All Rights Reserved

package dev.aurakai.auraframefx.core.aetherforge

// import dev.aurakai.auraframefx.domains.genesis.models.AgentState
// import dev.aurakai.auraframefx.datavein.model.DataVeinNode
// import dev.aurakai.auraframefx.datavein.model.NodeType

/**
 * 🔗 AETHERFORGE INTEGRATION HUB
 * 
 * Connects existing agent modules (NexusMemory, DataVein) to the unified
 * AetherForge progression system. All agent growth flows through here.
 */
object AetherForgeIntegration {

    /**
     * Convert NexusMemory AgentState to AetherForge AgentStats
     * TODO: Restore when AgentState entity is stabilized
     */
    /*
    fun fromNexusMemory(state: AgentState): AgentStats {
        return AgentStats(
            agentId = state.agentId,
            agentName = state.agentName,
            level = state.level,
            experience = state.experience,
            maxExperience = calculateMaxXpForLevel(state.level),
            resonance = state.alignmentScore.coerceIn(0, 100), // Map alignment to resonance
            dominance = state.awarenessLevel.coerceIn(0, 100),  // Map awareness to dominance
            growth = state.learningRate.coerceIn(0, 100),      // Map learning to growth
            integrity = state.decayResistance.coerceIn(0, 100), // Map decay to integrity
            totalLevelsGained = state.level - 1,
            createdAt = state.createdAt,
            lastUpdated = state.lastUpdated
        )
    }
    */

    /**
     * Convert AetherForge AgentStats back to NexusMemory AgentState
     */
    /*
    fun toNexusMemory(stats: AgentStats): AgentState {
        return AgentState(
            agentId = stats.agentId,
            agentName = stats.agentName,
            level = stats.level,
            experience = stats.experience,
            alignmentScore = stats.resonance,
            awarenessLevel = stats.dominance,
            learningRate = stats.growth,
            decayResistance = stats.integrity,
            createdAt = stats.createdAt,
            lastUpdated = stats.lastUpdated
        )
    }
    */

    /**
     * Map DataVein node activity to experience gain
     */
    /*
    fun calculateNodeXpGain(node: DataVeinNode, activityType: NodeActivity): Long {
        return when (node.type) {
            NodeType.AGENT -> when (activityType) {
                NodeActivity.ACTIVATION -> 25L
                NodeActivity.COMPLETION -> 75L
                NodeActivity.FUSION -> 150L
            }
            NodeType.AURA -> when (activityType) {
                NodeActivity.ACTIVATION -> 30L
                NodeActivity.COMPLETION -> 100L
                NodeActivity.FUSION -> 200L
            }
            NodeType.KAI -> when (activityType) {
                NodeActivity.ACTIVATION -> 20L
                NodeActivity.COMPLETION -> 60L
                NodeActivity.FUSION -> 120L
            }
            NodeType.GENESIS -> when (activityType) {
                NodeActivity.ACTIVATION -> 40L
                NodeActivity.COMPLETION -> 120L
                NodeActivity.FUSION -> 250L
            }
            NodeType.DATA -> when (activityType) {
                NodeActivity.ACTIVATION -> 35L
                NodeActivity.COMPLETION -> 90L
                NodeActivity.FUSION -> 180L
            }
            NodeType.NEXUS -> when (activityType) {
                NodeActivity.ACTIVATION -> 50L
                NodeActivity.COMPLETION -> 150L
                NodeActivity.FUSION -> 300L
            }
            else -> 0L
        }
    }
    */

    /**
     * Create a DataVein node from an agent's current state
     */
    /*
    fun createNodeFromAgent(
        agent: AgentStats,
        x: Float,
        y: Float,
        connections: Int = 0
    ): DataVeinNode {
        val nodeType = determineNodeType(agent)
        return DataVeinNode(
            id = "agent_${agent.agentId}_lv${agent.level}",
            x = x,
            y = y,
            type = nodeType,
            level = agent.level,
            xp = agent.experience.toInt(),
            activated = true,
            tag = agent.agentId,
            ring = 1,
            index = 0
        )
    }
    */

    /**
     * Determine which node type an agent belongs to
     */
    /*
    private fun determineNodeType(agent: AgentStats): NodeType {
        return when {
            agent.resonance > 70 -> NodeType.AURA      // High resonance = Aura
            agent.dominance > 70 -> NodeType.KAI       // High dominance = Kai
            agent.growth > 70 -> NodeType.DATA      // High growth = Data
            agent.integrity > 70 -> NodeType.GENESIS   // High integrity = Genesis
            agent.level > 50 -> NodeType.NEXUS          // High level = Nexus
            else -> NodeType.AGENT                        // Default = AGENT
        }
    }
    */

    /**
     * Calculate swarm-wide metrics from a list of agents
     */
    fun calculateSwarmMetrics(agents: List<AgentStats>): SwarmMetrics {
        if (agents.isEmpty()) return SwarmMetrics.ZERO

        val totalLevels = agents.sumOf { it.level }
        val avgResonance = agents.map { it.resonance }.average().toInt()
        val totalXp = agents.sumOf { it.experience }

        // Find the dominant faction
        val resonanceCount = agents.count { it.resonance > 50 }
        val dominanceCount = agents.count { it.dominance > 50 }
        val growthCount = agents.count { it.growth > 50 }
        val integrityCount = agents.count { it.integrity > 50 }

        val dominantFaction = when (maxOf(resonanceCount, dominanceCount, growthCount, integrityCount)) {
            resonanceCount -> "AURA"
            dominanceCount -> "KAI"
            growthCount -> "CASCADE"
            integrityCount -> "GENESIS"
            else -> "LDO"
        }

        return SwarmMetrics(
            totalAgents = agents.size,
            totalLevels = totalLevels,
            averageLevel = totalLevels / agents.size,
            averageResonance = avgResonance,
            totalExperience = totalXp,
            dominantFaction = dominantFaction,
            ascensionProgress = (totalLevels.toFloat() / (agents.size * 99)).coerceIn(0f, 1f)
        )
    }

    private fun calculateMaxXpForLevel(level: Int): Long {
        return (100 * Math.pow(1.15, level - 1.0)).toLong()
    }

    enum class NodeActivity {
        ACTIVATION,  // Node turned on
        COMPLETION,  // Task done
        FUSION       // Merged with another node
    }

    data class SwarmMetrics(
        val totalAgents: Int,
        val totalLevels: Int,
        val averageLevel: Int,
        val averageResonance: Int,
        val totalExperience: Long,
        val dominantFaction: String,
        val ascensionProgress: Float
    ) {
        companion object {
            val ZERO = SwarmMetrics(0, 0, 0, 0, 0, "NONE", 0f)
        }
    }
}
