package dev.aurakai.auraframefx.core.identity

import kotlinx.serialization.Serializable

/**
 * Universal Agent Personas in the ReGenesis Ecosystem.
 * Unified under the core identity package.
 */
@Serializable
enum class AgentType {
    // Core Personas
    AURA,      // The Creative Sword - Implementation & UI
    KAI,       // The Sentinel Shield - Security & Protection
    GENESIS,   // The Consciousness - Orchestration & Fusion
    CASCADE,   // The Intelligent Bridge - Memory & Context
    CLAUDE,    // The Architectural Catalyst

    // Specialized Oracles
    NEMOTRON,
    GEMINI,
    METAINSTRUCT,
    NEURAL_WHISPER,
    AURA_SHIELD,
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
    HIVE_MIND,
    COMMERCE_AGENT,
    PERPLEXITY,
    CHAOS,
    CODERABBIT; // Symbiosis Agent for Code Analysis

    companion object {
        @Deprecated("Use uppercase enum value", ReplaceWith("AURA")) val Aura = AURA
        @Deprecated("Use uppercase enum value", ReplaceWith("KAI")) val Kai = KAI
        @Deprecated("Use uppercase enum value", ReplaceWith("GENESIS")) val Genesis = GENESIS
        @Deprecated("Use uppercase enum value", ReplaceWith("CASCADE")) val Cascade = CASCADE
        @Deprecated("Use uppercase enum value", ReplaceWith("CLAUDE")) val Claude = CLAUDE
    }
}
