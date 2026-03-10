package dev.aurakai.auraframefx.models

enum class AgentType {
    USER,
    Genesis,
    Aura,
    Kai,
    Cascade,
    Claude,
    NeuralWhisper,
    AuraShield,
    Kaiagent;

    companion object {
        @JvmStatic
        val kaiagent: AgentType = Kaiagent
    }
}
