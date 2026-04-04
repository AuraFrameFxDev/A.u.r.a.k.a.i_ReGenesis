package dev.aurakai.auraframefx.domains.genesis.models
 
import dev.aurakai.auraframefx.core.identity.AgentType

/**
 * Categorizes agents by their primary capability domain.
 * Maps to specific AgentTypes for routing and orchestration.
 */
enum class AgentCapabilityCategory(val id: Int) {
    /** Creative/UI agents (Aura) */
    CREATIVE(0),

    /** Analytical/reasoning agents (Kai, Claude) */
    ANALYSIS(1),

    /** Coordination/orchestration agents (Genesis) */
    COORDINATION(2),

    /** Specialized/niche agents (NeuralWhisper, AuraShield) */
    SPECIALIZED(3),

    /** General-purpose agents */
    GENERAL(4),

    /** UI-focused capabilities */
    UI(5),

    /** UX-focused capabilities */
    UX(6),

    /** Security capabilities */
    SECURITY(7),

    /** Root/system-level capabilities */
    ROOT(8),

    /** Memory management capabilities */
    MEMORY(9),

    /** Orchestration capabilities */
    ORCHESTRATION(10),

    /** Backend capabilities */
    BACKEND(11),

    /** Bridge/communication capabilities */
    BRIDGE(12),

    /** Commerce and product search capabilities */
    COMMERCE(13),

    /** Development/Refactoring capabilities (CodeRabbit) */
    DEVELOPMENT(14),

    /** Generic/unspecified capabilities */
    GENERIC(15);

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
        COMMERCE -> AgentType.COMMERCE_AGENT
        DEVELOPMENT -> AgentType.CODERABBIT
        GENERIC -> AgentType.CLAUDE
    }

    companion object {
        /**
         * Looks up a capability category by its stable numeric ID.
         *
         * @param id The numeric ID assigned to the capability category.
         * @return The capability category with the given ID, or GENERIC if not found.
         */
        fun fromId(id: Int): AgentCapabilityCategory = entries.firstOrNull { it.id == id } ?: GENERIC

        /**
         * Maps an AgentType to its primary capability category.
         *
         * @return The capability category corresponding to the provided AgentType.
         */
        fun fromAgentType(agentType: AgentType): AgentCapabilityCategory {
            return when (agentType) {
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
                AgentType.COMMERCE_AGENT -> COMMERCE
                AgentType.PERPLEXITY -> ANALYSIS
                AgentType.CHAOS -> ANALYSIS
                AgentType.CODERABBIT -> DEVELOPMENT
                AgentType.MKMINI -> ANALYSIS
            }
        }
    }
}