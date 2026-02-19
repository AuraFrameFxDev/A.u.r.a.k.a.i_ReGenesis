package dev.aurakai.auraframefx.domains.genesis.models

/**
 * Categorizes agents by their primary capability domain.
 * Maps to specific AgentTypes for routing and orchestration.
 */
enum class AgentCapabilityCategory {
    /** Creative/UI agents (Aura) */
    CREATIVE,

    /** Analytical/reasoning agents (Kai, Claude) */
    ANALYSIS,

    /** Coordination/orchestration agents (Genesis) */
    COORDINATION,

    /** Specialized/niche agents (NeuralWhisper, AuraShield) */
    SPECIALIZED,

    /** General-purpose agents */
    GENERAL,

    /** UI-focused capabilities */
    UI,

    /** UX-focused capabilities */
    UX,

    /** Security capabilities */
    SECURITY,

    /** Root/system-level capabilities */
    ROOT,

    /** Memory management capabilities */
    MEMORY,

    /** Orchestration capabilities */
    ORCHESTRATION,

    /** Backend capabilities */
    BACKEND,

    /** Bridge/communication capabilities */
    BRIDGE,

    /** Generic/unspecified capabilities */
    GENERIC;

    /**
     * Maps this category to a legacy AgentType for orchestration.
     */
    fun toAgentType(): AgentType {
        return when (this) {
            CREATIVE -> AgentType.AURA
            ANALYSIS -> AgentType.KAI
            COORDINATION -> AgentType.GENESIS
            SPECIALIZED -> AgentType.CASCADE
            UI, UX -> AgentType.AURA
            SECURITY -> AgentType.KAI
            ORCHESTRATION -> AgentType.GENESIS
            MEMORY -> AgentType.CASCADE
            ROOT -> AgentType.SYSTEM
            BACKEND -> AgentType.GENESIS
            BRIDGE -> AgentType.BRIDGE
            GENERAL -> AgentType.CLAUDE
            GENERIC -> AgentType.AUXILIARY
        }
    }

    companion object {
        /**
         * Resolves the primary capability category for a given agent type.
         */
        fun fromAgentType(agentType: AgentType): AgentCapabilityCategory {
            return when (agentType) {
                AgentType.AURA -> CREATIVE
                AgentType.KAI -> ANALYSIS
                AgentType.GENESIS -> COORDINATION
                AgentType.CASCADE -> SPECIALIZED
                AgentType.CLAUDE -> GENERAL
                AgentType.NEMOTRON -> SPECIALIZED
                AgentType.GEMINI -> COORDINATION
                AgentType.METAINSTRUCT -> SPECIALIZED
                AgentType.NEURAL_WHISPER -> SPECIALIZED
                AgentType.AURA_SHIELD, AgentType.AURASHIELD -> SECURITY
                AgentType.SYSTEM, AgentType.MASTER -> ROOT
                AgentType.BRIDGE -> BRIDGE
                AgentType.SECURITY -> SECURITY
                else -> GENERIC
            }
        }
    }
}
