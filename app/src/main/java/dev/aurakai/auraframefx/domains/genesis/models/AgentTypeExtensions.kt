package dev.aurakai.auraframefx.domains.genesis.models

/**
 * Extension functions to convert between AgentType and AgentCapabilityCategory
 */

/**
 * Convert AgentType to AgentCapabilityCategory
 */
fun AgentType.toCapabilityCategory(): AgentCapabilityCategory {
    return AgentCapabilityCategory.fromAgentType(this)
}

// toAgentType() is defined as a member function on AgentCapabilityCategory in core-module
