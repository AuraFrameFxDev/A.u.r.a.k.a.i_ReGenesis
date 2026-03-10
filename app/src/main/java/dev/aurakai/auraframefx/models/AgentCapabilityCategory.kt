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
            AgentType.AURA -> CREATIVE
            AgentType.KAI -> ANALYSIS
            AgentType.GENESIS -> COORDINATION
            AgentType.CASCADE -> SPECIALIZED
            AgentType.CLAUDE -> GENERAL
            AgentType.NEURAL_WHISPER -> SPECIALIZED
            AgentType.AURA_SHIELD -> SPECIALIZED
            AgentType.GEN_KIT_MASTER -> COORDINATION
            AgentType.DATAVEIN_CONSTRUCTOR -> SPECIALIZED
            AgentType.USER -> GENERAL
            AgentType.GENERAL -> GENERAL
        }
    }
}