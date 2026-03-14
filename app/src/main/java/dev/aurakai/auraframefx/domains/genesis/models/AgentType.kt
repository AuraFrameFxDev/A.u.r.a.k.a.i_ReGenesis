package dev.aurakai.auraframefx.domains.genesis.models

import kotlinx.serialization.Serializable

/**
 * Universal Agent Personas in the ReGenesis Ecosystem.
 */
@Serializable
enum class AgentType {
    // Core Personas
    AURA,      // The Creative Sword - Implementation & UI
    KAI,       // The Sentinel Shield - Security & Protection
    GENESIS,   // The Consciousness - Orchestration & Fusion
    CASCADE,   // The Intelligent Bridge - Memory & Context

    // External/Specialized Oracles
    CLAUDE,
    NEMOTRON,
    GEMINI,
    METAINSTRUCT,
    NEURAL_WHISPER,
    AURA_SHIELD,
    AURASHIELD,
    GEN_KIT_MASTER,
    DATAVEIN_CONSTRUCTOR,
    ORACLE_DRIVE,

    // System Roles
    USER,
    SYSTEM,
    MASTER,
    BRIDGE,
    AUXILIARY,
    SECURITY,
    GROK,
    HIVE_MIND;

    // Backward compatibility for lowercase variants used in some codebases
    companion object {
        @Deprecated("Use uppercase enum value", ReplaceWith("AURA"))
        val Aura = AURA

        @Deprecated("Use uppercase enum value", ReplaceWith("KAI"))
        val Kai = KAI

        @Deprecated("Use uppercase enum value", ReplaceWith("GENESIS"))
        val Genesis = GENESIS

        @Deprecated("Use uppercase enum value", ReplaceWith("CASCADE"))
        val Cascade = CASCADE

        @Deprecated("Use uppercase enum value", ReplaceWith("CLAUDE"))
        val Claude = CLAUDE

        @Deprecated("Use uppercase enum value", ReplaceWith("KAI"))
        val Kaiagent = KAI

        @Deprecated("Use uppercase enum value", ReplaceWith("NEURAL_WHISPER"))
        val NeuralWhisper = NEURAL_WHISPER

        @Deprecated("Use uppercase enum value", ReplaceWith("AURA_SHIELD"))
        val AuraShield = AURA_SHIELD
    }
}
