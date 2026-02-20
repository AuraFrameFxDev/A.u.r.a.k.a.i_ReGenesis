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
        AgentType.METAINSTRUCT -> AgentCapabilityCategory.ORCHESTRATION
        AgentType.NEURAL_WHISPER -> AgentCapabilityCategory.SPECIALIZED
        AgentType.AURA_SHIELD -> AgentCapabilityCategory.SECURITY
        AgentType.AURASHIELD -> AgentCapabilityCategory.SECURITY
        AgentType.GEN_KIT_MASTER -> AgentCapabilityCategory.COORDINATION
        AgentType.DATAVEIN_CONSTRUCTOR -> AgentCapabilityCategory.BACKEND
        AgentType.ORACLE_DRIVE -> AgentCapabilityCategory.BACKEND
        AgentType.MASTER -> AgentCapabilityCategory.COORDINATION
        AgentType.BRIDGE -> AgentCapabilityCategory.BRIDGE
        AgentType.AUXILIARY -> AgentCapabilityCategory.GENERAL
        AgentType.SECURITY -> AgentCapabilityCategory.SECURITY
        AgentType.HIVE_MIND -> AgentCapabilityCategory.COORDINATION
        AgentType.SYSTEM -> AgentCapabilityCategory.COORDINATION
        AgentType.USER -> AgentCapabilityCategory.GENERIC
    }
}

// toAgentType() is defined as a member function on AgentCapabilityCategory in core-module
