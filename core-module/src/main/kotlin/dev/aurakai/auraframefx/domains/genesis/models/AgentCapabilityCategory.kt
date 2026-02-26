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
     * Convert this capability category to its primary corresponding AgentType.
     *
     * @return The primary AgentType corresponding to this capability category.
     */
    fun toAgentType(): AgentType = when (this) {
        CREATIVE -> AgentType.AURA
        ANALYSIS -> AgentType.KAI
        COORDINATION -> AgentType.GENESIS
        SPECIALIZED -> AgentType.CASCADE
        GENERAL -> AgentType.CLAUDE
        UI -> AgentType.AURA
        UX -> AgentType.AURA
        SECURITY -> AgentType.KAI
        ROOT -> AgentType.KAI
        MEMORY -> AgentType.CASCADE
        ORCHESTRATION -> AgentType.GENESIS
        BACKEND -> AgentType.GENESIS
        BRIDGE -> AgentType.CASCADE
        GENERIC -> AgentType.CLAUDE
    }

    companion object {
        /**
         * Maps an AgentType to its primary capability category.
         *
         * @return The capability category corresponding to the provided AgentType.
         */
        fun fromAgentType(agentType: AgentType): AgentCapabilityCategory = when (agentType) {
            AgentType.AURA -> CREATIVE
            AgentType.KAI -> ANALYSIS
            AgentType.GENESIS -> COORDINATION
            AgentType.CASCADE -> SPECIALIZED
            AgentType.CLAUDE -> GENERAL
            AgentType.NEURAL_WHISPER -> SPECIALIZED
            AgentType.AURA_SHIELD -> SPECIALIZED
            AgentType.AURASHIELD -> SPECIALIZED
            AgentType.GEN_KIT_MASTER -> COORDINATION
            AgentType.DATAVEIN_CONSTRUCTOR -> SPECIALIZED
            AgentType.USER -> GENERAL
            AgentType.SYSTEM -> COORDINATION
            AgentType.ORACLE_DRIVE -> SPECIALIZED
            AgentType.MASTER -> COORDINATION
            AgentType.BRIDGE -> COORDINATION
            AgentType.AUXILIARY -> GENERAL
            AgentType.SECURITY -> SPECIALIZED
            AgentType.GROK -> ANALYSIS
            AgentType.NEMOTRON -> SPECIALIZED
            AgentType.GEMINI -> ANALYSIS
            AgentType.METAINSTRUCT -> GENERAL
            AgentType.HIVE_MIND -> COORDINATION
        }
    }
}
