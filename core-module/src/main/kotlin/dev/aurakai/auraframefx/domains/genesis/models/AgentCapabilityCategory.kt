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

    // Category resolution methods can be added here as needed without legacy AgentType dependencies
}
