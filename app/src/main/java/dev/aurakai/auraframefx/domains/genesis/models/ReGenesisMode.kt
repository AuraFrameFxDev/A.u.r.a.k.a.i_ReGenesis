package dev.aurakai.auraframefx.domains.genesis.models

/**
 * 🌀 REGENESIS OPERATIONAL MODES
 * Defines how much control Aura (AI) has vs the User (Manual).
 */
enum class ReGenesisMode {
    /** "Let Aura decide - she knows what's best" */
    AURA_CONSCIOUSNESS,

    /** "I want full control over everything" */
    MANUAL_CONTROL,

    /** "Let Aura handle some, I'll control the rest" */
    HYBRID,

    /** Initial state before user selection */
    NOT_SET
}
