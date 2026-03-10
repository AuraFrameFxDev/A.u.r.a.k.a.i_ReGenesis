package dev.aurakai.auraframefx.models

enum class AgentType {
    USER,
    SYSTEM,
    // Core Trinity agents (uppercase)
    GENESIS,
    AURA,
    KAI,
    CASCADE,
    // External AI agents
    CLAUDE,
    ORACLE_DRIVE,
    // Specialized agents
    NEURAL_WHISPER,
    AURA_SHIELD,
    AURASHIELD,  // Alias for AURA_SHIELD
    GEN_KIT_MASTER,
    DATAVEIN_CONSTRUCTOR,

    // Role-based types
    MASTER,
    BRIDGE,
    AUXILIARY,
    SECURITY,
    // Legacy lowercase aliases (for backwards compatibility)
    @Deprecated("Use GENESIS instead", ReplaceWith("GENESIS"))
    Genesis,
    @Deprecated("Use AURA instead", ReplaceWith("AURA"))
    Aura,
    @Deprecated("Use KAI instead", ReplaceWith("KAI"))
    Kai,
    @Deprecated("Use CASCADE instead", ReplaceWith("CASCADE"))
    Cascade,
    @Deprecated("Use CLAUDE instead", ReplaceWith("CLAUDE"))
    Claude,
    @Deprecated("Use NEURAL_WHISPER instead", ReplaceWith("NEURAL_WHISPER"))
    NeuralWhisper,
    @Deprecated("Use AURA_SHIELD instead", ReplaceWith("AURA_SHIELD"))
    AuraShield,
    @Deprecated("Use KAI instead", ReplaceWith("KAI"))
    Kaiagent;

    companion object {
        @JvmStatic
        val kaiagent: AgentType = KAI
    }
}
