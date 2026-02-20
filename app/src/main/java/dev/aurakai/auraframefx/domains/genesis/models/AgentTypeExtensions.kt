package dev.aurakai.auraframefx.domains.genesis.models

/**
 * Extension functions to convert between AgentType and AgentCapabilityCategory
 */

/**
 * Convert AgentType to AgentCapabilityCategory
 */
fun AgentType.toCapabilityCategory(): AgentCapabilityCategory {
    return when (this) {
        AgentType.AURA -> AgentCapabilityCategory.CREATIVE
        AgentType.KAI -> AgentCapabilityCategory.SECURITY
        AgentType.GENESIS -> AgentCapabilityCategory.COORDINATION
        AgentType.CASCADE -> AgentCapabilityCategory.ORCHESTRATION
        AgentType.CLAUDE -> AgentCapabilityCategory.ANALYSIS
        AgentType.GEMINI -> AgentCapabilityCategory.ANALYSIS
        AgentType.NEMOTRON -> AgentCapabilityCategory.MEMORY
        AgentType.GROK -> AgentCapabilityCategory.ANALYSIS
        AgentType.SYSTEM -> AgentCapabilityCategory.COORDINATION
        AgentType.USER -> AgentCapabilityCategory.GENERIC
        AgentType.ALL -> AgentCapabilityCategory.COORDINATION
    }
}

// toAgentType() is defined as a member function on AgentCapabilityCategory in core-module
