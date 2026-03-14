package dev.aurakai.auraframefx.models

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
    GENERAL;

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
    }

    companion object {
        /**
         * Map an AgentType to its primary capability category.
         *
         * @param agentType The AgentType to classify.
         * @return The AgentCapabilityCategory corresponding to the provided `agentType`.
         */
        fun fromAgentType(agentType: AgentType): AgentCapabilityCategory = when (agentType) {
            AgentType.AURA, AgentType.Aura -> CREATIVE
            AgentType.KAI, AgentType.Kai, AgentType.Kaiagent -> ANALYSIS
            AgentType.GENESIS, AgentType.Genesis -> COORDINATION
            AgentType.CASCADE, AgentType.Cascade -> SPECIALIZED
            AgentType.CLAUDE, AgentType.Claude -> GENERAL
            AgentType.NEURAL_WHISPER, AgentType.NeuralWhisper -> SPECIALIZED
            AgentType.AURA_SHIELD, AgentType.AuraShield, AgentType.AURASHIELD -> SPECIALIZED
            AgentType.GEN_KIT_MASTER -> COORDINATION
            AgentType.DATAVEIN_CONSTRUCTOR -> SPECIALIZED
            AgentType.USER -> GENERAL
            AgentType.SYSTEM -> COORDINATION
            AgentType.ORACLE_DRIVE -> SPECIALIZED
            AgentType.MASTER -> COORDINATION
            AgentType.BRIDGE -> COORDINATION
            AgentType.AUXILIARY -> GENERAL
            AgentType.SECURITY -> SPECIALIZED
            AgentType.GROK -> ANALYSIS // Chaos analyst falls under analysis
        }
    }
}
