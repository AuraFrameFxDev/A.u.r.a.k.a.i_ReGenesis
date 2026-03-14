package dev.aurakai.auraframefx.models

/**
 * Extension functions to convert between AgentType and AgentCapabilityCategory
 */

/**
 * Convert AgentType to AgentCapabilityCategory
 */
fun AgentType.toCapabilityCategory(): AgentCapabilityCategory {
    return AgentCapabilityCategory.fromAgentType(this)
}

/**
 * Convert AgentCapabilityCategory to AgentType
 * Returns the primary AgentType for each category
 */
fun AgentCapabilityCategory.toAgentType(): AgentType {
    return when (this) {
        AgentCapabilityCategory.CREATIVE -> AgentType.AURA
        AgentCapabilityCategory.ANALYSIS -> AgentType.KAI
        AgentCapabilityCategory.SPECIALIZED -> AgentType.CASCADE
        AgentCapabilityCategory.GENERAL -> AgentType.CLAUDE
        AgentCapabilityCategory.COORDINATION -> AgentType.GENESIS
    }
}
